package metifikys.utils.DataBase.Connections;

import metifikys.utils.DataBase.PolledDb;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

/**
 * base class that stores a Connection
 * Created by Metifikys on 2016-10-22.
 */
public abstract class ConnectionBase
{
    protected Connection con;

    public ConnectionBase(Connection con)
    {
        this.con = con;
    }

    /**
     *  Performs inspection, starts a operation with a base
     *
     * @return flag ok or error
     */
    public final boolean runProcess()
    {
        boolean out = true;
        Objects.requireNonNull(con, "connection cannot be null");

        try
        {
            dataBaseCall();
        }
        catch (SQLException e)
        {
            PolledDb.logSqlExp(e);
            PolledDb.closeConn(con);
            out = false;
        }

        return out;
    }

    protected abstract void dataBaseCall() throws SQLException;
}