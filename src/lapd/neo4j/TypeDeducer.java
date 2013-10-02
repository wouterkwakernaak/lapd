package lapd.neo4j;

import org.eclipse.imp.pdb.facts.type.Type;
import org.eclipse.imp.pdb.facts.type.TypeFactory;
import org.neo4j.graphdb.Node;

public class TypeDeducer {
	
	private static final TypeFactory typeFactory = TypeFactory.getInstance();
	
	public static Type getType(Node node) {
		String type = node.getProperty(PropertyNames.TYPE).toString();
		switch (type) {
			case TypeNames.BOOLEAN:			return typeFactory.boolType();
			case TypeNames.DATE_TIME: 		return typeFactory.dateTimeType();
			case TypeNames.INTEGER: 		return typeFactory.integerType();
			case TypeNames.LIST: 			return typeFactory.listType(typeFactory.valueType());
			case TypeNames.MAP: 			return typeFactory.mapType(typeFactory.valueType(), typeFactory.valueType());
			case TypeNames.NODE: 			return typeFactory.nodeType();
			case TypeNames.CONSTRUCTOR: 	return null; // type cannot be created without additional info
			case TypeNames.RATIONAL: 		return typeFactory.rationalType();
			case TypeNames.REAL: 			return typeFactory.realType();
			case TypeNames.SET: 			return typeFactory.setType(typeFactory.valueType());
			case TypeNames.SOURCE_LOCATION: return typeFactory.sourceLocationType();
			case TypeNames.STRING: 			return typeFactory.stringType();
			case TypeNames.TUPLE: 			return typeFactory.tupleEmpty();
			default: 						return typeFactory.valueType();
		}
	}
	
}
