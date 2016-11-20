package metifikys.utils.DataBase.initializer;

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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static metifikys.utils.DataBase.initializer.InitAll.getDataSourceFromPoolProperties;

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
                    jaxbUnmarshaller.unmarshal(new URL(propValue));

            outMap = dataSourceParams.getListData()
                    .stream()
                    .collect(
                            Collectors.toMap(DataSourceParams::getName, DataSourceParams::createDataSource)
                    );
        }
        catch (JAXBException | MalformedURLException e)
        {
            LOGGER.error(e);
        }

        return outMap;
    }


    @XmlRootElement(name = "DataSourcesParams")
    @XmlAccessorType(XmlAccessType.FIELD)
    private static class DataSourcesParams
    {
        @XmlElement(name = "DataSourceParams")
        List<DataSourceParams> listData = new ArrayList<>();

        public List<DataSourceParams> getListData() { return listData; }
    }

    @XmlRootElement(name = "DataSourceParams")
    @XmlAccessorType(XmlAccessType.FIELD)
    private static class DataSourceParams
    {
        @XmlElement(name = "name")
        private String name;

        @XmlElement(name = "url")
        private String url;

        @XmlElement(name = "userName")
        private String userName;

        @XmlElement(name = "password")
        private String password;

        @XmlElement(name = "driver")
        private String driver;

        public String getName() { return name.trim(); }

        private DataSource createDataSource()
        {
            PoolProperties pool = new PoolProperties();

            pool.setUrl(url.trim());
            pool.setUsername(userName.trim());
            pool.setPassword(password.trim());
            pool.setDriverClassName(driver.trim());

            return getDataSourceFromPoolProperties(pool);
        }
    }
}