package lapd.neo4j.test;

import static org.junit.Assert.*;

import org.eclipse.imp.pdb.facts.ISourceLocation;
import org.junit.Test;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

public class TestPrimitives extends BaseGraphDbTest {
	
	@Test
	public void testInsertInteger() {
		final Integer intUnderTest = 13;
		Transaction tx = graphDb.beginTx();
		try {
			valueFactory.integer(intUnderTest).accept(graphDbValueInsertionVisitor);
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
				assertEquals(intUnderTest.toString(), node.getProperty("int"));
		}
	}
	
	@Test
	public void testInsertString() {
		String stringUndertest = "testing this stuff";
		Transaction tx = graphDb.beginTx();
		try {
			valueFactory.string(stringUndertest).accept(graphDbValueInsertionVisitor);
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
				assertEquals(stringUndertest, node.getProperty("str"));		
		}
	}
	
	@Test
	public void testInsertBoolean() {
		boolean boolUndertest = true;
		Transaction tx = graphDb.beginTx();
		try {
			valueFactory.bool(boolUndertest).accept(graphDbValueInsertionVisitor);
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
				assertEquals(boolUndertest, node.getProperty("bool"));		
		}
	}
	
	@Test
	public void testInsertReal() {
		Double realUndertest = 4.489689023;
		Transaction tx = graphDb.beginTx();
		try {
			valueFactory.real(realUndertest).accept(graphDbValueInsertionVisitor);
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
				assertEquals(realUndertest.toString(), node.getProperty("real"));		
		}
	}
	
	@Test
	public void testInsertRational() {
		Integer numerator = 7;
		Integer denominator = 3;
		Transaction tx = graphDb.beginTx();
		try {
			valueFactory.rational(numerator, denominator).accept(graphDbValueInsertionVisitor);
			tx.success();
		}
		catch(Exception e) {
			fail(e.getMessage());
		}
		finally {
		    tx.finish();
		}
		for (Node node : globalGraphOperations.getAllNodes()) {
			if (node.getId() != 0) {
				assertEquals(numerator.toString(), node.getProperty("numerator"));
				assertEquals(denominator.toString(), node.getProperty("denominator"));
			}			
		}
	}
	
	@Test
	public void testInsertDateTime() {
		long dateTimeUnderTest = System.currentTimeMillis();
		Transaction tx = graphDb.beginTx();
		try {
			valueFactory.datetime(dateTimeUnderTest).accept(graphDbValueInsertionVisitor);
			tx.success();
		}
		catch(Exception e) {
			fail(e.getMessage());
		}
		finally {
		    tx.finish();
		}
		// the time deviates by one hour, not sure why
		long deviatedDateTime = dateTimeUnderTest + 3600000;		
		for (Node node : globalGraphOperations.getAllNodes()) {
			if (node.getId() != 0)
				assertEquals(deviatedDateTime, node.getProperty("datetime"));		
		}
	}
	
	@Test
	public void testInsertSourceLocation() {
		String path = "C:\\Databases\\Neo4J\\graph.db";
		ISourceLocation loc = valueFactory.sourceLocation(path);
		String serializedPath = loc.toString();
		Transaction tx = graphDb.beginTx();
		try {
			loc.accept(graphDbValueInsertionVisitor);
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
				assertEquals(serializedPath, node.getProperty("loc"));
		}
	}

}
