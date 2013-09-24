package lapd.neo4j;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.imp.pdb.facts.IMapWriter;
import org.eclipse.imp.pdb.facts.IValue;
import org.eclipse.imp.pdb.facts.IValueFactory;
import org.eclipse.imp.pdb.facts.impl.fast.ValueFactory;
import org.eclipse.imp.pdb.facts.io.IValueTextReader;
import org.eclipse.imp.pdb.facts.io.StandardTextReader;
import org.eclipse.imp.pdb.facts.type.ITypeVisitor;
import org.eclipse.imp.pdb.facts.type.Type;
import org.eclipse.imp.pdb.facts.type.TypeFactory;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;

public class GraphDbTypeRetrievalVisitor implements ITypeVisitor<IValue, GraphDbMappingException>{
	
	private final Node node;
	private final IValueFactory valueFactory;
	
	public GraphDbTypeRetrievalVisitor(Node node) {
		this.node = node;
		valueFactory = ValueFactory.getInstance();
	}
	
	@Override
	public IValue visitString(Type type) throws GraphDbMappingException {
		return valueFactory.string(node.getProperty(ValueNames.STRING).toString());
	}
	
	@Override
	public IValue visitInteger(Type type) throws GraphDbMappingException {
		return valueFactory.integer(node.getProperty(ValueNames.INTEGER).toString());
	}

	@Override
	public IValue visitReal(Type type) throws GraphDbMappingException {
		return valueFactory.real(node.getProperty(ValueNames.REAL).toString());
	}

	@Override
	public IValue visitBool(Type type) throws GraphDbMappingException {
		return valueFactory.bool((Boolean)node.getProperty(ValueNames.BOOLEAN));
	}

	@Override
	public IValue visitRational(Type type) throws GraphDbMappingException {
		String numerator = node.getProperty(ValueNames.NUMERATOR).toString();
		String denominator = node.getProperty(ValueNames.DENOMINATOR).toString();
		return valueFactory.rational(numerator + "r" + denominator);
	}
	
	@Override
	public IValue visitSourceLocation(Type type) throws GraphDbMappingException {
		String locString = node.getProperty(ValueNames.SOURCE_LOCATION).toString();
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
		return valueFactory.datetime((Long)node.getProperty(ValueNames.DATE_TIME));
	}

	@Override
	public IValue visitList(Type type) throws GraphDbMappingException {
		if (!node.hasRelationship(RelTypes.LIST_HEAD))
			return valueFactory.list(TypeFactory.getInstance().voidType());
		List<IValue> valueList = new ArrayList<IValue>();
		Node currentNode = node.getSingleRelationship(RelTypes.LIST_HEAD, Direction.OUTGOING).getEndNode();
		valueList.add(type.getElementType().accept(new GraphDbTypeRetrievalVisitor(currentNode)));
		while (currentNode.hasRelationship(Direction.OUTGOING, RelTypes.NEXT_LIST_ELEMENT)) {
			currentNode = currentNode.getSingleRelationship(RelTypes.NEXT_LIST_ELEMENT, Direction.OUTGOING).getEndNode();
			valueList.add(type.getElementType().accept(new GraphDbTypeRetrievalVisitor(currentNode)));
		}
		return valueFactory.list(valueList.toArray(new IValue[valueList.size()]));
	}
	
	@Override
	public IValue visitSet(Type type) throws GraphDbMappingException {
		if (!node.hasRelationship(RelTypes.SET_HEAD))
			return valueFactory.set(TypeFactory.getInstance().voidType());
		List<IValue> valueList = new ArrayList<IValue>();
		Node currentNode = node.getSingleRelationship(RelTypes.SET_HEAD, Direction.OUTGOING).getEndNode();
		valueList.add(type.getElementType().accept(new GraphDbTypeRetrievalVisitor(currentNode)));
		while (currentNode.hasRelationship(Direction.OUTGOING, RelTypes.NEXT_SET_ELEMENT)) {
			currentNode = currentNode.getSingleRelationship(RelTypes.NEXT_SET_ELEMENT, Direction.OUTGOING).getEndNode();
			valueList.add(type.getElementType().accept(new GraphDbTypeRetrievalVisitor(currentNode)));
		}
		return valueFactory.set(valueList.toArray(new IValue[valueList.size()]));
	}
	
	@Override
	public IValue visitNode(Type type) throws GraphDbMappingException {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public IValue visitConstructor(Type type) throws GraphDbMappingException {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public IValue visitTuple(Type type) throws GraphDbMappingException {
		if (!node.hasRelationship(RelTypes.TUPLE_HEAD))
			return valueFactory.tuple();
		List<IValue> valueList = new ArrayList<IValue>();
		Node currentNode = node.getSingleRelationship(RelTypes.TUPLE_HEAD, Direction.OUTGOING).getEndNode();
		int count = 0;
		valueList.add(type.getFieldType(count).accept(new GraphDbTypeRetrievalVisitor(currentNode)));
		while (currentNode.hasRelationship(Direction.OUTGOING, RelTypes.NEXT_TUPLE_ELEMENT)) {
			count++;
			currentNode = currentNode.getSingleRelationship(RelTypes.NEXT_TUPLE_ELEMENT, Direction.OUTGOING).getEndNode();
			valueList.add(type.getFieldType(count).accept(new GraphDbTypeRetrievalVisitor(currentNode)));
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
		mapWriter.put(type.getKeyType().accept(new GraphDbTypeRetrievalVisitor(currentKeyNode)),
				type.getValueType().accept(new GraphDbTypeRetrievalVisitor(currentValueNode)));
		while (currentKeyNode.hasRelationship(Direction.OUTGOING, RelTypes.NEXT_MAP_ELEMENT)) {
			currentKeyNode = currentKeyNode.getSingleRelationship(RelTypes.NEXT_MAP_ELEMENT, Direction.OUTGOING).getEndNode();
			currentValueNode = currentKeyNode.getSingleRelationship(RelTypes.IS_MAP_VALUE, Direction.OUTGOING).getEndNode();
			mapWriter.put(type.getKeyType().accept(new GraphDbTypeRetrievalVisitor(currentKeyNode)),
					type.getValueType().accept(new GraphDbTypeRetrievalVisitor(currentValueNode)));
		}
		return mapWriter.done();
	}
	
	@Override
	public IValue visitExternal(Type type) throws GraphDbMappingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IValue visitNumber(Type type) throws GraphDbMappingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IValue visitAlias(Type type) throws GraphDbMappingException {
		// TODO Auto-generated method stub
		return null;
	}	

	@Override
	public IValue visitAbstractData(Type type) throws GraphDbMappingException {
		// TODO Auto-generated method stub
		return null;
	}	

	@Override
	public IValue visitValue(Type type) throws GraphDbMappingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IValue visitVoid(Type type) throws GraphDbMappingException {
		// TODO Auto-generated method stub
		return null;
	}	

	@Override
	public IValue visitParameter(Type type) throws GraphDbMappingException {
		// TODO Auto-generated method stub
		return null;
	}	

}
