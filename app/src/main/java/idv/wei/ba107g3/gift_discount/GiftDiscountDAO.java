package idv.wei.ba107g3.gift_discount;

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
import java.util.List;

import idv.wei.ba107g3.gift.GiftVO;
import idv.wei.ba107g3.main.Util;

/**
 * Created by WEI on 2018/3/24.
 */

public class GiftDiscountDAO implements GiftDiscountDAO_interface {
    private static final String TAG = "GiftDiscountDAO";
    @Override
    public void insert(GiftDiscountVO giftDiscountVO) {

    }

    @Override
    public void update(GiftDiscountVO giftDiscountVO) {

    }

    @Override
    public void delete(String giftd_no) {

    }

    @Override
    public GiftDiscountVO getByPrimaryKey(String giftd_no) {
        return null;
    }

    @Override
    public List<GiftDiscountVO> getAll() {
        String urlString = Util.URL + "/android/GiftDiscountServlet";
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
            String req = "action=getAll";
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
            Type listType = new TypeToken<List<GiftDiscountVO>>(){
            }.getType();
            Log.e(TAG,"GiftDiscountVO = "+ gson.fromJson(inStr.toString(),listType));
            return gson.fromJson(inStr.toString(),listType);
        }
        return null;
    }

    @Override
    public List<GiftVO> getGiftD() {
        String urlString = Util.URL + "/android/GiftDiscountServlet";
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
            String req = "action=getGiftD&imageSize=180";
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
}
