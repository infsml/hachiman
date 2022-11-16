package com.infsml.hachiman.admin;

import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.infsml.hachiman.R;
import com.infsml.hachiman.databinding.FragmentAdminCsvTemplateBinding;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class AdminCsvTemplateFragment extends Fragment {

    public AdminCsvTemplateFragment() {
    }

    public static AdminCsvTemplateFragment newInstance(String param1, String param2) {
        AdminCsvTemplateFragment fragment = new AdminCsvTemplateFragment();
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
    FragmentAdminCsvTemplateBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAdminCsvTemplateBinding.inflate(inflater,container,false);
        ActivityResultLauncher<String> create_course = registerForActivityResult(new ActivityResultContracts.CreateDocument("text/csv"),
                result -> {
                    saveCSV(result,"Course Code,Course Name,Semester");
                });
        ActivityResultLauncher<String> create_prev = registerForActivityResult(new ActivityResultContracts.CreateDocument("text/csv"),
                result -> {
                    saveCSV(result,"Course Code,Course Name");
                });
        ActivityResultLauncher<String> create_opt = registerForActivityResult(new ActivityResultContracts.CreateDocument("text/csv"),
                result -> {
                    saveCSV(result,"Course Code,Course Name,Availability,Previous Requirement");
                });
        binding.courseBtn.setOnClickListener(v->{
            create_course.launch("course_template.csv");
        });
        binding.prevOptBtn.setOnClickListener(v->{
            create_prev.launch("previous_option_template.csv");
        });
        binding.optBtn.setOnClickListener(v->{
            create_opt.launch("current_option_template.csv");
        });
        return binding.getRoot();
    }
    public void saveCSV(Uri uri,String text){
        try{
            OutputStream fos = requireContext().getContentResolver().openOutputStream(uri);
            fos.write(text.getBytes(StandardCharsets.UTF_8));
            fos.flush();
            fos.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}