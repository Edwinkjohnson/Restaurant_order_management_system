package DB;
import java.sql.*;
import java.sql.DriverManager;
public class DBconnection {
	private static final String URL = "jdbc:mysql://localhost:3306/restaurant";
	private static final String USER = "root";
	private static final String PASSWORD = "root";
	
	public static Connection getConnection() throws SQLException {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		}catch  (ClassNotFoundException e){
			e.printStackTrace();
		}
	    return DriverManager.getConnection(URL,USER,PASSWORD);
	}
}
