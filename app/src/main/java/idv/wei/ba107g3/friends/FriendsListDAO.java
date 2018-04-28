package idv.wei.ba107g3.friends;

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

import idv.wei.ba107g3.main.Util;
import idv.wei.ba107g3.member.MemberVO;

public class FriendsListDAO implements FriendsListDAO_interface {
    private static final String TAG = "FriendsListDAO";
    @Override
    public void insert(String mem_no_self,String mem_no_other) {
        String urlString = Util.URL + "/android/FriendsListServlet";
        Log.e(TAG,"URL="+urlString);
        DataOutputStream dos = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.connect();
            dos = new DataOutputStream(connection.getOutputStream());
            Log.e(TAG,"mem_no_self="+mem_no_self+"mem_no_other="+mem_no_other);
            String req = "action=insertFriend&mem_no_self="+mem_no_self+"&mem_no_other="+mem_no_other;
            dos.writeBytes(req);
            dos.flush();
            Log.e(TAG, String.valueOf(connection.getResponseCode()));
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
        }
    }

    @Override
    public void add(String mem_no_self,String mem_no_other) {
        String urlString = Util.URL + "/android/FriendsListServlet";
        Log.e(TAG,"URL="+urlString);
        DataOutputStream dos = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.connect();
            dos = new DataOutputStream(connection.getOutputStream());
            String req = "action=addFriend&mem_no_self="+mem_no_self+"&mem_no_other="+mem_no_other;
            dos.writeBytes(req);
            dos.flush();
            Log.e(TAG, String.valueOf(connection.getResponseCode()));
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
        }
    }

    @Override
    public void update(FriendsListVO frilistVO) {

    }

    @Override
    public void delete(String mem_no_self, String mem_no_other) {
        String urlString = Util.URL + "/android/FriendsListServlet";
        Log.e(TAG,"URL="+urlString);
        DataOutputStream dos = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.connect();
            dos = new DataOutputStream(connection.getOutputStream());
            Log.e(TAG,"mem_no_self="+mem_no_self+"mem_no_other="+mem_no_other);
            String req = "action=deleteFriend&mem_no_self="+mem_no_self+"&mem_no_other="+mem_no_other;
            dos.writeBytes(req);
            dos.flush();
            Log.e(TAG, String.valueOf(connection.getResponseCode()));
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
        }
    }

    @Override
    public FriendsListVO findByPrimaryKey(String mem_no_self, String mem_no_other) {
        return null;
    }

    @Override
    public List<FriendsListVO> getAll() {
        return null;
    }

    @Override
    public List<MemberVO> getMemberFriends(String mem_no) {
        String urlString = Util.URL + "/android/FriendsListServlet";
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
            Log.e(TAG,"mem_no="+mem_no);
            String req = "action=getMemberFriends&imageSize=300&mem_no="+mem_no;
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
            Gson gson = new Gson();
            Log.e(TAG,"friendList = " + inStr);
            Type listType = new TypeToken<List<MemberVO>>(){
            }.getType();
            return gson.fromJson(inStr.toString(),listType);
        }
        return null;
    }

    public Boolean havewait(String mem_no_self,String mem_no_other){
        String urlString = Util.URL + "/android/FriendsListServlet";
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
            String req = "action=havewait&mem_no_self="+mem_no_self+"&mem_no_other="+mem_no_other;
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
        }
        return Boolean.valueOf(inStr.toString());
    }
}
