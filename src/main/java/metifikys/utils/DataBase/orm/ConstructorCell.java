package metifikys.utils.DataBase.orm;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
 * construct object-based annotations in the class
 * Created by Metifikys on 2016-10-24.
 */
public class ConstructorCell<T>
{
    private static final Logger LOGGER =
                LogManager.getLogger(new Object(){}.getClass().getEnclosingClass().getName());

    private Class<T> aClass;
    private Constructor<T> ctor;
    private Map<Cell, Field> fieldsMap = new HashMap<>();

    private ConstructorCell(Class<T> aClass)
    {
        this.aClass = aClass;
        init();
    }

    public static <T>ConstructorCell <T>of(Class<T> aClass) {return new ConstructorCell<>(aClass);}

    /**
     * find constructor without params <br>
     * find all fields with annotation Cell <br>
     *
     * @see metifikys.utils.DataBase.orm.Cell
     */
    private void init()
    {
        for (Constructor<?> constructor : aClass.getDeclaredConstructors())
        {
            ctor = (Constructor<T>)constructor;
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
        }
    }

    /**
     * @param rSet ResultSet
     *
     * @return instance of T
     *
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws InstantiationException
     * @throws SQLException
     */
    public T create(ResultSet rSet) throws IllegalAccessException, InvocationTargetException, InstantiationException, SQLException
    {
        requireNonNull(rSet);

        T c = ctor.newInstance();

        for (Map.Entry<Cell, Field> nameHolder : fieldsMap.entrySet())
        {
            Field field = nameHolder.getValue();
            Cell cell = nameHolder.getKey();

            field.set(c, rSet.getObject(cell.value()));
        }

        return c;
    }

    /**
     * log all error from create
     *
     * @param rSet ResultSet
     *
     * @return instance of T
     */
    public T createOrNull(ResultSet rSet)
    {
        T out = null;

        try
        {
            out = create(rSet);
        }
        catch (InstantiationException | IllegalAccessException | SQLException | InvocationTargetException e)
        {
            LOGGER.error(e);
        }

        return out;
    }
}