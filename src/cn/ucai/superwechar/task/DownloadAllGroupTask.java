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
public class DownloadAllGroupTask extends BaseActivity {
    private static final String TAG = DownloadAllGroupTask.class.getName();
    Context mContext;
    String userName;
    String path;

    public DownloadAllGroupTask(Context mContext, String userName) {
        this.mContext = mContext;
        this.userName = userName;
        initPath();
    }

    private void initPath() {
        try {
            path = new ApiParams()
                    .with(I.Contact.USER_NAME,userName)
                    .getRequestUrl(I.REQUEST_DOWNLOAD_GROUPS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void execute(){
        executeRequest(new GsonRequest<Group[]>(path,Group[].class
                ,responseDownloadAllGroupTaskListener(),errorListener()));
    }

    private Response.Listener<Group[]> responseDownloadAllGroupTaskListener() {
        return new Response.Listener<Group[]>() {
            @Override
            public void onResponse(Group[] contacts) {
                if(contacts!=null){
                    ArrayList<Group> contactList=
                            superwecharApplication.getInstance().getGroupList();
                    ArrayList<Group> list = Utils.array2List(contacts);
                    contactList.clear();
                    contactList.addAll(list);
                    mContext.sendStickyBroadcast(new Intent("update_group_list"));
                }
            }
        };
    }
}
