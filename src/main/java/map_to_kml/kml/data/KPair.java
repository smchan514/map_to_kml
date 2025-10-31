package map_to_kml.kml.data;

import javax.xml.bind.annotation.XmlElement;

public class KPair {
    @XmlElement
    public String key;

    @XmlElement
    public String styleUrl;
}
