package metifikys.utils.DataBase.orm;

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
 * Created by Metifikys on 2016-10-24.
 */
public class ToClassStream<T> implements Iterator<T>, Closeable
{
    private static final Logger LOGGER =
            LogManager.getLogger(new Object(){}.getClass().getEnclosingClass().getName());

    private ResultSet rs;
    private Connection con;
    private Statement st;

    ConstructorCell<T> constructorCell;

    public ToClassStream(ResultSet rs, Connection con, Statement st, Class<T> aClass)
    {
        this.rs = rs;
        this.con = con;
        this.st = st;

        constructorCell = ConstructorCell.of(aClass);
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
    public T next()
    {
        return constructorCell.createOrNull(rs);
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
