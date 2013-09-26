package lapd;

import java.io.IOException;

import lapd.neo4j.GraphDbValueIO;

public class DbInstances {
	
	public final static GraphDbValueIO NEO4J;

    static {
        try { 
        	NEO4J = new GraphDbValueIO();
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

}
