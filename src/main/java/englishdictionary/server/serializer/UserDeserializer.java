package englishdictionary.server.serializer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import englishdictionary.server.models.testing.User;

import java.io.IOException;

public class UserDeserializer extends JsonDeserializer<User> {

    @Override
    public User deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        User user = jsonParser.readValueAs(User.class);

        return null;
    }
}
