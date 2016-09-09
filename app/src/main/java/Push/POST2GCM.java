package Push;

import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class POST2GCM {

    private static final String TAG = "HEllllooooo";

    public static void post(String apiKey, Content content){

        try{

        // 1. URL
        URL url = new URL("https://android.googleapis.com/gcm/send");

        // 2. Open cjava.lang.Stringonnection
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        // 3. Specify POST method
        conn.setRequestMethod("POST");

        // 4. Set the headers
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "key="+apiKey);

        conn.setDoOutput(true);
        
        

            // 5. Add JSON data into POST request body

            //`5.1 Use Jackson object mapper to convert Contnet object into JSON
            ObjectMapper mapper = new ObjectMapper();
            //ObjectMapper.setVisibility(JsonMethod.FIELD, Visibility.ANY);

            // 5.2 Get connection output stream
            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());

            // 5.3 Copy Content "JSON" into
            mapper.writeValue(wr, content);
            Log.d(TAG, "Hellp" + content);
            // 5.4 Send the request
            wr.flush();

            // 5.5 close
            wr.close();

            // 6. Get the response
            int responseCode = conn.getResponseCode();
            Log.d(TAG, "\nSending 'POST' request to URL : " + url);
            Log.d(TAG, "Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // 7. Print result
            Log.d(TAG, response.toString());

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
}