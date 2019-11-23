package com.shadowhite.archieve1971.ui.document;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;

import com.shadowhite.archieve1971.R;
import com.shadowhite.archieve1971.data.local.document.Documents;
import com.shadowhite.archieve1971.data.local.documentcategories.Datum;
import com.shadowhite.archieve1971.data.local.documentcategories.DocumentCategories;
import com.shadowhite.archieve1971.data.remote.RemoteApiProvider;
import com.shadowhite.archieve1971.data.remote.home.RemoteVideoApiInterface;
import com.shadowhite.archieve1971.databinding.ActivityDocumentBinding;
import com.shadowhite.archieve1971.ui.adapter.CategoryTabsAdapter;
import com.shadowhite.archieve1971.ui.adapter.DocumentAdapter;
import com.shadowhite.archieve1971.ui.documentdetails.DocumentDetailsActivity;
import com.w3engineers.ext.strom.application.ui.base.BaseActivity;
import com.w3engineers.ext.strom.application.ui.base.ItemClickListener;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DocumentActivity extends BaseActivity {

    private ActivityDocumentBinding mBinding;
    //This list for search
    public  List<Datum> mModelList = new ArrayList<>();

    private RemoteVideoApiInterface mRemoteVideoApiInterface;
    private CategoryTabsAdapter adapter;
    private DocumentAdapter videoadapter;
    public static void runActivity(Context context) {
        Intent intent = new Intent(context, DocumentActivity.class);
        runCurrentActivity(context, intent);
    }

    private void initTabs(List<Datum> tabs)
    {
        for(Datum datum:tabs){
            mBinding.categoryTabLayout.addTab(mBinding.categoryTabLayout.newTab().setText(datum.getDocCatTitle()));
        }

    }
    @Override
    protected int getLayoutId() {
        return R.layout.activity_document;
    }

    @Override
    protected void startUI() {
         mBinding=(ActivityDocumentBinding) getViewDataBinding();
        mRemoteVideoApiInterface= RemoteApiProvider.getInstance().getRemoteHomeVideoApi();
        adapter=new CategoryTabsAdapter(this);
        videoadapter=new DocumentAdapter(this);
        mBinding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

       videoadapter.setItemClickListener(new ItemClickListener<com.shadowhite.archieve1971.data.local.document.Datum>() {
           @Override
           public void onItemClick(View view, com.shadowhite.archieve1971.data.local.document.Datum item) {
               DocumentDetailsActivity.runActivity(DocumentActivity.this,item);
               finish();
           }
       });

        mBinding.categoryTabLayout.setSelectedTabIndicatorColor(Color.TRANSPARENT);
        mBinding.categoryTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
               getVideosByCategory(mModelList.get(tab.getPosition()).getDocId());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        setClickListener(mBinding.backBtn);
        initRecyclerView();
        getVideoCategories();
    }
    private void initRecyclerView(){



        mBinding.imagesRecyclerview.setAdapter(videoadapter);
        mBinding.imagesRecyclerview.setLayoutManager(new LinearLayoutManager(this));

    }

    private void getVideoCategories()
    {
       mRemoteVideoApiInterface.getDocumentCategories(getResources().getString(R.string.api_token_value),"0").enqueue(new Callback<DocumentCategories>() {
           @Override
           public void onResponse(Call<DocumentCategories> call, Response<DocumentCategories> response) {
               if(response.isSuccessful())
               {
                   DocumentCategories documentCategories=response.body();
                   mModelList=documentCategories.getData();

                   initTabs(mModelList);
                   getVideosByCategory(mModelList.get(0).getDocId());
               }
               else {

               }
           }

           @Override
           public void onFailure(Call<DocumentCategories> call, Throwable t) {

           }
       });
    }

    private void getVideosByCategory(String cat_id)
    {
        mRemoteVideoApiInterface.getDocumentById(getResources().getString(R.string.api_token_value),cat_id,"0").enqueue(new Callback<Documents>() {
            @Override
            public void onResponse(Call<Documents> call, Response<Documents> response) {

                if(response.isSuccessful())
                {
                    List<com.shadowhite.archieve1971.data.local.document.Datum> videoData=response.body().getData();
                    videoadapter.clear();
                    if(videoData!=null)
                    {
                        videoadapter.addItems(videoData);
                    }
                    Log.d("datacheck","error:"+response.body());
                }
                else {
                    Log.d("datacheck","error:"+response.code());
                }
            }

            @Override
            public void onFailure(Call<Documents> call, Throwable t) {
                Log.d("datacheck","error:"+t.getMessage());
            }
        });
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if(view.getId()==mBinding.backBtn.getId())
        {
            finish();
        }
    }
}
