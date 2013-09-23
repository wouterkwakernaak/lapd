package lapd.neo4j.test;

import static org.junit.Assert.*;
import lapd.neo4j.GraphDbMappingException;
import org.eclipse.imp.pdb.facts.IBool;
import org.eclipse.imp.pdb.facts.IList;
import org.eclipse.imp.pdb.facts.IListWriter;
import org.eclipse.imp.pdb.facts.type.TypeFactory;
import org.junit.Test;

public class TestList extends BaseGraphDbTest {

	@Test
	public void testBooleanList() throws GraphDbMappingException {
		IList booleanList = createBooleanList();
		graphDbValueIO.write(id, booleanList);
		assertEquals(booleanList, graphDbValueIO.read(id, booleanList));
	}
	
	@Test
	public void testEmptyList() throws GraphDbMappingException {
		IList emptyList = valueFactory.list(TypeFactory.getInstance().voidType());
		graphDbValueIO.write(id, emptyList);
		assertEquals(emptyList, graphDbValueIO.read(id, emptyList));
	}
	
	@Test
	public void testIntegerList() throws GraphDbMappingException {
		IList integerList = createIntegerList();
		graphDbValueIO.write(id, integerList);
		assertEquals(integerList, graphDbValueIO.read(id, integerList));
	}
	
	@Test
	public void testInsertListOfLists() throws GraphDbMappingException {
		IList integerList1 = createIntegerList();
		IList integerList2 = createIntegerList();
		IList listOfLists = valueFactory.list(integerList1, integerList2);
		graphDbValueIO.write(id, listOfLists);
		assertTrue(listOfLists.isEqual(graphDbValueIO.read(id, listOfLists)));
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
