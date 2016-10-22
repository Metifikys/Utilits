package metifikys.utils;

import javaslang.control.Try;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Properties;

/**
 * Class to work with the config.properties
 * Created by Metifikys on 2016-04-27.
 */
public class PropWork
{
    private static final Logger LOGGER = LogManager.getLogger(PropWork.class.getName());
    private static final String PROP_FILE_NAME = "config.properties";
    public static final String DEFAULT_CONFIG_PARAM = PropWork.class.getName();

    private static Properties prop = init(PROP_FILE_NAME);

    private PropWork () {}

    public static Properties init(String fileName)
    {
        Properties prop = new Properties();

        Try.run(() ->  prop.load(PropWork.class.getClassLoader().getResourceAsStream(fileName)))
                .onFailure(LOGGER::error);

        return prop;
    }

    /**
     * Getting parameters from config
     *
     * @param parameterName - parameter name
     * @return Data for from config or DEFAULT CONFIG PARAM
     */
    public static String getPropValue(String parameterName)
    {
        return prop.getProperty(parameterName, DEFAULT_CONFIG_PARAM);
    }

    /**
     * Getting parameters from config
     *
     * @param parameterName - parameter name
     * @param defaultValue - default value
     * @return Data for from config or DEFAULT CONFIG PARAM
     */
    public static String getPropValue(String parameterName, String defaultValue)
    {
        return prop.getProperty(parameterName, defaultValue);
    }
}