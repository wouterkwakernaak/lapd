package lapd.neo4j;

import org.eclipse.imp.pdb.facts.IValue;

public interface IGraphDbValueIO {
	
	public void write(IValue value) throws GraphDbMappingException;
	public void read();

}
