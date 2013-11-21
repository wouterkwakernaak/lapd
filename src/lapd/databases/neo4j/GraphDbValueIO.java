package lapd.databases.neo4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.imp.pdb.facts.IValue;
import org.eclipse.imp.pdb.facts.IValueFactory;
import org.eclipse.imp.pdb.facts.type.Type;
import org.eclipse.imp.pdb.facts.type.TypeStore;
import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
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
	//private GraphDatabaseService graphDb;
	//private Index<Node> nodeIndex;
	private String dbDirectoryPath;
	//private ExecutionEngine queryEngine;
	
	private final BatchInserter inserter;
	private final BatchInserterIndexProvider indexProvider;
	private final BatchInserterIndex nodeIndex;
	
	private GraphDbValueIO() throws IOException {
		Map<String, String> config = new HashMap<String, String>();
		config.put("cache_type", "none");
		config.put("use_memory_mapped_buffers", "true");
		config.put("logical_log_rotation_threshold", "500M");
		config.put("neostore.nodestore.db.mapped_memory", "100M");
		config.put("neostore.relationshipstore.db.mapped_memory", "100M");
		config.put("neostore.propertystore.db.mapped_memory", "100M");
		config.put("neostore.propertystore.db.strings.mapped_memory", "100M");
		config.put("neostore.propertystore.db.arrays.mapped_memory", "0M");
		//graphDb = new GraphDatabaseFactory().newEmbeddedDatabaseBuilder(fetchDbPath()).setConfig(config).newGraphDatabase();
		//queryEngine = new ExecutionEngine(graphDb);
		
		//nodeIndex = graphDb.index().forNodes("nodes");
		inserter = BatchInserters.inserter(fetchDbPath(), config);				
		indexProvider = new LuceneBatchInserterIndexProvider(inserter);
		nodeIndex = indexProvider.nodeIndex("nodes", MapUtil.stringMap("type", "exact"));
		registerShutdownHook(inserter, indexProvider);
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
		try {
//			if (nodeIndex.get("id", id).size() != 0)
//				throw new GraphDbMappingException("Cannot write value to database. The id already exists.");
			Map<String, Object> properties = new HashMap<String, Object>();
			properties.put("id", id);
			long node = value.accept(new GraphDbValueInsertionVisitor(inserter, properties));
			nodeIndex.add(node, properties);
			nodeIndex.flush();
		} catch (Exception e) {
			throw new GraphDbMappingException("Could not open database for writing.");
		}
		finally {
			//indexProvider.shutdown();
			//inserter.shutdown();
		}
	}
	
	@Override
	public void write(String id, IValue value, boolean deleteOld) throws GraphDbMappingException {
//		if (!deleteOld)
//			write(id, value);
//		else {
//			queryEngine.execute("start n=node:nodes(id = '" + id + "') match n-[r]-() delete n, r");
//			write(id, value);
//		}
	}
	
	@Override
	public IValue read(String id, TypeStore typeStore) throws GraphDbMappingException {
//		Node node = nodeIndex.get("id", id).getSingle();
//		if (node == null)
//			throw new IdNotFoundException("Id " + id + " not found.");
//		return new TypeDeducer(node, typeStore).getType().accept(new GraphDbValueRetrievalVisitor(node, valueFactory, typeStore));
		return null;
	}

	@Override
	public IValue read(String id, Type type, TypeStore typeStore) throws GraphDbMappingException {
//		GraphDatabaseService graphDb = null;
//		try {
//			graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(fetchDbPath());
//			registerShutdownHook(graphDb);
//			Index<Node> nodeIndex = graphDb.index().forNodes("nodes");
//			Node node = nodeIndex.get("id", id).getSingle();
//			if (node == null)
//				throw new GraphDbMappingException("Id not found.");
//			try {
//				return type.accept(new GraphDbValueRetrievalVisitor(node, valueFactory, typeStore));
//			} catch (NotFoundException e) {
//				throw new GraphDbMappingException("Could not find value. The id and type probably did not match.");
//			}
//		} catch (IOException e) {
//			throw new GraphDbMappingException("Could not open database for reading.");
//		}
//		finally {
//			graphDb.shutdown();
//		}
		return null;
	}

	@Override
	public IValue executeQuery(String query, TypeStore typeStore) throws GraphDbMappingException {
//		ExecutionResult result = queryEngine.execute(query);
//		if (!result.columns().isEmpty()) {
//			Iterator<Node> column = result.columnAs(result.columns().get(0));
//			if (column.hasNext()) {
//				Node node = column.next();
//				return new TypeDeducer(node, typeStore).getType().accept(new GraphDbValueRetrievalVisitor(node, valueFactory, typeStore));
//			}
//		}
//		throw new GraphDbMappingException("No query results were found.");
		return null;
	}	

	@Override
	public IValue executeQuery(String query, Type type, TypeStore typeStore) throws GraphDbMappingException {
//		ExecutionResult result = queryEngine.execute(query);
//		if (!result.columns().isEmpty()) {
//			Iterator<Node> column = result.columnAs(result.columns().get(0));
//			if (column.hasNext()) {
//				Node node = column.next();
//				try {
//					return type.accept(new GraphDbValueRetrievalVisitor(node, valueFactory, typeStore));
//				}
//				catch (NotFoundException e) {
//					throw new GraphDbMappingException("The type probably did not match the query result.");
//				}
//			}
//		}
//		throw new GraphDbMappingException("No query results were found.");
		return null;
	}

	@Override
	public boolean idExists(String id) {
		Long node = nodeIndex.get("id", id).getSingle();
		return node == null ? false : true;
	}

}
