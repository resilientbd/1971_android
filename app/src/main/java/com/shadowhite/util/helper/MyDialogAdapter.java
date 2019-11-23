package com.shadowhite.util.helper;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shadowhite.archieve1971.R;

import java.util.List;

public class MyDialogAdapter extends RecyclerView.Adapter<MyDialogAdapter.ViewHolder> {
    public ItemClickListener itemClickListener;
    public MyDialogAdapter(Context context, List<String> stringList) {
        this.context = context;
        this.stringList = stringList;
    }
    public void setItemClickListener(ItemClickListener itemClickListener)
    {
        this.itemClickListener=itemClickListener;
    }
    private Context context;
    private List<String> stringList;
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater= LayoutInflater.from(context);
        ViewHolder holder=new ViewHolder(inflater.inflate(R.layout.item_dialog,parent,false));
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textView.setText(stringList.get(position));
    }

    @Override
    public int getItemCount() {
        Log.d("mytag","Size: "+stringList.size());
        return stringList.size();

    }

    public  class ViewHolder extends RecyclerView.ViewHolder{
    private TextView textView;
        public ViewHolder(View itemView) {
            super(itemView);
            textView=itemView.findViewById(R.id.item_dialog_content);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onItemClick(context,(TextView) v);
                }
            });
        }
    }
   public interface ItemClickListener{
        public  void onItemClick(Context context, TextView textView);
   }
}
