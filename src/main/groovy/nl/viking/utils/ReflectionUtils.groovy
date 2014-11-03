package nl.viking.utils

import org.reflections.Reflections

import java.lang.annotation.Annotation

/**
 * User: mardo
 * Date: 10/30/14
 * Time: 11:27 AM
 */
class ReflectionUtils {

	static Set<Class> getModelClassesWithAnnotations(Collection<String> packages = ["models"], Class<Annotation>... annotationClasses) {
		packages.collect { it ->
			if (ReflectionUtils.getClassLoader().getResource(it) != null) {
				def reflections = new Reflections(it)
				return annotationClasses.collect{ reflections.getTypesAnnotatedWith(it) }.flatten().toSet()
			}
			return null
		}.findAll{ it != null }.flatten()
	}

	static String[] getModelClassNamesWithAnnotations(Collection<String> packages = ["models"], Class<Annotation>... annotationClasses) {
		def modelClasses = getModelClassesWithAnnotations(packages, annotationClasses)
		if (modelClasses) {
			return modelClasses.collect{ it.name } as String[]
		}
		return new String[0]
	}

	static Set<Class> getModelsSubTypesOf(List<String> packages = ["models"], Class... modelClasses) {

		if (ReflectionUtils.getClassLoader().getResource("models") != null){
			def reflections = new Reflections("models")
			return modelClasses.collect() { reflections.getSubTypesOf(it) }.flatten().toSet()
		}
		return null
	}
}
