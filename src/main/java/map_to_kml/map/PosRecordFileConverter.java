package map_to_kml.map;

import map_to_kml.map.data.MapFile;
import map_to_kml.map.data.MapRecord;
import map_to_kml.trips.data.PosRecord;
import map_to_kml.trips.data.PosRecordFile;

/**
 * Convert a {@link MapFile} object containing zero or more {@link MapRecord}
 * objects into {@link PosRecordFile} with the corresponding
 * {@link PosRecord} objects
 */
public class PosRecordFileConverter {
    public PosRecordFileConverter() {
        // ...
    }

    public PosRecordFile convert(MapFile mapFile) {
        assert (mapFile != null);
        PosRecordFile posRecFile = new PosRecordFile(mapFile.getFile());
        posRecFile.setStartTime(mapFile.getStartTime());

        // Convert records
        for (MapRecord mapRec : mapFile.getRecords()) {
            posRecFile.add(new PosRecord(mapRec._isValid, mapRec._date, mapRec._lat, mapRec._lon,
                    mapRec._speed));
        }

        return posRecFile;
    }
}
