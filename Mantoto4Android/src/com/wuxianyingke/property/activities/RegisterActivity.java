package com.wuxianyingke.property.activities;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
/*import cn.smssdk.EventHandler;*/
/*import cn.smssdk.SMSSDK;*/

import com.mantoto.property.R;
import com.umeng.message.PushAgent;
import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.LocalStore;
import com.wuxianyingke.property.common.MD5;
import com.wuxianyingke.property.common.Util;
import com.wuxianyingke.property.remote.RSAEncryptor;
import com.wuxianyingke.property.remote.RemoteApi;
import com.wuxianyingke.property.remote.RemoteApi.NetInfos;
import com.wuxianyingke.property.remote.RemoteApi.User;
import com.wuxianyingke.property.remote.RemoteApiImpl;
import com.wuxianyingke.property.threads.GetPropertyByNameListThread;

public class RegisterActivity extends BaseActivity {
	private Button mRegisterButton;
	private TextView mTopbarTxt;
	private ProgressDialog mWaitLoading = null;
	private String mToActivity = null;
	private String mErrorInfo = "";
	private EditText Register2_edit1, Register2_edit2, Register2_edit3,
			Register2_editcode;
	private ImageButton Register2_edit1_clear_btn, Register2_edit2_clear_btn,
			Register2_edit3_clear_btn;
	private TextView Register2_txt1, Register2_txt2, Register2_txt3;
	private String desc = "";
	private String getCode = "";
	private String menpaihao = "";
	private String username = "";
	private String U_ID = "";
	private String U_PASS = "";
	private TextView topbar_text, topbar_right;
	private Button topbar_left, Register2_GetPhoneCode;
	private boolean flag;
	private int i = 60;
	private String phoneNumber, phoneCode;
	private User retUserInfo = null;
	private RSAEncryptor rsaEncryptor;
	private GetPropertyByNameListThread mByNameThread;
	private long propertyId;
	public Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (mWaitLoading != null) {
				mWaitLoading.dismiss();
				mWaitLoading = null;
			}
			switch (msg.what) {
			// 注册失败
			case 2:
				Toast.makeText(RegisterActivity.this, mErrorInfo,
						Toast.LENGTH_SHORT).show();
				break;

			// 注册成功
			case 3:

				RemoteApi.LoginInfo logininfo = new RemoteApi.LoginInfo();
				logininfo.U_ID = U_ID;
				logininfo.U_PASS = U_PASS;
				logininfo.autoLogin = false;
				LocalStore.setLoginInfo(RegisterActivity.this, logininfo);
				LocalStore.setUserStatus(RegisterActivity.this, true);
				Toast.makeText(RegisterActivity.this, "注册成功",
						Toast.LENGTH_SHORT).show();
				LocalStore.setUserInfo(RegisterActivity.this, retUserInfo);
				Log.i("MyLog", "properid==" + retUserInfo.PropertyID
						+ LocalStore.getUserInfo().PropertyID);
				Intent intent = new Intent();
				intent.setClass(RegisterActivity.this, MainActivity.class);

				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
						| Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				finish();
				break;

			// 通讯错误
			case 4:
				Toast.makeText(RegisterActivity.this, "通信错误，请检查网络或稍后再试。",
						Toast.LENGTH_SHORT).show();
				break;

			case 8:
				Toast.makeText(RegisterActivity.this, desc, Toast.LENGTH_SHORT)
						.show();
				Log.i("MyLog", "response+desc=" + desc);
				break;
			case 9:
				Toast.makeText(RegisterActivity.this, "网络超时，请重新获取",
						Toast.LENGTH_SHORT).show();
				break;
			case Constants.MSG_GET_PRODUCT_LIST_FINISH:
				if (mByNameThread!=null) {
					mByNameThread.getPropertyList();
					propertyId =  mByNameThread.getPropertyList().get(0).PropertyID;
				}else{
					propertyId = 0;
				}
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent(RegisterActivity.this,
					LoginActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
					| Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			finish();
			return true;
		} else
			return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		PushAgent.getInstance(getApplicationContext()).onAppStart();
		setContentView(R.layout.register2);
		setImmerseLayout(findViewById(R.id.IncludeMainTopBarInclude));
		try {
			rsaEncryptor = new RSAEncryptor(getAssets().open(
					"rsa_public_key.pem"), getAssets().open(
					"pkcs8_private_key.pem"));
			String encode = rsaEncryptor.encryptWithBase64("15045412899");
			Log.i("MyLog", "encode=" + encode);
			String decode = rsaEncryptor.decryptWithBase64(encode);
			Log.i("MyLog", "decode=" + decode);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.i("MyLog", e.getMessage());
		}
		mByNameThread = new GetPropertyByNameListThread(RegisterActivity.this,
				mHandler, "未找到小区", 1);
		mByNameThread.start();
		// SMSSDK.initSDK(this, Constants.SMSSDK_APPKEY,
		// Constants.SMSSDK_APPSECRET);
		// 短信验证
		// init();

		Intent intent = this.getIntent();
//		menpaihao = intent.getStringExtra("menpaihao");
//		username = intent.getStringExtra("username");
		initRegisterNormal();
		topbar_left = (Button) findViewById(R.id.topbar_left);
		topbar_left.setVisibility(View.VISIBLE);
		topbar_left.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.setClass(RegisterActivity.this, LoginActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				finish();

			}
		});
		topbar_text = (TextView) findViewById(R.id.topbar_txt);
		topbar_text.setVisibility(View.VISIBLE);
		topbar_text.setText("注册");
		topbar_right = (TextView) findViewById(R.id.topbar_right);
		topbar_right.setVisibility(View.VISIBLE);
		topbar_right.setText("取消");
		topbar_right.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		Register2_edit1.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				String hint;
				if (arg1) {
					hint = Register2_edit1.getHint().toString();
					Register2_edit1.setTag(hint);
					Register2_edit1.setHint("");
				} else {
					hint = Register2_edit1.getTag().toString();
					Register2_edit1.setHint(hint);
				}

			}
		});
		Register2_edit2.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				String hint;
				if (arg1) {
					hint = Register2_edit2.getHint().toString();
					Register2_edit2.setTag(hint);
					Register2_edit2.setHint("");
				} else {
					hint = Register2_edit2.getTag().toString();
					Register2_edit2.setHint(hint);
				}

			}
		});
		Register2_edit3.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				String hint;
				if (arg1) {
					hint = Register2_edit3.getHint().toString();
					Register2_edit3.setTag(hint);
					Register2_edit3.setHint("");
				} else {
					hint = Register2_edit3.getTag().toString();
					Register2_edit3.setHint(hint);
				}

			}
		});
		Register2_editcode
				.setOnFocusChangeListener(new OnFocusChangeListener() {

					@Override
					public void onFocusChange(View arg0, boolean arg1) {
						String hint;
						if (arg1) {
							hint = Register2_editcode.getHint().toString();
							Register2_editcode.setTag(hint);
							Register2_editcode.setHint("");
						} else {
							hint = Register2_editcode.getTag().toString();
							Register2_editcode.setHint(hint);
						}
					}
				});
		/*
		 * // 请求短信验证码 (Mob 短信验证) Register2_GetPhoneCode.setOnClickListener(new
		 * OnClickListener() {
		 * 
		 * @Override public void onClick(View arg0) { if
		 * (Util.isEmpty(Register2_edit1)) {
		 * Toast.makeText(getApplicationContext(), "请输入手机号。",
		 * Toast.LENGTH_LONG).show(); } //
		 * 下面的代码就是调用sdk的发送短信的方法，其中的“86”是官方中定义的，代表中国的意思 // 第二个参数表示的是需要发送短信的手机号
		 * SMSSDK.getVerificationCode("86", Register2_edit1.getText()
		 * .toString()); Log.i("MyLog", "phone=" +
		 * Register2_edit1.getText().toString()); phoneNumber =
		 * Register2_edit1.getText().toString(); // 把按钮变成不可点击，并且显示倒计时（正在获取）
		 * Register2_GetPhoneCode.setClickable(false);
		 * Register2_GetPhoneCode.setText("重新发送(" + i + ")"); new Thread(new
		 * Runnable() {
		 * 
		 * @Override public void run() { for (; i > 0; i--) {
		 * handler.sendEmptyMessage(-9); if (i <= 0) { break; } try {
		 * Thread.sleep(1000); } catch (InterruptedException e) {
		 * e.printStackTrace(); } } handler.sendEmptyMessage(-8); } }).start();
		 * 
		 * } });
		 */

		Register2_GetPhoneCode.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (Util.isEmpty(Register2_edit1)) {
					Toast.makeText(getApplicationContext(), "请输入手机号。",
							Toast.LENGTH_LONG).show();
					
				
				}
				
				//取得输入的电话号码
				phoneNumber = Register2_edit1.getText().toString();
				// 把按钮变成不可点击，并且显示倒计时（正在获取）
				
			//	Register2_GetPhoneCode.setText("重新发送(" + i + ")");
				
				Thread getVerificationCodeThread = new Thread() {
					@Override
					public void run() {
						RemoteApiImpl remote = new RemoteApiImpl();
						NetInfos netInfo;
						try {
							Log.i("MyLog", "rsaEncryptor=code=");
							Log.i("MyLog",
									"rsaEncryptor=code="
											+ rsaEncryptor
													.encryptWithBase64(phoneNumber));
							netInfo = remote.getPhoneCode(
									RegisterActivity.this,
									rsaEncryptor.encryptWithBase64(phoneNumber),
									"mantutu",1);
							Log.i("MyLog",
									"rsaEncryptor=code="
											+ rsaEncryptor
													.encryptWithBase64(phoneNumber));
							if (netInfo == null) {
								handler.sendEmptyMessage(11);
							} else {
								if (netInfo.code == 200) {
									handler.sendEmptyMessage(10);
								} else {
									handler.sendEmptyMessage(11);
								}
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				};

				getVerificationCodeThread.start();

				new Thread(new Runnable() {
					@Override
					public void run() {
						for (; i > 0; i--) {
							handler.sendEmptyMessage(-9);
							if (i <= 0) {
								break;
							}
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						handler.sendEmptyMessage(-8);
					}
				}).start();
			}
		});

	}

	private byte[] InputStreamToByte(InputStream is) throws IOException {
		ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
		int ch;
		while ((ch = is.read()) != -1) {
			bytestream.write(ch);
		}
		byte imgdata[] = bytestream.toByteArray();
		bytestream.close();
		return imgdata;
	}


	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == -9) {
				Register2_GetPhoneCode.setText("重新发送(" + i + ")");
			} else if (msg.what == -8) {
				Register2_GetPhoneCode.setText("获取验证码");
				Register2_GetPhoneCode.setClickable(true);
				i = 60;
			} else if (msg.what == 10) {
				Toast.makeText(getApplicationContext(), "验证码已经发送",
						Toast.LENGTH_SHORT).show();
			} else if (msg.what == 11) {
				Toast.makeText(getApplicationContext(), "验证码发送失败",
						Toast.LENGTH_SHORT).show();
			} else if (msg.what == 12) {
				Toast.makeText(getApplicationContext(), "验证码错误",
						Toast.LENGTH_SHORT).show();
			}/*
			 * else { int event = msg.arg1; int result = msg.arg2; Object data =
			 * msg.obj; Log.e("event", "event=" + event); if (result ==
			 * SMSSDK.RESULT_COMPLETE) { // 短信注册成功后，返回MainActivity,然后提示 if
			 * (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {// 提交验证码成功
			 * 
			 * startRegistrNormal();
			 * 
			 * Intent intent = new Intent(RegisterActivity.this,
			 * Radio1Activity.class);
			 * intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			 * startActivity(intent); finish();
			 * 
			 * } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
			 * Toast.makeText(getApplicationContext(), "验证码已经发送",
			 * Toast.LENGTH_SHORT).show(); } else { ((Throwable)
			 * data).printStackTrace(); // int resId =
			 * getStringRes(RegisterActivity.this, // "smssdk_network_error");
			 * Toast.makeText(RegisterActivity.this, "验证码错误",
			 * Toast.LENGTH_SHORT).show(); // if (resId > 0) { //
			 * Toast.makeText(RegisterActivity.this, resId, //
			 * Toast.LENGTH_SHORT).show(); // } } } }
			 */
		}
	};

	// 普通注册初始化
	private void initRegisterNormal() {
		Register2_edit1 = (EditText) findViewById(R.id.Register2_edit1);
		Register2_edit1.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				// if(arg0.length()>0)
				// {
				// Register2_edit1_clear_btn.setVisibility(View.VISIBLE);
				// }
				// else
				// {
				// Register2_edit1_clear_btn.setVisibility(View.GONE);
				// }
			}
		});

		Register2_edit1_clear_btn = (ImageButton) findViewById(R.id.Register2_edit1_clear_btn);
		// if(Register2_edit1.getText().toString().equals(""))
		// {
		// Register2_edit1_clear_btn.setVisibility(View.GONE);
		// }
		// else
		// {
		// // Register2_edit1_clear_btn.setVisibility(View.VISIBLE);
		// }
		Register2_edit1_clear_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Register2_edit1.setText("");
			}
		});

		Register2_edit2 = (EditText) findViewById(R.id.Register2_edit2);
		Register2_edit2.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				if (arg0.length() > 0) {
					// Register2_edit2_clear_btn.setVisibility(View.VISIBLE);
				} else {
					// Register2_edit2_clear_btn.setVisibility(View.GONE);
				}
			}
		});

		Register2_edit2_clear_btn = (ImageButton) findViewById(R.id.Register2_edit2_clear_btn);
		if (Register2_edit2.getText().toString().equals(""))
			// {
			// Register2_edit2_clear_btn.setVisibility(View.GONE);
			// }
			// else
			// {
			// Register2_edit2_clear_btn.setVisibility(View.VISIBLE);
			// }
			Register2_edit2_clear_btn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Register2_edit2.setText("");
				}
			});

		Register2_edit3 = (EditText) findViewById(R.id.Register2_edit3);
		Register2_edit3.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				if (arg0.length() > 0) {
					// Register2_edit3_clear_btn.setVisibility(View.VISIBLE);
				} else {
					Register2_edit3_clear_btn.setVisibility(View.GONE);
				}
			}
		});

		Register2_edit3_clear_btn = (ImageButton) findViewById(R.id.Register2_edit3_clear_btn);
		if (Register2_edit3.getText().toString().equals("")) {
			Register2_edit3_clear_btn.setVisibility(View.GONE);
		} else {
			// Register2_edit3_clear_btn.setVisibility(View.VISIBLE);
		}
		Register2_edit3_clear_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Register2_edit3.setText("");
			}
		});

		Register2_txt1 = (TextView) findViewById(R.id.Register2_txt1);
		Register2_txt2 = (TextView) findViewById(R.id.Register2_txt2);
		Register2_txt3 = (TextView) findViewById(R.id.Register2_txt3);
		Register2_editcode = (EditText) findViewById(R.id.Register2_editCode);
		Register2_GetPhoneCode = (Button) findViewById(R.id.Register2_Button_GetPhoneCode);

		regTip(false, true, false);
		// mUserNameEditText = (EditText) findViewById(R.id.UserNameEditText);
		// mPasswordEditText = (EditText) findViewById(R.id.PasswordEditText);
		// mRegPasswordEditText = (EditText)
		// findViewById(R.id.RegPasswordEditText);
		Button mRegisterButton = (Button) findViewById(R.id.Register2_btn);
		mRegisterButton.setOnClickListener(mRegNormalListener);
	}

	// MOB短信验证
	/*
	 * // 普通注册按钮监听 private final OnClickListener mRegNormalListener = new
	 * OnClickListener() {
	 * 
	 * @Override public void onClick(View v) { if
	 * (!TextUtils.isEmpty(Register2_editcode.getText().toString())) {
	 * SMSSDK.submitVerificationCode("86", phoneNumber,
	 * Register2_editcode.getText().toString()); } else {
	 * Toast.makeText(RegisterActivity.this, "验证码不能为空", 1).show(); }
	 * 
	 * Log.i("MyLog", "phonenumber=" + phoneNumber + "---code=" +
	 * Register2_editcode.getText().toString()); // startRegistrNormal(); } };
	 */

	// 普通注册按钮监听
	private final OnClickListener mRegNormalListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (!TextUtils.isEmpty(Register2_editcode.getText().toString())) {
				Thread getVerifyCodeThread = new Thread() {
					@Override
					public void run() {
						RemoteApiImpl remote = new RemoteApiImpl();
						NetInfos netInfo = remote.getVarificationCode(
								RegisterActivity.this, phoneNumber,
								Register2_editcode.getText().toString());
						if (netInfo.bSuccess) {
							Log.i("MyLog", "验证成功");
							startRegistrNormal();
						} else {
							handler.sendEmptyMessage(12);
						}
					}
				};
				getVerifyCodeThread.start();
			} else {
				Toast.makeText(RegisterActivity.this, "验证码不能为空", Toast.LENGTH_SHORT).show();
			}

			Log.i("MyLog", "phonenumber=" + phoneNumber + "---code="
					+ Register2_editcode.getText().toString());
			// startRegistrNormal();
		}
	};

	// 普通注册，原方法，未修改
	private void startRegistrNormal() {
		if (!inputIsLegalNormal()) {
			return;
		}
		/*
		 * mWaitLoading = ProgressDialog.show(RegisterActivity.this, null,
		 * "注册中，请稍候......", true);
		 */
		U_ID = Register2_edit1.getText().toString();
		U_PASS = Register2_edit2.getText().toString();
		Thread registerThread = new Thread() {
			public void run() {
				SharedPreferences saving = RegisterActivity.this
						.getSharedPreferences(LocalStore.USER_INFO, 0);
				RemoteApiImpl remote = new RemoteApiImpl();
				retUserInfo = remote.userRegister(RegisterActivity.this,
						"", "", Register2_edit1.getText()
								.toString(), MD5.toMD5(Register2_edit2
								.getText().toString()), propertyId);
				LocalStore.setUserInfo(RegisterActivity.this, retUserInfo);
				Message msg = new Message();
				if (retUserInfo == null) {
					msg.what = 4;
				} else if (200 == retUserInfo.netInfo.code) {
					msg.what = 3;
					LocalStore.setUserInfo(RegisterActivity.this, retUserInfo);
				} else {
					msg.what = 2;
					mErrorInfo = retUserInfo.netInfo.desc;
					Log.i("MyLog", "mErrorInfo=" + mErrorInfo);
				}
				mHandler.sendMessage(msg);
			}
		};
		registerThread.start();
	}

	// 普通注册输入判断，未修改
	private boolean inputIsLegalNormal() {
		if (Util.isEmpty(Register2_edit1)) {
			Toast.makeText(RegisterActivity.this,
					R.string.error_username_cannot_be_empty, Toast.LENGTH_SHORT)
					.show();
			return false;
		} else if (!Util
				.isMobileNO(Register2_edit1.getText().toString().trim())) {
			regTip(false, true, false);
			Toast.makeText(RegisterActivity.this, "用户名输入不合法",
					Toast.LENGTH_SHORT).show();
			return false;
		} else if (Util.isEmpty(Register2_edit2)) {
			regTip(true, false, false);
			Toast.makeText(RegisterActivity.this,
					R.string.error_password_cannot_be_empty, Toast.LENGTH_SHORT)
					.show();
			return false;
		} else if (!Register2_edit2.getText().toString()
				.equals(Register2_edit3.getText().toString())) {
			regTip(false, false, true);
			Toast.makeText(RegisterActivity.this,
					R.string.error_two_password_not_same, Toast.LENGTH_SHORT)
					.show();
			return false;
		} else {
			String password1 = Register2_edit2.getText().toString();
			Pattern pat = Pattern.compile("^[a-z|0-9|A-Z]+$");
			Matcher isOk = pat.matcher(password1);
			if (!isOk.matches() || password1.length() < 6
					|| password1.length() > 16) {
				Toast.makeText(RegisterActivity.this,
						R.string.warn_bad_format, Toast.LENGTH_SHORT).show();
				return false;
			} else {
				return true;
			}
		}

	}

	private void resetTip() {
		Register2_txt1.setText("");
		Register2_txt2.setText("");
		Register2_txt3.setText("");
	}

	private void regTip(boolean tip1, boolean tip2, boolean tip3) {
		resetTip();
		if (tip1)
			Register2_txt1.setText(R.string.Register2_tip1);
		if (tip2)
			Register2_txt2.setText(R.string.Register2_tip2);
		if (tip3)
			Register2_txt3.setText(R.string.Register2_tip3);
	}

	private void goToActivity() {
		if (mToActivity == null) {
			return;
		}

		if (mToActivity.equals("OrderCarActivity")) {
			// Intent intent = new Intent(LoginActivity.this,
			// OrderCarActivity.class);
			// startActivity(intent);
		}
	}

	@Override
	protected void onDestroy() {
		// SMSSDK.unregisterAllEventHandler();
		super.onDestroy();
	}

}
