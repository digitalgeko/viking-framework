package nl.viking

import org.codehaus.jackson.JsonGenerator
import org.codehaus.jackson.JsonProcessingException
import org.codehaus.jackson.map.JsonSerializer
import org.codehaus.jackson.map.SerializerProvider
import org.codehaus.jackson.map.util.ISO8601DateFormat

/**
 * Created by mardo on 2/26/15.
 */
class DateSerializer extends JsonSerializer<Date> {

    @Override
    void serialize(Date value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
        jgen.writeString(new ISO8601DateFormat().format(value))
    }
    
}
