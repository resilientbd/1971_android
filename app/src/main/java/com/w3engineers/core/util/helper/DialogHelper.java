package com.w3engineers.core.util.helper;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.w3engineers.core.videon.R;
import com.w3engineers.core.videon.data.local.adaptermodel.ItemVideoDownload;

import java.util.List;



public class DialogHelper implements MyDialogAdapter.ItemClickListener{
    private static View selectedView;
    private static CmdDownload cmdDownload;
    private InputDialogListener inputDialogListener;
    private static Dialog dialog;

    public void showDialog(Context context, CmdDownload cmd, int layoutId, List<String> strings){
        cmdDownload=cmd;
         dialog = new Dialog(context, android.R.style.Theme_Holo_Light_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(layoutId);
        MyDialogAdapter adapter=new MyDialogAdapter(context,strings);
        RecyclerView recyclerView=dialog.findViewById(R.id.dialog_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);
        adapter.setItemClickListener(this);
        adapter.notifyDataSetChanged();
        dialog.show();


    }
    public void showInputDialog(Context context, InputDialogListener inputDialogListener,ItemVideoDownload itemVideoDownload){
        this.inputDialogListener=inputDialogListener;
        dialog = new Dialog(context, android.R.style.Theme_Holo_Light_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_input_dialog);
        EditText inputText=dialog.findViewById(R.id.idDialogInputText);

        Button btnSave=dialog.findViewById(R.id.idSave);
        Button btnCancel=dialog.findViewById(R.id.idCancel);
        if(inputDialogListener!=null)
        {
            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String filename=inputText.getText().toString().trim();
                    inputDialogListener.onSave(filename+"."+itemVideoDownload.getFormat(),itemVideoDownload.getUrl());
                    dialog.cancel();
                }
            });

        }
        if(inputDialogListener!=null)
        {
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    inputDialogListener.onCancel();
                    dialog.cancel();
                }
            });
        }
        dialog.show();
    }
    private static void executeTouchAction(Context context, View v) {
        TextView textView= (TextView) v;
        if((selectedView!=null))
        {
            selectedView.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
            selectedView=null;
        }

            v.setBackgroundColor(context.getResources().getColor(R.color.transparent_black));
            selectedView=v;
            cmdDownload.executeAction(textView.getText().toString());
            dialog.dismiss();
    }

    public void setInputDialogListener(InputDialogListener inputDialogListener) {
        this.inputDialogListener = inputDialogListener;
    }

    @Override
    public void onItemClick(Context context, TextView textView) {
        executeTouchAction(context,textView);
    }
    public interface InputDialogListener{
        public void onSave(String filename,String url);
        public void onCancel();
    }
}
