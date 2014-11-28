package nl.viking.listeners

import nl.viking.db.HibernateFactory
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

	@Override
	void contextInitialized(ServletContextEvent sce) {
		ModelEnhancer.enhanceAllModels()

		ModelResourcesUtils.registerAllModels(sce.servletContext)

		IndexerUtils.registerAllModelIndexers()

		AssetFactoryUtils.registerAllFactories()

		WorkflowUtils.registerHandlers()
	}

	@Override
	void contextDestroyed(ServletContextEvent sce) {
		HibernateFactory.destroy()
	}
}
