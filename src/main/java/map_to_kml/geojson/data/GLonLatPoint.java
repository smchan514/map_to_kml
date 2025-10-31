package map_to_kml.geojson.data;

public class GLonLatPoint {

    private float _lon;
    private float _lat;

    public GLonLatPoint() {
        // ...
    }

    public void setLon(float lon) {
        _lon = lon;
    }

    public float getLon() {
        return _lon;
    }

    public void setLat(float lat) {
        _lat = lat;
    }

    public float getLat() {
        return _lat;
    }

}
