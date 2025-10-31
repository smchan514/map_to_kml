package map_to_kml.util;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Required;

import map_to_kml.map.data.MapFile;
import map_to_kml.nmea.data.NmeaFile;

/**
 * Enumerate files in a directory, sorted by file name alphabetical order
 */
public class FileEnumerator implements Iterable<File> {

    ////////////////////////
    // Configuration variables

    private File _dir;
    private String[] _extensions = { MapFile.FILE_EXNTESION, NmeaFile.FILE_EXNTESION };

    public FileEnumerator() {
        // ...
    }

    @Required
    public void setDir(String dir) {
        _dir = new File(dir);
    }

    public File getDir() {
        return _dir;
    }

    public void setExtensions(String[] extensions) {
        _extensions = extensions;
    }

    @Override
    public Iterator<File> iterator() {
        // Scan for files with the defined list of extensions
        for (String ext : _extensions) {
            Collection<File> list = scanFiles(_dir, ext);
            if (!list.isEmpty()) {
                return list.iterator();
            }
        }
        
        // Return an iterator to an empty list
        return new ArrayList<File>().iterator();
    }
    
    private Collection<File> scanFiles(File dir, String ext) {
        // List all the files matching the filter
        File[] files = dir.listFiles(new MyFileFilter(ext));

        // Sort the files in the TreeSet
        TreeSet<File> sorted = new TreeSet<File>(new MyFileNameComparator());
        for (File file : files) {
            sorted.add(file);
        }
        return sorted;
    }

    /////////////////////////////////////////////////////////////
    private static class MyFileNameComparator implements Comparator<File> {
        public MyFileNameComparator() {
            // ...
        }

        @Override
        public int compare(File o1, File o2) {
            String s1 = o1.getAbsolutePath();
            String s2 = o2.getAbsolutePath();
            return s1.compareTo(s2);
        }
    }

    /////////////////////////////////////////////////////////////
    private static class MyFileFilter implements FileFilter {
        private final String _ext;

        public MyFileFilter(String ext) {
            assert (ext != null);
            _ext = ext;
        }

        @Override
        public boolean accept(File f) {
            // Accept files with .map extension (case insensitive)
            return (f.isFile() && f.getName().toLowerCase().endsWith(_ext));
        }

    }
}
