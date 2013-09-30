package lapd.neo4j;

import org.eclipse.imp.pdb.facts.IValue;
import org.eclipse.imp.pdb.facts.type.Type;

public interface IGraphDbValueIO {
	
	public void write(String id, IValue value) throws GraphDbMappingException;
	public IValue read(String id, Type type) throws GraphDbMappingException;

}
