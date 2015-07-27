package org.romastar.notary.controller;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.print.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.Callback;
import org.controlsfx.dialog.Dialogs;
import org.romastar.notary.controls.TextAreaTableCell;
import org.romastar.notary.interfaces.IChildController;
import org.romastar.notary.model.AppModel;
import org.romastar.notary.model.DocumentTemplate;
import org.romastar.notary.model.PrintedDocumentTemplate;
import org.romastar.notary.model.PrintedTemplate;
import org.romastar.notary.utils.HtmlUtils;

import java.util.Optional;
import java.util.function.Predicate;

enum PreviewStackViews {
    TABLE_VIEW,
    WEB_VIEW
}

/**
 * Created by roman on 01.08.14.
 */
public class PreviewController extends BorderPane implements IChildController {

    private PrintedTemplate template;

    private BooleanProperty viewOnly = new SimpleBooleanProperty(false);
    private ObjectProperty<PreviewStackViews> currentView = new SimpleObjectProperty<>(PreviewStackViews.TABLE_VIEW);

    @FXML
    private StackPane stackPane;
    @FXML
    private TableView<PrintedDocumentTemplate> tablePrintPreview;
    @FXML
    private TableColumn columnDocument;
    @FXML
    private TableColumn columnComments;

    @FXML
    private WebView webView;
    @FXML
    private VBox boxTableView;

    @FXML
    private TextArea textTitle;
    @FXML
    private TextArea textPrompt;

    @FXML
    private HBox boxButtons;
    @FXML
    private Button btnNext;
    @FXML
    private Button btnPrevious;
    @FXML
    private Button btnPrint;
    @FXML
    private Button btnSave;

    @FXML
    private void initialize() {

        currentView.addListener(new ChangeListener<PreviewStackViews>() {
            @Override
            public void changed(ObservableValue<? extends PreviewStackViews> observable, PreviewStackViews oldValue, PreviewStackViews newValue) {
                if (newValue == PreviewStackViews.WEB_VIEW)
                    updateWebView();
            }
        });

        webView.visibleProperty().bind(Bindings.equal(currentView, PreviewStackViews.WEB_VIEW));
        boxTableView.visibleProperty().bind(Bindings.equal(currentView, PreviewStackViews.TABLE_VIEW));

        btnSave.visibleProperty().bind(Bindings.equal(currentView, PreviewStackViews.WEB_VIEW).and(Bindings.not(viewOnlyProperty())));
        btnSave.managedProperty().bind(Bindings.equal(currentView, PreviewStackViews.WEB_VIEW).and(Bindings.not(viewOnlyProperty())));

        btnPrint.visibleProperty().bind(Bindings.equal(currentView, PreviewStackViews.WEB_VIEW));
        btnPrint.managedProperty().bind(Bindings.equal(currentView, PreviewStackViews.WEB_VIEW));

        btnNext.visibleProperty().bind(Bindings.equal(currentView, PreviewStackViews.TABLE_VIEW).and(Bindings.not(viewOnlyProperty())));
        btnNext.managedProperty().bind(Bindings.equal(currentView, PreviewStackViews.TABLE_VIEW).and(Bindings.not(viewOnlyProperty())));

        btnPrevious.visibleProperty().bind(Bindings.equal(currentView, PreviewStackViews.WEB_VIEW).and(Bindings.not(viewOnlyProperty())));
        btnPrevious.managedProperty().bind(Bindings.equal(currentView, PreviewStackViews.WEB_VIEW).and(Bindings.not(viewOnlyProperty())));

        tablePrintPreview.setEditable(true);
        tablePrintPreview.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        columnDocument.setEditable(false);
        columnDocument.setCellValueFactory(new PropertyValueFactory<DocumentTemplate, String>("documentFullInfo"));
        columnDocument.setCellFactory(new Callback<TableColumn, TableCell>() {
            @Override
            public TableCell call(TableColumn param) {
                final TableCell<DocumentTemplate, String> cell = new TableCell<DocumentTemplate, String>() {
                    private Text text;

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (!isEmpty()) {
                            text = new Text(item.toString());
                            text.setWrappingWidth(param.widthProperty().get()); // Setting the wrapping width to the Text
                            setGraphic(text);
                        } else {
                            setText(null);
                            setGraphic(null);
                        }
                    }
                };
                return cell;
            }
        });

        columnComments.setEditable(true);
        columnComments.setCellValueFactory(new PropertyValueFactory<DocumentTemplate, String>("additionalInfo"));
        columnComments.setCellFactory(new Callback<TableColumn, TableCell>() {
            @Override
            public TableCell call(TableColumn param) {
                return new TextAreaTableCell<DocumentTemplate, String>();
            }
        });

        btnNext.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                currentView.set(PreviewStackViews.WEB_VIEW);
            }
        });


        btnPrevious.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                currentView.set(PreviewStackViews.TABLE_VIEW);
            }
        });

        btnSave.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Optional<String> name = Dialogs.create()
                        .title(AppModel.getInstance().messagesProperties.get("SAVE_PRINTING_TEMPLATE_TITLE").toString())
                        .message(AppModel.getInstance().messagesProperties.get("SAVE_PRINTING_TEMPLATE").toString())
                        .showTextInput(template.getName());

                if (name.isPresent()) {
                    if (!name.get().isEmpty() && !AppModel.getInstance().printedTemplateNameExists(name.get())) {
                        template.setName(name.get());
                        AppModel.getInstance().getPrintedTemplatesList().getPrintedTemplates().add(template);
                        AppModel.getInstance().savePrintedTemplatesList();
                    } else {
                        Dialogs.create()
                                .title(AppModel.getInstance().messagesProperties.get("SAVE_TITLE").toString())
                                .message(AppModel.getInstance().messagesProperties.get("SAVE_PRINTING_TEMPLATE_INVALID_NAME").toString())
                                .showWarning();
                    }
                }
            }
        });

        btnPrint.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                Printer printer = Printer.getDefaultPrinter();
                if (printer == null) {
                    Dialogs.create()
                            .message(AppModel.getInstance().messagesProperties.get("PRINT_FAILED_MESSAGE").toString())
                            .title(AppModel.getInstance().messagesProperties.get("PRINT_FAILED_TITLE").toString())
                            .showError();
                    return;
                }

                PrinterJob printerJob = PrinterJob.createPrinterJob();

                if (printerJob != null) {
                    boolean result = printerJob.showPrintDialog(webView.getScene().getWindow());
                    if (result) {
                        WebEngine webEngine = webView.getEngine();
                        webEngine.print(printerJob);
                        if (result) {
                            printerJob.endJob();
                        }
                    }
                } else {
                    Dialogs.create()
                            .message(AppModel.getInstance().messagesProperties.get("PRINT_FAILED_MESSAGE").toString())
                            .title(AppModel.getInstance().messagesProperties.get("PRINT_FAILED_TITLE").toString())
                            .showError();
                }
            }
        });

        stackPane.getParent().addEventFilter(KeyEvent.KEY_RELEASED, new PreviewKeyHandler());
    }

    protected BooleanProperty viewOnlyProperty() {
        return viewOnly;
    }

    public boolean isViewOnly() {
        return viewOnlyProperty().get();
    }

    public void setViewOnly(boolean viewOnly) {
        viewOnlyProperty().set(viewOnly);
        currentView.set(viewOnly ? PreviewStackViews.WEB_VIEW : PreviewStackViews.TABLE_VIEW);
    }

    @Override
    public void setData(Object data) {
        if (template != null) {
            textTitle.textProperty().unbindBidirectional(template.titleProperty());
            textPrompt.textProperty().unbindBidirectional(template.promptProperty());
        }

        template = (PrintedTemplate) data;
        tablePrintPreview.itemsProperty().set(null);
        FilteredList<PrintedDocumentTemplate> filteredList = template.getPrintedDocumentTemplates().filtered(new Predicate<PrintedDocumentTemplate>() {

            @Override
            public boolean test(PrintedDocumentTemplate printedDocumentTemplate) {
                return printedDocumentTemplate.isSelected();
            }
        });
        tablePrintPreview.itemsProperty().set(filteredList);

        textTitle.textProperty().bindBidirectional(template.titleProperty());
        textPrompt.textProperty().bindBidirectional(template.promptProperty());

        if (currentView.get() == PreviewStackViews.WEB_VIEW) {
            updateWebView();
        }

    }

    protected void updateWebView() {
        String report = AppModel.getInstance().reportHtmlTemplateProperty().get().replaceAll("<!--%HTML_ROWS_CONTENT%-->", HtmlUtils.templateToHtml(template));
        report = report.replaceAll("<!--%TITLE%-->", template.getTitle());
        report = report.replaceAll("<!--%PROMPT%-->", template.getPrompt());
        report = report.replaceAll("<!--%NOTARY-POSITION%-->", AppModel.getInstance().applicationProperties.getProperty("notary-position"));
        report = report.replaceAll("<!--%NOTARY-NAME%-->", AppModel.getInstance().applicationProperties.getProperty("notary-name"));
        webView.getEngine().loadContent(report);
    }

    private class PreviewKeyHandler implements EventHandler<KeyEvent> {
        @Override
        public void handle(KeyEvent event) {
            if (currentView.get() == PreviewStackViews.TABLE_VIEW) {
                if (event.getCode() == KeyCode.RIGHT && event.isControlDown() && event.isShiftDown())
                    btnNext.fire();
            } else if (currentView.get() == PreviewStackViews.WEB_VIEW) {
                if (event.getCode() == KeyCode.LEFT && event.isControlDown() && event.isShiftDown() && !isViewOnly())
                    btnPrevious.fire();
                else if (event.getCode() == KeyCode.P && event.isControlDown() && event.isShiftDown())
                    btnPrint.fire();
                else if (event.getCode() == KeyCode.S && event.isControlDown() && event.isShiftDown() && !isViewOnly())
                    btnSave.fire();
            }
        }
    }
}
