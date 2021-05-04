package io.github.cubecreator.uri

final class DataURLStreamHandler extends URLStreamHandler {
    @Override
    protected URLConnection openConnection(URL url) throws IOException {
        return new DataURLConnection(url)
    }
}
