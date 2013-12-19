package lapd.databases.neo4j;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.imp.pdb.facts.IBool;
import org.eclipse.imp.pdb.facts.IConstructor;
import org.eclipse.imp.pdb.facts.IDateTime;
import org.eclipse.imp.pdb.facts.IExternalValue;
import org.eclipse.imp.pdb.facts.IInteger;
import org.eclipse.imp.pdb.facts.IList;
import org.eclipse.imp.pdb.facts.IMap;
import org.eclipse.imp.pdb.facts.INode;
import org.eclipse.imp.pdb.facts.IRational;
import org.eclipse.imp.pdb.facts.IReal;
import org.eclipse.imp.pdb.facts.ISet;
import org.eclipse.imp.pdb.facts.ISourceLocation;
import org.eclipse.imp.pdb.facts.IString;
import org.eclipse.imp.pdb.facts.ITuple;
import org.eclipse.imp.pdb.facts.IValue;
import org.eclipse.imp.pdb.facts.visitors.IValueVisitor;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.Index;

public class GraphDbValueInsertionVisitor implements IValueVisitor<Node, GraphDbMappingException> {

	private GraphDatabaseService graphDb;
	private final Index<Node> nodeIndex;
	
	public GraphDbValueInsertionVisitor(GraphDatabaseService graphDb, Index<Node> nodeIndex) {
		this.graphDb = graphDb;
		this.nodeIndex = nodeIndex;
	}
	
	@Override
	public Node visitString(IString stringValue) throws GraphDbMappingException {
		Node node = graphDb.createNode();
		node.setProperty(PropertyNames.TYPE, TypeNames.STRING);
		node.setProperty(PropertyNames.STRING, stringValue.getValue());
		nodeIndex.add(node, PropertyNames.STRING, stringValue.getValue());
		return node;
	}
	
	@Override
	public Node visitInteger(IInteger integerValue) throws GraphDbMappingException {
		return createPrimitiveStringNode(integerValue, PropertyNames.INTEGER, TypeNames.INTEGER);
	}

	@Override
	public Node visitReal(IReal realValue) throws GraphDbMappingException {
		return createPrimitiveStringNode(realValue, PropertyNames.REAL, TypeNames.REAL);
	}	
	
	@Override
	public Node visitBoolean(IBool booleanValue) throws GraphDbMappingException {
		Node node = graphDb.createNode();
		node.setProperty(PropertyNames.TYPE, TypeNames.BOOLEAN);
		node.setProperty(PropertyNames.BOOLEAN, booleanValue.getValue());
		return node;
	}

	@Override
	public Node visitRational(IRational rationalValue) throws GraphDbMappingException {
		Node node = graphDb.createNode();
		node.setProperty(PropertyNames.TYPE, TypeNames.RATIONAL);
		node.setProperty(PropertyNames.NUMERATOR, rationalValue.numerator().toString());
		node.setProperty(PropertyNames.DENOMINATOR, rationalValue.denominator().toString());
		return node;
	}
	
	@Override
	public Node visitSourceLocation(ISourceLocation sourceLocationValue) throws GraphDbMappingException {
		return createPrimitiveStringNode(sourceLocationValue, PropertyNames.SOURCE_LOCATION, TypeNames.SOURCE_LOCATION);
	}
	
	@Override
	public Node visitDateTime(IDateTime dateTimeValue) throws GraphDbMappingException {
		Node node = graphDb.createNode();
		node.setProperty(PropertyNames.TYPE, TypeNames.DATE_TIME);
		node.setProperty(PropertyNames.DATE_TIME, dateTimeValue.getInstant());
		return node;
	}

	@Override
	public Node visitList(IList listValue) throws GraphDbMappingException {
		return insertList(listValue);
	}	
	
	@Override
	public Node visitListRelation(IList listValue) throws GraphDbMappingException {		
		return insertList(listValue);
	}
	
	@Override
	public Node visitSet(ISet setValue) throws GraphDbMappingException {	
		return insertSet(setValue);
	}	

	@Override
	public Node visitRelation(ISet setValue) throws GraphDbMappingException {		
		if (setValue.getElementType().getArity() != 2)
			return insertSet(setValue);
		Node referenceNode = graphDb.createNode();		
		referenceNode.setProperty(PropertyNames.TYPE, TypeNames.BINARY_RELATION);
		Map<IValue, Node> currentNodes = new HashMap<IValue, Node>();
		for (IValue tuple : setValue) {
			IValue firstElement = ((ITuple)tuple).get(0);
			IValue secondElement = ((ITuple)tuple).get(1);			
			Node firstElementNode = null;
			if (!currentNodes.containsKey(firstElement)) {				
				firstElementNode = firstElement.accept(this);
				currentNodes.put(firstElement, firstElementNode);
				referenceNode.createRelationshipTo(firstElementNode, RelTypes.PART);
			}
			else
				firstElementNode = currentNodes.get(firstElement);
			if (!currentNodes.containsKey(secondElement)) {				
				Node secondElementNode = secondElement.accept(this);
				currentNodes.put(secondElement, secondElementNode);
				firstElementNode.createRelationshipTo(secondElementNode, RelTypes.TO);
			}
			else {
				Node secondElementNode = currentNodes.get(secondElement);
				firstElementNode.createRelationshipTo(secondElementNode, RelTypes.TO);
			}
		}
		return referenceNode;
	}
	
	@Override
	public Node visitNode(INode nodeValue) throws GraphDbMappingException {
		return createAnnotatableNode(nodeValue, TypeNames.NODE);
	}	

	@Override
	public Node visitConstructor(IConstructor constructorValue) throws GraphDbMappingException {
		Node node = createAnnotatableNode(constructorValue, TypeNames.CONSTRUCTOR);
		node.setProperty(PropertyNames.ADT, constructorValue.getType().getAbstractDataType().getName());
		return node;
	}

	@Override
	public Node visitTuple(ITuple tupleValue) throws GraphDbMappingException {
		Node firstElementNode = createLinkedNodeCollection(tupleValue.iterator());	
		firstElementNode.setProperty(PropertyNames.TYPE, TypeNames.TUPLE);
		return firstElementNode;
	}		

	@Override
	public Node visitMap(IMap mapValue) throws GraphDbMappingException {
		Iterator<Entry<IValue, IValue>> iterator = mapValue.entryIterator();	
		Node referenceNode = graphDb.createNode();
		referenceNode.setProperty(PropertyNames.TYPE, TypeNames.MAP);
		Node previousElementNode = null;
		if (iterator.hasNext()) {			
			Entry<IValue, IValue> entry = iterator.next();			
			previousElementNode = entry.getKey().accept(this);
			referenceNode.createRelationshipTo(previousElementNode, RelTypes.HEAD);
			addValueToMap(previousElementNode, entry.getValue());
			while (iterator.hasNext()) {
				entry = iterator.next();
				Node currentElementNode = entry.getKey().accept(this);
				addValueToMap(currentElementNode, entry.getValue());
				previousElementNode.createRelationshipTo(currentElementNode, RelTypes.TO);
				previousElementNode = currentElementNode;
			}
		}	
		return referenceNode;
	}
	
	@Override
	public Node visitExternal(IExternalValue externalValue)	throws GraphDbMappingException {
		throw new GraphDbMappingException("External values not supported.");
	}
	
	private Node insertList(IList listValue) throws GraphDbMappingException {
		Node firstElementNode = createLinkedNodeCollection(listValue.iterator());
		firstElementNode.setProperty(PropertyNames.TYPE, TypeNames.LIST);
		return firstElementNode;
	}
	
	private Node insertSet(ISet setValue) throws GraphDbMappingException {
		Node referenceNode = graphDb.createNode();
		referenceNode.setProperty(PropertyNames.TYPE, TypeNames.SET);
		for (IValue element : setValue) {
			Node elementNode = element.accept(this);
			referenceNode.createRelationshipTo(elementNode, RelTypes.TO);
		}		
		return referenceNode;
	}

	private void addValueToMap(Node keyNode, IValue value) throws GraphDbMappingException {
		Node valueNode = value.accept(this);
		keyNode.createRelationshipTo(valueNode, RelTypes.VALUE);		
	}	
	
	// stores values such as integers and reals as strings in order to allow larger then 64 bit integer storage
	private Node createPrimitiveStringNode(IValue value, String propertyName, String typeName) {
		Node node = graphDb.createNode();
		node.setProperty(PropertyNames.TYPE, typeName);
		node.setProperty(propertyName, value.toString());
		return node;
	}
	
	private Node createLinkedNodeCollection(Iterator<IValue> iterator) throws GraphDbMappingException {
		Node referenceNode = graphDb.createNode();
		Node previousElementNode = null;
		if (iterator.hasNext()) {
			IValue elementValue = iterator.next();
			previousElementNode = elementValue.accept(this);
			referenceNode.createRelationshipTo(previousElementNode, RelTypes.HEAD);
			while (iterator.hasNext()) {
				IValue currentElementValue = iterator.next();
				Node currentElementNode = currentElementValue.accept(this);
				previousElementNode.createRelationshipTo(currentElementNode, RelTypes.TO);
				previousElementNode = currentElementNode;
			}
		}
		return referenceNode;
	}
	
	private Node createAnnotatableNode(INode nodeValue, String typeName) 
			throws GraphDbMappingException {
		Node node = createLinkedNodeCollection(nodeValue.getChildren().iterator());
		node.setProperty(PropertyNames.NODE, nodeValue.getName());
		nodeIndex.add(node, PropertyNames.NODE, nodeValue.getName());
		for (Entry<String, IValue> annotation : nodeValue.asAnnotatable().getAnnotations().entrySet()) {
			Node annotationNode = annotation.getValue().accept(this);
			annotationNode.setProperty(PropertyNames.ANNOTATION, annotation.getKey());
			node.createRelationshipTo(annotationNode, RelTypes.ANNO);
		}
		node.setProperty(PropertyNames.TYPE, typeName);
		return node;
	}

}
