package metifikys.utils.DataBase.initializer;

import com.sun.xml.internal.txw2.annotation.XmlElement;
import metifikys.utils.PropWork;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Metifikys on 2016-11-02.
 */
public class DbInitializerXmlConfigs
{
    private static final Logger LOGGER =
                LogManager.getLogger(new Object(){}.getClass().getEnclosingClass().getName());

    public static final String BD_XML_FILE_PATH = "BD_XML_FILE_PATH";

    private DbInitializerXmlConfigs(){}

    public static Map<String, DataSource> init()
    {
        Map<String, DataSource> outMap = new HashMap<>();

        String propValue = PropWork.getPropValue(BD_XML_FILE_PATH);

        if (PropWork.DEFAULT_CONFIG_PARAM.equals(propValue))
            return outMap;

        try
        {
            Unmarshaller jaxbUnmarshaller =
                    JAXBContext.newInstance(DataSourcesParams.class).createUnmarshaller();

            DataSourcesParams dataSourceParams = (DataSourcesParams)
                    jaxbUnmarshaller.unmarshal(new File(propValue));

            outMap = dataSourceParams.getDataSourcesParams()
                    .stream()
                    .collect(
                            Collectors.toMap(DataSourceParams::getName, DataSourceParams::createDataSource)
                    );
        }
        catch (JAXBException e)
        {
            LOGGER.error(e);
        }

        return outMap;
    }


    @XmlRootElement(name = "DataSourcesParams")
    @XmlAccessorType(XmlAccessType.FIELD)
    private static class DataSourcesParams
    {
        List<DataSourceParams> dataSourcesParams = new ArrayList<>();

        @XmlElement("DataSourceParams")
        public List<DataSourceParams> getDataSourcesParams()
        {
            return dataSourcesParams;
        }

        public void setDataSourcesParams(List<DataSourceParams> dataSourcesParams)
        {
            this.dataSourcesParams = dataSourcesParams;
        }
    }

    @XmlRootElement(name = "DataSourceParams")
    @XmlAccessorType(XmlAccessType.FIELD)
    private static class DataSourceParams
    {
        private String name;
        private String url;
        private String userName;
        private String password;
        private String driver;

        @XmlElement("url")
        public void setUrl(String url)           { this.url = url; }

        @XmlElement("userName")
        public void setUserName(String userName) { this.userName = userName; }

        @XmlElement("password")
        public void setPassword(String password) { this.password = password; }

        @XmlElement("driver")
        public void setDriver(String driver)     { this.driver = driver; }

        @XmlElement("name")
        public void setName(String name)         { this.name = name; }

        public String getName() { return name; }

        private DataSource createDataSource()
        {
            PoolProperties pool = new PoolProperties();

            pool.setUrl(url);
            pool.setUsername(userName);
            pool.setPassword(password);
            pool.setDriverClassName(driver);

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
}