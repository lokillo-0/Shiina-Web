package dev.osunolimits.modules;

import java.io.*;
import dev.osunolimits.main.App;
import dev.osunolimits.main.WebServer;
import dev.osunolimits.modules.ShiinaRoute.ShiinaRequest;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import spark.Request;
import spark.Response;
import spark.Route;

public class Shiina implements Route {

    @Override
    public Object handle(Request req, Response res) throws Exception {
        throw new UnsupportedOperationException("Unimplemented method 'handle'");
    }

    public String renderTemplate(String template, ShiinaRequest shiina, Response res, Request req) {
        res.header("Content-Encoding", "gzip");
        res.header("Content-Type", "text/html; charset=utf-8");

        shiina.data.put("docs",ShiinaDocs.docs);
        try {
            Template templateFree = WebServer.freemarkerCfg.getTemplate(template);
            try (StringWriter out = new StringWriter()) {
                shiina.mysql.close();
                templateFree.process(shiina.data, out);
                return out.toString();
            } catch (TemplateException e) {
                App.log.error("Error processing template (" + template + ")", e);
                return null;
            }
        } catch (IOException e) {
            App.log.error("Error loading template (" + template + ")", e);
            res.status(500);
            return null;
        }

    }

    public Object redirect(Response response, ShiinaRequest shiina, String location) {
        response.status(302);
        response.redirect(location);
        shiina.mysql.close();
        return null;
    }

    public Object notFound(Response response, ShiinaRequest shiina) {
        response.status(404);
        shiina.mysql.close();
        return null;
        
    }

}
