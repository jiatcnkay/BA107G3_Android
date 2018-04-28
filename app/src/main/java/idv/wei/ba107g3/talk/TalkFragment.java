package idv.wei.ba107g3.talk;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import idv.wei.ba107g3.R;
import idv.wei.ba107g3.main.Util;
import idv.wei.ba107g3.member.MemberVO;

import static idv.wei.ba107g3.main.Util.TALK;

public class TalkFragment extends Fragment {
    private RecyclerView recyclerView_talk;
    private MemberVO member;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_talk, container, false);
        recyclerView_talk = view.findViewById(R.id.recyclerview_talk);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        recyclerView_talk.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        TalkAdapter talkAdapter = new TalkAdapter();
        recyclerView_talk.setAdapter(talkAdapter);
    }

    private class TalkAdapter extends RecyclerView.Adapter<TalkAdapter.ViewHolder> {

        class ViewHolder extends RecyclerView.ViewHolder {
            private ImageView friendPhoto;
            private TextView friendName;
            private CardView cardview_talk;

            public ViewHolder(View itemView) {
                super(itemView);
                friendPhoto = itemView.findViewById(R.id.friendPhoto);
                friendName = itemView.findViewById(R.id.friendName);
                cardview_talk = itemView.findViewById(R.id.cardview_talk);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(getContext()).inflate(R.layout.cardview_talk, parent, false);
            ViewHolder viewHolder = new ViewHolder(itemView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final ViewHolder viewholder, int position) {
            final MemberVO member = Util.TALK.get(position);
            byte[] photo = member.getMem_photo();
            final Bitmap bitmap = BitmapFactory.decodeByteArray(photo, 0, photo.length);
            viewholder.friendPhoto.setImageBitmap(Util.getCircleBitmap(bitmap, 300));
            viewholder.friendName.setText(member.getMem_name());
            viewholder.cardview_talk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), TalkActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("member", member);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return Util.TALK.size();
        }
    }
}
