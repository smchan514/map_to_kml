package map_to_kml.nmea.data;

import java.io.File;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * 
 * @see https://aprs.gids.nl/nmea/#vtg
 */
public class NmeaFile {

    public static final String FILE_EXNTESION = ".nmea";

    private final File _file;
    private final List<GpRmcRecord> _records = new LinkedList<>();
    private Date _startTime;

    public NmeaFile(File file) {
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
    public List<GpRmcRecord> getRecords() {
        return Collections.unmodifiableList(_records);
    }

    public void add(GpRmcRecord rec) {
        assert (rec != null);
        _records.add(rec);

        // Date/time in RMC record may not be reliable until the record is valid
        if (_startTime == null && rec._isValid) {
            _startTime = rec._date;
        }
    }

    public Date getStartTime() {
        return _startTime;
    }
}
