package metifikys.utils.DataBase.stream.api;

import javaslang.control.Try;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Metifikys on 2016-10-21.
 */
public class SelectGetter
{
    private static final Logger LOGGER =
                LogManager.getLogger(new Object(){}.getClass().getEnclosingClass().getName());

    private ResultSet rs;
    private int size;

    private List<String> columnNames = new ArrayList<>();

    public SelectGetter(ResultSet rs)
    {
        this.rs = rs;

        try
        {
            ResultSetMetaData metaData = rs.getMetaData();
            size = metaData.getColumnCount();

            for (int i = 1; i <= size; i++)
                columnNames.add(metaData.getColumnName(i));
        }
        catch (SQLException e)
        {
            LOGGER.error(e);
        }
    }

    @Override
    public String toString()
    {
        JSONObject jo = new JSONObject();

        columnNames.forEach(
                name -> jo.put(name, ofObject(name))
        );

        return jo.toString();
    }

    public int getSize() { return size; }


    // terrible ocean
    public Object ofObject(int index)   { return Try.of(() -> rs.getObject(index)).getOrElse(new Object()); }
    public Object ofObject(String name) { return Try.of(() -> rs.getObject(name)) .getOrElse(new Object()); }

    public long ofLong(String name)     { return Try.of(() -> rs.getLong(name))   .getOrElse(0L); }
    public long ofLong(int index)       { return Try.of(() -> rs.getLong(index))  .getOrElse(0L); }

    public int ofInt(String name)       { return Try.of(() -> rs.getInt(name))    .getOrElse(0); }
    public int ofInt(int index)         { return Try.of(() -> rs.getInt(index))   .getOrElse(0); }

    public double ofDouble(String name) { return Try.of(() -> rs.getDouble(name))  .getOrElse(0.0); }
    public double ofDouble(int index)   { return Try.of(() -> rs.getDouble(index)) .getOrElse(0.0); }

    public String ofString(String name) { return Try.of(() -> rs.getString(name))  .getOrElse(""); }
    public String ofString(int index)   { return Try.of(() -> rs.getString(index)) .getOrElse(""); }
}