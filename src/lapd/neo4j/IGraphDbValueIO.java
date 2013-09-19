package lapd.neo4j;

import org.eclipse.imp.pdb.facts.IValue;

public interface IGraphDbValueIO {
	
	public void write(String id, IValue value) throws GraphDbMappingException;
	public <T> T read(String id, T value) throws GraphDbMappingException;

}
