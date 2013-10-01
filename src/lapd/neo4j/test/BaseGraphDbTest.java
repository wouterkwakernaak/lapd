package lapd.neo4j.test;

import java.io.IOException;
import java.util.UUID;

import lapd.neo4j.GraphDbValueIO;

import org.eclipse.imp.pdb.facts.IValueFactory;
import org.eclipse.imp.pdb.facts.impl.fast.ValueFactory;
import org.junit.Before;

public class BaseGraphDbTest {
	
	protected IValueFactory valueFactory;
	protected String id;
	protected GraphDbValueIO graphDbValueIO;

	@Before
	public void setUp() throws IOException {
		valueFactory = ValueFactory.getInstance();
	    id = UUID.randomUUID().toString();
	    graphDbValueIO = GraphDbValueIO.getInstance();
	    graphDbValueIO.init(valueFactory);
	}

}
