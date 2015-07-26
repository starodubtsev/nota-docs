package org.romastar.notary.controller;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.DialogStyle;
import org.controlsfx.dialog.Dialogs;
import org.romastar.notary.events.EditorEvent;
import org.romastar.notary.interfaces.IChildController;
import org.romastar.notary.interfaces.IHasParentController;
import org.romastar.notary.interfaces.IListController;
import org.romastar.notary.model.AppModel;
import org.romastar.notary.model.Document;

/**
 * Created by roman on 29.07.14.
 */
public class DocumentsListController implements IChildController, IHasParentController, IListController {

    private AppController parentController;

    private ObservableList<Document> data;

    private boolean editorListenersAdded = false;

    @FXML
    private ListView<Document> listContent;
    @FXML
    private Button btnAddDocument;
    @FXML
    private Button btnRemoveDocument;
    @FXML
    private Button btnSaveDocuments;
    @FXML
    private Button btnRevertDocumentsChanges;

    private BooleanProperty editingStarted = new SimpleBooleanProperty(false);
    private BooleanProperty itemSelected = new SimpleBooleanProperty(false);

    @FXML
    private void initialize() {
        listContent.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        btnSaveDocuments.disableProperty().bind(Bindings.not(editingStarted));
        btnRevertDocumentsChanges.disableProperty().bind(Bindings.not(editingStarted));
        btnRemoveDocument.disableProperty().bind(Bindings.not(itemSelected));

        initHandlers();
    }

    protected void initHandlers() {
        listContent.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Document>() {
            @Override
            public void changed(ObservableValue<? extends Document> observable, Document oldValue, Document newValue) {
                if (newValue != null) {
                    parentController.showRightScreen(ScreensEnum.DOCUMENT_EDITOR);
                    itemSelected.set(true);
                    setEditorData(newValue);
                }
            }
        });

        btnAddDocument.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Document newDocument = new Document();
                AppModel.getInstance().getDocumentsList().addDocument(newDocument);
                listContent.getSelectionModel().select(newDocument);
                parentController.showRightScreen(ScreensEnum.DOCUMENT_EDITOR);
                setEditorData(newDocument);
                editingStarted.set(true);
            }
        });

        btnRemoveDocument.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Document selectedDocument = null;
                if (listContent.getSelectionModel().getSelectedItems().size() == 1) {
                    selectedDocument = listContent.getSelectionModel().getSelectedItems().get(0);
                }

                if (selectedDocument != null) {
                    String message = String.format(AppModel.getInstance().messagesProperties.getProperty("DELETE_DOCUMENT"), selectedDocument.getName());
                    Action action = Dialogs.create().style(DialogStyle.NATIVE).message(message).showConfirm();
                    if (action == Dialog.Actions.YES) {
                        listContent.getItems().remove(selectedDocument);
                        refresh();
                        listContent.getSelectionModel().select(0);
                        setEditorData(listContent.getSelectionModel().getSelectedItems().get(0));
                        editingStarted.set(true);
                        itemSelected.set(listContent.getSelectionModel().getSelectedItems().size() > 0);
                    }
                }
            }
        });

        btnRevertDocumentsChanges.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String message = AppModel.getInstance().messagesProperties.getProperty("REVERT_ALL_CHANGES");
                Action action = Dialogs.create().style(DialogStyle.NATIVE).message(message).showConfirm();
                if (action == Dialog.Actions.YES) {
                    int selectedIndex = listContent.getSelectionModel().getSelectedIndices().size() > 0 ? listContent.getSelectionModel().getSelectedIndices().get(0) : 0;
                    AppModel.getInstance().loadDocumentsList();
                    setData(AppModel.getInstance().getDocumentsList().getDocuments());

                    if (selectedIndex == -1 || selectedIndex >= listContent.getItems().size())
                        selectedIndex = 0;

                    Document doc = listContent.getItems().get(selectedIndex);
                    listContent.getSelectionModel().select(doc);
                    parentController.showRightScreen(ScreensEnum.DOCUMENT_EDITOR);
                    setEditorData(doc);

                    editingStarted.set(false);
                    itemSelected.set(true);
                }

            }
        });

        btnSaveDocuments.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                AppModel.getInstance().saveDocumentsList();

                if (AppModel.getInstance().getTemplatesList().getTemplates().size() > 0) {
                    String message = AppModel.getInstance().messagesProperties.getProperty("SAVE_DOCUMENT_IN_ALL_TEMPLATES");
                    Action action = Dialogs.create().style(DialogStyle.NATIVE).message(message).showConfirm();
                    if (action == Dialog.Actions.YES) {
                        AppModel.getInstance().updateTemplatesList();
                        AppModel.getInstance().saveTemplatesList();
                    }
                }
                editingStarted.set(false);
            }
        });
    }

    protected void addEditorListeners() {
        Parent editorController = (Parent) getEditorController();
        editorController.addEventHandler(EditorEvent.ITEM_ADDED, new EventHandler<EditorEvent>() {
            @Override
            public void handle(EditorEvent event) {
                if (event.getView() == ScreensEnum.DOCUMENT_EDITOR) {
                    editingStarted.set(true);
                }
            }
        });
        editorController.addEventHandler(EditorEvent.ITEM_CHANGED, new EventHandler<EditorEvent>() {
            @Override
            public void handle(EditorEvent event) {
                if (event.getView() == ScreensEnum.DOCUMENT_EDITOR) {
                    editingStarted.set(true);
                }
            }
        });
    }

    @Override
    public void setData(Object data) {
        this.data = (ObservableList<Document>) data;
        listContent.setItems(this.data);
    }

    @Override
    public void refresh() {
        listContent.setItems(null);
        listContent.setItems(data);
    }

    protected void setEditorData(Document document) {
        getEditorController().setData(document);
        if (!editorListenersAdded)
            addEditorListeners();
    }

    protected IChildController getEditorController() {
        return parentController.getChildController(ScreensEnum.DOCUMENT_EDITOR);
    }

    @Override
    public void setParentController(AppController parentController) {
        this.parentController = parentController;
    }

    @Override
    public ListView getContentListView() {
        return listContent;
    }

    @Override
    public ScreensEnum getEditorScreenIdentifier() {
        return ScreensEnum.DOCUMENT_EDITOR;
    }
}
