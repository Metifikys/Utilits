package metifikys.utils.DataBase.stream.api;

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
 * Created by Metifikys on 2016-10-23.
 */
public class StreamFomSelect implements Closeable
{
    private static final Logger LOGGER =
                LogManager.getLogger(new Object(){}.getClass().getEnclosingClass().getName());

    private Connection con;

    private StreamFomSelect(Connection con)
    {
        Objects.requireNonNull(con, "connection cannot be null");
        this.con = con;
    }

    public static StreamFomSelect of(Connection con) {return new StreamFomSelect(con);}


    public Stream<SelectGetter> select(String sql) throws SQLException
    {
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(sql);

        return StreamSupport
                .stream(spliteratorUnknownSize(new SelectIterator(rs, con, st), 0), false);
    }


    @Override
    public void close() throws IOException
    {
        Try.run(con::close)
                .onFailure(LOGGER::error);
    }
}