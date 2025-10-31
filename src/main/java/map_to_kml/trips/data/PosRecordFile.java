package map_to_kml.trips.data;

import java.io.File;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class PosRecordFile {

    private final File _file;
    private final List<PosRecord> _records = new LinkedList<>();
    private PosRecord _firstValidRecord;
    private int _totalRecordsCount;
    private int _validRecordsCount;
    private Date _startTime;

    public PosRecordFile(File file) {
        _file = file;
    }

    public File getFile() {
        return _file;
    }

    public String getFileName() {
        return _file.getName();
    }

    public void setStartTime(Date startTime) {
        _startTime = startTime;
    }

    public Date getStartTime() {
        return _startTime;
    }

    /**
     * @return a unmodified copy of the records
     */
    public List<PosRecord> getRecords() {
        return Collections.unmodifiableList(_records);
    }

    public void add(PosRecord rec) {
        assert (rec != null);

        _records.add(rec);

        // Remember first valid record
        if (_firstValidRecord == null && rec._isValid) {
            _firstValidRecord = rec;
        }

        // Update stats
        ++_totalRecordsCount;
        if (rec._isValid) {
            ++_validRecordsCount;
        }
    }

    public int getTotalRecordsCount() {
        return _totalRecordsCount;
    }

    public int getValidRecordsCount() {
        return _validRecordsCount;
    }

    public PosRecord getFirstValidRecord() {
        return _firstValidRecord;
    }

}
