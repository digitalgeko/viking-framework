package nl.viking.utils

import com.liferay.portal.kernel.exception.PortalException
import com.liferay.portal.kernel.exception.SystemException
import com.liferay.portal.kernel.portlet.LiferayPortletRequest
import com.liferay.portal.kernel.portlet.LiferayPortletResponse
import com.liferay.portal.security.permission.PermissionChecker
import com.liferay.portal.theme.ThemeDisplay
import com.liferay.portal.util.PortletKeys
import com.liferay.portlet.asset.AssetRendererFactoryRegistryUtil
import com.liferay.portlet.asset.model.AssetEntry
import com.liferay.portlet.asset.model.AssetRenderer
import com.liferay.portlet.asset.service.AssetEntryLocalServiceUtil
import nl.viking.model.liferay.asset.renderer.VikingModelAssetRenderer
import nl.viking.model.liferay.asset.renderer.VikingModelAssetRendererFactory
import nl.viking.model.annotation.Asset
import org.reflections.Reflections

import javax.portlet.PortletRequest
import javax.portlet.PortletURL
import javax.portlet.WindowState
import javax.portlet.WindowStateException

/**
 * User: mardo
 * Date: 10/21/14
 * Time: 10:59 AM
 */
class AssetRendererFactoryUtils {

	static registerAllFactories () {
		def reflections = new Reflections("models")

		reflections.getTypesAnnotatedWith(Asset.class).each { modelClass ->
			nl.viking.model.annotation.AssetRenderer assetRendererAnnotation = modelClass.annotations.find {it instanceof nl.viking.model.annotation.AssetRenderer}

			AssetRendererFactoryRegistryUtil.register(new VikingModelAssetRendererFactory(modelClass, assetRendererAnnotation))
		}
	}
}
