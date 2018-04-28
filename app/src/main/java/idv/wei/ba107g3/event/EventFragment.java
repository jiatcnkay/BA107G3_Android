package idv.wei.ba107g3.event;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.lang.ref.PhantomReference;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

import idv.wei.ba107g3.R;
import idv.wei.ba107g3.main.Util;
import idv.wei.ba107g3.member.AdvancedSearchActivity;
import idv.wei.ba107g3.member.BasicSearchFragment;
import idv.wei.ba107g3.member.MemberDAO;
import idv.wei.ba107g3.member.MemberDAO_interface;
import idv.wei.ba107g3.member.MemberProfileActivity;
import idv.wei.ba107g3.member.MemberVO;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

public class EventFragment extends Fragment {
    private static final String TAG = "EventFragment";
    private RecyclerView recyclerView_event;
    private List<EventVO> allEvent = new LinkedList<>();
    private List<EventVO> showEvent = new LinkedList<>();
    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_event, null);
        recyclerView_event = view.findViewById(R.id.recyclerview_event);
        GetALLEve getALLEve = new GetALLEve();
        getALLEve.execute();
        return view;
    }

    class GetALLEve extends AsyncTask<String, Void, List<EventVO>> {

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading...");
            progressDialog.show();
        }

        @Override
        protected List<EventVO> doInBackground(String... params) {
            EventDAO_interface dao = new EventDAO();
            return dao.getAll();
        }


        @Override
        protected void onPostExecute(List<EventVO> eventVOS) {
            super.onPostExecute(eventVOS);
            progressDialog.cancel();
            SharedPreferences pref = getActivity().getSharedPreferences(Util.PREF_FILE,MODE_PRIVATE);
            pref.edit().putString("AllEvent",new Gson().toJson(eventVOS)).apply();
            for(EventVO eventVO : eventVOS){
                if(eventVO.getEve_sts().equals("上架"))
                    showEvent.add(eventVO);
            }
            Log.e(TAG,"LIST="+eventVOS);
            recyclerView_event.setHasFixedSize(true);
            recyclerView_event.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
            recyclerView_event.setAdapter(new EventAdapter());

        }
    }

    private class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

        class ViewHolder extends RecyclerView.ViewHolder {
            private TextView eve_name,eve_cnt,eve_site,eve_regfee,eve_start,eve_end;
            private ImageView eve_pic;

            public ViewHolder(View itemView) {
                super(itemView);
                eve_name = itemView.findViewById(R.id.eve_name);
                eve_cnt = itemView.findViewById(R.id.eve_cnt);
                eve_end = itemView.findViewById(R.id.eve_end);
                eve_start = itemView.findViewById(R.id.eve_start);
                eve_pic = itemView.findViewById(R.id.eve_pic);
                eve_regfee = itemView.findViewById(R.id.eve_regfee);
                eve_site = itemView.findViewById(R.id.eve_site);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemview = LayoutInflater.from(getContext()).inflate(R.layout.cardview_eventfragment, parent, false);
            return new ViewHolder(itemview);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewholder, int position) {
            EventVO eventVO = showEvent.get(position);
                DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                viewholder.eve_name.setText(eventVO.getEve_name());
                viewholder.eve_start.setText(sdf.format(eventVO.getEve_start()));
                viewholder.eve_end.setText(sdf.format(eventVO.getEve_end()));
                String site = eventVO.getEve_site().substring(0,2);
                viewholder.eve_site.setText(site);
                viewholder.eve_regfee.setText(String.valueOf(eventVO.getEve_regfee()));
                byte[] pic = eventVO.getEve_pic();
                Bitmap bitmap = BitmapFactory.decodeByteArray(pic, 0, pic.length);
                viewholder.eve_pic.setImageBitmap(bitmap);
                viewholder.eve_cnt.setText(eventVO.getEve_cnt());

        }

        @Override
        public int getItemCount() {
            return showEvent.size();
        }
    }
}

