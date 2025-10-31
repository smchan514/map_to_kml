package map_to_kml.kml.data;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class KStyleMap {
    @XmlAttribute
    public String id;

    @XmlElement(name = "Pair")
    public List<KPair> pairs = new LinkedList<>();

}
