package nl.viking.utils

import com.liferay.portlet.asset.AssetRendererFactoryRegistryUtil
import nl.viking.model.annotation.Asset
import nl.viking.model.liferay.asset.renderer.VikingModelAssetRendererFactory

/**
 * User: mardo
 * Date: 10/21/14
 * Time: 10:59 AM
 */
class AssetFactoryUtils {

	static registerAllFactories () {
		ReflectionUtils.getModelClassesWithAnnotations(Asset.class).each { modelClass ->
			Asset assetAnnotation = modelClass.annotations.find {it instanceof Asset}

			AssetRendererFactoryRegistryUtil.register(new VikingModelAssetRendererFactory(modelClass, assetAnnotation))
		}
	}
}
