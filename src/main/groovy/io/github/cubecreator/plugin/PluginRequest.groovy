package io.github.cubecreator.plugin

/**
 * Use this class when requesting information from the Workbench
 */
abstract class PluginRequest {

    /**
     * Constant to request all the opened files
     */
    static final String REQUEST_FILES = "PluginRequest.REQUEST_FILES"
    /**
     * Constant to request the currently opened resource pack or data pack
     */
    static final String REQUEST_PACK = "PluginRequest.REQUEST_PACK"

    /**
     * The request
     */
    final String request

    /**
     * Constructor
     * @param request The requesy
     */
    PluginRequest(String request) {
        this.request = request
    }

    /**
     * This function is called when the Workbench provides a response
     * @param data The data sent by the Workbench
     */
    abstract void response(Object data)

}
