package lapd.neo4j.test;

import java.io.IOException;
import java.util.UUID;

import lapd.neo4j.GraphDbValueIO;
import lapd.neo4j.IGraphDbValueIO;

import org.eclipse.imp.pdb.facts.IValueFactory;
import org.eclipse.imp.pdb.facts.impl.fast.ValueFactory;
import org.junit.Before;

public class BaseGraphDbTest {
	
	protected IValueFactory valueFactory;	
	protected IGraphDbValueIO graphDbValueIO;
	protected String id;

	@Before
	public void setUp() throws IOException {
		valueFactory = ValueFactory.getInstance();	    
	    graphDbValueIO = new GraphDbValueIO(false);
	    id = UUID.randomUUID().toString();
	}

}
