package metifikys.utils;

import org.junit.Test;

/**
 * Created by Metifikys on 19.10.2016.
 */
public class StopwatchTaskTest
{

    @Test
    public void testDoAction() throws Exception
    {
        StopwatchTask.doAction("test", () -> Thread.sleep(500) );
    }


    @Test(expected = NullPointerException.class)
    public void testDoActionNull() throws Exception
    {
        StopwatchTask.doAction("test", null );
    }

    @Test
    public void testDoActionInnerException() throws Exception
    {
        StopwatchTask.doAction("test", () -> {throw new Exception("test error");} );
    }
}