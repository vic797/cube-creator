package io.github.cubecreator.plugin

/**
 * An action that can be invoked by a menu item in this plugin's menu. Menus are defined in the <code>menu</code>
 * array in the <code>plugin-info.json</code> file.
 *
 * <b>Example</b>
 *
 * Define a menu item in <code>plugin-info.json</code>:
 *
 * <pre><code>
 *     ...
 *     "menu": [
 *         {
 *              "text": "Demo Action",
 *              "action": "demoAction"
 *         }
 *     ]
 *     ...
 * </code></pre>
 *
 * And then on the {@link PluginContext#registerAction(String, PluginAction)}
 *
 * <pre><code>
 *     ...
 *     void load(PluginContext context) {
 *         context.registerAction("demoAction", ctx -> {
 *             JOptionPane.showMessageDialog(null, "Demo action triggered")
 *         })
 *     }
 *     ...
 * </code></pre>
 */
interface PluginAction {

    /**
     * Triggered when the action is triggered by a menu item
     * @param context The context of this plugin
     */
    void performAction(PluginContext context)

}