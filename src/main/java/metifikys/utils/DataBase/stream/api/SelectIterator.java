package metifikys.utils.DataBase.stream.api;

import javaslang.control.Try;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import metifikys.utils.DataBase.PolledDb;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;

import static java.util.Optional.ofNullable;

/**
 * Created by Metifikys on 2016-10-21.
 */
public class SelectIterator implements Iterator<SelectGetter>, Closeable
{
    private static final Logger LOGGER =
                LogManager.getLogger(new Object(){}.getClass().getEnclosingClass().getName());

    private ResultSet rs;
    private Connection con;
    private Statement st;
    private SelectGetter getter;

    public SelectIterator(ResultSet rs, Connection con, Statement st)
    {
        this.rs = rs;
        this.con = con;
        this.st = st;

        getter = new SelectGetter(rs);
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
    public SelectGetter next() { return getter; }

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