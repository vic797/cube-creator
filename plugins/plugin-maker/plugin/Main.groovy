import io.github.cubecreator.plugin.Plugin
import io.github.cubecreator.plugin.PluginContext
import io.github.cubecreator.plugin.PluginRequest

import javax.swing.JFileChooser
import javax.swing.JOptionPane

import net.lingala.zip4j.ZipFile

import PluginMakerDialog
import FileNameFilter

class PluginMaker extends Plugin {

    @Override
    void load(PluginContext context) {
        context.registerAction("createPlugin", ctx -> {
            PluginMakerDialog dialog = new PluginMakerDialog(ctx, info.data)
            dialog.showDialog()
            if (dialog.result == PluginMakerDialog.RESULT_OK) {
                try {
                    ctx.openPack(dialog.createPlugin().toString())
                } catch (IOException e) {
                    ctx.handle(info, e)
                }
            }
        })
        context.registerAction("packPlugin", ctx -> {
            ctx.request(new PluginRequest(PluginRequest.REQUEST_PACK) {

                @Override
                void response(Object data) {
                    if (data == null) {
                        JOptionPane.showMessageDialog(null, "Nothing to compress", "Pack plugin", JOptionPane.ERROR_MESSAGE)
                        return
                    }
                    JFileChooser chooser = new JFileChooser()
                    chooser.fileHidingEnabled = false
                    chooser.acceptAllFileFilterUsed = false
                    chooser.multiSelectionEnabled = false
                    chooser.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
                    if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                        File pluginLocation = (File) data
                        ZipFile file = new ZipFile(new File(chooser.selectedFile, pluginLocation.name + ".ccep"))
                        File[] files = pluginLocation.listFiles()
                        if (files != null) {
                            for (File entry : files) {
                                if (entry.isFile()) {
                                    file.addFile(entry)
                                } else {
                                    file.addFolder(entry)
                                }
                            }
                        }
                    }
                }
            })
        })
        context.registerAction("editPlugin", ctx -> {
            JFileChooser chooser = new JFileChooser()
            chooser.fileHidingEnabled = false
            chooser.acceptAllFileFilterUsed = false
            chooser.multiSelectionEnabled = false
            chooser.addChoosableFileFilter(new FileNameFilter("plugin-info.json"))
            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                ctx.openPack(chooser.getSelectedFile().getParentFile().toString())
            }
        })
    }

}

new PluginMaker()