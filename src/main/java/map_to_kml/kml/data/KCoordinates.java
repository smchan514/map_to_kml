package map_to_kml.kml.data;

public class KCoordinates {

    private float _longitude;
    private float _latitude;

    public KCoordinates(float lon, float lat) {
        _longitude = lon;
        _latitude = lat;
    }

    public void setLongitude(float longitude) {
        _longitude = longitude;
    }

    public float getLongitude() {
        return _longitude;
    }

    public void setLatitude(float latitude) {
        _latitude = latitude;
    }

    public float getLatitude() {
        return _latitude;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(_longitude);
        sb.append(",");
        sb.append(_latitude);
        return sb.toString();
    }
}
