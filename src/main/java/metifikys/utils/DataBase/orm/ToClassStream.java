package metifikys.utils.DataBase.orm;

import javaslang.control.Try;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.Spliterators.spliteratorUnknownSize;

/**
 * Created by Metifikys on 2016-10-27.
 */
public class ToClassStream<T> implements Closeable
{
    private static final Logger LOGGER =
                LogManager.getLogger(new Object(){}.getClass().getEnclosingClass().getName());

    private Connection con;
    private Class<T> aClass;

    private ToClassStream(Connection con, Class<T> aClass)
    {
        Objects.requireNonNull(con, "connection cannot be null");
        Objects.requireNonNull(aClass, "class cannot be null");
        this.con = con;
        this.aClass = aClass;
    }

    public static <T> ToClassStream<T> of(Connection con, Class<T> aClass) {return new ToClassStream<T>(con, aClass);}


    public Stream<T> select(String sql) throws SQLException
    {

        Objects.requireNonNull(sql, "sql cannot be null");

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(sql);

        return StreamSupport
                .stream(spliteratorUnknownSize(new ResultSetToCell<T>(rs, con, st, aClass), 0), false);
    }


    @Override
    public void close() throws IOException
    {
        Try.run(con::close)
                .onFailure(LOGGER::error);
    }
}