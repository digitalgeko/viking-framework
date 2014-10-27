package nl.viking.utils

import com.liferay.portal.kernel.portlet.LiferayPortletRequest
import com.liferay.portal.kernel.portlet.LiferayPortletResponse
import com.liferay.portal.kernel.search.IndexerRegistryUtil
import com.liferay.portal.kernel.workflow.WorkflowHandlerRegistryUtil
import com.liferay.portal.kernel.workflow.WorkflowTaskManagerUtil
import com.liferay.portal.util.PortalUtil
import com.liferay.portlet.asset.service.AssetEntryLocalServiceUtil
import nl.viking.model.annotation.Searchable
import nl.viking.model.annotation.SearchableField
import nl.viking.model.annotation.Workflow
import nl.viking.model.indexer.VikingModelIndexer
import nl.viking.model.liferay.workflow.VikingModelWorkflowHandler
import org.reflections.Reflections

import javax.portlet.PortletRequest
import javax.portlet.PortletURL
import java.lang.reflect.Method

/**
 * User: mardo
 * Date: 10/22/14
 * Time: 2:59 PM
 */
class WorkflowUtils {

	static registerHandlers() {
		def reflections = new Reflections("models")

		reflections.getTypesAnnotatedWith(Workflow.class).each { modelClass ->
			Workflow workflowAnnotation = modelClass.annotations.find {it instanceof Workflow}

			WorkflowHandlerRegistryUtil.register(new VikingModelWorkflowHandler(modelClass, workflowAnnotation))
		}
	}
}
