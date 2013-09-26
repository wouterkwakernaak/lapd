package lapd;

import java.io.IOException;

import org.eclipse.imp.pdb.facts.impl.fast.ValueFactory;

import lapd.neo4j.GraphDbValueIO;

public class DbInstances {
	
	public final static GraphDbValueIO NEO4J;

    static {
        try { 
        	NEO4J = new GraphDbValueIO(ValueFactory.getInstance());
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

}
