package metifikys.utils.DataBase.initializer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import metifikys.utils.PropWork;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static metifikys.utils.DataBase.initializer.InitAll.getDataSourceFromPoolProperties;
import static metifikys.utils.PropWork.getPropValue;

/**
 * Loader data to connect to the database from the Properties File (BD_FILE_PATH in config.properties)
 * Created by Metifikys on 2016-09-12.
 */
public final class DbInitializerPropertiesFile
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

        try(InputStream inputStream = new URL(getPropValue(BD_FILE_PATH)).openStream())
        {
            prop.load(inputStream);
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

            out.put(dbse, getDataSourceFromPoolProperties(pool));
        }

        return out;
    }
}