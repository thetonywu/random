import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import org.json.JSONArray;
import org.json.JSONObject;

import java.security.SecureRandom;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by tony on 1/13/17.
 * Random number generator based on random.org api
 */
public class RandomOrg extends SecureRandom {

    private List<Integer> queue = new LinkedList<Integer>();

    public void nextBytes(byte[] bytes){
        for (int i = 0, len = bytes.length; i < len; )
            for (int rnd = nextInt(),
                 n = Math.min(len - i, Integer.SIZE/Byte.SIZE);
                 n-- > 0; rnd >>= Byte.SIZE)
                bytes[i++] = (byte)rnd;
    }

    private void getMoreRandoms() {
        // todo make this nicer to configure the params instead of just having the whole blob as string
        String body = "{\"jsonrpc\":\"2.0\"," +
                "\"method\":\"generateIntegers\"" +
                ",\"params\":{" +
                "\"apiKey\":\"a12df6e9-8e1d-4ef2-b9e2-db27a9c79e59\"," +
                "\"n\":1000," +
                "\"min\":0," +
                "\"max\":1000000000," +
                "\"replacement\":true," +
                "\"base\":10}," +
                "\"id\":17905}";

        try {
            HttpResponse<JsonNode> resp = Unirest.post("https://api.random.org/json-rpc/1/invoke")
                    .body(body)
                    .asJson();

            JSONObject result = (JSONObject)resp.getBody().getObject().get("result");
            JSONObject random = (JSONObject)result.get("random");

            JSONArray randomData = (JSONArray)random.get("data");

            for(int i=0; i< randomData.length(); i++) {
                queue.add(randomData.getInt(i));
            }

        } catch (Exception e) {
            // todo ideally we'd want to handle this and take appropriate action based on exception (eg. retry, etc)
            System.out.println(e);
        }

    }

    public int nextRGB() {
        return nextInt() % 256;
    }

    /*
     * Right now this just ranges from 0 to 1000000000 since the api won't return us a full 32 bit integer. With a bit of smarts
     * we can improve this to derive a random integer in the full range of a java int based off of what the api is capable of returning
     */
    public int nextInt() {
        // nothing left in the queue, go grab more from the api
        // ideally we wouldn't do it just-in-time like this (since it is blocking)
        // we should pre-emptively check if the queue is running out
        // and kick off a background thread to call the api and add more to the queue
        if (queue.size() == 0)
            getMoreRandoms();

        return queue.remove(0);
    }
}
