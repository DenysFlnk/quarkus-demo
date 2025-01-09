package com.service;

import com.quarkus.model.ShoppingMall;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.xml.bind.JAXBElement;
import java.io.File;
import java.io.StringWriter;
import java.util.List;
import java.util.Properties;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.docx4j.XmlUtils;
import org.docx4j.openpackaging.packages.SpreadsheetMLPackage;
import org.docx4j.openpackaging.parts.SpreadsheetML.WorksheetPart;
import org.xlsx4j.jaxb.Context;
import org.xlsx4j.sml.Worksheet;

@ApplicationScoped
public class DocumentService {

    private static final String XLSX_TEMPLATE_PATH = "/docs_templates/template.xlsx";
    private static final String MALL_TABLE_TEMPLATE_PATH = "/docs_templates/shopping_mall_table.vm";

    private final VelocityEngine engine;

    public DocumentService() {
        engine = new VelocityEngine();
        Properties properties = new Properties();
        properties.setProperty("resource.loaders", "class");
        properties.setProperty("resource.loader.class.class",
            "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        engine.init(properties);
    }

    public File convertShoppingMallListToXlsx(List<ShoppingMall> malls) {
        Template template = engine.getTemplate(MALL_TABLE_TEMPLATE_PATH);

        VelocityContext context = new VelocityContext();
        context.put("malls", malls);

        StringWriter writer = new StringWriter();
        template.merge(context, writer);

        String updatedXml = writer.toString();

        String templateFile = DocumentService.class.getResource(XLSX_TEMPLATE_PATH).getPath();

        try {
            SpreadsheetMLPackage xlsxPackage = SpreadsheetMLPackage.load(new File(templateFile));
            WorksheetPart worksheetPart = xlsxPackage.getWorkbookPart().getWorksheet(0);

            JAXBElement<Worksheet> jaxbElement =
                (JAXBElement<Worksheet>) XmlUtils.unmarshalString(updatedXml, Context.jcSML);
            worksheetPart.setContents(jaxbElement.getValue());

            File tempFile = File.createTempFile("table-", ".xlsx");
            tempFile.deleteOnExit();

            xlsxPackage.save(tempFile);

            return tempFile;
        } catch (Exception e) {
            Log.warn("Exception occurred in convertShoppingMallListToXlsx()");
            throw new RuntimeException(e);
        }
    }
}
