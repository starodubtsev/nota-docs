package org.romastar.notary.events;

import javafx.beans.NamedArg;
import javafx.event.Event;
import javafx.event.EventType;
import org.romastar.notary.controller.ScreensEnum;

/**
 * Created by roman on 11.08.14.
 */
public class EditorEvent extends Event {

    public static final EventType<EditorEvent> ITEM_ADDED = new EventType<>("ITEM_ADDED");
    public static final EventType<EditorEvent> ITEM_CHANGED = new EventType<>("ITEM_CHANGED");
    public static final EventType<EditorEvent> ITEM_REMOVED = new EventType<>("ITEM_REMOVED");
    public static final EventType<EditorEvent> ALL_ITEMS_REMOVED = new EventType<>("ALL_ITEMS_REMOVED");

    private ScreensEnum view;


    public EditorEvent(@NamedArg("eventType") EventType<? extends Event> eventType, ScreensEnum view) {
        super(eventType);
        this.view = view;
    }

    public ScreensEnum getView() {
        return view;
    }
}
