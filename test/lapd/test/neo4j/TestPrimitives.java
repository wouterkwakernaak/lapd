package lapd.test.neo4j;

import static org.junit.Assert.*;
import lapd.databases.neo4j.GraphDbMappingException;

import org.eclipse.imp.pdb.facts.ISourceLocation;
import org.eclipse.imp.pdb.facts.IValue;
import org.junit.Test;

public class TestPrimitives extends BaseGraphDbTest {

	@Test
	public void testInteger() throws GraphDbMappingException {
		IValue integerValue = valueFactory.integer(13);
		graphDbValueIO.write(id, integerValue);
		assertEquals(integerValue, graphDbValueIO.read(id, integerValue.getType()));
	}
	
	@Test
	public void testString() throws GraphDbMappingException {
		IValue stringValue = valueFactory.string("testing this stuff");
		graphDbValueIO.write(id, stringValue);
		assertEquals(stringValue, graphDbValueIO.read(id, stringValue.getType()));
	}
	
	@Test
	public void testBoolean() throws GraphDbMappingException {
		IValue boolValue = valueFactory.bool(true);
		graphDbValueIO.write(id, boolValue);
		assertEquals(boolValue, graphDbValueIO.read(id, boolValue.getType()));
	}
	
	@Test
	public void testReal() throws GraphDbMappingException {
		IValue realValue = valueFactory.real(4.489689023);
		graphDbValueIO.write(id, realValue);
		assertEquals(realValue, graphDbValueIO.read(id, realValue.getType()));
	}
	
	@Test
	public void testRational() throws GraphDbMappingException {
		Integer numerator = 7;
		Integer denominator = 3;
		IValue rationalValue = valueFactory.rational(numerator, denominator);
		graphDbValueIO.write(id, rationalValue);
		assertEquals(rationalValue, graphDbValueIO.read(id, rationalValue.getType()));
	}
	
	@Test
	public void testInsertDateTime() throws GraphDbMappingException {
		long dateTimeUnderTest = System.currentTimeMillis();
		IValue dateTimeValue = valueFactory.datetime(dateTimeUnderTest);
		graphDbValueIO.write(id, dateTimeValue);
		assertEquals(dateTimeValue, graphDbValueIO.read(id, dateTimeValue.getType()));
	}
	
	@Test
	public void testInsertSourceLocation() throws GraphDbMappingException {
		String path = "C:\\Databases\\Neo4J\\graph.db";
		ISourceLocation locValue = valueFactory.sourceLocation(path);
		graphDbValueIO.write(id, locValue);
		assertEquals(locValue, graphDbValueIO.read(id, locValue.getType()));
	}

}
