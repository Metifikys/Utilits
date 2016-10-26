package metifikys.utils.DataBase.orm;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

/**
 * Created by Metifikys on 2016-10-24.
 */
public class ConstructorCell
{
    private Class<?> aClass;
    private Constructor ctor;
    private Map<Cell, Field> fieldsMap = new HashMap<>();

    private ConstructorCell(Class<?> aClass)
    {
        this.aClass = aClass;
        init();
    }

    public static ConstructorCell of(Class<?> aClass) {return new ConstructorCell(aClass);}

    private void init()
    {
        for (Constructor<?> constructor : aClass.getDeclaredConstructors())
        {
            ctor = constructor;
            if (constructor.getGenericParameterTypes().length == 0)
                break;
        }

        requireNonNull(ctor, "no-argument constructor is not found");
        ctor.setAccessible(true);

        for (Field field : aClass.getDeclaredFields())
        {

            ofNullable(field.getAnnotation(Cell.class))
                    .ifPresent(cell ->
                    {
                        field.setAccessible(true);
                        fieldsMap.put(cell, field);
                    });

             /*switch (cell.type())
                {
                    case STRING:  field.set(c, "some string");
                        break;
                    case INTEGER: field.set(c, 1);
                        break;
                    case LONG:    field.set(c, 10);
                        break;
                    case DOUBLE:  field.set(c, 0.5);
                        break;
                    case OBJECT:  field.set(c, new Object());
                        break;
                }*/

        }
    }

    public <T> T create(ResultSet rSet) throws IllegalAccessException, InvocationTargetException, InstantiationException, SQLException
    {
        requireNonNull(rSet);

        T c = (T)ctor.newInstance();

        for (Map.Entry<Cell, Field> nameHolder : fieldsMap.entrySet())
        {
            Field field = nameHolder.getValue();
            Cell cell = nameHolder.getKey();

            field.set(c, rSet.getObject(cell.name()));
        }

        return c;
    }
}