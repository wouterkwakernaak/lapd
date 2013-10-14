package lapd.databases.neo4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.imp.pdb.facts.exceptions.OverloadingNotSupportedException;
import org.eclipse.imp.pdb.facts.type.Type;
import org.eclipse.imp.pdb.facts.type.TypeFactory;
import org.eclipse.imp.pdb.facts.type.TypeStore;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;

// constructs rascal types based on a neo4j node
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
		String name = currentNode.getProperty(PropertyNames.NODE).toString();
		String adtName = currentNode.getProperty(PropertyNames.ADT).toString();
		Type adt = typeStore.lookupAbstractDataType(adtName);		
		if (adt == null)
			return typeFactory.nodeType();
		return typeStore.lookupConstructor(adt, name).iterator().next();
	}

	private Type getTupleType() {
		if (!hasHead())
			return typeFactory.tupleEmpty();	
		currentNode = currentNode.getSingleRelationship(RelTypes.HEAD, Direction.OUTGOING).getEndNode();
		List<Type> argumentList = new ArrayList<Type>();
		argumentList.add(getType());
		while (currentNode.hasRelationship(Direction.OUTGOING, RelTypes.NEXT_ELEMENT)) {
			currentNode = currentNode.getSingleRelationship(RelTypes.NEXT_ELEMENT, 
					Direction.OUTGOING).getEndNode();
			argumentList.add(getType());
		}
		return typeFactory.tupleType(argumentList.toArray(new Type[argumentList.size()]));
	}

	private Type getMapType() {
		if (!hasHead())
			return typeFactory.mapType(typeFactory.voidType(), typeFactory.voidType());
		Node currentKeyNode = currentNode.getSingleRelationship(RelTypes.HEAD, Direction.OUTGOING).getEndNode();
		Node currentValueNode = currentKeyNode.getSingleRelationship(RelTypes.MAP_KEY_VALUE, 
				Direction.OUTGOING).getEndNode();
		Type leastUpperBoundKeyType = new TypeDeducer(currentKeyNode, typeStore).getType();
		Type leastUpperBoundValueType = new TypeDeducer(currentValueNode, typeStore).getType();
		while (currentNode.hasRelationship(Direction.OUTGOING, RelTypes.NEXT_ELEMENT)) {
			currentKeyNode = currentKeyNode.getSingleRelationship(RelTypes.NEXT_ELEMENT, 
					Direction.OUTGOING).getEndNode();
			currentValueNode = currentKeyNode.getSingleRelationship(RelTypes.MAP_KEY_VALUE, 
					Direction.OUTGOING).getEndNode();
			leastUpperBoundKeyType =  new TypeDeducer(currentKeyNode, typeStore).getType();
			leastUpperBoundValueType = new TypeDeducer(currentValueNode, typeStore).getType();
		}
		return typeFactory.mapType(leastUpperBoundKeyType, leastUpperBoundValueType);
	}

	private Type getSetType() {
		return (!hasHead()) ? typeFactory.setType(typeFactory.voidType()) : typeFactory.setType(getLub());
	}

	private Type getListType() {
		return (!hasHead()) ? typeFactory.listType(typeFactory.voidType()) : typeFactory.listType(getLub());
	}

	private Type getLub() {
		currentNode = currentNode.getSingleRelationship(RelTypes.HEAD, Direction.OUTGOING).getEndNode();
		Type leastUpperBoundType = getType();
		while (currentNode.hasRelationship(Direction.OUTGOING, RelTypes.NEXT_ELEMENT)) {
			currentNode = currentNode.getSingleRelationship(RelTypes.NEXT_ELEMENT, 
					Direction.OUTGOING).getEndNode();
			leastUpperBoundType = getType();
		}
		return leastUpperBoundType;
	}

	private boolean hasHead() {
		return currentNode.hasRelationship(Direction.OUTGOING, RelTypes.HEAD);
	}
	
}
