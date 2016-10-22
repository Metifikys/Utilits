package metifikys.utils.DataBase.Connections;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by Metifikys on 2016-10-22.
 */
public class ConnStatement extends ConnectionBase
{
    private StatementProcessor pd;

    private ConnStatement(Connection con, StatementProcessor pd)
    {
        super(con);
        this.pd = pd;
    }

    public static ConnStatement of(Connection con, StatementProcessor pd)
    {
        return new ConnStatement(con, pd);
    }


    /**
     * create statement and sends it with a connection of the processor
     *
     * @throws SQLException
     */
    @Override
    protected void dataBaseCall() throws SQLException
    {
        Statement st = con.createStatement();

        pd.doProcess(con, st);

        st.close();
    }

    /**
     * Working with the database using the Statement
     * Created by Metifikys on 2016-07-22.
     */
    @FunctionalInterface
    public interface StatementProcessor
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
}