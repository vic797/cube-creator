package io.github.cubecreator.uri

final class DataURLStreamHandlerFactory implements URLStreamHandlerFactory {
    @Override
    URLStreamHandler createURLStreamHandler(String s) {
        if (s == "cube") {
            return new DataURLStreamHandler()
        }
        return null
    }
}
