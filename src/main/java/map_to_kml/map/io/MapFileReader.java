package map_to_kml.map.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import map_to_kml.map.data.MapFile;
import map_to_kml.map.data.MapRecord;

public class MapFileReader {
    private static final org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager
            .getLogger(MapFileReader.class);

    private static final String DEFAULT_TIME_ZONE = "UTC";
    private static final int MIN_PARTS_PER_LINE = 8;
    private static final int EXPECTED_PARTS = 11;

    private Calendar _cal;

    public MapFileReader() {
        TimeZone tz = TimeZone.getTimeZone(DEFAULT_TIME_ZONE);
        _cal = Calendar.getInstance(tz);
    }

    public MapFile load(String file) throws IOException {
        return readFile(new File(file));
    }

    public MapFile readFile(File file) throws IOException {
        MapFile mapFile = new MapFile(file);
        LOGGER.debug("Processing " + file + " ...");
        try (FileInputStream fis = new FileInputStream(file)) {
            try (InputStreamReader isr = new InputStreamReader(fis)) {
                try (BufferedReader br = new BufferedReader(isr)) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        mapFile.add(parseLine(line));
                    }
                }
            }
        }
        return mapFile;
    }

    private MapRecord parseLine(String line) {
        String[] parts = line.split(",");
        if (parts.length != EXPECTED_PARTS) {
            LOGGER.warn("Unexpected number of comma-separated parts: expected=" + EXPECTED_PARTS + ", actual="
                    + parts.length + ", line=" + line);
        }

        if (parts.length < MIN_PARTS_PER_LINE) {
            throw new RuntimeException("Insufficient number of parts, min=" + MIN_PARTS_PER_LINE + ", actual="
                    + parts.length + ", line='" + line + "'");
        }

        boolean validity = parseValidity(parts[0]);
        Date date = parseDate(parts[1], parts[2]);
        float lat = parseLat(parts[3], parts[4]);
        float lon = parseLon(parts[5], parts[6]);
        float speed = Float.parseFloat(parts[7]);
        return new MapRecord(validity, date, lat, lon, speed);
    }

    private boolean parseValidity(String s) {
        if ("V".equals(s)) {
            return false;
        } else if ("A".equals(s)) {
            return true;
        }

        throw new RuntimeException("Unexpected validity flag value: '" + s + "'");
    }

    private Date parseDate(String date, String time) {
        if (date.length() != 6) {
            throw new RuntimeException("Unexpected date string length: " + date);
        }
        if (time.length() != 6) {
            throw new RuntimeException("Unexpected time string length: " + time);
        }

        int day = Integer.parseInt(date.substring(0, 2));
        // Month in Calendar starts with 0
        int month = Integer.parseInt(date.substring(2, 4)) - 1;
        // Year numbers in MAP files have no century
        int year = Integer.parseInt(date.substring(4, 6)) + 2000;
        int hour = Integer.parseInt(time.substring(0, 2));
        int min = Integer.parseInt(time.substring(2, 4));
        int sec = Integer.parseInt(time.substring(4, 6));
        _cal.set(year, month, day, hour, min, sec);

        return _cal.getTime();
    }

    private float parseLat(String number, String indicator) {
        float deg = parseDegreeDecimalMinute(number);
        float sign = parseNorthSouth(indicator);
        return deg * sign;
    }

    private float parseLon(String number, String indicator) {
        float deg = parseDegreeDecimalMinute(number);
        float sign = parseEastWest(indicator);
        return deg * sign;
    }

    /**
     * Parse a string representation of a "degree decimal minute" number into
     * decimal degrees, e.g. "4527.2930" stands for "45 degrees, 27.2930 minutes"
     * which should convert to 45.454884f.
     * 
     * @param  s a non-null String
     * @return   decimal degrees
     * 
     * @see      https://www.ottergeospatial.info/2020/02/03/geographic-coordinate-notation-dd-vs-dm-vs-dms/
     * @see      https://pro.arcgis.com/en/pro-app/3.4/tool-reference/data-management/supported-notation-formats.htm
     */
    private float parseDegreeDecimalMinute(String s) {
        float n0 = Float.parseFloat(s);
        if (Math.signum(n0) < 0) {
            throw new RuntimeException(
                    "Unexpected number sign, expected 0 or positive, actual=" + n0 + " (s='" + s + "')");
        }

        float deg = (float) Math.floor(n0 / 100);
        float min = n0 % 100;
        float n1 = deg + min / 60;

        return n1;
    }

    private float parseNorthSouth(String flag) {
        switch (flag) {
        case "0":
        case "N":
            return 1;

        case "S":
            return -1;

        default:
            throw new RuntimeException("Unknown North/South indicator: " + flag);
        }
    }

    private float parseEastWest(String flag) {
        switch (flag) {
        case "0":
        case "E":
            return 1;

        case "W":
            return -1;

        default:
            throw new RuntimeException("Unknown East/West indicator: " + flag);
        }
    }

}
