package io.github.cubecreator.plugin

/**
 * This exception is thrown in case two plugins are loaded with the same UUID
 */
class DuplicatedPluginIDException extends Exception {

    DuplicatedPluginIDException(String id) {
        super("Duplicated plugin UUID: ${id}")
    }

}
