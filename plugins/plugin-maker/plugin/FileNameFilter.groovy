import javax.swing.filechooser.FileFilter

class FileNameFilter extends FileFilter {

    private String name;

    FileNameFilter(String name) {
        this.name = name
    }

    @Override
    boolean accept(File file) {
        if (file.isDirectory()) {
            return true
        }
        return file.getName() == name
    }

    @Override
    String getDescription() {
        return name
    }

}