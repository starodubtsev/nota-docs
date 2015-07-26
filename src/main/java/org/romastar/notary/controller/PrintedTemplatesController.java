package org.romastar.notary.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;
import org.romastar.notary.interfaces.IChildController;
import org.romastar.notary.interfaces.IHasParentController;
import org.romastar.notary.interfaces.IListController;
import org.romastar.notary.model.AppModel;
import org.romastar.notary.model.PrintedTemplate;

/**
 * Created by roman on 07.08.14.
 */
public class PrintedTemplatesController implements IChildController, IHasParentController, IListController {

    private AppController parentController;

    private ObservableList<PrintedTemplate> data;

    @FXML
    private ListView<PrintedTemplate> listContent;
    @FXML
    private Button btnPreview;
    @FXML
    private Button btnRemove;
    @FXML
    private Button btnRemoveAll;

    @FXML
    private void initialize() {
        listContent.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        btnPreview.disableProperty().set(true);
        btnRemove.disableProperty().set(true);
        btnRemoveAll.disableProperty().set(true);

        listContent.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<PrintedTemplate>() {
            @Override
            public void changed(ObservableValue<? extends PrintedTemplate> observable, PrintedTemplate oldValue, PrintedTemplate newValue) {
                btnPreview.disableProperty().set(newValue == null);
                btnRemove.disableProperty().set(newValue == null);
                btnRemoveAll.disableProperty().set(listContent.itemsProperty() == null || listContent.itemsProperty().get() == null || listContent.itemsProperty().get().size() == 0);
            }
        });

        listContent.itemsProperty().addListener(new ChangeListener<ObservableList<PrintedTemplate>>() {
            @Override
            public void changed(ObservableValue<? extends ObservableList<PrintedTemplate>> observable, ObservableList<PrintedTemplate> oldValue, ObservableList<PrintedTemplate> newValue) {
                btnRemoveAll.disableProperty().set(newValue == null || newValue.size() == 0);
                btnPreview.disableProperty().set(newValue == null);
                btnRemove.disableProperty().set(newValue == null);
            }
        });

        btnPreview.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                parentController.showPrintPreview(ScreensEnum.TEMPLATE_PREVIEW, listContent.getSelectionModel().getSelectedItems().get(0), true);
            }
        });

        btnRemove.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Action action = Dialogs.create()
                        .message(String.format(AppModel.getInstance().messagesProperties.getProperty("DELETE_PRINTED_TEMPLATE"), listContent.getSelectionModel().getSelectedItems().get(0)))
                        .title(AppModel.getInstance().messagesProperties.getProperty("DELETE_TITLE"))
                        .showConfirm();
                if (action == Dialog.Actions.YES) {
                    listContent.getItems().remove(listContent.getSelectionModel().getSelectedItems().get(0));
                    AppModel.getInstance().savePrintedTemplatesList();
                    refresh();

                    listContent.getSelectionModel().select(0);
                    btnPreview.disableProperty().set(listContent.getSelectionModel().getSelectedItems().size() == 0);
                }
            }
        });

        btnRemoveAll.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Action action = Dialogs.create()
                        .message(AppModel.getInstance().messagesProperties.getProperty("DELETE_ALL_PRINTED_TEMPLATE"))
                        .title(AppModel.getInstance().messagesProperties.getProperty("DELETE_TITLE"))
                        .showConfirm();
                if (action == Dialog.Actions.YES) {
                    listContent.getItems().clear();
                    AppModel.getInstance().savePrintedTemplatesList();
                    refresh();
                    btnRemoveAll.disableProperty().set(true);
                }
            }
        });
    }

    @Override
    public void setData(Object data) {
        this.data = (ObservableList<PrintedTemplate>) data;
        listContent.setItems(this.data);
    }

    @Override
    public void setParentController(AppController parentController) {
        this.parentController = parentController;
    }

    @Override
    public void refresh() {
        listContent.setItems(null);
        listContent.setItems(data);
    }

    @Override
    public ListView getContentListView() {
        return listContent;
    }

    @Override
    public ScreensEnum getEditorScreenIdentifier() {
        return null;
    }
}
