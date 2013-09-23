package lapd.neo4j.test;

import static org.junit.Assert.*;
import lapd.neo4j.GraphDbMappingException;

import org.eclipse.imp.pdb.facts.IValue;
import org.junit.Test;

public class TestRelation extends BaseGraphDbTest {

	@Test
	public void testInsertRelation() throws GraphDbMappingException {
		IValue tuple1 = valueFactory.tuple(new IValue[] { valueFactory.integer(1), valueFactory.string("one") });
		IValue tuple2 = valueFactory.tuple(new IValue[] { valueFactory.integer(2), valueFactory.string("two") });
		IValue relationValue = valueFactory.set(tuple1, tuple2);
		graphDbValueIO.write(id, relationValue);
		assertEquals(relationValue, graphDbValueIO.read(id, relationValue));
	}

}
