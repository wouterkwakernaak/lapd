package lapd.neo4j.test;

import static org.junit.Assert.*;

import org.eclipse.imp.pdb.facts.IValue;
import org.junit.Test;
import org.neo4j.graphdb.Transaction;

public class TestRelation extends BaseGraphDbTest {

	@Test
	public void testInsertRelation() {
		IValue tuple1 = valueFactory.tuple(new IValue[] { valueFactory.integer(1), valueFactory.string("one") });
		IValue tuple2 = valueFactory.tuple(new IValue[] { valueFactory.integer(2), valueFactory.string("two") });
		Transaction tx = graphDb.beginTx();
		try {
			valueFactory.set(tuple1, tuple2).accept(graphDbValueInsertionVisitor);
			tx.success();
		}
		catch(Exception e) {
			fail(e.getMessage());
		}
		finally {
			tx.finish();
		}
		assertEquals(5, countNodes());
	}

}
