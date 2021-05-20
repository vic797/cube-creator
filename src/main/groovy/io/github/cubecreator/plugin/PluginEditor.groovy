package io.github.cubecreator.plugin

import io.github.cubecreator.ui.editor.AbstractEditor
import io.github.cubecreator.util.Utils
import org.apache.logging.log4j.LogManager
import org.greenrobot.eventbus.EventBus

/**
 * @inheritDoc
 */
abstract class PluginEditor extends AbstractEditor {

    void handle(Throwable e) {
        LogManager.getLogger(getClass()).trace(Utils.dump(e))
    }

}
