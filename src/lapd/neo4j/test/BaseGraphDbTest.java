package lapd.neo4j.test;

import java.io.IOException;
import java.util.Iterator;
import java.util.UUID;

import lapd.neo4j.GraphDbValueIO;
import lapd.neo4j.GraphDbValueInsertionVisitor;
import lapd.neo4j.IGraphDbValueIO;

import org.eclipse.imp.pdb.facts.IValueFactory;
import org.eclipse.imp.pdb.facts.impl.fast.ValueFactory;
import org.junit.After;
import org.junit.Before;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.test.TestGraphDatabaseFactory;
import org.neo4j.tooling.GlobalGraphOperations;

public class BaseGraphDbTest {
	
	protected IValueFactory valueFactory;
	protected GraphDatabaseService graphDb;	
	protected GraphDbValueInsertionVisitor graphDbValueInsertionVisitor;
	protected GlobalGraphOperations globalGraphOperations;
	protected IGraphDbValueIO graphDbValueIO;
	protected String id;

	@Before
	public void setUp() throws IOException {
		valueFactory = ValueFactory.getInstance();
	    graphDb = new TestGraphDatabaseFactory().newImpermanentDatabase();	    
	    graphDbValueInsertionVisitor = new GraphDbValueInsertionVisitor(graphDb);
	    globalGraphOperations = GlobalGraphOperations.at(graphDb);
	    graphDbValueIO = new GraphDbValueIO(false);
	    id = UUID.randomUUID().toString();
	}
	
	@After
	public void tearDown() {
	    graphDb.shutdown();
	}
	
	protected int countNodes() {
		int count = 0;
		Iterator<Node> iterator = globalGraphOperations.getAllNodes().iterator();
		while(iterator.hasNext()) {
			count++;
			iterator.next();
		}
		return count;
	}

}
