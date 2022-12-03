package com.infsml.hachiman.admin;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.infsml.hachiman.PasswordHash;
import com.infsml.hachiman.R;
import com.infsml.hachiman.Utility;
import com.infsml.hachiman.databinding.FragmentPasswordResetBinding;

import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;

public class PasswordResetFragment extends Fragment {
    Bundle bundle;
    NavController navController;
    String username;
    String auth_token;
    public PasswordResetFragment() {
    }

    public static PasswordResetFragment newInstance(String param1, String param2) {
        PasswordResetFragment fragment = new PasswordResetFragment();
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
    FragmentPasswordResetBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPasswordResetBinding.inflate(inflater,container,false);
        navController = Navigation.findNavController(requireActivity(), R.id.fragmentContainerView);
        bundle = getArguments();
        assert bundle!=null;
        username = bundle.getString("username");
        auth_token = bundle.getString("auth_token");
        binding.registerButton.setOnClickListener((v)->{
            binding.registerButton.setText("Resetting...");
            binding.registerButton.setClickable(false);
            binding.registerButton.setEnabled(false);
            (new Thread(){
                @Override
                public void run(){
                    try {
                        JSONObject payload = new JSONObject();
                        String usn_s = binding.usn.getText().toString();
                        payload.put("student_username", usn_s.toUpperCase());
                        String pass_s = binding.password.getText().toString();
                        payload.put("password", PasswordHash.getHash(pass_s));
                        payload.put("username",username);
                        payload.put("auth_token",auth_token);
                        JSONObject res = Utility.postJSON(Utility.api_base + "/reset-password", payload.toString());
                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                binding.registerButton.setText("Success");
                                binding.registerButton.setClickable(true);
                                binding.registerButton.setEnabled(true);

                            }
                        });
                    }catch (FileNotFoundException e){
                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                giveMsg("Invalid username or password");
                                binding.registerButton.setText("Reset");
                                binding.registerButton.setClickable(true);
                                binding.registerButton.setEnabled(true);

                            }
                        });
                    }catch (Exception e){
                        Log.e("Hachiman","LoginError",e);
                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                binding.registerButton.setText("Try again");
                                binding.registerButton.setClickable(true);
                                binding.registerButton.setEnabled(true);
                            }
                        });
                    }
                }
            }).start();
        });
        return binding.getRoot();
    }
    public void giveMsg(String str){
        binding.msgbox.setText(str);
        binding.msgbox.setVisibility(View.VISIBLE);
    }
}