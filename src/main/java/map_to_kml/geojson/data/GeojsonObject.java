package map_to_kml.geojson.data;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

/**
 * Base class for all GeoJson classes.
 * 
 * Current implementation focuses on deserialisation only.
 */
@JsonTypeInfo(property = "type", use = Id.NAME)
@JsonSubTypes({ @Type(name = "FeatureCollection", value = GFeatureCollection.class),
        @Type(name = "Feature", value = GFeature.class), @Type(name = "Polygon", value = GPolygon.class),
        @Type(name = "MultiPolygon", value = GMultiPolygon.class), })
public class GeojsonObject {
    // ...
}
