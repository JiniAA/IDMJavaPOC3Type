
package com.tarento.Idm.poc.service;

import freemarker.cache.StringTemplateLoader;
import freemarker.core.ParseException;
import freemarker.template.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringWriter;
@Service
public class TemplateParser {

    Configuration cfg = new Configuration(Configuration.VERSION_2_3_23);
    StringTemplateLoader stringLoader = new StringTemplateLoader();

    public String parse(String templateName, String fillerObject, Object object) {
        try {
            stringLoader.putTemplate( templateName, fillerObject);
            cfg.setTemplateLoader(stringLoader);
            Template template = cfg.getTemplate(templateName);
            StringWriter stringWriter = new StringWriter();
            template.process(object, stringWriter);
            stringWriter.flush();
            return stringWriter.toString();
        } catch (IOException e) {
            e.printStackTrace();

        } catch (TemplateException e) {
            e.printStackTrace();

        }
        return null;
    }
}

