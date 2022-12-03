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
import android.widget.TextView;

import com.infsml.hachiman.R;
import com.infsml.hachiman.Utility;
import com.infsml.hachiman.databinding.AdminHomeElementBinding;
import com.infsml.hachiman.databinding.AdminOptionElementBinding;
import com.infsml.hachiman.databinding.AdminTitleElementBinding;
import com.infsml.hachiman.databinding.FragmentAdminOptionBinding;

import org.json.JSONArray;
import org.json.JSONObject;

public class AdminOptionFragment extends Fragment {
    NavController navController;
    Bundle bundle;
    String username = null;
    String auth_token = null;
    String event = null;

    public AdminOptionFragment() {
    }
    public static AdminOptionFragment newInstance(String param1, String param2) {
        AdminOptionFragment fragment = new AdminOptionFragment();
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
    FragmentAdminOptionBinding binding;
    boolean top_app_vis = false;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAdminOptionBinding.inflate(inflater,container,false);
        navController = Navigation.findNavController(requireActivity(), R.id.fragmentContainerView);
        bundle = getArguments();
        assert bundle!=null;
        username = bundle.getString("username");
        auth_token = bundle.getString("auth_token");
        event = bundle.getString("event");
        binding.addBtn.setOnClickListener(v->{
            if(optionData!=null) {
                Bundle bundle1 = new Bundle(bundle);
                bundle1.putString("prev",optionData.optJSONArray("prev").toString());
                navController.navigate(R.id.action_adminOptionFragment_to_adminAddOptionFragment, bundle1);
            }
        });
        binding.csvBtn.setOnClickListener(v->{
            if(optionData!=null) {
                Bundle bundle1 = new Bundle(bundle);
                bundle1.putString("prev",optionData.optJSONArray("prev").toString());
                navController.navigate(R.id.action_adminOptionFragment_to_adminCsvOptionFragment, bundle1);
            }
        });

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        final OptionAdapter optionAdapter = new OptionAdapter();
        binding.recyclerView.setAdapter(optionAdapter);
        fetchOptionData(()->{
            optionAdapter.updateData();
        });
        binding.topApp.setOnClickListener(v->{
            top_app_vis = !top_app_vis;
            if(top_app_vis)binding.btnStuff.setVisibility(View.VISIBLE);
            else binding.btnStuff.setVisibility(View.GONE);
        });
        binding.btnStuff.setVisibility(View.GONE);

        return binding.getRoot();
    }
    JSONObject optionData;
    public void fetchOptionData(Runnable runnable) {
        (new Thread() {
            @Override
            public void run() {
                try {
                    if(optionData==null) {
                        JSONObject payload = new JSONObject();
                        payload.put("username", username);
                        payload.put("auth_token", auth_token);
                        payload.put("event", event);
                        optionData = Utility.postJSON(
                                Utility.api_base + "/get-option-admin",
                                payload.toString()
                        );
                    }
                    requireActivity().runOnUiThread(runnable);
                } catch (Exception e) {
                    Log.e("Hachiman", "Error", e);
                }
            }
        }).start();
    }
    public class OptionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        public class VHtitle extends RecyclerView.ViewHolder{
            TextView textView;
            AdminTitleElementBinding binding;
            public VHtitle(AdminTitleElementBinding binding){
                super(binding.getRoot());
                this.textView=binding.title;
                this.binding = binding;
            }
        }
        public class VHoption extends RecyclerView.ViewHolder{
            AdminOptionElementBinding binding1;
            boolean btn_vis = false;
            String code;
            String type;
            public VHoption(AdminOptionElementBinding binding1){
                super(binding1.getRoot());
                this.binding1=binding1;
                binding1.btnLyt.setVisibility(View.GONE);
                binding1.parentLayout.setOnClickListener(v->{
                    btn_vis = !btn_vis;
                    if(btn_vis)binding1.btnLyt.setVisibility(View.VISIBLE);
                    else binding1.btnLyt.setVisibility(View.GONE);
                });
                binding1.deleteBtn.setOnClickListener(v->{
                    binding.recyclerView.setVisibility(View.GONE);
                    (new Thread(){
                        @Override
                        public void run(){
                            try {
                                JSONObject payload = new JSONObject();
                                payload.put("username",username);
                                payload.put("auth_token",auth_token);
                                payload.put("event",event);
                                payload.put("option",code);
                                payload.put("course_type",type);
                                JSONObject res = Utility.postJSON(
                                        Utility.api_base+"/delete-option-admin",
                                        payload.toString()
                                );
                                requireActivity().runOnUiThread(()->{
                                    navController.navigate(R.id.action_adminOptionFragment_self,bundle);
                                });
                            }catch (Exception e){
                                e.printStackTrace();
                                requireActivity().runOnUiThread(()->{
                                    binding.recyclerView.setVisibility(View.VISIBLE);
                                    binding1.deleteBtn.setText("Retry");
                                });
                            }
                        }
                    }).start();
                });
            }
        }
        JSONArray prev;
        JSONArray data;
        public OptionAdapter(){
            prev = new JSONArray();
            data = new JSONArray();
        }
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder holder;
            if(viewType==0||viewType==2){
                AdminTitleElementBinding titleElementBinding = AdminTitleElementBinding.inflate(
                        LayoutInflater.from(parent.getContext()),parent,false);
                holder = new VHtitle(titleElementBinding);
            }else{
                AdminOptionElementBinding binding1 = AdminOptionElementBinding.inflate(
                        LayoutInflater.from(parent.getContext()),parent,false
                );
                holder = new VHoption(binding1);
            }
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            int viewType = getItemViewType(position);
            if(viewType==0){
                VHtitle h = (VHtitle) holder;
                h.textView.setText("Previous Courses");
            }else if(viewType==1){
                VHoption p = (VHoption) holder;
                JSONObject object= prev.optJSONObject(position-1);
                p.binding1.codeVal.setText(object.optString("code"));
                p.binding1.nameVal.setText(object.optString("name"));
                p.binding1.semVal.setVisibility(View.GONE);
                p.code = object.optString("code");
                p.type = "previous";
            }else if(viewType==2){
                VHtitle h = (VHtitle) holder;
                h.textView.setText("Current Choices");
            }
            else{
                VHoption p = (VHoption) holder;
                JSONObject object = data.optJSONObject(position-prev.length()-2);
                p.binding1.codeVal.setText(object.optString("code"));
                p.binding1.nameVal.setText(object.optString("name"));
                p.binding1.semVal.setVisibility(View.VISIBLE);
                p.binding1.semVal.setText("Available : "+object.optString("availability")+"Previous Requirement : "+object.optString("requirement"));
                p.code = object.optString("code");
                p.type = "current";
            }
        }

        @Override
        public int getItemCount() {
            return prev.length()+data.length()+2;
        }
        @Override
        public int getItemViewType(int position){
            if(position==0)return 0;
            if(position<prev.length()+1)return 1;
            if(position==(prev.length()+1))return 2;
            return 3;
        }
        public void updateData(){
            prev = optionData.optJSONArray("prev");
            data = optionData.optJSONArray("data");
            notifyDataSetChanged();
        }
    }
}