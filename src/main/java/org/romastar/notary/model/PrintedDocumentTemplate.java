package org.romastar.notary.model;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by roman on 06.08.14.
 */
@XmlType(propOrder = {"documentTemplate", "additionalInfo", "documentFullInfo"}, name = "printed-document-template")
public class PrintedDocumentTemplate {

    private ObjectProperty<DocumentTemplate> documentTemplate = new SimpleObjectProperty<>();

    private StringProperty additionalInfo = new SimpleStringProperty("");

    private StringProperty documentFullInfo = new SimpleStringProperty();

    public PrintedDocumentTemplate() {
    }

    public PrintedDocumentTemplate(DocumentTemplate documentTemplate) {
        setDocumentTemplate(documentTemplate);
        documentFullInfoProperty().bind(Bindings.concat(documentTemplate.getDocument().getName().concat(
                        documentTemplate.getDocument().getDescription() == null || documentTemplate.getDocument().getDescription().trim().isEmpty() ? ""
                                : " (".concat(documentTemplate.getDocument().getDescription().concat(")")))
        ));
    }

    public ObjectProperty<DocumentTemplate> documentTemplateProperty() {
        return documentTemplate;
    }

    @XmlElement(name = "document-template")
    public DocumentTemplate getDocumentTemplate() {
        return documentTemplateProperty().get();
    }

    public void setDocumentTemplate(DocumentTemplate value) {
        documentTemplateProperty().set(value);
    }

    public StringProperty additionalInfoProperty() {
        return additionalInfo;
    }

    @XmlElement(name = "additional-info")
    public String getAdditionalInfo() {
        return additionalInfoProperty().get();
    }

    public void setAdditionalInfo(String value) {
        additionalInfoProperty().set(value);
    }

    public StringProperty documentFullInfoProperty() {
        return documentFullInfo;
    }

    @XmlElement(name = "document-full-info")
    public String getDocumentFullInfo() {
        return documentFullInfoProperty().get();
    }

    public void setDocumentFullInfo(String value) {
        documentFullInfoProperty().set(value);
    }

    public boolean isSelected() {
        return documentTemplateProperty().get().isSelected();
    }

    public PrintedDocumentTemplate copy() {
        PrintedDocumentTemplate copy = new PrintedDocumentTemplate(getDocumentTemplate());
        copy.setAdditionalInfo(getAdditionalInfo());
        return copy;
    }
}
