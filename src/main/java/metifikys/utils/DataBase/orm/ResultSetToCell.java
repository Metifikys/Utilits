package metifikys.utils.DataBase.orm;

import metifikys.utils.DataBase.stream.api.ResultSetIterator;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * return instance of T on next
 * Created by Metifikys on 2016-10-24.
 */
public class ResultSetToCell<T> extends ResultSetIterator<T>
{
    private ConstructorCell<T> constructorCell;

    public ResultSetToCell(ResultSet rs, Connection con, Statement st, Class<T> aClass)
    {
        super(rs, con, st);
        constructorCell = ConstructorCell.of(aClass);
    }

    @Override
    public T next()
    {
        return constructorCell.createOrNull(rs);
    }
}