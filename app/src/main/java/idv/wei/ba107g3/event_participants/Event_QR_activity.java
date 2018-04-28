package idv.wei.ba107g3.event_participants;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import idv.wei.ba107g3.R;

public class Event_QR_activity extends AppCompatActivity {
    private Event_participantsVO evep;
    private ImageView event_qr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_qr);
        event_qr = findViewById(R.id.event_qr);
        Bundle bundle = getIntent().getExtras();
        evep = (Event_participantsVO) bundle.getSerializable("evep");
        byte[] pic = evep.getEvep_qr();
        Bitmap bitmap = BitmapFactory.decodeByteArray(pic,0,pic.length);
        event_qr.setImageBitmap(bitmap);
    }
}
