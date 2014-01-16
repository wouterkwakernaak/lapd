package lapd.databases.neo4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.imp.pdb.facts.ISet;
import org.eclipse.imp.pdb.facts.IValue;
import org.eclipse.imp.pdb.facts.IValueFactory;
import org.eclipse.imp.pdb.facts.type.Type;
import org.eclipse.imp.pdb.facts.type.TypeStore;
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
	
	private String dbDirectoryPath;	
	private final BatchInserter inserter;
	private final BatchInserterIndexProvider indexProvider;
	private final BatchInserterIndex nodeIndex;
	
	private GraphDbValueIO() throws IOException {
		Map<String, String> config = new HashMap<String, String>();
		config.put("cache_type", "soft");
		config.put("use_memory_mapped_buffers", "true");
		config.put("logical_log_rotation_threshold", "500M");
		config.put("neostore.nodestore.db.mapped_memory", "100M");
		config.put("neostore.relationshipstore.db.mapped_memory", "100M");
		config.put("neostore.propertystore.db.mapped_memory", "100M");
		config.put("neostore.propertystore.db.strings.mapped_memory", "100M");
		config.put("neostore.propertystore.db.arrays.mapped_memory", "0M");
		inserter = BatchInserters.inserter(fetchDbPath(), config);				
		indexProvider = new LuceneBatchInserterIndexProvider(inserter);
		nodeIndex = indexProvider.nodeIndex("nodes", MapUtil.stringMap("type", "exact"));
		registerShutdownHook(inserter, indexProvider);
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
	
	private static void registerShutdownHook(final BatchInserter inserter, final BatchInserterIndexProvider indexProvider)
	{
	    Runtime.getRuntime().addShutdownHook(new Thread() {
	        @Override
	        public void run() {
	        	indexProvider.shutdown();
	            inserter.shutdown();
	        }
	    });
	}

	@Override
	public void write(String id, IValue value) throws GraphDbMappingException {
		if (nodeIndex.get("id", id).size() != 0)
			throw new GraphDbMappingException("Cannot write value to database. The id already exists.");
		writeToDb(id, value);
	}

	private void writeToDb(String id, IValue value)	throws GraphDbMappingException {
		try {
			Map<String, Object> properties = new HashMap<String, Object>();
			long node = value.accept(new GraphDbValueInsertionVisitor(inserter, properties, nodeIndex, true, id));
			properties.put("id", id);
			nodeIndex.add(node, properties);
			nodeIndex.flush();
		} catch (Exception e) {
			throw new GraphDbMappingException("Could not open database for writing.");
		}
	}
	
	@Override
	public void write(String id, IValue value, boolean deleteOld) throws GraphDbMappingException {
	}
	
	@Override
	public IValue read(String id, TypeStore typeStore) throws GraphDbMappingException {
		return null;
	}

	@Override
	public IValue read(String id, Type type, TypeStore typeStore) throws GraphDbMappingException {
		return null;
	}

	@Override
	public boolean idExists(String id) {
		Long node = nodeIndex.get("id", id).getSingle();
		return node == null ? false : true;
	}

	@Override
	public ISet executeJavaQuery(int queryId, String graphId, Type type, TypeStore typeStore) throws GraphDbMappingException {
		return null;
	}

	@Override
	public IValue executeQuery(String query, Type type, TypeStore typeStore, boolean isCollection) throws GraphDbMappingException {
		return null;
	}

	public void init(IValueFactory valueFactory) {		
	}

}
