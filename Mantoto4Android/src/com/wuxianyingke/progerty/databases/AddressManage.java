package com.wuxianyingke.progerty.databases;


/** 
* @ClassName: AddressManage 
* @Description: TODO(地址数据库管理) 
* @author Liudongdong 
* @date 2015-8-10 下午1:30:11 
*  
*/
//public class AddressManage {
//
//	public void addaddress(AddressInfo address, Context context) {
//		SQLiteDatabase sqLiteDatabase = DataBaseHelper.getInstance(context)
//				.getWritableDatabase();
//		ContentValues contentValues = new ContentValues();
//		contentValues.put("name", address.getName());
//		contentValues.put("address", address.getAddress());
//		contentValues.put("phone", address.getPhone());
//		contentValues.put("area", address.getArea());
//
//		if (sqLiteDatabase != null) {
//			sqLiteDatabase.insert("AddressInfo", null, contentValues);
//			sqLiteDatabase.close();
//			DataBaseHelper.closeDB();
//		}
//	}
//
//
//	public List<AddressInfo> getaddresss(Context context, String type) {
//		List<AddressInfo> addresss = new ArrayList<AddressInfo>();
//		SQLiteDatabase sqLiteDatabase = DataBaseHelper.getInstance(context)
//				.getReadableDatabase();
//		if (sqLiteDatabase != null) {
//			Cursor cursor = sqLiteDatabase.query("AddressInfo", null, null,
//					null, null, null, null);
//			while (cursor.moveToNext()) {
//				AddressInfo address = new AddressInfo();
//
//				address.setName(cursor.getString(cursor.getColumnIndex("name")));
//				address.setAddress(cursor.getString(cursor
//						.getColumnIndex("address")));
//				address.setPhone(cursor.getString(cursor
//						.getColumnIndex("phone")));
//				address.setArea(cursor.getString(cursor
//						.getColumnIndex("area")));
//
//				addresss.add(address);
//			}
//			sqLiteDatabase.close();
//			DataBaseHelper.closeDB();
//		}
//		return addresss;
//	}
//
//}
