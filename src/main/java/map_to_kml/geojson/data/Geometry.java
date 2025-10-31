package map_to_kml.geojson.data;

/**
 * Base class for a "geometry"
 * 
 * @see https://github.com/opendatalab-de/geojson-jackson
 */
public class Geometry<T> extends GeojsonObject {

    private T coordinates;

    public Geometry() {
        // ...
    }

    public T getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(T coordinates) {
        this.coordinates = coordinates;
    }

}
