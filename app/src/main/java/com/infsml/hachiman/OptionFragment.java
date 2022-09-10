package com.infsml.hachiman;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavActionBuilder;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.amplifyframework.auth.AuthUserAttribute;
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession;
import com.amplifyframework.core.Amplify;
import com.google.android.material.appbar.MaterialToolbar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OptionFragment extends Fragment implements MenuProvider {
    NavController navController;
    RecyclerView recyclerView;
    String username=null;
    String auth_token=null;
    String event=null;
    Bundle bundle;
    MenuProvider _this;
    public OptionFragment() {
    }
    public static OptionFragment newInstance(String param1, String param2) {
        OptionFragment fragment = new OptionFragment();
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
        _this=this;
        View fragment_view =inflater.inflate(R.layout.fragment_option, container, false);
        navController = Navigation.findNavController(requireActivity(),R.id.fragmentContainerView);
        bundle = getArguments();
        if(bundle==null){
            navController.navigate(R.id.action_optionFragment_to_loginFragment);
            return fragment_view;
        }
        username=bundle.getString("username");
        auth_token=bundle.getString("auth_token");
        event = bundle.getString("event");
        if(username==null||auth_token==null){
            navController.navigate(R.id.action_optionFragment_to_loginFragment);
            return fragment_view;
        }
        if(event==null){
            navController.navigate(R.id.action_optionFragment_to_homeFragment,bundle);
            return fragment_view;
        }
        fetchUserData();
        recyclerView = fragment_view.findViewById(R.id.options_recycler_view);
        recyclerView.setAdapter(new OptionListAdapter(navController,bundle));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return fragment_view;
    }

    @Override
    public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.fragment_option_menu,menu);
    }

    @Override
    public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
        if(menuItem.getItemId()==R.id.add_option){
            navController.navigate(R.id.action_optionFragment_to_addOptionFragment,bundle);
        }
        return true;
    }
    public void chViewOutside(JSONObject jsonArray){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run(){
                Log.i("Hachiman","state:"+jsonArray.optInt("state"));
                if(jsonArray.optInt("state")==StateCodes.state_invalid_login){
                    navController.navigate(R.id.action_optionFragment_to_loginFragment);
                    return;
                }
                boolean admin = jsonArray.optJSONObject("auth").optBoolean("admin");
                if(admin){
                    MenuHost host = requireActivity();
                    host.addMenuProvider(_this,getViewLifecycleOwner());
                    bundle.putBoolean("isAdmin",true);
                }
                if(jsonArray.optInt("state")==StateCodes.state_already_registered){
                    JSONObject jsonObject = jsonArray.optJSONObject("data");
                    Bundle bundle1 = new Bundle(bundle);
                    bundle1.putString("code",jsonObject.optJSONObject("code").optString("S"));
                    bundle1.putString("available",jsonObject.optJSONObject("available").optString("N"));
                    bundle1.putBoolean("registered",true);
                    navController.navigate(R.id.action_optionFragment_to_registerFragment2,bundle1);
                    return;
                }
                OptionListAdapter adapter = (OptionListAdapter) recyclerView.getAdapter();
                adapter.loadData(jsonArray.optJSONArray("data"));
                Log.i("Hachiman",username);
                MaterialToolbar toolbar = requireActivity().findViewById(R.id.materialToolbar);
                toolbar.setTitle(username);
            }
        });
    }

    public void fetchUserData(){
        (new Thread(){
            @Override
            public void run() {
                try {
                    JSONObject payload = new JSONObject();
                    payload.put("event",event);
                    payload.put("usn",username);
                    payload.put("auth_token",auth_token);
                    JSONObject jsonObject = Utility.postJSON(
                        "https://api.infsml.in/hachiman/get-option/",
                        payload.toString()
                    );
                    chViewOutside(jsonObject);
                }catch (Exception e){
                    Log.e("Hachiman","Error",e);
                }
            }
        }).start();
    }
}

