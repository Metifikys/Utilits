package metifikys.utils.DataBase.orm;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Metifikys on 2016-10-24.
 */
@Target(ElementType.FIELD)
@Retention(value= RetentionPolicy.RUNTIME)
public @interface Cell
{
    String value();
    Type type() default Type.OBJECT;

    enum Type
    {
        STRING,
        INTEGER,
        LONG,
        DOUBLE,
        OBJECT,
    }
}
