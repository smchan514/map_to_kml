package map_to_kml.trips;

import java.time.Duration;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;

import map_to_kml.trips.data.PosRecord;
import map_to_kml.trips.data.PosRecordFile;
import map_to_kml.trips.data.Trip;
import map_to_kml.trips.data.Trips;

/**
 * Group one or more position record files, each representing a short dashcam
 * recording, into trips.
 * 
 * Current implement is based on timestamp of MAP records: if the timestamp of
 * the first record in a file is within the threshold from the last record of
 * the preceding file, then consider
 */
public class TripDetector implements InitializingBean {
    private static final org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager
            .getLogger(TripDetector.class);

    private static final long DEFAULT_THRESHOLD_MINUTES = 10;

    ////////////////////////
    // Configuration variables

    // Threshold for new trip detection
    private Duration _newTripThreshold = Duration.ofMinutes(DEFAULT_THRESHOLD_MINUTES);

    ///////////////////////
    // Runtime variables

    // Container for all detected trips
    private final Trips _trips = new Trips();

    private Trip _currentTrip;
    private Date _lastTimestamp;

    public TripDetector() {
        // ...
    }

    public void setNewTripThresholdMinutes(int minutes) {
        _newTripThreshold = Duration.ofMinutes(minutes);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        reset();
    }

    /**
     * Reset trip detector
     */
    public void reset() {
        _trips.clear();
    }

    /**
     * Update the trip detector with a file
     * 
     * @param  posRecFile a non-null instance of {@link PosRecordFile}
     * @return            true if a new trip has been detected with this file, false
     *                    otherwise
     */
    public boolean updatePosRecordFile(PosRecordFile posRecFile) {
        boolean newTripDetected = false;

        // No current trip or new trip detected...
        if (_currentTrip == null || detectNewTrip(posRecFile)) {
            LOGGER.debug("New trip detected");
            createTrip(posRecFile);
            newTripDetected = true;
        }

        updateCurrentTrip(posRecFile);
        return newTripDetected;
    }

    /**
     * @return a non-null instance of {@link Trips} representing all trips detected
     *         following one or more calls to
     *         {@link #updatePosRecordFile(PosRecordFile)}
     */
    public Trips getTrips() {
        return _trips;
    }

    /**
     * Create a new Trip object, making it the "current trip" and add it to the list
     * of _trips
     */
    private void createTrip(PosRecordFile posRecFile) {
        _currentTrip = new Trip();
        _trips.addTrip(_currentTrip);

        _currentTrip.setTripNumber(_trips.size());
        _currentTrip.setStartTime(posRecFile.getStartTime());
    }

    private boolean detectNewTrip(PosRecordFile posRecFile) {
        // No records in this file... this means the dashcam might have been powered up
        // for a short time and powered off
        if (posRecFile.getTotalRecordsCount() == 0) {
            return true;
        }

        // Check the start time of this file...
        Date startTime = posRecFile.getStartTime();

        // No start time... this could be same as above, short power cycle, or the
        // dashcam was started in an area without GPS signal for the duration of
        // the recording
        if (startTime == null) {
            return true;
        }

        // If the start time of this file is greater than
        // the timestamp of the last record from the last file
        Duration duration = Duration.between(_lastTimestamp.toInstant(), startTime.toInstant());
        return duration.compareTo(_newTripThreshold) > 0;
    }

    private void updateCurrentTrip(PosRecordFile posRecFile) {
        _currentTrip.addPosRecordFile(posRecFile);

        // No records in this file
        if (posRecFile.getTotalRecordsCount() == 0) {
            return;
        }

        // Extend the end time to the last record of this new file
        List<PosRecord> records = posRecFile.getRecords();
        PosRecord record = records.getLast();
        _lastTimestamp = record._date;
        _currentTrip.setEndTime(_lastTimestamp);

        // Find the last record with valid coordinates
        for (PosRecord rec : posRecFile.getRecords().reversed()) {
            if (rec._isValid) {
                break;
            }
        }
    }

}
