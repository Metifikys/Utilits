package metifikys.utils.DataBase.stream.api;

import metifikys.utils.DataBase.PolledDb;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Metifikys on 2016-10-23.
 */
public class StreamFomSelectTest
{

    @Test
    public void testSelect() throws Exception
    {
        StreamFomSelect.of(PolledDb.getConnection("test"))
                .select("select * from test.test")
                .forEach(
                        sg ->
                                System.out.println(sg.ofInt("id") + "\t" + sg.ofString("name"))
                );
    }

    @Test
    public void testAllParams() throws Exception
    {
        StreamFomSelect.of(PolledDb.getConnection("test"))
                .select("select * from test.\"StreamFomSelect\"")
                .forEach(
                        sg ->
                        {
                            System.out.println(sg.ofInt("id")
                                    + "\t" + sg.ofString("name")
                                    + "\t" + sg.ofDouble("doubleVal")
                                    + "\t" + sg.ofLong("longVal")
                            );

                            System.out.println(sg.ofInt(1)
                                    + "\t" + sg.ofString(2)
                                    + "\t" + sg.ofDouble(3)
                                    + "\t" + sg.ofLong(4)
                            );
                        }
                );
    }

    @Test(expected = NullPointerException.class)
    public void testOfNull() throws Exception
    {
        StreamFomSelect.of(null);
    }
}