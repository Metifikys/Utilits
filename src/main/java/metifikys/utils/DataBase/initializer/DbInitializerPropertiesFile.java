package metifikys.utils.DataBase.initializer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import metifikys.utils.PropWork;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Loader data to connect to the database from the Properties File (BD_FILE_PATH in config.properties)
 * Created by Metifikys on 2016-09-12.
 */
final public class DbInitializerPropertiesFile
{
    private static final Logger LOGGER =
                LogManager.getLogger(new Object(){}.getClass().getEnclosingClass().getName());

    public static final String BD_FILE_PATH = "BD_FILE_PATH";

    private DbInitializerPropertiesFile() {}

    /**
     * Loading parameters from the configuration file <br>
     * example:<br>
     *
     * dbs=name1<br>
     * name1.login=DBlogin<br>
     * name1.pass=DBpass<br>
     * name1.url=jdbc:postgresql://localhost:5432/DBName <br>
     *
     * @return Map the connection name and connection pool
     */
    public static Map<String, DataSource> init()
    {
        Properties prop = new Properties();
        try (InputStream is = new FileInputStream(PropWork.getPropValue(BD_FILE_PATH));)
        {
            prop.load(is);
        }
        catch (IOException e)
        {
            LOGGER.error(e);
        }

        if (prop.isEmpty())
        {
            LOGGER.error("properties don't loaded");
            return new HashMap<>();
        }


        Map<String, DataSource> out = new HashMap<>();

        for (String dbse : prop.getProperty("dbs").split(","))
        {
            PoolProperties pool = new PoolProperties();

            pool.setUrl(prop.getProperty(dbse + ".url"));
            pool.setUsername(prop.getProperty(dbse + ".login"));
            pool.setPassword(prop.getProperty(dbse + ".pass"));
            pool.setDriverClassName(prop.getProperty(dbse + ".driver"));

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

            out.put(dbse, datasource);
        }

        return out;
    }
}