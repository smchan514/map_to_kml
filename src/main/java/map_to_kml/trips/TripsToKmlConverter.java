package map_to_kml.trips;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Required;

import map_to_kml.kml.data.KCoordinates;
import map_to_kml.kml.data.KDocument;
import map_to_kml.kml.data.KFolder;
import map_to_kml.kml.data.KHotSpot;
import map_to_kml.kml.data.KIcon;
import map_to_kml.kml.data.KIconStyle;
import map_to_kml.kml.data.KLineString;
import map_to_kml.kml.data.KLineStyle;
import map_to_kml.kml.data.KPair;
import map_to_kml.kml.data.KPlacemark;
import map_to_kml.kml.data.KPoint;
import map_to_kml.kml.data.KStyle;
import map_to_kml.kml.data.KStyleMap;
import map_to_kml.trips.data.PosRecord;
import map_to_kml.trips.data.PosRecordFile;
import map_to_kml.trips.data.Trip;
import map_to_kml.trips.data.Trips;
import map_to_kml.util.FileEnumerator;
import map_to_kml.util.TimeZoneLookup;

/**
 * Convert trips into Google Earth KML for visualisation
 */
public class TripsToKmlConverter {

    private static final String HREF_PUSHPIN_ICON = "http://maps.google.com/mapfiles/kml/pushpin/ylw-pushpin.png";
    private static final String STYLE_NORMAL_ID = "s0n";
    private static final String STYLE_NORMAL_URL = "#" + STYLE_NORMAL_ID;
    private static final String STYLE_HIGHLIGHT_ID = "s0h";
    private static final String STYLE_HIGHLIGHT_URL = "#" + STYLE_HIGHLIGHT_ID;
    private static final String STYLEMAP_ID = "s0m";
    private static final String STYLEMAP_URL = "#" + STYLEMAP_ID;
    private static final String DEFAULT_DOC_NAME = "Trips";

    ////////////////////////
    // Configuration variables

    private String _docName;
    private TimeZoneLookup _timeZoneLookup;
    private FileEnumerator _fileEnumerator;

    ////////////////////////
    // Runtime variables

    private SimpleDateFormat _sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss zzz");

    public TripsToKmlConverter() {
        // ...
    }

    @Required
    public void setTimeZoneLookup(TimeZoneLookup timeZoneLookup) {
        _timeZoneLookup = timeZoneLookup;
    }

    public void setFileEnumerator(FileEnumerator fileEnumerator) {
        _fileEnumerator = fileEnumerator;
    }

    public void setDocName(String docName) {
        _docName = docName;
    }

    public KDocument convertTrips(Trips trips) {
        // Create a new KML document
        KDocument doc = new KDocument();
        doc.name = getDocName();

        // Add "styles" and "style map" used to render points and line strings
        createStyleMap(doc);

        // Add each trip as a folder
        for (Trip trip : trips) {
            doc.folders.add(createFolder(trip));
        }

        return doc;
    }

    private String getDocName() {
        // Priority to the user-specified name
        if (_docName != null) {
            return _docName;
        }
        
        // Use the directory name of the FileEnumerator, if specified
        if(_fileEnumerator != null) {
            return _fileEnumerator.getDir().getName();
        }

        // Use the default name
        return DEFAULT_DOC_NAME;
    }

    private void createStyleMap(KDocument doc) {
        // Create a "normal" style
        KStyle styleNormal = new KStyle();
        doc.styles.add(styleNormal);
        styleNormal.id = STYLE_NORMAL_ID;
        styleNormal.iconStyle = new KIconStyle();
        styleNormal.iconStyle.scale = 1;
        styleNormal.iconStyle.icon = new KIcon();
        styleNormal.iconStyle.icon.href = HREF_PUSHPIN_ICON;
        styleNormal.iconStyle.hotSpot = new KHotSpot();
        styleNormal.iconStyle.hotSpot.x = 20;
        styleNormal.iconStyle.hotSpot.y = 2;
        styleNormal.iconStyle.hotSpot.xunits = "pixels";
        styleNormal.iconStyle.hotSpot.yunits = "pixels";
        styleNormal.lineStyle = new KLineStyle();
        styleNormal.lineStyle.color = "ffff0000";
        styleNormal.lineStyle.width = 3;

        // Create a "highlight" style, applied on mouse over
        KStyle styleHighlight = new KStyle();
        doc.styles.add(styleHighlight);
        styleHighlight.id = STYLE_HIGHLIGHT_ID;
        styleHighlight.iconStyle = new KIconStyle();
        styleHighlight.iconStyle.scale = 1.5f;
        styleHighlight.iconStyle.icon = new KIcon();
        styleHighlight.iconStyle.icon.href = HREF_PUSHPIN_ICON;
        styleHighlight.iconStyle.hotSpot = new KHotSpot();
        styleHighlight.iconStyle.hotSpot.x = 20;
        styleHighlight.iconStyle.hotSpot.y = 2;
        styleHighlight.iconStyle.hotSpot.xunits = "pixels";
        styleHighlight.iconStyle.hotSpot.yunits = "pixels";
        styleHighlight.lineStyle = new KLineStyle();
        styleHighlight.lineStyle.color = "ff00ff00";
        styleHighlight.lineStyle.width = 5;

        // Combine the normal and highlight styles into a "style map"
        KStyleMap styleMap = new KStyleMap();
        doc.styleMaps.add(styleMap);
        styleMap.id = STYLEMAP_ID;
        KPair pair0 = new KPair();
        styleMap.pairs.add(pair0);
        pair0.key = "normal";
        pair0.styleUrl = STYLE_NORMAL_URL;
        KPair pair1 = new KPair();
        styleMap.pairs.add(pair1);
        pair1.key = "highlight";
        pair1.styleUrl = STYLE_HIGHLIGHT_URL;
    }

    private KFolder createFolder(Trip trip) {
        // Create a new folder for the trip
        KFolder folderTrip = new KFolder();
        folderTrip.name = String.format("Trip #%d: %s", trip.getTripNumber(), trip.getDuration());

        // Add each MAP file in this trip as a subfolder
        for (PosRecordFile mapFile : trip.getPosRecordFiles()) {
            // Create a subfolder for the map file
            KFolder folderMapFile = new KFolder();
            folderMapFile.name = mapFile.getFileName();
            folderTrip.folders.add(folderMapFile);

            // Add KML placemarks in there are valid MAP records in this map file
            if (mapFile.getValidRecordsCount() > 0) {
                folderMapFile.placemarks.add(createLineString(mapFile));
                folderMapFile.placemarks.add(createStartPosition(mapFile));
            }
        }

        return folderTrip;
    }

    private KPlacemark createLineString(PosRecordFile mapFile) {
        assert (mapFile.getValidRecordsCount() > 0);

        KPlacemark placemark = new KPlacemark();
        placemark.name = "Track";
        placemark.visibility = 1;
        placemark.styleUrl = STYLEMAP_URL;

        // Show line string using valid records
        KLineString lineString = new KLineString();
        placemark.lineString = lineString;

        for (PosRecord rec : mapFile.getRecords()) {
            if (rec._isValid) {
                lineString.addCoordinates(new KCoordinates(rec._lon, rec._lat));
            }
        }

        return placemark;
    }

    private KPlacemark createStartPosition(PosRecordFile mapFile) {
        assert (mapFile.getValidRecordsCount() > 0);

        // Show the starting coordinates
        PosRecord firstRec = mapFile.getFirstValidRecord();
        KPlacemark placemark = new KPlacemark();
        placemark.name = getLocalDateTime(firstRec._lat, firstRec._lon, firstRec._date);
        placemark.styleUrl = STYLEMAP_URL;

        KPoint point = new KPoint(firstRec._lon, firstRec._lat);
        placemark.point = point;

        return placemark;
    }

    private String getLocalDateTime(float lat, float lon, Date date) {
        TimeZone tz = _timeZoneLookup.getTimeZone(lat, lon);
        _sdf.setTimeZone(tz);
        return _sdf.format(date);
    }

}
