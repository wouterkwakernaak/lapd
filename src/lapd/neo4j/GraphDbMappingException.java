package lapd.neo4j;

import lapd.DbMappingException;


public class GraphDbMappingException extends DbMappingException {
	
	private static final long serialVersionUID = 3918081694038663717L;
	
	public GraphDbMappingException(String message) {
		super(message);
	}	
	
}
