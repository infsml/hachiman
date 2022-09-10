package com.infsml.hachiman;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

public class OptionListAdapter extends RecyclerView.Adapter<OptionListAdapter.ViewHolder>{
    JSONArray items;
    NavController controller;
    Bundle bundle;
    class ViewHolder extends RecyclerView.ViewHolder{
        String value;
        String available;
        View itemView;
        TextView mainText;
        public ViewHolder(
                @NonNull @NotNull View itemView,
                NavController controller,
                Bundle bundle) {
            super(itemView);
            this.itemView = itemView;
            mainText = itemView.findViewById(R.id.textView);
            View view = itemView.findViewById(R.id.parent_layout);
            view.setOnClickListener(v->{
                bundle.putString("code",value);
                bundle.putString("available",available);
                bundle.putBoolean("registered",false);
                controller.navigate(R.id.action_optionFragment_to_registerFragment,bundle);
            });
        }
        public void putText(String text){
            mainText.setText(text);
        }
    }
    public OptionListAdapter(NavController controller, Bundle bundle){
        items = new JSONArray();
        this.controller=controller;
        this.bundle=bundle;
    }
    @NonNull
    @NotNull
    @Override
    public OptionListAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        OptionListAdapter.ViewHolder viewHolder = new OptionListAdapter.ViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.home_list_element,parent,false)
                ,controller,new Bundle(bundle));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull OptionListAdapter.ViewHolder holder, int position) {
        JSONObject jsonObject = items.optJSONObject(position);
        holder.putText(jsonObject.optJSONObject("code").optString("S"));
        holder.value=jsonObject.optJSONObject("code").optString("S");
        holder.available=jsonObject.optJSONObject("available").optString("N");
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
