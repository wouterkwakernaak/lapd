package lapd.databases.neo4j;

import org.eclipse.imp.pdb.facts.IValue;
import org.eclipse.imp.pdb.facts.type.Type;
import org.eclipse.imp.pdb.facts.type.TypeStore;

public abstract class AbstractGraphDbValueIO implements IGraphDbValueIO {

	@Override
	public IValue read(String id) throws GraphDbMappingException {
		return read(id, new TypeStore());
	}
	
	@Override
	public IValue read(String id, Type type) throws GraphDbMappingException {
		return read(id, type, new TypeStore());
	}
	
	@Override
	public IValue executeQuery(String query, Type type, boolean isCollection) throws GraphDbMappingException {
		return executeQuery(query, type, new TypeStore(), isCollection);
	}
	
}
