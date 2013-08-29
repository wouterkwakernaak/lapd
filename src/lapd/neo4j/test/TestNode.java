package lapd.neo4j.test;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.imp.pdb.facts.IValue;
import org.junit.Test;
import org.neo4j.graphdb.Transaction;

public class TestNode extends BaseGraphDbTest {

	@Test
	public void testInsertNullaryNode() {
		Transaction tx = graphDb.beginTx();
		try {
			valueFactory.node("").accept(graphDbValueVisitor);
			tx.success();
		}
		catch (Exception e) {
			fail(e.getMessage());
		}
		finally {
			tx.finish();
		}
		assertEquals(2, countNodes());
	}
	
	@Test
	public void testInsertNode() {
		Transaction tx = graphDb.beginTx();
		try {
			valueFactory.node("someNode", createAnnotations(), createChildren()).accept(graphDbValueVisitor);
			tx.success();
		}
		catch (Exception e) {
			fail(e.getMessage());
		}
		finally {
			tx.finish();
		}
		assertEquals(8, countNodes());
	}

	private Map<String, IValue> createAnnotations() {
		Map<String, IValue> annotations = new HashMap<String, IValue>();
		annotations.put("rating", valueFactory.integer(5));
		annotations.put("annotationX", valueFactory.string("itsover9000"));		
		return annotations;
	}

	private IValue[] createChildren() {
		IValue[] children = new IValue[3];
		children[0] = valueFactory.node("nodeX", valueFactory.bool(true));
		children[1] = valueFactory.datetime(System.currentTimeMillis());
		children[2] = valueFactory.rational(5, 3);
		return children;
	}
}
