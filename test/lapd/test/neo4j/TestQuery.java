package lapd.test.neo4j;

import static org.junit.Assert.*;
import lapd.databases.neo4j.GraphDbMappingException;

import org.eclipse.imp.pdb.facts.IValue;
import org.eclipse.imp.pdb.facts.type.TypeFactory;
import org.junit.Test;

public class TestQuery extends BaseGraphDbTest {

	@Test
	public void testSimpleQuery() throws GraphDbMappingException {
		IValue integerValue = valueFactory.integer(345);
		graphDbValueIO.write(id, integerValue);
		String query = "start n=node:nodes(id = '" + id + "') return n";
		assertEquals(integerValue, graphDbValueIO.executeQuery(query, TypeFactory.getInstance().integerType(), false));
	}

}
