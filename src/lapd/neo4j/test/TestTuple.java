package lapd.neo4j.test;

import static org.junit.Assert.*;

import org.eclipse.imp.pdb.facts.IValue;
import org.junit.Test;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

public class TestTuple extends BaseGraphDbTest {

	@Test
	public void testEmptyTuple() {
		Transaction tx = graphDb.beginTx();
		try {
			valueFactory.tuple().accept(graphDbValueInsertionVisitor);			
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
	public void testTuple() {
		Transaction tx = graphDb.beginTx();
		IValue[] tupleElements = createTupleElements();
		try {
			valueFactory.tuple(tupleElements).accept(graphDbValueInsertionVisitor);
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
	
	private IValue[] createTupleElements() {
		IValue[] elements = new IValue[] { valueFactory.integer(3), valueFactory.integer(4), valueFactory.integer(5) };
		return elements;
	}
	
}
