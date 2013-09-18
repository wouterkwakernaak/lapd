package lapd.hsqldb;

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
import org.eclipse.imp.pdb.facts.visitors.IValueVisitor;

public class RelationalDbValueVisitor implements IValueVisitor<Boolean, RelationalDbMappingException> {

	@Override
	public Boolean visitString(IString o) throws RelationalDbMappingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean visitReal(IReal o) throws RelationalDbMappingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean visitRational(IRational o)
			throws RelationalDbMappingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean visitList(IList o) throws RelationalDbMappingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean visitRelation(ISet o) throws RelationalDbMappingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean visitListRelation(IList o)
			throws RelationalDbMappingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean visitSet(ISet o) throws RelationalDbMappingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean visitSourceLocation(ISourceLocation o)
			throws RelationalDbMappingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean visitTuple(ITuple o) throws RelationalDbMappingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean visitNode(INode o) throws RelationalDbMappingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean visitConstructor(IConstructor o)
			throws RelationalDbMappingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean visitInteger(IInteger o) throws RelationalDbMappingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean visitMap(IMap o) throws RelationalDbMappingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean visitBoolean(IBool boolValue)
			throws RelationalDbMappingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean visitExternal(IExternalValue externalValue)
			throws RelationalDbMappingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean visitDateTime(IDateTime o)
			throws RelationalDbMappingException {
		// TODO Auto-generated method stub
		return null;
	}

}
