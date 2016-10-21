package metifikys.utils.DataBase;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/** Working with the database using the Statement
 * Created by Metifikys on 2016-07-22.
 */
@FunctionalInterface
public interface ProcessData
{
    /**
     * The base class handles the connection and statement
     *
     * @param connection Connect to the database
     * @param statement statement database
     *
     * @throws SQLException
     */
    void doProcess(Connection connection, Statement statement) throws SQLException;
}