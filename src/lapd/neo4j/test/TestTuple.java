package lapd.neo4j.test;

import static org.junit.Assert.*;

import java.util.Iterator;

import org.eclipse.imp.pdb.facts.IValue;
import org.eclipse.imp.pdb.facts.type.Type;
import org.eclipse.imp.pdb.facts.type.TypeFactory;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.helpers.collection.IteratorUtil;

public class TestTuple extends BaseGraphDbTest {
	
	private TypeFactory typeFactory;
	private ExecutionEngine engine;

	@Before
	public void setUp() {
		super.setUp();
		typeFactory = TypeFactory.getInstance();
	    engine = new ExecutionEngine(graphDb);
	}

	@Test
	public void testInsertNullaryTuple() {
		Transaction tx = graphDb.beginTx();
		try {
			valueFactory.tuple().accept(graphDbValueVisitor);			
			tx.success();
		}
		catch(Exception e) {
			fail(e.getMessage());
		}
		finally {
		    tx.finish();
		}
		assertEquals(2, countNodes());
	}
	
	@Test
	public void testInsertNonLabeledTuple() {
		Transaction tx = graphDb.beginTx();
		IValue[] tupleElements = createTupleElements();
		try {
			valueFactory.tuple(tupleElements).accept(graphDbValueVisitor);
			tx.success();
		}
		catch(Exception e) {
			fail(e.getMessage());
		}
		finally {
		    tx.finish();
		}
		int i = 0;
		for (Node node : globalGraphOperations.getAllNodes()) {
			if (node.getId() != 0) {
				assertEquals(tupleElements[i].toString(), node.getProperty("int").toString());
				i++;
			}
		}
	}	
	
	@SuppressWarnings("deprecation")
	@Test
	public void testInsertLabeledTuple() {
		Transaction tx = graphDb.beginTx();
		Type tupleType = createTupleType();
		IValue[] tupleElements = createTupleElements();
		try {
			valueFactory.tuple(tupleType, tupleElements).accept(graphDbValueVisitor);
			tx.success();
		}
		catch(Exception e) {
			fail(e.getMessage());
		}
		finally {
		    tx.finish();
		}
		ExecutionResult result = engine.execute("start n=node(*) return n");
		Iterator<Node> n_column = result.columnAs("n");
		int i = 0;
		for (Node node : IteratorUtil.asIterable(n_column)) {
			if (node.getId() != 0) {
				assertEquals(tupleType.getFieldNames()[i], node.getProperty("label"));
				assertEquals(tupleElements[i].toString(), node.getProperty("int").toString());
				i++;
			}
		}
	}
	
	private IValue[] createTupleElements() {
		IValue[] elements = new IValue[] { valueFactory.integer(3), valueFactory.integer(4), valueFactory.integer(5) };
		return elements;
	}

	private Type createTupleType() {
		Type[] types = new Type[] { typeFactory.integerType(), typeFactory.integerType(), typeFactory.integerType() };
		String[] labels = new String[] { "someInteger", "someInteger2", "someInteger3" };
		Type tupleType = typeFactory.tupleType(types, labels);
		return tupleType;
	}

}
