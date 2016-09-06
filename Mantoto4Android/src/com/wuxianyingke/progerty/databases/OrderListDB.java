package com.wuxianyingke.progerty.databases;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.wuxianyingke.property.remote.RemoteApi.OrderItem;

public class OrderListDB extends SQLiteOpenHelper
{


	private static final String DATABASE_NAME = "order.db";
	private static final int DATABASE_VERSION = 1;// not used

	private static final String ORDER_TABLE = "order_table";

	public final static String YEAR = "year";
	public final static String MONTH = "month";
	public final static String CALL_IN = "call_duration_in";
	public final static String CALL_OUT = "call_duration_out";
	public final static String SMS_IN = "incomming_sms_count";
	public final static String SMS_OUT = "sent_sms_count";
	public final static String MOBILE_SIZE = "mobile_traffic";
	public final static String WIFI_SIZE = "wifi_traffic";
	
	
	public final static String OrderID="OrderID";//订单id
	public final static String ThePromotion="ThePromotion";//优惠券
	public final static String Status="Status";//订单状态
	public final static String UserID="UserID";//用户id
	public final static String TheAddress="TheAddress";//邮寄地址
	public final static String Comment="Comment";//备注
	public final static String OrderSequenceNumber="OrderSequenceNumber";//订单序号
	public final static String Number="Number";//购买商品数量
	public final static String Total="Total";//订单总金额
	public final static String Ctime="Ctime";//下单时间
	public final static String AliOrderStr="AliOrderStr";//阿里支付订单信息（带签名）
	
	public final static String path="path";
	public final static String flag="flag";//1:canyin;2:gouwu;3:shenghuofuwu
	
	public OrderListDB(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{

		db.execSQL("CREATE TABLE IF NOT EXISTS " + ORDER_TABLE + 
				"(" + "id integer primary key autoincrement, "
				+ flag+ " integer, "
				+ OrderID+ " integer, "
				+ ThePromotion + " integer, " 
				+ Status + " text, "
				+ UserID + " integer, "
				+ TheAddress + " text, "
				+ Comment + " text, "
				+ OrderSequenceNumber + " integer, " 
				+ Number+ " integer, "
				+ Total + " integer," 
				+ Ctime + " text,"
				+ AliOrderStr + " text,"
				+ path + " text"
				+ " );");
		
//		+ flag+ " integer, "
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		db.execSQL("DROP TABLE IF EXISTS " + ORDER_TABLE);
		onCreate(db);
	}

	// Application
	public int insertOneItem(OrderItem item)
	{
		int ret = -1;

		SQLiteDatabase db = this.getWritableDatabase();
//        Log.d("MyTag","insertOneItem flag="+item.flag );
//        Log.d("MyTag","insertOneItem distance 103="+item.distance);
		
		// 首先根据年月判断是否存在该记录
		String sql = "select * from " + ORDER_TABLE + " where  " + OrderID + "=" + item.OrderID + ";";
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor.moveToFirst())
		{
			db.close();
			return cursor.getInt(0);
		}
		long result = db.insert(ORDER_TABLE, null, getContentCalues(item));

		ret = (int) result;
		cursor.close();
		db.close();
		return ret;
	}

	public void deleteOneApp(int id)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		String sql = "delete from " + ORDER_TABLE + " where OrderID=\"" + id + "\";";

		Log.d("MyTag","deleteOneApp Query sql="+sql );
		try
		{
			db.execSQL(sql);
		} catch (Exception ex)
		{
			ex.printStackTrace();
		} finally
		{
			db.close();
		}
	}
	
	public ArrayList<OrderItem> getAllItem(int orderflag)
	{
//		 Log.d("MyTag","insertOneItem getAllItem(int flag)="+flag );
		SQLiteDatabase db = this.getReadableDatabase();
		String sql = "select * from " + ORDER_TABLE + " where  " + flag + "=" + orderflag + ";";

		Log.d("MyTag","LivingDb sql 142 ="+sql );
		Cursor c = db.rawQuery(sql, null);
		ArrayList<OrderItem> list = new ArrayList<OrderItem>();
		if (c.moveToFirst())
		{
			do
			{
//				OrderID	Varchar(20)	订单id
//				ThePromotion	Promotion	优惠
//				Status	OrderStatus	订单状态
//				UserID	long	用户id
//				TheAddress	Address	邮寄地址
//				Comment	string	备注
//				OrderSequenceNumber	long	订单序号
//				Number	int	购买商品数量
//				Total	Decimal(10,2)	订单总金额
//				Ctime	Datetime	下单时间
//				AliOrderStr	string	阿里支付订单信息（带签名）

				 Log.d("MyTag","insertOneItem getAllItem="+c.getInt(1) );
				 Log.d("MyTag","distance151 ="+c.getInt(7) );
				 OrderItem item = new OrderItem();
				item.flag = c.getInt(1);
//				item.OrderID = c.getInt(2);
//				item.ThePromotion = c.getString(3);
//				item.Status = c.getInt(4);
				item.UserID = c.getLong(5);
				item.TheAddress = c.getString(6);
				item.Comment = c.getString(7);
				item.OrderSequenceNumber = c.getLong(8);
				item.Number = c.getInt(9);
				item.Total = c.getDouble(10);
				item.Ctime = c.getString(11);
				item.AliOrderStr = c.getString(12);
//				LivingItemPicture ft=new LivingItemPicture();
//				ft.path=item.path;
//				item.FrontCover=ft;
				list.add(item);
				
			} while (c.moveToNext());
		}
		c.close();
		db.close();
		return list;
	}
	// Application
		public int getOneItem(int mOrderItemID)
		{
			int ret = -1;

			SQLiteDatabase db = this.getWritableDatabase();
	        Log.d("MyTag","getOneItem mLivingItemID="+mOrderItemID );
			// 首先根据年月判断是否存在该记录
			String sql = "select * from " + ORDER_TABLE + " where  " + OrderID + "=" + mOrderItemID + ";";
			Cursor cursor = db.rawQuery(sql, null);
			if (cursor.moveToFirst())
			{
				db.close();
				return cursor.getInt(0);
			}
			cursor.close();
			db.close();
			return ret;
		}

	/*public int updateOneItemByMonth(LivingItem item)
	{
		int ret = -1;

		SQLiteDatabase db = this.getWritableDatabase();

		// 首先根据年月判断是否存在该记录
		String sql = "select * from " + MONTH_TABLE + " where " + YEAR + "=" + item.year + " and " + MONTH + "="
				+ item.month + ";";
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor.moveToFirst())
		{
			int id = cursor.getInt(0);

			// 记录存在，更新该记录
			db.beginTransaction();

			ContentValues cv = new ContentValues();
			cv.put(CALL_IN, item.call_in);
			cv.put(CALL_OUT, item.call_out);
			cv.put(SMS_IN, item.sms_in);
			cv.put(SMS_OUT, item.sms_out);
			cv.put(MOBILE_SIZE, item.mobile_size);
			cv.put(WIFI_SIZE, item.wifi_size);
			
			String where = "id =?";
			String[] whereValue = { Integer.toString(id) };
			int columns = db.update(MONTH_TABLE, cv, where, whereValue);
			db.setTransactionSuccessful();

			db.endTransaction();

			ret = id;
		}
		else
		{
			// 记录不存在，添加记录
			ContentValues cv = new ContentValues();
			cv.put(YEAR, item.year);
			cv.put(MONTH, item.month);
			cv.put(CALL_IN, item.call_in);
			cv.put(CALL_OUT, item.call_out);
			cv.put(SMS_IN, item.sms_in);
			cv.put(SMS_OUT, item.sms_out);
			cv.put(MOBILE_SIZE, item.mobile_size);
			cv.put(WIFI_SIZE, item.wifi_size);
			long result = db.insert(MONTH_TABLE, null, cv);
			ret = (int) result;
		}
		cursor.close();
		db.close();
		return ret;
	}*/

	/*public MonthLogItem getOneItemByMonth(MonthLogItem item)
	{
		MonthLogItem ret = new MonthLogItem();
		ret.id = -1;
		
		SQLiteDatabase db = this.getWritableDatabase();

		// 首先根据年月判断是否存在该记录
		String sql = "select * from " + MONTH_TABLE + " where " + YEAR + "=" + item.year + " and " + MONTH + "="
				+ item.month + ";";
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor.moveToFirst())
		{
			ret.id = cursor.getInt(0);
			ret.year = cursor.getInt(1);
			ret.month = cursor.getInt(2);
			ret.call_in = cursor.getInt(3);
			ret.call_out = cursor.getInt(4);
			ret.sms_in = cursor.getInt(5);
			ret.sms_out = cursor.getInt(6);
			ret.mobile_size = cursor.getInt(7);
			ret.wifi_size = cursor.getInt(8);
		}
		cursor.close();
		db.close();
		return ret;
	}
	*/

	
	public ContentValues getContentCalues(OrderItem item) {
		ContentValues cv = new ContentValues();
		cv.put(flag, item.flag);
		cv.put(OrderID, item.OrderID);
//		cv.put(ThePromotion, item.ThePromotion);
//		cv.put(Status, item.Status);
		cv.put(UserID, item.UserID);
		cv.put(TheAddress, item.TheAddress);
		cv.put(Comment, item.Comment);
		cv.put(OrderSequenceNumber, item.OrderSequenceNumber);
		cv.put(Number, item.Number);
		cv.put(Total, item.Total);
		cv.put(Ctime, item.Ctime);
		cv.put(AliOrderStr, item.AliOrderStr);
		cv.put(path, item.path);

		return cv;
	}

}
