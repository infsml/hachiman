package com.infsml.hachiman.admin;

import android.content.ContentProvider;
import android.content.ContentProviderClient;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.TextView;

import com.infsml.hachiman.R;
import com.infsml.hachiman.Utility;
import com.infsml.hachiman.databinding.FragmentAdminCsvEventBinding;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AdminCsvEventFragment extends Fragment {
    FragmentAdminCsvEventBinding binding;
    NavController navController;
    String username;
    String auth_token;
    Bundle bundle;
    List<List<String>> file_cells;
    public AdminCsvEventFragment() {
    }

    public static AdminCsvEventFragment newInstance(String param1, String param2) {
        AdminCsvEventFragment fragment = new AdminCsvEventFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAdminCsvEventBinding.inflate(inflater,container,false);
        navController = Navigation.findNavController(getActivity(),R.id.fragmentContainerView);
        bundle = getArguments();
        username=bundle.getString("username");
        auth_token=bundle.getString("auth_token");
        ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
                result -> {
                    loadCSV(result);
                });
        binding.selectBtn.setOnClickListener(v->{
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
                        for(List<String> row : file_cells) {
                            JSONArray element = new JSONArray();
                            for(String cell : row){
                                element.put(cell);
                            }
                            details.put(element);
                        }
                        payload.put("details",details);
                        JSONObject res = Utility.postJSON(
                                Utility.api_base+"/add-event-admin",
                                payload.toString()
                        );
                        requireActivity().runOnUiThread(()->{
                            navController.navigate(R.id.action_adminCsvEventFragment_to_adminHomeFragment,bundle);
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
        String[] params = uri.getLastPathSegment().split("/");
        binding.fileName.setText(params[params.length-1]);
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
                        List<String> mRow = Arrays.asList(file_rows[i].split(","));
                        file_cells.add(mRow);
                    }
                    requireActivity().runOnUiThread(()->{
                        for(List<String> row : file_cells) {
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
                    });
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
    int dp_fun(int px){
        final float scale = getResources().getDisplayMetrics().density;
        return (int)(px*scale+0.5f);
    }
}