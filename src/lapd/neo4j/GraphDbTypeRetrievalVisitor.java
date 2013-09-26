package lapd.neo4j;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.imp.pdb.facts.IMapWriter;
import org.eclipse.imp.pdb.facts.IValue;
import org.eclipse.imp.pdb.facts.IValueFactory;
import org.eclipse.imp.pdb.facts.io.IValueTextReader;
import org.eclipse.imp.pdb.facts.io.StandardTextReader;
import org.eclipse.imp.pdb.facts.type.ITypeVisitor;
import org.eclipse.imp.pdb.facts.type.Type;
import org.eclipse.imp.pdb.facts.type.TypeFactory;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

public class GraphDbTypeRetrievalVisitor implements ITypeVisitor<IValue, GraphDbMappingException>{
	
	private final Node node;
	private final IValueFactory valueFactory;
	
	public GraphDbTypeRetrievalVisitor(Node node, IValueFactory valueFactory) {
		this.node = node;
		this.valueFactory = valueFactory;
	}
	
	@Override
	public IValue visitString(Type type) throws GraphDbMappingException {
		return valueFactory.string(node.getProperty(PropertyNames.STRING).toString());
	}
	
	@Override
	public IValue visitInteger(Type type) throws GraphDbMappingException {
		return valueFactory.integer(node.getProperty(PropertyNames.INTEGER).toString());
	}

	@Override
	public IValue visitReal(Type type) throws GraphDbMappingException {
		return valueFactory.real(node.getProperty(PropertyNames.REAL).toString());
	}

	@Override
	public IValue visitBool(Type type) throws GraphDbMappingException {
		return valueFactory.bool((Boolean)node.getProperty(PropertyNames.BOOLEAN));
	}

	@Override
	public IValue visitRational(Type type) throws GraphDbMappingException {
		String numerator = node.getProperty(PropertyNames.NUMERATOR).toString();
		String denominator = node.getProperty(PropertyNames.DENOMINATOR).toString();
		return valueFactory.rational(numerator + "r" + denominator);
	}
	
	@Override
	public IValue visitSourceLocation(Type type) throws GraphDbMappingException {
		String locString = node.getProperty(PropertyNames.SOURCE_LOCATION).toString();
		IValueTextReader reader = new StandardTextReader();
		try {
			return reader.read(valueFactory, TypeFactory.getInstance().sourceLocationType(), 
					new StringReader(locString));
		} catch (Exception e) {
			throw new GraphDbMappingException(e.getMessage());
		}
	}
	
	@Override
	public IValue visitDateTime(Type type) throws GraphDbMappingException {
		return valueFactory.datetime((Long)node.getProperty(PropertyNames.DATE_TIME));
	}

	@Override
	public IValue visitList(Type type) throws GraphDbMappingException {
		if (!node.hasRelationship(RelTypes.LIST_HEAD))
			return valueFactory.list(TypeFactory.getInstance().voidType());
		List<IValue> valueList = new ArrayList<IValue>();
		Node currentNode = node.getSingleRelationship(RelTypes.LIST_HEAD, Direction.OUTGOING).getEndNode();
		valueList.add(TypeDeducer.getType(currentNode).accept(new GraphDbTypeRetrievalVisitor(currentNode, valueFactory)));
		while (currentNode.hasRelationship(Direction.OUTGOING, RelTypes.NEXT_LIST_ELEMENT)) {
			currentNode = currentNode.getSingleRelationship(RelTypes.NEXT_LIST_ELEMENT, Direction.OUTGOING).getEndNode();
			valueList.add(TypeDeducer.getType(currentNode).accept(new GraphDbTypeRetrievalVisitor(currentNode, valueFactory)));
		}
		return valueFactory.list(valueList.toArray(new IValue[valueList.size()]));
	}
	
	@Override
	public IValue visitSet(Type type) throws GraphDbMappingException {
		if (!node.hasRelationship(RelTypes.SET_HEAD))
			return valueFactory.set(TypeFactory.getInstance().voidType());
		List<IValue> valueList = new ArrayList<IValue>();
		Node currentNode = node.getSingleRelationship(RelTypes.SET_HEAD, Direction.OUTGOING).getEndNode();
		valueList.add(TypeDeducer.getType(currentNode).accept(new GraphDbTypeRetrievalVisitor(currentNode, valueFactory)));
		while (currentNode.hasRelationship(Direction.OUTGOING, RelTypes.NEXT_SET_ELEMENT)) {
			currentNode = currentNode.getSingleRelationship(RelTypes.NEXT_SET_ELEMENT, Direction.OUTGOING).getEndNode();
			valueList.add(TypeDeducer.getType(currentNode).accept(new GraphDbTypeRetrievalVisitor(currentNode, valueFactory)));
		}
		return valueFactory.set(valueList.toArray(new IValue[valueList.size()]));
	}
	
	@Override
	public IValue visitNode(Type type) throws GraphDbMappingException {
		String nodeName = node.getProperty(PropertyNames.NODE).toString();
		if (!node.hasRelationship(Direction.OUTGOING, RelTypes.NODE_HEAD)) 
			return valueFactory.node(nodeName);		
		List<IValue> valueList = new ArrayList<IValue>();
		Node currentNode = node.getSingleRelationship(RelTypes.NODE_HEAD, Direction.OUTGOING).getEndNode();
		valueList.add(TypeDeducer.getType(currentNode).accept(new GraphDbTypeRetrievalVisitor(currentNode, valueFactory)));
		while (currentNode.hasRelationship(Direction.OUTGOING, RelTypes.NEXT_CHILD_NODE)) {
			currentNode = currentNode.getSingleRelationship(RelTypes.NEXT_CHILD_NODE, Direction.OUTGOING).getEndNode();
			valueList.add(TypeDeducer.getType(currentNode).accept(new GraphDbTypeRetrievalVisitor(currentNode, valueFactory)));
		}
		if (!node.hasRelationship(Direction.OUTGOING, RelTypes.ANNOTATION_NODE)) 
			return valueFactory.node(nodeName, valueList.toArray(new IValue[valueList.size()]));
		Map<String, IValue> annotations = new HashMap<String, IValue>();
		for (Relationship rel : node.getRelationships(Direction.OUTGOING, RelTypes.ANNOTATION_NODE)) {
			Node annotationNode = rel.getEndNode();
			String annotationName = annotationNode.getProperty(PropertyNames.ANNOTATION).toString();
			IValue annotationValue = TypeDeducer.getType(annotationNode).accept(new GraphDbTypeRetrievalVisitor(annotationNode, valueFactory));
			annotations.put(annotationName, annotationValue);
		}
		return valueFactory.node(nodeName, annotations, valueList.toArray(new IValue[valueList.size()]));
	}
	
	@Override
	public IValue visitConstructor(Type type) throws GraphDbMappingException {		
		if (!node.hasRelationship(Direction.OUTGOING, RelTypes.CONSTRUCTOR_HEAD)) 
			return valueFactory.constructor(type);		
		List<IValue> valueList = new ArrayList<IValue>();
		Node currentNode = node.getSingleRelationship(RelTypes.CONSTRUCTOR_HEAD, Direction.OUTGOING).getEndNode();
		valueList.add(TypeDeducer.getType(currentNode).accept(new GraphDbTypeRetrievalVisitor(currentNode, valueFactory)));
		while (currentNode.hasRelationship(Direction.OUTGOING, RelTypes.NEXT_CHILD_CONSTRUCTOR)) {
			currentNode = currentNode.getSingleRelationship(RelTypes.NEXT_CHILD_CONSTRUCTOR, Direction.OUTGOING).getEndNode();
			valueList.add(TypeDeducer.getType(currentNode).accept(new GraphDbTypeRetrievalVisitor(currentNode, valueFactory)));
		}
		if (!node.hasRelationship(Direction.OUTGOING, RelTypes.ANNOTATION_CONSTRUCTOR))
			return valueFactory.constructor(type, valueList.toArray(new IValue[valueList.size()]));
		Map<String, IValue> annotations = new HashMap<String, IValue>();
		for (Relationship rel : node.getRelationships(Direction.OUTGOING, RelTypes.ANNOTATION_CONSTRUCTOR)) {
			Node annotationNode = rel.getEndNode();
			String annotationName = annotationNode.getProperty(PropertyNames.ANNOTATION).toString();
			IValue annotationValue = TypeDeducer.getType(annotationNode).accept(new GraphDbTypeRetrievalVisitor(annotationNode, valueFactory));
			annotations.put(annotationName, annotationValue);
		}
		return valueFactory.constructor(type, annotations, valueList.toArray(new IValue[valueList.size()]));
	}
	
	@Override
	public IValue visitTuple(Type type) throws GraphDbMappingException {
		if (!node.hasRelationship(RelTypes.TUPLE_HEAD))
			return valueFactory.tuple();
		List<IValue> valueList = new ArrayList<IValue>();
		Node currentNode = node.getSingleRelationship(RelTypes.TUPLE_HEAD, Direction.OUTGOING).getEndNode();
		valueList.add(TypeDeducer.getType(currentNode).accept(new GraphDbTypeRetrievalVisitor(currentNode, valueFactory)));
		while (currentNode.hasRelationship(Direction.OUTGOING, RelTypes.NEXT_TUPLE_ELEMENT)) {
			currentNode = currentNode.getSingleRelationship(RelTypes.NEXT_TUPLE_ELEMENT, Direction.OUTGOING).getEndNode();
			valueList.add(TypeDeducer.getType(currentNode).accept(new GraphDbTypeRetrievalVisitor(currentNode, valueFactory)));
		}
		return valueFactory.tuple(valueList.toArray(new IValue[valueList.size()]));
	}

	@Override
	public IValue visitMap(Type type) throws GraphDbMappingException {
		IMapWriter mapWriter = valueFactory.mapWriter();
		if (!node.hasRelationship(RelTypes.MAP_HEAD))		
			return mapWriter.done();
		Node currentKeyNode = node.getSingleRelationship(RelTypes.MAP_HEAD, Direction.OUTGOING).getEndNode();
		Node currentValueNode = currentKeyNode.getSingleRelationship(RelTypes.IS_MAP_VALUE, Direction.OUTGOING).getEndNode();
		mapWriter.put(TypeDeducer.getType(currentKeyNode).accept(new GraphDbTypeRetrievalVisitor(currentKeyNode, valueFactory)),
				TypeDeducer.getType(currentValueNode).accept(new GraphDbTypeRetrievalVisitor(currentValueNode, valueFactory)));
		while (currentKeyNode.hasRelationship(Direction.OUTGOING, RelTypes.NEXT_MAP_ELEMENT)) {
			currentKeyNode = currentKeyNode.getSingleRelationship(RelTypes.NEXT_MAP_ELEMENT, Direction.OUTGOING).getEndNode();
			currentValueNode = currentKeyNode.getSingleRelationship(RelTypes.IS_MAP_VALUE, Direction.OUTGOING).getEndNode();
			mapWriter.put(TypeDeducer.getType(currentKeyNode).accept(new GraphDbTypeRetrievalVisitor(currentKeyNode, valueFactory)),
					TypeDeducer.getType(currentValueNode).accept(new GraphDbTypeRetrievalVisitor(currentValueNode, valueFactory)));
		}
		return mapWriter.done();
	}
	
	@Override
	public IValue visitExternal(Type type) throws GraphDbMappingException {
		throw new GraphDbMappingException("Cannot handle external types.");
	}

	@Override
	public IValue visitNumber(Type type) throws GraphDbMappingException {
		throw new GraphDbMappingException("Cannot handle number types.");
	}

	@Override
	public IValue visitAlias(Type type) throws GraphDbMappingException {
		throw new GraphDbMappingException("Cannot handle alias types.");
	}	

	@Override
	public IValue visitAbstractData(Type type) throws GraphDbMappingException {
		throw new GraphDbMappingException("Cannot handle abstract data types.");
	}	

	@Override
	public IValue visitValue(Type type) throws GraphDbMappingException {
		throw new GraphDbMappingException("Cannot handle value types.");
	}

	@Override
	public IValue visitVoid(Type type) throws GraphDbMappingException {
		throw new GraphDbMappingException("Cannot handle void types.");
	}	

	@Override
	public IValue visitParameter(Type type) throws GraphDbMappingException {
		throw new GraphDbMappingException("Cannot handle parameter types.");
	}	

}
