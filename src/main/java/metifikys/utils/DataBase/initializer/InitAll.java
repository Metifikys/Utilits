package metifikys.utils.DataBase.initializer;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;

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


    public static DataSource getDataSourceFromPoolProperties(PoolProperties pool)
    {
        pool.setTestOnBorrow(true);
        pool.setValidationQuery("SELECT 1");
        pool.setTimeBetweenEvictionRunsMillis(30000);
        pool.setMaxWait(10000);
        pool.setRemoveAbandonedTimeout(10);
        pool.setMinEvictableIdleTimeMillis(30000);
        pool.setMinIdle(30);
        pool.setInitialSize(5);
        pool.setLogAbandoned(true);
        pool.setRemoveAbandoned(true);
        pool.setJdbcInterceptors(
                "org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;" +
                        "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer");

        DataSource datasource = new DataSource();
        datasource.setPoolProperties(pool);
        return datasource;
    }
}