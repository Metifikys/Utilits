package metifikys.utils.DataBase.orm;

import org.junit.Test;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.Assert.*;

/**
 * Created by Metifikys on 2016-10-27.
 */
public class ToClassStreamTest
{
    private static final String SQL = "select num, dbVal, text from table";

    private Connection prepareConnection() throws SQLException
    {
        ResultSet resultSet = Mockito.mock(ResultSet.class);

        Mockito.when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false);
        Mockito.when(resultSet.getObject("num")).thenReturn(1).thenReturn(2).thenReturn(3);
        Mockito.when(resultSet.getObject("double")).thenReturn(5.1).thenReturn(5.2).thenReturn(5.3);
        Mockito.when(resultSet.getObject("text")).thenReturn("text").thenReturn("new text").thenReturn("some text");


        Statement statement = Mockito.mock(Statement.class);
        Mockito.when(statement.executeQuery(SQL)).thenReturn(resultSet);

        Connection con = Mockito.mock(Connection.class);

        Mockito.when(con.createStatement()).thenReturn(statement);

        return con;
    }


    @Test
    public void testSelect() throws Exception
    {
        ToClassStream
                .of(prepareConnection(), DataCell.class)
                .select(SQL)
                .forEach(
                        dataCell -> System.out.println(dataCell.formString())
                );
    }

    static private class DataCell
    {
        @Cell(name = "num")
        Integer num;

        @Cell(name = "dbVal")
        Double dbVal;

        @Cell(name = "text")
        String text;

        public String formString()
        {
            return num + "\t" + dbVal + "\t" + text;
        }
    }
}