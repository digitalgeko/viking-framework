package nl.viking.utils

import com.liferay.portal.kernel.search.IndexerRegistryUtil
import nl.viking.model.annotation.Searchable
import nl.viking.model.annotation.SearchableField
import nl.viking.model.indexer.VikingModelIndexer

import java.lang.reflect.Method

/**
 * User: mardo
 * Date: 10/16/14
 * Time: 2:32 PM
 */
class IndexerUtils {

	static registerAllModelIndexers() {

		ReflectionUtils.getModelClassesWithAnnotations(Searchable.class).each { modelClass ->
			Searchable searchableAnnotation = modelClass.annotations.find {it instanceof Searchable}

			def searchableFields = [modelClass.declaredFields + modelClass.declaredMethods].flatten().collect {
				String propName = it.name
				if (propName.startsWith("get") && it instanceof Method) {
					propName = propName.getAt(3).toLowerCase() + propName.substring(4)
				}
				def searchableFieldAnnotation = it.annotations.find {it instanceof SearchableField}
				if (searchableFieldAnnotation) {
					return [
							searchableFieldAnnotation: searchableFieldAnnotation,
							fieldType: it instanceof Method ? it.returnType : it.type,
							name: searchableFieldAnnotation.name() ?: propName,
							propName: propName
					]
				}
				return null
			}.findAll { it != null }

			def modelIndexer = new VikingModelIndexer(
					modelClass,
					searchableAnnotation.portletId(),
					searchableFields
			)

			IndexerRegistryUtil.register(modelIndexer)
		}
	}
}
