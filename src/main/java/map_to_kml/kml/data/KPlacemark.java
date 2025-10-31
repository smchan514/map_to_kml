package map_to_kml.kml.data;

import javax.xml.bind.annotation.XmlElement;

public class KPlacemark {
    @XmlElement
    public String name;

    @XmlElement
    public String description;

    @XmlElement
    public int visibility;

    @XmlElement
    public String styleUrl;

    @XmlElement(name = "LineString")
    public KLineString lineString;

    @XmlElement(name = "Point")
    public KPoint point;

}
