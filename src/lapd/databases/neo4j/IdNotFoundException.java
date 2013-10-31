package lapd.databases.neo4j;

public class IdNotFoundException extends GraphDbMappingException {

	private static final long serialVersionUID = 5754671575748292097L;

	public IdNotFoundException(String message) {
		super(message);
	}

}
