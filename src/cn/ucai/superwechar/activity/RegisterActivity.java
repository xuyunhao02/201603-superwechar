/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.ucai.superwechar.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.easemob.EMError;
import com.easemob.chat.EMChatManager;
import com.easemob.exceptions.EaseMobException;

import java.io.File;

import cn.ucai.superwechar.I;
import cn.ucai.superwechar.R;
import cn.ucai.superwechar.bean.Message;
import cn.ucai.superwechar.data.OkHttpUtils;
import cn.ucai.superwechar.listener.OnSetAvatarListener;
import cn.ucai.superwechar.superwecharApplication;
import cn.ucai.superwechar.utils.ImageUtils;
import cn.ucai.superwechar.utils.Utils;

/**
 * 注册页
 *
 */
public class RegisterActivity extends BaseActivity {
	private final static String TAG = RegisterActivity.class.getName();
	Activity mContext;
	private EditText userNameEditText;
	private EditText userNickEditText;
	private EditText passwordEditText;
	private EditText confirmPwdEditText;
	ImageView mIVAvatar;
	String avatarName;
	String username;
	String pwd;
	String nick;
	OnSetAvatarListener mOnSetAvatarListener;
	final ProgressDialog pd = new ProgressDialog(mContext);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		mContext = this;
		initView();
		setListener();
	}

	private void setListener() {
		onLoginClickListener();
		onSetOnRegisterListener();
		onSetAvatarListener();
	}

	private void onLoginClickListener() {
		findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			mOnSetAvatarListener.setAvatar(requestCode, data, mIVAvatar);
		}
	}

	private String getAvatarName() {
		avatarName=System.currentTimeMillis()+"";
		return avatarName;
	}

	private void onSetAvatarListener() {
		findViewById(R.id.rl_avatar).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mOnSetAvatarListener = new OnSetAvatarListener(mContext,R.id.layout_register,getAvatarName(), I.AVATAR_TYPE_USER_PATH);
			}
		});


	}



	private void initView() {
		userNameEditText = (EditText) findViewById(R.id.username);
		userNickEditText = (EditText) findViewById(R.id.nick);
		passwordEditText = (EditText) findViewById(R.id.password);
		confirmPwdEditText = (EditText) findViewById(R.id.confirm_password);
		mIVAvatar = (ImageView) findViewById(R.id.layout_user_avatar);

	}
	/**
	 * 注册
	 */
	private void onSetOnRegisterListener() {
		findViewById(R.id.btn_register).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				username = userNameEditText.getText().toString().trim();
				nick = userNickEditText.getText().toString().trim();
				pwd = passwordEditText.getText().toString().trim();
				String confirm_pwd = confirmPwdEditText.getText().toString().trim();
				if (TextUtils.isEmpty(username)) {
					userNameEditText.requestFocus();
					userNameEditText.setError(getResources().getString(R.string.User_name_cannot_be_empty));
					return;
				} else if (username.matches("[\\w][\\w\\d_]+")) {
					userNameEditText.requestFocus();
					userNameEditText.setError(getResources().getString(R.string.User_name_cannot_be_wd));
				} else if (TextUtils.isEmpty(nick)) {
					userNickEditText.requestFocus();
					userNickEditText.setError(getResources().getString(R.string.Nick_name_cannot_be_empty));
				} else if (TextUtils.isEmpty(pwd)) {
					passwordEditText.requestFocus();
					passwordEditText.setError(getResources().getString(R.string.Password_cannot_be_empty));
					return;
				} else if (TextUtils.isEmpty(confirm_pwd)) {
					Toast.makeText(mContext, getResources().getString(R.string.Confirm_password_cannot_be_empty), Toast.LENGTH_SHORT).show();
					confirmPwdEditText.requestFocus();
					return;
				} else if (!pwd.equals(confirm_pwd)) {
					Toast.makeText(mContext, getResources().getString(R.string.Two_input_password), Toast.LENGTH_SHORT).show();
					return;
				}

				if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(pwd)) {
					pd.setMessage(getResources().getString(R.string.Is_the_registered));
					pd.show();

					registerAppSever();
				}

			}
		});

	}

	private void registerAppSever() {
		File file = new File(ImageUtils.getAvatarPath(mContext,I.AVATAR_TYPE_USER_PATH),avatarName+I.AVATAR_SUFFIX_JPG);
		OkHttpUtils<Message> utils = new OkHttpUtils<Message>();
		utils.url(superwecharApplication.SERVER_ROOT)
				.addParam(I.KEY_REQUEST,I.REQUEST_REGISTER)
				.addParam(I.User.USER_NAME,username)
				.addParam(I.User.NICK,nick)
				.addParam(I.User.PASSWORD,pwd)
				.targetClass(Message.class)
				.addFile(file)
				.execute(new OkHttpUtils.OnCompleteListener<Message>() {
					@Override
					public void onSuccess(Message result) {
						if (result.isResult()) {
							registerEMServer();
						} else {
							pd.dismiss();
							Utils.showToast(mContext, Utils.getResourceString(mContext,result.getMsg()),Toast.LENGTH_LONG);
							Log.e(TAG, "rehister fail,error:" + result.getMsg());
						}
					}

					@Override
					public void onError(String error) {
						pd.dismiss();
						Utils.showToast(mContext, error, Toast.LENGTH_LONG);
						Log.e(TAG, "register fail.error:" + error);

					}
				});
	}

	//注册环信的账号，并上传头像
	//注册环信的账号
	//如果环信注册失败，调用取消注册的方法，删除远端账号和图片；
	private void registerEMServer() {
		new Thread(new Runnable() {
			public void run() {
				try {
					// 调用sdk注册方法
					EMChatManager.getInstance().createAccountOnServer(username, pwd);
					runOnUiThread(new Runnable() {
						public void run() {
							if (!RegisterActivity.this.isFinishing())
								pd.dismiss();
							// 保存用户名
							superwecharApplication.getInstance().setUserName(username);
							Toast.makeText(getApplicationContext(), getResources().getString(R.string.Registered_successfully), Toast.LENGTH_LONG).show();
							finish();
						}
					});
				} catch (final EaseMobException e) {
					runOnUiThread(new Runnable() {
						public void run() {
							if (!RegisterActivity.this.isFinishing())
								pd.dismiss();
							int errorCode=e.getErrorCode();
							if(errorCode== EMError.NONETWORK_ERROR){
								Toast.makeText(getApplicationContext(), getResources().getString(R.string.network_anomalies), Toast.LENGTH_SHORT).show();
							}else if(errorCode == EMError.USER_ALREADY_EXISTS){
								Toast.makeText(getApplicationContext(), getResources().getString(R.string.User_already_exists), Toast.LENGTH_SHORT).show();
							}else if(errorCode == EMError.UNAUTHORIZED){
								Toast.makeText(getApplicationContext(), getResources().getString(R.string.registration_failed_without_permission), Toast.LENGTH_SHORT).show();
							}else if(errorCode == EMError.ILLEGAL_USER_NAME){
								Toast.makeText(getApplicationContext(), getResources().getString(R.string.illegal_user_name),Toast.LENGTH_SHORT).show();
							}else{
								Toast.makeText(getApplicationContext(), getResources().getString(R.string.Registration_failed) + e.getMessage(), Toast.LENGTH_SHORT).show();
							}
						}
					});
				}
			}
		}).start();

	}

	//未注册成功时；
	private void unRegister() {

	}

	public void back(View view) {
		finish();
	}

}
