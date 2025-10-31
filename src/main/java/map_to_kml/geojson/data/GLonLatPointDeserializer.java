package map_to_kml.geojson.data;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

/**
 * Deserializer for GeoJson Lon/Lat points which is expected to an array of two
 * floats, e.g. "[-53.111710, 47.342075]"
 */
public class GLonLatPointDeserializer extends JsonDeserializer<GLonLatPoint> {
    public GLonLatPointDeserializer() {
        // ...
    }

    @Override
    public GLonLatPoint deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        if (p.isExpectedStartArrayToken()) {
            return deserializeArray(p, ctxt);
        }
        return (GLonLatPoint) ctxt.handleUnexpectedToken(GLonLatPoint.class, p);
    }

    private GLonLatPoint deserializeArray(JsonParser p, DeserializationContext ctxt) throws IOException {
        GLonLatPoint llp = new GLonLatPoint();
        llp.setLon(parseNumber(p, ctxt));
        llp.setLat(parseNumber(p, ctxt));

        JsonToken token = p.nextToken();
        if (token != JsonToken.END_ARRAY) {
            return (GLonLatPoint) ctxt.handleUnexpectedToken(GLonLatPoint.class, token, p, "Expecting END_ARRAY");
        }

        return llp;
    }

    private float parseNumber(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonToken token = p.nextToken();
        switch (token) {
        case VALUE_NUMBER_FLOAT:
            return p.getFloatValue();
        case VALUE_NUMBER_INT:
            return p.getIntValue();

        default:
            return (float) ctxt.handleUnexpectedToken(GLonLatPoint.class, token, p, "Expecting float or int");
        }
    }

}
