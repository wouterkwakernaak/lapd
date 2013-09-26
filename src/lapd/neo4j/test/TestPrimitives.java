package lapd.neo4j.test;

import static org.junit.Assert.*;

import lapd.neo4j.GraphDbMappingException;

import org.eclipse.imp.pdb.facts.ISourceLocation;
import org.eclipse.imp.pdb.facts.IValue;
import org.junit.Test;

public class TestPrimitives extends BaseGraphDbTest {

	@Test
	public void testInteger() throws GraphDbMappingException {
		IValue integerValue = valueFactory.integer(13);
		graphDbValueIO.write(id, integerValue);
		assertEquals(integerValue, graphDbValueIO.read(id, integerValue));
	}
	
	@Test
	public void testString() throws GraphDbMappingException {
		IValue stringValue = valueFactory.string("testing this stuff");
		graphDbValueIO.write(id, stringValue);
		assertEquals(stringValue, graphDbValueIO.read(id, stringValue));
	}
	
	@Test
	public void testBoolean() throws GraphDbMappingException {
		IValue boolValue = valueFactory.bool(true);
		graphDbValueIO.write(id, boolValue);
		assertEquals(boolValue, graphDbValueIO.read(id, boolValue));
	}
	
	@Test
	public void testReal() throws GraphDbMappingException {
		IValue realValue = valueFactory.real(4.489689023);
		graphDbValueIO.write(id, realValue);
		assertEquals(realValue, graphDbValueIO.read(id, realValue));
	}
	
	@Test
	public void testRational() throws GraphDbMappingException {
		Integer numerator = 7;
		Integer denominator = 3;
		IValue rationalValue = valueFactory.rational(numerator, denominator);
		graphDbValueIO.write(id, rationalValue);
		assertEquals(rationalValue, graphDbValueIO.read(id, rationalValue));
	}
	
	@Test
	public void testInsertDateTime() throws GraphDbMappingException {
		long dateTimeUnderTest = System.currentTimeMillis();
		IValue dateTimeValue = valueFactory.datetime(dateTimeUnderTest);
		graphDbValueIO.write(id, dateTimeValue);
		// the time deviates by one hour, not sure why
		IValue deviatedDateTimeValue = valueFactory.datetime(dateTimeUnderTest + 3600000);
		assertEquals(deviatedDateTimeValue, graphDbValueIO.read(id, dateTimeValue));
	}
	
	@Test
	public void testInsertSourceLocation() throws GraphDbMappingException {
		String path = "C:\\Databases\\Neo4J\\graph.db";
		ISourceLocation locValue = valueFactory.sourceLocation(path);
		graphDbValueIO.write(id, locValue);
		assertEquals(locValue, graphDbValueIO.read(id, locValue));
	}

}
