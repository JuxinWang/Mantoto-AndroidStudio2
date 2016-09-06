package com.wuxianyingke.property.remote;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.LogUtil;
import com.wuxianyingke.property.common.Util;

public class RemoteApiImpl implements RemoteApi {
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy.MM.dd");

	/** 活动手机客户端启动界面的图片数据请求 */
	/**
	 * logoid int 客户端当前保存的启动界面标识号 propertyid int 楼盘标识号
	 * 
	 * 返回数据类型 logoUrl String 启动界面图片Url logoID int 启动界面图片的标识号
	 * */
	@Override
	public Loading getLoadingInfo(int logoId, long propertyid) {
		JSONObject send = new JSONObject();
		JSONObject response = null;
		try {
			send.put("logoid", logoId);
			send.put("propertyid", propertyid);
			response = HttpComm.sendJSONToServer(Constants.GET_LOADING_URL,
					send, Constants.HTTP_SO_TIMEOUT);
			if (response == null) {
				return null;
			}
			Loading loading = new Loading();
			loading.logoImgUrl = response.getString("logoUrl");
			loading.logoId = response.getInt("LogoID");
			loading.netInfo = new NetInfo();
			loading.netInfo.code = response.getInt("code");
			loading.netInfo.desc = response.getString("desc");
			return loading;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 获得首页清单列表信息 propertyid 小区id
	 * */
	@Override
	public ArrayList<MenuList> getHomeMenu(Context context, int propertyid) {
		ArrayList<MenuList> menuList;
		JSONObject send = new JSONObject();
		JSONObject response = null;
		try {
			send.put("propertyid", propertyid);
			response = HttpComm.sendJSONToServer(Constants.GET_MENU_LIST_URL,
					send, Constants.HTTP_SO_TIMEOUT);
			Log.i("MyLog", "RemoteApiImpl-------获得清单列表信息为：" + response);
			if (response == null) {
				return null;
			}
			menuList = new ArrayList<MenuList>();
			int code = response.getInt("code");
			if (200 == code) {
				JSONArray menuLists = response.getJSONArray("menuarray");
				for (int i = 0; i < menuLists.length(); i++) {
					JSONObject obj = menuLists.getJSONObject(i);
					MenuList mList = new MenuList();
					mList.Tag = obj.getString("Tag");
					mList.MenuName = obj.getString("MenuName");
					mList.MenuID = obj.getInt("MenuID");
					menuList.add(mList);
				}
			}
			Log.i("MyLog", "response--------+,menuList=" + response);
			return menuList;
		} catch (Exception e) {

		}
		return null;
	}

	/**
	 * 根据名称模糊查询小区
	 */
	@Override
	public ArrayList<Propertys> getPropertyList(Context context,
			String description, int pageindex) {
		JSONObject send = new JSONObject();
		JSONObject response = null;
		ArrayList<Propertys> propertyList = null;
		try {
			send.put("description", description);
			send.put("pageindex", pageindex);

			response = HttpComm.sendJSONToServer(Constants.Get_LOADING_BY_NAME,
					send, Constants.HTTP_SO_TIMEOUT);
			Log.i("MyLog", "通过名称查询到的小区为---：" + response);
			if (response == null) {
				return null;
			} else {
				propertyList = new ArrayList<Propertys>();
				int code = response.getInt("code");
				if (200 == code) {
					JSONArray propertysList = response
							.getJSONArray("propertyarray");
					for (int i = 0; i < propertysList.length(); i++) {
						JSONObject msgObj = propertysList.getJSONObject(i);
						Propertys propertys = new Propertys();
						propertys.PropertyID = msgObj.getLong("PropertyID");
						propertys.PropertyName = msgObj
								.getString("PropertyName");
						propertys.OrganizationID = msgObj
								.getInt("OrganizationID");
						propertys.latitude = msgObj.getInt("latitude");
						propertys.longitude = msgObj.getInt("longitude");
						propertyList.add(propertys);
					}
				}
			}
			return propertyList;
		} catch (Exception e) {

		}
		return null;
	}

	/**
	 * 根据当前位置识别小区
	 */
	@Override
	public ArrayList<Propertys> getPropertyList(Context context,
			float latitude, float longitude, int pageindex) {
		JSONObject send = new JSONObject();
		JSONObject response = null;
		ArrayList<Propertys> propertyList = null;//小区集合
		try {
			send.put("latitude", latitude);
			send.put("longitude", longitude);
			send.put("pageindex", pageindex);
			response = HttpComm.sendJSONToServer(
					Constants.GET_LOADING_BY_LOCATION, send,
					Constants.HTTP_SO_TIMEOUT);
			Log.i("MyLog", "通过定位获得的当前的小区为----------------" + response);
			if (response == null) {
				return null;
			} else {
				propertyList = new ArrayList<Propertys>();
				int code = response.getInt("code");
				if (200 == code) {
					JSONArray propertysList = response
							.getJSONArray("propertyarray");
					for (int i = 0; i < propertysList.length(); i++) {
						JSONObject msgObj = propertysList.getJSONObject(i);
						Propertys propertys = new Propertys();
						propertys.PropertyID = msgObj.getLong("PropertyID");
						propertys.PropertyName = msgObj
								.getString("PropertyName");
						propertys.OrganizationID = msgObj
								.getInt("OrganizationID");
						propertys.latitude = msgObj.getInt("latitude");
						propertys.longitude = msgObj.getInt("longitude");
						propertyList.add(propertys);
					}
				}
			}
			return propertyList;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

	/**
	 * 识别小区验证码 请求字段 regcode string 小区验证码 返回结果 PropertyID int 楼盘id PropertyName
	 * String 楼盘名称 PhoneNumber String 物业电话号码 logoUrl String 楼盘logo
	 * */
	@Override
	public InvitationCode sendInvitationCode(Context c, String regcode) {
		JSONObject send = new JSONObject();
		JSONObject response = null;
		try {
			send.put("regcode", regcode);
			// send.put("softtype", 1) ;
			response = HttpComm.sendJSONToServer(Constants.INVITATION_CODE_URL,
					send, Constants.HTTP_SO_TIMEOUT);

			if (response == null)
				return null;

			InvitationCode info = new InvitationCode();
			info.netInfo = new NetInfo();
			info.netInfo.code = response.getInt("code");
			info.netInfo.desc = response.getString("desc");
			if (200 == info.netInfo.code) {
				info.propertyID = response.getInt("PropertyID");
				info.propertyName = response.getString("PropertyName");
				info.phoneNumber = response.getString("PhoneNumber");
				info.logoUrl = response.getString("logoUrl");
			}
			return info;

		} catch (Exception ex) {
			ex.printStackTrace();
			LogUtil.d("MyTag", "Remote userLogin error: " + ex.getMessage());
			return null;
		}
	}

	/*
	 * (非 Javadoc) <p>Title: userLogin</p>
	 * <p>Description:------------------------
	 * -用户登录---------------------------------------- </p>
	 * 
	 * @param c 上下文参数
	 * 
	 * @param name 用户名
	 * 
	 * @param password 用户密码
	 * 
	 * @return 用户id 和 用户名
	 * 
	 * @see
	 * com.wuxianyingke.property.remote.RemoteApi#userLogin(android.content.
	 * Context, java.lang.String, java.lang.String)
	 */
	@Override
	public User userLogin(Context c, String name, String password) {
		JSONObject send = new JSONObject();
		JSONObject response = null;
		try {
			send.put("username", name);
			send.put("password", password);
			send.put("softtype", 1);
			response = HttpComm.sendJSONToServer(Constants.USER_LOGIN_URL,
					send, Constants.HTTP_SO_TIMEOUT);
			Log.i("MyLog", "用户登录返回的数据为" + response);
			if (response == null)
				return null;

			User info = new User();
			info.netInfo = new NetInfo();
			info.netInfo.code = response.getInt("code");
			info.netInfo.desc = response.getString("desc");
			if (200 == info.netInfo.code) {
				info.userId = response.getLong("UserID");
				info.userName = response.getString("UserName");
				info.PropertyID = response.getInt("PropertyID");
				info.phone = response.getString("PhoneNumber");
				info.telnumber = response.getString("Mobile");
				Log.i("MyLog", "登陆的密码为===" + info.userId + info.PropertyID);
			}
			return info;

		} catch (Exception ex) {
			ex.printStackTrace();
			LogUtil.d("MyTag", "Remote userLogin error: " + ex.getMessage());
			return null;
		}
	}



	// 支付缴费
	@Override
	public OrderItem billPayOrder(Context c, long promotionid, long userid,
			long billid, int number) {
		JSONObject send = new JSONObject();
		JSONObject response = null;
		try {

			// 提交请求字段
			send.put("promotionid", promotionid);
			send.put("userid", userid);
			send.put("billid", billid);
			send.put("number", number);
			Log.i("MyLog", "remote-promotionid=" + promotionid);
			// 获取网络返回数据
			response = HttpComm.sendJSONToServer(Constants.GET_BILL_PAID, send,
					Constants.HTTP_SO_TIMEOUT);
			Log.i("MyLog", "getBillPay---返回订单信息为：" + response.toString());
			if (response == null)
				return null;

			OrderItem info = new OrderItem();
			info.ThePromotion = new Promotion();
			info.Status = new Status();
			// info.netInfo = new NetInfo();
			// info.netInfo.code = response.getInt("code");
			// info.netInfo.desc = response.getString("desc");
			// 解析数据并存入到Orderitem对象中
			if (response != null) {
				// JSONObject userObj = response.getJSONObject("user") ;
				// info.ThePromotion = response.getString("ThePromotion");
				// JSONObject obj0 = new JSONObject(response.toString());
				JSONObject obj = response.getJSONObject("ThePromotion");
				Log.i("MyLog", "thepromotion-------------->" + obj.toString());
				// Promotion promotion=new Promotion();
				Log.i("MyLog", "bodyooo------->" + obj.getString("body"));

				info.ThePromotion.body = obj.getString("body");
				Log.i("MyLog", "body------->" + obj.getString("body"));
				info.ThePromotion.LivingItemID = obj.getInt("LivingItemID");
				Log.i("MyLog",
						"LivingItemID------->" + obj.getInt("LivingItemID"));
				info.ThePromotion.PromotionID = obj.getLong("PromotionID");
				info.ThePromotion.Price = obj.getDouble("Price");
				info.ThePromotion.ETime = obj.getString("ETime");
				Log.i("MyLog", "ETime------->" + obj.getString("ETime"));
				info.ThePromotion.path = obj.getString("path");
				info.ThePromotion.Height = obj.getInt("Height");
				info.ThePromotion.Width = obj.getInt("Width");
				info.ThePromotion.header = obj.getString("header");
				info.ThePromotion.SaleTypeID = obj.getInt("SaleTypeID");
				Log.i("MyLog", "header------->" + obj.getString("header"));
				info.ThePromotion.Priority = obj.getInt("Priority");
				info.ThePromotion.ForSal = obj.getBoolean("ForSale");
				Log.i("MyLog", "ForSale------->" + obj.getBoolean("ForSale"));
				// if (obj.getString("TelNumber")==null) {
				// info.TelNumber="号码为空！";
				// }else {
				// info.TelNumber=obj.getString("TelNumber");
				// }

				// info.ThePromotion.
				info.OrderID = response.getString("OrderID");
				info.Number = response.getInt("Number");
				info.Comment = response.getString("Comment");
				Log.i("MyLog",
						"OrderID------->" + response.getString("Comment"));
				info.UserID = response.getLong("UserID");
				// Status status=new Status();
				JSONObject obj2 = response.getJSONObject("Status");
				Log.i("MyLog", "Status-------------->" + obj2.toString());
				info.Status.OrderStatusID = obj2.getInt("OrderStatusID");
				info.Status.OrderStatusName = obj2.getString("OrderStatusName");
				info.TelNumber = response.getString("TelNumber");
				info.AliOrderStr = response.getString("AliOrderStr");
				info.TheAddress = response.getString("Address");
				info.Comment = response.getString("Comment");
				info.OrderSequenceNumber = response
						.getLong("OrderSequenceNumber");
				info.Total = response.getDouble("Total");
				info.Ctime = response.getString("CTime");

				Log.i("MyLog",
						"CTime-------------->" + response.getString("CTime"));
			}
			Log.i("MyLog", "createOrderItem----------" + response.toString());
			return info;
		} catch (Exception e) {
		}
		return null;
	}

	// 获取完成的订单
	/*
	 * (非 Javadoc) <p>Title: getCompletedOrder</p>
	 * <p>Description:----------------
	 * ----------获取已完成的订单----------------------------------</p>
	 * 
	 * @param c 上下文参数
	 * 
	 * @param ordersequencenumber 订单序号
	 * 
	 * @return code desc
	 * 
	 * @see
	 * com.wuxianyingke.property.remote.RemoteApi#getCompletedOrder(android.
	 * content.Context, long)
	 */
	@Override
	public OrderInfo getCompletedOrder(Context c, long userid, int pageindex) {
		JSONObject send = new JSONObject();
		JSONObject response = null;
		try {
			send.put("userid", userid);
			send.put("pageindex", pageindex);
			response = HttpComm.sendJSONToServer(Constants.ORDER_RECIEVED_URL,
					send, Constants.HTTP_SO_TIMEOUT);
			Log.i("MyLog", "获得已经完成的定单为@#￥%…………&&**==response" + response);
			if (response == null) {
				return null;
			}

			OrderInfo info = new OrderInfo();
			info.orderInfo = new ArrayList<OrderItem>();
			/*
			 * OrderItem oi = new OrderItem(); oi.ThePromotion = new
			 * Promotion(); oi.Status=new Status();
			 */
			// info.netInfo = new NetInfo();
			// info.netInfo.code = response.getInt("code");
			// info.netInfo.desc = response.getString("desc");
			if (response != null) {
				JSONArray array = response.getJSONArray("ordersarray");

				for (int i = 0; i < array.length(); i++) {
					OrderItem oi = new OrderItem();
					oi.ThePromotion = new Promotion();
					oi.Status = new Status();
					JSONObject obj = array.getJSONObject(i);
					JSONObject obj2 = obj.getJSONObject("ThePromotion");
					oi.ThePromotion.header = obj2.getString("header");
					oi.ThePromotion.path = obj2.getString("path");
					oi.ThePromotion.body = obj2.getString("body");
					oi.ThePromotion.Price = obj2.getDouble("Price");
					oi.ThePromotion.SaleTypeID = obj2.getInt("SaleTypeID");
					Log.i("MyLog",
							"path=================>complete"
									+ obj2.getString("path"));
					oi.OrderID = obj.getString("OrderID");
					oi.Total = obj.getDouble("Total");
					oi.Ctime = obj.getString("CTime");
					oi.Number = obj.getInt("Number");
					oi.TelNumber = obj.getString("TelNumber");
					oi.OrderSequenceNumber = obj.getLong("OrderSequenceNumber");
					JSONObject obj3 = obj.getJSONObject("Status");
					oi.Status.OrderStatusID = obj3.getInt("OrderStatusID");
					Log.i("MyLog",
							"Number=================>complete"
									+ obj.getInt("Number"));
					info.orderInfo.add(oi);
				}

			}
			Log.i("MyLog", "获得yijing完成的订单为info=======================>"
					+ response);
			return info;
		} catch (Exception ex) {
			ex.printStackTrace();
			LogUtil.d("MyTag",
					"Remote getuncommplete error: " + ex.getMessage());
		}
		return null;
	}

	// 获取未完成的订单
	/*
	 * (非 Javadoc) <p>Title: getUncompletedOrder</p>
	 * <p>Description:--------------
	 * ------------获取未完成的订单------------------------------</p>
	 * 
	 * @param c 上下文参数
	 * 
	 * @param userid 用户id
	 * 
	 * @param pageindex 分页
	 * 
	 * @return
	 * 
	 * @see
	 * com.wuxianyingke.property.remote.RemoteApi#getUncompletedOrder(android
	 * .content.Context, long, int)
	 */
	@Override
	public OrderInfo getUncompletedOrder(Context c, long userid, int pageindex) {
		JSONObject send = new JSONObject();
		JSONObject response = null;
		try {
			send.put("userid", userid);
			send.put("pageindex", pageindex);
			response = HttpComm.sendJSONToServer(
					Constants.ORDER_UNCLOSED_GET_URL, send,
					Constants.HTTP_SO_TIMEOUT);
			Log.i("MyLog", "获得未完成的订单为==response" + response);
			if (response == null) {
				return null;
			}
			OrderInfo info = new OrderInfo();
			info.orderInfo = new ArrayList<OrderItem>();

			// info.netInfo = new NetInfo();
			// info.netInfo.code = response.getInt("code");
			// info.netInfo.desc = response.getString("desc");
			if (response != null) {
				JSONArray array = response.getJSONArray("ordersarray");

				for (int i = 0; i < array.length(); i++) {
					OrderItem oi = new OrderItem();
					oi.ThePromotion = new Promotion();
					oi.Status = new Status();
					JSONObject obj = array.getJSONObject(i);
					JSONObject obj2 = obj.getJSONObject("ThePromotion");
					oi.ThePromotion.header = obj2.getString("header");
					oi.ThePromotion.path = obj2.getString("path");
					oi.ThePromotion.body = obj2.getString("body");
					oi.ThePromotion.Price = obj2.getDouble("Price");
					oi.ThePromotion.SaleTypeID = obj2.getInt("SaleTypeID");
					oi.OrderID = obj.getString("OrderID");
					oi.Total = obj.getDouble("Total");
					oi.Ctime = obj.getString("CTime");
					oi.Number = obj.getInt("Number");
					oi.TelNumber = obj.getString("TelNumber");
					oi.OrderSequenceNumber = obj.getLong("OrderSequenceNumber");
					Log.i("MyLog",
							"Number=================>" + obj.getInt("Number"));
					JSONObject obj3 = obj.getJSONObject("Status");
					oi.Status.OrderStatusID = obj3.getInt("OrderStatusID");
					info.orderInfo.add(oi);
				}
			}
			Log.i("MyLog", "获得未完成的订单为info=======================>" + response);
			return info;
		} catch (Exception ex) {
			ex.printStackTrace();
			LogUtil.d("MyTag",
					"Remote getuncommplete error: " + ex.getMessage());
		}
		return null;
	}

	/*
	 * (非 Javadoc) <p>Title: getPromotionCodeArray</p>
	 * <p>Description:-------------------------获取指定订单的券码列表---------------- </p>
	*/
	@Override
	public PromotionCodeArray getPromotionCodeArray(Context c,
			long ordersequencenumber) {
		JSONObject send = new JSONObject();
		JSONObject response = null;
		try {
			send.put("ordersequencenumber", ordersequencenumber);
			response = HttpComm.sendJSONToServer(
					Constants.ORDER_GET_PROMOTIONCODE, send,
					Constants.HTTP_SO_TIMEOUT);
			Log.i("MyLog", "获得指定订单的券码列表为*&……%￥==response" + response);
			if (response == null) {
				return null;
			}
			PromotionCodeArray pcArray = new PromotionCodeArray();
			pcArray.proArray = new ArrayList<PromotionCode>();
			PromotionCode pc = new PromotionCode();
			pc.PromotionStatus = new PromotionCodeStatus();
			if (response != null) {
				JSONArray array = response.getJSONArray("promotioncodearray");
				for (int i = 0; i < array.length(); i++) {
					JSONObject obj = array.getJSONObject(i);
					pc.Code = obj.getString("Code");
					pc.PromotionCodeID = obj.getLong("PromotionCodeID");
					JSONObject obj2 = obj.getJSONObject("Status");
					pc.PromotionStatus.PromotionCodeStatusName = obj2
							.getString("PromotionCodeStatusName");
					Log.i("MyLog",
							"pc.PromotionStatus.PromotionCodeStatusName=="
									+ pc.PromotionStatus.PromotionCodeStatusName);
					pcArray.proArray.add(pc);
				}
			}
			Log.i("MyLog", "￥￥%获得我的消费券为=======================>" + response);
			return pcArray;
		} catch (Exception ex) {
			ex.printStackTrace();
			LogUtil.d("MyTag",
					"Remote getuncommplete error: " + ex.getMessage());
		}
		return null;
	}

	// 未使用的消费券列表
	@Override
	public OrderInfo getUnUsePromotion(Context c, long userid, int pageindex) {
		JSONObject send = new JSONObject();
		JSONObject response = null;
		try {
			send.put("userid", userid);
			send.put("pageindex", pageindex);
			response = HttpComm.sendJSONToServer(
					Constants.ORDER_UNUSED_PROMOTION_URL, send,
					Constants.HTTP_SO_TIMEOUT);
			Log.i("MyLog", "++++++##获得未使用的消费券==response" + response);
			if (response == null) {
				return null;
			}

			OrderInfo info = new OrderInfo();
			info.orderInfo = new ArrayList<OrderItem>();

			// info.netInfo = new NetInfo();
			// info.netInfo.code = response.getInt("code");
			// info.netInfo.desc = response.getString("desc");
			if (response != null) {
				JSONArray array = response.getJSONArray("ordersarray");

				for (int i = 0; i < array.length(); i++) {
					OrderItem oi = new OrderItem();
					oi.ThePromotion = new Promotion();
					JSONObject obj = array.getJSONObject(i);
					JSONObject obj2 = obj.getJSONObject("ThePromotion");
					oi.ThePromotion.header = obj2.getString("header");
					oi.ThePromotion.path = obj2.getString("path");
					oi.ThePromotion.body = obj2.getString("body");
					oi.ThePromotion.Price = obj2.getDouble("Price");
					oi.ThePromotion.SaleTypeID = obj2.getInt("SaleTypeID");
					// Log.i("MyLog",
					// "path=================>"+obj2.getString("path"));
					oi.OrderID = obj.getString("OrderID");
					oi.Total = obj.getDouble("Total");
					oi.Ctime = obj.getString("CTime");
					oi.Number = obj.getInt("Number");
					oi.TelNumber = obj.getString("TelNumber");
					oi.OrderSequenceNumber = obj.getLong("OrderSequenceNumber");
					Log.i("MyLog",
							"Number=================>" + obj.getInt("Number"));
					info.orderInfo.add(oi);
				}

			}
			Log.i("MyLog", "获得未完成的订单为info=======================>" + response);
			return info;
		} catch (Exception ex) {
			ex.printStackTrace();
			LogUtil.d("MyTag",
					"Remote getuncommplete error: " + ex.getMessage());
		}
		return null;
	}
	
/*	// 删除收藏夹商品
		public NetInfo deleteFromFavorite(Context c, long userId, long productId) {
			NetInfo ret = null;
			try {
				JSONObject reqObj = new JSONObject();
				reqObj.put("userid", userId);
				reqObj.put("productid", productId);

				JSONObject resObj = HttpComm.sendJSONToProductServer(
						Constants.DELETE_FROM_FAVORITE_URL, reqObj,
						Constants.HTTP_SO_TIMEOUT);
				if (resObj != null) {
					ret = new NetInfo();
					ret.code = resObj.getInt("code");
					ret.desc = resObj.getString("desc");
				}
			} catch (JSONException e) {
				e.printStackTrace();
				ret = null;
			}
			return ret;
		}
*/
	//判断注册或者修改密码(取值：1(表示注册)；2(表示修改))
	@Override
	public NetInfos getPhoneCode(Context c, String phoneNum ,String appName,int sendType) {
		JSONObject send = new JSONObject();
		JSONObject response = null;
		NetInfos info =null;
		try {
			send.put("phoneNum", phoneNum);
			send.put("appName", "mantutu");
			send.put("sendType", sendType);
			response = HttpComm.sendJSONToServer2(Constants.GET_PHONE_CODE,send, Constants.HTTP_SO_TIMEOUT);
			Log.i("MyLog", "phoneNum="+phoneNum);
			Log.i("MyLog", "获取验证码数据为" + response);
			if (response == null)
				return null;

			info = new NetInfos();
			info.code = response.getInt("code");
			info.desc = response.getString("desc");
			return info;

		} catch (Exception ex) {
			ex.printStackTrace();
			LogUtil.d("MyTag", "Remote userLogin error: " + ex.getMessage());
			return null;
		}
	}
	
	@Override
	public NetInfos getVarificationCode(Context context, String phoneNum,
			String smsCode) {
		JSONObject send = new JSONObject();
		JSONObject response = null;
		NetInfos info =null;
		try {
			JSONObject reqObject = new JSONObject();
			reqObject.put("phoneNum", phoneNum);
			reqObject.put("smsCode", smsCode);
			response = HttpComm.sendJSONToServer2(Constants.GET_PHONE_VERIFICATION_CODE, reqObject, Constants.HTTP_SO_TIMEOUT);
			Log.i("MyLog", "检查  验证码数据为" + response);
			if (response == null)
				return null;

			info = new NetInfos();
			info.code = response.getInt("code");
			info.desc = response.getString("desc");
			info.bSuccess = response.getBoolean("bSuccess");
			return info;

		} catch (Exception ex) {
			ex.printStackTrace();
			LogUtil.d("MyTag", "Remote userLogin error: " + ex.getMessage());
			return null;
		}
	}
	
	// 用户注册
	/*
	 * (非 Javadoc) <p>Title: userRegister</p>
	 * <p>Description:--------------------
	 * ------用户注册------------------------------------ </p>
	 * 
	 * @param c 上下文参数
	 * 
	 * @param houseNumber 门牌号
	 * 
	 * @param proprietorName 真实姓名
	 * 
	 * @param userName 用户名
	 * 
	 * @param password 密码
	 * 
	 * @param propertyid 楼盘id
	 * 
	 * @return 用户id 用户名
	 * 
	 * @see
	 * com.wuxianyingke.property.remote.RemoteApi#userRegister(android.content
	 * .Context, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.Long)
	 */
	@Override
	public User userRegister(Context c, String houseNumber,
			String proprietorName, String telnumber, String password,
			Long propertyid) {
		JSONObject send = new JSONObject();
		JSONObject response = null;
		try {
			send.put("telnumber", telnumber);
			// send.put("username", userName);
			send.put("truename", proprietorName);
			send.put("room", houseNumber);
			send.put("password", password);
			send.put("propertyid", propertyid);
			response = HttpComm.sendJSONToServer(Constants.USER_REGISTER_URL,
					send, Constants.HTTP_SO_TIMEOUT);

			Log.i("MyLog", "useRegister----用户注册--" + response);
			if (response == null)
				return null;

			User info = new User();
			info.netInfo = new NetInfo();
			info.netInfo.code = response.getInt("code");
			info.netInfo.desc = response.getString("desc");
			if (info.netInfo.code == 200) {
				// JSONObject userObj = response.getJSONObject("user") ;
				info.userId = response.getLong("UserID");
				info.userName = response.getString("UserName");
				info.PropertyID = response.getInt("PropertyID");
				info.telnumber = response.getString("PhoneNumber");
				info.phone = response.getString("Mobile");
			}

			return info;

		} catch (Exception ex) {
			ex.printStackTrace();
			LogUtil.d("MyTag", "Remote userRegister error: " + ex.getMessage());
			return null;
		}
	}

	/** 应用没有方法 */
	public UserCenterRetInfo getUserCenter(Context c, long userId) {
		JSONObject send = new JSONObject();
		JSONObject response = null;
		try {
			send.put("userid", userId);
			response = HttpComm.sendJSONToServer(Constants.USER_CENTER_URL,
					send, Constants.HTTP_SO_TIMEOUT);
			if (response == null)
				return null;
			UserCenterRetInfo info = new UserCenterRetInfo();
			info.netInfo = new NetInfo();
			info.netInfo.code = response.getInt("code");
			info.netInfo.desc = response.getString("desc");
			if (info.netInfo.code == 200) {
				JSONArray msgArray = response.getJSONArray("news");
				info.messages = new ArrayList<RemoteApi.ProductMessage>();
				for (int i = 0; i < msgArray.length(); ++i) {
					JSONObject msgObj = msgArray.getJSONObject(i);
					ProductMessage msg = new ProductMessage();
					msg.msgId = msgObj.getLong("MessageID");
					msg.header = msgObj.getString("Header");
					msg.body = msgObj.getString("Body");
					if (!"".equals(msgObj.getString("PublishTime"))) {
						long times = Long.valueOf(msgObj
								.getString("PublishTime"));
						msg.time = sdf.format(times);
					}
					info.messages.add(msg);
				}

				JSONArray noteArray = response.getJSONArray("notes");
				info.notes = new ArrayList<RemoteApi.Note>();
				for (int i = 0; i < noteArray.length(); ++i) {
					JSONObject noteObj = noteArray.getJSONObject(i);
					Note note = new Note();
					note.NoteId = noteObj.getLong("NoteID");
					note.header = noteObj.getString("Header");
					note.body = noteObj.getString("Body");
					note.signature = noteObj.getString("Signature");
					Log.d("MyTag",
							"noteObj.getStringSignature"
									+ noteObj.getString("Signature"));
					note.hit = noteObj.getInt("Hit");
					note.priorityId = noteObj.getInt("PropertyID");
					note.ppropertyManagerId = noteObj
							.getInt("PropertyManagerID");
					note.isValid = noteObj.getInt("isValid");
					if (!"".equals(noteObj.getString("CTime"))) {
						long times = Long.valueOf(noteObj.getString("CTime"));
						note.time = sdf.format(times);
					}
					info.notes.add(note);
				}

				JSONObject userObj = response.getJSONObject("user");
				info.user = new User();
				info.user.userId = userObj.getLong("UserID");
				info.user.userName = userObj.getString("UserName");
			}

			return info;

		} catch (Exception ex) {
			ex.printStackTrace();
			LogUtil.d("MyTag", "Remote userRegister error: " + ex.getMessage());
			return null;
		}
	}

	public PushMessageRetInfo getPushMessage(Context context, long userId,
			long lastPushId) {
		JSONObject send = new JSONObject();
		JSONObject response = null;
		try {
			send.put("imei", Util.md5(Util.getIME(context)));
			send.put("model", 1);
			send.put("userid", userId);
			send.put("pushmessageid", lastPushId);
			response = HttpComm.sendJSONToServer(Constants.GET_PUSH_MSG, send,
					Constants.HTTP_SO_TIMEOUT);
			if (response == null)
				return null;
			PushMessageRetInfo info = new PushMessageRetInfo();
			info.netInfo = new NetInfo();
			info.netInfo.code = response.getInt("code");
			info.netInfo.desc = response.getString("desc");
			info.pushList = new ArrayList<RemoteApi.PushMessage>();
			if (info.netInfo.code == 200) {
				JSONArray array = response.getJSONArray("messagearray");
				int length = array.length();
				for (int i = 0; i < length; ++i) {
					JSONObject obj = array.getJSONObject(i);
					PushMessage msg = new PushMessage();
					msg.pushMessageId = obj.getLong("PushMessageID");
					msg.header = obj.getString("header");
					msg.msg = obj.getString("body");
					msg.imgUrl = obj.getString("path");
					msg.readed = false;
					info.pushList.add(msg);
				}
			}
			return info;
		} catch (Exception ex) {
			ex.printStackTrace();
			LogUtil.d("MyTag",
					"Remote getPushMessage error: " + ex.getMessage());
			return null;
		}
	}

	/**
	 * 获得是首页信息详情
	 */
	public HomeMessage getHomeMsg(int logoId, long userId, int propertyid) {
		JSONObject send = new JSONObject();
		JSONObject response = null;
		try {
			send.put("logoid", logoId);
			send.put("userid", userId);
			send.put("propertyid", propertyid);
			response = HttpComm.sendJSONToServer(Constants.GET_HOME_MSG_URL,
					send, Constants.HTTP_SO_TIMEOUT);

			Log.i("MyLog",
					"Radio1Activity----------------------------->获得的首页数据详情为："
							+ response);
			if (response == null)
				return null;

			HomeMessage info = new HomeMessage();
			info.netInfo = new NetInfo();
			info.netInfo.code = response.getInt("code");
			info.netInfo.desc = response.getString("desc");
			if (info.netInfo.code == 200) {
				info.isVisitor = response.getBoolean("isVisitor");
				info.clientConfig = new ClientConfig();
				JSONObject configObj = response.getJSONObject("clientConfig");
				info.clientConfig.logoId = configObj.getInt("LogoID");
				info.clientConfig.logoUrl = configObj.getString("logoUrl");

				JSONArray notesArray = response.getJSONArray("notes");
				info.notes = new ArrayList<RemoteApi.Note>();
				int length = notesArray.length();
				for (int i = 0; i < length; ++i) {
					JSONObject obj = notesArray.getJSONObject(i);
					Note note = new Note();
					note.body = obj.getString("Body");
					note.NoteId = obj.getLong("NoteID");
					note.header = obj.getString("Header");
					note.priority = obj.getInt("Priority");
					note.signature = obj.getString("Signature");
					Log.d("MyTag",
							"noteObj.getStringSignature"
									+ obj.getString("Signature"));
					note.hit = obj.getInt("Hit");
					note.priorityId = obj.getInt("PropertyID");
					note.ppropertyManagerId = obj.getInt("PropertyManagerID");
					note.isValid = obj.getInt("isValid");
					note.time = obj.getString("CTime");
					if (!"".equals(obj.getString("CTime"))) {
						long times = Long.valueOf(obj.getString("CTime"));
						note.time = sdf.format(times);
					}
					info.notes.add(note);
				}
			}
			return info;
		} catch (Exception ex) {
			ex.printStackTrace();
			LogUtil.d("MyTag", "Remote userRegister error: " + ex.getMessage());
			return null;
		}
	}

	@Override
	public ProductCaategoryRetInfo getRootCategory() {
		JSONObject reqObj = new JSONObject();
		try {
			reqObj.put("", "");
			JSONObject resObj = HttpComm.sendJSONToServer(
					Constants.GET_CATEGORY_URL, reqObj,
					Constants.HTTP_SO_TIMEOUT);
			if (resObj == null)
				return null;
			ProductCaategoryRetInfo retInfo = new ProductCaategoryRetInfo();
			retInfo.netInfo = new NetInfo();
			retInfo.netInfo.code = resObj.getInt("code");
			retInfo.netInfo.desc = resObj.getString("desc");
			if (retInfo.netInfo.code == 200) {
				JSONArray cArray = resObj.getJSONArray("catearray");
				int length = cArray.length();
				retInfo.list = new ArrayList<RemoteApi.ProductCategory>();
				for (int i = 0; i < length; ++i) {
					JSONObject cObj = cArray.getJSONObject(i);
					ProductCategory category = new ProductCategory();
					category.categoryID = cObj.getLong("CategoryID");
					category.categoryName = cObj.getString("CategoryName");
					category.parentID = cObj.getLong("ParentID");
					category.isLeaf = cObj.getBoolean("isLeaf");
					category.layer = cObj.getInt("Layer");
					retInfo.list.add(category);
				}
			}
			return retInfo;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public ProductCaategoryRetInfo getChildCategory(Long cId) {

		JSONObject reqObj = new JSONObject();
		try {
			reqObj.put("categoryid", cId);
			JSONObject resObj = HttpComm.sendJSONToServer(
					Constants.GET_CHILDREN_CATEGORY_URL, reqObj,
					Constants.HTTP_SO_TIMEOUT);
			if (resObj == null)
				return null;
			ProductCaategoryRetInfo retInfo = new ProductCaategoryRetInfo();
			retInfo.netInfo = new NetInfo();
			retInfo.netInfo.code = resObj.getInt("code");
			retInfo.netInfo.desc = resObj.getString("desc");
			if (retInfo.netInfo.code == 200) {
				JSONArray cArray = resObj.getJSONArray("catearray");
				int length = cArray.length();
				retInfo.list = new ArrayList<RemoteApi.ProductCategory>();
				for (int i = 0; i < length; ++i) {
					JSONObject cObj = cArray.getJSONObject(i);
					ProductCategory category = new ProductCategory();
					category.categoryID = cObj.getLong("CategoryID");
					category.categoryName = cObj.getString("CategoryName");
					category.parentID = cObj.getLong("ParentID");
					category.isLeaf = cObj.getBoolean("isLeaf");
					category.layer = cObj.getInt("Layer");
					retInfo.list.add(category);
				}
			}
			return retInfo;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// 1：产品上架时间降序 2：产品上架时间升序3：价格降序4：价格升序
	public ProductListRetInfo getProductList(Long cId, int sortPara,
			int pageindex) {
		JSONObject reqObj = new JSONObject();
		try {
			reqObj.put("categoryid", cId);
			reqObj.put("sortpara", sortPara);
			reqObj.put("pageindex", pageindex);
			JSONObject resObj = HttpComm.sendJSONToServer(
					Constants.GET_PRODUCT_LIST_URL, reqObj,
					Constants.HTTP_SO_TIMEOUT);
			if (resObj == null)
				return null;
			ProductListRetInfo retInfo = new ProductListRetInfo();
			retInfo.netInfo = new NetInfo();
			retInfo.netInfo.code = resObj.getInt("code");
			retInfo.netInfo.desc = resObj.getString("desc");
			if (retInfo.netInfo.code == 200) {
				JSONArray pArray = resObj.getJSONArray("productarray");
				int length = pArray.length();
				retInfo.products = new ArrayList<RemoteApi.ProductBase>();
				for (int i = 0; i < length; ++i) {
					JSONObject pObj = pArray.getJSONObject(i);
					ProductBase p = new ProductBase();
					p.productId = pObj.getLong("ProductID");
					p.categoryId = pObj.getLong("CategoryID");
					p.productName = pObj.getString("ProductName");
					p.price = pObj.getDouble("UnitPrice");
					p.salePrice = pObj.getDouble("PromotionPrice");
					p.saleWord = pObj.getString("PromotionWord");
					p.dateTime = pObj.getString("CTime");
					p.productPic = new ProductPic();
					JSONObject picObj = pObj.getJSONObject("FrontCover");
					p.productPic.imgId = picObj.getLong("ImageID");
					p.productPic.desc = picObj.getString("Description");
					p.productPic.imgUrl = picObj.getString("Path");
					retInfo.products.add(p);
				}

				/*
				 * JSONArray cArray = resObj.getJSONArray("categories") ; length
				 * = cArray.length() ; retInfo.categorys = new
				 * ArrayList<RemoteApi.ProductCategory>() ; for(int i = 0 ; i <
				 * length ; ++i){ JSONObject cObj = cArray.getJSONObject(i) ;
				 * ProductCategory category = new ProductCategory() ;
				 * category.categoryID = cObj.getLong("CategoryID") ;
				 * category.categoryName = cObj.getString("CategoryName") ;
				 * category.parentID = cObj.getLong("ParentID") ;
				 * category.isLeaf = cObj.getBoolean("isLeaf") ; category.layer
				 * = cObj.getInt("Layer") ; retInfo.categorys.add(category) ; }
				 */
			}
			return retInfo;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public ProductDetailNew getProductDetail(Long productid) {
		JSONObject reqObj = new JSONObject();
		try {
			reqObj.put("productid", productid);

			JSONObject resObj = HttpComm.sendJSONToServer(
					Constants.GET_PRODUCT_DETAIL, reqObj,
					Constants.HTTP_SO_TIMEOUT);
			if (resObj == null)
				return null;
			ProductDetailNew retInfo = new ProductDetailNew();
			retInfo.netInfo = new NetInfo();
			retInfo.netInfo.code = resObj.getInt("code");
			retInfo.netInfo.desc = resObj.getString("desc");
			if (retInfo.netInfo.code == 200) {
				retInfo.otherInfo = resObj.getString("otherInfo");
				retInfo.productDesc = resObj.getString("productDesc");

				JSONObject pObj = resObj.getJSONObject("productBase");
				retInfo.product = new RemoteApi.ProductBase();
				retInfo.product.productId = pObj.getLong("ProductID");
				retInfo.product.categoryId = pObj.getLong("CategoryID");
				retInfo.product.productName = pObj.getString("ProductName");
				retInfo.product.price = pObj.getDouble("UnitPrice");
				retInfo.product.salePrice = pObj.getDouble("PromotionPrice");
				retInfo.product.saleWord = pObj.getString("PromotionWord");
				retInfo.product.dateTime = pObj.getString("CTime");
				retInfo.product.productPic = new ProductPic();
				JSONObject picObj = pObj.getJSONObject("FrontCover");
				retInfo.product.productPic.imgId = picObj.getLong("ImageID");
				retInfo.product.productPic.desc = picObj
						.getString("Description");
				retInfo.product.productPic.imgUrl = picObj.getString("Path");

				JSONArray eArray = resObj.getJSONArray("productExtends");
				int length = eArray.length();
				retInfo.pextends = new ArrayList<RemoteApi.ProductExtend>();
				for (int i = 0; i < length; ++i) {
					JSONObject eObj = eArray.getJSONObject(i);
					ProductExtend extend = new ProductExtend();
					extend.fieldKey = eObj.getString("FieldKey");
					extend.fieldValue = eObj.get("FieldValue");
					extend.disName = eObj.getString("DisplayName");
					retInfo.pextends.add(extend);
				}

				JSONArray pArray = resObj.getJSONArray("productPictures");
				length = pArray.length();
				retInfo.pics = new ArrayList<RemoteApi.ProductPic>();
				for (int i = 0; i < length; ++i) {
					JSONObject ppObj = pArray.getJSONObject(i);
					ProductPic pic = new ProductPic();
					pic.imgId = ppObj.getLong("ImageID");
					pic.desc = ppObj.getString("Description");
					pic.imgUrl = ppObj.getString("Path");
					retInfo.pics.add(pic);
				}
			}
			return retInfo;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public QuickSearchResult getQuickSearchResult(Context c, String searchKey) {
		JSONObject feed = new JSONObject();
		JSONObject feedResponse = null;
		try {
			feed.put("input", searchKey);

			// feedResponse =
			// HttpComm.sendJSONToServer(Constants.QUCIK_SEARCH_URL, feed,
			// Constants.HTTP_SO_TIMEOUT);
			feedResponse = HttpComm.sendJSONToProductServer(
					Constants.COO8_QUCIK_SEARCH_URL, feed,
					Constants.HTTP_SO_TIMEOUT);
			if (feedResponse == null)
				return null;

			JSONArray jsArray = (JSONArray) feedResponse.get("keywordarray");
			QuickSearchResult result = new QuickSearchResult();
			result.quickMatchList = new ArrayList<QuickMatch>();
			for (int i = 0; i < jsArray.length(); i++) {
				try {
					JSONObject obj = (JSONObject) jsArray.get(i);
					QuickMatch info = new QuickMatch();
					info.keyWord = obj.getString("name");
					info.number = obj.getInt("count");
					result.quickMatchList.add(info);

				} catch (Exception ex) {
				}
			}
			return result;

		} catch (Exception ex) {
			LogUtil.d("MyTag", "getQuickSearchResult error: " + ex.getMessage());
			return null;
		}
	}

	@Override
	public SearchResult getSearchResult(Context c, String searchKey, int type,
			int pageIndex, int pageSize) {
		JSONObject feed = new JSONObject();
		JSONObject feedResponse = null;
		try {
			feed.put("keyword", searchKey);
			feed.put("sorttype", type);
			feed.put("pageindex", pageIndex);
			feed.put("pagesize", pageSize);

			// feedResponse =
			// HttpComm.sendJSONToServer(Constants.NORMAL_SEARCH_URL, feed,
			// Constants.HTTP_SO_TIMEOUT);
			feedResponse = HttpComm.sendJSONToProductServer(
					Constants.COO8_NORMAL_SEARCH_URL, feed,
					Constants.HTTP_SO_TIMEOUT);

			if (feedResponse == null)
				return null;

			SearchResult result = new SearchResult();
			if (searchKey.equals("")) {
				result.resultType = 0;
				result.hotKeyList = new ArrayList<String>();
				result.productGuessList = new ArrayList<ProductTopInfo>();
				result.productSuggestList = new ArrayList<ProductTopInfo>();

				JSONArray jsHotArray = (JSONArray) feedResponse
						.get("hotwordarray");
				for (int i = 0; i < jsHotArray.length(); i++) {
					try {
						JSONObject obj = (JSONObject) jsHotArray.get(i);
						String hotWord = obj.getString("name");
						result.hotKeyList.add(hotWord);

					} catch (Exception ex) {
					}
				}

				JSONArray jsGuessArray = (JSONArray) feedResponse
						.get("recmdarray1");
				for (int i = 0; i < jsGuessArray.length(); i++) {
					try {
						JSONObject obj = (JSONObject) jsGuessArray.get(i);
						ProductTopInfo f = new ProductTopInfo();
						f.productId = obj.getString("productid");
						f.productDesc = obj.getString("name");
						f.productPrice = obj.getString("price");
						// f.productMarketPrice = obj.getString("marketprice");
						f.imageUrl = obj.getString("imageurl");
						f.imageDrawable = null;
						result.productGuessList.add(f);

					} catch (Exception ex) {
					}
				}

				JSONArray jsSuggestArray = (JSONArray) feedResponse
						.get("recmdarray2");
				for (int i = 0; i < jsSuggestArray.length(); i++) {
					try {
						JSONObject obj = (JSONObject) jsSuggestArray.get(i);
						ProductTopInfo f = new ProductTopInfo();
						f.productId = obj.getString("productid");
						f.productDesc = obj.getString("name");
						f.productPrice = obj.getString("price");
						// f.productMarketPrice = obj.getString("marketprice");
						f.imageUrl = obj.getString("imageurl");
						f.imageDrawable = null;
						result.productSuggestList.add(f);

					} catch (Exception ex) {
					}
				}
			} else {
				JSONArray jsResultArray = (JSONArray) feedResponse
						.get("productarray");
				if (jsResultArray.length() > 0) {
					result.resultType = 1;
					result.productResultList = new ArrayList<ProductTopInfo>();
					for (int i = 0; i < jsResultArray.length(); i++) {
						try {
							JSONObject obj = (JSONObject) jsResultArray.get(i);
							ProductTopInfo f = new ProductTopInfo();
							f.productId = obj.getString("productid");
							f.productDesc = obj.getString("name");
							f.productPrice = obj.getString("price");
							// f.productMarketPrice =
							// obj.getString("marketprice");
							f.imageUrl = obj.getString("imageurl");
							f.imageDrawable = null;
							result.productResultList.add(f);

						} catch (Exception ex) {
						}
					}
				} else
					result.resultType = 2;

				result.productGuessList = new ArrayList<ProductTopInfo>();
				result.productSuggestList = new ArrayList<ProductTopInfo>();
				JSONArray jsGuessArray = (JSONArray) feedResponse
						.get("recmdarray1");
				for (int i = 0; i < jsGuessArray.length(); i++) {
					try {
						JSONObject obj = (JSONObject) jsGuessArray.get(i);
						ProductTopInfo f = new ProductTopInfo();
						f.productId = obj.getString("productid");
						f.productDesc = obj.getString("name");
						f.productPrice = obj.getString("price");
						// f.productMarketPrice = obj.getString("marketprice");
						f.imageUrl = obj.getString("imageurl");
						f.imageDrawable = null;
						result.productGuessList.add(f);

					} catch (Exception ex) {
					}
				}

				JSONArray jsSuggestArray = (JSONArray) feedResponse
						.get("recmdarray2");
				for (int i = 0; i < jsSuggestArray.length(); i++) {
					try {
						JSONObject obj = (JSONObject) jsSuggestArray.get(i);
						ProductTopInfo f = new ProductTopInfo();
						f.productId = obj.getString("productid");
						f.productDesc = obj.getString("name");
						f.productPrice = obj.getString("price");
						// f.productMarketPrice = obj.getString("marketprice");
						f.imageUrl = obj.getString("imageurl");
						f.imageDrawable = null;
						result.productSuggestList.add(f);

					} catch (Exception ex) {
					}
				}

			}
			return result;

		} catch (Exception ex) {
			LogUtil.d("MyTag", "getSearchResult error: " + ex.getMessage());
			return null;
		}
	}

	/*
	 * ----------------------修改密码--------------------------</p>
	 * 
	 * @param c 上下文参数
	 * 
	 * @param userId 用户id
	 * 
	 * @param oldPwd 旧密码
	 * 
	 * @param newPwd 新密码
	 * 
	 */
	@Override
	public NetInfo modifyPassword(Context c, String telnumber, String newPwd) {
		JSONObject send = new JSONObject();
		JSONObject response = null;
		NetInfo ret = new NetInfo();
		ret = new NetInfo();
		try {
			send.put("telnumber", telnumber);
			send.put("newpassword", newPwd);
			response = HttpComm.sendJSONToServer(Constants.MODIFY_PASSWORD_URL,
					send, Constants.HTTP_SO_TIMEOUT);
			if (response == null) {
				ret.code = 4;
				ret.desc = "";
				return ret;
			}
			ret.code = response.getInt("code");
			ret.desc = response.getString("desc");
			return ret;

		} catch (Exception ex) {
			ex.printStackTrace();
			LogUtil.d("MyTag",
					"Remote modifyPassword error: " + ex.getMessage());
			ret.code = 4;
			ret.desc = "";
			return ret;
		}
	}

	/*
	 * (非 Javadoc) <p>Title: findPassword</p> <p>Description:
	 * ---------------------------找回密码---------------------------------</p>
	 */
	@Override
	public int findPassword(Context c, String name) {
		JSONObject send = new JSONObject();
		JSONObject response = null;
		try {
			send.put("username", name);
			response = HttpComm.sendJSONToServer(
					Constants.USER_FIND_PASSWORD_URL, send,
					Constants.HTTP_SO_TIMEOUT);
			if (response == null)
				return 4;

			String ret = response.getString("code");
			if (ret.equals("200"))
				return 1;
			else
				return 0;

		} catch (Exception ex) {
			ex.printStackTrace();
			LogUtil.d("MyTag", "Remote findPassword error: " + ex.getMessage());
			return 4;
		}
	}

	/*
	获取地区列表
	 */
	public ArrayList<Area> getAreaList(Context c, String areaid, int type) {
		LogUtil.d("MyTag", "getAreaList");
		ArrayList<Area> ret = null;
		try {
			JSONObject reqObj = new JSONObject();
			JSONObject resObj = null;
			reqObj.put("areaid", areaid);
			reqObj.put("type", type);
			resObj = HttpComm.sendJSONToServer(Constants.GET_AREA_LIST_URL,
					reqObj, Constants.HTTP_SO_TIMEOUT);
			if (resObj != null) {
				ret = new ArrayList<Area>();
				int code = resObj.getInt("code");
				LogUtil.d("MyTag", "getAreaList code = " + code);
				if (200 == code) {
					JSONArray areaList = resObj.getJSONArray("arealist");
					LogUtil.d("MyTag", "areaList length = " + areaList.length());

					for (int i = 0; i < areaList.length(); i++) {
						JSONObject msgObj = areaList.getJSONObject(i);
						Area msg = new Area();
						msg.areaid = msgObj.getString("AreaID");
						msg.areaname = msgObj.getString("AreaName");
						ret.add(msg);
					}
				} else {
				}
			}
		} catch (JSONException e) {
			LogUtil.d("MyTag", "JSONException : " + e);
			e.printStackTrace();
			ret = null;
		}
		return ret;
	}

	/*新建地址*/

	// userid long 用户id
	// recipient String 收件人姓名
	// telnumber string 联系电话
	// cityarea string 城市区域
	// detail string 详细地址
	// isdefault bool 是否为默认邮寄地址
	@Override
	public CreateAddress createNewAddress(Context c, long userid,
			String recipient, String telnumber, String cityarea, String detail,
			boolean isDefault) {

		JSONObject reqObj = new JSONObject();
		JSONObject resObj = null;
		try {
			reqObj.put("userid", userid);
			reqObj.put("recipient", recipient);
			reqObj.put("telnumber", telnumber);
			reqObj.put("cityarea", cityarea);
			reqObj.put("detail", detail);
			reqObj.put("isDefault", isDefault);
			resObj = HttpComm.sendJSONToServer(Constants.CREATE_ADDRESS,
					reqObj, Constants.HTTP_SO_TIMEOUT);
			CreateAddress cAddress = new CreateAddress();
			if (resObj == null) {
				return null;
			}
			if (resObj != null) {

				cAddress.netInfo = new NetInfo();
				cAddress.netInfo.code = resObj.getInt("code");
				cAddress.netInfo.desc = resObj.getString("desc");
				if (cAddress.netInfo.code == 200) {
					// JSONObject userObj = response.getJSONObject("user") ;

					cAddress.UserID = resObj.getLong("UserID");
					cAddress.AddressID = resObj.getLong("AddressID");
					cAddress.Recipient = resObj.getString("Recipient");
					cAddress.TelNumber = resObj.getString("TelNumber");
					cAddress.CityArea = resObj.getString("CityArea");
					cAddress.Detail = resObj.getString("Detail");
					cAddress.IsDefault = resObj.getBoolean("IsDefault");
					Log.i("MyLog", "服务端返回的新建地址信息为--=" + resObj
							+ cAddress.UserID);
				}
			}
			return cAddress;
		} catch (Exception e) {
		}

		return null;
	}

	// 获取所有收货地址列表
	public AddressAll getAllAddress(Context c, long uid) {
		LogUtil.d("MyTag", "getAllAddress");
		JSONObject reqObj = new JSONObject();
		JSONObject resObj = null;

		try {
			reqObj.put("userid", uid);
			resObj = HttpComm.sendJSONToServer(Constants.GET_ADDRESS_LIST_URL,
					reqObj, Constants.HTTP_SO_TIMEOUT);
			Log.i("MyLog", "服务端获取的地址数据为数据----" + resObj);
			AddressAll addressAll = new AddressAll();
			addressAll.addressItems = new ArrayList<AddressItem>();
			if (resObj != null) {
				int code = resObj.getInt("code");
				LogUtil.d("MyTag", "getAllAddress code = " + code);
				List<AddressItem> ret = new ArrayList<AddressItem>();
				if (200 == code) {

					JSONArray jsArray = (JSONArray) resObj.get("addressarray");
					for (int i = 0; i < jsArray.length(); i++) {
						JSONObject obj = (JSONObject) jsArray.get(i);
						AddressItem shopAddress = new AddressItem();
						shopAddress.AddressID = obj.getLong("AddressID");
						Log.i("MyLog",
								"++)+)+)+)+)+addressid---------------------->"
										+ obj.getLong("AddressID"));
						shopAddress.CityArea = obj.getString("CityArea");
						shopAddress.Detail = obj.getString("Detail");
						shopAddress.IsDefault = obj.getBoolean("IsDefault");
						shopAddress.Recipient = obj.getString("Recipient");
						shopAddress.TelNumber = obj.getString("TelNumber");
						shopAddress.UserID = obj.getLong("UserID");
						ret.add(shopAddress);
					}
					addressAll.addressItems = (ArrayList<AddressItem>) ret;

				}
			}

			Log.i("MyLog",
					"服务端获取的地址数据为数据----"
							+ (addressAll.addressItems.get(0).Recipient));
			return addressAll;

		} catch (Exception ex) {
			ex.printStackTrace();
			LogUtil.d("MyTag", "Remote getAllAddress error: " + ex.getMessage());
		}
		return null;
	}

	// 修改更新收货地址
	public CreateAddress updateAddress(Context c, long addressid, long userid,
			String recipient, String telnumber, String cityarea, String detail,
			boolean isDefault) {

		JSONObject reqObj = new JSONObject();
		JSONObject resObj = null;
		try {
			reqObj.put("addressid", addressid);
			reqObj.put("userid", userid);
			reqObj.put("recipient", recipient);
			reqObj.put("telnumber", telnumber);
			reqObj.put("cityarea", cityarea);
			reqObj.put("detail", detail);
			reqObj.put("isdefault", isDefault);

			resObj = HttpComm.sendJSONToServer(
					Constants.COO8_UPDATE_ADDRESS_URL, reqObj,
					Constants.HTTP_SO_TIMEOUT);
			Log.i("MyLog", "服务端返回的新建地址信息为--======++修改地址为=" + resObj);
			CreateAddress cAddress = new CreateAddress();
			if (resObj == null) {
				return null;
			} else if (resObj != null) {
				cAddress.netInfo = new NetInfo();
				cAddress.netInfo.code = resObj.getInt("code");
				cAddress.netInfo.desc = resObj.getString("desc");
				if (cAddress.netInfo.code == 200) {
					// JSONObject userObj = response.getJSONObject("user") ;

					cAddress.UserID = resObj.getLong("UserID");
					cAddress.AddressID = resObj.getLong("AddressID");
					cAddress.Recipient = resObj.getString("Recipient");
					cAddress.TelNumber = resObj.getString("TelNumber");
					cAddress.CityArea = resObj.getString("CityArea");
					cAddress.Detail = resObj.getString("Detail");
					cAddress.IsDefault = resObj.getBoolean("IsDefault");
					Log.i("MyLog", "服务端返回的新建地址信息为--======" + resObj
							+ cAddress.UserID);
				}
			}
			return cAddress;
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return null;
	}

	// 删除收货地址
	public CreateAddress deleteAddress(Context c, long addressId) {
		LogUtil.d("MyTag", "deleteAddress");
		JSONObject info = new JSONObject();
		JSONObject resObj = null;
		try {
			info.put("addressid", addressId);

			resObj = HttpComm.sendJSONToProductServer(
					Constants.GET_DELETEADDRESS_URL, info,
					Constants.HTTP_SO_TIMEOUT);
			Log.i("MyLog", "#$%^----------------要删除的地址--->" + resObj);
			CreateAddress cAddress = new CreateAddress();
			if (resObj == null) {
				return null;
			} else {

			}

		} catch (Exception ex) {
			ex.printStackTrace();
			// cAddress = null;
		}

		return null;
	}

	// 获取收藏夹
	@Override
	public FavoriteRetInfo getFavoriteList(Context c, long userId,
			int pageindex, int pageSize) {
		// userId = "416798";
		FavoriteRetInfo ret = null;
		try {
			JSONObject reqObj = new JSONObject();
			reqObj.put("userid", userId);
			/*
			 * reqObj.put("pageindex", pageindex); reqObj.put("pagesize",
			 * pageSize);
			 */

			// JSONObject resObj =
			// HttpComm.sendJSONToServer(Constants.GET_FAVORITES_URL, reqObj,
			// Constants.HTTP_SO_TIMEOUT);
			JSONObject resObj = HttpComm.sendJSONToProductServer(
					Constants.GET_FAVORITES_URL, reqObj,
					Constants.HTTP_SO_TIMEOUT);
			if (resObj != null) {
				ret = new FavoriteRetInfo();

				ret.netInfo = new NetInfo();
				ret.netInfo.code = resObj.getInt("code");
				ret.netInfo.desc = resObj.getString("desc");
				if (200 == ret.netInfo.code) {
					JSONArray favoriteArray = resObj
							.getJSONArray("productarray");
					ret.list = new ArrayList<RemoteApi.ProductBase>();
					for (int i = 0; i < favoriteArray.length(); i++) {
						JSONObject pObj = favoriteArray.getJSONObject(i);
						ProductBase item = new ProductBase();
						ProductBase p = new ProductBase();
						p.productId = pObj.getLong("ProductID");
						p.categoryId = pObj.getLong("CategoryID");
						p.productName = pObj.getString("ProductName");
						p.price = pObj.getDouble("UnitPrice");
						p.salePrice = pObj.getDouble("PromotionPrice");
						p.saleWord = pObj.getString("PromotionWord");
						p.dateTime = pObj.getString("CTime");
						p.productPic = new ProductPic();
						JSONObject picObj = pObj.getJSONObject("FrontCover");
						p.productPic.imgId = picObj.getLong("ImageID");
						p.productPic.desc = picObj.getString("Description");
						p.productPic.imgUrl = picObj.getString("Path");
						ret.list.add(p);
					}
				} else {

				}
			}
		} catch (JSONException e) {
			LogUtil.d("MyTag", "JSONException : " + e);
			e.printStackTrace();
		}
		return ret;
	}

	// 删除收藏夹商品
	public NetInfo deleteFromFavorite(Context c, long userId, long productId) {
		NetInfo ret = null;
		try {
			JSONObject reqObj = new JSONObject();
			reqObj.put("userid", userId);
			reqObj.put("productid", productId);

			JSONObject resObj = HttpComm.sendJSONToProductServer(
					Constants.DELETE_FROM_FAVORITE_URL, reqObj,
					Constants.HTTP_SO_TIMEOUT);
			if (resObj != null) {
				ret = new NetInfo();
				ret.code = resObj.getInt("code");
				ret.desc = resObj.getString("desc");
			}
		} catch (JSONException e) {
			e.printStackTrace();
			ret = null;
		}
		return ret;
	}

	// 添加商品到收藏夹
	public NetInfo addtoFavorite(Context c, long userId, long productId) {
		NetInfo ret = null;
		try {
			JSONObject reqObj = new JSONObject();
			reqObj.put("userid", userId);
			reqObj.put("productid", productId);

			JSONObject resObj = HttpComm.sendJSONToProductServer(
					Constants.ADD_TO_FAVORITES_URL, reqObj,
					Constants.HTTP_SO_TIMEOUT);
			if (resObj != null) {
				ret = new NetInfo();
				ret.code = resObj.getInt("code");
				ret.desc = resObj.getString("desc");
			}
		} catch (JSONException e) {
			e.printStackTrace();
			ret = null;
		}
		return ret;
	}

	@Override
	public NetInfo submitFeedback(Context c, long userid, FeadbackInfo info) {

		LogUtil.d("MyTag", "submitFeedback");
		NetInfo ret = null;
		try {
			JSONObject reqObj = new JSONObject();
			reqObj.put("userid", userid);
			reqObj.put("contact", info.contact);
			reqObj.put("body", info.content);
			JSONObject resObj = HttpComm.sendJSONToServer(
					Constants.SUBMIT_FEEDBACK_URL, reqObj,
					Constants.HTTP_SO_TIMEOUT);
			if (resObj != null) {
				ret = new NetInfo();
				ret.code = resObj.getInt("code");
				ret.desc = resObj.getString("desc");
				LogUtil.d("MyTag", "submitFeedback code = " + ret.code);
			}
		} catch (JSONException e) {
			LogUtil.d("MyTag", "JSONException : " + e);
			e.printStackTrace();
			ret = null;
		}
		return ret;
	}

	@Override
	public boolean applicationCanGo(Context ctx) {
		JSONObject reqObj = new JSONObject();
		try {
			PackageManager pm = ctx.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), 0);
			reqObj.put("package", ctx.getPackageName());
			reqObj.put("version", pi.versionName);
			JSONObject obj = HttpComm.sendJSONToServer(
					"memhome/role/role_isValid.action", reqObj,
					Constants.HTTP_SO_TIMEOUT);
			if (obj == null)
				return true;
			return obj.getBoolean("ret");
		} catch (JSONException e) {
			e.printStackTrace();
			return true;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return true;
		}
	}

	public int queryStorage(String productId, String cateId, String cityId) {
		JSONObject reqObj = new JSONObject();
		try {
			reqObj.put("productid", productId);
			reqObj.put("cateid", cateId);
			reqObj.put("cityid", cityId);
			JSONObject resObj = HttpComm.sendJSONToProductServer(
					Constants.COO8_QUERY_STORAGE_URL, reqObj,
					Constants.HTTP_SO_TIMEOUT);
			if (resObj != null) {
				return resObj.getInt("storage");
			} else {
				return -1;
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return -1;
		}

	}

	public ArrayList<TopicsProduct> getTopicsList(String topicId) {
		JSONObject reqObj = new JSONObject();
		try {
			reqObj.put("specailid", topicId);
			JSONObject resObj = HttpComm.sendJSONToProductServer(
					Constants.COO8_TOPICS_URL, reqObj,
					Constants.HTTP_SO_TIMEOUT);
			if (resObj != null) {
				JSONObject dataObj = resObj.getJSONObject("data");
				ArrayList<TopicsProduct> list = new ArrayList<TopicsProduct>();
				JSONArray array = dataObj.getJSONArray("product_list");
				int count = array.length();
				for (int i = 0; i < count; ++i) {
					JSONObject pObj = array.getJSONObject(i);
					TopicsProduct tp = new TopicsProduct();
					tp.productDesc = pObj.getString("name");
					tp.productId = pObj.getString("productid");
					tp.productPrice = pObj.getString("price");
					tp.sign = pObj.getInt("sign");
					tp.imageUrl = pObj.getString("image");
					list.add(tp);
				}
				return list;
			} else {
				return null;
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public ErrorInfo setDefaultAddress(Context c, long userId, String addressid) {
		LogUtil.d("MyTag", "updateInvoice");
		ErrorInfo ret = null;
		try {
			JSONObject reqObj = new JSONObject();
			reqObj.put("userid", userId);
			reqObj.put("addressid", addressid);
			JSONObject resObj = HttpComm.sendJSONToProductServer(
					Constants.COO8_SET_DEFALUT_ADDRESS_URL, reqObj,
					Constants.HTTP_SO_TIMEOUT);
			if (resObj != null) {
				ret = new ErrorInfo();
				ret.code = resObj.getInt("code");
				ret.desc = resObj.getString("desc");
				LogUtil.d("MyTag", "updateInvoice code = " + ret.code);
			}
		} catch (JSONException e) {
			LogUtil.d("MyTag", "JSONException : " + e);
			e.printStackTrace();
			ret = null;
		}
		return ret;
	}

	@Override
	public CheckCode getCheckCode(Context c, String mobile) {
		LogUtil.d("MyTag", "updateInvoice");
		CheckCode mCheckCode = null;
		try {
			JSONObject reqObj = new JSONObject();
			reqObj.put("mobile", mobile);

			JSONObject resObj = HttpComm.sendJSONToServer(
					Constants.CHECKCODE_URL, reqObj, Constants.HTTP_SO_TIMEOUT);
			if (resObj != null) {
				mCheckCode = new CheckCode();
				mCheckCode.checkCode = resObj.getString("checkcode");
				mCheckCode.code = resObj.getInt("code");
				mCheckCode.desc = resObj.getString("desc");
			}
		} catch (JSONException e) {
			LogUtil.d("MyTag", "JSONException : " + e);
			e.printStackTrace();
			mCheckCode = null;
		}
		return mCheckCode;
	}

	@Override
	public ArrayList<ProductMessage> getproductMessage(Context c, int count) {
		// TODO Auto-generated method stub
		JSONObject lastest = new JSONObject();
		JSONObject response = null;

		try {
			lastest.put("pageindex", count);
			response = HttpComm.sendJSONToServer(
					Constants.GET_BUSINESSESEXPRESS, lastest,
					Constants.HTTP_SO_TIMEOUT);

			if (response == null) {
				return null;
			}
			ArrayList<ProductMessage> ret = new ArrayList<ProductMessage>();
			if (200 == response.getInt("code")) {
				JSONArray jsArray = (JSONArray) response.get("messagearray");

				for (int i = 0; i < jsArray.length(); i++) {
					JSONObject obj = (JSONObject) jsArray.get(i);
					ProductMessage app = new ProductMessage();
					app.msgId = obj.getLong("MessageID");
					app.header = obj.getString("Header"); // 标题
					app.body = obj.getString("Body"); // 内容
					String time = obj.getString("PublishTime");
					if (!"".equals(time)) {
						long times = Long.valueOf(obj.getString("PublishTime"));
						app.time = sdf.format(times);
					}
					ret.add(app);
				}
			}
			return ret;

		} catch (Exception ex) {
			ex.printStackTrace();
			LogUtil.d("MyTag", "error: " + ex.getMessage());
			return null;
		}
	}

	@Override
	public ArrayList<PropertyNotificationInfo> getPropertyNotification(
			Context c, int propertyId, int count) {
		// TODO Auto-generated method stub
		JSONObject lastest = new JSONObject();
		JSONObject response = null;

		try {
			lastest.put("propertyid", propertyId);
			lastest.put("pageindex", count);
			response = HttpComm.sendJSONToServer(
					Constants.GET_PROPERTY_NOTIFICATION_URL, lastest,
					Constants.HTTP_SO_TIMEOUT);

			if (response == null) {
				return null;
			}
			ArrayList<PropertyNotificationInfo> ret = null;
			if (200 == response.getInt("code")) {
				ret = new ArrayList<PropertyNotificationInfo>();
				JSONArray jsArray = (JSONArray) response.get("notearray");

				for (int i = 0; i < jsArray.length(); i++) {
					JSONObject obj = (JSONObject) jsArray.get(i);
					PropertyNotificationInfo app = new PropertyNotificationInfo();
					app.noteID = obj.getInt("NoteID");
					app.header = obj.getString("Header"); // 标题
					app.body = obj.getString("Body"); // 内容
					app.priority = obj.getInt("Priority");
					app.signature = obj.getString("Signature");
					String time = obj.getString("CTime");
					if (!"".equals(time)) {
						long times = Long.valueOf(obj.getString("CTime"));
						app.dateTime = sdf.format(times);
					}
					ret.add(app);
				}
			}
			return ret;

		} catch (Exception ex) {
			ex.printStackTrace();
			LogUtil.d("MyTag", "error: " + ex.getMessage());
			return null;
		}
	}

	@Override
	public ArrayList<Note> getNoteMessage(Context c, long userid, int count) {
		// TODO Auto-generated method stub
		JSONObject lastest = new JSONObject();
		JSONObject response = null;

		try {
			lastest.put("userid", userid);
			lastest.put("pageindex", count);
			response = HttpComm.sendJSONToServer(Constants.GET_NOTE_MESSAGE,
					lastest, Constants.HTTP_SO_TIMEOUT);

			if (response == null) {
				return null;
			}
			ArrayList<Note> ret = new ArrayList<Note>();
			if (200 == response.getInt("code")) {
				JSONArray jsArray = (JSONArray) response.get("notearray");

				for (int i = 0; i < jsArray.length(); i++) {
					JSONObject obj = (JSONObject) jsArray.get(i);
					Note app = new Note();
					app.NoteId = obj.getLong("NoteID");
					app.header = obj.getString("Header"); // 标题
					app.body = obj.getString("Body"); // 内容
					String time = obj.getString("CTime");
					if (!"".equals(time)) {
						long times = Long.valueOf(time);
						app.time = sdf.format(times);
					}
					ret.add(app);
				}
			}
			return ret;

		} catch (Exception ex) {
			ex.printStackTrace();
			LogUtil.d("MyTag", "error: " + ex.getMessage());
			return null;
		}
	}

	@Override
	public NetInfo getNotesRemove(Context c, long userid, long noteid) {
		// TODO Auto-generated method stub

		NetInfo mNetInfo = null;
		try {
			JSONObject reqObj = new JSONObject();
			reqObj.put("userid", userid);
			reqObj.put("noteid", noteid);
			JSONObject resObj = HttpComm.sendJSONToServer(
					Constants.NOTESREMOVE_URL, reqObj,
					Constants.HTTP_SO_TIMEOUT);
			if (resObj != null) {
				mNetInfo = new NetInfo();
				mNetInfo.code = resObj.getInt("code");
				mNetInfo.desc = resObj.getString("desc");
			}
		} catch (JSONException e) {
			LogUtil.d("MyTag", "JSONException : " + e);
			e.printStackTrace();
			mNetInfo = null;
		}
		return mNetInfo;
	}

	@Override
	public PersonalInformation getPersonalInformation(Context c, long userid) {
		// TODO Auto-generated method stub
		PersonalInformation mPersonalInformation = null;
		try {
			JSONObject reqObj = new JSONObject();
			reqObj.put("userid", userid);
			JSONObject resObj = HttpComm.sendJSONToServer(
					Constants.GET_PERSONALINFOMATION_URL, reqObj,
					Constants.HTTP_SO_TIMEOUT);
			if (resObj != null) {
				if (200 == resObj.getInt("code")) {
					mPersonalInformation = new PersonalInformation();
					mPersonalInformation.netInfo = new NetInfo();
					mPersonalInformation.netInfo.code = resObj.getInt("code");
					mPersonalInformation.netInfo.desc = resObj
							.getString("desc");
					mPersonalInformation.username = resObj
							.getString("UserName");
					mPersonalInformation.truename = resObj
							.getString("TrueName");
					mPersonalInformation.gender = resObj.getInt("Gender");
					mPersonalInformation.email = resObj.getString("EMail");
					mPersonalInformation.mobile = resObj.getString("Mobile");
					mPersonalInformation.birthday = resObj
							.getString("Birthday");

					mPersonalInformation.provinceid = resObj
							.getString("ProvinceID");
					mPersonalInformation.cityid = resObj.getString("CityID");
					mPersonalInformation.areaid = resObj.getString("AreaID");
					mPersonalInformation.provinceName = resObj
							.getString("ProvinceName");
					mPersonalInformation.cityName = resObj
							.getString("CityName");
					mPersonalInformation.areaName = resObj
							.getString("AreaName");
					mPersonalInformation.phone = resObj.getString("Phone");
					mPersonalInformation.postaddress = resObj
							.getString("PostAddress");
					mPersonalInformation.postcode = resObj
							.getString("PostCode");

				}

			}
		} catch (JSONException e) {
			LogUtil.d("MyTag", "JSONException : " + e);
			e.printStackTrace();
			mPersonalInformation = null;
		}
		return mPersonalInformation;
	}

	@Override
	public NetInfo updatePersonalInformation(Context c, long userid,
			PersonalInformation mPersonalInformation) {
		// TODO Auto-generated method stub

		NetInfo mNetInfo = null;
		try {
			JSONObject reqObj = new JSONObject();
			reqObj.put("userid", userid);
			reqObj.put("username", mPersonalInformation.username);
			reqObj.put("truename", mPersonalInformation.truename);
			reqObj.put("gender", mPersonalInformation.gender);
			reqObj.put("email", mPersonalInformation.email);
			reqObj.put("mobile", mPersonalInformation.mobile);
			reqObj.put("birthday", mPersonalInformation.birthday);
			reqObj.put("provinceid", mPersonalInformation.provinceid);
			reqObj.put("cityid", mPersonalInformation.cityid);
			reqObj.put("areaid", mPersonalInformation.areaid);
			reqObj.put("phone", mPersonalInformation.phone);
			reqObj.put("postaddress", mPersonalInformation.postaddress);
			reqObj.put("postcode", mPersonalInformation.postcode);
			JSONObject resObj = HttpComm.sendJSONToServer(
					Constants.UPDATE_PERSONALINFOMATION_URL, reqObj,
					Constants.HTTP_SO_TIMEOUT);
			if (resObj != null) {
				mNetInfo = new NetInfo();
				mNetInfo.code = resObj.getInt("code");
				mNetInfo.desc = resObj.getString("desc");
			}
		} catch (JSONException e) {
			LogUtil.d("MyTag", "JSONException : " + e);
			e.printStackTrace();
			mNetInfo = null;
		}
		return mNetInfo;
	}

	@Override
	public ArrayList<InformationsInfo> getInformations(Context c, int propertyid) {
		// TODO Auto-generated method stub
		JSONObject lastest = new JSONObject();
		JSONObject response = null;

		try {
			lastest.put("propertyid", propertyid);
			response = HttpComm.sendJSONToServer(Constants.GET_INFOMATIONS_URL,
					lastest, Constants.HTTP_SO_TIMEOUT);

			if (response == null) {
				return null;
			}
			ArrayList<InformationsInfo> ret = new ArrayList<InformationsInfo>();
			if (200 == response.getInt("code")) {
				JSONArray jsArray = (JSONArray) response
						.get("informationarray");

				for (int i = 0; i < jsArray.length(); i++) {
					JSONObject obj = (JSONObject) jsArray.get(i);
					InformationsInfo informationsInfo = new InformationsInfo();
					informationsInfo.informationID = obj
							.getInt("InformationID");
					informationsInfo.header = obj.getString("Header"); // 标题
					informationsInfo.body = obj.getString("Body"); // 内容
					String time = obj.getString("CTime");
					if (!"".equals(time)) {
						long times = Long.valueOf(time);
						informationsInfo.dateTime = sdf.format(times);
					}
					ret.add(informationsInfo);
				}
			}
			return ret;

		} catch (Exception ex) {
			ex.printStackTrace();
			LogUtil.d("MyTag", "error: " + ex.getMessage());
			return null;
		}
	}

	@Override
	public ArrayList<PaidServicesInfo> getPaidServices(Context c, int propertyid) {
		// TODO Auto-generated method stub
		JSONObject lastest = new JSONObject();
		JSONObject response = null;

		try {
			lastest.put("propertyid", propertyid);
			response = HttpComm.sendJSONToServer(
					Constants.GET_PAID_SERVICES_URL, lastest,
					Constants.HTTP_SO_TIMEOUT);

			if (response == null) {
				return null;
			}
			ArrayList<PaidServicesInfo> ret = new ArrayList<PaidServicesInfo>();
			if (200 == response.getInt("code")) {
				JSONArray jsArray = (JSONArray) response.get("servicearray");

				for (int i = 0; i < jsArray.length(); i++) {
					JSONObject obj = (JSONObject) jsArray.get(i);
					PaidServicesInfo paidServicesInfo = new PaidServicesInfo();
					paidServicesInfo.serviceID = obj.getInt("ServiceID");
					paidServicesInfo.serviceName = obj.getString("ServiceName");
					paidServicesInfo.description = obj.getString("Description");
					paidServicesInfo.price = obj.getString("Price");
					String time = obj.getString("CTime");
					if (!"".equals(time)) {
						long times = Long.valueOf(time);
						paidServicesInfo.dateTime = sdf.format(times);
					}
					ret.add(paidServicesInfo);
				}
			}
			return ret;

		} catch (Exception ex) {
			ex.printStackTrace();
			LogUtil.d("MyTag", "error: " + ex.getMessage());
			return null;
		}
	}

	@Override
	public NetInfo sendPropertyCollection(Context c, long userId,
			long propertyid, String comment) {
		// TODO Auto-generated method stub
		JSONObject send = new JSONObject();
		JSONObject response = null;
		NetInfo ret = null;
		ret = new NetInfo();
		try {
			send.put("userid", userId);
			send.put("propertyid", propertyid);
			send.put("comment", comment);
			response = HttpComm.sendJSONToServer(
					Constants.SEND_PROPERTY_COLLECTION_URL, send,
					Constants.HTTP_SO_TIMEOUT);
			if (response == null) {
				return null;
			}

			ret.code = response.getInt("code");
			ret.desc = response.getString("desc");
			return ret;

		} catch (Exception ex) {
			ex.printStackTrace();
			LogUtil.d("MyTag",
					"Remote modifyPassword error: " + ex.getMessage());

			ret.code = 4;
			ret.desc = "";
			return ret;
		}
	}

	@Override
	public ArrayList<ExpressService> getPropertyCollection(Context c,
			int propertyId, long userId, int pageIndex) {
		// TODO Auto-generated method stub
		JSONObject lastest = new JSONObject();
		JSONObject response = null;

		try {
			lastest.put("propertyid", propertyId);
			lastest.put("userid", userId);
			lastest.put("pageindex", pageIndex);
			response = HttpComm.sendJSONToServer(
					Constants.GET_PROPERTY_COLLECTION_URL, lastest,
					Constants.HTTP_SO_TIMEOUT);

			if (response == null) {
				return null;
			}
			ArrayList<ExpressService> ret = new ArrayList<ExpressService>();
			if (200 == response.getInt("code")) {
				JSONArray jsArray = (JSONArray) response
						.get("expressservicearray");

				for (int i = 0; i < jsArray.length(); i++) {
					JSONObject obj = (JSONObject) jsArray.get(i);
					ExpressService expressService = new ExpressService();
					expressService.expressServiceID = obj
							.getInt("ExpressServiceID");
					expressService.comment = obj.getString("Comment");
					expressService.statusName = obj.getString("StatusName");
					String time = obj.getString("CTime");
					String rTime = obj.getString("RTime");
					SimpleDateFormat sdformat = new SimpleDateFormat("HH:mm:ss");
					SimpleDateFormat ctformat = new SimpleDateFormat(
							"yyyy-MM-dd");
					if (!"".equals(time)) {

						expressService.CTime = ctformat.format(Long
								.valueOf(time));
					}
					if (!"".equals(rTime)) {
						expressService.RTime = sdformat.format(Long
								.valueOf(rTime));
					}
					ret.add(expressService);
				}
			}
			return ret;

		} catch (Exception ex) {
			ex.printStackTrace();
			LogUtil.d("MyTag", "error: " + ex.getMessage());
			return null;
		}
	}

	@Override
	public ArrayList<MessageTypeInfo> getMessageType(Context c, int propertyId) {
		// TODO Auto-generated method stub
		JSONObject lastest = new JSONObject();
		JSONObject response = null;

		try {
			lastest.put("propertyid", propertyId);
			response = HttpComm.sendJSONToServer(
					Constants.GET_MESSAGE_TYPE_URL, lastest,
					Constants.HTTP_SO_TIMEOUT);
			Log.i("MyLog", "MessageTypeInfo" + response);
			if (response == null) {
				return null;
			}
			ArrayList<MessageTypeInfo> ret = new ArrayList<MessageTypeInfo>();
			if (200 == response.getInt("code")) {
				JSONArray jsArray = (JSONArray) response
						.get("messagetypearray");

				for (int i = 0; i < jsArray.length(); i++) {
					JSONObject obj = (JSONObject) jsArray.get(i);
					MessageTypeInfo messageType = new MessageTypeInfo();
					messageType.messageTypeID = obj.getInt("MessageTypeID");
					messageType.messageTypeName = obj
							.getString("MessageTypeName");
					ret.add(messageType);
				}
			}
			return ret;

		} catch (Exception ex) {
			ex.printStackTrace();
			LogUtil.d("MyTag", "error: " + ex.getMessage());
			return null;
		}
	}

	@Override
	public NetInfo sendMessage(Context c, long userId, int propertyid,
			int messagetypeid, String header, String body) {
		// TODO Auto-generated method stub
		JSONObject send = new JSONObject();
		JSONObject response = null;
		NetInfo ret = null;
		ret = new NetInfo();
		try {
			send.put("userid", userId);
			send.put("propertyid", propertyid);
			send.put("messagetypeid", messagetypeid);
			send.put("header", header);
			send.put("body", body);
			response = HttpComm.sendJSONToServer(Constants.SEND_MESSAGE_URL,
					send, Constants.HTTP_SO_TIMEOUT);
			if (response == null) {
				return null;
			}

			ret.code = response.getInt("code");
			ret.desc = response.getString("desc");
			return ret;

		} catch (Exception ex) {
			ex.printStackTrace();
			LogUtil.d("MyTag",
					"Remote modifyPassword error: " + ex.getMessage());

			ret.code = 4;
			ret.desc = "";
			return ret;
		}
	}

	@Override
	public NetInfo sendMessageReply(Context c, long userId, int propertyid,
			int rootid, String header, String body) {
		// TODO Auto-generated method stub
		JSONObject send = new JSONObject();
		JSONObject response = null;
		NetInfo ret = null;
		ret = new NetInfo();
		try {
			send.put("userid", userId);
			send.put("propertyid", propertyid);
			send.put("rootid", rootid);
			send.put("header", header);
			send.put("body", body);
			response = HttpComm.sendJSONToServer(
					Constants.SEND_MESSAGE_REPLY_URL, send,
					Constants.HTTP_SO_TIMEOUT);
			if (response == null) {
				return null;
			}

			ret.code = response.getInt("code");
			ret.desc = response.getString("desc");
			return ret;

		} catch (Exception ex) {
			ex.printStackTrace();
			LogUtil.d("MyTag",
					"Remote modifyPassword error: " + ex.getMessage());

			ret.code = 4;
			ret.desc = "";
			return ret;
		}
	}

	@Override
	public ArrayList<MessageInfo> getMessageInbox(Context c, int propertyId,
			long userId, int pageIndex) {
		// TODO Auto-generated method stub
		JSONObject lastest = new JSONObject();
		JSONObject response = null;

		try {
			lastest.put("propertyid", propertyId);
			lastest.put("userid", userId);
			lastest.put("pageindex", pageIndex);
			response = HttpComm.sendJSONToServer(
					Constants.SEND_MESSAGE_IN_BOX_URL, lastest,
					Constants.HTTP_SO_TIMEOUT);

			if (response == null) {
				return null;
			}
			ArrayList<MessageInfo> ret = new ArrayList<MessageInfo>();
			if (200 == response.getInt("code")) {
				JSONArray jsArray = (JSONArray) response.get("messagearray");

				for (int i = 0; i < jsArray.length(); i++) {
					JSONObject obj = (JSONObject) jsArray.get(i);
					MessageInfo messageInfo = new MessageInfo();
					messageInfo.messageID = obj.getLong("MessageID");
					MessageTypeInfo messageTypeInfo = new MessageTypeInfo();
					JSONObject picObj = obj.getJSONObject("Type");
					messageTypeInfo.messageTypeID = picObj
							.getInt("MessageTypeID");
					messageTypeInfo.messageTypeName = picObj
							.getString("MessageTypeName");
					messageInfo.type = messageTypeInfo;
					messageInfo.header = obj.getString("Header");
					messageInfo.body = obj.getString("Body");
					messageInfo.isRead = obj.getInt("isRead");
					String time = obj.getString("CTime");
					if (!"".equals(time)) {
						long times = Long.valueOf(time);
						messageInfo.cTime = sdf.format(times);
					}
					ret.add(messageInfo);
				}
			}
			return ret;

		} catch (Exception ex) {
			ex.printStackTrace();
			LogUtil.d("MyTag", "error: " + ex.getMessage());
			return null;
		}
	}

	@Override
	public ArrayList<MessageInfo> getMessageOutbox(Context c, int propertyId,
			long userId, int pageIndex) {
		// TODO Auto-generated method stub
		JSONObject lastest = new JSONObject();
		JSONObject response = null;

		try {
			lastest.put("propertyid", propertyId);
			lastest.put("userid", userId);
			lastest.put("pageindex", pageIndex);
			response = HttpComm.sendJSONToServer(
					Constants.SEND_MESSAGE_OUT_BOX_URL, lastest,
					Constants.HTTP_SO_TIMEOUT);
			Log.i("MyLog", "messageInfo=" + response);
			if (response == null) {
				return null;
			}
			ArrayList<MessageInfo> ret = new ArrayList<MessageInfo>();
			if (200 == response.getInt("code")) {
				JSONArray jsArray = (JSONArray) response.get("messagearray");

				for (int i = 0; i < jsArray.length(); i++) {
					JSONObject obj = (JSONObject) jsArray.get(i);
					MessageInfo messageInfo = new MessageInfo();
					messageInfo.messageID = obj.getLong("MessageID");
					MessageTypeInfo messageTypeInfo = new MessageTypeInfo();
					JSONObject picObj = obj.getJSONObject("Type");
					messageTypeInfo.messageTypeID = picObj
							.getInt("MessageTypeID");
					messageTypeInfo.messageTypeName = picObj
							.getString("MessageTypeName");
					messageInfo.type = messageTypeInfo;
					messageInfo.header = obj.getString("Header");
					messageInfo.body = obj.getString("Body");
					messageInfo.isRead = obj.getInt("isRead");
					messageInfo.RootID = obj.getInt("RootID");
					String time = obj.getString("CTime");
					if (!"".equals(time)) {
						long times = Long.valueOf(time);
						messageInfo.cTime = sdf.format(times);
					}
					ret.add(messageInfo);
				}
			}
			return ret;

		} catch (Exception ex) {
			ex.printStackTrace();
			LogUtil.d("MyTag", "error: " + ex.getMessage());
			return null;
		}
	}

	@Override
	public ArrayList<AppPopularize> getAppPopularize(Context c) {
		// TODO Auto-generated method stub
		JSONObject lastest = new JSONObject();
		JSONObject response = null;
		ArrayList<AppPopularize> ret;
		try {
			response = HttpComm.sendJSONToServer(
					Constants.GET_YINGYONGTUIJIAN_GET, lastest,
					Constants.HTTP_SO_TIMEOUT);

			if (response == null) {
				return null;
			}
			ret = new ArrayList<AppPopularize>();
			if (200 == response.getInt("code")) {
				JSONArray jsArray = (JSONArray) response.get("apppackagearray");

				for (int i = 0; i < jsArray.length(); i++) {
					JSONObject obj = (JSONObject) jsArray.get(i);
					AppPopularize messageInfo = new AppPopularize();
					messageInfo.appID = obj.getLong("appID");
					messageInfo.iconPath = obj.getString("iconPath");
					messageInfo.appName = obj.getString("appName");
					messageInfo.appDescription = obj
							.getString("appDescription");
					messageInfo.url = obj.getString("url");
					messageInfo.priority = obj.getInt("priority");
					ret.add(messageInfo);
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			LogUtil.d("MyTag", "error: " + ex.getMessage());
			return null;
		}
		return ret;
	}

	@Override
	public NetInfo sendMessageFromFlea(int propertyid, long userid,
			long fleaid, String body) {
		// TODO Auto-generated method stub
		JSONObject send = new JSONObject();
		JSONObject response = null;
		NetInfo ret = null;
		ret = new NetInfo();
		try {
			send.put("userid", userid);
			send.put("propertyid", propertyid);
			send.put("fleaid", fleaid);
			send.put("body", body);
			response = HttpComm.sendJSONToServer(
					Constants.SEND_MESSAGE_FROM_FLEA_URL, send,
					Constants.HTTP_SO_TIMEOUT);
			if (response == null) {
				return null;
			}

			ret.code = response.getInt("code");
			ret.desc = response.getString("desc");
			return ret;

		} catch (Exception ex) {
			ex.printStackTrace();
			LogUtil.d("MyTag",
					"Remote modifyPassword error: " + ex.getMessage());

			ret.code = 4;
			ret.desc = "";
			return ret;
		}
	}

	@Override
	public NetInfo sendFleaNew(int propertyid, long userid, String header,
			String description, File pictures) {
		// TODO Auto-generated method stub
		JSONObject response = null;
		NetInfo ret = null;
		ret = new NetInfo();
		try {
			response = HttpComm.uploadFile(Constants.SEND_NEW_FLEA_URL, 0,
					propertyid, userid, header, description, "", pictures,
					Constants.HTTP_SO_TIMEOUT);
			if (response == null) {
				return null;
			}

			ret.code = response.getInt("code");
			ret.desc = response.getString("desc");
			return ret;

		} catch (Exception ex) {
			ex.printStackTrace();
			LogUtil.d("MyTag",
					"Remote modifyPassword error: " + ex.getMessage());

			ret.code = 4;
			ret.desc = "";
			return ret;
		}
	}

	@Override
	public NetInfo editFleaNew(long fleaid, int propertyid, long userid,
			String header, String description, String deletelist, File pictures) {
		// TODO Auto-generated method stub
		JSONObject send = new JSONObject();
		JSONObject response = null;
		NetInfo ret = null;
		ret = new NetInfo();
		try {
			response = HttpComm.uploadFile(Constants.EDIT_FLED_URL, fleaid,
					propertyid, userid, header, description, deletelist,
					pictures, Constants.HTTP_SO_TIMEOUT);
			if (response == null) {
				return null;
			}

			ret.code = response.getInt("code");
			ret.desc = response.getString("desc");
			return ret;

		} catch (Exception ex) {
			ex.printStackTrace();
			LogUtil.d("MyTag",
					"Remote modifyPassword error: " + ex.getMessage());

			ret.code = 4;
			ret.desc = "";
			return ret;
		}
	}

	@Override
	public NetInfo deleteFleaNew(long fleaid, int propertyid, long userid) {
		// TODO Auto-generated method stub
		JSONObject send = new JSONObject();
		JSONObject response = null;
		NetInfo ret = null;
		ret = new NetInfo();
		try {
			send.put("fleaid", fleaid);
			send.put("propertyid", propertyid);
			send.put("userid", userid);
			response = HttpComm.sendJSONToServer(Constants.DELETE_FLEA_URL,
					send, Constants.HTTP_SO_TIMEOUT);
			if (response == null) {
				return null;
			}

			ret.code = response.getInt("code");
			ret.desc = response.getString("desc");
			return ret;

		} catch (Exception ex) {
			ex.printStackTrace();
			LogUtil.d("MyTag",
					"Remote modifyPassword error: " + ex.getMessage());

			ret.code = 4;
			ret.desc = "";
			return ret;
		}
	}

	// 新建订单

	// 获取跳骚商品列表
	@Override
	public FleaInfo getFleaByProperty(int propertyid, int pageindex) {
		// TODO Auto-generated method stub
		JSONObject lastest = new JSONObject();
		JSONObject response = null;

		try {
			lastest.put("propertyid", propertyid);
			lastest.put("pageindex", pageindex);
			response = HttpComm.sendJSONToServer(
					Constants.GET_FLEA_BY_PROPERTY_URL, lastest,
					Constants.HTTP_SO_TIMEOUT);
			Log.i("MyLog", "跳蚤市场的商品为-----" + response);
			if (response == null) {
				return null;
			}
			FleaInfo fleaInfo = new FleaInfo();
			NetInfo netInfo = new NetInfo();
			netInfo.code = response.getInt("code");
			netInfo.desc = response.getString("desc");
			fleaInfo.netInfo = netInfo;
			if (200 == response.getInt("code")) {

				ArrayList<Flea> ret = new ArrayList<Flea>();
				JSONArray jsArray = (JSONArray) response.get("fleaarray");

				for (int i = 0; i < jsArray.length(); i++) {
					JSONObject obj = (JSONObject) jsArray.get(i);
					Flea flea = new Flea();

					flea.fleaID = obj.getLong("FleaID");
					flea.userID = obj.getLong("UserID");
					flea.header = obj.getString("Header");
					flea.description = obj.getString("Description");
					String time = obj.getString("CTime");
					if (!"".equals(time)) {
						long times = Long.valueOf(time);
						flea.cTime = sdf.format(times);
					}
					FleaPicture fleaPictureInfo = new FleaPicture();
					JSONObject picObj = obj.getJSONObject("FrontCover");
					fleaPictureInfo.fleaPictureID = picObj
							.getInt("FleaPictureID");
					fleaPictureInfo.path = picObj.getString("path");
					fleaPictureInfo.description = picObj
							.getString("Description");
					flea.frontCover = fleaPictureInfo;
					ret.add(flea);
				}
				fleaInfo.fleas = ret;
			}
			return fleaInfo;

		} catch (Exception ex) {
			ex.printStackTrace();
			LogUtil.d("MyTag", "error: " + ex.getMessage());
			return null;
		}

	}

	@Override
	public FleaInfo getFleaByOwner(int propertyid, long userid) {
		// TODO Auto-generated method stub
		JSONObject lastest = new JSONObject();
		JSONObject response = null;

		try {
			lastest.put("propertyid", propertyid);
			lastest.put("userid", userid);
			response = HttpComm.sendJSONToServer(
					Constants.GET_FLEA_GET_OWNER_URL, lastest,
					Constants.HTTP_SO_TIMEOUT);

			if (response == null) {
				return null;
			}
			FleaInfo fleaInfo = new FleaInfo();
			NetInfo netInfo = new NetInfo();
			netInfo.code = response.getInt("code");
			netInfo.desc = response.getString("desc");
			fleaInfo.netInfo = netInfo;
			if (200 == response.getInt("code")) {

				ArrayList<Flea> ret = new ArrayList<Flea>();
				JSONArray jsArray = (JSONArray) response.get("fleaarray");

				for (int i = 0; i < jsArray.length(); i++) {
					JSONObject obj = (JSONObject) jsArray.get(i);
					Flea flea = new Flea();

					flea.fleaID = obj.getLong("FleaID");
					flea.userID = obj.getLong("UserID");
					flea.header = obj.getString("Header");
					flea.description = obj.getString("Description");
					String time = obj.getString("CTime");
					if (!"".equals(time)) {
						long times = Long.valueOf(time);
						flea.cTime = sdf.format(times);
					}
					FleaPicture fleaPictureInfo = new FleaPicture();
					JSONObject picObj = obj.getJSONObject("FrontCover");
					fleaPictureInfo.fleaPictureID = picObj
							.getInt("FleaPictureID");
					fleaPictureInfo.path = picObj.getString("path");
					fleaPictureInfo.description = picObj
							.getString("Description");
					flea.frontCover = fleaPictureInfo;
					ret.add(flea);
				}
				fleaInfo.fleas = ret;
			}
			return fleaInfo;

		} catch (Exception ex) {
			ex.printStackTrace();
			LogUtil.d("MyTag", "error: " + ex.getMessage());
			return null;
		}
	}

	@Override
	public FleaContent getFleaContent(int propertyid, long fleaid) {
		// TODO Auto-generated method stub
		JSONObject lastest = new JSONObject();
		JSONObject response = null;
		FleaPicture fleaPictureInfo = null;
		FleaContent fleaContentInfo = null;
		try {
			lastest.put("propertyid", propertyid);
			lastest.put("fleaid", fleaid);
			response = HttpComm.sendJSONToServer(Constants.GET_FLEA_URL,
					lastest, Constants.HTTP_SO_TIMEOUT);

			if (response == null) {
				return null;
			}
			ArrayList<FleaPicture> ret = new ArrayList<FleaPicture>();
			if (200 == response.getInt("code")) {
				fleaContentInfo = new FleaContent();
				NetInfo netInfo = new NetInfo();
				netInfo.code = response.getInt("code");
				netInfo.desc = response.getString("desc");
				fleaContentInfo.netInfo = netInfo;
				fleaContentInfo.username = response.getString("username");
				Flea fleaInfo = new Flea();
				JSONObject fleaObj = response.getJSONObject("flea");
				fleaInfo.fleaID = fleaObj.getLong("FleaID");
				fleaInfo.userID = fleaObj.getLong("UserID");
				fleaInfo.header = fleaObj.getString("Header");
				fleaInfo.description = fleaObj.getString("Description");
				String time = fleaObj.getString("CTime");
				if (!"".equals(time)) {
					long times = Long.valueOf(time);
					fleaInfo.cTime = sdf.format(times);
				}
				fleaPictureInfo = new FleaPicture();

				JSONObject picObj = fleaObj.getJSONObject("FrontCover");
				fleaPictureInfo.fleaPictureID = picObj.getInt("FleaPictureID");
				fleaPictureInfo.path = picObj.getString("path");
				fleaPictureInfo.description = picObj.getString("Description");
				fleaInfo.frontCover = fleaPictureInfo;
				fleaContentInfo.flea = fleaInfo;

				JSONArray jsArray = (JSONArray) response
						.get("fleaPictureArray");

				for (int i = 0; i < jsArray.length(); i++) {
					JSONObject obj = (JSONObject) jsArray.get(i);
					fleaPictureInfo = new FleaPicture();

					fleaPictureInfo.fleaPictureID = obj
							.getLong("FleaPictureID");
					fleaPictureInfo.path = obj.getString("path");
					fleaPictureInfo.description = obj.getString("Description");

					ret.add(fleaPictureInfo);
				}
				fleaContentInfo.fleaPictureArray = ret;
			}
			return fleaContentInfo;

		} catch (Exception ex) {
			ex.printStackTrace();
			LogUtil.d("MyTag", "error: " + ex.getMessage());
			return null;
		}
	}

	@Override
	public ArrayList<MessageInfo> getMessageInboxContent(Context c,
			int propertyId, long userId, long rootid) {
		// TODO Auto-generated method stub
		JSONObject lastest = new JSONObject();
		JSONObject response = null;

		try {
			lastest.put("propertyid", propertyId);
			lastest.put("userid", userId);
			lastest.put("rootid", rootid);
			response = HttpComm.sendJSONToServer(
					Constants.SEND_MESSAGE_IN_BOX_CONTENT_URL, lastest,
					Constants.HTTP_SO_TIMEOUT);

			if (response == null) {
				return null;
			}
			ArrayList<MessageInfo> ret = new ArrayList<MessageInfo>();
			if (200 == response.getInt("code")) {
				JSONArray jsArray = (JSONArray) response.get("messagearray");

				for (int i = 0; i < jsArray.length(); i++) {
					JSONObject obj = (JSONObject) jsArray.get(i);
					MessageInfo messageInfo = new MessageInfo();
					messageInfo.messageID = obj.getLong("MessageID");
					MessageTypeInfo messageTypeInfo = new MessageTypeInfo();
					JSONObject picObj = obj.getJSONObject("Type");
					messageTypeInfo.messageTypeID = picObj
							.getInt("MessageTypeID");
					messageTypeInfo.messageTypeName = picObj
							.getString("MessageTypeName");
					messageInfo.type = messageTypeInfo;
					messageInfo.userid = obj.getLong("UserID");
					messageInfo.toUserId = obj.getLong("ToUserID");
					messageInfo.header = obj.getString("Header");
					messageInfo.body = obj.getString("Body");
					messageInfo.isRead = obj.getInt("isRead");
					String time = obj.getString("CTime");
					if (!"".equals(time)) {
						long times = Long.valueOf(time);
						messageInfo.cTime = sdf.format(times);
					}
					ret.add(messageInfo);
				}
			}
			return ret;

		} catch (Exception ex) {
			ex.printStackTrace();
			LogUtil.d("MyTag", "error: " + ex.getMessage());
			return null;
		}
	}

	@Override
	public LivingPayList getLivingPayList(Context c, int propertyid,
			String livingcategoryid, int pageindex) {

		JSONObject obj = new JSONObject();
		JSONObject response = null;

		return null;
	}

	@Override
	public LivingItemInfo getLivingItems(Context c, int propertyid,
			String livingcategoryid, int pageindex,double latitude,double longitude) {
		// TODO Auto-generated method stub
		JSONObject lastest = new JSONObject();
		JSONObject response = null;

		try {
			lastest.put("propertyid", propertyid);
			lastest.put("livingcategoryid", livingcategoryid);
			lastest.put("pageindex", pageindex);
			lastest.put("latitude", latitude);
			lastest.put("longitude", longitude);
			//获取生活项列表
			response = HttpComm.sendJSONToServer(Constants.LIVING_ITEMS,
					lastest, Constants.HTTP_SO_TIMEOUT);

			Log.i("MyLog", "获得生活内容为-----" + response);
			if (response == null) {
				return null;
			}
			LivingItemInfo livingItemInfo = new LivingItemInfo();
			ArrayList<LivingItem> ret = new ArrayList<LivingItem>();
			if (200 == response.getInt("code")) {
				JSONArray jsArray = (JSONArray) response.get("livingitemarray");

				for (int i = 0; i < jsArray.length(); i++) {
					JSONObject obj = (JSONObject) jsArray.get(i);
					LivingItem livingItem = new LivingItem();
					livingItem.LivingItemID = obj.getInt("LivingItemID");
					livingItem.LivingItemName = obj.getString("LivingItemName");
					livingItem.address = obj.getString("address");
					livingItem.telephone = obj.getString("telephone");
					livingItem.categories = obj.getString("categories");
					livingItem.distance = obj.getInt("distance");
					livingItem.avg_price = obj.getInt("avg_price");
					livingItem.has_activity = obj.getInt("has_activity");
					livingItem.has_coupon = obj.getInt("has_coupon");
					try {
						if (livingItem.has_coupon > 0) {
							//含优惠券
							Coupon coupon = new Coupon();
							JSONObject couObj = obj.getJSONObject("coupon");
							coupon.coupon_id = couObj.getInt("coupon_id");
							coupon.coupon_description = couObj
									.getString("coupon_description");
							coupon.coupon_url = couObj.getString("coupon_url");
							livingItem.coupon = coupon;
						}
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}
					try {
						livingItem.has_deal = obj.getInt("has_deal");
						if (livingItem.has_deal > 0) {
							//有团购
							ArrayList<Deal> deals = new ArrayList<Deal>();
							JSONArray dealsArray = (JSONArray) obj.get("deals");
							for (int j = 0; j < dealsArray.length(); j++) {
								JSONObject dealObj = (JSONObject) dealsArray
										.get(j);
								Deal deal = new Deal();
								deal.id = dealObj.getString("id");
								deal.description = dealObj
										.getString("description");
								deal.url = dealObj.getString("url");
								deals.add(deal);
							}
							livingItem.deals = deals;
						}
					} catch (Exception e) {
						// TODO: handle exception
					}
					livingItem.source = obj.getString("source");
					livingItem.Description = obj.getString("Description");
					LivingItemPicture livingItemPicture = new LivingItemPicture();

					JSONObject picObj = obj.getJSONObject("FrontCover");
					livingItemPicture.LivingItemPictureID = picObj
							.getLong("LivingItemPictureID");
					livingItemPicture.path = picObj.getString("path");
					livingItemPicture.Description = picObj
							.getString("Description");
					livingItem.FrontCover = livingItemPicture;

					String time = obj.getString("CTime");
					if (!"".equals(time)) {
						long times = Long.valueOf(time);
						livingItem.Ctime = sdf.format(times);
					}
					ret.add(livingItem);
				}
				livingItemInfo.netInfo = new NetInfo();
				livingItemInfo.netInfo.code = response.getInt("code");
				livingItemInfo.netInfo.desc = response.getString("desc");
				livingItemInfo.livingItem = ret;
			}
			Log.i("MyLog", "获取的生活项内容" + response);
			return livingItemInfo;

		} catch (Exception ex) {
			ex.printStackTrace();
			LogUtil.d("MyTag", "error: " + ex.getMessage());
			return null;
		}
	}

	@Override
	public LivingItem getLivingItemsByLivingItemId(Context c, int livingitemid,
			String source, int propertyid,double latitude,double longitude) {
		// TODO Auto-generated method stub\

		JSONObject lastest = new JSONObject();
		JSONObject response = null;
		try {
			lastest.put("livingitemid", livingitemid);
			lastest.put("source", source);
			lastest.put("propertyid", propertyid);
			lastest.put("latitude", latitude);
			lastest.put("longitude",longitude);
			response = HttpComm.sendJSONToServer(Constants.GET_LIVING_ITEM_GET,
					lastest, Constants.HTTP_SO_TIMEOUT);
			Log.i("MyLog", "latitude = "+latitude+"/"+longitude);
			if (response == null) {
				return null;
			}
			LivingItem livingItem = new LivingItem();
			if (200 == response.getInt("code")) {
				Log.d("MyTag", "Constants.GET_LIVING_ITEM_GET- 2378"
						+ Constants.GET_LIVING_ITEM_GET);

				Log.d("MyTag", "getLivingItemsByLivingItemId- 2378 distance"
						+ response.getInt("distance"));

				livingItem.LivingItemID = response.getInt("LivingItemID");
				livingItem.LivingItemName = response
						.getString("LivingItemName");
				livingItem.address = response.getString("address");
				livingItem.telephone = response.getString("telephone");
				livingItem.categories = response.getString("categories");
				livingItem.distance = response.getInt("distance");
				livingItem.avg_price = response.getInt("avg_price");
				livingItem.has_coupon = response.getInt("has_coupon");
				try {
					if (livingItem.has_coupon > 0) {
						Coupon coupon = new Coupon();
						JSONObject couObj = response.getJSONObject("coupon");
						coupon.coupon_id = couObj.getInt("coupon_id");
						coupon.coupon_description = couObj
								.getString("coupon_description");
						coupon.coupon_url = couObj.getString("coupon_url");
						livingItem.coupon = coupon;
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
				livingItem.has_deal = response.getInt("has_deal");
				try {
					if (livingItem.has_deal > 0) {
						ArrayList<Deal> deals = new ArrayList<Deal>();
						JSONArray dealsArray = (JSONArray) response
								.get("deals");
						for (int j = 0; j < dealsArray.length(); j++) {
							JSONObject dealObj = (JSONObject) dealsArray.get(j);
							Deal deal = new Deal();
							deal.id = dealObj.getString("id");
							deal.description = dealObj.getString("description");
							deal.url = dealObj.getString("url");
							deals.add(deal);
						}
						livingItem.deals = deals;
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
				livingItem.source = response.getString("source");
				livingItem.Description = response.getString("Description");
				LivingItemPicture livingItemPicture = new LivingItemPicture();

				JSONObject picObj = response.getJSONObject("FrontCover");
				livingItemPicture.LivingItemPictureID = picObj
						.getLong("LivingItemPictureID");
				livingItemPicture.path = picObj.getString("path");
				livingItemPicture.Description = picObj.getString("Description");
				livingItem.FrontCover = livingItemPicture;

				String time = response.getString("CTime");
				try {
					livingItem.latitude = (float) response
							.getDouble("latitude");
					livingItem.longitude = (float) response
							.getDouble("longitude");
					livingItem.Hours = response.getString("Hours");
					livingItem.ForExpress = response.getBoolean("ForExpress");
				} catch (Exception e) {
					// TODO: handle exception
				}
				if (!"".equals(time)) {
					long times = Long.valueOf(time);
					livingItem.Ctime = sdf.format(times);
				}
			}
			return livingItem;

		} catch (Exception ex) {
			ex.printStackTrace();
			LogUtil.d("MyTag", "error: " + ex.getMessage());
			return null;
		}
	}

	@Override
	public PromotionList getActicityByLivingItemId(Context c, int livingitemid) {
		// TODO Auto-generated method stub
		JSONObject lastest = new JSONObject();
		JSONObject response = null;
		try {
			lastest.put("livingitemid", livingitemid);
			response = HttpComm.sendJSONToServer(
					Constants.GET_PROMOTTION_ACTIVITY_GET, lastest,
					Constants.HTTP_SO_TIMEOUT);
			Log.i("MyLog", "商品列表信息为——————" + response);
			if (response == null) {
				return null;
			}
			PromotionList livingItem = new PromotionList();
			livingItem.promotionList = new ArrayList<Promotion>();
			if (200 == response.getInt("code")) {
				JSONArray jsArray = (JSONArray) response.get("promotionarray");

				for (int i = 0; i < jsArray.length(); i++) {
					JSONObject obj = (JSONObject) jsArray.get(i);
					Promotion promotionItem = new Promotion();
					promotionItem.body = obj.getString("body");
					promotionItem.LivingItemID = obj.getInt("LivingItemID");
					promotionItem.Price = obj.getDouble("Price");
					Log.i("MyLog",
							"lobj.getDouble(Price" + obj.getDouble("Price"));
					Log.i("MyLog", "promotionprice=" + promotionItem.Price);
					promotionItem.path = obj.getString("path");
					promotionItem.header = obj.getString("header");
					promotionItem.Priority = obj.getInt("Priority");
					promotionItem.SaleTypeID = obj.getInt("SaleTypeID");
					promotionItem.Height = obj.getInt("Height");
					promotionItem.Width = obj.getInt("Width");
					promotionItem.ForSal = obj.getBoolean("ForSale");
					promotionItem.PromotionID = obj.getLong("PromotionID");
					livingItem.promotionList.add(promotionItem);

					// Log.i("Mylog", "response"+promotionItem.PromotionID);
				}
			}

			Log.i("MyLog", "livingItemId==================" + response);
			return livingItem;

		} catch (Exception ex) {
			ex.printStackTrace();
			LogUtil.d("MyTag", "error: " + ex.getMessage());
			return null;
		}
	}

	//获取所有活动的商品信息
	@Override
	public PromotionList getProductByLivingItemId(Context c, int livingitemid) {
		// TODO Auto-generated method stub
		JSONObject lastest = new JSONObject();
		JSONObject response = null;
		try {
			lastest.put("livingitemid", livingitemid);
			response = HttpComm.sendJSONToServer(
					Constants.GET_PROMOTTION_PRODUCT_GET, lastest,
					Constants.HTTP_SO_TIMEOUT);
			Log.i("MyLog", "商品列表信息为2——————" + response);
			if (response == null) {
				return null;
			}
			PromotionList livingItem = new PromotionList();
			livingItem.promotionList = new ArrayList<Promotion>();
			if (200 == response.getInt("code")) {
				JSONArray jsArray = (JSONArray) response.get("promotionarray");

				for (int i = 0; i < jsArray.length(); i++) {
					JSONObject obj = (JSONObject) jsArray.get(i);
					Promotion promotionItem = new Promotion();
					promotionItem.body = obj.getString("body");
					promotionItem.LivingItemID = obj.getInt("LivingItemID");
					promotionItem.Price = obj.getDouble("Price");
					promotionItem.path = obj.getString("path");
					promotionItem.header = obj.getString("header");
					promotionItem.Priority = obj.getInt("Priority");
					promotionItem.SaleTypeID = obj.getInt("SaleTypeID");
					promotionItem.Height = obj.getInt("Height");
					promotionItem.Width = obj.getInt("Width");
					promotionItem.ForSal = obj.getBoolean("ForSale");
					promotionItem.PromotionID = obj.getLong("PromotionID");
					livingItem.promotionList.add(promotionItem);

					Log.i("MyLog", "promotionid===="
							+ promotionItem.PromotionID);
				}
			}
			Log.i("MyLog", "商品信息为------------" + response);
			return livingItem;

		} catch (Exception ex) {
			ex.printStackTrace();
			LogUtil.d("MyTag", "error: " + ex.getMessage());
			return null;
		}
	}

	@Override
	public LivingItemPictureInfo getLivingItemsPictureByLivingItemId(Context c,
			int livingitemid, String source, int propertyid) {
		// TODO Auto-generated method stub
		JSONObject lastest = new JSONObject();
		JSONObject response = null;

		try {

			LogUtil.d("MyTag", "livingitemid: " + livingitemid);
			LogUtil.d("MyTag", "source: " + source);
			LogUtil.d("MyTag", "propertyid: " + propertyid);

			lastest.put("livingitemid", livingitemid);
			lastest.put("source", source);
			lastest.put("propertyid", propertyid);
			response = HttpComm.sendJSONToServer(
					Constants.GET_LIVING_ITEM_PICTURE_GET, lastest,
					Constants.HTTP_SO_TIMEOUT);

			if (response == null) {
				return null;
			}
			LivingItemPictureInfo livingItemPictureInfo = new LivingItemPictureInfo();
			ArrayList<LivingItemPicture> ret = new ArrayList<LivingItemPicture>();
			if (200 == response.getInt("code")) {
				JSONArray jsArray = (JSONArray) response
						.get("livingitempicturearray");

				for (int i = 0; i < jsArray.length(); i++) {
					JSONObject obj = (JSONObject) jsArray.get(i);
					LivingItemPicture livingItemPicture = new LivingItemPicture();
					livingItemPicture.LivingItemPictureID = obj
							.getLong("LivingItemPictureID");
					livingItemPicture.path = obj.getString("path");
					livingItemPicture.Description = obj
							.getString("Description");
					ret.add(livingItemPicture);
				}
				livingItemPictureInfo.netInfo = new NetInfo();
				livingItemPictureInfo.netInfo.code = response.getInt("code");
				livingItemPictureInfo.netInfo.desc = response.getString("desc");
				livingItemPictureInfo.livingItemPicture = ret;
			}
			return livingItemPictureInfo;

		} catch (Exception ex) {
			ex.printStackTrace();
			LogUtil.d("MyTag", "error: " + ex.getMessage());
			return null;
		}
	}

	/**
	 * 查询缴费
	 */
	@Override
	public LifePay getLifePay(Context context, long userid, String account) {
		JSONObject obj = new JSONObject();
		JSONObject response = null;
		try {
			obj.put("userid", userid);
			obj.put("account", account);
			response = HttpComm.sendJSONToServer(Constants.GET_BILL, obj,
					Constants.HTTP_SO_TIMEOUT);
			Log.i("MyLog", "LifePay查询缴费=" + response);
			if (response == null) {
				return null;
			}
			LifePay lifePay = new LifePay();
			if (200 == response.getInt("code")) {
				JSONArray jsonArray = response.getJSONArray("promotionarray");
				int length = jsonArray.length();
				lifePay.promotionArray = new ArrayList<RemoteApi.Promotion>();
				for (int i = 0; i < length; i++) {
					JSONObject obj2 = jsonArray.getJSONObject(i);
					Promotion promotion = new Promotion();
					promotion.body = obj2.getString("body");
					Log.i("MyLog", "promotion-body=" + promotion.body);
					promotion.CTime = obj2.getString("CTime");
					promotion.ETime = obj2.getString("ETime");
					promotion.ForSal = obj2.getBoolean("ForSale");
					Log.i("MyLog", "response===promotion.ForSal="
							+ promotion.ForSal);
					promotion.header = obj2.getString("header");
					Log.i("MyLog", "response===promotion.header="
							+ promotion.header);
					promotion.SaleTypeID = obj2.getInt("SaleTypeID");
					promotion.LivingItemID = obj2.getInt("LivingItemID");
					promotion.Price = obj2.getDouble("Price");
					promotion.Priority = obj2.getInt("Priority");
					promotion.PromotionID = obj2.getLong("PromotionID");
					Log.i("MyLog", "response===promotion.Price="
							+ promotion.Price);
					lifePay.promotionArray.add(promotion);
				}
				lifePay.TheBill = new Bill();

				Log.i("MyLog", "response===promotion.Price=1");
				JSONObject jsonObject = response.getJSONObject("TheBill");
				Log.i("MyLog", "response===promotion.Price=2");
				lifePay.TheBill.Status = new BillStatus();
				Log.i("MyLog", "response===promotion.Price=3");
				JSONObject billStatus = jsonObject.getJSONObject("Status");
				lifePay.TheBill.Status.BillStatusName = billStatus
						.getString("BillStatusName");
				lifePay.TheBill.Status.BillStatusID = billStatus
						.getInt("BillStatusID");
				Log.i("MyLog", "response===promotion.Price=4");
				Log.i("MyLog", "response===promotion.Price="
						+ lifePay.TheBill.Status.BillStatusName);
				lifePay.TheBill.TrueName = jsonObject.getString("TrueName");
				lifePay.TheBill.Address = jsonObject.getString("Address");
				Log.i("MyLog", "response===Address=" + lifePay.TheBill.Address);
				lifePay.TheBill.BTime = jsonObject.getString("BTime");
				lifePay.TheBill.ETime = jsonObject.getString("ETime");
				lifePay.TheBill.PTime = jsonObject.getString("PTime");
				lifePay.TheBill.BillID = jsonObject.getLong("BillID");
				lifePay.TheBill.OrderSequenceNumber = jsonObject
						.getLong("OrderSequenceNumber");
				lifePay.TheBill.Comment = jsonObject.getString("Comment");
				lifePay.desc = response.getString("desc");
				lifePay.code = response.getInt("code");

				Log.i("MyLog", "response===Address=Theorder6");
				if (response.toString().contains("AliOrderStr")) {
					lifePay.TheOrder = new OrderItem();
					lifePay.TheOrder.ThePromotion = new Promotion();
					JSONObject orderObject = response.getJSONObject("TheOrder");
					lifePay.TheOrder.AliOrderStr = orderObject
							.getString("AliOrderStr");
					lifePay.TheOrder.Total = orderObject.getDouble("Total");
					lifePay.TheOrder.Number = orderObject.getInt("Number");
					JSONObject thePromotionObject = orderObject
							.getJSONObject("ThePromotion");

					lifePay.TheOrder.ThePromotion.body = thePromotionObject
							.getString("body");
					Log.i("MyLog", "promotion-body="
							+ lifePay.TheOrder.ThePromotion.body);
					lifePay.TheOrder.ThePromotion.CTime = thePromotionObject
							.getString("CTime");
					lifePay.TheOrder.ThePromotion.ETime = thePromotionObject
							.getString("ETime");
					lifePay.TheOrder.ThePromotion.ForSal = thePromotionObject
							.getBoolean("ForSale");
					Log.i("MyLog", "response===promotion.ForSal="
							+ lifePay.TheOrder.ThePromotion.ForSal);
					lifePay.TheOrder.ThePromotion.header = thePromotionObject
							.getString("header");
					Log.i("MyLog", "response===promotion.header="
							+ lifePay.TheOrder.ThePromotion.header);
					lifePay.TheOrder.ThePromotion.SaleTypeID = thePromotionObject
							.getInt("SaleTypeID");
					lifePay.TheOrder.ThePromotion.LivingItemID = thePromotionObject
							.getInt("LivingItemID");
					lifePay.TheOrder.ThePromotion.Price = thePromotionObject
							.getDouble("Price");
					lifePay.TheOrder.ThePromotion.Priority = thePromotionObject
							.getInt("Priority");
					Log.i("MyLog", "lifePay.TheOrder.ThePromotion.Priority="
							+ lifePay.TheOrder.ThePromotion.header);
					lifePay.TheOrder.ThePromotion.PromotionID = thePromotionObject
							.getLong("PromotionID");
				}
			}
			Log.i("MyLog", "response===promotion.Price=5");
			Log.i("MyLog", "response===" + lifePay);
			return lifePay;
		} catch (Exception e) {
		}
		return null;
	}

	@Override
	public WeatherInfo getWeather(Context c, String city) {
		// TODO Auto-generated method stub
		JSONObject lastest = new JSONObject();
		JSONObject response = null;

		try {
			lastest.put("cityname", city);
			response = HttpComm.sendJSONToServer(Constants.GET_WEATHER_GET,
					lastest, Constants.HTTP_SO_TIMEOUT);

			if (response == null) {
				return null;
			}
			WeatherInfo weatherInfo = null;
			if (200 == response.getInt("code")) {
				weatherInfo = new WeatherInfo();
				weatherInfo.temp1 = response.getString("temp1");
				weatherInfo.img_title_single = response
						.getString("img_title_single");
				weatherInfo.wind1 = response.getString("wind1");
				weatherInfo.fl1 = "空气 " + response.getString("aqi") + " "
						+ response.getString("quality");
			}
			return weatherInfo;

		} catch (Exception ex) {
			ex.printStackTrace();
			LogUtil.d("MyTag", "error: " + ex.getMessage());
			return null;
		}
	}

	@Override
	public WifiInfo sendGetWifi(Context c) {
		// TODO Auto-generated method stub
		JSONObject send = new JSONObject();
		JSONObject response = null;
		WifiInfo ret = null;
		ret = new WifiInfo();
		try {
			response = HttpComm.sendJSONToServer(
					Constants.SEND_GET_WIFI_PASSWORD_URL, send,
					Constants.HTTP_SO_TIMEOUT);
			if (response == null) {
				ret.code = 4;
				ret.desc = "网络异常";
				ret.desc1 = "";
				return ret;
			}
			ret.code = response.getInt("code");
			ret.desc = response.getString("wifi");
			ret.desc1 = response.getString("comment");
			return ret;

		} catch (Exception ex) {
			ex.printStackTrace();
			LogUtil.d("MyTag",
					"Remote modifyPassword error: " + ex.getMessage());

			ret.code = 4;
			ret.desc = "未知错误";
			ret.desc1 = "";
			return ret;
		}
	}

	@Override
	public ValidateCode getValidateCode(Context c, String imei) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NetInfo sendRepairNew(int propertyid, long userid, int typeid,
			String telnumber, String body, File pictures) {
		// TODO Auto-generated method stub
		JSONObject response = null;
		NetInfo ret = null;
		ret = new NetInfo();

		LogUtil.d("sendRepairNew", "APIUrl: " + Constants.REPAIR_CREATE_URL
				+ " propertyid:" + propertyid + " userid:" + userid
				+ " typeid:" + typeid + " telnumber:" + telnumber + " body:"
				+ body);

		try {
			response = HttpComm.addRepirAndUploadFile(
					Constants.REPAIR_CREATE_URL, propertyid, userid, typeid,
					telnumber, body, pictures, Constants.HTTP_SO_TIMEOUT);
			if (response == null) {
				return null;
			}
			ret.code = response.getInt("code");
			ret.desc = response.getString("desc");
			return ret;

		} catch (Exception ex) {
			ex.printStackTrace();
			LogUtil.d("MyTag", "Remote sendRepairNew error: " + ex.getMessage());
			ret.code = 4;
			ret.desc = "";
			return ret;
		}
	}

	@Override
	public ArrayList<RepairType> getRepairTypeList(Context c, long userId,
			int propertyid) {

		JSONObject lastest = new JSONObject();
		JSONObject response = null;

		try {
			lastest.put("propertyid", propertyid);
			lastest.put("userid", userId);

			byte[] httpPostBytes = lastest.toString().getBytes();

			LogUtil.d("getRepairTypeList", "param:" + new String(httpPostBytes));
			LogUtil.d("getRepairTypeList", "url:"
					+ Constants.REPAIR_TYPE_LIST_URL);

			response = HttpComm.sendJSONToServer(
					Constants.REPAIR_TYPE_LIST_URL, lastest,
					Constants.HTTP_SO_TIMEOUT);

			LogUtil.d("getRepairTypeList", "response " + response);
			if (response == null) {
				return null;
			}
			ArrayList<RepairType> ret = new ArrayList<RepairType>();

			if (200 == response.getInt("code")) {

				LogUtil.d("getRepairTypeList", "response " + response);

				JSONArray jsArray = (JSONArray) response.get("repairtypearray");
				for (int i = 0; i < jsArray.length(); i++) {
					JSONObject obj = (JSONObject) jsArray.get(i);
					RepairType repairType = new RepairType();
					repairType.repairTypeId = obj.getLong("RepairTypeID");
					repairType.shortCut = obj.getBoolean("ShortCut");
					repairType.repairTypeName = obj.getString("RepairTypeName");
					repairType.repairTypeDescription = obj
							.getString("RepairTypeDescription");
					repairType.mayBePay = obj.getBoolean("MayBePay");
					ret.add(repairType);
				}
			}
			return ret;

		} catch (Exception ex) {
			ex.printStackTrace();
			LogUtil.d("MyTag", "error: " + ex.getMessage());
			return null;
		}
	}

	@Override
	public ArrayList<Repair> getRepairList(Context c, long userId,
			int propertyid, int count) {

		JSONObject lastest = new JSONObject();
		JSONObject response = null;

		try {

			if (count > 1) {
				LogUtil.d("getRepairList", "count:" + count);
				return null;
			}
			lastest.put("propertyid", propertyid);
			lastest.put("userid", userId);
			lastest.put("type", 1);
			byte[] httpPostBytes = lastest.toString().getBytes();
			LogUtil.d("getRepairList", "param:" + new String(httpPostBytes));
			LogUtil.d("getRepairList", "url:" + Constants.REPAIR_LIST_URL);

			response = HttpComm.sendJSONToServer(Constants.REPAIR_LIST_URL,
					lastest, Constants.HTTP_SO_TIMEOUT);
			Log.i("MyLog", "保修进度为-------》" + response);
			LogUtil.d("getRepairList", "response " + response);
			if (response == null) {
				return null;
			}
			ArrayList<Repair> ret = new ArrayList<Repair>();

			if (200 == response.getInt("code")) {
				JSONArray jsArray = (JSONArray) response.get("repairarray");
				SimpleDateFormat ymdSdf = new SimpleDateFormat("yyyy-MM-dd");
				for (int i = 0; i < jsArray.length(); i++) {
					JSONObject obj = (JSONObject) jsArray.get(i);
					Repair repair = new Repair();
					repair.repairid = obj.getLong("RepairID");
					repair.body = obj.getString("Body");

					String ctime = obj.getString("CTime");
					if (!"".equals(ctime)) {
						long ctimes = Long.valueOf(ctime);
						repair.cTime = ymdSdf.format(ctimes);
					}
					repair.room = obj.getString("Room");
					repair.contact = obj.getString("Contact");
					String otime = obj.getString("OTime");
					if (!"".equals(otime)) {
						long otimes = Long.valueOf(otime);
						repair.oTime = ymdSdf.format(otimes);
					}

					JSONObject statusObj = obj.getJSONObject("Status");
					RepairStatus repairStatus = new RepairStatus();
					repairStatus.repairStatusId = statusObj
							.getLong("RepairStatusID");
					repairStatus.repairStatusName = statusObj
							.getString("RepairStatusName");
					repairStatus.repairStatusDescription = statusObj
							.getString("RepairStatusDescription");
					repair.status = repairStatus;

					JSONObject typeObj = obj.getJSONObject("Type");
					RepairType repairType = new RepairType();
					repairType.mayBePay = typeObj.getBoolean("MayBePay");
					repairType.propertyID = typeObj.getLong("PropertyID");
					repairType.repairTypeId = typeObj.getLong("RepairTypeID");
					repairType.repairTypeDescription = typeObj
							.getString("RepairTypeDescription");
					repairType.repairTypeName = typeObj
							.getString("RepairTypeName");
					repairType.shortCut = typeObj.getBoolean("ShortCut");
					repair.type = repairType;

					JSONObject userObj = obj.getJSONObject("TheUser");
					User theUser = new User();
					theUser.userName = userObj.getString("TrueName");
					repair.theUser = theUser;

					JSONObject operatorObj = obj.getJSONObject("TheOperator");
					PropertyUser theOperator = new PropertyUser();
					theOperator.workerName = operatorObj
							.getString("WorkerName");
					theOperator.mobile = operatorObj.getString("WorkerTel");
					repair.theOperator = theOperator;

					ret.add(repair);

				}
			}
			return ret;

		} catch (Exception ex) {
			ex.printStackTrace();
			LogUtil.d("MyTag", "error: " + ex.getMessage());
			return null;
		}
	}

	@Override
	public ArrayList<RepairLog> getRepairLog(Context c, long userId,
			long repairId, int propertyid) {
		JSONObject lastest = new JSONObject();
		JSONObject response = null;

		try {
			lastest.put("propertyid", propertyid);
			lastest.put("userid", userId);
			lastest.put("repairid", repairId);

			byte[] httpPostBytes = lastest.toString().getBytes();
			LogUtil.d("getRepairLog", "param:" + new String(httpPostBytes));
			LogUtil.d("getRepairLog", "url:" + Constants.REPAIR_LIST_URL);

			response = HttpComm.sendJSONToServer(Constants.REPAIR_DETAIL_URL,
					lastest, Constants.HTTP_SO_TIMEOUT);

			LogUtil.d("getRepairLog", "response " + response);
			if (response == null) {
				return null;
			}
			ArrayList<RepairLog> ret = new ArrayList<RepairLog>();

			if (200 == response.getInt("code")) {
				JSONArray jsArray = (JSONArray) response.get("repairlogarray");
				for (int i = 0; i < jsArray.length(); i++) {
					JSONObject obj = (JSONObject) jsArray.get(i);

					RepairLog repairLog = new RepairLog();
					repairLog.displayContent = obj.getString("DisplayContent");

					String repairLogCTime = obj.getString("RepairLogCTime");
					if (!"".equals(repairLogCTime)) {
						long ctimes = Long.valueOf(repairLogCTime);
						repairLog.cTime = sdf.format(ctimes);
					}

					JSONObject statusObj = obj.getJSONObject("TheRepairStatus");
					repairLog.theRepairStatus = statusObj
							.getInt("RepairStatusID");

					/*
					 * Repair repair = new Repair(); repair.repairid =
					 * obj.getLong("RepairID"); repair.body =
					 * obj.getString("Body");
					 * 
					 * String ctime = obj.getString("CTime"); if
					 * (!"".equals(ctime)) { long ctimes = Long.valueOf(ctime);
					 * repair.cTime = sdf.format(ctimes); } repair.room =
					 * obj.getString("Room"); repair.contact =
					 * obj.getString("Contact"); String otime =
					 * obj.getString("OTime"); if (!"".equals(otime)) { long
					 * otimes = Long.valueOf(otime); repair.oTime =
					 * sdf.format(otimes); }
					 * 
					 * JSONObject statusObj = obj.getJSONObject("Status");
					 * RepairStatus repairStatus = new RepairStatus();
					 * repairStatus.repairStatusId =
					 * statusObj.getLong("RepairStatusID");
					 * repairStatus.repairStatusName =
					 * statusObj.getString("RepairStatusName");
					 * repairStatus.repairStatusDescription =
					 * statusObj.getString("RepairStatusDescription");
					 * repair.status = repairStatus;
					 * 
					 * JSONObject typeObj = obj.getJSONObject("Type");
					 * RepairType repairType = new RepairType();
					 * repairType.mayBePay = typeObj.getBoolean("MayBePay");
					 * repairType.propertyID = typeObj.getLong("PropertyID");
					 * repairType.repairTypeId =
					 * typeObj.getLong("RepairTypeID");
					 * repairType.repairTypeDescription =
					 * typeObj.getString("RepairTypeDescription");
					 * repairType.repairTypeName =
					 * typeObj.getString("RepairTypeName"); repairType.shortCut
					 * = typeObj.getBoolean("ShortCut"); repair.type =
					 * repairType;
					 * 
					 * 
					 * JSONObject userObj = obj.getJSONObject("TheUser"); User
					 * theUser = new User(); theUser.userName =
					 * userObj.getString("TrueName"); repair.theUser = theUser;
					 * 
					 * JSONObject operatorObj =
					 * obj.getJSONObject("TheOperator"); PropertyUser
					 * theOperator = new PropertyUser(); theOperator.workerName
					 * = operatorObj.getString("WorkerName"); theOperator.mobile
					 * = operatorObj.getString("WorkerTel"); repair.theOperator
					 * = theOperator;
					 */
					ret.add(repairLog);

				}
			}
			return ret;

		} catch (Exception ex) {
			ex.printStackTrace();
			LogUtil.d("MyTag", "error: " + ex.getMessage());
			return null;
		}
	}

	@Override
	public NetInfo repairRemove(int propertyid, long userid, int repairid) {
		JSONObject response = null;
		JSONObject lastest = new JSONObject();
		NetInfo ret = null;
		ret = new NetInfo();
		try {

			lastest.put("propertyid", propertyid);
			lastest.put("userid", userid);
			lastest.put("repairid", repairid);

			byte[] httpPostBytes = lastest.toString().getBytes();
			LogUtil.d("repairRemove", "param:" + new String(httpPostBytes));
			LogUtil.d("repairRemove", "url:" + Constants.REPAIR_REMOVE_URL);

			response = HttpComm.sendJSONToServer(Constants.REPAIR_REMOVE_URL,
					lastest, Constants.HTTP_SO_TIMEOUT);
			if (response == null) {
				return null;
			}
			ret.code = response.getInt("code");
			ret.desc = response.getString("desc");

			return ret;

		} catch (Exception ex) {
			ex.printStackTrace();
			LogUtil.d("MyTag", "Remote sendRepairNew error: " + ex.getMessage());
			ret.code = 4;
			ret.desc = "";
			return ret;
		}
	}

	@Override
	public ArrayList<RepairPicture> getRepairPicture(Context c, long userId,
			long repairId, int propertyid) {
		JSONObject lastest = new JSONObject();
		JSONObject response = null;

		try {
			lastest.put("propertyid", propertyid);
			lastest.put("userid", userId);
			lastest.put("repairid", repairId);

			byte[] httpPostBytes = lastest.toString().getBytes();
			LogUtil.d("getRepairPicture", "param:" + new String(httpPostBytes));
			LogUtil.d("getRepairPicture", "url:" + Constants.REPAIR_LIST_URL);

			response = HttpComm.sendJSONToServer(Constants.REPAIR_DETAIL_URL,
					lastest, Constants.HTTP_SO_TIMEOUT);

			Log.i("MyLog", "responser图片 " + response);
			if (response == null) {
				return null;
			}
			ArrayList<RepairPicture> ret = new ArrayList<RepairPicture>();

			if (200 == response.getInt("code")) {
				JSONArray jsArray = (JSONArray) response
						.get("repairpicturearray");
				if (jsArray.length() > 0) {

					for (int i = 0; i < jsArray.length(); i++) {
						JSONObject obj = (JSONObject) jsArray.get(i);
						RepairPicture repairPicture = new RepairPicture();
						repairPicture.repairPictureID = obj
								.getLong("RepairPictureID");
						repairPicture.path = obj.getString("path");
						repairPicture.description = obj
								.getString("Description");
						ret.add(repairPicture);
					}

				} else {
					return null;
				}

			}
			return ret;

		} catch (Exception ex) {
			ex.printStackTrace();
			LogUtil.d("MyTag", "error: " + ex.getMessage());
			return null;
		}
	}

	@Override
	public NetInfo repairSolved(int propertyid, long userid, int repairid,
			String code) {
		JSONObject response = null;
		JSONObject lastest = new JSONObject();
		NetInfo ret = null;
		ret = new NetInfo();

		try {

			lastest.put("propertyid", propertyid);
			lastest.put("userid", userid);
			lastest.put("repairid", repairid);
			lastest.put("code", code);

			// byte[] httpPostBytes = lastest.toString().getBytes();
			// LogUtil.d("repairSolved", "param:" + new String(httpPostBytes));
			// LogUtil.d("repairSolved", "url:" + Constants.REPAIR_LIST_URL);

			response = HttpComm.sendJSONToServer(Constants.REPAIR_SOLVED_URL,
					lastest, Constants.HTTP_SO_TIMEOUT);
			if (response == null) {
				return null;
			}

			ret.code = response.getInt("code");
			ret.desc = response.getString("desc");

			return ret;

		} catch (Exception ex) {
			ex.printStackTrace();
			LogUtil.d("MyTag", "Remote sendRepairNew error: " + ex.getMessage());
			ret.code = 4;
			ret.desc = "";
			return ret;
		}
	}

	/**
	 * 用户申请退款
	 */
	@Override
	public NetInfo userReturnApply(Context context, long ordersequencenumber) {
		JSONObject send = new JSONObject();
		JSONObject response = null;
		try {
			send.put("ordersequencenumber", ordersequencenumber);
			response = HttpComm.sendJSONToServer(Constants.ORDER_RETURN_APPLY,
					send, Constants.HTTP_SO_TIMEOUT);
			Log.i("MyLog", "获得数据为--user=" + response);
			if (response == null)
				return null;

			NetInfo netInfo = new NetInfo();
			netInfo.code = response.getInt("code");
			netInfo.desc = response.getString("desc");

			return netInfo;
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * 用户确认收货
	 */
	@Override
	public NetInfo userConfirmGoods(Context context, long ordersequencenumber) {
		JSONObject send = new JSONObject();
		JSONObject response = null;
		try {
			send.put("ordersequencenumber", ordersequencenumber);
			response = HttpComm.sendJSONToServer(Constants.ORDER_CONFIRM_GOODS,
					send, Constants.HTTP_SO_TIMEOUT);
			Log.i("MyLog", "获得数据为--user=" + response);
			if (response == null)
				return null;

			NetInfo netInfo = new NetInfo();
			netInfo.code = response.getInt("code");
			netInfo.desc = response.getString("desc");

			return netInfo;
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * 获取免费wifi信息
	 */
	@Override
	public FreeWifi getFreeWifiInfo(Context c, long userid) {
		JSONObject send = new JSONObject();
		JSONObject response = null;
		try {
			send.put("userid", userid);
			Log.i("MyLog", "userid=" + userid);
			response = HttpComm.sendJSONToServer(Constants.WIFI_PASSWORD_GET,
					send, Constants.HTTP_SO_TIMEOUT);
			Log.i("MyLog", "获得数据为--wifiInfo=" + response);
			if (response == null)
				return null;

			FreeWifi wifiInfo = new FreeWifi();
			/*
			 * wifiInfo.netInfo=new NetInfo(); wifiInfo.netInfo.code=
			 * response.getInt("code");
			 * wifiInfo.netInfo.desc=response.getString("desc");
			 */
			wifiInfo.UserID = response.getString("UserID");
			wifiInfo.WIFIUserID = response.getString("WIFIUserID");
			wifiInfo.WIFIAccount = response.getString("WIFIAccount");
			wifiInfo.WIFIPwd = response.getString("WIFIPwd");

			return wifiInfo;
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * 获取代金券列表
	 */
	@Override
	public CashCouponList getListUserCashCoupon(Context Context, int userid,
			int typeid) {
		JSONObject lastest = new JSONObject();
		JSONObject response = null;
		try {
			lastest.put("userid", userid);
			lastest.put("typeid", 2);
			response = HttpComm.sendJSONToServer(Constants.GET_COUPON_LIST,
					lastest, Constants.HTTP_SO_TIMEOUT);
			Log.i("MyLog", "CouponList=" + response);
			if (response == null) {
				return null;
			}
			CashCouponList cashCouponList = new CashCouponList();
			cashCouponList.userCashCouponList = new ArrayList<UserCashCoupon>();
			cashCouponList.netInfo = new NetInfo();
			cashCouponList.netInfo.code = response.getInt("code");
			if (200 == response.getInt("code")) {
				JSONArray jsArray = (JSONArray) response
						.get("usercashcouponarray");

				for (int i = 0; i < jsArray.length(); i++) {
					JSONObject obj = (JSONObject) jsArray.get(i);
					// cashcouponstatusid 1.可用 2.已过期 3.已作废 4.已使用
					UserCashCoupon userCashCoupon = new UserCashCoupon();
					JSONObject objStatus = obj.getJSONObject("Status");
					userCashCoupon.cashCouponStatus = new CashCouponStatus();
					userCashCoupon.cashCouponStatus.CashCouponStatusID = objStatus
							.getInt("CashCouponStatusID");
					userCashCoupon.cashCouponStatus.CashCouponStatusName = objStatus
							.getString("CashCouponStatusName");
					userCashCoupon.cashCoupon = new CashCoupon();
					userCashCoupon.UserCashCouponID = obj
							.getLong("UserCashCouponID");
					JSONObject objCashCoupon = obj
							.getJSONObject("TheCashCoupon");
					userCashCoupon.cashCoupon.ParValue = objCashCoupon
							.getInt("ParValue");
					Log.i("MyLog", "ParValue="
							+ userCashCoupon.cashCoupon.ParValue);
					String bTime = obj.getString("BTime");
					if (!"".equals(bTime)) {
						long times = Long.valueOf(bTime);
						userCashCoupon.BTime = sdf3.format(times);
					}
					Log.i("MyLog", "userCashCoupon.BTime="
							+ userCashCoupon.BTime);
					String ETime = obj.getString("ETime");
					if (!"".equals(ETime)) {
						long times = Long.valueOf(ETime);
						userCashCoupon.ETime = sdf3.format(times);
					}
					cashCouponList.userCashCouponList.add(userCashCoupon);
				}
			}
			return cashCouponList;

		} catch (Exception ex) {
			ex.printStackTrace();
			LogUtil.d("MyTag", "error: " + ex.getMessage());
			return null;
		}
	}

	/**
	 * 创建支付订单
	 */

	@Override
	public OrderItem createOrder(Context c, long promotionid, long userid,
			long addressid, String comment, int number, String paytype,
			long usercashcouponid, long workorderid, long repairid) {
		JSONObject send = new JSONObject();
		JSONObject response = null;
		OrderItem info=null;
		try {
			// 提交请求字段
			send.put("promotionid", promotionid);
			send.put("userid", userid);
			send.put("addressid", addressid);
			send.put("comment", comment);
			send.put("number", number);
			send.put("paytype", paytype);
			send.put("usercashcouponid", usercashcouponid);
			send.put("workorderid", workorderid);
			send.put("repairid", repairid);
			Log.i("MyLog", "123==" + "  promotionid=" + promotionid
					+ " userid=" + userid + " addressid=" + addressid
					+ " comment=" + comment + " number=" + number + " paytype"
					+ paytype + " usercashcouponid=" + usercashcouponid
					+ " workorderid=" + workorderid + " repairid=" + repairid);
			// 获取网络返回数据
			response = HttpComm.sendJSONToServer(Constants.CREATE_ORDER_URL,
					send, Constants.HTTP_SO_TIMEOUT);
			Log.i("MyLog",
					"createOrderItem--创建支付订单成功---获取订单信息为："
							+ response);
			if (response == null) {
				return null;
			}
			info = new OrderItem();
			info.ThePromotion = new Promotion();
			info.Status = new Status();
			info.WxPayInfo = new WXPayInfo();
			info.netInfo = new NetInfo();
			info.netInfo.code = response.getInt("code");
			info.netInfo.desc = response.getString("desc");
			Log.i("MyLog", "code = "+info.netInfo.code);
			// 解析数据并存入到Orderitem对象中
			if (response.getInt("code")==200) {
				// JSONObject userObj = response.getJSONObject("user") ;
				// info.ThePromotion = response.getString("ThePromotion");
				// JSONObject obj0 = new JSONObject(response.toString());
				JSONObject obj = response.getJSONObject("ThePromotion");
				Log.i("MyLog", "thepromotion-------------->" + obj.toString());
				// Promotion promotion=new Promotion();
				Log.i("MyLog", "bodyooo------->" + obj.getString("body"));

				info.ThePromotion.body = obj.getString("body");
				Log.i("MyLog", "body------->" + obj.getString("body"));
				info.ThePromotion.LivingItemID = obj.getInt("LivingItemID");
				Log.i("MyLog",
						"LivingItemID------->" + obj.getInt("LivingItemID"));
				info.ThePromotion.PromotionID = obj.getLong("PromotionID");
				info.ThePromotion.Price = obj.getDouble("Price");
				info.ThePromotion.ETime = obj.getString("ETime");
				Log.i("MyLog", "ETime------->" + obj.getString("ETime"));
				info.ThePromotion.path = obj.getString("path");
				info.ThePromotion.Height = obj.getInt("Height");
				info.ThePromotion.Width = obj.getInt("Width");
				info.ThePromotion.header = obj.getString("header");
				info.ThePromotion.SaleTypeID = obj.getInt("SaleTypeID");
				Log.i("MyLog", "header------->" + obj.getString("header"));
				info.ThePromotion.Priority = obj.getInt("Priority");
				info.ThePromotion.ForSal = obj.getBoolean("ForSale");
				Log.i("MyLog", "ForSale------->" + obj.getBoolean("ForSale"));
				
				info.OrderID = response.getString("OrderID");
				info.Number = response.getInt("Number");
				info.Comment = response.getString("Comment");
				Log.i("MyLog",
						"OrderID------->" + response.getString("Comment"));
				info.UserID = response.getLong("UserID");
				// Status status=new Status();
				JSONObject obj2 = response.getJSONObject("Status");
				Log.i("MyLog", "Status-------------->" + obj2.toString());
				info.Status.OrderStatusID = obj2.getInt("OrderStatusID");
				info.Status.OrderStatusName = obj2.getString("OrderStatusName");
				info.TelNumber = response.getString("TelNumber");
				info.AliOrderStr = response.getString("AliOrderStr");

				if (response.getString("WxPayInfo").equals("null")) {

					Log.i("MyLog",
							"WxPayInfo===123" + response.getString("WxPayInfo"));
					Log.i("MyLog", "response.getString(\"AliOrderStr\")="
							+ response.getString("AliOrderStr"));
				} else {
					JSONObject wxObj = response.getJSONObject("WxPayInfo");
					info.WxPayInfo.TimeStamp = wxObj.getString("TimeStamp");
					Log.i("MyLog",
							"WxPayInfo===123" + wxObj.getString("TimeStamp"));
					info.WxPayInfo.Appid = wxObj.getString("AppId");
					info.WxPayInfo.PartnerId = wxObj.getString("PartnerId");
					info.WxPayInfo.Package = wxObj.getString("Package");
					info.WxPayInfo.PrepayId = wxObj.getString("PrepayId");
					info.WxPayInfo.Sign = wxObj.getString("Sign");
					info.WxPayInfo.NonceStr = wxObj.getString("NonceStr");
					Log.i("MyLog",
							"WxPayInfo===" + response.getString("WxPayInfo"));
					Log.i("MyLog",
							"response.getString(\"wixin\")="
									+ response.getString("AliOrderStr"));
				}
				Log.i("MyLog", "response.getString(\"AliOrderStr\")="
						+ response.getString("AliOrderStr"));

				info.TheAddress = response.getString("Address");
				info.Comment = response.getString("Comment");
				info.OrderSequenceNumber = response
						.getLong("OrderSequenceNumber");
				Log.i("MyLog",
						"-OrderSequenceNumber=--number----------->" + response
						.getLong("OrderSequenceNumber"));
				info.Total = response.getDouble("Total");
				info.Ctime = response.getString("CTime");

				Log.i("MyLog",
						"CTime-------------->" + response.getString("CTime"));
			}
			Log.i("MyLog",
					"createOrderItem--创建支付订单成功--------" + response.toString());
			return info;

		} catch (Exception ex) {
			ex.printStackTrace();
			LogUtil.d("MyTag",
					"Remote CreateOrder error: " + response.toString());
		}
		Log.i("MyLog", "Info="+info);
		return info;
		
	}

	@Override
	public Promotion getPromotionId(Context context, int propertyid,
			int userid, String description) {
		JSONObject send = new JSONObject();
		JSONObject response = null;
		try {
			send.put("propertyid", propertyid);
			send.put("userid", userid);
			send.put("description", description);
			Log.i("MyLog", "userid=" + userid +"propertyid="+propertyid+"description="+description);
			response = HttpComm.sendJSONToServer(Constants.GET_PROMOTION_URL,
					send, Constants.HTTP_SO_TIMEOUT);
			Log.i("MyLog", "获得数据为--promotionid=" + response);
			if (response == null)
				return null;
			Promotion promotion = new Promotion();
			promotion.PromotionID = response.getLong("PromotionID");

			return promotion;
		} catch (Exception e) {
		}
		return null;
	}
	
	@Override
	public WIFISSID getWifiSSID(Context context) {
		JSONObject send = new JSONObject();
		JSONObject response = null;
		try {
			response = HttpComm.sendJSONToServer(Constants.WIFI_SSID_GET,
					send, Constants.HTTP_SO_TIMEOUT);
			Log.i("MyLog", "获得数据为--WIFISSID=" + response);
			if (response == null)
				return null;
			WIFISSID wifiSSID = new WIFISSID();
			wifiSSID.ssidList = new ArrayList<RemoteApi.SSIDS>();
			NetInfo netInfo = new NetInfo();
			netInfo.code = response.getInt("code");
			netInfo.desc = response.getString("desc");
			wifiSSID.netInfo=netInfo;
			if (200 == response.getInt("code")) {
			wifiSSID.wifiap = response.getString("wifiap");
			JSONArray array = response.getJSONArray("ssidarray");
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj=array.getJSONObject(i);
				SSIDS ssids = new SSIDS();
				ssids.SSID = obj.getString("SSID");
				wifiSSID.ssidList.add(ssids);
			}
			}
			return wifiSSID;
		} catch (Exception e) {
		}
		return null;
	}
	
	@Override
	public UpdateInfo getUpdateInfo(Context context, String appname) {
		JSONObject send = new JSONObject();
		JSONObject response = null;
		UpdateInfo updateInfo = new UpdateInfo();
		try {
			send.put("appname", appname);
			response = HttpComm.sendJSONToServer2(Constants.GET_UNDATE_INFO, send, Constants.HTTP_SO_TIMEOUT);
			Log.i("MyLog", "更新 response = " +response);
			if (response ==null ) {
				return null;
			}
			updateInfo.netInfo = new NetInfo();
			updateInfo.netInfo.code = response.getInt("code");
			updateInfo.netInfo.desc = response.getString("desc");
			if (updateInfo.netInfo.code == 200) {
				updateInfo.url = response.getString("url");
				updateInfo.versionCode = response.getInt("currentCode");
				updateInfo.updateInfo = response.getString("upinfo");
				updateInfo.appversion = response.getString("appversion");
			}
			return updateInfo;
		} catch (Exception e) {
			e.printStackTrace();
			Log.i("MyLog", " exception:"+e.getMessage());
		}
		return null;
	}

	@Override
	public User getUserActiveInfo(Context context, String telnumber) {
		JSONObject response = null;
		JSONObject putObj = new JSONObject();
		try {
			putObj.put("telnumber",telnumber);
			response = HttpComm.sendJSONToServer(Constants.GET_SMS_INFO,putObj,Constants.HTTP_SO_TIMEOUT);
			Log.i("MyLog","response by Mobile = " +response);
			User user = new User();
			if (response == null)
			{
				return null;
			}
			else
			{
				user.netInfo = new NetInfo();
				user.netInfo.code = response.getInt("code");
				user.netInfo.desc = response.getString("desc");
				if (user.netInfo.code == 200)
				{
					
					user.userId = response.getLong("UserID");
					user.userName = response.getString("UserName");
					user.PropertyID = response.getInt("PropertyID");
					user.phone = response.getString("PhoneNumber");
					user.telnumber = response.getString("Mobile");
				}
			}
			Log.i("MyLog","response = " +response);
			return user;
		}catch (Exception e){
			e.printStackTrace();
			Log.i("MyLog","exception = "+ e.getMessage());
		}
		return null;
	}

}