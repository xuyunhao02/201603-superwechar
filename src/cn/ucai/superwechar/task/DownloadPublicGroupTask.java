package cn.ucai.superwechar.task;

import android.content.Context;
import android.content.Intent;

import com.android.volley.Response;

import java.util.ArrayList;

import cn.ucai.superwechar.I;
import cn.ucai.superwechar.activity.BaseActivity;
import cn.ucai.superwechar.bean.Group;
import cn.ucai.superwechar.data.ApiParams;
import cn.ucai.superwechar.data.GsonRequest;
import cn.ucai.superwechar.superwecharApplication;
import cn.ucai.superwechar.utils.Utils;

/**
 * Created by Administrator on 2016/5/23 0023.
 */
public class DownloadPublicGroupTask extends BaseActivity {
    private static final String TAG = DownloadPublicGroupTask.class.getName();
    Context mContext;
    String userName;
    String path;
    int mPage_id;
    int mPage_size;


    public DownloadPublicGroupTask(Context mContext, String userName,int Page_id,int Page_size) {
        this.mContext = mContext;
        this.userName = userName;
        this.mPage_id=Page_id;
        this.mPage_size = Page_size;
        initPath();
    }

    private void initPath() {
        try {
            path = new ApiParams()
                    .with(I.Contact.USER_NAME,userName)
                    .with(I.PAGE_ID,mPage_id+"")
                    .with(I.PAGE_SIZE,mPage_size+"")
                    .getRequestUrl(I.REQUEST_FIND_PUBLIC_GROUPS)
            ;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void execute(){
        executeRequest(new GsonRequest<Group[]>(path,Group[].class
                ,responseDownloadPublicGroupTaskListener(),errorListener()));
    }

    private Response.Listener<Group[]> responseDownloadPublicGroupTaskListener() {
        return new Response.Listener<Group[]>() {
            @Override
            public void onResponse(Group[] contacts) {
                if(contacts!=null){
                    ArrayList<Group> contactList=
                            superwecharApplication.getInstance().getPublicGroupList();
                    ArrayList<Group> list = Utils.array2List(contacts);
                    contactList.clear();
                    contactList.addAll(list);
                    mContext.sendStickyBroadcast(new Intent("update_public_group"));
                }
            }
        };
    }
}
