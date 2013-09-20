package lapd.neo4j;

import java.io.StringReader;
import java.util.Iterator;

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
import org.eclipse.imp.pdb.facts.IValueFactory;
import org.eclipse.imp.pdb.facts.impl.fast.ValueFactory;
import org.eclipse.imp.pdb.facts.io.IValueTextReader;
import org.eclipse.imp.pdb.facts.io.StandardTextReader;
import org.eclipse.imp.pdb.facts.type.TypeFactory;
import org.eclipse.imp.pdb.facts.visitors.IValueVisitor;
import org.neo4j.graphdb.Node;

public class GraphDbValueRetrievalVisitor implements IValueVisitor<IValue, GraphDbMappingException>{
	
	private Node node;
	private IValueFactory valueFactory;
	
	public GraphDbValueRetrievalVisitor(Node node) {
		this.node = node;
		valueFactory = ValueFactory.getInstance();
	}

	@Override
	public IValue visitString(IString stringValue) throws GraphDbMappingException {
		return valueFactory.string(node.getProperty(ValueNames.STRING).toString());
	}
	
	@Override
	public IValue visitInteger(IInteger integerValue) throws GraphDbMappingException {
		return valueFactory.integer(node.getProperty(ValueNames.INTEGER).toString());
	}

	@Override
	public IValue visitReal(IReal realValue) throws GraphDbMappingException {
		return valueFactory.real(node.getProperty(ValueNames.REAL).toString());
	}
	
	@Override
	public IValue visitBoolean(IBool boolValue) throws GraphDbMappingException {
		return valueFactory.bool((Boolean)node.getProperty(ValueNames.BOOLEAN));
	}

	@Override
	public IValue visitRational(IRational rationalValue) throws GraphDbMappingException {
		String numerator = node.getProperty(ValueNames.NUMERATOR).toString();
		String denominator = node.getProperty(ValueNames.DENOMINATOR).toString();
		return valueFactory.rational(numerator + "r" + denominator);
	}
	
	@Override
	public IValue visitSourceLocation(ISourceLocation sourceLocationValue) throws GraphDbMappingException {
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
	public IValue visitDateTime(IDateTime dateTimeValue) throws GraphDbMappingException {
		return valueFactory.datetime((Long)node.getProperty(ValueNames.DATE_TIME));
	}

	@Override
	public IValue visitList(IList listValue) throws GraphDbMappingException {
		Iterator<String> propertyKeys = node.getPropertyKeys().iterator();
		if (!propertyKeys.hasNext())	// empty list
			return valueFactory.list(TypeFactory.getInstance().voidType());
		return null;
	}
	
	@Override
	public IValue visitListRelation(IList listValue) throws GraphDbMappingException {
		Iterator<String> propertyKeys = node.getPropertyKeys().iterator();
		if (!propertyKeys.hasNext())	// empty list
			return valueFactory.list(TypeFactory.getInstance().voidType());
		return null;
	}
	
	@Override
	public IValue visitSet(ISet setValue) throws GraphDbMappingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IValue visitRelation(ISet setValue) throws GraphDbMappingException {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public IValue visitNode(INode nodeValue) throws GraphDbMappingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IValue visitConstructor(IConstructor constructorValue) throws GraphDbMappingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IValue visitTuple(ITuple tupleValue) throws GraphDbMappingException {
		// TODO Auto-generated method stub
		return null;
	}	

	@Override
	public IValue visitMap(IMap mapValue) throws GraphDbMappingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IValue visitExternal(IExternalValue externalValue) throws GraphDbMappingException {
		// TODO Auto-generated method stub
		return null;
	}

}
