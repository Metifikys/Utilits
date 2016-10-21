package metifikys.utils.DataBase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Working with the database using the PreparedStatement
 * Created by Metifikys on 2016-07-22.
 */
@FunctionalInterface
public interface ProcessDataPrSt
{
    /**
     * The base class handles the connection and statement
     *
     * @param connection Connect to the database
     * @param statement preparedStatement database
     *
     * @throws SQLException
     */
    void doProcess(Connection connection, PreparedStatement statement) throws SQLException;
}