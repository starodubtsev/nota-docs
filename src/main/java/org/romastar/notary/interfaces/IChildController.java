package org.romastar.notary.interfaces;

/**
 * Created by roman on 27.07.14.
 */
public interface IChildController extends IRefresh {
    default void setData(Object data) {
    }

    ;

    default void refresh() {
    }

    ;

    //ScreensEnum getScreenEdentifier();
}
