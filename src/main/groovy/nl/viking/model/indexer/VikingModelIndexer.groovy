package nl.viking.model.indexer

import com.liferay.portal.kernel.search.*
import com.liferay.portal.util.PortalUtil
import groovy.text.SimpleTemplateEngine
import nl.viking.controllers.Controller
import nl.viking.logging.Logger
import nl.viking.model.annotation.Asset
import nl.viking.utils.TemplateUtils

import javax.portlet.PortletURL

/**
 * User: mardo
 * Date: 10/16/14
 * Time: 2:32 PM
 */

class VikingModelIndexer extends BaseIndexer {

	Class modelClass

	String portletId

	List searchableFields

	public static final MODEL_CLASS_NAME = "modelClassName"

	public static final MODEL_ID = "modelId"

    Long getCompanyId () {
        def h = Controller.currentDataHelper
        if (h) {
            return h.themeDisplay.companyId
        }
        PortalUtil.defaultCompanyId
    }

	VikingModelIndexer(Class modelClass, String portletId, List searchableFields) {
		this.modelClass = modelClass
		this.portletId = portletId
		this.searchableFields = searchableFields
	}

	@Override
	protected void doDelete(Object o) throws Exception {
		deleteDocument(companyId, o.id)
	}

	@Override
	protected Document doGetDocument(Object o) throws Exception {
		Document document = new DocumentImpl()
		document.addUID(portletId, o.id)

		document.addKeyword(MODEL_ID, o.id)
		document.addKeyword(Field.ENTRY_CLASS_NAME, modelClass.name)
		document.addKeyword(Field.PORTLET_ID, portletId)
		document.addKeyword(Field.COMPANY_ID, companyId)

		Asset assetAnnotation = modelClass.annotations.find {it instanceof Asset}
		if (assetAnnotation) {
			document.addKeyword(Field.ENTRY_CLASS_PK, o.assetInfo.classPK)
			document.addKeyword(Field.GROUP_ID, o.assetInfo.groupId)
			document.addKeyword(Field.SCOPE_GROUP_ID, o.assetInfo.groupId)
		}

		searchableFields.each {
			def type = it.searchableFieldAnnotation.type()
			Class fieldType = it.fieldType
			if (!type) {
				if (fieldType.isAssignableFrom(String.class)) {
					type = "text"
				} else if (fieldType.isAssignableFrom(Date.class)) {
					type = "date"
				} else if ([Double.class, Float.class, Integer.class, Long.class].find { fieldType.isAssignableFrom(it) }) {
					type = "number"
				}	else {
					type = "keyword"
				}
			}

			if (type.equalsIgnoreCase("keyword") && fieldType.isAssignableFrom(String.class)) {
				document.addKeyword(it.name, o[it.propName].toString().toLowerCase())
			} else {
				try {
                    if (o[it.propName] != null) {
                        document."add${type.capitalize()}"(it.name, o[it.propName])
                    }
				} catch (e) {
					Logger.error("Field $it.propName could not be set using document.add${type.capitalize()}(${it.name}, ${o[it.propName]})")
					throw e
				}

			}
		}

		return document
	}

	@Override
	protected Summary doGetSummary(Document document, Locale locale, String s, PortletURL portletURL) throws Exception {
		def modelId = document[MODEL_ID]

		portletURL.setParameter(MODEL_CLASS_NAME, modelClass.name)
		portletURL.setParameter(MODEL_ID, modelId.toString())

		def engine = new SimpleTemplateEngine()
		def record = modelClass.findById(modelId)
		def templateData = [
				modelClass: modelClass,
				modelId: modelId,
				document: document,
				record: record
		]


		def title = TemplateUtils.i18nTemplate(locale, "model.resource.${modelClass.name}.title", templateData) ?: "${modelClass.simpleName}[$modelId]"
		def description = TemplateUtils.i18nTemplate(locale, "model.resource.${modelClass.name}.description", templateData) ?: document.toString()

		return new Summary(title, description, portletURL)
	}

	@Override
	protected void doReindex(Object o) throws Exception {
		SearchEngineUtil.updateDocument(getSearchEngineId(), companyId, getDocument(o))
	}

	@Override
	protected void doReindex(String s, long l) throws Exception {
		// TODO
	}

	@Override
	protected void doReindex(String[] strings) throws Exception {
		// TODO
	}

	@Override
	void reindex(Object obj) throws SearchException {
		super.reindex(obj)
	}

	@Override
	void reindex(String className, long classPK) throws SearchException {
		super.reindex(className, classPK)
	}

	@Override
	void reindex(String[] ids) throws SearchException {
		super.reindex(ids)
	}



	@Override
	protected String getPortletId(SearchContext searchContext) {
		portletId
	}


	@Override
	String[] getClassNames() {
		[modelClass.name] as String[]
	}

	@Override
	String getPortletId() {
		portletId
	}

	@Override
	void postProcessSearchQuery(BooleanQuery searchQuery, SearchContext searchContext) throws Exception {
		searchableFields.each {
			addSearchTerm(searchQuery, searchContext, it.name, it.searchableFieldAnnotation.like())
		}
	}
}
