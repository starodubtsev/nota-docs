package org.romastar.notary.utils;

import org.romastar.notary.model.PrintedDocumentTemplate;
import org.romastar.notary.model.PrintedTemplate;

/**
 * Created by roman on 05.08.14.
 */
public class HtmlUtils {
    public static String templateToHtml(PrintedTemplate template) {
        StringBuilder htmlBuilder = new StringBuilder();
        String format = "<tr class='table-documents-tr'><td class='table-documents-td'>%s</td><td class='table-documents-td'>%s</td></tr>";

        for (PrintedDocumentTemplate printedDocumentTemplate : template.getPrintedDocumentTemplates()) {
            if (!printedDocumentTemplate.isSelected())
                continue;
            String row = String.format(format, printedDocumentTemplate.documentFullInfoProperty().get(), printedDocumentTemplate.additionalInfoProperty().get());
            htmlBuilder.append(row);
        }
        return htmlBuilder.toString();
    }
}
