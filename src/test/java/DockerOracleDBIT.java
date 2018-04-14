import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.Test;

public class DockerOracleDBIT extends DbConfig {

    @Test
    public void initialTest() throws IOException, SQLException {
        populateDbTable();

        ResultSet allPets = getAllPets();

        while (allPets.next()) {
            System.out.println(allPets.getString("PET_ID") + " - " + allPets.getString("NAME"));
        }
    }
}
