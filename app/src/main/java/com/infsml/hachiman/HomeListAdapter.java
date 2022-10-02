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
                controller.navigate(R.id.action_homeFragment_to_optionFragment,bundle);
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
