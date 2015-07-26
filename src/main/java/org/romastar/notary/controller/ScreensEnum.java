package org.romastar.notary.controller;

import org.romastar.notary.interfaces.IResource;

/**
 * Created by roman on 26.07.14.
 */
public enum ScreensEnum implements IResource {
    INFO {
        public String getResource() {
            return "/org/romastar/notary/info.fxml";
        }
    },
    DOCUMENTS_LIST {
        public String getResource() {
            return "/org/romastar/notary/documentsList.fxml";
        }
    },
    DOCUMENT_EDITOR {
        public String getResource() {
            return "/org/romastar/notary/documentEditor.fxml";
        }
    },
    TEMPLATES_LIST {
        public String getResource() {
            return "/org/romastar/notary/templatesList.fxml";
        }
    },
    TEMPLATE_EDITOR {
        public String getResource() {
            return "/org/romastar/notary/templateEditor.fxml";
        }
    },
    PRINTED_TEMPLATES_LIST {
        public String getResource() {
            return "/org/romastar/notary/printedTemplatesList.fxml";
        }
    },
    TEMPLATE_PREVIEW {
        public String getResource() {
            return "/org/romastar/notary/preview.fxml";
        }
    }
}

