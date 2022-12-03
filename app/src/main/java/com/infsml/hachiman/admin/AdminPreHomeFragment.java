package com.infsml.hachiman.admin;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.infsml.hachiman.R;
import com.infsml.hachiman.Utility;
import com.infsml.hachiman.databinding.FragmentAdminPreHomeBinding;

import org.json.JSONObject;

public class AdminPreHomeFragment extends Fragment {

    NavController navController;
    String username=null;
    String auth_token=null;
    Bundle bundle=null;

    public AdminPreHomeFragment() {
    }

    public static AdminPreHomeFragment newInstance(String param1, String param2) {
        AdminPreHomeFragment fragment = new AdminPreHomeFragment();
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
    FragmentAdminPreHomeBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAdminPreHomeBinding.inflate(inflater,container,false);
        navController = Navigation.findNavController(getActivity(),R.id.fragmentContainerView);
        bundle = getArguments();
        username=bundle.getString("username");
        auth_token=bundle.getString("auth_token");
        binding.courseEdit.setOnClickListener(v -> {
            navController.navigate(R.id.action_adminPreHomeFragment_to_adminOptionFragment,bundle);
        });
        binding.reportBtn.setOnClickListener(v -> {
            navController.navigate(R.id.action_adminPreHomeFragment_to_adminReportFragment,bundle);
        });
        binding.templateBtn.setOnClickListener(v->{
            navController.navigate(R.id.action_adminPreHomeFragment_to_adminCsvTemplateFragment,bundle);
        });
        binding.textView8.setText(bundle.getString("event"));
        return binding.getRoot();
    }
    /*public void fetchUserAttributes(Runnable runnable){
        (new Thread(){
            @Override
            public void run() {
                try {
                    JSONObject payload = new JSONObject();
                    payload.put("username",username);
                    payload.put("auth_token",auth_token);
                    JSONObject jsonObject = Utility.postJSON(
                            Utility.api_base+"/admin-verify",
                            payload.toString()
                    );
                    requireActivity().runOnUiThread(runnable);
                }catch (Exception e){
                    Log.e("Hachiman","Detail Error",e);
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Bundle logout = new Bundle();
                            logout.putBoolean("logout",true);
                            navController.navigate(R.id.action_adminPreHomeFragment_to_loginFragment,logout);
                        }
                    });
                }
            }
        }).start();
    }*/
}