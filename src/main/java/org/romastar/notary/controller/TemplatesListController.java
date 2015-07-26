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
import org.romastar.notary.model.PrintedTemplate;
import org.romastar.notary.model.Template;

/**
 * Created by roman on 29.07.14.
 */
public class TemplatesListController implements IChildController, IHasParentController, IListController {

    private AppController parentController;

    private ObservableList<Template> data;

    private BooleanProperty editingStarted = new SimpleBooleanProperty(false);
    private BooleanProperty itemSelected = new SimpleBooleanProperty(false);

    private boolean editorListenersAdded = false;

    @FXML
    private ListView<Template> listContent;
    @FXML
    private Button btnAddTemplate;
    @FXML
    private Button btnRemoveTemplate;
    @FXML
    private Button btnSaveTemplates;
    @FXML
    private Button btnRevertTemplatesChanges;
    @FXML
    private Button btnPreview;

    @FXML
    private void initialize() {
        listContent.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        btnSaveTemplates.disableProperty().bind(Bindings.not(editingStarted));
        btnRevertTemplatesChanges.disableProperty().bind(Bindings.not(editingStarted));
        btnRemoveTemplate.disableProperty().bind(Bindings.not(itemSelected));
        btnPreview.disableProperty().bind(Bindings.not(itemSelected));

        initHandlers();
    }

    protected void initHandlers() {
        listContent.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Template>() {
            @Override
            public void changed(ObservableValue<? extends Template> observable, Template oldValue, Template newValue) {
                if (newValue != null) {
                    parentController.showRightScreen(ScreensEnum.TEMPLATE_EDITOR);
                    setEditorData(newValue);
                    itemSelected.set(true);
                    getEditorController().changeShowOnlyChecked(true);
                    getEditorController().clearFilter();
                }
            }
        });

        btnAddTemplate.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (AppModel.getInstance().getDocumentsList().getDocuments().size() == 0) {
                    Dialogs.create()
                            .title(AppModel.getInstance().messagesProperties.getProperty("WARNING_TITLE"))
                            .message(AppModel.getInstance().messagesProperties.getProperty("DOCUMENTS_LIST_IS_EMPTY"))
                            .showWarning();
                    return;
                }

                Template newTemplate = new Template(AppModel.getInstance().getDocumentsList().getDocuments());

                AppModel.getInstance().getTemplatesList().addTemplate(newTemplate);
                listContent.getSelectionModel().select(newTemplate);

                parentController.showRightScreen(ScreensEnum.TEMPLATE_EDITOR);
                setEditorData(newTemplate);
                editingStarted.set(true);
                getEditorController().changeShowOnlyChecked(false);
                getEditorController().clearFilter();
            }
        });

        btnRemoveTemplate.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Template selectedTemplate = null;
                if (listContent.getSelectionModel().getSelectedItems().size() == 1) {
                    selectedTemplate = listContent.getSelectionModel().getSelectedItems().get(0);
                }

                if (selectedTemplate != null) {
                    String message = String.format(AppModel.getInstance().messagesProperties.getProperty("DELETE_TEMPLATE"), selectedTemplate.getName());
                    Action action = Dialogs.create().style(DialogStyle.NATIVE).message(message).showConfirm();
                    if (action == Dialog.Actions.YES) {
                        listContent.getItems().remove(selectedTemplate);
                        refresh();
                        listContent.getSelectionModel().select(0);
                        setEditorData(listContent.getSelectionModel().getSelectedItems().get(0));
                        editingStarted.set(true);
                        itemSelected.set(listContent.getSelectionModel().getSelectedItems().size() != 0);
                    }
                }
            }
        });

        btnRevertTemplatesChanges.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String message = AppModel.getInstance().messagesProperties.getProperty("REVERT_ALL_CHANGES");
                Action action = Dialogs.create().style(DialogStyle.NATIVE).message(message).showConfirm();
                if (action == Dialog.Actions.YES) {
                    int selectedIndex = listContent.getSelectionModel().getSelectedIndices().size() > 0 ? listContent.getSelectionModel().getSelectedIndices().get(0) : 0;
                    AppModel.getInstance().loadTemplatesList();
                    setData(AppModel.getInstance().getTemplatesList().getTemplates());

                    if (selectedIndex == -1 || selectedIndex >= listContent.getItems().size())
                        selectedIndex = 0;

                    Template template = listContent.getItems().get(selectedIndex);
                    listContent.getSelectionModel().select(template);
                    parentController.showRightScreen(ScreensEnum.TEMPLATE_EDITOR);
                    setEditorData(template);

                    editingStarted.set(false);
                    itemSelected.set(true);
                }

            }
        });

        btnSaveTemplates.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                AppModel.getInstance().saveTemplatesList();
                editingStarted.set(false);
                getEditorController().changeShowOnlyChecked(true);
                getEditorController().clearFilter();
            }
        });

        btnPreview.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                parentController.showPrintPreview(ScreensEnum.TEMPLATE_PREVIEW, new PrintedTemplate(listContent.getSelectionModel().getSelectedItems().get(0)), false);
            }
        });
    }

    protected void addEditorListeners() {
        Parent editorController = (Parent) getEditorController();
        editorController.addEventHandler(EditorEvent.ITEM_ADDED, new EventHandler<EditorEvent>() {
            @Override
            public void handle(EditorEvent event) {
                if (event.getView() == ScreensEnum.TEMPLATE_EDITOR) {
                    editingStarted.set(true);
                }
            }
        });
        editorController.addEventHandler(EditorEvent.ITEM_CHANGED, new EventHandler<EditorEvent>() {
            @Override
            public void handle(EditorEvent event) {
                if (event.getView() == ScreensEnum.TEMPLATE_EDITOR) {
                    editingStarted.set(true);
                }
            }
        });
    }

    @Override
    public void setData(Object data) {
        this.data = (ObservableList<Template>) data;
        listContent.setItems(this.data);
    }

    @Override
    public void refresh() {
        listContent.setItems(null);
        listContent.setItems(data);
    }

    protected void setEditorData(Template template) {
        getEditorController().setData(template);
        if (!editorListenersAdded)
            addEditorListeners();
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
        return ScreensEnum.TEMPLATE_EDITOR;
    }

    public TemplateEditorController getEditorController() {
        return ((TemplateEditorController) parentController.getChildController(ScreensEnum.TEMPLATE_EDITOR));
    }
}
