package lapd.neo4j;

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
		return valueFactory.string(node.getProperty("str").toString());
	}
	
	@Override
	public IValue visitInteger(IInteger integerValue) throws GraphDbMappingException {
		return valueFactory.integer(node.getProperty("int").toString());
	}

	@Override
	public IValue visitReal(IReal realValue) throws GraphDbMappingException {
		return valueFactory.real(node.getProperty("real").toString());
	}
	
	@Override
	public IValue visitBoolean(IBool boolValue) throws GraphDbMappingException {
		return valueFactory.bool((Boolean)node.getProperty("bool"));
	}

	@Override
	public IValue visitRational(IRational rationalValue) throws GraphDbMappingException {
		String numerator = node.getProperty("numerator").toString();
		String denominator = node.getProperty("denominator").toString();
		return valueFactory.rational(numerator + "r" + denominator);
	}
	
	@Override
	public IValue visitSourceLocation(ISourceLocation sourceLocationValue) throws GraphDbMappingException {
		return valueFactory.sourceLocation(node.getProperty("loc").toString());
	}
	
	@Override
	public IValue visitDateTime(IDateTime dateTime) throws GraphDbMappingException {
		return valueFactory.datetime((Long)node.getProperty("datetime"));
	}

	@Override
	public IValue visitList(IList o) throws GraphDbMappingException {
		return null;
	}
	
	@Override
	public IValue visitListRelation(IList o) throws GraphDbMappingException {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public IValue visitSet(ISet o) throws GraphDbMappingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IValue visitRelation(ISet o) throws GraphDbMappingException {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public IValue visitNode(INode o) throws GraphDbMappingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IValue visitConstructor(IConstructor o) throws GraphDbMappingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IValue visitTuple(ITuple o) throws GraphDbMappingException {
		// TODO Auto-generated method stub
		return null;
	}	

	@Override
	public IValue visitMap(IMap o) throws GraphDbMappingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IValue visitExternal(IExternalValue externalValue) throws GraphDbMappingException {
		// TODO Auto-generated method stub
		return null;
	}

}
