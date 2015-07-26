package org.romastar.notary.controller;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import org.romastar.notary.events.EditorEvent;
import org.romastar.notary.interfaces.IChildController;
import org.romastar.notary.interfaces.IHasParentController;
import org.romastar.notary.model.DocumentTemplate;
import org.romastar.notary.model.Template;

import java.util.function.Predicate;

/**
 * Created by roman on 30.07.14.
 */
public class TemplateEditorController extends AnchorPane implements IChildController, IHasParentController {

    private ObjectProperty<Template> data = new SimpleObjectProperty<>();

    private AppController parentController;

    @FXML
    private TextField txtTemplateName;
    @FXML
    private TextField txtDocumentsFilter;
    @FXML
    private CheckBox checkShowOnlyChecked;
    @FXML
    private TableView<DocumentTemplate> tableTemplateDocuments;

    @FXML
    private TableColumn<DocumentTemplate, Boolean> columnSelected;
    @FXML
    private TableColumn<DocumentTemplate, String> columnDocument;


    @FXML
    private void initialize() {
        txtTemplateName.disableProperty().bind(Bindings.isNull(data));
        tableTemplateDocuments.disableProperty().bind(Bindings.isNull(data));

        tableTemplateDocuments.setEditable(true);

        tableTemplateDocuments.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        columnSelected.setEditable(true);
        columnSelected.setPrefWidth(40);
        columnSelected.setCellValueFactory(new PropertyValueFactory<DocumentTemplate, Boolean>("selected"));
        columnSelected.setCellFactory(CheckBoxTableCell.forTableColumn(columnSelected));

        DoubleBinding columnDocumentWidth = tableTemplateDocuments.widthProperty().subtract(columnSelected.widthProperty());
        columnDocument.prefWidthProperty().bind(columnDocumentWidth);
        columnDocument.setEditable(false);
        columnDocument.setCellValueFactory(new PropertyValueFactory<DocumentTemplate, String>("document"));

        txtTemplateName.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                parentController.getChildController(ScreensEnum.TEMPLATES_LIST).refresh();
            }
        });

        txtTemplateName.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode().isLetterKey() || event.getCode().isDigitKey() || event.getCode() == KeyCode.BACK_SPACE || event.getCode() == KeyCode.DELETE || event.getCode() == KeyCode.SPACE) {
                    Event.fireEvent(TemplateEditorController.this, new EditorEvent(EditorEvent.ITEM_CHANGED, ScreensEnum.TEMPLATE_EDITOR));
                }
            }
        });

        txtDocumentsFilter.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!oldValue.equalsIgnoreCase(newValue))
                    refreshDocumentsList();
            }
        });

        checkShowOnlyChecked.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (oldValue != newValue)
                    refreshDocumentsList();
            }
        });
    }

    @Override
    public void setData(Object data) {
        if (this.data.get() != null) {
            txtTemplateName.textProperty().unbindBidirectional(this.data.get().nameProperty());
        }

        this.data.set((Template) data);
        if (this.data.get() != null) {
            for (DocumentTemplate doc : ((Template) data).getTemplateDocuments()) {
                doc.selectedProperty().addListener(new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                        if (oldValue != newValue) {
                            Event.fireEvent(TemplateEditorController.this, new EditorEvent(EditorEvent.ITEM_CHANGED, ScreensEnum.TEMPLATE_EDITOR));
                            refreshDocumentsList();
                        }
                    }
                });
            }

            txtTemplateName.textProperty().bindBidirectional(this.data.get().nameProperty());
            tableTemplateDocuments.setItems(getTableData());

            refreshDocumentsList();
        } else {
            txtTemplateName.setText("");
            tableTemplateDocuments.setItems(null);
        }
    }

    protected void refreshDocumentsList() {
        if (data == null || data.get().getTemplateDocuments() == null || data.get().getTemplateDocuments().size() == 0)
            return;

        ObservableList<DocumentTemplate> filteredList = data.get().getTemplateDocuments().filtered(new Predicate<DocumentTemplate>() {
            @Override
            public boolean test(DocumentTemplate documentTemplate) {
                if (txtDocumentsFilter.getText() == null || txtDocumentsFilter.getText().trim().isEmpty()) {
                    return isVisibleByCheckBoxShowOnlyChecked(documentTemplate);
                }

                return documentTemplate.getDocument().getName().toLowerCase().indexOf(txtDocumentsFilter.getText().toLowerCase()) != -1 && isVisibleByCheckBoxShowOnlyChecked(documentTemplate);
            }
        });
        tableTemplateDocuments.setItems(filteredList);
    }

    ;


    protected boolean isVisibleByCheckBoxShowOnlyChecked(DocumentTemplate documentTemplate) {
        return checkShowOnlyChecked.isSelected() == true ? documentTemplate.isSelected() : true;
    }

    @Override
    public void refresh() {
        tableTemplateDocuments.setItems(null);
        tableTemplateDocuments.setItems(getTableData());
    }

    private ObservableList<DocumentTemplate> getTableData() {
        return data.get().getTemplateDocuments();
    }

    @Override
    public void setParentController(AppController parentController) {
        this.parentController = parentController;
    }

    public void clearFilter() {
        txtDocumentsFilter.setText("");
    }

    public void changeShowOnlyChecked(boolean selected) {
        checkShowOnlyChecked.selectedProperty().set(selected);
    }
}
