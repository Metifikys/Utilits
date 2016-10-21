package metifikys.utils.DataBase;

import java.sql.ResultSet;
import java.sql.SQLException;

/** Working with the database using the ResultSet
 * Created by Metifikys on 2016-07-22.
 */
@FunctionalInterface
public interface ProcessDataSelect
{
    /**
     * Get resultSet processing
     *
     * @param resultSet resultSet database
     *
     * @throws SQLException
     */
    void doProcess(ResultSet resultSet) throws SQLException;
}