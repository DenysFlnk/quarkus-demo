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
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.SpreadsheetML.WorksheetPart;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.Document;
import org.xlsx4j.jaxb.Context;
import org.xlsx4j.sml.Worksheet;

@ApplicationScoped
public class DocumentService {

    private static final String XLSX_TEMPLATE_PATH = "/docs_templates/template.xlsx";
    private static final String DOCX_TEMPLATE_PATH = "/docs_templates/template.docx";
    private static final String MALL_LIST_TABLE_TEMPLATE_PATH = "/docs_templates/shopping_mall_list_xlsx.vm";
    private static final String MALL_LIST_DOCX_TEMPLATE_PATH = "/docs_templates/shopping_mall_list_docx.vm";

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
        String updatedXml = getShoppingMallListUpdatedXml(malls, MALL_LIST_TABLE_TEMPLATE_PATH);

        try {
            String templateFile = DocumentService.class.getResource(XLSX_TEMPLATE_PATH).getPath();

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

    public File convertShoppingMallListToDocx(List<ShoppingMall> malls) {
        String updatedXml = getShoppingMallListUpdatedXml(malls, MALL_LIST_DOCX_TEMPLATE_PATH);

        try {
            String templateFile = DocumentService.class.getResource(DOCX_TEMPLATE_PATH).getPath();

            WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(new File(templateFile));
            MainDocumentPart mainDocumentPart = wordMLPackage.getMainDocumentPart();

            Document document = (Document) XmlUtils.unmarshalString(updatedXml);
            mainDocumentPart.setContents(document);

            File tempFile = File.createTempFile("document-", ".docx");
            tempFile.deleteOnExit();

            wordMLPackage.save(tempFile);

            return tempFile;
        } catch (Exception e) {
            Log.warn("Exception occurred in convertShoppingMallListToDocx()");
            throw new RuntimeException(e);
        }
    }

    private String getShoppingMallListUpdatedXml(List<ShoppingMall> malls, String templatePath) {
        Template template = engine.getTemplate(templatePath);

        VelocityContext context = new VelocityContext();
        context.put("malls", malls);

        StringWriter writer = new StringWriter();
        template.merge(context, writer);

        return writer.toString();
    }
}
