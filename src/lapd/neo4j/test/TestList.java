package lapd.neo4j.test;

import static org.junit.Assert.*;

import org.eclipse.imp.pdb.facts.IBool;
import org.eclipse.imp.pdb.facts.IList;
import org.eclipse.imp.pdb.facts.IListWriter;
import org.junit.Test;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.tooling.GlobalGraphOperations;

public class TestList extends BaseGraphDbTest {

	@Test
	public void testInsertBooleanList() {
		IList booleanList = createBooleanList();
		Transaction tx = graphDb.beginTx();
		try {
			booleanList.accept(graphDbValueInsertionVisitor);
			tx.success();
		}
		catch(Exception e) {
			fail(e.getMessage());
		}
		finally {
		    tx.finish();
		}
		GlobalGraphOperations op = GlobalGraphOperations.at(graphDb);
		int i = 0;
		for (Node node : op.getAllNodes()) {
			if (node.getId() != 0) {
				assertEquals(booleanList.get(i).toString(), node.getProperty("bool").toString());
				i++;
			}			
		}
	}
	
	@Test
	public void testInsertIntegerList() {
		IList integerList = createIntegerList();
		Transaction tx = graphDb.beginTx();
		try {
			integerList.accept(graphDbValueInsertionVisitor);
			tx.success();
		}
		catch(Exception e) {
			fail(e.getMessage());
		}
		finally {
		    tx.finish();
		}
		GlobalGraphOperations op = GlobalGraphOperations.at(graphDb);
		int i = 0;
		for (Node node : op.getAllNodes()) {
			if (node.getId() != 0) {
				assertEquals(integerList.get(i).toString(), node.getProperty("int").toString());
				i++;
			}
		}
	}
	
	@Test
	public void testInsertListOfLists() {
		IList integerList1 = createIntegerList();
		IList integerList2 = createIntegerList();
		IList listOfLists = valueFactory.list(integerList1, integerList2);
		Transaction tx = graphDb.beginTx();
		try {
			listOfLists.accept(graphDbValueInsertionVisitor);
			tx.success();
		}
		catch(Exception e) {
			fail(e.getMessage());
		}
		finally {
		    tx.finish();
		}
		assertEquals(41, countNodes());
	}

	private IList createBooleanList() {
		IListWriter listWriter = valueFactory.listWriter();
		for (int i = 0; i < 20; i++) {
			IBool bool = null;
			if (i % 2 == 0)
				bool = valueFactory.bool(true);
			else
				bool = valueFactory.bool(false);
			listWriter.append(bool);
		}
		return listWriter.done();
	}
	
	private IList createIntegerList() {
		IListWriter listWriter = valueFactory.listWriter();
		for (int i = 1; i <= 20; i++) {
			listWriter.append(valueFactory.integer(i));
		}
		return listWriter.done();
	}

}
