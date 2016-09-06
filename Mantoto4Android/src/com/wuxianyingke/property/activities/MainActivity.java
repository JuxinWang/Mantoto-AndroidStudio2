package com.wuxianyingke.property.activities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.mantoto.property.R;
import com.umeng.message.PushAgent;
import com.wuxianyingke.gaode.RouteActivity;
import com.wuxianyingke.property.PropertyApplication;
import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.LocalStore;
import com.wuxianyingke.property.common.LogUtil;
import com.wuxianyingke.property.common.UpdateManger;
import com.wuxianyingke.property.remote.RemoteApi;
import com.wuxianyingke.property.remote.RemoteApi.FreeWifi;
import com.wuxianyingke.property.remote.RemoteApi.MenuList;
import com.wuxianyingke.property.remote.RemoteApi.UpdateInfo;
import com.wuxianyingke.property.remote.RemoteApi.WIFISSID;
import com.wuxianyingke.property.remote.RemoteApi.WeatherInfo;
import com.wuxianyingke.property.remote.RemoteApiImpl;
import com.wuxianyingke.property.service.LocationService;
import com.wuxianyingke.property.service.SendLocation;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends BaseActivity {
	
	static final String TAG = "MainActivity";
	private GridView mGridView; // MyGridView
	private Button mBohaoLinearLayout;
	private static SendLocation info = new SendLocation();
	private ProgressDialog mWaitLoading = null;
	private TextView DateTextView, Date_day_TextView, Date_weekday_TextView,
			DiZhiTextView, WenduTextView, TianQiTextView;
	private ImageView TianqiImageView, LuKuangImage;
	//public LocationActivity.MyLocationListenner myListener = new LocationActivity.MyLocationListenner();
	// 定义跳转activity集合
	private Map<String, Object> activityRes;
	private List<Object> propertyActivityRes;
	// 定义图标集合
	private Map<String, String> imageRes;

	// 定义标题名字数组（通知、代收快递、跳蚤市场、购物、医疗、常用信息、生活便签、报修）
	private int[] itemTags = { R.string.gridview_tongzhi,
			R.string.gridview_daishoukuaidi,
			R.string.gridview_tiaozhaoshichang, R.string.gridview_canyin,
			R.string.gridview_gouwu, R.string.gridview_jiaotong,
			R.string.gridview_yiliao, R.string.gridview_shenghuofuwu,
			R.string.gridview_changyongxinxi, R.string.gridview_youchangfuwu,
			R.string.gridview_shenghuobianqian, R.string.gridview_wifi,
			R.string.gridview_baoxiu, R.string.gridview_shenghuojiaofei };

	private Map<String, Object> menuMap = null;
	private FreeWifi wifiInfo;
	private long exitTimeMillis = System.currentTimeMillis();
	private String[] ssid = null;
	private String desc;//?????
	private UpdateInfo updateInfo;

	private LocationService locationService;
	private ImageView updateRemindImg;
	
	
	 
	private int propertyid;// 小区id
	private ArrayList<MenuList> menuList = new ArrayList<MenuList>();// 首页菜单列表,小格子列表

	
	private Handler mHandler = new Handler() {
		
				@Override
				public void handleMessage(Message msg) {
					LogUtil.d(TAG, "PushHandler handleMessage : " + msg.what);
		
					switch (msg.what) {
					case Constants.MSG_LOCATION_READY: {//用户地理位置定位
						SendLocation obj = (SendLocation) msg.obj;
						LogUtil.d(TAG, "MSG_LOCATION_READY");
		
						LogUtil.d(TAG, "obj.city=" + obj.city);
						LogUtil.d(TAG, "obj.longitude=" + obj.longitude);
						LogUtil.d(TAG, "obj.latitude=" + obj.latitude);
						// 加载地址 location和region, 需要本地的 region是城市
						// http://api.map.baidu.com/place/search?query=公交站&location=%@&coord_type=wgs84&radius=1000&region=%@&output=html&src=wuxianying|propertyManager
						String city = obj.city;
						String location = obj.latitude + "," + obj.longitude;
						String mapUrl = "http://api.map.baidu.com/place/search?query=医院&location="
								+ location
								+ "&coord_type=wgs84&radius=1000&output=html&src=wuxianying|propertyManager";
						LogUtil.d(TAG, "obj.mapUrl=" + mapUrl);
						Intent intent = new Intent();
						intent.setData(Uri.parse(mapUrl));
						intent.setAction(Intent.ACTION_VIEW);
						MainActivity.this.startActivity(intent); // 启动浏览器
					}
						break;
					case 3:
						//初始化标题布局
						initTopBar();
						break;
					case 4:
						//初始化活动页面activity,一个图标对应一个条目
						propertyActivityRes.clear();
						List<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
						int length = menuList.size();
						for (int i = 0; i < length; i++) {
							Log.i("MyLog", "获得所需菜单文件为-------------------》" + menuList);
							HashMap<String, Object> map = new HashMap<String, Object>();
							for (int j = 0; j < itemTags.length; j++) {//最大为标题元数个数
								if (getResources().getText(itemTags[j]).equals(
										menuList.get(i).Tag)) {
									map.put("ItemImageView",
											imageRes.get(menuList.get(i).Tag));
									map.put("ItemTextView", menuList.get(i).MenuName);
									propertyActivityRes.add(activityRes.get(menuList
											.get(i).Tag));
									Log.i("MyLog",
											"新的数据源为---------PropertyActivityRes-----------="
													+ menuList.get(i).Tag);
									data.add(map);
									Log.i("MyLog", "新的数据源为--------------------=" + data);
								}
							}
						}
						initactivityRes();
						/*
						 * HashMap<String, Object> map2 = new HashMap<String, Object>();
						 * map2.put("ItemImageView", R.drawable.style_repair);
						 * map2.put("ItemTextView", "报修"); propertyActivityRes.add(
						 * activityRes.get("报修")); data.add(map2);
						 * 
						 * HashMap<String, Object> map3= new HashMap<String, Object>();
						 * map3.put("ItemImageView", R.drawable.style_shenghuojiaofei);
						 * map3.put("ItemTextView", "生活缴费"); propertyActivityRes.add(
						 * activityRes.get("生活缴费")); data.add(map3);
						 */
		
						Log.i("MyLog", "新的数据源为--------------------=" + data);
						// 为itme.xml添加适配器，为每一个小格子填充数据
						SimpleAdapter simpleAdapter = new SimpleAdapter(
								MainActivity.this, data, R.layout.gridview_item,
								new String[] { "ItemImageView", "ItemTextView" },
								new int[] { R.id.ItemImageView, R.id.ItemTextView });
		
						mGridView.setAdapter(simpleAdapter);
						break;
					case 5:
						Toast.makeText(getApplicationContext(), "请查看网络连接是否正常",Toast.LENGTH_SHORT)
								.show();
						break;
					case 6:
		
						break;
					case Constants.MSG_GET_INDEX_INFO_NET_ERROR:
						/*无网络*/
						if (mWaitLoading != null && mWaitLoading.isShowing())
							mWaitLoading.dismiss();//取消
		
						View view = (View) findViewById(R.id.Gridview_network_error);
						view.setVisibility(View.VISIBLE);
						break;
					case 9:
						if (getVersionCode() < updateInfo.versionCode) {
						//	updateRemindImg.setVisibility(View.VISIBLE);//????
						}
						if (LocalStore.getIsUpload(getApplicationContext())) {
							UpdateManger updateManger = new UpdateManger(
									MainActivity.this, updateInfo.url,
									updateInfo.updateInfo, updateInfo.versionCode,
									updateInfo.appversion);
							updateManger.checkUpdate();
						}
		
						break;
					case 10:
						Toast.makeText(getApplicationContext(), desc, Toast.LENGTH_SHORT).show();
						break;
					}
				}
	};

	
	/*初始化标题栏布局：日期、天气等*/
	public void initTopBar() {
		DateTextView = (TextView) findViewById(R.id.DateTextView);
		Date_day_TextView = (TextView) findViewById(R.id.Date_day_TextView);
		Date_weekday_TextView = (TextView) findViewById(R.id.Date_weekday_TextView);
		DiZhiTextView = (TextView) findViewById(R.id.DiZhiTextView);
		WenduTextView = (TextView) findViewById(R.id.WenduTextView);
		TianQiTextView = (TextView) findViewById(R.id.TianQiTextView);
		TianqiImageView = (ImageView) findViewById(R.id.TianqiImageView);

		final Calendar c = Calendar.getInstance();
		c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
		String mYear = String.valueOf(c.get(Calendar.YEAR)); // 获取当前年份
		String mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);// 获取当前月份
		String mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));// 获取当前月份的日期号码
		String mWay = String.valueOf(c.get(Calendar.DAY_OF_WEEK));
		if ("1".equals(mWay)) {
			mWay = "日";
		} else if ("2".equals(mWay)) {
			mWay = "一";
		} else if ("3".equals(mWay)) {
			mWay = "二";
		} else if ("4".equals(mWay)) {
			mWay = "三";
		} else if ("5".equals(mWay)) {
			mWay = "四";
		} else if ("6".equals(mWay)) {
			mWay = "五";
		} else if ("7".equals(mWay)) {
			mWay = "六";
		}

		DateTextView.setText(mYear + "." + mMonth+".");
		Date_day_TextView.setText(mDay);
		Date_weekday_TextView.setText("星期" + mWay);
		DiZhiTextView.setText(LocalStore.cityInfo.city_name);
		WenduTextView.setText(LocalStore.weatherInfo.temp1);
		TianQiTextView.setText(LocalStore.weatherInfo.fl1);

		int rid = R.drawable.qing;

		if (LocalStore.weatherInfo.img_title_single.equals("特大暴雨")) {
			rid = R.drawable.baoyu;
		} else if (LocalStore.weatherInfo.img_title_single.equals("大暴雨")) {
			rid = R.drawable.baoyu;
		} else if (LocalStore.weatherInfo.img_title_single.equals("暴雨")) {
			rid = R.drawable.baoyu;
		} else if (LocalStore.weatherInfo.img_title_single.equals("大雨")) {
			rid = R.drawable.baoyu;
		} else if (LocalStore.weatherInfo.img_title_single.equals("中雨")) {
			rid = R.drawable.baoyu;
		} else if (LocalStore.weatherInfo.img_title_single.equals("小雨")) {
			rid = R.drawable.xiaoyu;
		} else if (LocalStore.weatherInfo.img_title_single.equals("雷阵雨")) {
			rid = R.drawable.leizhenyu;
		} else if (LocalStore.weatherInfo.img_title_single.equals("阵雨")) {
			rid = R.drawable.zhenyu;
		} else if (LocalStore.weatherInfo.img_title_single.equals("冻雨")) {
			rid = R.drawable.yujiaxue;
		} else if (LocalStore.weatherInfo.img_title_single.equals("雨夹雪")) {
			rid = R.drawable.yujiaxue;
		} else if (LocalStore.weatherInfo.img_title_single.equals("暴雪")) {
			rid = R.drawable.baoxue;
		} else if (LocalStore.weatherInfo.img_title_single.equals("阵雪")) {
			rid = R.drawable.zhenxue;
		} else if (LocalStore.weatherInfo.img_title_single.equals("大雪")) {
			rid = R.drawable.zhenxue;
		} else if (LocalStore.weatherInfo.img_title_single.equals("中雪")) {
			rid = R.drawable.zhongxue;
		} else if (LocalStore.weatherInfo.img_title_single.equals("小雪")) {
			rid = R.drawable.xiaoxue;
		} else if (LocalStore.weatherInfo.img_title_single.equals("霾")) {
			rid = R.drawable.wumai;
		} else if (LocalStore.weatherInfo.img_title_single.equals("强沙尘暴")) {
			rid = R.drawable.qiangshacenbao;
		} else if (LocalStore.weatherInfo.img_title_single.equals("沙尘暴")) {
			rid = R.drawable.shachenbao;
		} else if (LocalStore.weatherInfo.img_title_single.equals("扬沙")) {
			rid = R.drawable.yangsha;
		} else if (LocalStore.weatherInfo.img_title_single.equals("浮尘")) {
			rid = R.drawable.yangsha;
		} else if (LocalStore.weatherInfo.img_title_single.equals("雾")) {
			rid = R.drawable.zhongwu;
		} else if (LocalStore.weatherInfo.img_title_single.equals("阴")) {
			rid = R.drawable.yin;
		} else if (LocalStore.weatherInfo.img_title_single.equals("多云")) {
			rid = R.drawable.yin;
		} else if (LocalStore.weatherInfo.img_title_single.equals("晴")) {
			rid = R.drawable.qing;
		}
		TianqiImageView.setImageResource(rid);
	}

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
				super.onCreate(savedInstanceState);
				// UmengUpdateAgent.update(this);
				PushAgent.getInstance(getApplicationContext()).onAppStart();
				// this.requestWindowFeature(Window.FEATURE_NO_TITLE);
				// 设置Activity标题不显示
				// this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				// WindowManager.LayoutParams.FLAG_FULLSCREEN); // 设置全屏显示
				Log.d("MyTag", "getString(R.string.gridview_tongzhi)"
						+ getResources().getString(R.string.about));
				// StatusBarCompat.compat(this,
				// getResources().getColor(R.color.status_bar_color));
				
				//wifi实现
				Thread wifiInfoThread = new Thread() {
					public void run() {
						RemoteApiImpl remote = new RemoteApiImpl();
						wifiInfo = remote.getFreeWifiInfo(getApplicationContext(),
								LocalStore.getUserInfo().userId);
						Message msg = new Message();
						if (wifiInfo == null) {
		
						} else {
							msg.what = 6;
							Log.i("MyLog", "wifiUser=" + wifiInfo.WIFIAccount);
							LocalStore.freeWifi.UserID = wifiInfo.UserID;
							LocalStore.freeWifi.WIFIAccount = wifiInfo.WIFIAccount;
							LocalStore.freeWifi.WIFIPwd = wifiInfo.WIFIPwd;
							LocalStore.freeWifi.WIFIUserID = wifiInfo.WIFIUserID;
							LocalStore.saveFreeWifi(MainActivity.this);
						}
						mHandler.sendMessage(msg);
					}
				};
				wifiInfoThread.start();
		
				//WIFI  SSID 列表 
				Thread GetWifiSSIDThread = new Thread() {
					@Override
					public void run() {
						RemoteApiImpl remote = new RemoteApiImpl();
						
						WIFISSID wifiSSID = remote.getWifiSSID(getApplicationContext());
						Message msg = new Message();
						if (wifiSSID == null) {
							msg.what = 9;
							return;
						} else {
							LocalStore.setWifiApUrl(MainActivity.this, wifiSSID.wifiap);
							ssid = new String[wifiSSID.ssidList.size()];
							for (int i = 0; i < wifiSSID.ssidList.size(); i++) {
								ssid[i] = wifiSSID.ssidList.get(i).SSID;
								Log.i("MyLog", "wifissidlist=" + ssid[i] + "wifiUrl = "
										+ LocalStore.getWifiApUrl(MainActivity.this));
							}
						}
					}
				};
				GetWifiSSIDThread.start();
				
				//自动更新
				Thread getUpdateThread = new Thread() {
					@Override
					public void run() {
		
						RemoteApiImpl remoteApiImpl = new RemoteApiImpl();
						updateInfo = remoteApiImpl.getUpdateInfo(
								getApplicationContext(), "mantoto");
						Message msg = new Message();
						if (updateInfo == null) {
							msg.what = 5;
						} else {
							if (updateInfo.netInfo.code == 200) {
								msg.what = 9;
							} else {
								desc = updateInfo.netInfo.desc;
								msg.what = 10;
							}
						}
						mHandler.sendMessage(msg);
					}
				};
				getUpdateThread.start();
				
				// 初始化格子icon图标
				initImageRes();
				//初始化activity
				initactivityRes();
				
				propertyActivityRes = new ArrayList<Object>();
		
				setContentView(R.layout.gridview_main);
				super.init(0);

				//updateRemindImg = (ImageView) findViewById(R.id.updateRemindImg);
				mGridView = (GridView) findViewById(R.id.MyGridView);
				mGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
				mGridView.setNumColumns(4);
				
				setImmerseLayout(findViewById(R.id.view));
				
				
				// 获得首页格子菜单列表
				SharedPreferences saving = this.getSharedPreferences(
						LocalStore.USER_INFO, 0);
				propertyid = LocalStore.getUserInfo().PropertyID;
				Log.i("MyLog", "MainActivity---》小区idpropertyid" + propertyid);
				
				getMenuList();
				
				initWidget();

			//获取天气信息
				Thread registerThread = new Thread() {
					public void run() {
						RemoteApiImpl remote = new RemoteApiImpl();
						WeatherInfo netInfo = remote.getWeather(MainActivity.this,
								LocalStore.getCityInfo().city_name);
		
						Log.i("MyLog", "获取的地址为:" + LocalStore.getCityInfo().city_name);
		
						Message msg = new Message();
						if (netInfo == null) {
		
						} else {
							LocalStore.weatherInfo.fl1 = netInfo.fl1;
							LocalStore.weatherInfo.temp1 = netInfo.temp1;
							LocalStore.weatherInfo.img_title_single = netInfo.img_title_single;
							LocalStore.weatherInfo.wind1 = netInfo.wind1;
							LocalStore.saveWeatherInfo(MainActivity.this);
							msg.what = 3;
						}
						mHandler.sendMessage(msg);
					}
				};
				registerThread.start();
				initTopBar();
	}

	//初始化小格子图标
	private void initImageRes() {
				imageRes = new HashMap<String, String>();
				
				imageRes.put(
						(String) getResources().getText(R.string.gridview_tongzhi), ""
								+ R.drawable.style_tongzhi);//通知
				
				/*imageRes.put((String) getResources().getText(R.string.gridview_xinxi),
						"" + R.drawable.style_xinxi);//没有
				imageRes.put(
						(String) getResources().getText(
								R.string.gridview_tiaozhaoshichang), ""
								+ R.drawable.style_tiaozhaoshichang);//没有
				imageRes.put(
						(String) getResources()
								.getText(R.string.gridview_daishoukuaidi), ""
								+ R.drawable.style_daishoukuaidi);//没有*/
				imageRes.put((String) getResources().getText(R.string.gridview_canyin),
						"" + R.drawable.style_canyin);//餐饮
				imageRes.put((String) getResources().getText(R.string.gridview_gouwu),
						"" + R.drawable.style_gouwu);//购物
				imageRes.put((String) getResources()
						.getText(R.string.gridview_jiaotong), ""
						+ R.drawable.style_jiaotong);//交通
				imageRes.put((String) getResources().getText(R.string.gridview_yiliao),
						"" + R.drawable.style_yiliao);//医疗
				imageRes.put(
						(String) getResources().getText(R.string.gridview_shenghuofuwu),
						"" + R.drawable.style_shenghuofuwu);//生活服务
				imageRes.put(
						(String) getResources().getText(
								R.string.gridview_shenghuobianqian), ""
								+ R.drawable.style_shenghuobianqian);//生活便签
				/*imageRes.put(
						(String) getResources().getText(
								R.string.gridview_changyongxinxi), ""
								+ R.drawable.style_changyongxinxi);//没有
				imageRes.put(
						(String) getResources().getText(R.string.gridview_youchangfuwu),
						"" + R.drawable.style_youchangfuwu);//没有*/
				imageRes.put((String) getResources().getText(R.string.gridview_wifi),
						"" + R.drawable.style_wifi);//wifi
				/*imageRes.put((String) getResources().getText(R.string.gridview_baoxiu),
						"" + R.drawable.style_repair);//没有
				imageRes.put(
						(String) getResources().getText(
								R.string.gridview_shenghuojiaofei), ""
								+ R.drawable.style_shenghuojiaofei);//没有*/
	}
	
	
	
	//初始化activity即每个小格子对应的页面
	private void initactivityRes() {
				activityRes = new HashMap<String, Object>();
				activityRes.put(
						(String) getResources().getText(R.string.gridview_tongzhi),
						InformActivity.class);//物业通知
				/*activityRes.put((String) getResources()
						.getText(R.string.gridview_xinxi), MessageActivity.class);*/

				activityRes.put(
						(String) getResources().getText(R.string.gridview_canyin),
						CanYinListActivity.class);
				activityRes.put((String) getResources()
						.getText(R.string.gridview_gouwu), GouWuListActivity.class);
				activityRes.put(
						(String) getResources().getText(R.string.gridview_jiaotong),
						RouteActivity.class);
				activityRes.put(
						(String) getResources().getText(R.string.gridview_yiliao),
						YiLiaoActivity.class);
				activityRes
						.put((String) getResources().getText(
								R.string.gridview_shenghuofuwu),
								ShengHuoFuWuListActivity.class);
				activityRes.put(
						(String) getResources().getText(
								R.string.gridview_shenghuobianqian),
						StickerActivity.class);//生活便签

				activityRes.put(
						(String) getResources().getText(R.string.gridview_wifi),
						WIFILoginActivity.class);

	}

	// 获得首页小格子列表即菜单列表
	private void getMenuList() {
			Thread getMenuListThread = new Thread() {
				@Override
				public void run() {
					RemoteApiImpl remoteApi = new RemoteApiImpl();
					Log.i("MyLog", "propertyid---------------------->" + propertyid);
					menuList = remoteApi.getHomeMenu(MainActivity.this,
							LocalStore.getUserInfo().PropertyID);
					Message msg = new Message();
	
					if (menuList != null) {
						msg.what = 4;//与上面的case联系起来了
					} else {
						msg.what = Constants.MSG_GET_INDEX_INFO_NET_ERROR;
					}
					mHandler.sendMessage(msg);
				}
			};
			getMenuListThread.start();
	}

	//初始化拨号、首页、用户中心
	private void initWidget() {
					mBohaoLinearLayout = (Button) findViewById(R.id.BohaoLinearLayout);
					//拨号
					mBohaoLinearLayout.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {

							showPopwindow();

						}
			});

			// 为mGridView添加点击事件监听器
			mGridView.setOnItemClickListener(new GridViewItemOnClick());

			/*LuKuangImage = (ImageView) findViewById(R.id.LuKuangImage);
			LuKuangImage.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent();
					intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
							| Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.setClass(MainActivity.this, lukuangActivity.class);
					startActivity(intent);
				}
			});*/
	}

	//方法:拨号弹出框Popwindow
	private void showPopwindow() {
		// 利用layoutInflater获得View
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.popupwindow,null);
		// 下面是两种方法得到宽度和高度 getWindow().getDecorView().getWidth()
		final PopupWindow window = new PopupWindow(view,
				WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.WRAP_CONTENT);
		// 设置popWindow弹出窗体可点击，这句话必须添加，并且是true
		 window.setFocusable(true);
		// 实例化一个ColorDrawable颜色为半透明
		ColorDrawable dw = new ColorDrawable(0xb0000000);
		window.setBackgroundDrawable(dw);
		// 设置popWindow的显示和消失动画
		window.setAnimationStyle(R.style.mypopwindow_anim_style);
		// 在底部显示
		window.showAtLocation(MainActivity.this.findViewById(R.id.BohaoLinearLayout),
				Gravity.BOTTOM,0,0);
		// 这里检验popWindow里的button是否可以点击
		Button callopen = (Button) view.findViewById(R.id.call_open);
		Button callcancle = (Button) view.findViewById(R.id.call_cancle);
		callopen.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(getApplicationContext(),"拨号",Toast.LENGTH_SHORT);
				Uri uri = Uri.parse("tel:" + LocalStore.getUserInfo().phone);
				Log.i("MyLog", "MainActivity  tel="
						+ LocalStore.getUserInfo().phone + " number="
						+ LocalStore.getUserInfo().telnumber);
				Intent it = new Intent(Intent.ACTION_DIAL, uri);
				startActivity(it);
			}
		});
		callcancle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(getApplicationContext(),"取消",Toast.LENGTH_SHORT);
				window.dismiss();

			}
		});
		//popWindow消失监听方法
		window.setOnDismissListener(new PopupWindow.OnDismissListener() {
			@Override
			public void onDismiss() {
				Toast.makeText(getApplicationContext(),"消失",Toast.LENGTH_SHORT);
			}
		});

	}

	//拨号的弹出菜单事件popmenu
	/*private  void showPoupMenu(View view){
		// View当前PopupMenu显示的相对View的位置
		PopupMenu popupMenu = new PopupMenu(this,view);
		// menu布局
		popupMenu.getMenuInflater().inflate(R.menu.main,popupMenu.getMenu());

		// menu的item点击事件
		popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				//Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
				switch (item.getItemId()){
					case R.id.call_open:
						Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
						Uri uri = Uri.parse("tel:" + LocalStore.getUserInfo().phone);
						Log.i("MyLog", "MainActivity  tel="
								+ LocalStore.getUserInfo().phone + " number="
								+ LocalStore.getUserInfo().telnumber);
						Intent it = new Intent(Intent.ACTION_DIAL, uri);
						startActivity(it);
						break;
					case R.id.call_cancle:
						Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
				}
				return false;
			}
		});
		// PopupMenu关闭事件
		popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
			@Override
			public void onDismiss(PopupMenu menu) {
				//Toast.makeText(getApplicationContext(), "关闭PopupMenu", Toast.LENGTH_SHORT).show();
			}
		});

		popupMenu.show();
	}*/

	// 定义点击事件监听器
	public class GridViewItemOnClick implements OnItemClickListener {
				@Override
				public void onItemClick(AdapterView<?> arg0, View view, int position,
						long arg3) {
					Intent intent = new Intent();
		
					String tag = "view";
					intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
							| Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.setClass(MainActivity.this,
							(Class<?>) propertyActivityRes.get(position));
					if (propertyActivityRes
							.get(position)
							.toString()
							.equals("class com.wuxianyingke.property.activities.WIFILoginActivity")) {
						Bundle bundle = new Bundle();
						bundle.putStringArray("ssid", ssid);
						intent.putExtras(bundle);
						/*
						 * if (wifiInfo==null) { Toast.makeText(getApplicationContext(),
						 * "该用户未注册开通WIFI账户", Toast.LENGTH_LONG).show(); }else
						 * if(LocalStore.getUserInfo().userId == 0){
						 * Toast.makeText(getApplicationContext(), "请登录。。。",
						 * Toast.LENGTH_SHORT).show(); remindDialog(); }
						 */
					}
					Log.i("MyLog",
							"propertyActivityRes==" + propertyActivityRes.get(position));
					if (wifiInfo == null
							&& propertyActivityRes
									.get(position)
									.toString()
									.equals("class com.wuxianyingke.property.activities.WIFILoginActivity")) {
						Toast.makeText(getApplicationContext(), "该用户未注册开通WIFI账户",
								Toast.LENGTH_LONG).show();
						return;
		
					}
					if (LocalStore.getUserInfo().userId == 0
							&& propertyActivityRes
									.get(position)
									.toString()
									.equals("class com.wuxianyingke.property.activities.WIFILoginActivity")) {
						Toast.makeText(getApplicationContext(), "请登录。。。",
								Toast.LENGTH_SHORT).show();
						remindDialog();
					} else {
						startActivity(intent);
					}
				}
	}

	@Override
	protected void onResume() {
				// TODO Auto-generated method stub
				super.onResume();
				if (LocalStore.getUserInfo().telnumber == null||LocalStore.getUserInfo().telnumber.equals("")) {
					Intent intent = new Intent();
					intent.setClass(getApplicationContext(), LoginActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
							| Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
					finish();
				} else {
					// 初始化icon图标
					Thread getUserActiveInfo = new Thread() {
						@Override
						public void run() {
							RemoteApiImpl remoteApi = new RemoteApiImpl();
		
							RemoteApi.User user = remoteApi.getUserActiveInfo(
									MainActivity.this,
									LocalStore.getUserInfo().telnumber);
							Log.i("MyLog", "phone = local ="
									+ LocalStore.getUserInfo().phone);
							Message msg = new Message();
							if (user != null) {
								if (user.netInfo.code == 200) {
									LocalStore.setUserInfo(MainActivity.this, user);
									getMenuList();
								} else {
									Log.i("MyLog", "userinfo = " +user.netInfo.desc+"  ="+LocalStore.getUserInfo().telnumber);
									desc = user.netInfo.desc;
									msg.what = 10;
								}
							} else {
								msg.what = 5;
							}
							mHandler.sendMessage(msg);
						}
					};
					getUserActiveInfo.start();
				}
	}

	@Override
	protected void onStart() {
				super.onStart();
				locationService = ((PropertyApplication) getApplication()).locationService;
				// 获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
				locationService.registerListener(mListener);
				// 注册监听
				int type = getIntent().getIntExtra("from", 0);
				if (type == 0) {
					locationService.setLocationOption(locationService
							.getDefaultLocationClientOption());
				} else if (type == 1) {
					locationService.setLocationOption(locationService.getOption());
				}
				locationService.start();
		
	 }
		
			@Override
	protected void onStop() {
				// TODO Auto-generated method stub
				super.onStop();
				locationService.unregisterListener(mListener); // 注销掉监听
				locationService.stop(); // 停止定位服务
	}

	private BDLocationListener mListener = new BDLocationListener() {

			@Override
			public void onReceiveLocation(BDLocation location) {
				// map view 销毁后不在处理新接收的位置
				if (location == null)
					return;
				info.latitude = location.getLatitude();
				info.longitude = location.getLongitude();
				info.city = location.getCity();
				LocalStore.setLatitude(getApplicationContext(),
						(float) location.getLatitude());
				LocalStore.setLongitude(getApplicationContext(),
						(float) location.getLongitude());
				Log.i("MyLog",
						"百度定位获得的当前的位置为------------》" + info.city
								+ location.getCity() + "----" + "latitude = "
								+ LocalStore.getLatitude(getApplicationContext())
								+ " / "
								+ LocalStore.getLongitude(getApplicationContext())
								+ location.getCity());
				LocalStore.cityInfo.city_name = info.city;
				LocalStore.setCityInfo(MainActivity.this, LocalStore.cityInfo);
	
			}
	};

	/**
	 * 定位SDK监听函数
	 */
	/*
	 * public class MyLocationListenner implements BDLocationListener {
	 * 
	 * @Override public void onReceiveLocation(BDLocation location) { // map
	 * view 销毁后不在处理新接收的位置 if (location == null) return;
	 * 
	 * info.latitude = location.getLatitude(); info.longitude =
	 * location.getLongitude(); info.city = location.getCity();
	 * LocalStore.setLatitude(getApplicationContext(),
	 * (float)location.getLatitude());
	 * LocalStore.setLongitude(getApplicationContext(),
	 * (float)location.getLongitude()); Log.i("MyLog",
	 * "百度定位获得的当前的位置为------------》" + info.city +
	 * "----"+"latitude = "+LocalStore
	 * .getLatitude(getApplicationContext())+" / "
	 * +LocalStore.getLongitude(getApplicationContext()) + location.getCity());
	 * LocalStore.cityInfo.city_name = info.city;
	 * LocalStore.setCityInfo(MainActivity.this, LocalStore.cityInfo); } public
	 * void onReceivePoi(BDLocation poiLocation){ } }
	 */
	protected void confirmLogouDialog() {
			AlertDialog.Builder builder = new Builder(this);
	
			String strTitle = getResources().getString(R.string.txt_tips);
			String strOk = getResources().getString(R.string.txt_ok);
			String strCancel = getResources().getString(R.string.txt_cancel);
	
			builder.setTitle(strTitle);
			builder.setMessage("确认退出登录吗？");
	
			builder.setPositiveButton(strOk, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
	
					LocalStore.logout(MainActivity.this);
	
					Intent intent = new Intent();
					intent.setClass(MainActivity.this, LoginActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
							| Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
	
					finish();
				}
			});
	
			builder.setNegativeButton(strCancel,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
	
			builder.create().show();
	}

	/**
	 * 游客报修提醒
	 */
	private void remindDialog() {
			// 1. 布局文件转换为View对象
			LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
			LinearLayout layout = (LinearLayout) inflater.inflate(
					R.layout.activity_remind_dialog, null);
			final Dialog dialog = new AlertDialog.Builder(MainActivity.this)
					.create();
			dialog.setCancelable(false);
			dialog.show();
			dialog.getWindow().setContentView(layout);
			WindowManager wm = (MainActivity.this).getWindowManager();
			WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
			params.width = wm.getDefaultDisplay().getWidth() * 5 / 6;
			params.height = wm.getDefaultDisplay().getHeight() * 4 / 11;
			dialog.getWindow().setAttributes(params);
			TextView dialog_msg = (TextView) layout
					.findViewById(R.id.remind_messagesId);
			TextView btnOK = (TextView) layout.findViewById(R.id.btn_yesId);
			btnOK.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(MainActivity.this,
							LoginActivity.class);
					intent.putExtra("requst", 1);
					startActivityForResult(intent, 1);
					finish();
					dialog.dismiss();
				}
			});
	
			// 5. 取消按钮
			TextView btnCancel = (TextView) layout.findViewById(R.id.btn_noId);
			btnCancel.setOnClickListener(new OnClickListener() {
	
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});

	}

	// 获取软件版本号，对应AndroidManifest.xml下android:versionCode
	private int getVersionCode() {
			int versionCode = 0;
			try {
				
				versionCode = getPackageManager().getPackageInfo(
						"com.mantoto.property", 0).versionCode;
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
			return versionCode;
		}
}