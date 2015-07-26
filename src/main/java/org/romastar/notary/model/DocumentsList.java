package org.romastar.notary.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by roman on 26.07.14.
 */
@XmlRootElement(name = "documents")
public class DocumentsList {

    public static final String FILE = "documents.xml";

    private ObservableList<Document> documents = FXCollections.observableArrayList();


    public ObservableList<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(ObservableList<Document> documents) {
        this.documents = documents;
    }

    public boolean addDocument(Document document) {
        return documents.add(document);
    }

    public boolean removeDocument(Document document) {
        return documents.remove(document);
    }
}
