package lapd.neo4j.test;

import static org.junit.Assert.*;

import java.io.IOException;

import org.eclipse.imp.pdb.facts.type.Type;
import org.eclipse.imp.pdb.facts.type.TypeFactory;
import org.eclipse.imp.pdb.facts.type.TypeStore;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.Transaction;

public class TestConstructor extends BaseGraphDbTest {

	private TypeStore typeStore;
	private TypeFactory typeFactory;;

	@Before
	public void setUp() throws IOException {
		super.setUp();
		typeStore = new TypeStore();
		typeFactory = TypeFactory.getInstance();
	}

	@Test
	public void testConstructor() {
		Transaction tx = graphDb.beginTx();
		try {
			Type adt = typeFactory.abstractDataType(typeStore, "SomeADT");
			Type constructorX = typeFactory.constructor(typeStore, adt, "someConstructorX");
			Type constructorY = typeFactory.constructor(typeStore, adt, "someConstructorY", 
					typeFactory.boolType(), typeFactory.integerType());
			valueFactory.constructor(constructorX).accept(graphDbValueInsertionVisitor);
			valueFactory.constructor(constructorY, valueFactory.bool(true), 
					valueFactory.datetime(System.currentTimeMillis())).accept(graphDbValueInsertionVisitor);
			tx.success();
		} 
		catch (Exception e) {
			fail(e.getMessage());
		}
		finally {
			tx.finish();
		}
		assertEquals(5, countNodes());
	}

}
