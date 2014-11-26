package nl.viking.arquillian.deployment

/**
 * User: mardo
 * Date: 11/24/14
 * Time: 2:58 PM
 */
class VikingTestDeploymentHelper {

	static File getWarFile() {
		new File(System.properties.getProperty("viking.test.warFilePath"))
	}

}
