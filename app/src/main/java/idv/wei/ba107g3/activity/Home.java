package idv.wei.ba107g3.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import java.util.ArrayList;
import java.util.List;

import idv.wei.ba107g3.R;
import idv.wei.ba107g3.main.Util;
import idv.wei.ba107g3.member.MemberDAO;
import idv.wei.ba107g3.member.MemberDAO_interface;
import idv.wei.ba107g3.member.MemberProfileActivity;
import idv.wei.ba107g3.member.MemberVO;

public class Home extends Fragment {
    private RecyclerView recyclerView;
    private CarouselView carouselView;
    private int[] sampleImages = {R.drawable.carousel1,R.drawable.carousel2,R.drawable.carousel3};
    private ProgressDialog progressDialog;
    private List<MemberVO> popularList = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.home,null);
        carouselView = view.findViewById(R.id.carouselView);
        carouselView.setPageCount(sampleImages.length);
        carouselView.setImageListener(imageListener);
        recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        GetPopular getPopular = new GetPopular();
        getPopular.execute();
    }

    private class GetPopular extends AsyncTask<Void,Void,List<MemberVO>>{
        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading...");
            progressDialog.show();
        }

        @Override
        protected List<MemberVO> doInBackground(Void... voids) {
            MemberDAO_interface dao = new MemberDAO();
            return dao.getPopular();
        }

        @Override
        protected void onPostExecute(List<MemberVO> memberVOS) {
            super.onPostExecute(memberVOS);
            progressDialog.cancel();
            popularList = memberVOS;
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL));
            recyclerView.setAdapter(new PopularAdapter(getContext()));
        }
    }

    ImageListener imageListener = new ImageListener() {
        @Override
        public void setImageForPosition(final int position, ImageView imageView) {
            imageView.setImageResource(sampleImages[position]);
        }
    };

    private class PopularAdapter extends RecyclerView.Adapter<PopularAdapter.ViewHolder> {
        private Context context;

        public PopularAdapter(Context context) {
            this.context = context;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private ImageView mem_photo;
            private TextView mem_name, mem_gender, mem_county,mem_receive_gift,mem_age;
            private CardView popular_cardview;

            public ViewHolder(View itemView) {
                super(itemView);
                mem_age = itemView.findViewById(R.id.mem_age);
                mem_photo = itemView.findViewById(R.id.mem_photo);
                mem_name = itemView.findViewById(R.id.mem_name);
                mem_gender = itemView.findViewById(R.id.mem_gender);
                mem_county = itemView.findViewById(R.id.mem_county);
                mem_receive_gift = itemView.findViewById(R.id.mem_receive_gift);
                popular_cardview = itemView.findViewById(R.id.popular_cardview);
            }
        }

        @Override
        public int getItemCount() {
            return popularList.size();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemview = LayoutInflater.from(context).inflate(R.layout.recycleview_home, parent, false);
            return new ViewHolder(itemview);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewholder, int position) {
            final MemberVO member = popularList.get(position);
            byte[] memPhoto = member.getMem_photo();
            Bitmap bitmap = BitmapFactory.decodeByteArray(memPhoto, 0, memPhoto.length);
            viewholder.mem_photo.setImageBitmap(bitmap);
            viewholder.mem_age.setText(Util.getAge(member.getMem_age()));
            viewholder.mem_name.setText(member.getMem_name());
            viewholder.mem_gender.setText(member.getMem_gender());
            viewholder.mem_county.setText(member.getMem_county());
            viewholder.mem_receive_gift.setText(member.getMem_receive_gift().toString());
            viewholder.popular_cardview.setOnClickListener(new View.OnClickListener() {
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
    }
}

