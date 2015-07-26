package org.romastar.notary.controls;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;

/**
 * Created by roman on 10.08.14.
 */
public class TextAreaTableCell<S, T> extends TableCell<S, T> {

    private TextArea textArea;

    @Override
    public void startEdit() {
        super.startEdit();

        if (textArea == null) {
            createTextArea();
        }

        setGraphic(textArea);
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        textArea.selectAll();
        textArea.requestFocus();
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();

        setText(String.valueOf(getItem()));
        if (textArea != null)
            textArea.setText(getText());
        setContentDisplay(ContentDisplay.TEXT_ONLY);
    }

    @Override
    public void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            if (isEditing()) {
                if (textArea != null) {
                    textArea.setText(getString());
                }
                setGraphic(textArea);
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            } else {
                Text text = new Text(getString());
                text.setWrappingWidth(getTableColumn().getWidth());
                setGraphic(text);
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            }
        }
    }

    private TextArea createTextArea() {
        if (textArea != null)
            return textArea;
        textArea = new TextArea(getString());
        textArea.setWrapText(true);
        textArea.setPrefWidth(getTableColumn().getWidth());
        textArea.setPrefHeight(getTableRow().getHeight());
        textArea.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue == false)
                    commitEdit((T) textArea.getText());
            }
        });
        return textArea;
    }

    private String getString() {
        return getItem() == null ? "" : getItem().toString();
    }
}
