package com.infsml.hachiman.admin;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.TextView;

import com.infsml.hachiman.R;
import com.infsml.hachiman.Utility;
import com.infsml.hachiman.databinding.FragmentAdminAddOptionBinding;
import com.infsml.hachiman.databinding.FragmentAdminCsvOptionBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AdminCsvOptionFragment extends Fragment {
    FragmentAdminCsvOptionBinding binding;
    NavController navController;
    String username;
    String auth_token;
    String event;
    Bundle bundle;
    JSONArray prev_courses;
    List<List<String>> file_cells;
    public AdminCsvOptionFragment() {
    }
    public static AdminCsvOptionFragment newInstance(String param1, String param2) {
        AdminCsvOptionFragment fragment = new AdminCsvOptionFragment();
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
    Set<String> prev_set;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAdminCsvOptionBinding.inflate(inflater,container,false);
        navController = Navigation.findNavController(getActivity(),R.id.fragmentContainerView);
        bundle = getArguments();
        username=bundle.getString("username");
        auth_token=bundle.getString("auth_token");
        event = bundle.getString("event");
        try {
            prev_courses = new JSONArray(bundle.getString("prev"));
        } catch (JSONException e) {
            e.printStackTrace();
            prev_courses = new JSONArray();
        }
        prev_set = new HashSet<>();
        prev_set.add("None");
        for(int i = 0 ;i<prev_courses.length();i++){
            JSONObject prev_course = prev_courses.optJSONObject(i);
            prev_set.add(prev_course.optString("code"));
        }
        ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
                result -> {
                    loadCSV(result);
                });
        binding.selectBtn.setOnClickListener(v->{
            binding.spinner3.setEnabled(false);
            binding.spinner3.setClickable(false);
            mGetContent.launch("*/*");
        });
        binding.addBtn.setOnClickListener(v -> {
            binding.addBtn.setEnabled(false);
            binding.addBtn.setClickable(false);
            binding.addBtn.setText("Adding..");
            (new Thread(){
                @Override
                public void run(){
                    try{
                        JSONObject payload = new JSONObject();
                        payload.put("username",username);
                        payload.put("auth_token",auth_token);
                        JSONArray details = new JSONArray();
                        int course_type = binding.spinner3.getSelectedItemPosition();
                        if(course_type==0) {
                            payload.put("course_type", "previous");
                        }else{
                            payload.put("course_type", "current");
                        }
                        for(List<String> row : file_cells) {
                            JSONArray element = new JSONArray();
                            for(String cell : row){
                                element.put(cell);
                            }
                            if(course_type!=1 || prev_set.contains(row.get(row.size()-1)))
                                details.put(element);
                        }
                        payload.put("details",details);
                        JSONObject res = Utility.postJSON(
                                Utility.api_base+"/add-option-admin",
                                payload.toString()
                        );
                        requireActivity().runOnUiThread(()->{
                            navController.navigate(R.id.action_adminCsvOptionFragment_to_adminOptionFragment,bundle);
                        });
                    }catch (Exception e){
                        e.printStackTrace();
                        requireActivity().runOnUiThread(()->{
                            binding.addBtn.setEnabled(true);
                            binding.addBtn.setClickable(true);
                            binding.addBtn.setText("Retry");
                        });
                    }
                }
            }).start();
        });
        return binding.getRoot();
    }
    public void loadCSV(Uri uri){
        (new Thread(){
            @Override
            public void run(){
                try {
                    InputStream fis = requireContext().getContentResolver().openInputStream(uri);
                    StringBuilder stringBuilder = new StringBuilder();
                    for(int i;(i=fis.read())!=-1;){
                        stringBuilder.append((char)i);
                    }
                    String file_content = stringBuilder.toString();
                    String[] file_rows = file_content.split("\n");
                    file_cells = new ArrayList<>();
                    for(int i=0;i<file_rows.length;i++){
                        String [] smRow = file_rows[i].trim().split(",");
                        List<String> mRow = new ArrayList<>(Arrays.asList(smRow));
                        mRow.add(1,event);
                        file_cells.add(mRow);
                    }
                    requireActivity().runOnUiThread(()->{
                        int course_type = binding.spinner3.getSelectedItemPosition();
                        binding.mainTable.removeAllViews();
                        for(int i=0;i< file_cells.size();i++) {
                            List<String> row = file_cells.get(i);
                            TableRow tableRow = new TableRow(requireContext());
                            for(String cell : row){
                                TextView textView = new TextView(requireContext());
                                textView.setText(cell);
                                tableRow.addView(textView);
                            }
                            if(course_type==1 && !prev_set.contains(row.get(row.size()-1)))
                                tableRow.setBackgroundColor(Color.rgb(180,255,255));
                            binding.mainTable.addView(tableRow);
                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
}