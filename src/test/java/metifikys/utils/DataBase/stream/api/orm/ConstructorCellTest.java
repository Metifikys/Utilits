package metifikys.utils.DataBase.stream.api.orm;

import org.junit.Test;

import java.lang.reflect.Constructor;

import static org.junit.Assert.*;

/**
 * Created by Metifikys on 2016-10-24.
 */
public class ConstructorCellTest
{
    @Test
    public void testCreate() throws Exception
    {
        ForTest forTest = ConstructorCell
                .of(ForTest.class)
                .<ForTest>create();

        forTest.method();
    }


    static class ForTest
    {
        @Cell(name = "id", type = Cell.Type.INTEGER)
        Integer id;

        @Cell(name = "kek2", type = Cell.Type.STRING)
        String name;


        public void method()
        {
            System.out.println("I'm ForTest.method " + id  + " " + name);
        }
    }
}