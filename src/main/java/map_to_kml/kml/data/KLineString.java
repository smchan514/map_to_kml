package map_to_kml.kml.data;

import java.util.LinkedList;

import javax.xml.bind.annotation.XmlElement;

public class KLineString {
    // Private list of KCoordinates used to render the string representation
    // required for the XML element "coordinates"
    private final LinkedList<KCoordinates> _coordinates = new LinkedList<>();

    @XmlElement
    public int tesselate = 1;

    public void addCoordinates(KCoordinates coord) {
        _coordinates.add(coord);
    }

    @XmlElement
    public String getCoordinates() {
        StringBuilder sb = new StringBuilder();
        for (KCoordinates coords : _coordinates) {
            sb.append(coords.toString());
            sb.append(" ");
        }
        return sb.toString();
    }

}
