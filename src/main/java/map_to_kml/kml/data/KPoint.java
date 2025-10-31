package map_to_kml.kml.data;

import javax.xml.bind.annotation.XmlElement;

public class KPoint {
    // Private instance of KCoordinates used to render the string representation
    // required for the XML element "coordinates"
    private KCoordinates _coordinates;

    public KPoint(float lon, float lat) {
        _coordinates = new KCoordinates(lon, lat);
    }

    public void setCoordinates(KCoordinates coord) {
        _coordinates = coord;
    }

    @XmlElement
    public String getCoordinates() {
        if (_coordinates == null) {
            return null;
        }

        return _coordinates.toString();
    }

}
