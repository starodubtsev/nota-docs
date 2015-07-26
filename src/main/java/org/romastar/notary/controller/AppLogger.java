package org.romastar.notary.controller;

import org.controlsfx.dialog.DialogStyle;
import org.controlsfx.dialog.Dialogs;

/**
 * Created by roman on 27.07.14.
 */
public class AppLogger {
    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger("ApplicationLogger");

    public static void processException(Throwable ex) {
        logger.error(ex);
        Dialogs.create().style(DialogStyle.NATIVE).showException(ex);
    }

    public static void processException(String message, Throwable ex) {
        logger.error(ex);
        Dialogs.create().style(DialogStyle.NATIVE).message(message).showException(ex);
    }

    public static void logException(Throwable ex) {
        logger.error(ex);
    }

    public static void logInfo(String message) {
        logger.info(message);
    }
}
