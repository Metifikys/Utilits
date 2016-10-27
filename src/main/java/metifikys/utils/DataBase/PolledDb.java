package metifikys.utils.DataBase;

import javaslang.control.Try;
import metifikys.utils.DataBase.Connections.ConnPreparedStatement;
import metifikys.utils.DataBase.Connections.ConnPreparedStatement.PreparedStatementProcessor;
import metifikys.utils.DataBase.Connections.ConnResultSet;
import metifikys.utils.DataBase.Connections.ConnResultSet.ResultSetProcessor;
import metifikys.utils.DataBase.Connections.ConnStatement;
import metifikys.utils.DataBase.Connections.ConnStatement.StatementProcessor;
import metifikys.utils.DataBase.initializer.DbInitializerProperties;
import metifikys.utils.DataBase.orm.ToClassStream;
import metifikys.utils.DataBase.stream.api.SelectGetter;
import metifikys.utils.DataBase.stream.api.StreamFomSelect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tomcat.jdbc.pool.DataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.Optional.ofNullable;

/**
 * Class for processing requests in a pool of connections
 * Created by Metifikys on 2016-07-15.
 */
public final class PolledDb
{
    private static final Logger LOGGER =
                LogManager.getLogger(new Object(){}.getClass().getEnclosingClass().getName());

    private static final Map<String, DataSource> DATA_SOURCE_MAP = DbInitializerProperties.init();

    private PolledDb() {}

    /**
     * Controls Connection and Statement
     *
     * @param name The database server name in the configuration file
     * @param pd The processor that will handle it
     */
    public static void doDataProcess(String name, StatementProcessor pd)
    {
        try
        {
            ConnStatement
                    .of(getConnection(name), pd)
                    .runProcess();
        }
        catch (SQLException e)
        {
            logSqlExp(e);
        }
    }

    /**
     * Controls Connection and PreparedStatement
     *
     * @param name The database server name in the configuration file
     * @param sql sql for PreparedStatement
     * @param pd The processor that will handle it
     */
    public static boolean doDataProcessPrepareSt(String name, String sql, PreparedStatementProcessor pd)
    {
        boolean state;

        try
        {
            state = ConnPreparedStatement
                    .of( getConnection(name), sql, pd )
                    .runProcess();
        }
        catch (SQLException e)
        {
            logSqlExp(e);
            state = false;
        }

        return state;
    }

    /**
     * Controls Connection and PreparedStatement
     *
     * @param name The database server name in the configuration file
     * @param sql sql for select data
     * @param pd The processor that will handle it
     */
    public static void doSelect(String name, String sql, ResultSetProcessor pd)
    {
        try
        {
            ConnResultSet
                    .of(getConnection(name), sql, pd)
                    .runProcess();
        }
        catch (SQLException e)
        {
            logSqlExp(e);
        }
    }


    /**
     * @param name The database server name in the configuration file
     * @param sql sql for select data
     *
     * @return Stream of SelectGetter
     *
     * @see metifikys.utils.DataBase.stream.api.SelectGetter
     */
    public static Stream<SelectGetter> doSelect(String name, String sql)
    {
        Stream<SelectGetter> stream;

        Connection con = null;
        try
        {
            con = getConnection(name);
            if (con == null)
            {
                LOGGER.error("cannot find connect to {}", name);
                return Stream.empty();
            }

            stream = StreamFomSelect.of(con)
                    .select(sql);
        }
        catch (SQLException e)
        {
            logSqlExp(e);
            closeConn(con);
            stream = Stream.empty();
        }

        return stream;
    }

    /**
     * @param name The database server name in the configuration file
     * @param sql sql for select data
     *
     * @return Stream of SelectGetter
     *
     * @see metifikys.utils.DataBase.stream.api.SelectGetter
     */
    public static <T> Stream<T> doSelect(String name, String sql, Class<T> aClass)
    {
        Stream<T> stream;

        Connection con = null;
        try
        {
            con = getConnection(name);
            if (con == null)
            {
                LOGGER.error("cannot find connect to {}", name);
                return Stream.empty();
            }

            stream =ToClassStream.of(con, aClass)
                    .select(sql);
        }
        catch (SQLException e)
        {
            logSqlExp(e);
            closeConn(con);
            stream = Stream.empty();
        }

        return stream;
    }


    public static Connection getConnection(String name) throws SQLException
    {
        return DATA_SOURCE_MAP.get(name).getConnection();
    }

    /**
     * log all exception
     *
     * @param e - Exception
     */
    public static void logSqlExp(SQLException e)
    {
        LOGGER.error(e);
        SQLException nextException = e.getNextException();
        while (nextException != null)
        {
            LOGGER.error(nextException);
            nextException = nextException.getNextException();
        }
    }


    /**
     * Try to close the connection
     * @param con коннект для закрытия
     */
    public static void closeConn(Connection con)
    {
        ofNullable(con)
                .ifPresent(cn -> Try.run(cn::close));
    }

    /**
     * Trying to close all connection pools
     */
    public static void closeAll() { DATA_SOURCE_MAP.values().forEach(DataSource::close); }
}