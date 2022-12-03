package com.infsml.hachiman.admin;

import android.os.Bundle;

import androidx.annotation.NonNull;
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

import com.google.android.material.appbar.MaterialToolbar;

import com.infsml.hachiman.HomeFragment;
import com.infsml.hachiman.R;
import com.infsml.hachiman.Utility;
import com.infsml.hachiman.databinding.AdminHomeElementBinding;
import com.infsml.hachiman.databinding.FragmentAdminHomeBinding;
import com.infsml.hachiman.databinding.HomeListElement2Binding;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

public class AdminHomeFragment extends Fragment {
    NavController navController;
    String username=null;
    String auth_token=null;
    Bundle bundle=null;
    JSONObject event_data;
    public AdminHomeFragment() {
    }

    public static AdminHomeFragment newInstance(String param1, String param2) {
        AdminHomeFragment fragment = new AdminHomeFragment();
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

    FragmentAdminHomeBinding binding;
    boolean top_app_vis = false;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAdminHomeBinding.inflate(inflater,container,false);
        navController = Navigation.findNavController(getActivity(),R.id.fragmentContainerView);
        bundle = getArguments();
        username=bundle.getString("username");
        auth_token=bundle.getString("auth_token");
        binding.addBtn.setOnClickListener(v -> {
            navController.navigate(R.id.action_adminHomeFragment_to_adminAddEventFragment,bundle);
        });
        binding.csvBtn.setOnClickListener(v->{
            navController.navigate(R.id.action_adminHomeFragment_to_adminCsvEventFragment,bundle);
        });
        binding.passResetBtn.setOnClickListener(v->{
            navController.navigate(R.id.action_adminHomeFragment_to_passwordResetFragment,bundle);
        });

        fetchUserData(()->{
                AdminHomeFragment.HomeListAdapter adapter = (AdminHomeFragment.HomeListAdapter) binding.homeRecyclerView.getAdapter();
                adapter.loadData(event_data);
            });
        binding.signoutBtn.setOnClickListener(v -> {
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
                                navController.navigate(R.id.action_adminHomeFragment_to_loginFragment,logout);
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
        binding.homeRecyclerView.setAdapter(new AdminHomeFragment.HomeListAdapter(navController));
        binding.homeRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.topApp.setOnClickListener(v->{
            top_app_vis = !top_app_vis;
            if(top_app_vis)binding.btnStuff.setVisibility(View.VISIBLE);
            else binding.btnStuff.setVisibility(View.GONE);
        });
        binding.btnStuff.setVisibility(View.GONE);

        return binding.getRoot();
    }

    public void fetchUserData(Runnable runnable){
        (new Thread(){
            @Override
            public void run() {
                try {
                    JSONObject payload = new JSONObject();
                    payload.put("username",username);
                    payload.put("auth_token",auth_token);
                    event_data = Utility.postJSON(
                            Utility.api_base+"/get-event-admin",
                            payload.toString()
                    );
                    //chViewOutside(jsonObject);
                    //event_data = jsonObject.getJSONArray("data");
                    requireActivity().runOnUiThread(runnable);
                }catch (Exception e){
                    Log.e("Hachiman","Data Error",e);
                }
            }
        }).start();
    }
    public class HomeListAdapter extends RecyclerView.Adapter{
        JSONArray items;
        NavController controller;

        class ItemViewHolder extends RecyclerView.ViewHolder{
            private final AdminHomeElementBinding binding2;
            boolean expanded;
            String code;
            String name;
            public ItemViewHolder(AdminHomeElementBinding binding2) {
                super(binding2.getRoot());
                this.binding2 = binding2;
                expanded = true;
                binding2.coursesBtn.setText("Courses");
                binding2.btnLyt.setVisibility(View.GONE);
                binding2.parentLayout.setOnClickListener(v -> {
                    if (expanded) binding2.btnLyt.setVisibility(View.VISIBLE);
                    else binding2.btnLyt.setVisibility(View.GONE);
                    expanded = !expanded;
                });
                binding2.coursesBtn.setOnClickListener(v->{
                    Bundle bundle1 = new Bundle(bundle);
                    bundle1.putString("event",code);
                    navController.navigate(R.id.action_adminHomeFragment_to_adminPreHomeFragment,bundle1);
                });
                binding2.deleteBtn.setOnClickListener(v->{
                    binding.homeRecyclerView.setVisibility(View.GONE);
                    (new Thread(){
                        @Override
                        public void run(){
                            try {
                                JSONObject payload = new JSONObject();
                                payload.put("username",username);
                                payload.put("auth_token",auth_token);
                                payload.put("code",code);
                                JSONObject res = Utility.postJSON(
                                        Utility.api_base+"/delete-event-admin",
                                        payload.toString()
                                );
                                requireActivity().runOnUiThread(()->{
                                    navController.navigate(R.id.action_adminHomeFragment_self,bundle);
                                });
                            }catch (Exception e){
                                e.printStackTrace();
                                requireActivity().runOnUiThread(()->{
                                    binding.homeRecyclerView.setVisibility(View.VISIBLE);
                                    binding2.deleteBtn.setText("Retry");
                                });
                            }
                        }
                    }).start();
                });
            }
            public void setData(JSONObject jsonObject){
                code = jsonObject.optString("code");
                name = jsonObject.optString("name");
                binding2.codeVal.setText(code);
                binding2.nameVal.setText(name);
                binding2.semVal.setText(jsonObject.optString("sem")+" Sem");
            }
        }

        public HomeListAdapter(NavController controller){
            items = new JSONArray();
            this.controller=controller;
        }
        @NonNull
        @NotNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

            AdminHomeElementBinding binding = AdminHomeElementBinding.inflate(
                    LayoutInflater.from(parent.getContext()),parent,false
            );
            RecyclerView.ViewHolder viewHolder = new AdminHomeFragment.HomeListAdapter.ItemViewHolder(binding);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NotNull RecyclerView.ViewHolder holder, int position) {
                AdminHomeFragment.HomeListAdapter.ItemViewHolder viewHolder = (AdminHomeFragment.HomeListAdapter.ItemViewHolder) holder;
                JSONObject jsonObject = items.optJSONObject(position);
                viewHolder.setData(jsonObject);
        }
        public void loadData(JSONObject jsonArray){
            items=jsonArray.optJSONArray("data");
            notifyDataSetChanged();
        }
        @Override
        public int getItemCount() {
            return items.length();
        }

    }
}