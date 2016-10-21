package metifikys.utils.DataBase;

import javaslang.control.Try;
import metifikys.utils.DataBase.initializer.DbInitializerProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tomcat.jdbc.pool.DataSource;
import metifikys.utils.DataBase.stream.api.SelectGetter;
import metifikys.utils.DataBase.stream.api.SelectIterator;

import java.sql.*;
import java.util.Map;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.Optional.ofNullable;
import static java.util.Spliterators.*;

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
    public static void doDataProcess(String name, ProcessData pd)
    {
        Connection con = null;
        try
        {
            con = DATA_SOURCE_MAP.get(name).getConnection();
            if (con == null)
            {
                LOGGER.error("cannot find connect to {}", name);
                return;
            }
            Statement st = con.createStatement();

            pd.doProcess(con, st);

            st.close();
        }
        catch (SQLException e)
        {
            logSqlExp(e);
        }
        finally
        {
            closeConn(con);
        }
    }

    /**
     * Controls Connection and PreparedStatement
     *
     * @param name The database server name in the configuration file
     * @param sql sql for PreparedStatement
     * @param pd The processor that will handle it
     */
    public static boolean doDataProcessPrepareSt(String name, String sql, ProcessDataPrSt pd)
    {
        boolean state = true;

        Connection con = null;
        try
        {
            con = DATA_SOURCE_MAP.get(name).getConnection();
            if (con == null)
            {
                LOGGER.error("cannot find connect to {}", name);
                return false;
            }

            PreparedStatement st = con.prepareStatement(sql);

            pd.doProcess(con, st);

            st.executeBatch();
            st.close();
        }
        catch (SQLException e)
        {
            logSqlExp(e);

            state = false;
        }
        finally
        {
            closeConn(con);
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
    public static void doSelect(String name, String sql, ProcessDataSelect pd)
    {
        Connection con = null;
        try
        {
            con = DATA_SOURCE_MAP.get(name).getConnection();
            if (con == null)
            {
                LOGGER.error("cannot find connect to {}", name);
                return;
            }
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);

            pd.doProcess(rs);

            rs.close();
            st.close();
        }
        catch (SQLException e)
        {
            logSqlExp(e);
        }
        finally
        {
            closeConn(con);
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
            con = DATA_SOURCE_MAP.get(name).getConnection();
            if (con == null)
            {
                LOGGER.error("cannot find connect to {}", name);
                return Stream.empty();
            }
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);

            stream = StreamSupport
                    .stream(spliteratorUnknownSize(new SelectIterator(rs, con, st), 0), false);
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
     * log all exception
     *
     * @param e - Exception
     */
    private static void logSqlExp(SQLException e)
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