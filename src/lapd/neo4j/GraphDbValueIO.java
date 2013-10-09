package lapd.neo4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.imp.pdb.facts.IValue;
import org.eclipse.imp.pdb.facts.IValueFactory;
import org.eclipse.imp.pdb.facts.type.Type;
import org.eclipse.imp.pdb.facts.type.TypeStore;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.NotFoundException;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.Index;
import org.osgi.framework.Bundle;

public class GraphDbValueIO extends AbstractGraphDbValueIO {
	
	private static final GraphDbValueIO instance;
	
	static {
        try { 
        	instance = new GraphDbValueIO();
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }
	
	public static GraphDbValueIO getInstance() {
		return instance;
	}
	
	private final GraphDbValueInsertionVisitor graphDbValueInsertionVisitor;
	private IValueFactory valueFactory;
	private final GraphDatabaseService graphDb;
	private final Index<Node> nodeIndex;
	
	
	private GraphDbValueIO() throws IOException {
		String dbPath = getDbPath();
		graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(dbPath);
		registerShutdownHook(graphDb);
		nodeIndex = graphDb.index().forNodes("nodes");
		graphDbValueInsertionVisitor = new GraphDbValueInsertionVisitor(graphDb);		
	}
	
	public void init(IValueFactory valueFactory) {
		this.valueFactory = valueFactory;
	}
	
	private String getDbPath() throws IOException {
		Properties prop = new Properties();
		InputStream stream;
		Bundle bundle = Platform.getBundle("lapd");
		if (bundle != null)
			stream = FileLocator.openStream(bundle,	new Path("bin/config.properties"), false);
		else
			stream = ClassLoader.class.getResourceAsStream("/config.properties");
		prop.load(stream);
		String neo4jDbName = prop.getProperty("neo4jDbName");
		String userSpecifiedDir = prop.getProperty("databasesDirectory");
		if (userSpecifiedDir != null)
			return userSpecifiedDir + "/" + neo4jDbName;
		return System.getProperty("user.home") + "/databases/" + neo4jDbName;
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
	
	public void shutdown() {
		graphDb.shutdown();
	}

	@Override
	public void write(String id, IValue value) throws GraphDbMappingException {
		if (nodeIndex.get("id", id).size() != 0)
			throw new GraphDbMappingException("Cannot write value to database. The id already exists.");
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
	public IValue read(String id, Type type, TypeStore typeStore) throws GraphDbMappingException {
		Node node = nodeIndex.get("id", id).getSingle();
		if (node == null)
			throw new GraphDbMappingException("Id not found.");
		try {
			return type.accept(new GraphDbValueRetrievalVisitor(node, valueFactory, typeStore));
		}
		catch (NotFoundException e) {
			throw new GraphDbMappingException("Could not find value. The id and type probably did not match.");
		}
	}

}
