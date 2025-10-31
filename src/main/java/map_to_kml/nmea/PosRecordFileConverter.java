package map_to_kml.nmea;

import map_to_kml.map.data.MapRecord;
import map_to_kml.nmea.data.GpRmcRecord;
import map_to_kml.nmea.data.NmeaFile;
import map_to_kml.trips.data.PosRecord;
import map_to_kml.trips.data.PosRecordFile;

/**
 * Convert a {@link NmeaFile} object containing zero or more {@link MapRecord}
 * objects into {@link PosRecordFile} with the corresponding {@link PosRecord}
 * objects
 */
public class PosRecordFileConverter {
    public PosRecordFileConverter() {
        // ...
    }

    public PosRecordFile convert(NmeaFile nmeaFile) {
        assert (nmeaFile != null);
        PosRecordFile posRecFile = new PosRecordFile(nmeaFile.getFile());
        posRecFile.setStartTime(nmeaFile.getStartTime());

        // Convert records
        for (GpRmcRecord mapRec : nmeaFile.getRecords()) {
            posRecFile.add(new PosRecord(mapRec._isValid, mapRec._date, mapRec._lat, mapRec._lon,
                    mapRec._speed));
        }

        return posRecFile;
    }
}
