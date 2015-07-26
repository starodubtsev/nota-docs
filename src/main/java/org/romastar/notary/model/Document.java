package org.romastar.notary.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import javax.xml.bind.annotation.XmlType;
import java.util.UUID;

/**
 * Created by roman on 26.07.14.
 */
@XmlType(propOrder = {"uid", "name", "description"}, name = "document")
public class Document {

    private StringProperty uid;
    private StringProperty name = new SimpleStringProperty("");
    private StringProperty description = new SimpleStringProperty("");

    public Document() {
        setUid(UUID.randomUUID().toString());
    }

    public Document(String name) {
        this();
        setName(name);
    }

    public Document(String name, String description) {
        this(name);
        setDescription(description);
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
        return nameProperty().get();
    }

    public void setName(String name) {
        nameProperty().set(name);
    }

    public StringProperty nameProperty() {
        if (name == null) {
            name = new SimpleStringProperty();
        }
        return name;
    }


    public String getDescription() {
        return descriptionProperty().get();
    }

    public void setDescription(String description) {
        descriptionProperty().set(description);
    }

    public StringProperty descriptionProperty() {
        if (description == null) {
            description = new SimpleStringProperty();
        }
        return description;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Document)) {
            return false;
        }

        if (this.getUid().equals(((Document) obj).getUid()))
            return true;

        return false;
    }
}
