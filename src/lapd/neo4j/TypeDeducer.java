package lapd.neo4j;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.imp.pdb.facts.type.Type;
import org.eclipse.imp.pdb.facts.type.TypeFactory;
import org.eclipse.imp.pdb.facts.type.TypeStore;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;

public class TypeDeducer {
	
	private static final TypeFactory typeFactory = TypeFactory.getInstance();
	private final TypeStore typeStore;
	private Node currentNode;	
	
	public TypeDeducer(Node node, TypeStore typeStore) {
		this.currentNode = node;
		this.typeStore = typeStore;
	}
	
	public Type getType() {
		String typeName = currentNode.getProperty(PropertyNames.TYPE).toString();
		switch (typeName) {
			case TypeNames.BOOLEAN:			return typeFactory.boolType();
			case TypeNames.DATE_TIME: 		return typeFactory.dateTimeType();
			case TypeNames.INTEGER: 		return typeFactory.integerType();
			case TypeNames.LIST: 			return getListType();
			case TypeNames.MAP: 			return getMapType();
			case TypeNames.NODE: 			return typeFactory.nodeType();
			case TypeNames.CONSTRUCTOR: 	return getConstructorType();
			case TypeNames.RATIONAL: 		return typeFactory.rationalType();
			case TypeNames.REAL: 			return typeFactory.realType();
			case TypeNames.SET: 			return getSetType();
			case TypeNames.SOURCE_LOCATION: return typeFactory.sourceLocationType();
			case TypeNames.STRING: 			return typeFactory.stringType();
			case TypeNames.TUPLE: 			return getTupleType();
			default: 						return typeFactory.valueType();
		}
	}

	private Type getConstructorType() {
		String name = currentNode.getProperty(PropertyNames.CONSTRUCTOR).toString();
		String adtName = currentNode.getProperty(PropertyNames.ADT).toString();
		Type adt = typeStore.lookupAbstractDataType(adtName);		
		return typeStore.lookupConstructor(adt, name).iterator().next();
	}

	private Type getTupleType() {
		if (!currentNode.hasRelationship(RelTypes.TUPLE_HEAD))
			return typeFactory.tupleEmpty();	
		currentNode = currentNode.getSingleRelationship(RelTypes.TUPLE_HEAD, Direction.OUTGOING).getEndNode();
		List<Type> argumentList = new ArrayList<Type>();
		argumentList.add(getType());
		while (currentNode.hasRelationship(Direction.OUTGOING, RelTypes.NEXT_TUPLE_ELEMENT)) {
			currentNode = currentNode.getSingleRelationship(RelTypes.NEXT_TUPLE_ELEMENT, Direction.OUTGOING).getEndNode();
			argumentList.add(getType());
		}
		return typeFactory.tupleType(argumentList.toArray(new Type[argumentList.size()]));
	}

	private Type getMapType() {
		if (!currentNode.hasRelationship(RelTypes.MAP_HEAD))
			return typeFactory.mapType(typeFactory.voidType(), typeFactory.voidType());
		Node currentKeyNode = currentNode.getSingleRelationship(RelTypes.MAP_HEAD, Direction.OUTGOING).getEndNode();
		Node currentValueNode = currentKeyNode.getSingleRelationship(RelTypes.IS_MAP_VALUE, Direction.OUTGOING).getEndNode();
		Type leastUpperBoundKeyType = new TypeDeducer(currentKeyNode, typeStore).getType();
		Type leastUpperBoundValueType = new TypeDeducer(currentValueNode, typeStore).getType();
		while (currentNode.hasRelationship(Direction.OUTGOING, RelTypes.NEXT_MAP_ELEMENT)) {
			currentKeyNode = currentKeyNode.getSingleRelationship(RelTypes.NEXT_MAP_ELEMENT, Direction.OUTGOING).getEndNode();
			currentValueNode = currentKeyNode.getSingleRelationship(RelTypes.IS_MAP_VALUE, Direction.OUTGOING).getEndNode();
			leastUpperBoundKeyType =  new TypeDeducer(currentKeyNode, typeStore).getType();
			leastUpperBoundValueType = new TypeDeducer(currentKeyNode, typeStore).getType();
		}
		return typeFactory.mapType(leastUpperBoundKeyType, leastUpperBoundValueType);
	}

	private Type getSetType() {
		if (!currentNode.hasRelationship(RelTypes.SET_HEAD))
			return typeFactory.setType(typeFactory.voidType());		
		currentNode = currentNode.getSingleRelationship(RelTypes.SET_HEAD, Direction.OUTGOING).getEndNode();
		Type leastUpperBoundType = getType();
		while (currentNode.hasRelationship(Direction.OUTGOING, RelTypes.NEXT_SET_ELEMENT)) {
			currentNode = currentNode.getSingleRelationship(RelTypes.NEXT_SET_ELEMENT, Direction.OUTGOING).getEndNode();
			leastUpperBoundType = getType();
		}
		return typeFactory.setType(leastUpperBoundType);
	}

	private Type getListType() {
		if (!currentNode.hasRelationship(RelTypes.LIST_HEAD))
			return typeFactory.listType(typeFactory.voidType());		
		currentNode = currentNode.getSingleRelationship(RelTypes.LIST_HEAD, Direction.OUTGOING).getEndNode();
		Type leastUpperBoundType = getType();
		while (currentNode.hasRelationship(Direction.OUTGOING, RelTypes.NEXT_LIST_ELEMENT)) {
			currentNode = currentNode.getSingleRelationship(RelTypes.NEXT_LIST_ELEMENT, Direction.OUTGOING).getEndNode();
			leastUpperBoundType = getType();
		}
		return typeFactory.listType(leastUpperBoundType);
	}
	
}
