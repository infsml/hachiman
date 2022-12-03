package com.infsml.hachiman.admin;

import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TableRow;
import android.widget.TextView;

import com.infsml.hachiman.R;
import com.infsml.hachiman.Utility;
import com.infsml.hachiman.databinding.FragmentAdminOptionBinding;
import com.infsml.hachiman.databinding.FragmentAdminReportBinding;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AdminReportFragment extends Fragment {
    NavController navController;
    Bundle bundle;
    String username = null;
    String auth_token = null;
    String event = null;
    public AdminReportFragment() {
    }
    public static AdminReportFragment newInstance(String param1, String param2) {
        AdminReportFragment fragment = new AdminReportFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }
    FragmentAdminReportBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAdminReportBinding.inflate(inflater,container,false);
        navController = Navigation.findNavController(requireActivity(), R.id.fragmentContainerView);
        bundle = getArguments();
        assert bundle!=null;
        username = bundle.getString("username");
        auth_token = bundle.getString("auth_token");
        event = bundle.getString("event");
        binding.spinner3.setEnabled(false);
        binding.spinner3.setClickable(false);
        fetchOptionData(()->{
            binding.spinner3.setEnabled(true);
            binding.spinner3.setClickable(true);
            binding.spinner3.setSelection(0);
            populateMainTable(allData);
        });
        ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.CreateDocument("text/csv"),
                result -> {
                    if(binding.spinner3.getSelectedItemPosition()==0)
                    saveCSV(result,allData);
                    else saveCSV(result,sectionWise);
                });
        binding.addBtn.setOnClickListener(v->{
            if(binding.spinner3.getSelectedItemPosition()==0)
            mGetContent.launch("allData.csv");
            else mGetContent.launch("sectionWise.csv");
        });

        binding.spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0)populateMainTable(allData);
                else populateMainTable(sectionWise);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return binding.getRoot();
    }
    JSONObject optionData;
    public void fetchOptionData(Runnable runnable) {
        (new Thread() {
            @Override
            public void run() {
                try {
                    if(optionData==null) {
                        JSONObject payload = new JSONObject();
                        payload.put("username", username);
                        payload.put("auth_token", auth_token);
                        payload.put("event", event);
                        optionData = Utility.postJSON(
                                Utility.api_base + "/get-registration-admin",
                                payload.toString()
                        );
                    }
                    parseAllData();
                    parseSectionWise();
                    requireActivity().runOnUiThread(runnable);
                } catch (Exception e) {
                    Log.e("Hachiman", "Error", e);
                }
            }
        }).start();
    }
    List<List<String>> allData;
    List<List<String>> sectionWise;
    void parseAllData(){
        String[] columns = {"username","name","first_option","second_option","third_option","section","semester"};
        allData = new ArrayList<>();
        allData.add(Arrays.asList(columns));
        JSONArray data = optionData.optJSONArray("data");
        for(int i=0;i<data.length();i++){
            JSONObject object = data.optJSONObject(i);
            List<String> row = new ArrayList<>();
            for(String key : columns){
                row.add(object.optString(key));
            }
            allData.add(row);
        }
    }
    void parseSectionWise(){
        HashMap<String,HashMap<String,HashMap<String,Integer>>> megaMap= new HashMap<>();
        HashMap<String,HashMap<String,Integer>> megaBranch;
        HashMap<String,Integer> megaCourse;
        JSONArray data = optionData.optJSONArray("data");
        Set<String> sections = new HashSet<>();
        for (int i = 0; i < data.length(); i++) {
            JSONObject object = data.optJSONObject(i);
            String branch = object.optString("username").substring(5,7);
            String course = object.optString("first_option");
            String section = object.optString("section");
            Log.i("Branch",branch);
            if(!megaMap.containsKey(branch))megaMap.put(branch,new HashMap<>());
            megaBranch = megaMap.get(branch);
            if(!megaBranch.containsKey(course))megaBranch.put(course,new HashMap<>());
            megaCourse = megaBranch.get(course);
            if(!megaCourse.containsKey(section))megaCourse.put(section,0);
            megaCourse.put(section,megaCourse.get(section)+1);
            sections.add(section);
        }
        List<String> sec_list = new ArrayList<>(sections);
        sectionWise = new ArrayList<>();
        for(String branch: megaMap.keySet()){
            List<String> row = new ArrayList<>();
            row.add(branch);
            sectionWise.add(row);
            row = new ArrayList<>();
            row.add("");
            row.addAll(sec_list);
            sectionWise.add(row);
            megaBranch = megaMap.get(branch);
            for(String course:megaBranch.keySet()){
                row = new ArrayList<>();
                row.add(course);
                for(String section: sec_list){
                    megaCourse = megaBranch.get(course);
                    if(megaCourse.containsKey(section))row.add(""+megaCourse.get(section));
                    else row.add("0");
                }
                sectionWise.add(row);
            }
        }
    }

    void populateMainTable(List<List<String>> mList){
        if(mList==null)return;

        binding.mainTable.removeAllViews();
        for(List<String> row : mList) {
            TableRow tableRow = new TableRow(requireContext());
            for(String cell : row){
                TextView textView = new TextView(requireContext());
                textView.setText(cell);
                textView.setPadding(dp_fun(5),dp_fun(5),dp_fun(5),dp_fun(5));
                textView.setBackgroundResource(R.drawable.rectangle);
                tableRow.addView(textView);
            }
            binding.mainTable.addView(tableRow);
        }

    }
    void saveCSV(Uri uri,List<List<String>> mList){
        try{
            OutputStream fos = requireContext().getContentResolver().openOutputStream(uri);
            for(List<String> row : mList){
                String line = String.join(",",row)+"\n";
                fos.write(line.getBytes(StandardCharsets.UTF_8));
            }
            fos.flush();
            fos.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    int dp_fun(int px){
        final float scale = getResources().getDisplayMetrics().density;
        return (int)(px*scale+0.5f);
    }
}