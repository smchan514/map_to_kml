package map_to_kml.kml.data;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

public class KFolder {
    @XmlElement
    public String name;

    @XmlElement
    public String description;

    @XmlElement
    public int open;

    @XmlElement(name = "Placemark")
    public List<KPlacemark> placemarks = new LinkedList<>();

    @XmlElement(name = "Folder")
    public List<KFolder> folders = new LinkedList<>();

}
