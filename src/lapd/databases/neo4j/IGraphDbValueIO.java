package lapd.databases.neo4j;

import org.eclipse.imp.pdb.facts.IValue;
import org.eclipse.imp.pdb.facts.type.Type;
import org.eclipse.imp.pdb.facts.type.TypeStore;

public interface IGraphDbValueIO {
	
	public void write(String id, IValue value) throws GraphDbMappingException;
	public IValue read(String id, Type type) throws GraphDbMappingException;
	public IValue read(String id, Type type, TypeStore TypeStore) throws GraphDbMappingException;

}
