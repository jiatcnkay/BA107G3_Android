package idv.wei.ba107g3.member;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

import com.google.gson.Gson;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import idv.wei.ba107g3.R;
import idv.wei.ba107g3.main.Util;

public class AdvancedSearchActivity extends AppCompatActivity {
    private Spinner selectGender,selectAge,selectCounty,selectEmotion;
    private ImageView close;
    private Button confirm;
    private Map<String, String> map = new TreeMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advancedsearch);
        find();
        putData();
    }

    private void find() {
        selectGender = findViewById(R.id.selectGender);
        selectAge = findViewById(R.id.selectAge);
        selectCounty = findViewById(R.id.selectCounty);
        selectEmotion = findViewById(R.id.selectEmotion);
        confirm = findViewById(R.id.confirm);
        close = findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }

    private void putData() {
        String[] gender = {getString(R.string.nochoose), getString(R.string.boy), getString(R.string.girl)};
        ArrayAdapter<String> genderadapter = new ArrayAdapter<>
                (this, android.R.layout.simple_spinner_item, gender);
        genderadapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        selectGender.setAdapter(genderadapter);
        selectGender.setSelection(0, true);
        selectGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String getGender = parent.getItemAtPosition(position).toString();
                if(position==0)
                map.put("mem_gender","");
                else
                map.put("mem_gender",getGender);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        String[] age = {getString(R.string.nochoose), getString(R.string.down20), getString(R.string.from20to30)
                , getString(R.string.from30to40) , getString(R.string.up40)};
        ArrayAdapter<String> ageadapter = new ArrayAdapter<>
                (this, android.R.layout.simple_spinner_item, age);
        ageadapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        selectAge.setAdapter(ageadapter);
        selectAge.setSelection(0, true);
        selectAge.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String getAge = parent.getItemAtPosition(position).toString();
                if(position==0)
                map.put("mem_birthday","");
                else
                map.put("mem_birthday",getAge);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        String[] county = {getString(R.string.nochoose), getString(R.string.Keelung), getString(R.string.Taipei)
                , getString(R.string.NewTaipei), getString(R.string.Taoyuan), getString(R.string.Hsinchu)
                , getString(R.string.Hsinchu2), getString(R.string.Miaoli), getString(R.string.Changhua)
                , getString(R.string.Taichung), getString(R.string.Nantou), getString(R.string.Yunlin)
                , getString(R.string.Chiayi), getString(R.string.Chiayi2), getString(R.string.Tainan)
                , getString(R.string.Kaohsiung), getString(R.string.Pingtung), getString(R.string.Taitung)
                , getString(R.string.Hualien), getString(R.string.Yilan)};
        ArrayAdapter<String> countyadapter = new ArrayAdapter<>
                (this, android.R.layout.simple_spinner_item, county);
        countyadapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        selectCounty.setAdapter(countyadapter);
        selectCounty.setSelection(0, true);
        selectCounty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String getCounty = parent.getItemAtPosition(position).toString();
                if(position==0)
                map.put("mem_county","");
                else
                map.put("mem_county",getCounty);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        String[] emotion = {getString(R.string.nochoose),getString(R.string.single), getString(R.string.love)
                , getString(R.string.couple), getString(R.string.breakup), getString(R.string.dontnohowtosay)
                , getString(R.string.secret)};
        ArrayAdapter<String> emotionadapter = new ArrayAdapter<>
                (this, android.R.layout.simple_spinner_item, emotion);
        emotionadapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        selectEmotion.setAdapter(emotionadapter);
        selectEmotion.setSelection(0, true);
        selectEmotion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String getEmotion = parent.getItemAtPosition(position).toString();
                if(position==0)
                map.put("mem_emotion","");
                else
                map.put("mem_emotion",getEmotion);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public void onClickConfirm(View view){
        Set set = map.keySet();
        Iterator it = set.iterator();
        if(!it.hasNext()) {
            map.put("mem_gender", "");
            map.put("mem_birthday", "");
            map.put("mem_county", "");
            map.put("mem_emotion", "");
        }
        Gson gson = new Gson();
        SharedPreferences pref = getSharedPreferences(Util.PREF_FILE,MODE_PRIVATE);
        pref.edit().putString("advanced",gson.toJson(map)).apply();
        setResult(RESULT_OK);
        finish();
    }
}
