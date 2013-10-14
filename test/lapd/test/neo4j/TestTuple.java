package lapd.test.neo4j;

import static org.junit.Assert.*;
import lapd.databases.neo4j.GraphDbMappingException;

import org.eclipse.imp.pdb.facts.IValue;
import org.junit.Test;

public class TestTuple extends BaseGraphDbTest {

	@Test
	public void testEmptyTuple() throws GraphDbMappingException {
		IValue tupleValue = valueFactory.tuple();
		graphDbValueIO.write(id, tupleValue);
		assertEquals(tupleValue, graphDbValueIO.read(id, tupleValue.getType()));
	}
	
	@Test
	public void testTuple() throws GraphDbMappingException {
		IValue tupleValue = valueFactory.tuple(createTupleElements());
		graphDbValueIO.write(id, tupleValue);
		assertEquals(tupleValue, graphDbValueIO.read(id, tupleValue.getType()));
	}	
	
	private IValue[] createTupleElements() {
		IValue[] elements = new IValue[] { valueFactory.integer(3), valueFactory.integer(4), valueFactory.integer(5) };
		return elements;
	}
	
}
