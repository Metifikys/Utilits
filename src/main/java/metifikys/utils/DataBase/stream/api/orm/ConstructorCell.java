package metifikys.utils.DataBase.stream.api.orm;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import static java.util.Objects.requireNonNull;

/**
 * Created by Metifikys on 2016-10-24.
 */
public class ConstructorCell
{
    private Class<?> aClass;
    private Constructor ctor;

    private ConstructorCell(Class<?> aClass)
    {
        this.aClass = aClass;
    }

    public static ConstructorCell of(Class<?> aClass) {return new ConstructorCell(aClass);}

    public <T> T create() throws IllegalAccessException, InvocationTargetException, InstantiationException
    {
        for (Constructor<?> constructor : aClass.getDeclaredConstructors())
        {
            ctor = constructor;
            if (constructor.getGenericParameterTypes().length == 0)
                break;
        }

        requireNonNull(ctor, "no-argument constructor is not found");

        ctor.setAccessible(true);
        T c = (T)ctor.newInstance();

        for (Field field : aClass.getDeclaredFields())
        {
            for (Annotation annotation : field.getAnnotations())
            {
                if (annotation instanceof Cell)
                {
                    field.setAccessible(true);

                    if (field.getType().equals(Integer.class))
                        System.out.println("is integer");

                    switch (((Cell) annotation).type())
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
                    }
                }
            }

        }



        return c;
    }
}