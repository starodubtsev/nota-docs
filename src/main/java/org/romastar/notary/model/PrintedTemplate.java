package org.romastar.notary.model;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.UUID;

/**
 * Created by roman on 06.08.14.
 */
@XmlType(propOrder = {"uid", "name", "title", "prompt", "printedDocumentTemplates"}, name = "printed-template")
public class PrintedTemplate {

    private StringProperty uid;
    private StringProperty name = new SimpleStringProperty("");
    private StringProperty title = new SimpleStringProperty("");
    private StringProperty prompt = new SimpleStringProperty("");

    private ListProperty<PrintedDocumentTemplate> printedDocumentTemplates = new SimpleListProperty<>(FXCollections.observableArrayList());

    public PrintedTemplate() {
        setUid(UUID.randomUUID().toString());
    }

    public PrintedTemplate(Template template) {
        this();
        if (template != null) {
            convertDocumentTemplates(template.getTemplateDocuments());
            setName(template.getName());
            setTitle(template.getName());
        }
    }

    public String getUid() {
        return uidProperty().get();
    }

    public void setUid(String value) {
        uidProperty().set(value);
    }

    public StringProperty uidProperty() {
        if (uid == null) {
            uid = new SimpleStringProperty();
        }
        return uid;
    }

    public String getName() {
        return name.get();
    }

    public void setName(String value) {
        name.set(value);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public String getTitle() {
        return title.get();
    }

    public void setTitle(String value) {
        title.set(value);
    }

    public StringProperty titleProperty() {
        return title;
    }

    public String getPrompt() {
        return prompt.get();
    }

    public void setPrompt(String value) {
        prompt.set(value);
    }

    public StringProperty promptProperty() {
        return prompt;
    }

    @XmlElement(name = "printed-document-template")
    public ObservableList<PrintedDocumentTemplate> getPrintedDocumentTemplates() {
        return printedDocumentTemplatesProperty().get();
    }

    public void setPrintedDocumentTemplates(ObservableList<PrintedDocumentTemplate> list) {
        printedDocumentTemplatesProperty().set(list);
    }

    public ListProperty<PrintedDocumentTemplate> printedDocumentTemplatesProperty() {
        return printedDocumentTemplates;
    }

    protected void convertDocumentTemplates(ObservableList<DocumentTemplate> list) {
        printedDocumentTemplates.clear();
        for (DocumentTemplate documentTemplate : list) {
            printedDocumentTemplates.add(new PrintedDocumentTemplate(documentTemplate));
        }
    }

    @Override
    public String toString() {
        return nameProperty().get();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PrintedTemplate)) {
            return false;
        }

        if (this.getUid().equals(((PrintedTemplate) obj).getUid()))
            return true;

        return false;
    }

    public PrintedTemplate copy() {
        PrintedTemplate copy = new PrintedTemplate();
        copy = new PrintedTemplate();
        copy.setName(getName());
        copy.setTitle(getTitle());
        copy.setPrompt(getPrompt());
        for (PrintedDocumentTemplate originalPrintedDocumentTemplate : getPrintedDocumentTemplates()) {
            copy.getPrintedDocumentTemplates().add(originalPrintedDocumentTemplate.copy());
        }
        return copy;
    }
}
