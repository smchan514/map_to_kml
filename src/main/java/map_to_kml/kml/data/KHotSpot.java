package map_to_kml.kml.data;

import javax.xml.bind.annotation.XmlElement;

public class KHotSpot {
    @XmlElement
    public int x;

    @XmlElement
    public int y;

    @XmlElement
    public String xunits;

    @XmlElement
    public String yunits;

}
