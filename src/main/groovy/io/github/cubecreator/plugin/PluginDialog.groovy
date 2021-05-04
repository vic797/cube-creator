package io.github.cubecreator.plugin

import io.github.cubecreator.ui.dialog.BaseDialog

/**
 * @inheritDoc
 *
 * @see PluginContext
 */
class PluginDialog extends BaseDialog {

    /**
     * Context of the plugin that instantiated this dialog
     */
    protected final PluginContext context

    /**
     * Constructor
     * @param context Context of the plugin that instantiated this dialog
     */
    PluginDialog(PluginContext context) {
        this.context = context
    }

    /**
     * Shows this dialog
     */
    void showDialog() {
        showDialog(null)
    }
}
