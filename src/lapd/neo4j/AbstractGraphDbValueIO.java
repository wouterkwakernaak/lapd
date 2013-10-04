package lapd.neo4j;

import org.eclipse.imp.pdb.facts.IValue;
import org.eclipse.imp.pdb.facts.type.Type;
import org.eclipse.imp.pdb.facts.type.TypeStore;

public abstract class AbstractGraphDbValueIO implements IGraphDbValueIO {

	@Override
	public IValue read(String id, Type type) throws GraphDbMappingException {
		return read(id, type, new TypeStore());
	}
	
}
