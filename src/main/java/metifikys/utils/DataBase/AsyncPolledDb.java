package metifikys.utils.DataBase;

import metifikys.utils.DataBase.Connections.ConnPreparedStatement.PreparedStatementProcessor;
import metifikys.utils.DataBase.Connections.ConnStatement.StatementProcessor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * It works similarly PolledDb only makes requests on another thread
 * Created by Metifikys on 2016-09-12.
 *
 * @see metifikys.utils.DataBase.PolledDb
 */
public final class AsyncPolledDb
{
    private static final ExecutorService executorService = Executors.newCachedThreadPool();

    private AsyncPolledDb() {}

    /**
     * Controls Connection and Statement
     *
     * @param name The database server name in the configuration file
     * @param pd The processor that will handle it
     */
    public static void doDataProcess(String name, StatementProcessor pd)
    {
        executorService.execute(() -> PolledDb.doDataProcess(name, pd));
    }

    /**
     * Controls Connection and PreparedStatement
     *
     * @param name The database server name in the configuration file
     * @param sql sql for PreparedStatement
     * @param pd The processor that will handle it
     */
    public static void doDataProcessPrepareSt(String name, String sql, PreparedStatementProcessor pd)
    {
        executorService.execute(() -> PolledDb.doDataProcessPrepareSt(name, sql, pd));
    }


    public static void finalAll() { executorService.shutdown(); }

    public static void finalAndClose()
    {
        finalAll();
        PolledDb.closeAll();
    }
}