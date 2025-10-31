package map_to_kml.kml.data;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Document")
public class KDocument {

    @XmlElement
    public String name;

    @XmlElement
    public String description;

    @XmlElement(name = "StyleMap")
    public List<KStyleMap> styleMaps = new LinkedList<>();

    @XmlElement(name = "Style")
    public List<KStyle> styles = new LinkedList<>();

    @XmlElement(name = "Folder")
    public List<KFolder> folders = new LinkedList<>();

}
