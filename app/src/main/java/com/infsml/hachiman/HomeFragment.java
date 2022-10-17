package com.infsml.hachiman;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

public class HomeFragment extends Fragment{
    NavController navController;
    RecyclerView recyclerView;
    String username=null;
    String auth_token=null;
    Bundle bundle=null;
    int semester=-1;
    String section=null;
    String name=null;
    int admin=-1;
    JSONArray event_data;
    public HomeFragment() {
    }
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        View fragment_view =inflater.inflate(R.layout.fragment_home, container, false);
        navController = Navigation.findNavController(getActivity(),R.id.fragmentContainerView);
        bundle = getArguments();
        if(bundle==null){
            navController.navigate(R.id.action_homeFragment_to_loginFragment);
            return fragment_view;
        }
        username=bundle.getString("username");
        auth_token=bundle.getString("auth_token");
        if(username==null||auth_token==null){
            navController.navigate(R.id.action_homeFragment_to_loginFragment);
            return fragment_view;
        }
        fetchUserAttributes(()->{
            fetchUserData(()->{
                HomeListAdapter adapter = (HomeListAdapter) recyclerView.getAdapter();
                adapter.loadData(event_data);
                Log.i("Hachiman",username);
            });
            MaterialToolbar toolbar = requireActivity().findViewById(R.id.materialToolbar);
            toolbar.setTitle(username);
        });
        recyclerView = fragment_view.findViewById(R.id.home_recycler_view);
        recyclerView.setAdapter(new HomeListAdapter(navController,bundle));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        Button sign_out = fragment_view.findViewById(R.id.button3);
        sign_out.setOnClickListener(v -> {
            Log.i("AuthQuickStart","signing out XD");
            Button b = (Button) v;
            b.setText(R.string.signing_out);
            b.setClickable(false);
            b.setEnabled(false);
            (new Thread() {
                @Override
                public void run() {
                    try {
                        JSONObject payload = new JSONObject();
                        payload.put("username", username);
                        payload.put("auth_token", auth_token);
                        JSONObject jsonObject = Utility.postJSON(
                                Utility.api_base + "/logout",
                                payload.toString()
                        );
                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Bundle logout = new Bundle();
                                logout.putBoolean("logout",true);
                                navController.navigate(R.id.action_homeFragment_to_loginFragment,logout);
                            }
                        });
                    } catch (Exception e) {
                        Log.e("Hachiman","Logout Error",e);
                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                b.setText(R.string.signout);
                                b.setClickable(true);
                                b.setEnabled(true);
                            }
                        });
                    }
                }
            }).start();
        });
        return fragment_view;
    }
    public void chViewOutside(JSONObject jsonArray){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                /*if(jsonArray.optInt("state")==StateCodes.state_invalid_login){
                    navController.navigate(R.id.action_homeFragment_to_loginFragment);
                    return;
                }
                boolean admin = jsonArray.optJSONObject("auth").optBoolean("admin");
                if(admin){
                    MenuHost host = requireActivity();
                    host.addMenuProvider(_this,getViewLifecycleOwner());
                }*/
                HomeListAdapter adapter = (HomeListAdapter) recyclerView.getAdapter();
                adapter.loadData(jsonArray.optJSONArray("data"));
                Log.i("Hachiman",username);
            }
        });
    }
    public void runOutside(int res){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                navController.navigate(res);
            }
        });
    }
    public void fetchUserAttributes(Runnable runnable){
        (new Thread(){
            @Override
            public void run() {
                try {
                    JSONObject payload = new JSONObject();
                    payload.put("username",username);
                    payload.put("auth_token",auth_token);
                    JSONObject jsonObject = Utility.postJSON(
                            Utility.api_base+"/user-attr",
                            payload.toString()
                    );
                    semester = jsonObject.getInt("semester");
                    section = jsonObject.getString("section");
                    admin = jsonObject.getInt("admin");
                    name=jsonObject.getString("name");
                    requireActivity().runOnUiThread(runnable);
                }catch (Exception e){
                    Log.e("Hachiman","Detail Error",e);
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Bundle logout = new Bundle();
                            logout.putBoolean("logout",true);
                            navController.navigate(R.id.action_homeFragment_to_loginFragment,logout);
                        }
                    });
                }
            }
        }).start();
    }
    public void fetchUserData(Runnable runnable){
        (new Thread(){
            @Override
            public void run() {
                try {
                    JSONObject payload = new JSONObject();
                    payload.put("username",username);
                    payload.put("auth_token",auth_token);
                    payload.put("semester",semester);
                    JSONObject jsonObject = Utility.postJSON(
                        Utility.api_base+"/get-event",
                        payload.toString()
                    );
                    //chViewOutside(jsonObject);
                    event_data = jsonObject.getJSONArray("data");
                    requireActivity().runOnUiThread(runnable);
                }catch (Exception e){
                    Log.e("Hachiman","Data Error",e);
                }
            }
        }).start();
    }
    public class HomeListAdapter extends RecyclerView.Adapter<HomeListAdapter.ViewHolder> {
        JSONArray items;
        NavController controller;
        Bundle bundle;
        class ViewHolder extends RecyclerView.ViewHolder{
            TextView mainText;
            String value;
            public ViewHolder(
                    @NonNull @NotNull View itemView,
                    NavController controller,
                    Bundle bundle) {
                super(itemView);
                mainText = itemView.findViewById(R.id.textView);
                View view = itemView.findViewById(R.id.parent_layout);
                view.setOnClickListener(v->{
                    bundle.putString("event",value);
                    bundle.putString("name",name);
                    bundle.putInt("semester",semester);
                    bundle.putString("section",section);
                    bundle.putInt("admin",admin);
                    controller.navigate(R.id.action_homeFragment_to_registerFragment,bundle);
                });
            }
            public void putText(String text){
                mainText.setText(text);
            }
        }
        public HomeListAdapter(NavController controller, Bundle bundle){
            items = new JSONArray();
            this.controller=controller;
            this.bundle=bundle;
        }
        @NonNull
        @NotNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
            ViewHolder viewHolder = new ViewHolder(
                    LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.home_list_element,parent,false)
                    ,controller,new Bundle(bundle));
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
            JSONObject jsonObject = items.optJSONObject(position);
            String code = jsonObject.optString("code");
            holder.putText(code);
            holder.value=code;
        }
        public void loadData(JSONArray jsonArray){
            items=jsonArray;
            notifyDataSetChanged();
        }
        @Override
        public int getItemCount() {
            return items.length();
        }
    }
}