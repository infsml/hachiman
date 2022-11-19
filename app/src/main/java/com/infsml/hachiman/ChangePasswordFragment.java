package com.infsml.hachiman;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.infsml.hachiman.databinding.FragmentChangePasswordBinding;

import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.util.regex.Pattern;

import javax.xml.transform.sax.SAXResult;

public class ChangePasswordFragment extends Fragment {
    NavController navController;

    public ChangePasswordFragment() {
    }

    public static ChangePasswordFragment newInstance(String param1, String param2) {
        ChangePasswordFragment fragment = new ChangePasswordFragment();
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

    FragmentChangePasswordBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChangePasswordBinding.inflate(inflater,container,false);
        navController = Navigation.findNavController(requireActivity(),R.id.fragmentContainerView);
        binding.submitBtn.setOnClickListener(v->{
            String username = binding.usn.getText().toString();
            String prev_password_s = binding.prevPassword.getText().toString();
            String password_s=binding.password.getText().toString();
            String password_confirm_s=binding.confirmPassword.getText().toString();
            if(Pattern.matches("^[^a-zA-Z]*$",password_s)||Pattern.matches("^\\D*$",password_s)){
                giveMsg("Password should contain both letters and numbers");return;
            }
            if(password_s.length()<8){
                giveMsg("Password should contain min 8 characters");return;
            }
            if(!password_s.equals(password_confirm_s)){
                giveMsg("Passwords must match");return;
            }
            v.setClickable(false);
            v.setEnabled(false);
            (new Thread(){
                @Override
                public void run(){
                    try {
                        JSONObject payload = new JSONObject();
                        payload.put("username",username);
                        payload.put("password",PasswordHash.getHash(prev_password_s));
                        payload.put("new_password", PasswordHash.getHash(password_s));
                        JSONObject res = Utility.postJSON(Utility.api_base + "/change-password", payload.toString());
                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                navController.popBackStack();
                            }
                        });
                    }catch (FileNotFoundException e){
                        Log.e("Hachiman","Signup Error",e);
                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                v.setEnabled(true);
                                v.setClickable(true);
                                giveMsg("User Already Exists");
                            }
                        });
                    }catch (Exception e){
                        Log.e("Hachiman","Signup Error",e);
                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                v.setEnabled(true);
                                v.setClickable(true);
                                giveMsg("Error Registering");
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