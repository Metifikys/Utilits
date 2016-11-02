package metifikys.utils.DataBase.initializer;

import org.apache.tomcat.jdbc.pool.DataSource;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Metifikys on 2016-11-02.
 */
public class InitAll
{
    private InitAll(){}

    public static Map<String, DataSource> init()
    {
        Map<String, DataSource> outMap = new HashMap<>();

        outMap.putAll(DbInitializerPropertiesFile.init());
        outMap.putAll(DbInitializerXmlConfigs.init());

        return outMap;
    }
}