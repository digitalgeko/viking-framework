package nl.viking.controllers.router

import freemarker.template.*

/**
 * Created with IntelliJ IDEA.
 * User: nefy
 * Date: 27/11/13
 * Time: 10:06 AM
 * To change this template use File | Settings | File Templates.
 */
class JSi18nFreemarkerMethod implements TemplateMethodModelEx {
    def request
    def response
    def h

    public TemplateModel exec(List args) throws TemplateModelException {

        def i18nMap = "";

        if (args.size() > 0){
            SimpleSequence freemarkerParams = args.get(0)

            freemarkerParams.toList().each {
                String parameterName = it.toString()
                String parameterValue = h.messages.get(parameterName)
                parameterValue = parameterValue.replace('"','\\"')
                i18nMap += "\"${parameterName}\" : \"${parameterValue}\","
            }

            i18nMap = i18nMap[0..-1]
        }



        def jsFunction = """
function (key) {
    var i18nMap = {${i18nMap}};

    if (i18nMap[key] !== undefined) {
        return i18nMap[key];
    }

    return key;
}
"""
        return new SimpleScalar(jsFunction);
    }
}
