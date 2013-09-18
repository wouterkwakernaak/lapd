package lapd.neo4j.test;

import static org.junit.Assert.*;

import lapd.neo4j.GraphDbValueIO;
import lapd.neo4j.IGraphDbValueIO;

import org.eclipse.imp.pdb.facts.impl.reference.ValueFactory;
import org.junit.Test;

/* 	Uses an actual DB on the filesystem instead of the special non persistent testDB of neo4j.
	See config.properties for the location. */
public class TestAPIWithRealPersistentDb {
	
	@Test
	public void testWrite() {
		try {
			IGraphDbValueIO graphDbAPI = new GraphDbValueIO();
			graphDbAPI.write(ValueFactory.getInstance().integer(5));
		} catch (Exception e) {
			fail(e.getMessage());
		}	
	}

}
