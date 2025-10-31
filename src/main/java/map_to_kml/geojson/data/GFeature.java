package map_to_kml.geojson.data;

import java.util.HashMap;
import java.util.Map;

public class GFeature extends GeojsonObject {
    public Map<String, Object> properties = new HashMap<String, Object>();
    public Geometry<?> geometry;
}
