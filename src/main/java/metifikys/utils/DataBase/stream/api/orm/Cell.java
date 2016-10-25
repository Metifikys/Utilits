package metifikys.utils.DataBase.stream.api.orm;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Metifikys on 2016-10-24.
 */
@Target(value= ElementType.FIELD)
@Retention(value= RetentionPolicy.RUNTIME)
public @interface Cell
{
    String name();
    Type type();

    enum Type
    {
        STRING,
        INTEGER,
        LONG,
        DOUBLE,
        OBJECT,
    }
}
