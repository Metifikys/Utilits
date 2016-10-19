package metifikys.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

/**
 * simple class for measuring code execution
 * Created by Metifikys on 19.10.2016.
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

        try
        {
            task.action();
        }
        catch (Exception e)
        {
            LOGGER.error(e);
        }

        LOGGER.info("task: {}, finish elapsed time: {} (ms)", taskName, System.currentTimeMillis() - startTime);
    }

    // TODO: 19.10.2016 doAction rethrow exception
    // TODO: 19.10.2016 doAction with generic params

    @FunctionalInterface
    interface Task
    {
        void action() throws Exception;
    }
}