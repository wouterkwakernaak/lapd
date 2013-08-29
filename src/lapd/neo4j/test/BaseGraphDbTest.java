package lapd.neo4j.test;

import java.util.Iterator;

import lapd.neo4j.GraphDbValueVisitor;

import org.eclipse.imp.pdb.facts.IValueFactory;
import org.eclipse.imp.pdb.facts.impl.reference.ValueFactory;
import org.junit.After;
import org.junit.Before;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.test.TestGraphDatabaseFactory;
import org.neo4j.tooling.GlobalGraphOperations;

public class BaseGraphDbTest {
	
	protected IValueFactory valueFactory;
	protected GraphDatabaseService graphDb;	
	protected GraphDbValueVisitor graphDbValueVisitor;
	protected GlobalGraphOperations globalGraphOperations;

	@Before
	public void setUp() {
		valueFactory = ValueFactory.getInstance();
	    graphDb = new TestGraphDatabaseFactory().newImpermanentDatabase();	    
	    graphDbValueVisitor = new GraphDbValueVisitor(graphDb);
	    globalGraphOperations = GlobalGraphOperations.at(graphDb);
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
