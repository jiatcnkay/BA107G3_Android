package idv.wei.ba107g3.member;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
//import com.google.android.gms.location.LocationListener;
//import com.google.android.gms.location.LocationRequest;
//import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.shawnlin.numberpicker.NumberPicker;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import idv.wei.ba107g3.R;
import idv.wei.ba107g3.main.Util;

import static android.content.Context.MODE_PRIVATE;


public class DistanceSearchFragment extends Fragment  {
    private static final int MY_REQUEST_CODE = 0;
    private static final int REQUEST_CODE_RESOLUTION = 1;
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    private NumberPicker numberPicker;
    private ImageView go;
    private int chooseNum;
    private Location location;
    private GoogleApiClient googleApiClient;
    private final static String TAG = "DistanceSearchFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_distancesearch, null);
        go = view.findViewById(R.id.go);
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences pref = getActivity().getSharedPreferences(Util.PREF_FILE, MODE_PRIVATE);
                if (!pref.getBoolean("login", false)) {
                    Toast.makeText(getActivity(), "請先登入", Toast.LENGTH_SHORT).show();
                    Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(loginIntent);
                }else {
                    Intent intent = new Intent(getActivity(), DistanceSearchActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("chooseNum", chooseNum);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });
        numberPicker = view.findViewById(R.id.number_picker);
        // Set divider color
        numberPicker.setDividerColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        numberPicker.setDividerColorResource(R.color.colorPrimary);
        // Set selected text color
        numberPicker.setSelectedTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        numberPicker.setSelectedTextColorResource(R.color.colorPrimary);
        // Set selected text size
        numberPicker.setSelectedTextSize(getResources().getDimension(R.dimen.selected_text_size));
        numberPicker.setSelectedTextSize(R.dimen.selected_text_size);
        // Set text color
        numberPicker.setTextColor(ContextCompat.getColor(getContext(), R.color.colorSecond));
        numberPicker.setTextColorResource(R.color.colorSecond);
        // Set text size
        numberPicker.setTextSize(getResources().getDimension(R.dimen.text_size));
        numberPicker.setTextSize(R.dimen.text_size);
        // Set value
        numberPicker.setMaxValue(300);
        numberPicker.setMinValue(10);
        numberPicker.setValue(10);
        // Set fading edge enabled
        numberPicker.setFadingEdgeEnabled(true);
        // Set scroller enabled
        numberPicker.setScrollerEnabled(true);
        // Set wrap selector wheel
        numberPicker.setWrapSelectorWheel(true);
        // OnValueChangeListener
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                chooseNum = newVal;
            }
        });
        return view;
    }

}
