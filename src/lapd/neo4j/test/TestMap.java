package lapd.neo4j.test;

import static org.junit.Assert.*;

import org.eclipse.imp.pdb.facts.IMap;
import org.eclipse.imp.pdb.facts.IMapWriter;
import org.junit.Test;
import org.neo4j.graphdb.Transaction;

public class TestMap extends BaseGraphDbTest {

	@Test
	public void testInsertMap() {
		IMapWriter mapWriter = valueFactory.mapWriter();
		mapWriter.put(valueFactory.integer(1), valueFactory.string("one"));
		mapWriter.put(valueFactory.integer(2), valueFactory.string("two"));
		IMap map = mapWriter.done();
		Transaction tx = graphDb.beginTx();
		try {
			map.accept(graphDbValueInsertionVisitor);
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
