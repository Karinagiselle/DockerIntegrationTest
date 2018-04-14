import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.AfterClass;
import org.junit.BeforeClass;

public abstract class DbConfig {

    private static final String HOSTNAME = "127.0.0.1";
    private static final String PORT = "1521";
    private static final String SERVICE_NAME = "xe";
    private static final String DB_URL = "jdbc:oracle:thin:@" + HOSTNAME + ":" + PORT + "/" + SERVICE_NAME;
    private static final String USERNAME = "system";
    private static final String PASSWORD = "password";
    private static final String DB_CREATE_DDL_FILE = "src/test/resources/createTables.sql";
    private static final String DB_DROP_DDL_FILE = "src/test/resources/dropTables.sql";
    private static final String DB_INSERT_DDL_FILE = "src/test/resources/insertTables.csv";

    protected static Connection CONNECTION;
    private static final String JDBC_DRIVER = "oracle.jdbc.driver.OracleDriver";

    @BeforeClass
    public static void setUpClass() throws IOException, SQLException, ClassNotFoundException {
        CONNECTION = createConnection();
        runDDL(new File(DB_CREATE_DDL_FILE));
    }

    @AfterClass
    public static void tearDownClass() throws SQLException, IOException {
        runDDL(new File(DB_DROP_DDL_FILE));
        closeConnection();
    }

    static void populateDbTable() throws IOException, SQLException {
        final BufferedReader reader = new BufferedReader(new FileReader(DB_INSERT_DDL_FILE));
        final Statement stmt = CONNECTION.createStatement();
        String line;
        while ((line = reader.readLine()) != null) {
            line = "INSERT INTO PET" + " VALUES (" + line + ")";
            stmt.executeUpdate(line);
        }
        reader.close();
    }

    static ResultSet getAllPets() throws SQLException {
        final Statement stmt = CONNECTION.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM PET");

        return rs;
    }

    private static Connection createConnection() throws ClassNotFoundException, SQLException {
        Class.forName(JDBC_DRIVER);
        Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
        return connection;
    }

    private static void runDDL(File ddlFile) throws IOException {
        String line;
        StringBuilder sqlSb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(ddlFile))) {
            while ((line = reader.readLine()) != null) {
                sqlSb.append(line + "\n ");
                if (line.isEmpty()) {
                    try {
                        Statement stmt = CONNECTION.createStatement();
                        stmt.execute(sqlSb.toString());
                        stmt.close();
                    } catch (SQLException e) {
                        throw new RuntimeException(sqlSb.toString() + e);
                    }
                    sqlSb.setLength(0);
                }
            }
        }
    }

    private static void closeConnection() throws SQLException {
        if (CONNECTION != null) {
            CONNECTION.close();
        }
    }
}
