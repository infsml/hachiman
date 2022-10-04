package com.infsml.hachiman;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RegisterFragment extends Fragment {
    NavController navController;
    View button_list;
    String available;
    View spinner;
    String username = null;
    String auth_token = null;
    String event = null;
    RegisterFragmentAdapter adapter;
    String code = null;
    String url_link_default = Utility.api_base + "/register";
    boolean isAdmin = false;
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
        navController = Navigation.findNavController(requireActivity(), R.id.fragmentContainerView);
        bundle = getArguments();
        if (bundle == null) {
            navController.navigate(R.id.action_registerFragment_to_loginFragment);
            return fragment_view;
        }
        username = bundle.getString("username");
        auth_token = bundle.getString("auth_token");
        isAdmin = bundle.getBoolean("isAdmin", false);
        event = bundle.getString("event");
        boolean registered = bundle.getBoolean("registered");
        Log.i("Hachiman", bundle.toString());
        if (username == null || auth_token == null) {
            navController.navigate(R.id.action_registerFragment_to_loginFragment);
            return fragment_view;
        }
        if (event == null) {
            navController.navigate(R.id.action_registerFragment_to_homeFragment, bundle);
            return fragment_view;
        }
        button_list = fragment_view.findViewById(R.id.btnLyt);
        spinner = fragment_view.findViewById(R.id.progressBar);
        button_list.setVisibility(View.VISIBLE);
        spinner.setVisibility(View.GONE);
        List<ListItemHolder> objectList = new ArrayList<>();
        objectList.add(new ListItemHolder("USN", username, 0));
        objectList.add(new ListItemHolder("Name", "Infinitely Small", 0));
        objectList.add(new ListItemHolder("Sem", "7", 0));
        //objectList.add(new ListItemHolder("Course", "", 1));

        Button register_button = fragment_view.findViewById(R.id.button5);
        /*if(registered){
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
                        jsonObject.put("username",username);
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
        });*/

        Button cancel_button = fragment_view.findViewById(R.id.button6);
        cancel_button.setOnClickListener(v -> {
            navController.popBackStack();
        });
        RecyclerView recyclerView = fragment_view.findViewById(R.id.recyclerView);
        adapter = new RegisterFragmentAdapter(objectList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        String f_choice = bundle.getString("First Choice");
        String s_choice = bundle.getString("Second Choice");
        try {
            if(f_choice!=null)optionData = new JSONArray(bundle.getString("optionData"));
        } catch (JSONException e) {
            Log.e("Hachiman","JSONArray Error",e);
            f_choice=null;
        }
        if(f_choice==null) {
            fetchOptionData(() -> {
                adapter.data.add(new ListItemHolder("First Choice", optionData.toString(), 1));
                adapter.notifyItemInserted(adapter.data.size() - 1);
            });
        }else{
            adapter.data.add(new ListItemHolder("First Choice",f_choice, 0));
            adapter.notifyItemInserted(adapter.data.size() - 1);
            if(s_choice==null) {
                adapter.data.add(new ListItemHolder("Second Choice", optionData.toString(), 1));
                adapter.notifyItemInserted(adapter.data.size() - 1);
            }else{
                adapter.data.add(new ListItemHolder("Second Choice", s_choice, 0));
                adapter.notifyItemInserted(adapter.data.size() - 1);
                register_button.setVisibility(View.VISIBLE);
            }
        }
        return fragment_view;
    }

    JSONArray optionData = null;

    public void fetchOptionData(Runnable runnable) {
        (new Thread() {
            @Override
            public void run() {
                try {
                    JSONObject payload = new JSONObject();
                    payload.put("username", username);
                    payload.put("auth_token", auth_token);
                    payload.put("event", event);
                    JSONObject jsonObject = Utility.postJSON(
                            Utility.api_base + "/get-option",
                            payload.toString()
                    );
                    optionData = jsonObject.getJSONArray("data");
                    requireActivity().runOnUiThread(runnable);
                } catch (Exception e) {
                    Log.e("Hachiman", "Error", e);
                }
            }
        }).start();
    }


    class RegisterFragmentAdapter extends RecyclerView.Adapter<RegisterFragmentAdapter.ViewHolder> {

        public class ViewHolder extends RecyclerView.ViewHolder {
            Button choose;
            TextView val;
            TextView label;
            String val_str;
            String name_str;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                choose = itemView.findViewById(R.id.choose);
                val = itemView.findViewById(R.id.val);
                label = itemView.findViewById(R.id.label);
            }

            public void setType(int type) {
                if (type == 0) {
                    choose.setVisibility(View.GONE);
                    val.setVisibility(View.VISIBLE);
                }
                if (type == 1) {
                    val.setVisibility(View.GONE);
                    choose.setVisibility(View.VISIBLE);
                    choose.setOnClickListener(v -> {
                        Bundle nw_bundle = new Bundle(bundle);
                        nw_bundle.putString("optionData",val_str);
                        nw_bundle.putString("requirement_for",name_str);
                        navController.navigate(R.id.action_registerFragment_to_optionFragment,nw_bundle);
                    });
                }
                if(type == 2){

                }
            }
        }

        List<ListItemHolder> data;

        public RegisterFragmentAdapter(List<ListItemHolder> data) {
            this.data = data;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.register_list_element, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            ListItemHolder itemHolder = data.get(position);
            holder.setType(itemHolder.type);
            holder.label.setText(itemHolder.name);
            if (itemHolder.type == 0) holder.val.setText(itemHolder.val);
            holder.val_str=itemHolder.val;
            holder.name_str=itemHolder.name;
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }

    class ListItemHolder {
        String name;
        String val;
        int type;

        public ListItemHolder(String name, String val, int type) {
            this.name = name;
            this.val = val;
            this.type = type;
        }
    }
}