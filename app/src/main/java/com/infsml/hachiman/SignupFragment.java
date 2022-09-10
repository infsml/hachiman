package com.infsml.hachiman;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.cognitoauth.Auth;
import com.amplifyframework.auth.AuthUserAttribute;
import com.amplifyframework.auth.AuthUserAttributeKey;
import com.amplifyframework.auth.options.AuthSignUpOptions;
import com.amplifyframework.core.Amplify;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class SignupFragment extends Fragment {
    NavController navController;
    TextView msgBox;
    TextView usn;
    TextView section;
    TextView email;
    TextView password;
    TextView confirm_password;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragment_view =inflater.inflate(R.layout.fragment_signup, container, false);
        Button cancel_button = fragment_view.findViewById(R.id.button);
        navController = Navigation.findNavController(getActivity(),R.id.fragmentContainerView);
        cancel_button.setOnClickListener((v)->{
            navController.navigate(R.id.action_signupFragment_to_loginFragment);
        });

        msgBox=fragment_view.findViewById(R.id.textView3);
        usn = fragment_view.findViewById(R.id.USN);
        section = fragment_view.findViewById(R.id.section);
        email = fragment_view.findViewById(R.id.email);
        password=fragment_view.findViewById(R.id.PASSWORD);
        confirm_password=fragment_view.findViewById(R.id.confirm_password);

        Button signup_button = fragment_view.findViewById(R.id.button2);
        signup_button.setOnClickListener((v)->{
            String usn_s = usn.getText().toString();
            String email_s=email.getText().toString();
            String section_s=section.getText().toString();
            String password_s=password.getText().toString();
            String password_confirm_s=confirm_password.getText().toString();
            if(!Pattern.matches("^\\d\\w{2}\\d{2}\\w{2}\\d{3}$",usn_s.toLowerCase())){
                giveMsg("Enter valid usn");return;
            };
            if(!Patterns.EMAIL_ADDRESS.matcher(email_s).matches()){
                giveMsg("Enter valid email");return;
            }
            if(!Pattern.matches("^[A-Z]$",section_s)){
                giveMsg("Enter valid section");return;
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
            List<AuthUserAttribute>attributeList=new ArrayList<>();
            attributeList.add(new AuthUserAttribute(AuthUserAttributeKey.custom("custom:custom:section"),section.getText().toString()));
            attributeList.add(new AuthUserAttribute(AuthUserAttributeKey.email(),email.getText().toString()));
            attributeList.add(new AuthUserAttribute(AuthUserAttributeKey.address(),"Somewhere around here"));

            AuthSignUpOptions signUpOptions = AuthSignUpOptions.builder()
                    .userAttributes(attributeList)
                    .build();
            Amplify.Auth.signUp(usn.getText().toString(),password.getText().toString(),signUpOptions,
                    result-> Log.i("SignUP","Res : "+ result.toString()),
                    error-> Log.e("SignUP","Err : ",error)
            );
        });

        return fragment_view;
    }
    public void giveMsg(String str){
        msgBox.setText(str);
        msgBox.setVisibility(View.VISIBLE);
    }
}