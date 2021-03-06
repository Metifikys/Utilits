package metifikys.utils.DataBase.orm;

import org.junit.Test;
import org.mockito.Mockito;

import java.sql.ResultSet;
import java.util.Arrays;

/**
 * Created by Metifikys on 2016-10-24.
 */
public class ConstructorCellTest
{
    @Test
    public void testCreate() throws Exception
    {
        ConstructorCell<ForTest> constructorCell =
                ConstructorCell.of(ForTest.class);

        ResultSet resultSet = Mockito.mock(ResultSet.class);
        Mockito.when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false);

        Mockito.when(resultSet.getObject("id")).thenReturn(1).thenReturn(2).thenReturn(3);
        Mockito.when(resultSet.getObject("name")).thenReturn("name1").thenReturn("name2").thenReturn("name2");

        while (resultSet.next())
            constructorCell
                    .create(resultSet)
                    .method();
    }

    static class ForTest
    {
        @Cell(value = "id", type = Cell.Type.INTEGER)
        Integer id;

        @Cell(value = "name", type = Cell.Type.STRING)
        String name;


        public void method()
        {
            System.out.println("I'm " + name  + ". My id is " + id);
        }
    }
}