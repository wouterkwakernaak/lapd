package lapd.neo4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.eclipse.imp.pdb.facts.IValue;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

public class GraphDbValueIO implements IGraphDbValueIO {
	
	private GraphDbValueVisitor graphDbValueVisitor;
	private GraphDatabaseService graphDb;
	
	public GraphDbValueIO() throws IOException {
		String dbPath = getDbPath();
		graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(dbPath);
		registerShutdownHook(graphDb);
		graphDbValueVisitor = new GraphDbValueVisitor(graphDb);		
	}

	private String getDbPath() throws IOException {
		Properties prop = new Properties();
		InputStream stream = ClassLoader.class.getResourceAsStream("/config.properties");
		prop.load(stream);
		String path = prop.getProperty("databasesFolderPath") + prop.getProperty("neo4jDbName");
		return path;
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

	@Override
	public void write(String id, IValue value) throws GraphDbMappingException {
		Transaction tx = graphDb.beginTx();
		Node node = value.accept(graphDbValueVisitor);
		node.setProperty("id", id);
		tx.success();
		tx.finish();		
	}

	@Override
	public <T> T read(String id, T value) {
		return null;		
	}

}
