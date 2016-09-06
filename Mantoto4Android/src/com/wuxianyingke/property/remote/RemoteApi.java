package com.wuxianyingke.property.remote;

import java.io.File;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;

public interface RemoteApi {
	/**
	 * 首页菜单
	 * */
	public class MenuList{
		public String Tag;
		public String MenuName;
		public int MenuID;
	}
	
	 /**
     * 代金券模板 CashCoupopn
     */
    public class CashCoupon {
        public int CashCouponID;//代金券模板id
        public String Title;//代金券标题
        public String Body;//代金券内容描述
        public int ParValue;//代金券面额（需要保留两位有效数字）
        public int Integral;//对应的积分
    }
    

    public class CashCouponList {
        public ArrayList<UserCashCoupon> userCashCouponList;
        public NetInfo netInfo;
    }
    
    public class WIFISSID{
    	public String wifiap;
    	public ArrayList<SSIDS> ssidList;
    	public NetInfo netInfo;
    }
    public class SSIDS{
    	public boolean isValid;
    	public String  SSID;
    	public int WIFIID;
    }
    
    /**
     *  通过手机号获取用户信息
     */
    public User getUserActiveInfo(Context context,String telnumber);
    
    public WIFISSID getWifiSSID(Context context);
    
    // 新建订单
    public OrderItem createOrder(Context c, long promotionid, long userid,
                                 long addressid, String comment, int number, String paytype, long usercashcouponid, long workorderid, long repairid);

    /**
     * 代金券列表  List< UserCashCoupon>
     */
    public CashCouponList getListUserCashCoupon(Context Context, int userid,int typeid);

    /**
     * 代金券状态 CashCouponStatus
     */
    public class CashCouponStatus {
        public int CashCouponStatusID;//代金券状态ID
        public String CashCouponStatusName;//代金券状态
    }
    

    /**
     * 代金券  UserCashCoupon
     */
    public class UserCashCoupon {
        public long UserCashCouponID;//代金券id
        public CashCoupon cashCoupon;//代金券模板
        public long UserID;//用户id
        public CashCouponStatus cashCouponStatus;//代金券状态
        public long OrderSequenceNumber;//订单序号
        public String BTime;//开始有效期
        public String ETime;//终止有效期
        public int flag = 1;
    }
    
    
	/**
	 * 获取免费wifi
	 */
	public class FreeWifi{
		public String WIFIUserID;
		public String UserID;
		public String WIFIAccount;
		public String WIFIPwd;
//		public NetInfo netInfo;
	}
	/**
	 * 账单状态
	 * @author liudongdong
	 *
	 */
	public class BillStatus{
		public String BillStatusName;
		public int BillStatusID;
	}
	
	/**
	 * 帐单
	 */
	public class Bill{
		/**
		 * "Status": 
        "PTime": "/Date(-2209017600000+0800)/",
        "BTime": "/Date(-2209017600000+0800)/",
        "TrueName": "于静",
        "BillID": 1,
        "ETime": "1443628800000",
        "Address": "大海阳南街28号",
        "TelNumber": "13856742562",
        "Comment": "",
        "OrderSequenceNumber": 0
		 */
		public BillStatus Status;//订单状态
		public String PTime;
		public String BTime;
		public String TrueName;
		public long BillID;
		public String ETime;
		public String Address;
		public String TelNumber;
		public String Comment;
		public long OrderSequenceNumber;
		
	}

	/**
	 * 生活缴费
	 */
	public class LifePay{
		public ArrayList<Promotion>promotionArray;
		public Bill TheBill;
		public String desc;
		public int code;
		public OrderItem TheOrder;
	}
	
	/**
	 * 查询缴费
	 */
	public LifePay getLifePay(Context code,long userid,String account);
	/**
	 * 支付缴费
	 */
	public OrderItem billPayOrder(Context c, long promotionid, long userid,
			 long billid, int number);

	/**
	 * 当前位置识别小区
	 */
	public class Propertys implements Serializable{
		public long PropertyID;//楼盘id
		public String PropertyName;//楼盘名称
		public int OrganizationID;//楼盘物业公司id
		public float latitude;//纬度
		public float longitude;//经度
	}
	/**
	 * 根据当前位置识别小区
	 * @author liudongdong
	 *
	 */
	public ArrayList<Propertys>getPropertyList(Context context,float latitude,float longitude,int pageindex);
	/**
	 * 根据名称模糊查询小区
	 * @author Administrator
	 *
	 */
	public ArrayList<Propertys>getPropertyList(Context context,String description,int pageindex);
	/**
	 * 获得首页菜单列表
	 * */
	public ArrayList<MenuList>getHomeMenu(Context context,int propertyid);
	/**
	 * 用户申请退款
	 * @author liudongdong
	 *
	 */
	public NetInfo userReturnApply(Context context,long ordersequencenumber);
	/**
	 * 用户确认收货
	 * @author liudongdong
	 *
	 */
	public NetInfo userConfirmGoods(Context context,long ordersequencenumber);
	/*
	 * 固定信息
	 */
	public class NetInfo implements Serializable {
		public int code;
		public String desc;
	}
	/*
	 * 固定信息
	 */
	public class NetInfos implements Serializable {
		public int code;
		public String desc;
		public boolean bSuccess;
	}


	public class WifiInfo implements Serializable {
		public int code;
		public String desc;// 密码
		public String desc1;// 描述
	}
	public NetInfo repairSolved(int propertyid, long userid, int repairid , String code);
	/**
	 * splash页面
	 * 
	 * @author wentinggao
	 * 
	 */
	public class Loading {
		public String logoImgUrl;
		public int logoId;
		public NetInfo netInfo;
	}

	/**
	 * 获取启动界面图片
	 * 
	 * @param logoId
	 * @return
	 */
	public Loading getLoadingInfo(int logoId, long propertyid);

	/**
	 * 小区验证码
	 * 
	 * @author wentinggao
	 * 
	 */
	public class InvitationCode {
		public long propertyID;
		public String propertyName;
		public String phoneNumber = "0";
		public String logoUrl;
		public NetInfo netInfo;
	}

	/**
	 * 发生小区验证码
	 * 
 */
	public InvitationCode sendInvitationCode(Context c, String regcode);
	
	/**
	 * 物业用户
	 */
	public class PropertyUser{
		public long propertyUserID;
		public String workerName;
		public String mobile;
	}
	/**
	 * 报修状态
	 */
	public class RepairStatus{
		public long repairStatusId;
		public String repairStatusName;
		public String repairStatusDescription;
	}

	/**
	 * 报修类型
	 */
	public class RepairType{
		public long repairTypeId;
		public String repairTypeName;
		public String repairTypeDescription;
		public boolean mayBePay;
		public boolean shortCut;
		public long propertyID;

	}

	/**
	 * 报修
	 */
	public class Repair{
		public long repairid;
		public String body;
		public RepairStatus status;
		public RepairType type;
		public String cTime;
		public String room;
		public String oTime;
		public String contact;
		public User theUser;
		public PropertyUser theOperator;
	}
	public NetInfo sendRepairNew(int propertyid, long userid, int typeid,String telnumber, String body, File pictures);
	//获取最新的已派单报修日志
//	public RepairLog getRepairLogLastest(Context c, long userId, int propertyid );
	public NetInfo repairRemove(int propertyid, long userid, int repairid);
	public ArrayList<RepairType> getRepairTypeList(Context c, long userId, int propertyid);
	public ArrayList<Repair> getRepairList(Context c, long userId, int propertyid,int count);
	public ArrayList<RepairLog> getRepairLog(Context c, long userId,long repairId, int propertyid);
	public ArrayList<RepairPicture> getRepairPicture(Context c, long userId,long repairId, int propertyid);
/*	public ArrayList<Repair> getRepairList(Context c, long userId, int propertyid,int count);



	*/
	/**
	 * 报修日志
	 */
	public class RepairLog{
		public long repairID;
		public int theWorkOrder;
		public int theRepairStatus;
		public String displayContent;
		public String cTime;
	}

	/**
	 * 报修图片
	 */
	public class RepairPicture{
		public long repairPictureID;
		public String path;
		public String description;
	}

	/*
	 * 商品快报
	 */
	public class ProductMessage {
		public long msgId;
		public String header; // 标题
		public String body; // 内容
		public String time;
		public NetInfo netInfo;
	}

	/*
	 * 站内消息
	 */
	public class Note {
		public long NoteId;
		public String header;
		public String body;
		public String time;
		public int priority;
		public int priorityId;
		public int ppropertyManagerId;
		public String signature;
		public int hit;
		public int isValid;
	}

	/*
	 * 用户
	 */
	public class User {
		public String userName;
		public String telnumber;
		public long userId = 0;
		public int PropertyID;
		public String phone;
		public NetInfo netInfo;
		
	}

	/*
	 * 登陆
	 */
	public class LoginInfo {
		public String U_ID;
		public String U_PASS;
		public boolean autoLogin;
	}

	/*
	 * 注册返回信息
	 */
	public class RegistRetInfo {
		public User user;
		public NetInfo netInfo;
	}

	// 用户注册
	public User userRegister(Context c, String houseNumber,
			String proprietorName, String telnumber, String password,
			Long propertyid);
	//用户通过手机获取验证码
	//判断注册或者修改密码(取值：1(表示注册)；2(表示修改))
	public NetInfos getPhoneCode(Context c,String phoneNum ,String appName,int sendType);
	
	//用户通过手机号码 验证验证码
	public NetInfos getVarificationCode(Context context, String phoneNum ,String smsCode);
	
	// 用户登录
	public User userLogin(Context c, String name, String password);

	// 常用信息返回数据
	public class InformationsInfo {
		public int informationID;
		public String header;
		public String body;
		public String dateTime;
		public NetInfo netInfo;
	}
	

	/**
	 * 获取常用信息
	 * 
	 * @param c
	 * @param propertyid
	 * @return
	 */
	public ArrayList<InformationsInfo> getInformations(Context c, int propertyid);

	// 有偿服务返回数据
	public class PaidServicesInfo {
		public int serviceID;
		public String serviceName;
		public String body;
		public String description;
		public String price;
		public String dateTime;
		public NetInfo netInfo;
	}

	public class OrderItemInfo {
		public ArrayList<OrderItem> orderInfo;
		public NetInfo netInfo;
	}
	
/**新建订单*/
	/**优惠*/
	public class ThePromotion {
	public String body;//内容
	public int LivingItemID;//生活项
	public long PromotionID;//优惠id
	public double Price;//价格
	public String ETime;//结束有效期
	public String path;//图片 url
	public int Height;//图片高度
	public int Width;//图片宽度
	public int SaleTypeID;//优惠售卖类型
	public String CTime;//开始有效期
	public String header;//标题
	public int Priority;//优先级
	public boolean ForSal;//是否支持客户端支付
	}
	/**优惠*/
	public class Promotion {
		public String body;//内容
		public int LivingItemID;//生活项
		public long PromotionID;//优惠id
		public double Price;//价格
		public String ETime;//结束有效期
		public String path;//图片 url
		public int Height;//图片高度
		public int Width;//图片宽度
		public int SaleTypeID;//优惠售卖类型
		public String CTime;//开始有效期
		public String header;//标题
		public int Priority;//优先级
		public boolean ForSal;//是否支持客户端支付
		public int PromotionTypeID;
	}
	
	/**订单状态*/
	public class Status{
		public int OrderStatusID;//订单状态id
		public String OrderStatusName;//订单状态名称
	}
	/**券码状态*/
	public class PromotionCodeStatus{
		public int PromotionCodeStatusID;//券码id
		public String PromotionCodeStatusName;//去吗状态名称
	}
	
	//获取promotionId 
	public Promotion getPromotionId(Context context,int propertyid,int userid,String description);
	
	// 4.12.1 订单
	public class OrderItem {
		// OrderID Varchar(20) 订单id
		// ThePromotion Promotion 优惠
		// Status OrderStatus 订单状态
		// UserID long 用户id
		// TheAddress Address 邮寄地址
		// Comment string 备注
		// OrderSequenceNumber long 订单序号
		// Number int 购买商品数量
		// Total Decimal(10,2) 订单总金额
		// Ctime Datetime 下单时间
		// AliOrderStr string 阿里支付订单信息（带签名）
		public String OrderID;// 订单id
		public Promotion ThePromotion;// 优惠
		public Status Status;// 订单状态
		public long UserID;// 用户ID
		public String TheAddress;// 邮寄地址
		public String Comment;// 备注
		public long OrderSequenceNumber;// 订单序号
		public int Number;// 购买商品数量
		public Double Total;// 订单总金额
		public String Ctime;// 下单时间
		public String TelNumber;//手机号码
		public WXPayInfo WxPayInfo;//微信支付
		public String AliOrderStr;// 阿里支付订单信息（带签名）
		public int flag;
		public String path;//图片路径
		public Drawable imgDw;
		public NetInfo netInfo;
	}
	
	  /**
     * 优惠
     */
    public class Promotion2 {
        public String body;//内容
        public int LivingItemID;//生活项
        public long PromotionID;//优惠id
        public double Price;//价格
        public String ETime;//结束有效期
        public String path;//图片 url
        public int Height;//图片高度
        public int Width;//图片宽度
        public int SaleTypeID;//优惠售卖类型
        public String CTime;//开始有效期
        public String header;//标题
        public int Priority;//优先级
        public boolean ForSal;//是否支持客户端支付
    }
	
    /**
     * 微信支付签名信息WXPayInfo
     */
    public class WXPayInfo {
        public String Appid;//appid
        public String PartnerId;//商户号
        public String PrepayId;//预支付交易回话id
        public String Package;//扩展字段
        public String NonceStr;//随机字符串
        public String TimeStamp;//时间戳
        public String Sign;//签名
    }



	/*// 新建订单
	public OrderItem createOrder(Context c, long promotionid, long userid,
			long addressid, String comment, int number,String paytype,long usercashcouponid,long workorderid);
*/
	// 已完成订单
	public class OrderInfo {
		public ArrayList<OrderItem> orderInfo;
		public NetInfo netInfo;
	}
	
	//获得券码
//	public class ProCode{
//		public String UTime;//使用时间
//		public PromotionCodeStatus status;//状态
//		public String BTime;//有效时间
//		public long promotionCodeID;
//	}
	public class PromotionCodeArray{
		public ArrayList<PromotionCode>proArray;
	}
	public PromotionCodeArray getPromotionCodeArray(Context context,long ordersequencenumber);
	/**
	 * PromotionCodeID	long	券码id
		Code	string	券码
		ThePromotion	Promotion	优惠
		Status	PromotionCodeStatus	券码状态
		BTime	datetime	开始有效期
		ETime	datetime	过期时间
		UTime	datetime	使用时间*/
	//券码
	public class PromotionCode{
		public long PromotionCodeID;//券码id
		public String Code;//券码
		public Promotion ThePromotion;//优惠
		public PromotionCodeStatus PromotionStatus;//券码状态
		public String BTime;//开始有效期
		public String ETime;//过期时间
		public String UTime;//使用时间
	}
	/**
	 * PromotionCodeStatusID	int	券码状态id
	 * PromotionCodeStatusName	string	券码状态名称
	 * */
//	//券码状态
//	public class PromotionCodeStatus{
//		public int PromotionCodeStatusID;
//		public String PromotionCodeStatusName;
//	}

	// 获取已完成订单 ordersequencenumber订单序号
	//获得已完成订单
	public OrderInfo getCompletedOrder(Context c, long userid, int pageindex);

	// 获取未完成的订单
	public OrderInfo getUncompletedOrder(Context c, long userid, int pageindex);

	// 3.13 生活项
	public class LivingItem {
		public int LivingItemID;
		public String LivingItemName;
		public String address;
		public String telephone;
		public String categories;
		public int distance;
		public int avg_price;
		public int has_coupon;
		public int has_activity;
		public Coupon coupon;
		public int has_deal;
		public ArrayList<Deal> deals;
		public String source;//信息来源
		public String Description;
		public String Ctime;
		public float latitude;
		public float longitude;
		public String Hours;
		public Boolean ForExpress;
		public LivingItemPicture FrontCover;
		public ArrayList<LivingItemPicture> livingItemPicture;
		public int priority;
		public int flag;
		public String path;
		public Drawable imgDw;
	}

	public class LivingItemInfo {
		public ArrayList<LivingItem> livingItem;
		public NetInfo netInfo;
	}
	
	/**
	 * 生活缴费
	 * @author liudongdong
	 *
	 */
	public class LivingPay{
		
	}
	public class LivingPayList{
		public ArrayList<LivingPay>livingPayList;
		public NetInfo netInfo;
	}

	/**
	 * 获取生活缴费
	 * @author liudongdong
	 *
	 */
	public LivingPayList getLivingPayList(Context c, int propertyid,
			String livingcategoryid, int pageindex);
	
	// 促销商品
	public class PromotionList {
		public ArrayList<Promotion> promotionList;
		public NetInfo netInfo;
	}

	// 3.11 团购
	public class Deal {
		public String id;
		public String description;
		public String url;

	}

	// 3.12 优惠券
	public class Coupon {
		public int coupon_id;
		public String coupon_description;
		public String coupon_url;

	}

	// 3.15 生活项图片
	public class LivingItemPicture {
		public long LivingItemPictureID;
		public String path;
		public String Description;
		public Drawable imgDw;
	}

	public class LivingItemPictureInfo {
		public ArrayList<LivingItemPicture> livingItemPicture;
		public NetInfo netInfo;
	}

	/**
	 * 获取生活项
	 */
	public LivingItemInfo getLivingItems(Context c, int propertyid,
			String livingcategoryid, int pageindex,double latitude,double longitude);


	/**
	 * 根据生活项ID获取生活项内容
	 * 
	 * @param c
	 * @param livingitemid
	 * @param source
	 * @return
	 */
	public LivingItem getLivingItemsByLivingItemId(Context c, int livingitemid,
			String source, int propertyid,double latitude,double longitude);

	/**
	 * 根据生活项ID获取指定生活项的图片列表
	 * 
	 * @param c
	 * @param livingitemid
	 * @param source
	 * @return
	 */
	public LivingItemPictureInfo getLivingItemsPictureByLivingItemId(Context c,
			int livingitemid, String source, int propertyid);

	/**
	 * 获取有偿服务
	 * 
	 * @param c
	 * @param propertyid
	 * @return
	 */
	public ArrayList<PaidServicesInfo> getPaidServices(Context c, int propertyid);

	// 消息分类列
	public class MessageTypeInfo {
		public int messageTypeID;
		public String messageTypeName;
	}

	// 站内消息
	public class MessageInfo {
		public Long messageID;
		public MessageTypeInfo type;
		public long userid;
		public long toUserId;
		public String header;
		public int RootID;
		public String body;
		public String cTime;
		public int isRead;
	}

	/**
	 * 获取消息分类列表
	 * 
	 * @param c
	 * @param propertyId
	 * @return
	 */
	public ArrayList<MessageTypeInfo> getMessageType(Context c, int propertyId);

	/**
	 * 发生用户新建消息
	 * 
	 * @param c
	 * @param userId
	 * @param propertyid
	 * @param messagetypeid
	 * @param header
	 * @param body
	 * @return
	 */
	public NetInfo sendMessage(Context c, long userId, int propertyid,
			int messagetypeid, String header, String body);

	/**
	 * 回复消息
	 * 
	 * @param c
	 * @param userId
	 * @param propertyid
	 * @param rootid
	 * @param header
	 * @param body
	 * @return
	 */
	public NetInfo sendMessageReply(Context c, long userId, int propertyid,
			int rootid, String header, String body);

	/**
	 * 获取收件箱消息列表
	 * 
	 * @param c
	 * @param propertyId
	 * @param userId
	 * @param pageIndex
	 * @return
	 */
	public ArrayList<MessageInfo> getMessageInbox(Context c, int propertyId,
			long userId, int pageIndex);

	/**
	 * 获取收件箱消息列表内容
	 * 
	 * @param c
	 * @param propertyId
	 * @param userId
	 * @param rootid
	 * @return
	 */
	public ArrayList<MessageInfo> getMessageInboxContent(Context c,
			int propertyId, long userId, long rootid);

	/**
	 * 获取发件箱消息列表
	 * 
	 * @param c
	 * @param propertyId
	 * @param userId
	 * @param pageIndex
	 * @return
	 */
	public ArrayList<MessageInfo> getMessageOutbox(Context c, int propertyId,
			long userId, int pageIndex);

	// 物业通知
	public class PropertyNotificationInfo {
		public int noteID;
		public String header;
		public String body;
		public String dateTime;
		public int priority;
		public String signature;

		public NetInfo netInfo;
	}

	/**
	 * 获取物业通知
	 * 
	 * @param c
	 * @param propertyId
	 * @param count
	 * @return
	 */
	public ArrayList<PropertyNotificationInfo> getPropertyNotification(
			Context c, int propertyId, int count);

	/**
	 * 发送通知物业代收
	 * 
	 * @param c
	 * @param userId
	 * @param propertyid
	 * @param comment
	 * @return
	 */
	public NetInfo sendPropertyCollection(Context c, long userId,
			long propertyid, String comment);

	// 快递带收服务
	public class ExpressService {
		public long expressServiceID;
		public String serviceName;
		public String comment;
		public String statusName;
		public String CTime;
		public String RTime;

	}

	/**
	 * 获取快递服务列表
	 * 
	 * @param c
	 * @param propertyId
	 */
	public ArrayList<ExpressService> getPropertyCollection(Context c,
			int propertyId, long userId, int pageIndex);

	// 用户中心返回数据
	public class UserCenterRetInfo {
		public User user;
		public ArrayList<Note> notes;
		public ArrayList<ProductMessage> messages;
		public NetInfo netInfo;
	}

	/***
	 * 用户中心返回数据
	 * 
	 * @param c
	 * @param userId
	 * @return
	 */
	public UserCenterRetInfo getUserCenter(Context c, long userId);

	// 修改密码
	public NetInfo modifyPassword(Context c, String telnumber,
			String newPwd);

	// 推送消息
	public class PushMessage implements Serializable {
		public long pushMessageId;
		public String header;
		public String msg;
		public String imgUrl;
		transient public Drawable imgDw;
		public boolean readed;
	}

	public class PushMessageRetInfo implements Serializable {
		public ArrayList<PushMessage> pushList;
		public NetInfo netInfo;
	}

	/**
	 * 获取push消息
	 * 
	 * @param context
	 * @param userId
	 * @param lastPushId
	 * @return
	 */
	public PushMessageRetInfo getPushMessage(Context context, long userId,
			long lastPushId);

	public class ClientConfig {
		public String logoUrl; // 启动界面图片Url
		public int logoId; // 启动界面图片的标识号
	}

	/**
	 * 联系发布人
	 * 
	 * @param propertyid
	 * @param userid
	 * @param fleaid
	 * @param body
	 * @return
	 */
	public NetInfo sendMessageFromFlea(int propertyid, long userid,
			long fleaid, String body);

	/**
	 * 发布跳骚商品
	 * 
	 * @param propertyid
	 * @param userid
	 * @param header
	 * @param description
	 * @param pictures
	 * @return
	 */
	public NetInfo sendFleaNew(int propertyid, long userid, String header,
			String description, File pictures);

	/**
	 * 编辑跳骚商品
	 * 
	 * @param fleaid
	 * @param propertyid
	 * @param userid
	 * @param header
	 * @param description
	 * @param deletelist
	 *            删除图片的id（fleaPictureID）列表 用逗号隔开
	 * @param pictures
	 * @return
	 */
	public NetInfo editFleaNew(long fleaid, int propertyid, long userid,
			String header, String description, String deletelist, File pictures);

	/**
	 * 删除跳骚商品
	 * 
	 * @param fleaid
	 * @param propertyid
	 * @param userid
	 * @return
	 */
	public NetInfo deleteFleaNew(long fleaid, int propertyid, long userid);

	/**
	 * 获取跳骚商品列表
	 * 
	 * @param propertyid
	 * @param pageindex
	 * @return
	 */
	public FleaInfo getFleaByProperty(int propertyid, int pageindex);

	/**
	 * 我的跳骚商品列表
	 * 
	 * @param propertyid
	 * @param userid
	 * @return
	 */
	public FleaInfo getFleaByOwner(int propertyid, long userid);

	/**
	 * 跳骚商品详情
	 * 
	 * @param propertyid
	 * @param fleaid
	 * @return
	 */
	public FleaContent getFleaContent(int propertyid, long fleaid);

	// 跳骚商品详情
	public class FleaContent {
		public String username;
		public Flea flea;
		public ArrayList<FleaPicture> fleaPictureArray;
		public NetInfo netInfo;
	}

	public class FleaInfo {
		public ArrayList<Flea> fleas;
		public NetInfo netInfo;
	}

	// 跳骚商品
	public class Flea {
		public long fleaID;// 跳骚商品id
		public long userID;// 商品发布人用户id
		public String header;// 商品标题
		public String description;// 商品描述
		public FleaPicture frontCover;// 封面图片
		public String cTime;// 发布时间

	}

	// 跳骚商品图片
	public class FleaPicture {
		public long fleaPictureID;// 图片id
		public String path;// 图片的url
		public String description;// 图片描述
		public Drawable imgDw;

	}

	// 推广应用
	public class AppPopularize {
		public long appID;// 图片id
		public String iconPath;// 图片的url
		public String appName;// 图片描述
		public String appDescription;// 图片描述
		public String url;
		public int priority;
		public Drawable imgDw;
		public String cTime;

	}

	public ArrayList<AppPopularize> getAppPopularize(Context c);

	// 基本商品信息
	public class ProductPic {
		public long imgId;
		public String imgUrl;
		public String desc;
		public Drawable imgDw;
	}

	public class ProductBase {
		public long productId;
		public long categoryId;
		public String productName;
		public double price;
		public double salePrice;
		public String saleWord; // 促销关键字
		public String dateTime;
		public ProductPic productPic;
	}

	public class HomeMessage {
		public ClientConfig clientConfig;
		public ArrayList<Note> notes; // 站内消息列表
		public boolean isVisitor;// 判断是否是游客
		public NetInfo netInfo;
	}

	public class UserCenter {
		public User user;
		public ArrayList<ProductMessage> messages;// 公告栏内容（商家快报）
		public ArrayList<Note> notes; // 站内消息列表
		public NetInfo netInfo;
	}

	// 商品分类
	public class ProductCategory {
		public long categoryID;
		public String categoryName;
		public long parentID;
		public int layer;
		public boolean isLeaf;
	}

	// 商品扩展属性
	public class ProductExtend {
		public String fieldKey;// 扩展属性建
		public Object fieldValue; // 扩展属性值
		public String disName; // 扩展属性显示名称
	}

	// 获取首页信息
	public HomeMessage getHomeMsg(int logoId, long userId, int propertyid);

	// 分类返回信息
	public class ProductCaategoryRetInfo {
		public ArrayList<ProductCategory> list;
		public NetInfo netInfo;
	}

	// 获取根分类列表
	public ProductCaategoryRetInfo getRootCategory();

	// 获取子分类列表
	public ProductCaategoryRetInfo getChildCategory(Long cId);

	public class ProductListRetInfo {
		public ArrayList<ProductBase> products;
		public ArrayList<ProductCategory> categorys;
		public NetInfo netInfo;
	}

	// 获取商品列表
	public ProductListRetInfo getProductList(Long cId, int sortPara,
			int pageindex);

	public class ProductDetailNew {
		public ProductBase product;
		public ArrayList<ProductExtend> pextends;
		public ArrayList<ProductPic> pics;
		public String otherInfo;
		public String productDesc;
		public NetInfo netInfo;
	}

	// 获取商品详情
	public ProductDetailNew getProductDetail(Long productid);

	//

	// 找回密码 TODO DELETE
	public int findPassword(Context c, String name);

	// 获得搜索产品列表
	public SearchResult getSearchResult(Context c, String searchKey, int type,
			int pageIndex, int pageSize);

	public QuickSearchResult getQuickSearchResult(Context c, String searchKey);

	public class SearchResult {
		public int resultType; // 返回结果类型：0-初始推荐；1-找到结果；2-没找到结果
		public List<String> hotKeyList;
		public List<ProductTopInfo> productResultList;
		public List<ProductTopInfo> productGuessList;
		public List<ProductTopInfo> productSuggestList;
	}

	public class QuickSearchResult {
		public List<QuickMatch> quickMatchList;
	}

	public class ProductTopInfo {
		public String productId;
		public String imageUrl;
		public String productDesc;
		public String productPrice;
		public String productMarketPrice;
		public Drawable imageDrawable;
	}

	public class QuickMatch {
		public String keyWord;
		public int number;
	}

	public NetInfo submitFeedback(Context c, long userid, FeadbackInfo info);

	public class FeadbackInfo {
		public int type;
		public String contact;
		public String content;
	}

	public class FavoriteRetInfo {
		public List<ProductBase> list;
		public NetInfo netInfo;
	}

	// 获取收藏夹
	public FavoriteRetInfo getFavoriteList(Context c, long userId,
			int pageindex, int pageSize);

	// 删除收藏夹商品
	public NetInfo deleteFromFavorite(Context c, long userId, long productId);

	// 添加商品到收藏夹
	public NetInfo addtoFavorite(Context c, long userId, long productId);

	//
	// AddressID long 邮寄地址id
	// UserID long 用户id
	// Recipient String 收件人姓名
	// TelNumber string 联系电话
	// CityArea string 城市区域
	// Detail string 详细地址
	// IsDefault bool 是否为默认邮寄地址
	public class CreateAddress {
		public long AddressID;// 邮寄地址id
		public long UserID;// 用户id
		public String Recipient;// 收件人姓名
		public String TelNumber;// 联系电话
		public String CityArea = "北京";// 城市区域
		public String Detail;// 详细地址
		public boolean IsDefault;// 是否为默认地址
		public NetInfo netInfo;
	}

	// 地址管理
	public class AddressItem implements Serializable {
		public long AddressID;// 邮寄地址id
		public long UserID;// 用户id
		public String Recipient;// 收件人姓名
		public String TelNumber;// 联系电话
		public String CityArea = "北京";// 城市区域
		public String Detail;// 详细地址
		public boolean IsDefault;// 是否为默认地址
	}

	// 地址
	public class AddressInfo implements Serializable {
		private String name;// 收件人姓名
		private String address;// 详细地址
		private String phone;// 手机号
		private String area;// 区域

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getAddress() {
			return address;
		}

		public void setAddress(String address) {
			this.address = address;
		}

		public String getPhone() {
			return phone;
		}

		public void setPhone(String phone) {
			this.phone = phone;
		}

		public String getArea() {
			return area;
		}

		public void setArea(String area) {
			this.area = area;
		}

	}

	// 地址区域
	public class AddressArea {
		public String areaId;
		public String areaName;
		// public boolean issupcod;
	}

	// 地理位置信息
	public class AreaInfo implements Serializable {
		private static final long serialVersionUID = 1L;

		public int id;
		public int parentid;
		public String name;
		public ArrayList<AreaInfo> list = new ArrayList<AreaInfo>();

	}


	//所有地址条目
	public class AddressAll{
		public ArrayList<AddressItem> addressItems;
		public NetInfo netInfo;
	}
	// 获取所有收货地址
	 public AddressAll getAllAddress(Context c, long uid);
	 
//	 
//	public void getAllAddress(String userID, String addressID,
//			com.wuxianyingke.property.activitiey.IRequest<AddressInfo> iRequest);

	// 更新/添加收货地址
	public CreateAddress updateAddress(Context c,long addressid,long userid,
			String recipient, String telnumber, String cityarea, String detail,
			boolean isDefault);

	/** 创建新的地址 */
	public CreateAddress createNewAddress(Context c, long userid,
			String recipient, String telnumber, String cityarea, String detail,
			boolean isDefault);
	

	public class ErrorInfo {
		public int code;
		public String desc;
	}

	// userid long 用户id
	// recipient String 收件人姓名
	// telnumber string 联系电话
	// cityarea string 城市区域
	// detail string 详细地址
	// isdefault bool 是否为默认邮寄地址
	public boolean applicationCanGo(Context ctx);

	// 地址信息
	public class CityInfo {
		public String city_id;
		public String city_name;
	}

	// 天气信息
	public class WeatherInfo {
		public String temp1;
		public String img_title_single;
		public String wind1;
		public String fl1;
	}

	// 专题
	public class TopicsProduct extends ProductTopInfo {
		public int sign; // 类型，保留字段
	}

	public ArrayList<TopicsProduct> getTopicsList(String topicId);

	/***
	 * 验证码
	 * 
	 * @author gaowenting
	 * 
	 */
	public class ValidateCode {
		public String validateCodeImg;
		public String validateCode;
	}

	/**
	 * 校验码
	 * 
	 * @author gaowenting
	 * 
	 */
	public class CheckCode {
		public String checkCode;
		public int code;
		public String desc;
	}

	/**
	 * 获取手机校验码
	 * 
	 * @param c
	 * @param mobile
	 * @return
	 */
	public CheckCode getCheckCode(Context c, String mobile);

	/**
	 * 设置默认地址
	 * 
	 * @param c
	 * @param userId
	 * @param addressid
	 * @return
	 */
	public ErrorInfo setDefaultAddress(Context c, long userId, String addressid);

	public ValidateCode getValidateCode(Context c, String imei);

	/***
	 * 获取商家快报列表
	 * 
	 * @param c
	 * @param count
	 * @return
	 */
	public ArrayList<ProductMessage> getproductMessage(Context c, int count);

	/***
	 * 获取站内信
	 * 
	 * @param c
	 * @param count
	 * @return
	 */
	public ArrayList<Note> getNoteMessage(Context c, long userid, int count);

	/***
	 * 发送已读站内信ID
	 * 
	 * @param c
	 * @param userid
	 * @return
	 */
	public NetInfo getNotesRemove(Context c, long userid, long noteid);

	// 获取地址
	public class Area {
		public String areaid;
		public String areaname;
		public NetInfo netInfo;
	}

	/***
	 * 获取地址列表
	 * 
	 * @param c
	 * @param areaid
	 * @param type
	 * @return
	 */
	public ArrayList<Area> getAreaList(Context c, String areaid, int type);

	// 个人资料
	public class PersonalInformation {
		public long userid;
		public String username;
		public String truename;
		public int gender;
		public String email;
		public String mobile;
		public String birthday;
		public String provinceid;
		public String provinceName;
		public String cityid;
		public String cityName;
		public String areaid;
		public String areaName;
		public String phone;
		public String postaddress;
		public String postcode;
		public NetInfo netInfo;
	}
	/**
	 * 获取免费wifi登陆信息
	 */
	public FreeWifi getFreeWifiInfo(Context c,long userid);

	/***
	 * 获取个人资料
	 * 
	 * @param c
	 * @param userid
	 * @return
	 */
	public PersonalInformation getPersonalInformation(Context c, long userid);

	/***
	 * 修改个人资料
	 * 
	 * @param c
	 * @param userid
	 * @param mPersonalInformation
	 * @return
	 */
	public NetInfo updatePersonalInformation(Context c, long userid,
			PersonalInformation mPersonalInformation);

	public WeatherInfo getWeather(Context c, String city);

	public WifiInfo sendGetWifi(Context c);

	PromotionList getActicityByLivingItemId(Context c, int livingitemid);

	PromotionList getProductByLivingItemId(Context c, int livingitemid);

//	public PromotionCodeArray getPromotionCodeArray(Context c, long ordersequencenumber);

	OrderInfo getUnUsePromotion(Context c, long userid, int pageindex);
	
	public class UpdateInfo{
		public String url;
		public int versionCode;
		public String updateInfo;
		public NetInfo netInfo;
		public String appversion;
	}
	public UpdateInfo getUpdateInfo(Context context,String appname);

}
