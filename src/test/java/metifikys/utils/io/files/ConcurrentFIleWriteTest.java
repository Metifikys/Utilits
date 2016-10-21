package metifikys.utils.io.files;

import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 *
 * Created by Metifikys on 19.10.2016.
 */
public class ConcurrentFileWriteTest
{

    @Test
    public void testAdd() throws Exception
    {
        File f = new File("OutfileName");

        ConcurrentFileWrite out = new ConcurrentFileWrite(f);

        out.add("some line");

        out.close();

        if (f.exists())
            assertTrue(f.delete());
    }


    @Test
    public void testAdd2() throws Exception
    {
        try(ConcurrentFileWrite out = new ConcurrentFileWrite("OutfileName2"))
        {
            out.add("some line");
        }

        assertTrue(new File("OutfileName2").delete());
    }

    @Test
    public void testConcurrent() throws Exception
    {
        final int activeThreads = 50;

        final int threads = 100;
        final int linesInThreadCount = 50;
        final int simpleBufferSize = 2_000;

        ExecutorService executorService = Executors.newFixedThreadPool(activeThreads);

        File outFile = new File("someName.dat");

        ConcurrentFileWrite out = new ConcurrentFileWrite(outFile, simpleBufferSize);

        for (int i = 0; i < threads; i++)
        {
            executorService.execute(
                () ->
                {
                    for (int j = 0; j < linesInThreadCount; j++)
                        out.add(Thread.currentThread().getName() + "\t" + j);
                }
            );
        }

        executorService.shutdown();
        executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        out.close();

        long count = Files.lines(outFile.toPath()).count();

        assertEquals(count, threads * linesInThreadCount);
        assertTrue(outFile.delete());
    }
}