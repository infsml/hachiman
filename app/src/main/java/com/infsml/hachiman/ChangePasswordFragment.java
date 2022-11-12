package com.infsml.hachiman;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.infsml.hachiman.databinding.FragmentChangePasswordBinding;

import java.util.regex.Pattern;

import javax.xml.transform.sax.SAXResult;

public class ChangePasswordFragment extends Fragment {

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
        binding.submitBtn.setOnClickListener(v->{
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

        });
        return binding.getRoot();
    }
    public void giveMsg(String str){
        binding.msgbox.setText(str);
        binding.msgbox.setVisibility(View.VISIBLE);
    }
}