package lapd.neo4j;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.Index;

public class Test {
	
	private String dbPath;
	private GraphDatabaseService graphDb;
	private Index<Node> nodeIndex;
	
	public Test() {
		Properties prop = new Properties();
		InputStream stream = ClassLoader.class.getResourceAsStream("/config.properties");
		try {
			prop.load(stream);
			dbPath = prop.getProperty("neo4jdbpath");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		Test test = new Test();
		test.openDatabase();
		test.testMethod();
		test.closeDatabase();		
	}
	
	private void openDatabase() {
		graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(dbPath);
		nodeIndex = graphDb.index().forNodes("nodes");
		registerShutdownHook(graphDb);		
	}
	
	private void closeDatabase() {
		graphDb.shutdown();
	}
	
	private static void registerShutdownHook(final GraphDatabaseService graphDb)
	{
	    Runtime.getRuntime().addShutdownHook(new Thread() {
	        @Override
	        public void run() {
	            graphDb.shutdown();
	        }
	    });
	}
	
	public void testMethod() throws Exception {
		executeTransaction(new ITransactionCommand() { public void execute() { doStuffToDb(); }});
	}
	
	public void doStuffToDb() { 
		Node someNode = graphDb.createNode();
		someNode.setProperty("value", true);
		nodeIndex.add(someNode, "value", someNode.getProperty("value"));
		Node anotherNode = graphDb.createNode();
		anotherNode.setProperty("value", false);
		Relationship relationship = someNode.createRelationshipTo(anotherNode, RelTypes.CHILD_NODE);
		relationship.setProperty("someProperty", "some text");	
	}

	public void executeTransaction(ITransactionCommand command) throws Exception {
		Transaction tx = graphDb.beginTx();
		try {
			command.execute();
			tx.success();
		}
		catch (Exception e) {
			throw e;
		}
		finally {
		    tx.finish();
		}		
	}

}
