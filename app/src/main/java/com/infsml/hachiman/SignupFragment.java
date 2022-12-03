package com.infsml.hachiman;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.infsml.hachiman.databinding.FragmentSignupBinding;

import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class SignupFragment extends Fragment {
    NavController navController;

    public SignupFragment() {
    }

    public static SignupFragment newInstance(String param1, String param2) {
        SignupFragment fragment = new SignupFragment();
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
    FragmentSignupBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSignupBinding.inflate(inflater,container,false);
        //View fragment_view =inflater.inflate(R.layout.fragment_signup, container, false);

        navController = Navigation.findNavController(getActivity(),R.id.fragmentContainerView);
        binding.progressBar.setVisibility(View.GONE);
        binding.registerButton.setOnClickListener((v)->{
            String usn_s = binding.usn.getText().toString().toUpperCase();
            String name_s = binding.nameInput.getText().toString().trim();
            String section_s=binding.section.getText().toString();
            String sem_s=binding.semester.getText().toString();
            String password_s=binding.password.getText().toString();
            String password_confirm_s=binding.confirmPassword.getText().toString();
            if(!Pattern.matches("^\\d\\w{2}\\d{2}\\w{2}\\d{3}$",usn_s)){
                giveMsg("Enter valid usn");return;
            };
            /*if(!Patterns.EMAIL_ADDRESS.matcher(email_s).matches()){
                giveMsg("Enter valid email");return;
            }*/
            if(!Pattern.matches("^[a-zA-Z]+(([',. -][a-zA-Z ])?[a-zA-Z]*)*$",name_s)){
                giveMsg("Enter valid name");return;
            }
            if(!Pattern.matches("^[A-Z]$",section_s)){
                giveMsg("Enter valid section");return;
            }
            if(!Pattern.matches("^[1-8]$",sem_s)){
                giveMsg("Enter valid semester");return;
            }
            if(Pattern.matches("^[^a-zA-Z]*$",password_s)||Pattern.matches("^\\D*$",password_s)){
                giveMsg("Password should contain both letters and numbers");return;
            }
            if(password_s.length()<8){
                giveMsg("Password should contain min 8 characters");return;
            }
            if(!password_s.equals(password_confirm_s)){
                giveMsg("Passwords must match");return;
            }
            binding.constraintLayout2.setVisibility(View.GONE);
            binding.progressBar.setVisibility(View.VISIBLE);
            (new Thread(){
                @Override
                public void run(){
                    try {
                        JSONObject payload = new JSONObject();
                        payload.put("username", usn_s);
                        payload.put("password", PasswordHash.getHash(password_s));
                        payload.put("name", name_s);
                        payload.put("semester", Integer.parseInt(sem_s));
                        payload.put("section", section_s);
                        JSONObject res = Utility.postJSON(Utility.api_base + "/signup", payload.toString());
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
                                binding.progressBar.setVisibility(View.GONE);
                                binding.constraintLayout2.setVisibility(View.VISIBLE);
                                giveMsg("User Already Exists");
                            }
                        });
                    }catch (Exception e){
                        Log.e("Hachiman","Signup Error",e);
                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                binding.progressBar.setVisibility(View.GONE);
                                binding.constraintLayout2.setVisibility(View.VISIBLE);
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