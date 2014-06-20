package nl.viking

import org.apache.commons.configuration.CompositeConfiguration
import org.apache.commons.configuration.ConfigurationException
import org.apache.commons.configuration.PropertiesConfiguration

/**
 * Created with IntelliJ IDEA.
 * User: mardo
 * Date: 4/2/13
 * Time: 12:12 PM
 * To change this template use File | Settings | File Templates.
 */
class Conf {
    public static final String FRAMEWORK_NAME = "VIKING";
    public static final String REQUEST_CONTROLLER_KEY = "${FRAMEWORK_NAME}_controller";
    public static final String REQUEST_ACTION_KEY = "${FRAMEWORK_NAME}_action";
    public static final String REQUEST_TEMPLATE_KEY = "${FRAMEWORK_NAME}_viewTemplate";
    public static final String REQUEST_DATA_KEY = "${FRAMEWORK_NAME}_data";
    public static final String REQUEST_STRING_DATA_KEY = "${FRAMEWORK_NAME}_stringData";

    public static final String JS_ROUTER_PARAMETER_PREFIX = "${FRAMEWORK_NAME}_JS_ROUTER_PARAMETER_PREFIX";

    private static final String CONFIG_FILE = "portlet.conf";
	private static final String DEV_CONFIG_FILE = "dev.conf";
    public final static ConfigObject properties;

    static {
        ConfigObject mainConfig = new ConfigSlurper().parse(Conf.class.classLoader.getResource(CONFIG_FILE))
		def devConfigResource = Conf.class.classLoader.getResource(DEV_CONFIG_FILE)
		if (devConfigResource) {
			ConfigObject devConfig = new ConfigSlurper().parse(devConfigResource)
			mainConfig.merge(devConfig)
		}
		properties = mainConfig
	}

}
