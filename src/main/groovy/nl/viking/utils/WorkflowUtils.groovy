package nl.viking.utils

import com.liferay.portal.kernel.workflow.WorkflowHandlerRegistryUtil
import nl.viking.model.annotation.Workflow
import nl.viking.model.liferay.workflow.VikingModelWorkflowHandler

/**
 * User: mardo
 * Date: 10/22/14
 * Time: 2:59 PM
 */
class WorkflowUtils {

	static registerHandlers() {
		ReflectionUtils.getModelClassesWithAnnotations(Workflow.class).each { modelClass ->
			Workflow workflowAnnotation = modelClass.annotations.find {it instanceof Workflow}

			WorkflowHandlerRegistryUtil.register(new VikingModelWorkflowHandler(modelClass, workflowAnnotation))
		}
	}
}
