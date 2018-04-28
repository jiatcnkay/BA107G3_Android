package idv.wei.ba107g3.gift;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.util.List;

import idv.wei.ba107g3.gift_label_detail.GiftLabelDetailVO;
import idv.wei.ba107g3.main.Util;

public class GiftDAO implements GiftDAO_interface {
    private static final String TAG = "GiftDAO";

    @Override
    public void insert(GiftVO giftVO, List<GiftLabelDetailVO> giftLabelDetailList) {

    }

    @Override
    public void update(GiftVO giftVO, List<GiftLabelDetailVO> giftLabelDetailList) {

    }

    @Override
    public void updateTrackQty(String gift_no, Integer gift_track_qty, Connection con) {

    }

    @Override
    public void updateBuyQty(GiftVO giftVO, Integer gift_buy_qty, Connection con) {

    }

    @Override
    public void delete(String gift_no) {

    }

    @Override
    public GiftVO getByPrimaryKey(String gift_no) {
        return null;
    }

    @Override
    public List<GiftVO> getAll() {
        String urlString = Util.URL + "/android/GiftServlet";
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
            String req = "action=getALL&imageSize=200";
            dos.writeBytes(req);
            dos.flush();

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                inStr = new StringBuilder();
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = br.readLine()) != null) {
                    inStr.append(line);
                }
                br.close();
            } else {
                Log.d(TAG, "response code: " + responseCode);
            }
        } catch (IOException e) {
            Log.d(TAG, e.toString());
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
            Gson gson = new Gson();
            Type listType = new TypeToken<List<GiftVO>>(){
            }.getType();
            return gson.fromJson(inStr.toString(),listType);
        }
        return null;
    }

    @Override
    public List<GiftVO> getByKeyWord(String keyword) {
        String urlString = Util.URL + "/android/GiftServlet";
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
            String req = "action=getByKeyWord&imageSize=200&keyword=";
            dos.writeBytes(req + URLEncoder.encode(keyword, "UTF-8"));
            dos.flush();

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                inStr = new StringBuilder();
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = br.readLine()) != null) {
                    inStr.append(line);
                }
                br.close();
            } else {
                Log.d(TAG, "response code: " + responseCode);
            }
        } catch (IOException e) {
            Log.d(TAG, e.toString());
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
            Gson gson = new Gson();
            Type listType = new TypeToken<List<GiftVO>>(){
            }.getType();
            return gson.fromJson(inStr.toString(),listType);
        }
        return null;
    }

    @Override
    public byte[] getPic(String gift_no) {
        return new byte[0];
    }
}
