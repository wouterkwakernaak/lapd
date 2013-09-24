package lapd.neo4j.test;

import static org.junit.Assert.*;
import lapd.neo4j.GraphDbMappingException;

import org.eclipse.imp.pdb.facts.IMap;
import org.eclipse.imp.pdb.facts.IMapWriter;
import org.junit.Test;

public class TestMap extends BaseGraphDbTest {

	@Test
	public void testInsertMap() throws GraphDbMappingException {
		IMapWriter mapWriter = valueFactory.mapWriter();
		mapWriter.put(valueFactory.integer(1), valueFactory.string("one"));
		mapWriter.put(valueFactory.integer(2), valueFactory.string("two"));
		IMap mapValue = mapWriter.done();
		graphDbValueIO.write(id, mapValue);
		assertEquals(mapValue, graphDbValueIO.read(id, mapValue));
	}

}
