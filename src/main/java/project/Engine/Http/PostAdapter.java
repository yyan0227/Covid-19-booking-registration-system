package project.Engine.Http;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.json.JSONObject;

import java.io.IOException;
import java.net.http.HttpResponse;

public class PostAdapter {
    public static <T> T convertHttpPostResponse(
            HttpResponse<String> postResponse, Class<T> targetType,
            JsonDeserializer<T> customDeserializer
    ) {
        JSONObject responseJson = new JSONObject(postResponse.body());
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        SimpleModule module = new SimpleModule();
        module.addDeserializer(targetType, customDeserializer);
        objectMapper.registerModule(module);

        T newObjectT = null;
        try {
            newObjectT = objectMapper.readValue(responseJson.toString(), targetType);
        } catch (IOException e) {
            String msg = String.format("Unable to parse JSON to %s objects: ", targetType.toString());
            System.out.println(msg);
            System.out.println(e.getMessage());
        }
        return newObjectT;
    }
}
