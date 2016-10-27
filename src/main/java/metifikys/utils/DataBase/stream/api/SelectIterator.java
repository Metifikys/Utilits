package metifikys.utils.DataBase.stream.api;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Return SelectGetter on next
 * Created by Metifikys on 2016-10-21.
 */
public class SelectIterator extends ResultSetIterator<SelectGetter>
{
    private SelectGetter getter;

    public SelectIterator(ResultSet rs, Connection con, Statement st)
    {
        super(rs, con, st);
        getter = new SelectGetter(rs);
    }

    @Override
    public SelectGetter next()
    {
        return getter;
    }
}