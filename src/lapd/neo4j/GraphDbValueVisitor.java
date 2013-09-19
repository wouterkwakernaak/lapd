package lapd.neo4j;
import java.util.Iterator;
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
import org.eclipse.imp.pdb.facts.type.Type;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

public class GraphDbValueVisitor implements org.eclipse.imp.pdb.facts.visitors.IValueVisitor<Node, GraphDbMappingException> {

	private GraphDatabaseService graphDb;
	
	public GraphDbValueVisitor(GraphDatabaseService graphDb) {
		this.graphDb = graphDb;	
	}
	
	@Override
	public Node visitString(IString stringValue) throws GraphDbMappingException {
		Node node = graphDb.createNode();
		String propertyName = "str";
		node.setProperty(propertyName, stringValue.getValue());
		return node;
	}
	
	@Override
	public Node visitInteger(IInteger integerValue) throws GraphDbMappingException {
		return createPrimitiveNode(integerValue, "int");
	}

	@Override
	public Node visitReal(IReal realValue) throws GraphDbMappingException {
		return createPrimitiveNode(realValue, "real");
	}	
	
	@Override
	public Node visitBoolean(IBool booleanValue) throws GraphDbMappingException {
		String boolPropertyName = "bool";
		Node node = graphDb.createNode();
		node.setProperty(boolPropertyName, booleanValue.getValue());
		return node;
	}

	@Override
	public Node visitRational(IRational rationalValue) throws GraphDbMappingException {
		String numeratorPropertyName = "numerator";
		String denominatorPropertyName = "denominator";
		Node node = graphDb.createNode();
		node.setProperty(numeratorPropertyName, rationalValue.numerator().toString());
		node.setProperty(denominatorPropertyName, rationalValue.denominator().toString());
		return node;
	}
	
	@Override
	public Node visitSourceLocation(ISourceLocation sourceLocationValue) throws GraphDbMappingException {
		return createPrimitiveNode(sourceLocationValue, "loc");
	}
	
	@Override
	public Node visitDateTime(IDateTime dateTimeValue) throws GraphDbMappingException {
		String dateTimePropertyName = "datetime";
		Node node = graphDb.createNode();		
		node.setProperty(dateTimePropertyName, dateTimeValue.getInstant());
		return node;
	}

	@Override
	public Node visitList(IList listValue) throws GraphDbMappingException {
		Node firstElementNode = createIterableNodeCollection(listValue.iterator(), RelTypes.NEXT_LIST_ELEMENT);		
		return firstElementNode;
	}	
	
	@Override
	public Node visitListRelation(IList listValue) throws GraphDbMappingException {		
		Node firstElementNode = createIterableNodeCollection(listValue.iterator(), RelTypes.NEXT_LIST_RELATION_ELEMENT);		
		return firstElementNode;
	}
	
	@Override
	public Node visitSet(ISet setValue) throws GraphDbMappingException {	
		Node firstElementNode = createIterableNodeCollection(setValue.iterator(), RelTypes.NEXT_SET_ELEMENT);		
		return firstElementNode;
	}	

	@Override
	public Node visitRelation(ISet setValue) throws GraphDbMappingException {		
		Node firstElementNode = createIterableNodeCollection(setValue.iterator(), RelTypes.NEXT_RELATION_ELEMENT);		
		return firstElementNode;
	}
	
	@Override
	public Node visitNode(INode nodeValue) throws GraphDbMappingException {
		return createAnnotatableNode(nodeValue, "nodeName", RelTypes.CHILD_NODE, RelTypes.ANNOTATION_NODE);
	}	

	@Override
	public Node visitConstructor(IConstructor constructorValue) throws GraphDbMappingException {
		return createAnnotatableNode(constructorValue, "constructorName", 
				RelTypes.CHILD_CONSTRUCTOR, RelTypes.ANNOTATION_CONSTRUCTOR);
	}

	@Override
	public Node visitTuple(ITuple tupleValue) throws GraphDbMappingException {
		Iterator<IValue> iterator = tupleValue.iterator();
		Type type = tupleValue.getType();
		boolean hasLabels = type.hasFieldNames();
		Node firstElementNode = null;
		Node previousElementNode = null;
		int count = 0;
		if (iterator.hasNext()) {			
			IValue elementValue = iterator.next();
			firstElementNode = elementValue.accept(this);
			if (hasLabels) {
				firstElementNode.setProperty("label", type.getFieldName(count));
				count++;
			}
			previousElementNode = firstElementNode;
			while (iterator.hasNext()) {
				IValue currentElementValue = iterator.next();
				Node currentElementNode = currentElementValue.accept(this);
				if (hasLabels)
					currentElementNode.setProperty("label", type.getFieldName(count));
				previousElementNode.createRelationshipTo(currentElementNode, RelTypes.NEXT_TUPLE_ELEMENT);
				previousElementNode = currentElementNode;
				count++;
			}
		}
		else	// empty tuple
			firstElementNode = graphDb.createNode();
		return firstElementNode;
	}		

	@Override
	public Node visitMap(IMap mapValue) throws GraphDbMappingException {
		Iterator<Entry<IValue, IValue>> iterator = mapValue.entryIterator();		
		Node firstElementNode = null;
		Node previousElementNode = null;
		if (iterator.hasNext()) {
			Entry<IValue, IValue> entry = iterator.next();			
			firstElementNode = previousElementNode = entry.getKey().accept(this);
			addValueToMap(firstElementNode, entry.getValue());
			while (iterator.hasNext()) {
				Entry<IValue, IValue> currentEntry = iterator.next();
				Node currentElementNode = currentEntry.getKey().accept(this);
				addValueToMap(currentElementNode, entry.getValue());
				previousElementNode.createRelationshipTo(currentElementNode, RelTypes.NEXT_MAP_ELEMENT);
				previousElementNode = currentElementNode;
			}
		}		
		else	// empty map
			firstElementNode = graphDb.createNode();		
		return firstElementNode;
	}
	
	@Override
	public Node visitExternal(IExternalValue externalValue)	throws GraphDbMappingException {
		throw new GraphDbMappingException("External values not supported.");
	}	

	private void addValueToMap(Node keyNode, IValue value) throws GraphDbMappingException {
		Node valueNode = value.accept(this);
		keyNode.createRelationshipTo(valueNode, RelTypes.IS_MAP_VALUE);		
	}	
	
	private Node createPrimitiveNode(IValue value, String propertyName) {
		Node node = graphDb.createNode();
		node.setProperty(propertyName, value.toString());
		return node;
	}
	
	private Node createIterableNodeCollection(Iterator<IValue> iterator, RelTypes relType) throws GraphDbMappingException {
		Node firstElementNode = null;
		Node previousElementNode = null;
		if (iterator.hasNext()) {
			IValue elementValue = iterator.next();
			firstElementNode = previousElementNode = elementValue.accept(this);
			while (iterator.hasNext()) {
				IValue currentElementValue = iterator.next();
				Node currentElementNode = currentElementValue.accept(this);
				previousElementNode.createRelationshipTo(currentElementNode, relType);
				previousElementNode = currentElementNode;
			}
		}		
		else	// empty node
			firstElementNode = graphDb.createNode();
		return firstElementNode;
	}
	
	private Node createAnnotatableNode(INode nodeValue, String propertyName, 
			RelTypes childRelation, RelTypes annotationRelation) throws GraphDbMappingException {
		Node node = graphDb.createNode();
		node.setProperty(propertyName, nodeValue.getName());
		for (IValue child : nodeValue.getChildren() ) {
			Node childNode = child.accept(this);
			node.createRelationshipTo(childNode, childRelation);
		}
		for (Entry<String, IValue> annotation : nodeValue.asAnnotatable().getAnnotations().entrySet()) {
			Node annotationNode = annotation.getValue().accept(this);
			annotationNode.setProperty("annotationName", annotation.getKey());
			node.createRelationshipTo(annotationNode, annotationRelation);
		}
		return node;
	}

}
