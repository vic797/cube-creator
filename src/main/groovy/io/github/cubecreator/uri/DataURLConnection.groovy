package io.github.cubecreator.uri

import io.github.cubecreator.util.Settings

final class DataURLConnection extends URLConnection {

    public static File rootFile = new File(Settings.get("config.properties").get("url-root", "."))

    private File file

    protected DataURLConnection(URL url) {
        super(url)
    }

    @Override
    void connect() throws IOException {
        List<String> pathParts = new ArrayList<>()
        pathParts.add("data")
        pathParts.add(getURL().getHost())
        pathParts.addAll(Arrays.asList(getURL().getPath().split("/")))
        String[] paths = new String[pathParts.size()]
        pathParts.toArray(paths)
        file = new File(rootFile, String.join(File.separator, paths))
        if (!file.exists()) {
            throw new IOException("file not found: ${file}")
        }
    }

    @Override
    InputStream getInputStream() throws IOException {
        if (file == null) {
            connect()
        }
        return new FileInputStream(file)
    }
}
