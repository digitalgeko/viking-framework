package nl.viking.listeners

import nl.viking.db.GMongoDBFactory
import nl.viking.db.HibernateFactory
import nl.viking.db.MorphiaFactory
import nl.viking.enhancers.ModelEnhancer
import nl.viking.utils.AssetFactoryUtils
import nl.viking.utils.IndexerUtils
import nl.viking.utils.ModelResourcesUtils
import nl.viking.utils.WorkflowUtils

import javax.servlet.ServletContextEvent
import javax.servlet.ServletContextListener

/**
 * User: mardo
 * Date: 11/24/14
 * Time: 12:28 PM
 */
class BootstrapListener implements ServletContextListener {

    private static volatile boolean applicationStarted = false;

	@Override
	void contextInitialized(ServletContextEvent sce) {
        if (!applicationStarted) {
            applicationStarted = true;
            ModelEnhancer.enhanceAllModels()

            ModelResourcesUtils.registerAllModels(sce.servletContext)

            IndexerUtils.registerAllModelIndexers()

            AssetFactoryUtils.registerAllFactories()

            WorkflowUtils.registerHandlers()
        }
	}


	@Override
	void contextDestroyed(ServletContextEvent sce) {
        if (applicationStarted) {
            applicationStarted = false;
            MorphiaFactory.destroy()
            GMongoDBFactory.destroy()
            HibernateFactory.destroy()
        }
	}
}
