package metifikys.utils.DataBase;

import metifikys.utils.DataBase.orm.Cell;
import org.junit.Test;

import java.io.File;
import java.net.URL;

import static org.junit.Assert.*;

/**
 * Created by Metifikys on 2016-10-23.
 */
public class PolledDbTest
{

    @Test
    public void testDoSelect() throws Exception
    {
        PolledDb.doSelect("test", "select * from test.test" )
                .forEach( System.out::println );
    }

    @Test
    public void testAll() throws Exception
    {
        int testCount = 10;

        PolledDb.doDataProcess("test",
                (connection, statement) ->
                        statement.execute("create table test.do_data_process_test (name text)")
        );


        PolledDb.doDataProcessPrepareSt("test", "insert into test.do_data_process_test(name) values (?)",
                (connection, statement) ->
                {
                    for (int i = 0; i < testCount; i++)
                    {
                        statement.setString(1, "name " + i);
                        statement.addBatch();
                    }
                }
        );


        boolean[] isOk = {false};
        PolledDb.doSelect("test", "select count(*) from test.do_data_process_test",
                resultSet ->
                {
                    if (resultSet.next())
                        isOk[0] = resultSet.getLong(1) == testCount;
                }
        );

        assertTrue(isOk[0]);


        PolledDb.doDataProcess("test",
                (connection, statement) ->
                        statement.execute("drop table test.do_data_process_test")
        );
    }

    @Test
    public void testDoSelect1() throws Exception
    {
        PolledDb.doSelect("test", "select * from test.test", DoSelectClass.class)
                .forEach( DoSelectClass::print );
    }

    private static class DoSelectClass
    {
        @Cell("name")
        String name;

        @Cell("id")
        Integer id;

        public void print() { System.out.println(name + "\t" + id); }
    }
}