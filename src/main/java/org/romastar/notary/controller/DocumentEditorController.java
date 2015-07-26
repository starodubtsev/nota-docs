package org.romastar.notary.controller;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import org.romastar.notary.events.EditorEvent;
import org.romastar.notary.interfaces.IChildController;
import org.romastar.notary.interfaces.IHasParentController;
import org.romastar.notary.model.Document;

/**
 * Created by roman on 26.07.14.
 */
public class DocumentEditorController extends AnchorPane implements IChildController, IHasParentController {

    @FXML
    private TextField txtName;
    @FXML
    private TextArea txtDescription;

    private AppController parentController;

    private ObjectProperty<Document> document = new SimpleObjectProperty<>();

    @FXML
    private void initialize() {

        txtName.disableProperty().bind(Bindings.isNull(document));
        txtDescription.disableProperty().bind(Bindings.isNull(document));

        txtName.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                parentController.getChildController(ScreensEnum.DOCUMENTS_LIST).refresh();
            }
        });

        txtName.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode().isLetterKey() || event.getCode().isDigitKey() || event.getCode() == KeyCode.BACK_SPACE || event.getCode() == KeyCode.DELETE || event.getCode() == KeyCode.SPACE) {
                    Event.fireEvent(DocumentEditorController.this, new EditorEvent(EditorEvent.ITEM_CHANGED, ScreensEnum.DOCUMENT_EDITOR));
                }
            }
        });

        txtDescription.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                parentController.getChildController(ScreensEnum.DOCUMENTS_LIST).refresh();
            }
        });

        txtDescription.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode().isLetterKey() || event.getCode().isDigitKey() || event.getCode() == KeyCode.BACK_SPACE || event.getCode() == KeyCode.DELETE || event.getCode() == KeyCode.SPACE) {
                    Event.fireEvent(DocumentEditorController.this, new EditorEvent(EditorEvent.ITEM_CHANGED, ScreensEnum.DOCUMENT_EDITOR));
                }
            }
        });
    }

    @Override
    public void setData(Object data) {
        if (document.get() != null) {
            txtName.textProperty().unbindBidirectional(document.get().nameProperty());
            txtDescription.textProperty().unbindBidirectional(document.get().descriptionProperty());
        }

        document.set((Document) data);
        if (document.get() != null) {
            txtName.textProperty().bindBidirectional(document.get().nameProperty());
            txtDescription.textProperty().bindBidirectional(document.get().descriptionProperty());
        } else {
            txtName.setText("");
            txtDescription.setText("");
        }
    }

    @Override
    public void setParentController(AppController parentController) {
        this.parentController = parentController;
    }

}
