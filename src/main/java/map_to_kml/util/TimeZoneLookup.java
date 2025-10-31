package map_to_kml.util;

import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.springframework.beans.factory.InitializingBean;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import map_to_kml.geojson.data.GFeature;
import map_to_kml.geojson.data.GFeatureCollection;
import map_to_kml.geojson.data.GLonLatPoint;
import map_to_kml.geojson.data.GLonLatPointDeserializer;
import map_to_kml.geojson.data.GMultiPolygon;
import map_to_kml.geojson.data.GPolygon;

/**
 * Class implementing time zone lookup at a lat/lon coordinates using a polygon
 * database loaded from the configured GeoJson Zip file.
 * 
 * Java AWT geometry is used for "point in shape" detection.
 * 
 * @see https://github.com/evansiroky/timezone-boundary-builder
 */
public class TimeZoneLookup implements InitializingBean {
    private static final org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager
            .getLogger(TimeZoneLookup.class);

    ////////////////////////
    // Configuration variables

    private TimeZone _defaultTimeZone = TimeZone.getDefault();
    private String _infile = "timezones-now.geojson.zip";

    ////////////////////////
    // Runtime variables

    // List of all the shapes loaded from GeoJson
    private final LinkedList<Path2D> _shapes = new LinkedList<>();

    // Map from each shape object to the corresponding "time zone ID", e.g.
    // "America/New_York"
    private final HashMap<Path2D, String> _map = new HashMap<>();

    // Stats
    private int _totalShapes;
    private int _totalFeatures;

    public TimeZoneLookup() {
        // ...
    }

    public void setDefaultTimeZone(String tzid) {
        _defaultTimeZone = TimeZone.getTimeZone(tzid);
        LOGGER.info("_defaultTimeZone=" + _defaultTimeZone);
    }

    public void setInfile(String infile) {
        _infile = infile;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            loadGeojsonZip(_infile);
            LOGGER.info("Loaded " + _totalFeatures + " features and " + _totalShapes + " shapes from " + _infile);
        } catch (Exception e) {
            LOGGER.warn("Failed to load " + _infile + ", using default time zone for all locations");
        }
    }

    public TimeZone getTimeZone(float lat, float lon) {
        Point2D.Float pt = new Point2D.Float(lon, lat);
        for (Path2D path : _shapes) {
            if (path.contains(pt)) {
                String tzid = _map.get(path);
                TimeZone tz = TimeZone.getTimeZone(tzid);
                LOGGER.debug("Found a match: tzid=" + tzid + ", tz=" + tz);
                return tz;
            }
        }

        // Not found, return the default time zone
        return _defaultTimeZone;
    }

    private void loadGeojsonZip(String infile) throws IOException {
        LOGGER.debug("Loading from Zip file " + infile + " ...");
        try (ZipFile zipFile = new ZipFile(new File(infile), ZipFile.OPEN_READ)) {
            Enumeration<? extends ZipEntry> enumEntries = zipFile.entries();
            while (enumEntries.hasMoreElements()) {
                ZipEntry zipEntry = enumEntries.nextElement();
                LOGGER.debug("Loading Zip entry: " + zipEntry.getName() + " ...");
                InputStream in = zipFile.getInputStream(zipEntry);
                loadGeojson(in);
                in.close();
            }
        }
    }

    private void loadGeojson(InputStream in) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule("CustomLonLatPointDeserializer");
        module.addDeserializer(GLonLatPoint.class, new GLonLatPointDeserializer());
        mapper.registerModule(module);

        // Read the features from the input GeoJson file
        GFeatureCollection coll = mapper.readValue(in, GFeatureCollection.class);

        // For each "feature", unpack the shapes in them and add them to our internal
        // list of Path2D's
        for (GFeature feature : coll.features) {
            ++_totalFeatures;
            String tzid = (String) feature.properties.get("tzid");
            if (feature.geometry instanceof GMultiPolygon) {
                unpackMultiPolygon((GMultiPolygon) feature.geometry, tzid);
            } else if (feature.geometry instanceof GPolygon) {
                unpackPolygon((GPolygon) feature.geometry, tzid);
            } else {
                throw new RuntimeException("Unhandle geometry class " + feature.geometry.getClass());
            }
        }
    }

    private void unpackPolygon(GPolygon geom, String tzid) {
        List<List<GLonLatPoint>> list1 = geom.getCoordinates();
        for (List<GLonLatPoint> list0 : list1) {
            ++_totalShapes;
            Path2D.Float path = new Path2D.Float();
            _shapes.add(path);
            _map.put(path, tzid);
            boolean first = true;
            for (GLonLatPoint llp : list0) {
                if (first) {
                    first = false;
                    path.moveTo(llp.getLon(), llp.getLat());
                } else {
                    path.lineTo(llp.getLon(), llp.getLat());
                }
            }
        }
    }

    private void unpackMultiPolygon(GMultiPolygon geom, String tzid) {
        List<List<List<GLonLatPoint>>> list2 = geom.getCoordinates();
        for (List<List<GLonLatPoint>> list1 : list2) {
            for (List<GLonLatPoint> list0 : list1) {
                ++_totalShapes;
                Path2D.Float path = new Path2D.Float();
                _shapes.add(path);
                _map.put(path, tzid);
                boolean first = true;
                for (GLonLatPoint llp : list0) {
                    if (first) {
                        first = false;
                        path.moveTo(llp.getLon(), llp.getLat());
                    } else {
                        path.lineTo(llp.getLon(), llp.getLat());
                    }
                }
            }
        }
    }

}
