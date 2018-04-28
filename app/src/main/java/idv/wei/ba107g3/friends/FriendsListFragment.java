package idv.wei.ba107g3.friends;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import idv.wei.ba107g3.R;
import idv.wei.ba107g3.activity.Talk;
import idv.wei.ba107g3.main.MainActivity;
import idv.wei.ba107g3.main.Util;
import idv.wei.ba107g3.member.MemberProfileActivity;
import idv.wei.ba107g3.member.MemberVO;
import idv.wei.ba107g3.talk.TalkActivity;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.content.Context.MODE_PRIVATE;
import static idv.wei.ba107g3.main.Util.CART;
import static idv.wei.ba107g3.main.Util.TALK;

public class FriendsListFragment extends Fragment {
    private Dialog dialog;
    private Button btnTalk, btnMem, btnBlock, btnUnBlock, btnback, btnconfirm;
    private TextView dialog_name, dialog_gender, dialog_age;
    private ImageView dialog_photo, dialog_close;
    public static RecyclerView recyclerView_friendList;
    private MemberVO memberVO;
    private List<MemberVO> friendList;
    private EditText searchfriend;
    private LocalBroadcastManager broadcastManager;
    public static List<String> onlineList = new ArrayList<>();
    private String onlineMemNo, offlineMemNo;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_friendslist, container, false);
        searchfriend = view.findViewById(R.id.searchfriend);
        SharedPreferences pref = getActivity().getSharedPreferences(Util.PREF_FILE, MODE_PRIVATE);
        memberVO = new Gson().fromJson(pref.getString("loginMem", ""), MemberVO.class);
        recyclerView_friendList = view.findViewById(R.id.recyclerview_friendlist);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        GetFriendsList getFriendsList = new GetFriendsList();
        getFriendsList.execute(memberVO.getMem_no());
    }

    class GetFriendsList extends AsyncTask<String, Void, List<MemberVO>> {
        @Override
        protected List<MemberVO> doInBackground(String... params) {
            FriendsListDAO_interface dao = new FriendsListDAO();
            return dao.getMemberFriends(params[0]);
        }

        @Override
        protected void onPostExecute(List<MemberVO> memberVOS) {
            super.onPostExecute(memberVOS);
            SharedPreferences pref = getActivity().getSharedPreferences(Util.PREF_FILE, MODE_PRIVATE);
            pref.edit().putString("friendsList", new Gson().toJson(memberVOS)).apply();
            friendList = memberVOS;
            recyclerView_friendList.setHasFixedSize(true);
            recyclerView_friendList.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
            FriendAdapter friendAdapter = new FriendAdapter();
            recyclerView_friendList.setAdapter(friendAdapter);
            ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(friendAdapter);
            ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
            touchHelper.attachToRecyclerView(recyclerView_friendList);
            broadcastManager = LocalBroadcastManager.getInstance(getContext());
            registerFriendStateReceiver();
        }
    }

    // 攔截user連線或斷線的Broadcast
    private void registerFriendStateReceiver() {
        IntentFilter notifyEveryFilter = new IntentFilter("sendEveryFri");
        IntentFilter openFilter = new IntentFilter("sendSelf");
        IntentFilter closeFilter = new IntentFilter("leave");
        FriendStateReceiver friendStateReceiver = new FriendStateReceiver();
        broadcastManager.registerReceiver(friendStateReceiver, notifyEveryFilter);
        broadcastManager.registerReceiver(friendStateReceiver, openFilter);
        broadcastManager.registerReceiver(friendStateReceiver, closeFilter);
    }

    // 攔截user連線或斷線的Broadcast，並在RecyclerView呈現
    private class FriendStateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            JsonObject jsonObject = new Gson().fromJson(message, JsonObject.class);
            String type = jsonObject.get("type").getAsString();
            switch (type) {
                case "sendEveryFri":
                    onlineMemNo = jsonObject.get("memberNO").getAsString();
                    recyclerView_friendList.getAdapter().notifyDataSetChanged();
                    break;
                case "sendSelf":
                    String onlineFri = jsonObject.get("onlineFri").toString();
                    String token = onlineFri.substring(2, onlineFri.length() - 2);
                    String[] tokens = token.split("[\",]");
                    for (int i = 0; i < tokens.length; i++) {
                        if (tokens[i].length() != 0)
                            onlineList.add(tokens[i]);
                    }
                    for (String s : onlineList) {
                        Log.e("TAG", "LIST=" + s);
                    }
                    recyclerView_friendList.getAdapter().notifyDataSetChanged();
                    break;
                case "leave":
                    offlineMemNo = jsonObject.get("memberNO").getAsString();
                    recyclerView_friendList.getAdapter().notifyDataSetChanged();
                    break;
            }
        }

    }

    class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> implements ItemTouchHelperAdapter {

        class ViewHolder extends RecyclerView.ViewHolder {
            private ImageView friendPhoto, getOnline;
            private TextView friendName;
            private CardView cardview_friendList;
            private float defaultZ;

            public ViewHolder(View itemView) {
                super(itemView);
                friendPhoto = itemView.findViewById(R.id.friendPhoto);
                friendName = itemView.findViewById(R.id.friendName);
                cardview_friendList = itemView.findViewById(R.id.cardview_friendList);
                getOnline = itemView.findViewById(R.id.getOnline);
                defaultZ = itemView.getTranslationZ();
            }

        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(getContext()).inflate(R.layout.cardview_friendlistfragment, parent, false);
            ViewHolder viewHolder = new ViewHolder(itemView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final ViewHolder viewholder, int position) {
            final MemberVO member = friendList.get(position);
            byte[] photo = member.getMem_photo();
            final Bitmap bitmap = BitmapFactory.decodeByteArray(photo, 0, photo.length);
            viewholder.friendPhoto.setImageBitmap(Util.getCircleBitmap(bitmap, 300));
            viewholder.friendName.setText(member.getMem_name());
            if (onlineMemNo != null) {
                if (onlineMemNo.equals(member.getMem_no())) {
                    viewholder.getOnline.setImageResource(R.drawable.online);
                }
            }
            if (onlineList.size() != 0) {
                if (position <= onlineList.size() - 1) {
                    if (onlineList.get(position).equals(member.getMem_no())) {
                        viewholder.getOnline.setImageResource(R.drawable.online);
                    }
                }
            }
            if (offlineMemNo != null) {
                if (offlineMemNo.equals(member.getMem_no())) {
                    viewholder.getOnline.setImageResource(R.drawable.offline);
                }
            }
            viewholder.cardview_friendList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog = new Dialog(getContext());
                    dialog.setTitle("Friend");
                    dialog.setCancelable(true);
                    dialog.setContentView(R.layout.dialog_friendslist);
                    Window dw = dialog.getWindow();
                    WindowManager.LayoutParams lp = dw.getAttributes();
                    lp.alpha = 1.0f;
                    lp.width = 1000;
                    lp.height = 1350;
                    dw.setAttributes(lp);
                    dialog_photo = dialog.findViewById(R.id.dialog_photo);
                    dialog_name = dialog.findViewById(R.id.dialog_name);
                    dialog_age = dialog.findViewById(R.id.dialog_age);
                    dialog_gender = dialog.findViewById(R.id.dialog_gender);
                    dialog_close = dialog.findViewById(R.id.dialog_close);
                    dialog_photo.setImageBitmap(bitmap);
                    dialog_name.setText(member.getMem_name());
                    dialog_age.setText(Util.getAge(member.getMem_age()));
                    dialog_gender.setText(member.getMem_gender());
                    dialog_close.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.cancel();
                        }
                    });
                    btnMem = dialog.findViewById(R.id.btnMem);
                    btnMem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getActivity(), MemberProfileActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("member", member);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    });
                    btnTalk = dialog.findViewById(R.id.btnTalk);
                    btnTalk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getActivity(), TalkActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("member", member);
                            intent.putExtras(bundle);
                            int index = Util.TALK.indexOf(member);
                            if (index == -1) {
                                TALK.add(member);
                            }
                            startActivity(intent);
                        }
                    });
                    dialog.show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return friendList.size();
        }

        @Override
        public boolean onItemMove(int fromPosition, int toPosition) {
            // 將集合裡的資料進行交換
            Collections.swap(friendList, fromPosition, toPosition);
            notifyItemMoved(fromPosition, toPosition);
            return true;
        }

        @Override
        public void onItemDismiss(final int position) {
            final MemberVO member = friendList.get(position);
            friendList.remove(position);
            dialog = new Dialog(getContext());
            dialog.setTitle("deleteConfirm");
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.dialog_friendslist_delete);
            Window dw = dialog.getWindow();
            WindowManager.LayoutParams lp = dw.getAttributes();
            lp.alpha = 1.0f;
            lp.width = 1000;
            lp.height = 650;
            dw.setAttributes(lp);
            btnback = dialog.findViewById(R.id.btnback);
            btnconfirm = dialog.findViewById(R.id.btnconfirm);
            btnconfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences pref = getActivity().getSharedPreferences(Util.PREF_FILE, MODE_PRIVATE);
                    String memJson = pref.getString("loginMem", "");
                    String mem_no_self = new Gson().fromJson(memJson.toString(), MemberVO.class).getMem_no();
                    DeleteFriend deleteFriend = new DeleteFriend();
                    deleteFriend.execute(mem_no_self, member.getMem_no());
                    TALK.remove(member);
                    dialog.cancel();
                }
            });
            btnback.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                    friendList.add(member);
                    FriendAdapter friendAdapter = new FriendAdapter();
                    recyclerView_friendList.setAdapter(friendAdapter);
                }
            });
            FriendAdapter friendAdapter = new FriendAdapter();
            recyclerView_friendList.setAdapter(friendAdapter);
            dialog.show();
            notifyItemRemoved(position);
        }
    }


    private class SimpleItemTouchHelperCallback extends ItemTouchHelper.Callback {
        private ItemTouchHelperAdapter adapter;

        public SimpleItemTouchHelperCallback(ItemTouchHelperAdapter adapter) {
            this.adapter = adapter;
        }

        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            // 也可以設ItemTouchHelper.RIGHT(向右滑)，或是 ItemTouchHelper.START | ItemTouchHelper.END (左右滑都可以)
            int swipeFlags = ItemTouchHelper.RIGHT;
            return makeMovementFlags(dragFlags, swipeFlags);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            adapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
            return true;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            adapter.onItemDismiss(viewHolder.getAdapterPosition());
        }
    }

    class DeleteFriend extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            FriendsListDAO_interface dao = new FriendsListDAO();
            dao.delete(params[0], params[1]);
            return null;
        }
    }

}
