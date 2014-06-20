package nl.viking.controllers.router

import freemarker.template.SimpleScalar
import freemarker.template.TemplateMethodModelEx
import freemarker.template.TemplateModel
import freemarker.template.TemplateModelException

/**
 * Created with IntelliJ IDEA.
 * User: mardo
 * Date: 4/11/13
 * Time: 10:06 AM
 * To change this template use File | Settings | File Templates.
 */
class RouterFreemarkerMethod implements TemplateMethodModelEx {
    def request
    def response
    public TemplateModel exec(List args) throws TemplateModelException {

        def route = args.get(0).toString()
        LinkedHashMap params = []

        if (args.size() > 1){
            def freemarkerParams = args.get(1)
            for (def i = freemarkerParams.keys().iterator(); i.hasNext(); ) {
                def key = i.next().toString();
                def value = freemarkerParams.get(key).toString();
                params << [(key): value];
            }
        }

        return new SimpleScalar(new Router(request:request, response: response).route(route, params));
    }
}
