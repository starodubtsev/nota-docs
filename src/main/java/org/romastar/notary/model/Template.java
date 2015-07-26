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
 * Created by roman on 30.07.14.
 */
@XmlType(propOrder = {"uid", "name", "templateDocuments"}, name = "template")
public class Template {

    private StringProperty uid;

    private StringProperty name;

    private ListProperty<DocumentTemplate> templateDocuments = new SimpleListProperty<>(FXCollections.observableArrayList());

    public Template() {
        setUid(UUID.randomUUID().toString());
    }

    public Template(ObservableList<Document> documents) {
        this();
        for (Document document : documents) {
            DocumentTemplate documentTemplate = new DocumentTemplate(document);
            getTemplateDocuments().add(documentTemplate);
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

    public StringProperty nameProperty() {
        if (name == null)
            name = new SimpleStringProperty();
        return name;
    }

    public String getName() {
        return nameProperty().get();
    }

    public void setName(String value) {
        nameProperty().set(value);
    }

    @XmlElement(name = "document-template")
    public ObservableList<DocumentTemplate> getTemplateDocuments() {
        return templateDocuments.get();
    }

    public void setTemplateDocuments(ObservableList<DocumentTemplate> list) {
        templateDocuments.set(list);
    }

    public ListProperty<DocumentTemplate> templateDocumentsProperty() {
        return templateDocuments;
    }

    public DocumentTemplate getDocumentTemplateByDocumentUid(String documentUid) {
        for (DocumentTemplate documentTemplate : templateDocuments) {
            if (documentTemplate.getDocument().getUid().equals(documentUid))
                return documentTemplate;
        }
        return null;
    }

    @Override
    public String toString() {
        return getName();
    }
}
