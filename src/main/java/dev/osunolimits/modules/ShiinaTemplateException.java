package dev.osunolimits.modules;

import java.io.Writer;

import dev.osunolimits.main.App;
import freemarker.core.Environment;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

public class ShiinaTemplateException implements TemplateExceptionHandler{

    @Override
    public void handleTemplateException(TemplateException te, Environment env, Writer out) throws TemplateException {
        App.log.error("Error processing template " + te.getTemplateSourceName() + " | " +  te.getMessage());
    }
    
}
