package map_to_kml.kml.data;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class KStyle {
    @XmlAttribute
    public String id;

    @XmlElement(name = "IconStyle")
    public KIconStyle iconStyle;

    @XmlElement(name = "LineStyle")
    public KLineStyle lineStyle;

}
