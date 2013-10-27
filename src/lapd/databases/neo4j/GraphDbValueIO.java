package lapd.databases.neo4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
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
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.Index;
import org.neo4j.helpers.collection.MapUtil;
import org.neo4j.index.lucene.unsafe.batchinsert.LuceneBatchInserterIndexProvider;
import org.neo4j.unsafe.batchinsert.BatchInserter;
import org.neo4j.unsafe.batchinsert.BatchInserterIndex;
import org.neo4j.unsafe.batchinsert.BatchInserterIndexProvider;
import org.neo4j.unsafe.batchinsert.BatchInserters;
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
	
	private IValueFactory valueFactory;
	private String dbDirectoryPath;	
	
	private GraphDbValueIO() throws IOException {		
	}
	
	public void init(IValueFactory valueFactory) {
		this.valueFactory = valueFactory;
	}
	
	public String getDbDirectoryPath() {
		return dbDirectoryPath;
	}	
	
	private String fetchDbPath() throws IOException {
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
			dbDirectoryPath = userSpecifiedDir;
		else
			dbDirectoryPath = System.getProperty("user.home") + "/databases";
		return dbDirectoryPath + "/" + neo4jDbName;
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
		BatchInserter inserter = null;
		BatchInserterIndexProvider indexProvider = null;
		try {
			inserter = BatchInserters.inserter(fetchDbPath());				
			indexProvider = new LuceneBatchInserterIndexProvider(inserter);
			BatchInserterIndex nodeIndex = indexProvider.nodeIndex("nodes", MapUtil.stringMap("type", "exact"));
			if (nodeIndex.get("id", id).size() != 0)
				throw new GraphDbMappingException("Cannot write value to database. The id already exists.");
			Map<String, Object> properties = new HashMap<String, Object>();
			properties.put("id", id);
			long node = value.accept(new GraphDbValueInsertionVisitor(inserter, properties));
			nodeIndex.add(node, properties);
			nodeIndex.flush();
		} catch (IOException e) {
			throw new GraphDbMappingException("Could not open database for writing.");
		}
		finally {
			indexProvider.shutdown();
			inserter.shutdown();
		}
	}

	@Override
	public IValue read(String id, Type type, TypeStore typeStore) throws GraphDbMappingException {
		GraphDatabaseService graphDb = null;
		try {
			graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(fetchDbPath());
			registerShutdownHook(graphDb);
			Index<Node> nodeIndex = graphDb.index().forNodes("nodes");
			Node node = nodeIndex.get("id", id).getSingle();
			if (node == null)
				throw new GraphDbMappingException("Id not found.");
			try {
				return type.accept(new GraphDbValueRetrievalVisitor(node, valueFactory, typeStore));
			} catch (NotFoundException e) {
				throw new GraphDbMappingException("Could not find value. The id and type probably did not match.");
			}
		} catch (IOException e) {
			throw new GraphDbMappingException("Could not open database for reading.");
		}
		finally {
			graphDb.shutdown();
		}
	}

}
