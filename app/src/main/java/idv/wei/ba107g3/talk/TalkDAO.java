package idv.wei.ba107g3.talk;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import idv.wei.ba107g3.main.Util;

public class TalkDAO implements TalkDAO_interface {
    private final static String TAG = "TalkDAO";

    @Override
    public String findTalkByFriends(String self_no,String fri_no) {
        String urlString = Util.URL + "/android/TalkServlet";
        Log.e(TAG,"URL="+urlString);
        DataOutputStream dos = null;
        HttpURLConnection connection = null;
        StringBuilder inStr = null;
        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.connect();
            dos = new DataOutputStream(connection.getOutputStream());
            String req = "action=getTalk&self_no="+self_no+"&fri_no="+fri_no;
            dos.writeBytes(req);
            dos.flush();

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                inStr = new StringBuilder();
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line = "";
                while ((line = br.readLine()) != null) {
                    inStr.append(line);
                }
                br.close();
            } else {
                Log.d(TAG, "response code: " + responseCode);
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        } finally {
            if (dos != null) {
                try {
                    dos.close();
                } catch (IOException e) {
                    Log.e(TAG, e.toString());
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }if(inStr != null) {
            Log.e(TAG,inStr.toString());
            return inStr.toString();
        }
        return null;
    }
}
