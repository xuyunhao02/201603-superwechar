package cn.ucai.superwechar.task;

import android.content.Context;
import android.content.Intent;

import com.android.volley.Response;

import java.util.ArrayList;
import java.util.HashMap;

import cn.ucai.superwechar.I;
import cn.ucai.superwechar.activity.BaseActivity;
import cn.ucai.superwechar.bean.Contact;
import cn.ucai.superwechar.data.ApiParams;
import cn.ucai.superwechar.data.GsonRequest;
import cn.ucai.superwechar.superwecharApplication;
import cn.ucai.superwechar.utils.Utils;

/**
 * Created by Administrator on 2016/5/23 0023.
 */
public class DownloadContactListTask extends BaseActivity {
    private static final String TAG = DownloadContactListTask.class.getName();
    Context mContext;
    String username;
    String path;

    public DownloadContactListTask(Context mContext, String username) {
        this.mContext = mContext;
        this.username = username;
        initPath();
    }

    private void initPath() {
        try {
            path = new ApiParams()
                    .with(I.Contact.USER_NAME,username)
                    .getRequestUrl(I.REQUEST_DOWNLOAD_CONTACT_ALL_LIST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void execute() {
        executeRequest(new GsonRequest<Contact[]>(path,Contact[].class,
                responseDownloadContactListTaskListener(),errorListener()));
    }

    private Response.Listener<Contact[]> responseDownloadContactListTaskListener() {
        return new Response.Listener<Contact[]>() {
            @Override
            public void onResponse(Contact[] response) {
                if (response != null) {
                    ArrayList<Contact> contactList =
                            superwecharApplication.getInstance().getContactList();
                    ArrayList<Contact> list = Utils.array2List(response);
                    contactList.clear();
                    contactList.addAll(list);
                    HashMap<String, Contact> userList =
                            superwecharApplication.getInstance().getUserList();
                    userList.clear();
                    for (Contact c : list) {
                        userList.put(c.getMContactCname(), c);
                    }
                    mContext.sendStickyBroadcast(new Intent("update_contact_list"));

                }
            }
        };
    }


}
