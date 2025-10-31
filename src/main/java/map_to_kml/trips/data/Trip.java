package map_to_kml.trips.data;

import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class Trip {

    private int _tripNumber;
    private Date _startTime;
    private Date _endTime;
    private int _validRecordsCount;
    private int _totalRecordsCount;
    private final List<PosRecordFile> _posRecFiles = new LinkedList<>();

    public Trip() {
        // ...
    }

    public void setTripNumber(int i) {
        _tripNumber = i;
    }

    public int getTripNumber() {
        return _tripNumber;
    }

    public void setStartTime(Date startTime) {
        _startTime = startTime;
    }

    public Date getStartTime() {
        return _startTime;
    }

    public void setEndTime(Date endTime) {
        _endTime = endTime;
    }

    public Date getEndTime() {
        return _endTime;
    }

    public int getValidRecordsCount() {
        return _validRecordsCount;
    }

    public int getTotalRecordsCount() {
        return _totalRecordsCount;
    }

    public void addPosRecordFile(PosRecordFile posRecFile) {
        _posRecFiles.add(posRecFile);

        // Update start time if we didn't have one yet
        if (_startTime == null) {
            _startTime = posRecFile.getStartTime();
        }

        _validRecordsCount += posRecFile.getValidRecordsCount();
        _totalRecordsCount += posRecFile.getTotalRecordsCount();
    }

    public List<PosRecordFile> getPosRecordFiles() {
        return Collections.unmodifiableList(_posRecFiles);
    }

    public Duration getDuration() {
        return Duration.between(_startTime.toInstant(), _endTime.toInstant());
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Trip [_tripNumber=");
        builder.append(_tripNumber);
        builder.append(", duration=");
        builder.append(Duration.between(_startTime.toInstant(), _endTime.toInstant()));
        builder.append(", _startTime=");
        builder.append(_startTime);
        builder.append(", _endTime=");
        builder.append(_endTime);
        builder.append(", _validRecordsCount=");
        builder.append(_validRecordsCount);
        builder.append(", _totalRecordsCount=");
        builder.append(_totalRecordsCount);
        builder.append("]");
        return builder.toString();
    }

}
