package idv.wei.ba107g3.member;

import android.annotation.SuppressLint;
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

import java.util.LinkedList;
import java.util.List;

import idv.wei.ba107g3.R;
import idv.wei.ba107g3.main.Util;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

@SuppressLint("ValidFragment")
public class BasicSearchFragment extends Fragment {
    private RecyclerView recyclerView_basicsearch;
    private List<MemberVO> allMemList = new LinkedList<>();
    private MemberVO memberVO;
    private Button btnadvanced;
    private static final int ADAVANCED = 1;
    private ProgressDialog progressDialog;
    private TextView noMatch;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_basicsearch, null);
        noMatch = view.findViewById(R.id.noMatch);
        btnadvanced = view.findViewById(R.id.btnadvanced);
        btnadvanced.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(getActivity(), AdvancedSearchActivity.class), ADAVANCED);
            }
        });
        recyclerView_basicsearch = view.findViewById(R.id.recyclerview_basicsearch);
        recyclerView_basicsearch.setHasFixedSize(true);

        GetALLMember getALLMember = new GetALLMember();
        getALLMember.execute();
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADAVANCED) {
            if (resultCode == RESULT_OK) {
                SharedPreferences pref = getActivity().getSharedPreferences(Util.PREF_FILE, MODE_PRIVATE);
                String map = pref.getString("advanced", "");
                GetALLMember getALLMember = new GetALLMember();
                getALLMember.execute(map.toString());
            }
        }
    }


    class GetALLMember extends AsyncTask<String, Void, List<MemberVO>> {

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading...");
            progressDialog.show();
        }

        @Override
        protected List<MemberVO> doInBackground(String... params) {
            MemberDAO_interface dao = new MemberDAO();
            if (params.length == 0) {
                allMemList = dao.getAll();
                SharedPreferences pref = getActivity().getSharedPreferences(Util.PREF_FILE,MODE_PRIVATE);
                pref.edit().putString("allMem",new Gson().toJson(allMemList)).apply();
            } else {
                allMemList = dao.getLike(params[0]);
            }
            return allMemList;
        }


        @Override
        protected void onPostExecute(List<MemberVO> memberVOS) {
            super.onPostExecute(memberVOS);
            progressDialog.cancel();
            allMemList = memberVOS;
            SharedPreferences pref = getActivity().getSharedPreferences(Util.PREF_FILE, MODE_PRIVATE);
            memberVO = new Gson().fromJson(pref.getString("loginMem", ""), MemberVO.class);
            if (memberVO != null) {
                for (int i = 0; i < allMemList.size(); i++) {
                    if (allMemList.get(i).getMem_name().equals(memberVO.getMem_name()))
                        allMemList.remove(allMemList.get(i));
                }
            }
                noMatch.setVisibility(View.INVISIBLE);
            if (allMemList.size()==0)
                noMatch.setVisibility(View.VISIBLE);

            recyclerView_basicsearch.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
            recyclerView_basicsearch.setAdapter(new BasicSearchAdapter());

        }
    }

    private class BasicSearchAdapter extends RecyclerView.Adapter<BasicSearchAdapter.ViewHolder> {

        class ViewHolder extends RecyclerView.ViewHolder {
            private ImageView photo;
            private TextView name, age, gender, county;
            private CardView cardview_search;

            public ViewHolder(View itemView) {
                super(itemView);
                photo = itemView.findViewById(R.id.photo);
                name = itemView.findViewById(R.id.name);
                age = itemView.findViewById(R.id.age);
                gender = itemView.findViewById(R.id.gender);
                county = itemView.findViewById(R.id.county);
                cardview_search = itemView.findViewById(R.id.cardview_search);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemview = LayoutInflater.from(getContext()).inflate(R.layout.cardview_basicfragment, parent, false);
            return new ViewHolder(itemview);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewholder, int position) {
            Log.e("list", "list=" + allMemList.toString());
            final MemberVO member = allMemList.get(position);
            byte[] memPhoto = member.getMem_photo();
            Bitmap bitmap = BitmapFactory.decodeByteArray(memPhoto, 0, memPhoto.length);
            viewholder.photo.setImageBitmap(bitmap);
            viewholder.age.setText(Util.getAge(member.getMem_age()));
            viewholder.name.setText(member.getMem_name());
            viewholder.gender.setText(member.getMem_gender());
            viewholder.county.setText(member.getMem_county());
            viewholder.cardview_search.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), MemberProfileActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("member", member);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return allMemList.size();
        }

    }
}

