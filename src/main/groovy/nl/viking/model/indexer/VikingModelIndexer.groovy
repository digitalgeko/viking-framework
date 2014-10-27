package nl.viking.model.indexer

import com.liferay.portal.kernel.language.LanguageUtil
import com.liferay.portal.kernel.search.BaseIndexer
import com.liferay.portal.kernel.search.BooleanQuery
import com.liferay.portal.kernel.search.Document
import com.liferay.portal.kernel.search.DocumentImpl
import com.liferay.portal.kernel.search.Field
import com.liferay.portal.kernel.search.SearchContext
import com.liferay.portal.kernel.search.SearchEngineUtil
import com.liferay.portal.kernel.search.Summary
import com.liferay.portal.util.PortalUtil
import groovy.text.SimpleTemplateEngine
import nl.viking.model.annotation.SearchableField
import nl.viking.utils.TemplateUtils

import javax.portlet.PortletURL

/**
 * User: mardo
 * Date: 10/16/14
 * Time: 2:32 PM
 */

class VikingModelIndexer extends BaseIndexer {

	String titleKey

	String descriptionKey

	Class modelClass

	Long companyId = PortalUtil.defaultCompanyId

	String portletId

	List searchableFields

	public static final MODEL_CLASS_NAME = "modelClassName"

	public static final MODEL_ID = "modelId"

	VikingModelIndexer(String titleKey, String descriptionKey, Class modelClass, String portletId, List searchableFields) {
		this.titleKey = titleKey
		this.descriptionKey = descriptionKey
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
				document."add${type.capitalize()}"(it.name, o[it.propName])
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

		def title = TemplateUtils.i18nTemplate(locale, titleKey, templateData) ?: "${modelClass.simpleName}[$modelId]"
		def description = TemplateUtils.i18nTemplate(locale, descriptionKey, templateData) ?: document.toString()

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
			addSearchTerm(searchQuery, searchContext, it.name, it.searchableFieldAnnotation.like());
		}
	}
}
