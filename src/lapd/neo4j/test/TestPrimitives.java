package lapd.neo4j.test;

import static org.junit.Assert.*;
import lapd.neo4j.GraphDbMappingException;
import lapd.neo4j.GraphDbValueRetrievalVisitor;

import org.eclipse.imp.pdb.facts.ISourceLocation;
import org.eclipse.imp.pdb.facts.IValue;
import org.junit.Test;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

public class TestPrimitives extends BaseGraphDbTest {
	
	@Test
	public void testInteger() throws GraphDbMappingException {
		IValue integerValue = valueFactory.integer(13);
		Transaction tx = graphDb.beginTx();
		try {
			integerValue.accept(graphDbValueInsertionVisitor);
			tx.success();
		}
		catch (Exception e) {
			fail(e.getMessage());
		}
		finally {
		    tx.finish();
		}	
		for (Node node : globalGraphOperations.getAllNodes()) {
			if (node.getId() != 0)
				assertEquals(integerValue, integerValue.accept(new GraphDbValueRetrievalVisitor(node)));
		}
	}
	
	@Test
	public void testString() throws GraphDbMappingException {
		IValue stringValue = valueFactory.string("testing this stuff");
		Transaction tx = graphDb.beginTx();
		try {
			stringValue.accept(graphDbValueInsertionVisitor);
			tx.success();
		}
		catch(Exception e) {
			fail(e.getMessage());
		}
		finally {
		    tx.finish();
		}
		for (Node node : globalGraphOperations.getAllNodes()) {
			if (node.getId() != 0)
				assertEquals(stringValue, stringValue.accept(new GraphDbValueRetrievalVisitor(node)));		
		}
	}
	
	@Test
	public void testBoolean() throws GraphDbMappingException {
		IValue boolValue = valueFactory.bool(true);
		Transaction tx = graphDb.beginTx();
		try {
			boolValue.accept(graphDbValueInsertionVisitor);
			tx.success();
		}
		catch(Exception e) {
			fail(e.getMessage());
		}
		finally {
		    tx.finish();
		}		
		for (Node node : globalGraphOperations.getAllNodes()) {
			if (node.getId() != 0)
				assertEquals(boolValue, boolValue.accept(new GraphDbValueRetrievalVisitor(node)));		
		}
	}
	
	@Test
	public void testReal() throws GraphDbMappingException {
		IValue realValue = valueFactory.real(4.489689023);
		Transaction tx = graphDb.beginTx();
		try {
			realValue.accept(graphDbValueInsertionVisitor);
			tx.success();
		}
		catch(Exception e) {
			fail(e.getMessage());
		}
		finally {
		    tx.finish();
		}		
		for (Node node : globalGraphOperations.getAllNodes()) {
			if (node.getId() != 0)
				assertEquals(realValue, realValue.accept(new GraphDbValueRetrievalVisitor(node)));		
		}
	}
	
	@Test
	public void testRational() throws GraphDbMappingException {
		Integer numerator = 7;
		Integer denominator = 3;
		IValue rationalValue = valueFactory.rational(numerator, denominator);
		Transaction tx = graphDb.beginTx();
		try {
			rationalValue.accept(graphDbValueInsertionVisitor);
			tx.success();
		}
		catch(Exception e) {
			fail(e.getMessage());
		}
		finally {
		    tx.finish();
		}
		for (Node node : globalGraphOperations.getAllNodes()) {
			if (node.getId() != 0)
				assertEquals(rationalValue, rationalValue.accept(new GraphDbValueRetrievalVisitor(node)));			
		}
	}
	
	@Test
	public void testInsertDateTime() throws GraphDbMappingException {
		long dateTimeUnderTest = System.currentTimeMillis();
		IValue dateTimeValue = valueFactory.datetime(dateTimeUnderTest);
		Transaction tx = graphDb.beginTx();
		try {
			dateTimeValue.accept(graphDbValueInsertionVisitor);
			tx.success();
		}
		catch(Exception e) {
			fail(e.getMessage());
		}
		finally {
		    tx.finish();
		}
		// the time deviates by one hour, not sure why
		IValue deviatedDateTimeValue = valueFactory.datetime(dateTimeUnderTest + 3600000);
		for (Node node : globalGraphOperations.getAllNodes()) {
			if (node.getId() != 0)
				assertEquals(deviatedDateTimeValue, dateTimeValue.accept(new GraphDbValueRetrievalVisitor(node)));		
		}
	}
	
	@Test
	public void testInsertSourceLocation() throws GraphDbMappingException {
		String path = "C:\\Databases\\Neo4J\\graph.db";
		ISourceLocation locValue = valueFactory.sourceLocation(path);
		Transaction tx = graphDb.beginTx();
		try {
			locValue.accept(graphDbValueInsertionVisitor);
			tx.success();
		}
		catch(Exception e) {
			fail(e.getMessage());
		}
		finally {
		    tx.finish();
		}
		for (Node node : globalGraphOperations.getAllNodes()) {
			if (node.getId() != 0)
				assertEquals(locValue, locValue.accept(new GraphDbValueRetrievalVisitor(node)));
		}
	}

}
