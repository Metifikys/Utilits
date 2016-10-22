package metifikys.utils.DataBase.Connections;

import java.sql.*;

/**
 * Created by Metifikys on 2016-10-22.
 */
public class ConnResultSet extends ConnectionBase
{
    private ResultSetProcessor pd;
    private String sql;

    private ConnResultSet(Connection con, String sql, ResultSetProcessor pd)
    {
        super(con);
        this.pd = pd;
        this.sql = sql;
    }

    public static ConnResultSet of(Connection con, String sql, ResultSetProcessor pd)
    {
        return new ConnResultSet(con, sql, pd);
    }


    /**
     * create result set and sends it of the processor
     *
     * @throws SQLException
     */
    @Override
    protected void dataBaseCall() throws SQLException
    {
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(sql);

        pd.doProcess(rs);

        rs.close();
        st.close();
    }

    /**
     * Working with the database using the ResultSet
     * Created by Metifikys on 2016-07-22.
     */
    @FunctionalInterface
    public interface ResultSetProcessor
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
}