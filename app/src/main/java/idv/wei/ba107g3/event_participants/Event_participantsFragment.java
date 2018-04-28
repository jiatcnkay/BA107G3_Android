package idv.wei.ba107g3.event_participants;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.LinkedList;
import java.util.List;
import idv.wei.ba107g3.R;
import idv.wei.ba107g3.activity.Event;
import idv.wei.ba107g3.event.EventVO;
import idv.wei.ba107g3.main.Util;
import idv.wei.ba107g3.member.MemberVO;

import static android.content.Context.MODE_PRIVATE;

public class Event_participantsFragment extends Fragment {
    private static final String TAG = "Event_participantsFragment";
    private RecyclerView recyclerview_event_participants;
    private List<Event_participantsVO> showEvent = new LinkedList<>();
    private List<EventVO> allEve = new LinkedList<>();
    private ProgressDialog progressDialog;
    private MemberVO member;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_event_participants, null);
        recyclerview_event_participants = view.findViewById(R.id.recyclerview_event_participants);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        showEvent.clear();
        SharedPreferences pref = getActivity().getSharedPreferences(Util.PREF_FILE,MODE_PRIVATE);
        member = new Gson().fromJson(pref.getString("loginMem","").toString(), MemberVO.class);
        GetOneEve getOneEve = new GetOneEve();
        getOneEve.execute(member.getMem_no());
    }

    class GetOneEve extends AsyncTask<String, Void, List<Event_participantsVO>> {

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading...");
            progressDialog.show();
        }

        @Override
        protected List<Event_participantsVO> doInBackground(String... params) {
            Event_participantsDAO_interface dao = new Event_participantsDAO();
            return dao.getOneEve(params[0]);
        }


        @Override
        protected void onPostExecute(List<Event_participantsVO> event_participantsVOS) {
            super.onPostExecute(event_participantsVOS);
            progressDialog.cancel();
            Event_participantsVO event_participantsVO = new Event_participantsVO();
            event_participantsVO.setEvep_sts("未報到");
            event_participantsVO.setType(0);
            showEvent.add(event_participantsVO);
            for(Event_participantsVO eventParticipantsVO : event_participantsVOS){
                if(eventParticipantsVO.getEvep_sts().equals("未報到")){
                    eventParticipantsVO.setType(1);
                    showEvent.add(eventParticipantsVO);
                }
            }
            Event_participantsVO event_participantsVO1 = new Event_participantsVO();
            event_participantsVO1.setEvep_sts("已報到");
            event_participantsVO1.setType(0);
            showEvent.add(event_participantsVO1);
            for(Event_participantsVO eventParticipantsVO : event_participantsVOS){
                if(eventParticipantsVO.getEvep_sts().equals("已報到")){
                    eventParticipantsVO.setType(1);
                    showEvent.add(eventParticipantsVO);
                }
            }
            SharedPreferences pref = getActivity().getSharedPreferences(Util.PREF_FILE,MODE_PRIVATE);
            allEve = new Gson().fromJson(pref.getString("AllEvent","").toString(),new TypeToken<List<EventVO>>(){}.getType());
            recyclerview_event_participants.setHasFixedSize(true);
            recyclerview_event_participants.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
            recyclerview_event_participants.setAdapter(new Event_ParticipantsAdapter());

        }
    }

    private class Event_ParticipantsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        class TitleViewHolder extends RecyclerView.ViewHolder {
            private TextView title;

            public TitleViewHolder(View itemView) {
                super(itemView);
                title = itemView.findViewById(R.id.title);
            }
        }

        class TextViewHolder extends RecyclerView.ViewHolder {
            private TextView eve_name;
            private ImageView eve_pic,map;
            private Button btnCheckin;

            public TextViewHolder(View itemView) {
                super(itemView);
                eve_name = itemView.findViewById(R.id.eve_name);
                eve_pic = itemView.findViewById(R.id.eve_pic);
                map = itemView.findViewById(R.id.map);
                btnCheckin = itemView.findViewById(R.id.btnCheckin);
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemview;
            switch (viewType) {
                case Event_participantsVO.TITLE_TYPE:
                    itemview = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_event_participants_title, parent, false);
                    return new TitleViewHolder(itemview);
                case Event_participantsVO.TEXT_TYPE:
                    itemview = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_event_participants, parent, false);
                    return new TextViewHolder(itemview);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            final Event_participantsVO event_participantsVO = showEvent.get(position);
            switch (event_participantsVO.getType()){
                case Event_participantsVO.TITLE_TYPE:
                    ((TitleViewHolder) holder).title.setText(event_participantsVO.getEvep_sts());
                    break;
                case Event_participantsVO.TEXT_TYPE:
                    for(final EventVO eventVO : allEve){
                        if(eventVO.getEve_no().equals(event_participantsVO.getEve_no())){
                            ((TextViewHolder) holder).eve_name.setText(eventVO.getEve_name());
                            byte[] pic = eventVO.getEve_pic();
                            Bitmap bitmap = BitmapFactory.decodeByteArray(pic,0,pic.length);
                            ((TextViewHolder) holder).eve_pic.setImageBitmap(bitmap);
                            if(event_participantsVO.getEvep_sts().equals("未報到")) {
                                ((TextViewHolder) holder).map.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Bundle bundle = new Bundle();
                                        bundle.putSerializable("event", eventVO);
                                        Intent intent = new Intent(getActivity(), Event_map_activity.class);
                                        intent.putExtras(bundle);
                                        startActivity(intent);
                                    }
                                });

                                ((TextViewHolder) holder).btnCheckin.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Bundle bundle = new Bundle();
                                        bundle.putSerializable("evep", event_participantsVO);
                                        Intent intent = new Intent(getActivity(), Event_QR_activity.class);
                                        intent.putExtras(bundle);
                                        startActivity(intent);
                                    }
                                });
                            }else {
                                ((TextViewHolder) holder).map.setVisibility(View.INVISIBLE);
                                ((TextViewHolder) holder).btnCheckin.setVisibility(View.INVISIBLE);
                            }
                        }
                    }
                    break;
            }
        }

        @Override
        public int getItemCount() {
            return showEvent.size();
        }

        // 主動改寫getItemViewType方法，根據position回傳對應的識別整數
        // 這樣即可在onCreateViewHolder的int viewType參數取得
        @Override
        public int getItemViewType(int position) {
            if (showEvent != null) {
                Event_participantsVO event_participantsVO = showEvent.get(position);
                if (showEvent != null) {
                    return event_participantsVO.getType();
                }
            }
            return 0;
        }
    }
}
