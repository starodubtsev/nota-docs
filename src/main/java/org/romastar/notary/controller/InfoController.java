package org.romastar.notary.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import org.romastar.notary.interfaces.IChildController;
import org.romastar.notary.model.AppModel;

/**
 * Created by roman on 26.07.14.
 */
public class InfoController implements IChildController {
    @FXML
    private TextArea txtInfo;

    @FXML
    private void initialize() {
        txtInfo.textProperty().bind(AppModel.getInstance().infoProperty());
    }
}
