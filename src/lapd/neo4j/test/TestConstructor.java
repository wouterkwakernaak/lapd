package lapd.neo4j.test;

import static org.junit.Assert.*;

import java.io.IOException;

import lapd.neo4j.GraphDbMappingException;

import org.eclipse.imp.pdb.facts.IValue;
import org.eclipse.imp.pdb.facts.type.Type;
import org.eclipse.imp.pdb.facts.type.TypeFactory;
import org.eclipse.imp.pdb.facts.type.TypeStore;
import org.junit.Before;
import org.junit.Test;

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
	public void testNullaryConstructor() throws GraphDbMappingException {
		Type adt = typeFactory.abstractDataType(typeStore, "SomeADT");
		Type constructorType = typeFactory.constructor(typeStore, adt, "someConstructor");
		IValue constructorValue = valueFactory.constructor(constructorType);
		graphDbValueIO.write(id, constructorValue);
		assertEquals(constructorValue, graphDbValueIO.read(id, constructorType));
	}
	
	@Test
	public void testConstructor() throws GraphDbMappingException {
		Type adt = typeFactory.abstractDataType(typeStore, "SomeADT");
		Type constructorType = typeFactory.constructor(typeStore, adt, "someConstructor", 
			typeFactory.boolType(), typeFactory.integerType());
		IValue constructorValue = valueFactory.constructor(constructorType, valueFactory.bool(true), valueFactory.integer(5));
		graphDbValueIO.write(id, constructorValue);
		assertEquals(constructorValue, graphDbValueIO.read(id, constructorType));
	}

}
