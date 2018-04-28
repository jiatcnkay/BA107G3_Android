package idv.wei.ba107g3.ws;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.nio.ByteBuffer;
import java.util.Locale;

    public class MemberWebSocketClient extends WebSocketClient {
        private static final String TAG = "MemberWebSocketClient";
        private Gson gson;
        private Context context;

        public MemberWebSocketClient(URI serverURI, Context context) {
            // Draft_17是連接協議，就是標準的RFC 6455（JSR256）
            super(serverURI, new Draft_17());
            this.context = context;
            gson = new Gson();
        }

        @Override
        public void onOpen(ServerHandshake handshakeData) {
            String text = String.format(Locale.getDefault(),
                    "onOpen: Http status code = %d; status message = %s",
                    handshakeData.getHttpStatus(),
                    handshakeData.getHttpStatusMessage());
            Log.d(TAG, "onOpen: " + text);
        }

        // 訊息內容多(例如：圖片)，server端必須以byte型式傳送，此方法可以接收byte型式資料
        @Override
        public void onMessage(ByteBuffer bytes) {
            int length = bytes.array().length;
            String message = new String(bytes.array());
            Log.d(TAG, "onMessage(ByteBuffer): length = " + length);
            onMessage(message);
        }

        @Override
        public void onMessage(String message) {
            JsonObject jsonObject = gson.fromJson(message, JsonObject.class);
            String type = jsonObject.get("type").getAsString();
            Log.e(TAG,type);
            sendMessageBroadcast(type, message);
        }

        @Override
        public void onClose(int code, String reason, boolean remote) {
        }

        @Override
        public void onError(Exception ex) {
            Log.d(TAG, "onError: exception = " + ex.toString());
        }

        private void sendMessageBroadcast(String messageType, String message) {
            Intent intent = new Intent(messageType);
            Log.e(TAG, "onMessage: " + message);
            intent.putExtra("message", message);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }
}

