package map_to_kml.kml.data;

import javax.xml.bind.annotation.XmlElement;

public class KIconStyle {
    @XmlElement
    public float scale;

    @XmlElement(name = "Icon")
    public KIcon icon;

    public KHotSpot hotSpot;

}
