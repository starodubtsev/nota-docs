package org.romastar.notary.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.ListView;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import org.romastar.notary.interfaces.IChildController;
import org.romastar.notary.interfaces.IHasParentController;
import org.romastar.notary.interfaces.IListController;
import org.romastar.notary.model.AppModel;
import org.romastar.notary.model.PrintedTemplate;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class AppController {


    private Map<ScreensEnum, Node> mapScreens = new HashMap<>();
    private Map<ScreensEnum, IChildController> mapControllers = new HashMap<>();

    @FXML
    private Accordion accordion;
    @FXML
    private StackPane paneRight;

    private Stage dialog;

    @FXML
    private void initialize() {

        AppModel.getInstance().loadData();

        loadAllScreens();

        showLeftScreen(ScreensEnum.DOCUMENTS_LIST, 0);
        initDocumentsListData();

        showLeftScreen(ScreensEnum.TEMPLATES_LIST, 1);
        initTemplatesListData();

        showLeftScreen(ScreensEnum.PRINTED_TEMPLATES_LIST, 2);
        initPrintedTemplatesListData();

        accordion.expandedPaneProperty().addListener(new ChangeListener<TitledPane>() {
            @Override
            public void changed(ObservableValue<? extends TitledPane> observable, TitledPane oldValue, TitledPane newValue) {
                if (newValue != null) {
                    Node openedNode = newValue.getContent() instanceof StackPane ? ((StackPane) newValue.getContent()).getChildren().get(0) : newValue.getContent();
                    for (Map.Entry<ScreensEnum, Node> entry : mapScreens.entrySet()) {
                        if (entry.getValue() == openedNode) {
                            IListController listController = (IListController) mapControllers.get(entry.getKey());
                            if (listController instanceof IListController) {
                                ListView listView = ((IListController) listController).getContentListView();
                                ScreensEnum editorEdentifier = listController.getEditorScreenIdentifier();
                                showRightScreen(editorEdentifier);

                                Object selectedItem = null;
                                if (listView != null && listView.getItems() != null && listView.getItems().size() > 0) {
                                    selectedItem = listView.getSelectionModel().getSelectedItems().size() > 0
                                            ? listView.getSelectionModel().getSelectedItems().get(0)
                                            : (listView.getItems().size() > 0 ? listView.getItems().get(0) : null);
                                    if (selectedItem != null)
                                        listView.getSelectionModel().select(selectedItem);
                                    if (editorEdentifier != null)
                                        ((IChildController) getChildController(editorEdentifier)).setData(selectedItem);
                                }
                            }
                            break;
                        }
                    }
                }
            }
        });

        accordion.setExpandedPane(accordion.getPanes().get(2));
        showRightScreen(ScreensEnum.INFO);
    }

    private void loadAllScreens() {
        loadScreen(ScreensEnum.INFO);
        loadScreen(ScreensEnum.DOCUMENTS_LIST);
        loadScreen(ScreensEnum.DOCUMENT_EDITOR);
        loadScreen(ScreensEnum.TEMPLATES_LIST);
        loadScreen(ScreensEnum.TEMPLATE_EDITOR);
        loadScreen(ScreensEnum.PRINTED_TEMPLATES_LIST);
        loadScreen(ScreensEnum.TEMPLATE_PREVIEW);
    }

    protected void initDocumentsListData() {
        IChildController controller = getChildController(ScreensEnum.DOCUMENTS_LIST);
        if (controller != null)
            controller.setData(AppModel.getInstance().getDocumentsList().getDocuments());
    }

    protected void initTemplatesListData() {
        IChildController controller = getChildController(ScreensEnum.TEMPLATES_LIST);
        if (controller != null)
            controller.setData(AppModel.getInstance().getTemplatesList().getTemplates());
    }

    protected void initPrintedTemplatesListData() {
        IChildController controller = getChildController(ScreensEnum.PRINTED_TEMPLATES_LIST);
        if (controller != null)
            controller.setData(AppModel.getInstance().getPrintedTemplatesList().getPrintedTemplates());
    }

    public Node getChildScreen(ScreensEnum screen) {
        if (!mapScreens.containsKey(screen))
            loadScreen(screen);

        return mapScreens.get(screen);
    }

    public IChildController getChildController(ScreensEnum screen) {
        return mapControllers.get(screen);
    }

    protected Node loadScreen(ScreensEnum screen) {
        Parent node = null;
        try {
            if (mapScreens.containsKey(screen)) {
                node = (Parent) mapScreens.get(screen);
                return node;
            }

            URL url = getClass().getResource(screen.getResource());

            AppLogger.logInfo("Path to loaded scren: " + url.toString());

            FXMLLoader loader = new FXMLLoader(url);
            node = loader.load();

            IChildController controller = (IChildController) loader.getController();
            if (controller instanceof IHasParentController)
                ((IHasParentController) controller).setParentController(this);

            mapScreens.put(screen, node);
            mapControllers.put(screen, controller);
        } catch (IOException ex) {
            AppLogger.processException(ex);
        } finally {
            return node;
        }
    }

    public void showLeftScreen(ScreensEnum screensEnum, int accordionPanelIndex) {

        Node node = getChildScreen(screensEnum);

        TitledPane pane = accordion.getPanes().get(accordionPanelIndex);
        Pane content = (Pane) pane.getContent();
        content.getChildren().clear();
        content.getChildren().add(node);
    }

    protected Stage getPrintPreview() {
        Parent parent = (Parent) loadScreen(ScreensEnum.TEMPLATE_PREVIEW);
        Stage dialog = new Stage(StageStyle.UNIFIED);
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.initOwner(accordion.getScene().getWindow());
        dialog.setResizable(false);
        Scene scene = new Scene(parent);
        dialog.setScene(scene);
        return dialog;
    }

    public void showPrintPreview(ScreensEnum forScreen, PrintedTemplate printedTemplate, boolean viewOnly) {
        if (dialog == null) {
            dialog = getPrintPreview();
            dialog.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    dialog.close();
                }
            });
        }
        PreviewController controller = (PreviewController) getChildController(forScreen);
        controller.setData(printedTemplate.copy());
        controller.setViewOnly(viewOnly);
        dialog.showAndWait();
    }

    public void showRightScreen(ScreensEnum screensEnum) {
        Node node = getChildScreen(screensEnum);

        if (paneRight.getChildren().size() > 0) {
            paneRight.getChildren().remove(0);
        }

        if (node != null)
            paneRight.getChildren().add(node);
    }

    public void showRightScreen(Node node) {
        if (paneRight.getChildren().size() > 0) {
            paneRight.getChildren().remove(0);
        }
        paneRight.getChildren().add(node);
    }


}
