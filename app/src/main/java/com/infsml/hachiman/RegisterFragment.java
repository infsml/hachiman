package com.infsml.hachiman;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONObject;

public class RegisterFragment extends Fragment {
    NavController navController;
    View button_list;
    String available;
    View spinner;
    String username=null;
    String auth_token=null;
    String event=null;
    String code=null;
    String url_link_default="https://api.infsml.in/hachiman/register/";
    boolean isAdmin=false;
    Bundle bundle;
    public RegisterFragment() {
        // Required empty public constructor
    }

    public static RegisterFragment newInstance(String param1, String param2) {
        RegisterFragment fragment = new RegisterFragment();
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
        View fragment_view = inflater.inflate(R.layout.fragment_register, container, false);
        navController = Navigation.findNavController(requireActivity(),R.id.fragmentContainerView);
        bundle = getArguments();
        if(bundle==null){
            navController.navigate(R.id.action_optionFragment_to_loginFragment);
            return fragment_view;
        }
        username=bundle.getString("username");
        auth_token=bundle.getString("auth_token");
        isAdmin = bundle.getBoolean("isAdmin",false);
        event = bundle.getString("event");
        code = bundle.getString("code");
        available=bundle.getString("available");
        boolean registered = bundle.getBoolean("registered");
        Log.i("Hachiman",bundle.toString());
        if(username==null||auth_token==null){
            navController.navigate(R.id.action_registerFragment_to_loginFragment);
            return fragment_view;
        }
        if(event==null){
            navController.navigate(R.id.action_registerFragment_to_homeFragment,bundle);
            return fragment_view;
        }
        if(code==null||available==null){
            navController.navigate(R.id.action_registerFragment_to_optionFragment,bundle);
            return fragment_view;
        }
        button_list = fragment_view.findViewById(R.id.constraintLayout2);
        spinner = fragment_view.findViewById(R.id.progressBar);
        button_list.setVisibility(View.VISIBLE);
        spinner.setVisibility(View.GONE);
        TextView textView = fragment_view.findViewById(R.id.textView2);
        TextView usn = fragment_view.findViewById(R.id.USN);
        TextView spinner = fragment_view.findViewById(R.id.spinner);
        TextView available_view = fragment_view.findViewById(R.id.available);
        textView.setText(event);
        usn.setText(username);
        spinner.setText(code);
        available_view.setText("Available : "+available);
        Button register_button = fragment_view.findViewById(R.id.button5);
        if(registered){
            register_button.setText(R.string.unregister);
            url_link_default="https://api.infsml.in/hachiman/unregister/";
        }
        register_button.setOnClickListener(v->{
            button_list.setVisibility(View.GONE);
            spinner.setVisibility(View.VISIBLE);
            (new Thread(){
                @Override
                public void run(){
                    try{
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("event",event);
                        jsonObject.put("usn",username);
                        jsonObject.put("code",code);
                        jsonObject.put("auth_token",auth_token);
                        JSONObject jsonObject1 = Utility.postJSON(
                                url_link_default,jsonObject.toString());
                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(jsonObject1.optInt("state")==StateCodes.state_invalid_login){
                                    navController.navigate(R.id.action_registerFragment_to_loginFragment);
                                }else {
                                    navController.navigate(R.id.action_registerFragment_to_optionFragment);
                                }
                            }
                        });
                    }catch (Exception e){
                        Log.i("Hachiman","Error",e);
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
        Button cancel_button = fragment_view.findViewById(R.id.button6);
        if(registered){
            cancel_button.setOnClickListener(v-> {
                Log.i("Hachiman","Going home");
                navController.navigate(R.id.action_registerFragment_to_homeFragment, bundle);
            });
        }else{
            cancel_button.setOnClickListener(v->{
                navController.navigate(R.id.action_registerFragment_to_optionFragment,bundle);
            });
        }
        return fragment_view;
    }
}