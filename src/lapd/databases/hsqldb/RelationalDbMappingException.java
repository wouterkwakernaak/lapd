package lapd.databases.hsqldb;

import lapd.databases.DbMappingException;

public class RelationalDbMappingException extends DbMappingException {	

	private static final long serialVersionUID = -1723314515784549162L;

	public RelationalDbMappingException(String message) {
		super(message);
	}

}
