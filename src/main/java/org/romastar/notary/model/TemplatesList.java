package org.romastar.notary.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by roman on 30.07.14.
 */
@XmlRootElement(name = "templates")
public class TemplatesList {

    public static final String FILE = "templates.xml";

    private ObservableList<Template> templates = FXCollections.observableArrayList();


    public ObservableList<Template> getTemplates() {
        return templates;
    }

    public void setTemplates(ObservableList<Template> templates) {
        this.templates = templates;
    }

    public boolean addTemplate(Template template) {
        return templates.add(template);
    }

    public boolean removeTemplate(Template template) {
        return templates.remove(template);
    }
}
