package io.github.cubecreator.util

final class FileComparator implements Comparator<File> {
    @Override
    int compare(File file, File t1) {
        if (file.isDirectory() && t1.isDirectory()) {
            return file.getName().compareToIgnoreCase(t1.getName())
        } else if (file.isDirectory() && t1.isFile()) {
            return -1
        } else if (file.isFile() && t1.isDirectory()) {
            return 1
        } else {
            return file.getName().compareToIgnoreCase(t1.getName())
        }
    }
}
