package org.romastar.notary.interfaces;

import javafx.scene.control.ListView;
import org.romastar.notary.controller.ScreensEnum;

/**
 * Created by roman on 31.07.14.
 */
public interface IListController {
    ListView getContentListView();

    ScreensEnum getEditorScreenIdentifier();
}
