package io.github.cubecreator.ui.editor

import io.github.cubecreator.util.AbstractObjectDescriptor

class DefaultEditorDescriptor extends AbstractObjectDescriptor<Class<? extends AbstractEditor>> {
    DefaultEditorDescriptor(Class<? extends AbstractEditor> object) {
        super(object)
    }

    @Override
    String describe() {
        return "Default Editors"
    }
}
