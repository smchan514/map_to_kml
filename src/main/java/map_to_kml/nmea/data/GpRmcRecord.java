package map_to_kml.nmea.data;

import java.util.Date;

public class GpRmcRecord {
    /**
     * Entry validity flag
     */
    public final boolean _isValid;

    /**
     * Date/time stamp
     */
    public final Date _date;

    /**
     * Latitude in degrees
     */
    public final float _lat;

    /**
     * Longitude in degrees
     */
    public final float _lon;

    /**
     * Speed in km/h
     */
    public final float _speed;

    public GpRmcRecord(boolean isValid, Date date, float lat, float lon, float speed) {
        _isValid = isValid;
        _date = date;
        _lat = lat;
        _lon = lon;
        _speed = speed;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("GpRmcRecord [_isValid=");
        builder.append(_isValid);
        builder.append(", _date=");
        builder.append(_date);
        builder.append(", _lat=");
        builder.append(_lat);
        builder.append(", _lon=");
        builder.append(_lon);
        builder.append(", _speed=");
        builder.append(_speed);
        builder.append("]");
        return builder.toString();
    }

}
