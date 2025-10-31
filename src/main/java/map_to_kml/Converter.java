package map_to_kml;

import java.io.File;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextStartedEvent;

import map_to_kml.kml.data.KDocument;
import map_to_kml.kml.io.KmlWriter;
import map_to_kml.map.PosRecordFileConverter;
import map_to_kml.map.data.MapFile;
import map_to_kml.map.io.MapFileReader;
import map_to_kml.nmea.data.NmeaFile;
import map_to_kml.nmea.io.NmeaFileReader;
import map_to_kml.trips.TripDetector;
import map_to_kml.trips.TripsToKmlConverter;
import map_to_kml.trips.data.PosRecordFile;
import map_to_kml.trips.data.Trip;
import map_to_kml.trips.data.Trips;

/**
 * Class implementing main converter logic
 */
public class Converter implements ApplicationListener<ContextStartedEvent> {
    private static final org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager
            .getLogger(Converter.class);

    private static final String DEFAULT_OUTFILE_NAME = "out.kml";

    private Iterable<File> _fileEnumerator;
    private MapFileReader _mapFileReader;
    private PosRecordFileConverter _mapFile2PosRecFileConverter;
    private NmeaFileReader _nmeaFileReader;
    private map_to_kml.nmea.PosRecordFileConverter _nmeaFile2PosRecFileConverter;
    private TripDetector _tripDetector;
    private TripsToKmlConverter _tripsToKmlConverter;
    private KmlWriter _kmlWriter;
    private File _lastMapFile;
    private File _outfile;


    public Converter() {
        // ...
    }

    @Required
    public void setFileEnumerator(Iterable<File> fileEnumerator) {
        _fileEnumerator = fileEnumerator;
    }

    @Required
    public void setMapFileReader(MapFileReader mapFileReader) {
        _mapFileReader = mapFileReader;
    }

    @Required
    public void setMapFile2PosRecFileConverter(PosRecordFileConverter mapFile2PosRecFileConverter) {
        _mapFile2PosRecFileConverter = mapFile2PosRecFileConverter;
    }

    @Required
    public void setNmeaFileReader(NmeaFileReader nmeaFileReader) {
        _nmeaFileReader = nmeaFileReader;
    }

    @Required
    public void setNmeaFile2PosRecFileConverter(map_to_kml.nmea.PosRecordFileConverter nmeaFile2PosRecFileConverter) {
        _nmeaFile2PosRecFileConverter = nmeaFile2PosRecFileConverter;
    }

    @Required
    public void setTripDetector(TripDetector tripDetector) {
        _tripDetector = tripDetector;
    }

    @Required
    public void setTripsToKmlConverter(TripsToKmlConverter tripsToKmlConverter) {
        _tripsToKmlConverter = tripsToKmlConverter;
    }

    @Required
    public void setKmlWriter(KmlWriter kmlWriter) {
        _kmlWriter = kmlWriter;
    }

    public void setOutfile(String outfile) {
        _outfile = new File(outfile);
    }

    @Override
    public void onApplicationEvent(ContextStartedEvent event) {
        // Load all the MAP files to identify "trips"
        for (File f : _fileEnumerator) {
            try {
                PosRecordFile posRecFile;
                if (f.getName().toLowerCase().endsWith(MapFile.FILE_EXNTESION)) {
                    MapFile mapFile = _mapFileReader.readFile(f);
                    LOGGER.info("Loaded " + mapFile.getRecords().size() + " records from " + f);
                    posRecFile = _mapFile2PosRecFileConverter.convert(mapFile);
                } else if (f.getName().toLowerCase().endsWith(NmeaFile.FILE_EXNTESION)) {
                    NmeaFile nmeaFile = _nmeaFileReader.readFile(f);
                    LOGGER.info("Loaded " + nmeaFile.getRecords().size() + " records from " + f);
                    posRecFile = _nmeaFile2PosRecFileConverter.convert(nmeaFile);
                } else {
                    throw new RuntimeException("Unknown file extension: " + f);
                }

                boolean newTripDeteced = _tripDetector.updatePosRecordFile(posRecFile);
                if (newTripDeteced) {
                    LOGGER.info("New trip detected!");
                }

                _lastMapFile = f;
            } catch (Exception e) {
                throw new RuntimeException("Unhandled exception", e);
            }
        }

        // Show detected trips
        Trips trips = _tripDetector.getTrips();
        LOGGER.info("Processed " + trips.size() + " trips:");
        for (Trip trip : trips.getTrips()) {
            LOGGER.info(">> " + trip);
        }

        // Write trips into a KML file
        try {
            if (_outfile == null) {
                _outfile = guessOutfile();
            }

            LOGGER.info("Writing to " + _outfile + " ...");
            KDocument kmlDoc = _tripsToKmlConverter.convertTrips(trips);
            _kmlWriter.writeKmlFile(kmlDoc, _outfile);
            LOGGER.info("DONE!");
        } catch (Exception e) {
            throw new RuntimeException("Unhandled exception", e);
        }
    }

    private File guessOutfile() {
        if (_lastMapFile != null) {
            return new File(_lastMapFile.getParent(), DEFAULT_OUTFILE_NAME);
        }

        return new File(DEFAULT_OUTFILE_NAME);
    }
}
