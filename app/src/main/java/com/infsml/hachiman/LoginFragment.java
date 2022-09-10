package com.infsml.hachiman;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.amplifyframework.auth.AuthUserAttribute;
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession;
import com.amplifyframework.core.Amplify;

import java.util.HashMap;
import java.util.Map;

public class LoginFragment extends Fragment {
    NavController navController;
    View button_list;
    View spinner;
    public LoginFragment() {
    }

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
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
        /*signup_button.setOnClickListener((v)->{
            navController.navigate(R.id.action_loginFragment_to_signupFragment);
        });*/
        Button login_button = fragment_view.findViewById(R.id.button2);
        checkSignIn();
        login_button.setOnClickListener((v)->{
            button_list.setVisibility(View.GONE);
            spinner.setVisibility(View.VISIBLE);
            Log.i("AuthQuickstart", "signing in");
            Amplify.Auth.signIn(
                usn_textView.getText().toString(),
                pass_textView.getText().toString(),
                result -> {
                    if(result.isSignInComplete()){
                        checkSignIn();
                    }else{
                        Log.i("AuthQuickStart","Sign in not complete");
                    }
                },
                error -> Log.e("AuthQuickstart", error.toString())
            );
        });
        return fragment_view;
    }
    public void checkSignIn(){
        Log.i("Hachiman","Checking previous SignIn");
        Amplify.Auth.fetchAuthSession(
                result ->{
                    Log.i("AmplifyQuickstart", result.toString());
                    if(result.isSignedIn()){
                        AWSCognitoAuthSession cognitoAuthSession = (AWSCognitoAuthSession) result;
                        String username = Amplify.Auth.getCurrentUser().getUsername();
                        String auth_token = cognitoAuthSession.getUserPoolTokens().getValue().getAccessToken();
                        Log.i("Hachiman",username+":"+auth_token);
                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Bundle bundle = new Bundle();
                                bundle.putString("username",username);
                                bundle.putString("auth_token",auth_token);
                                navController.navigate(R.id.action_loginFragment_to_homeFragment,bundle);
                            }
                        });
                    }else{
                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                button_list.setVisibility(View.VISIBLE);
                                spinner.setVisibility(View.GONE);
                            }
                        });
                    }
                },
                error -> Log.e("AmplifyQuickstart", error.toString())
        );
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