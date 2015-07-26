package org.romastar.notary.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;

import javax.xml.bind.annotation.XmlType;

/**
 * Created by roman on 30.07.14.
 */
@XmlType(propOrder = {"document", "selected"}, name = "document-template")
public class DocumentTemplate {

    private ObjectProperty<Document> document;

    private BooleanProperty selected = new SimpleBooleanProperty(false);

    public DocumentTemplate() {

    }

    public DocumentTemplate(Document document) {
        this();
        setDocument(document);
    }

    public DocumentTemplate(Document document, boolean selected) {
        this(document);
        setSelected(selected);
    }

    public ObjectProperty<Document> documentProperty() {
        if (document == null)
            document = new SimpleObjectProperty<>();
        return document;
    }

    public Document getDocument() {
        return (Document) documentProperty().get();
    }

    public void setDocument(Document value) {
        documentProperty().set(value);
    }

    public BooleanProperty selectedProperty() {
        return selected;
    }

    public boolean isSelected() {
        return selectedProperty().get();
    }

    public void setSelected(boolean value) {
        selectedProperty().set(value);
    }


}
