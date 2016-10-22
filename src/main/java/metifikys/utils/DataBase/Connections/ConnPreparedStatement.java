package metifikys.utils.DataBase.Connections;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by Metifikys on 2016-10-22.
 */
public class ConnPreparedStatement extends ConnectionBase
{
    private PreparedStatementProcessor pd;
    private String sql;

    private ConnPreparedStatement(Connection con, String sql, PreparedStatementProcessor pd)
    {
        super(con);
        this.pd = pd;
        this.sql = sql;
    }

    public static ConnPreparedStatement of(Connection con, String sql, PreparedStatementProcessor pd)
    {
        return new ConnPreparedStatement(con, sql, pd);
    }


    /**
     * create prepare statement and sends it with a connection of the processor
     *
     * @throws SQLException
     */
    @Override
    protected void dataBaseCall() throws SQLException
    {
        PreparedStatement st = con.prepareStatement(sql);

        pd.doProcess(con, st);

        st.executeBatch();
        st.close();
    }

    /**
     * Working with the database using the PreparedStatement
     * Created by Metifikys on 2016-07-22.
     */
    @FunctionalInterface
    public interface PreparedStatementProcessor
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
}