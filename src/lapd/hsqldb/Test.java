package lapd.hsqldb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Test {
	
	public static void main(String[] args) {
		try {
			Connection connection = DriverManager.getConnection("jdbc:hsqldb:file:/ufs/wouterk/databases/hsqldb/hsqltestdb");
			Statement statement = connection.createStatement();
			String query = "create table sometable(somefield1 INTEGER, somefield2 VARCHAR(50));";
			statement.executeUpdate(query);
			statement.close();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
