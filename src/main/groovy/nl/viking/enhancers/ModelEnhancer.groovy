package nl.viking.enhancers

import nl.viking.Conf
import nl.viking.model.hibernate.GenericModel
import nl.viking.model.hibernate.Model
import org.reflections.Reflections

/**
 * User: mardo
 * Date: 11/13/13
 * Time: 12:50 PM
 */
class ModelEnhancer {

	static Boolean modelsAlreadyEnhanced = false

	static enhancers = [
			[modelClass:nl.viking.model.gmongo.GMongoModel.class, 		enhancerClass:GMongoModelEnhancer.class],
			[modelClass:nl.viking.model.hibernate.Model.class, 			enhancerClass:HibernateModelEnhancer.class],
			[modelClass:nl.viking.model.hibernate.GenericModel.class, 	enhancerClass:HibernateGenericModelEnhancer.class],
			[modelClass:nl.viking.model.morphia.Model.class, 			enhancerClass:MorphiaModelEnhancer.class],
	]

	static enhanceModel (Class clazz) {
		enhancers.each {
			if (it.modelClass.isAssignableFrom(clazz)) {
				it.enhancerClass.enhance(clazz)
			}
		}
	}

	static enhanceAllModels() {
		if (!modelsAlreadyEnhanced || Conf.properties.dev.enabled) {
			modelsAlreadyEnhanced = true

			def modelPackages = ['models', 'nl.viking.model.internal']

			modelPackages.each {
				def reflections = new Reflections(it)

				enhancers.each {
					Class modelClass = it.modelClass
					Class enhancerClass = it.enhancerClass
					def modelClasses = reflections.getSubTypesOf(modelClass);
					if (modelClass == GenericModel) {
						modelClasses.addAll(reflections.getSubTypesOf(Model.class))
					}

					modelClasses.each { type ->
						enhancerClass.enhance(type)
					}
				}
			}

		}
	}
}
