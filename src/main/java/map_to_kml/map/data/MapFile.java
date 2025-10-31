package map_to_kml.map.data;

import java.io.File;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class MapFile {

    public static final String FILE_EXNTESION = ".map";

    private final File _file;
    private final List<MapRecord> _records = new LinkedList<>();
    private Date _startTime;

    public MapFile(File file) {
        _file = file;
    }

    public File getFile() {
        return _file;
    }

    public String getFileName() {
        return _file.getName();
    }

    /**
     * @return a unmodified copy of the records
     */
    public List<MapRecord> getRecords() {
        return Collections.unmodifiableList(_records);
    }

    public void add(MapRecord rec) {
        assert (rec != null);

        _records.add(rec);

        if (_startTime == null) {
            _startTime = rec._date;
        }
    }

    public Date getStartTime() {
        return _startTime;
    }
}
