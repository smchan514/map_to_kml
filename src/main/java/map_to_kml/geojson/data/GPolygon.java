package map_to_kml.geojson.data;

import java.util.List;

/**
 * <UL>
 * <LI>Polygon: List of LineString, first being the exterior ring and any others
 * the interior rings, per RFC 7946</LI>
 * <LI>LineString: List of Point</LI>
 * </UL>
 * 
 * Consequently: Polygon is List&lt;List&lt;Point&gt;&gt;
 * 
 * @see https://datatracker.ietf.org/doc/html/rfc7946#section-3.1.7
 */
public class GPolygon extends Geometry<List<List<GLonLatPoint>>> {

    public GPolygon() {
        // ...
    }
}
