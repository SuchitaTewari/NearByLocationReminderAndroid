package project.suchita.com.nearbyreminder;


import com.loopj.android.http.*;

/**
 * Created by SuchitaTewari on 6/10/16.
 */
public class RestClient {

    private static final String BASE_URL = "https://maps.googleapis.com/maps/api/place/search/json?";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}
