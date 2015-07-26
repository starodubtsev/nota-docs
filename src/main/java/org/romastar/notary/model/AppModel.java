package org.romastar.notary.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.romastar.notary.controller.AppLogger;
import org.romastar.notary.controller.SerializerController;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Created by roman on 26.07.14.
 */
public class AppModel {
    private static AppModel ourInstance = new AppModel();
    public Properties messagesProperties;
    public Properties applicationProperties;
    private StringProperty info;
    private StringProperty reportHtmlTemplate;

    private DocumentsList _documentsList;
    private TemplatesList _templatesList;
    private PrintedTemplatesList _printedTemplatesList;
    ;

    private AppModel() {

    }

    public static AppModel getInstance() {
        return ourInstance;
    }

    ;

    public void loadData() {
        try {
            String fileContent = FileUtils.readFileToString(new File(getPath4File("info.txt")), Charset.forName("UTF-8"));
            info = new SimpleStringProperty(fileContent);

            fileContent = FileUtils.readFileToString(new File(getPath4File("documents-list-template.html")), Charset.forName("UTF-8"));
            reportHtmlTemplate = new SimpleStringProperty(fileContent);

            applicationProperties = new Properties();
            applicationProperties.load(Files.newBufferedReader(Paths.get("documents/application.properties").toRealPath(), Charset.forName("UTF-8")));

            messagesProperties = new Properties();
            messagesProperties.load(new InputStreamReader(getClass().getResourceAsStream("messages.properties"), "UTF-8"));

            loadDocumentsList();
            loadTemplatesList();
            loadPrintedTemplatesList();

        } catch (IOException ex) {
            AppLogger.processException(ex);
        }
    }

    public void loadDocumentsList() {
        try {
            _documentsList = (DocumentsList) SerializerController.getInstance().loadXML(DocumentsList.class, DocumentsList.FILE);
            if (_documentsList == null)
                _documentsList = new DocumentsList();
        } catch (IOException | JAXBException ex) {
            String message = String.format(AppModel.getInstance().messagesProperties.getProperty("ERROR_FILE_LOADING"), DocumentsList.FILE);
            AppLogger.processException(message, ex);
        }
    }

    public void loadTemplatesList() {
        try {
            _templatesList = (TemplatesList) SerializerController.getInstance().loadXML(TemplatesList.class, TemplatesList.FILE);
            if (_templatesList == null)
                _templatesList = new TemplatesList();
        } catch (IOException | JAXBException ex) {
            String message = String.format(AppModel.getInstance().messagesProperties.getProperty("ERROR_FILE_LOADING"), TemplatesList.FILE);
            AppLogger.processException(message, ex);
        }
    }

    public void loadPrintedTemplatesList() {
        try {
            _printedTemplatesList = (PrintedTemplatesList) SerializerController.getInstance().loadXML(PrintedTemplatesList.class, PrintedTemplatesList.FILE);
            if (_printedTemplatesList == null) {
                _printedTemplatesList = new PrintedTemplatesList();
            }
        } catch (IOException | JAXBException ex) {
            String message = String.format(AppModel.getInstance().messagesProperties.getProperty("ERROR_FILE_LOADING"), PrintedTemplatesList.FILE);
            AppLogger.processException(message, ex);
        }
    }

    public void saveDocumentsList() {
        try {
            SerializerController.getInstance().saveXML(AppModel.getInstance().getDocumentsList(), DocumentsList.FILE);

        } catch (IOException | JAXBException ex) {
            AppLogger.processException(ex);
        }
    }

    public void updateTemplatesList() {
        for (Template template : getTemplatesList().getTemplates()) {
            template.setTemplateDocuments(updateDocumentsListInTemplate(template, getDocumentsList().getDocuments()));
        }
    }

    protected ObservableList<DocumentTemplate> updateDocumentsListInTemplate(Template template, ObservableList<Document> newDocumentsList) {
        ObservableList<DocumentTemplate> list = FXCollections.observableArrayList();
        for (Document document : newDocumentsList) {
            DocumentTemplate oldDocumentTemplate = template.getDocumentTemplateByDocumentUid(document.getUid());
            DocumentTemplate newDocumentTemplate = new DocumentTemplate(document, oldDocumentTemplate != null ? oldDocumentTemplate.isSelected() : false);
            list.add(newDocumentTemplate);
        }
        return list;
    }

    public boolean printedTemplateNameExists(String name) {
        for (PrintedTemplate printedTemplate : getPrintedTemplatesList().getPrintedTemplates()) {
            if (printedTemplate.getName().equalsIgnoreCase(name))
                return true;
        }
        return false;
    }

    public void saveTemplatesList() {
        try {
            SerializerController.getInstance().saveXML(AppModel.getInstance().getTemplatesList(), TemplatesList.FILE);

        } catch (IOException | JAXBException ex) {
            AppLogger.processException(ex);
        }
    }

    public void savePrintedTemplatesList() {
        try {
            SerializerController.getInstance().saveXML(AppModel.getInstance().getPrintedTemplatesList(), PrintedTemplatesList.FILE);

        } catch (IOException | JAXBException ex) {
            AppLogger.processException(ex);
        }
    }

    public StringProperty infoProperty() {
        return info;
    }

    public StringProperty reportHtmlTemplateProperty() {
        return reportHtmlTemplate;
    }

    public DocumentsList getDocumentsList() {
        if (_documentsList == null)
            _documentsList = new DocumentsList();
        return _documentsList;
    }

    public TemplatesList getTemplatesList() {
        if (_templatesList == null)
            _templatesList = new TemplatesList();
        return _templatesList;
    }

    public PrintedTemplatesList getPrintedTemplatesList() {
        if (_printedTemplatesList == null)
            _printedTemplatesList = new PrintedTemplatesList();
        return _printedTemplatesList;
    }

    public String getDocumentsDirectory() {
        return "documents";
    }

    public String getPath4File(String file) {
        return FilenameUtils.normalize(getDocumentsDirectory() + "/" + file);
    }
}
