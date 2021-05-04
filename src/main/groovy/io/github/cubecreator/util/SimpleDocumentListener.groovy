package io.github.cubecreator.util

import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

abstract class SimpleDocumentListener implements DocumentListener {

    abstract void update(DocumentEvent event)

    @Override
    void insertUpdate(DocumentEvent documentEvent) {
        update(documentEvent)
    }

    @Override
    void removeUpdate(DocumentEvent documentEvent) {
        update(documentEvent)
    }

    @Override
    void changedUpdate(DocumentEvent documentEvent) {
        update(documentEvent)
    }
}
