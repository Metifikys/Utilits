package metifikys.utils;

import javaslang.control.Try;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static java.util.Objects.requireNonNull;

/**
 * simple class for measuring code execution
 * Created by Metifikys on 2016-10-19
 */
public final class StopwatchTask
{
    private static final Logger LOGGER =
                LogManager.getLogger(new Object(){}.getClass().getEnclosingClass().getName());

    private StopwatchTask(){}

    /**
     * execute task, log time and error in Task.action
     *
     * @param taskName task name in log
     * @param task task to execute
     * @see metifikys.utils.StopwatchTask.Task
     */
    public static void doAction(String taskName, Task task)
    {
        requireNonNull(task, "task cannot be null");

        long startTime = System.currentTimeMillis();

        LOGGER.info("start {}", taskName);

        Try.run(task::action)
                .onFailure(LOGGER::error);

        LOGGER.info("task: {}, finish elapsed time: {} (ms)", taskName, System.currentTimeMillis() - startTime);
    }


    /**
     * execute task, log time and error in Task.action
     * rethrow error in Task.action
     *
     * @param taskName task name in log
     * @param task task to execute
     * @see metifikys.utils.StopwatchTask.Task
     */
    public static void doActionReThrow(String taskName, Task task) throws Exception
    {
        requireNonNull(task, "task cannot be null");

        long startTime = System.currentTimeMillis();

        LOGGER.info("start {}", taskName);

        try
        {
            task.action();
        }
        catch (Exception e)
        {
            LOGGER.info("task: {}, finish incorrect, elapsed time: {} (ms)", taskName, System.currentTimeMillis() - startTime);
            throw e;
        }

        LOGGER.info("task: {}, finish elapsed time: {} (ms)", taskName, System.currentTimeMillis() - startTime);
    }


    @FunctionalInterface
    interface Task
    {
        void action() throws Exception;
    }
}