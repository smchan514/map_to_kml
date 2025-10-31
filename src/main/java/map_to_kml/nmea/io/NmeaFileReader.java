package map_to_kml.nmea.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import map_to_kml.nmea.data.GpRmcRecord;
import map_to_kml.nmea.data.NmeaFile;

/**
 * <PRE>
 * eg4. $GPRMC,hhmmss.ss,A,llll.ll,a,yyyyy.yy,a,x.x,x.x,ddmmyy,x.x,a*hh
 * 1    = UTC of position fix
 * 2    = Data status (V=navigation receiver warning)
 * 3    = Latitude of fix
 * 4    = N or S
 * 5    = Longitude of fix
 * 6    = E or W
 * 7    = Speed over ground in knots
 * 8    = Track made good in degrees True
 * 9    = UT date
 * 10   = Magnetic variation degrees (Easterly var. subtracts from true course)
 * 11   = E or W
 * 12   = Checksum
 * </PRE>
 *
 * @see https://aprs.gids.nl/nmea/#rmc
 */
public class NmeaFileReader {
    private static final org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager
            .getLogger(NmeaFileReader.class);

    private static final String DEFAULT_TIME_ZONE = "UTC";
    private static final int MIN_PARTS_PER_LINE = 2;
    private static final int EXPECTED_PARTS_GPRMC = 13;

    private static final float KNOTS_TO_KPH = 1.852f;

    private Calendar _cal;

    public NmeaFileReader() {
        TimeZone tz = TimeZone.getTimeZone(DEFAULT_TIME_ZONE);
        _cal = Calendar.getInstance(tz);
    }

    public NmeaFile readFile(String file) throws IOException {
        return readFile(new File(file));
    }

    public NmeaFile readFile(File file) throws IOException {
        NmeaFile mapFile = new NmeaFile(file);
        LOGGER.debug("Processing " + file + " ...");
        try (FileInputStream fis = new FileInputStream(file)) {
            try (InputStreamReader isr = new InputStreamReader(fis)) {
                try (BufferedReader br = new BufferedReader(isr)) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        String[] parts = line.split(",");

                        // Make sure the line has something...
                        if (parts.length < MIN_PARTS_PER_LINE) {
                            throw new RuntimeException("Insufficient number of parts, min=" + MIN_PARTS_PER_LINE
                                    + ", actual=" + parts.length + ", line='" + line + "'");
                        }

                        String sentence = parts[0];
                        switch (sentence) {
                        case "$GPRMC":
                            mapFile.add(parseGPRMC(line));
                            break;

                        default:
                            // Skipping the line
                        }

                    }
                }
            }
        }
        return mapFile;
    }

    private GpRmcRecord parseGPRMC(String line) {
        String[] parts = line.split(",");
        if (parts.length != EXPECTED_PARTS_GPRMC) {
            LOGGER.warn("Unexpected number of comma-separated parts: expected=" + EXPECTED_PARTS_GPRMC + ", actual="
                    + parts.length + ", line=" + line);
        }

        boolean validity = parseValidity(parts[2]);
        Date date = parseDate(parts[9], parts[1]);
        float lat = parseLat(parts[3], parts[4]);
        float lon = parseLon(parts[5], parts[6]);
        float speed = Float.parseFloat(parts[7]) * KNOTS_TO_KPH;
        return new GpRmcRecord(validity, date, lat, lon, speed);
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
        if (time.length() != 10) {
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
        int msec = Integer.parseInt(time.substring(7, 10));
        _cal.set(year, month, day, hour, min, sec);
        _cal.set(Calendar.MILLISECOND, msec);

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
