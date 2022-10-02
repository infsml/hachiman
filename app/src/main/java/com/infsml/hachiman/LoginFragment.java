package com.infsml.hachiman;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;

public class LoginFragment extends Fragment {
    NavController navController;
    View button_list;
    View spinner;
    public LoginFragment() {
    }

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //mParam1 = getArguments().getString(ARG_PARAM1);
            //mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragment_view =inflater.inflate(R.layout.fragment_login, container, false);
        Button signup_button = fragment_view.findViewById(R.id.button);
        navController = Navigation.findNavController(requireActivity(),R.id.fragmentContainerView);
        button_list = fragment_view.findViewById(R.id.constraintLayout2);
        spinner = fragment_view.findViewById(R.id.progressBar);
        final TextView usn_textView = fragment_view.findViewById(R.id.USN);
        final TextView pass_textView = fragment_view.findViewById(R.id.PASSWORD);
        signup_button.setOnClickListener((v)->{
            navController.navigate(R.id.action_loginFragment_to_signupFragment);
        });
        Button login_button = fragment_view.findViewById(R.id.button2);
        checkSignIn();
        login_button.setOnClickListener((v)->{
            button_list.setVisibility(View.GONE);
            spinner.setVisibility(View.VISIBLE);
            (new Thread(){
                @Override
                public void run(){
                    try {
                        JSONObject payload = new JSONObject();
                        payload.put("username", usn_textView.getText().toString());
                        payload.put("password",pass_textView.getText().toString());
                        JSONObject res = Utility.postJSON(Utility.api_base + "/login", payload.toString());
                        JSONObject saveData = new JSONObject();
                        saveData.put("username",payload.optString("username"));
                        saveData.put("token",res.optString("token"));
                        FileOutputStream prev_login = getContext().openFileOutput("prev_login.json", Context.MODE_PRIVATE);
                        prev_login.write(saveData.toString().getBytes(StandardCharsets.UTF_8));
                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Bundle bundle = new Bundle();
                                bundle.putString("username",payload.optString("username"));
                                bundle.putString("auth_token",res.optString("token"));
                                navController.navigate(R.id.action_loginFragment_to_homeFragment,bundle);
                            }
                        });
                    }catch (Exception e){
                        Log.e("Hachiman","LoginError",e);
                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                button_list.setVisibility(View.VISIBLE);
                                spinner.setVisibility(View.GONE);
                            }
                        });
                    }
                }
            }).start();
        });
        return fragment_view;
    }
    public void checkSignIn(){
        Log.i("Hachiman","Checking previous SignIn");
        try{
            //File prev_login = new File(getContext().getFilesDir(),"prev_login.json");
            FileInputStream prev_login = getContext().openFileInput("prev_login.json");
            int i;StringBuffer stringBuffer=new StringBuffer();
            while ((i=prev_login.read())!=-1){
                stringBuffer.append((char)i);
            }
            String readData = stringBuffer.toString();
            JSONObject readJSON = new JSONObject(readData);
            requireActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Bundle bundle = new Bundle();
                    bundle.putString("username",readJSON.optString("username"));
                    bundle.putString("auth_token",readJSON.optString("token"));
                    navController.navigate(R.id.action_loginFragment_to_homeFragment,bundle);
                }
            });
        }catch (Exception e) {
            Log.e("Hachiman","FileRead",e);
            requireActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    button_list.setVisibility(View.VISIBLE);
                    spinner.setVisibility(View.GONE);
                }
            });
        }
    }
    public void runOutside(int res){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                navController.navigate(res);
            }
        });
    }
}