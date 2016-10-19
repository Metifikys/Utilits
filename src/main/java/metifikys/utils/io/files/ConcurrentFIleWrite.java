package metifikys.utils.io.files;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Metifikys on 19.10.2016.
 */
public class ConcurrentFIleWrite implements Closeable
{
    private static final Logger LOGGER =
            LogManager.getLogger(new Object(){}.getClass().getEnclosingClass().getName());

    private final List<String> data = Collections.synchronizedList(new ArrayList<>());
    private int buffSize;
    private BufferedWriter out;

    /**
     * @param fileName Output File Name
     */
    public ConcurrentFIleWrite(String fileName)
    {
        this(fileName, 1000);
    }

    /**
     * @param fileName Output File Name
     * @param buffSize number of lines in a buffer
     */
    public ConcurrentFIleWrite(String fileName, int buffSize)
    {
        this.buffSize = buffSize;

        try
        {
            out = new BufferedWriter(new FileWriter(fileName));
        }
        catch (IOException e)
        {
            LOGGER.error(e);
        }
    }

    /**
     * @param line line to add
     */
    public void add(String line)
    {
        data.add(line);

        if (buffSize <= data.size())
            writeAllData();
    }

    /**
     * output of the current buffer to the file
     */
    private void writeAllData()
    {
        synchronized(data)
        {
            for (String s : data)
            {
                try
                {
                    out.write(s + "\n");
                }
                catch (IOException e)
                {
                    LOGGER.error(e);
                }
            }

            data.clear();
        }
    }

    /**
     * Close output
     */
    @Override
    public void close()
    {
        try
        {
            writeAllData();
            out.close();
        }
        catch (IOException e)
        {
            LOGGER.error(e);
        }
    }
}