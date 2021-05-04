import io.github.cubecreator.plugin.Plugin
import io.github.cubecreator.plugin.PluginContext
import io.github.cubecreator.plugin.PluginRequest

import TagEditor

class CubePlugin extends Plugin {

    @Override
    void load(PluginContext context) {
        context.registerEditor("**/tags/**/*.json", TagEditor.class)
    }

}

new CubePlugin()