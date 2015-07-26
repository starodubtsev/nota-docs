package org.romastar.notary.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by roman on 06.08.14.
 */
@XmlRootElement(name = "printed-templates")
public class PrintedTemplatesList {

    public static final String FILE = "printed-templates.xml";

    private ObservableList<PrintedTemplate> printedTemplates = FXCollections.observableArrayList();

    @XmlElement(name = "printed-template")
    public ObservableList<PrintedTemplate> getPrintedTemplates() {
        return printedTemplates;
    }

    public void setPrintedTemplates(ObservableList<PrintedTemplate> printedTemplates) {
        this.printedTemplates = printedTemplates;
    }

}
