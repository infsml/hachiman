package com.infsml.hachiman;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class OptionFragment extends Fragment {
    NavController navController;
    RecyclerView recyclerView;
    String username = null;
    String auth_token = null;
    String event = null;
    Bundle bundle;
    MenuProvider _this;
    String requirement_for=null;
    boolean alternate_option;

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
        View fragment_view = inflater.inflate(R.layout.fragment_option, container, false);
        navController = Navigation.findNavController(requireActivity(), R.id.fragmentContainerView);
        bundle = getArguments();
        JSONArray items;
        Log.i("Hachiman",bundle.toString());
        try {
            items = new JSONArray(bundle.getString("optionData"));
            requirement_for = bundle.getString("requirement_for");
        } catch (JSONException e) {
            Log.e("Hachiman","JSONArray Error",e);
            items = new JSONArray();
        }
        recyclerView = fragment_view.findViewById(R.id.options_recycler_view);
        recyclerView.setAdapter(new OptionListAdapter(items));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return fragment_view;
    }

    class OptionListAdapter extends RecyclerView.Adapter<OptionListAdapter.ViewHolder> {
        JSONArray items;

        class ViewHolder extends RecyclerView.ViewHolder {
            String value;
            String available;
            View itemView;
            TextView mainText;
            View expand_layout;
            boolean expanded;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                this.itemView = itemView;
                mainText = itemView.findViewById(R.id.title);
                View view = itemView.findViewById(R.id.parent_layout);
                expand_layout = itemView.findViewById(R.id.expand_layout);
                expanded = true;
                view.setOnClickListener(v -> {
                    if (expanded) expand_layout.setVisibility(View.VISIBLE);
                    else expand_layout.setVisibility(View.GONE);
                    expanded = !expanded;
                });
                view = itemView.findViewById(R.id.button7);
                view.setOnClickListener(v -> {
                    Bundle bundle1 = new Bundle(bundle);
                    bundle1.putString(requirement_for, value);
                    navController.navigate(R.id.action_optionFragment_to_registerFragment, bundle1);
                });
            }
            public void putText(String text){
                mainText.setText(text);
            }
        }

        public OptionListAdapter(JSONArray items) {
            this.items=items;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.option_list_element,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            JSONObject jsonObject = items.optJSONObject(position);
            String code=jsonObject.optString("code");
            holder.putText(code);
            holder.value=code;
            holder.available=""+jsonObject.optInt("availability");
        }

        @Override
        public int getItemCount() {
            return items.length();
        }
    }
}