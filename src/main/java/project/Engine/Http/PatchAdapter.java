package project.Engine.Http;

import com.fasterxml.jackson.databind.JsonDeserializer;
import org.json.JSONObject;

import java.net.http.HttpResponse;

public class PatchAdapter {
    public static <T> T patchResource(
            String endpoint_URL, JSONObject updateJson, Class<T> targetType,
            JsonDeserializer<T> customDeserializer
    ) {
        HttpResponse<String> response = Http.search(endpoint_URL);
        JSONObject originalJSON = new JSONObject(response.body());

        // only the 'additionalInfo' in the JSON needs merging
        updateJson = mergeAdditionalInfo(originalJSON, updateJson);
        HttpResponse<String> patchResponse = Http.patch(endpoint_URL, updateJson);

        // Http POST & PATCH returns the same object, so the POST adapter can be reused here
        return PostAdapter.convertHttpPostResponse(
                patchResponse,targetType, customDeserializer
        );
    }

    private static JSONObject mergeAdditionalInfo(JSONObject source, JSONObject target) {
        JSONObject sourceAddInfo = (JSONObject) source.get("additionalInfo");
        JSONObject targetAddInfo = (JSONObject) target.get("additionalInfo");
        for (String key : sourceAddInfo.keySet()) {
            if (!targetAddInfo.has(key)) {
                targetAddInfo.put(key, sourceAddInfo.get(key));
            }
        }
        target.put("additionalInfo", targetAddInfo);
        return target;
    }
}
