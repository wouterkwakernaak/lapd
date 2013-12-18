package lapd.test.neo4j;

import static org.junit.Assert.*;
import lapd.databases.neo4j.GraphDbMappingException;

import org.eclipse.imp.pdb.facts.IValue;
import org.junit.Test;

public class TestRelation extends BaseGraphDbTest {

	@Test
	public void testRelation() throws GraphDbMappingException {
		IValue tuple1 = valueFactory.tuple(new IValue[] { valueFactory.integer(1), valueFactory.integer(2) });
		IValue tuple2 = valueFactory.tuple(new IValue[] { valueFactory.integer(2), valueFactory.integer(1) });
		IValue relationValue = valueFactory.set(tuple1, tuple2);
		graphDbValueIO.write(id, relationValue);
		assertEquals(relationValue, graphDbValueIO.read(id, relationValue.getType()));
	}

}
