package project.Engine.Http;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Follows the Adapter pattern. This class contains the logic to convert a HttpResponse object into
 * a list of HealthFacility objects so that client doesn't need to know anything about the conversion.
 * (Client only cares about getting a list of facility objects)
 */
public class SearchAdapter {

    public static <T> List<T> getAllAvailableObjectsOnWebService(
            String lookupURL, Class<T> targetType, JsonDeserializer<T> customDeserializer
    ) {
        List<T> objectList = new ArrayList<>();
        HttpResponse<String> response = Http.search(lookupURL);

        if (response != null) {
            List<JSONObject> jsons = new ArrayList<>();
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            SimpleModule module = new SimpleModule();
            module.addDeserializer(targetType, customDeserializer);
            objectMapper.registerModule(module);
            try {
                // https://stackoverflow.com/questions/15609306/convert-string-to-json-array
                JSONArray jsonArray = new JSONArray(response.body());
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    jsons.add(jsonObject);
                }

                for (JSONObject jsonObject : jsons) {
                     // https://www.baeldung.com/jackson-object-mapper-tutorial
                    T objectT = objectMapper.readValue(jsonObject.toString(), targetType);
                    objectList.add(objectT);
                }
            } catch (IOException e) {
                System.out.println("Unable to parse JSON to HealthFacility objects: ");
                System.out.println(e.getMessage());
            } catch (org.json.JSONException e) {
                System.out.println("Unable to parse string to JSON array: ");
                System.out.println(e.getMessage());
            }
        }
        return objectList;
    }
}
