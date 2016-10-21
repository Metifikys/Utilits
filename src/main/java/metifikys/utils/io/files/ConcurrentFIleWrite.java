package metifikys.utils.io.files;

import javaslang.control.Try;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Synchronized buffer with output to a file<br>
 *
 * Simple use
 * <pre>
 * <code>ConcurrentFileWrite out = new ConcurrentFileWrite("fileName");
 * out.add("some line");
 * out.close();
 * </code>
 * </pre>
 *
 * or try with resources
 * <pre>
 * <code>try(ConcurrentFileWrite out = new ConcurrentFileWrite("fileName"))
 * {
 *  out.add("some line");
 * }
 * </code>
 * </pre>
 * Created by Metifikys on 19.10.2016.
 */
public class ConcurrentFileWrite implements Closeable
{
    private static final Logger LOGGER =
            LogManager.getLogger(new Object(){}.getClass().getEnclosingClass().getName());

    private final List<String> data = Collections.synchronizedList(new ArrayList<>());
    private int buffSize;
    private BufferedWriter out;

    /**
     * @param fileName Output File Name
     */
    public ConcurrentFileWrite(String fileName) { this(fileName, 1000); }

    /**
     * @param file Output File
     */
    public ConcurrentFileWrite(File file) { this(file.getAbsolutePath(), 1000); }

    /**
     * @param file Output File
     * @param buffSize number of lines in a buffer
     */
    public ConcurrentFileWrite(File file,  int buffSize) { this(file.getAbsolutePath(), buffSize); }

    /**
     * @param fileName Output File Name
     * @param buffSize number of lines in a buffer
     */
    public ConcurrentFileWrite(String fileName, int buffSize)
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
     * output of the current buffer to the file <br>
     * can be caused by several times due to the fact that verification of the size of different streams
     */
    private void writeAllData()
    {
        synchronized(data)
        {
            data.forEach(this::writeLine);
            data.clear();
        }
    }

    /**
     * @param line output line
     */
    private void writeLine(String line)
    {
        Try.run(() -> out.write(line + "\n"))
                .onFailure(LOGGER::error);
    }

    /**
     * Close output
     */
    @Override
    public void close()
    {
        writeAllData();
        Optional.ofNullable(out)
                .ifPresent(outSt -> Try.run(outSt::close));
    }
}