package metifikys.utils.DataBase.stream.api;

import javaslang.control.Try;
import metifikys.utils.DataBase.PolledDb;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;

import static java.util.Optional.ofNullable;

/**
 * base class for iterate ResultSet
 * Created by Metifikys on 2016-10-27.
 */
public abstract class ResultSetIterator<T> implements Iterator<T>, Closeable
{
    private static final Logger LOGGER =
            LogManager.getLogger(new Object(){}.getClass().getEnclosingClass().getName());

    private Connection con;
    private Statement st;
    protected ResultSet rs;

    public ResultSetIterator(ResultSet rs, Connection con, Statement st)
    {
        this.rs = rs;
        this.con = con;
        this.st = st;
    }

    @Override
    public boolean hasNext()
    {
        boolean out = false;

        try
        {
            if (!rs.next())
                Try.run(this::close);
            else
                out = true;
        }
        catch (SQLException e)
        {
            LOGGER.error(e);
            Try.run(this::close);
        }

        return out;
    }

    @Override
    public void close() throws IOException
    {
        ofNullable(rs)
                .ifPresent(rst -> Try.run(rst::close));

        ofNullable(st)
                .ifPresent(stm -> Try.run(stm::close));

        PolledDb.closeConn(con);
        LOGGER.debug("is close");
    }
}