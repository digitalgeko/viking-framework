package nl.viking.controllers.router

import freemarker.template.SimpleScalar
import freemarker.template.SimpleSequence
import freemarker.template.TemplateMethodModelEx
import freemarker.template.TemplateModel
import freemarker.template.TemplateModelException
import nl.viking.Conf

/**
 * Created with IntelliJ IDEA.
 * User: mardo
 * Date: 4/11/13
 * Time: 10:06 AM
 * To change this template use File | Settings | File Templates.
 */
class JSRouterFreemarkerMethod implements TemplateMethodModelEx {
    def request
    def response

    public TemplateModel exec(List args) throws TemplateModelException {

        def route = args.get(0).toString()
        LinkedHashMap params = []

        if (args.size() > 1){
            SimpleSequence freemarkerParams = args.get(1)

            freemarkerParams.toList().each {
                String parameterName = it.toString()
                params << [(parameterName): "${Conf.JS_ROUTER_PARAMETER_PREFIX}_$parameterName"];
            }
        }

        def url = new Router(request:request, response: response).route(route, params)
        def jsFunction = """
function (params) {
    var url = "${url}";
    for (var key in params) {
        var value = params[key];
        if (value == null) value = "";
        url = url.replace("${Conf.JS_ROUTER_PARAMETER_PREFIX}_"+key, encodeURIComponent(value));
    }
    return url;
}
"""
        return new SimpleScalar(jsFunction);
    }
}
