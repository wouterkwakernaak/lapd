package lapd.neo4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.eclipse.imp.pdb.facts.IConstructor;
import org.eclipse.imp.pdb.facts.IValue;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.Index;
import org.neo4j.test.TestGraphDatabaseFactory;

public class GraphDbValueIO implements IGraphDbValueIO {
	
	private final GraphDbValueInsertionVisitor graphDbValueInsertionVisitor;
	private final GraphDatabaseService graphDb;
	private final Index<Node> nodeIndex;
	
	public GraphDbValueIO(boolean persistent) throws IOException {
		if (persistent) {
			String dbPath = getDbPath();
			graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(dbPath);
		}
		else 
			graphDb = new TestGraphDatabaseFactory().newImpermanentDatabase();
		registerShutdownHook(graphDb);
		nodeIndex = graphDb.index().forNodes("nodes");
		graphDbValueInsertionVisitor = new GraphDbValueInsertionVisitor(graphDb);		
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
		try {
			Node node = value.accept(graphDbValueInsertionVisitor);
			node.setProperty("id", id);
			nodeIndex.add(node, "id", id);
			tx.success();
		}
		catch (Exception e) { 
			throw new GraphDbMappingException(e.getMessage()); 
		}
		finally {
			tx.finish();
		}
	}

	@Override
	public <T> T read(String id, T value) throws GraphDbMappingException {
		Node node = nodeIndex.get("id", id).getSingle();
		IValue retrievedValue = null;
		if (value instanceof IConstructor)
			retrievedValue = ((IConstructor)value).getConstructorType().accept(new GraphDbTypeRetrievalVisitor(node));
		else
			retrievedValue = ((IValue)value).getType().accept(new GraphDbTypeRetrievalVisitor(node));
		return (T)retrievedValue;
	}

}
