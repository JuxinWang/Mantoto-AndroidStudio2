package com.wuxianyingke.property.adapter;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mantoto.property.R;
import com.wuxianyingke.property.activities.AddBianqianActivity;
import com.wuxianyingke.property.activities.AddressActivity;
import com.wuxianyingke.property.activities.AddressEditActivity;
import com.wuxianyingke.property.activities.StickerActivity;
import com.wuxianyingke.property.common.LocalStore;
import com.wuxianyingke.property.remote.RemoteApi;
import com.wuxianyingke.property.remote.RemoteApiImpl;
import com.wuxianyingke.property.remote.RemoteApi.AddressItem;
import com.wuxianyingke.property.remote.RemoteApi.CreateAddress;
import com.wuxianyingke.property.threads.GetAddressListThread;
import com.wuxianyingke.property.widget.FilpperListvew;

public class AddressAdapter extends BaseAdapter {
	private GetAddressListThread mThread = null;
	private ArrayList<AddressItem> mList;
	private Context mContext;
	private static AddressAdapter mListAdapter;
	private FilpperListvew listView;
	private AddressActivity address;

	public AddressAdapter(Context ctx, ArrayList<AddressItem> list) {
		this.mContext = ctx;
		this.mList = list;
	}

	@Override
	public int getCount() {
		Log.i("MyTag", "Mlist----------------------------------------------"+mList);
		return mList.size();
	}
	
	@Override
	public Object getItem(int position) {
		return mList.get(position);

	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int arg0, View convertView, ViewGroup parent) {
		ProductItem productItem;
		final int position = arg0;

		Log.d("MyTag","CanYinListAdapter--position="+position);

		final AddressItem info = mList.get(position);

		if (convertView == null) {
			productItem = new ProductItem();
  		   View v = LayoutInflater.from(mContext).inflate(
					R.layout.user_address_item, null);
			productItem.mName = (TextView) v.findViewById(R.id.tv_nameId);
			productItem.mPhoneNumber = (TextView) v.findViewById(R.id.tv_phoneNumberId);
			productItem.mAddress = (TextView) v.findViewById(R.id.tv_AddressId);
			productItem.mItemBackground = (LinearLayout) v
					.findViewById(R.id.product_list_item_bg);
			//productItem.addressEdit = (LinearLayout) v.findViewById(R.id.address_edit);
			productItem.deladdressbtn = (Button) v.findViewById(R.id.user_address_delete);
			productItem.getaddressbtn = (Button) v.findViewById(R.id.user_address_get);
			v.setTag(productItem);
			convertView = v;

			/*convertView.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(View arg0) {
					Dialog dialog = new AlertDialog.Builder(mContext)
					.setTitle("删除地址？")
					.setIcon(R.drawable.code_logo)
					.setMessage("是否确认删除！")
					.setPositiveButton("是", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {


							Thread DeleteThread=new Thread(){
								@Override
								public void run() {
									RemoteApiImpl rai=new RemoteApiImpl();
									CreateAddress cAddress=rai.deleteAddress(mContext,position);
									Message msg=new Message();

								};

							};
							DeleteThread.start();
							mList.remove(position);

							mThread.start();
							mListAdapter=new AddressAdapter(mContext, mThread.getAddress());
							listView.setAdapter(mListAdapter);
							notifyDataSetChanged();

						}
					})
					.setNegativeButton("否", null)
					.create();
					dialog.show();


					return false;
				}
			});*/
		} else {
			productItem = (ProductItem) convertView.getTag();
		}

		
		productItem.mName.setText(info.Recipient);
		productItem.mPhoneNumber.setText(info.TelNumber);
		productItem.mAddress.setText(info.Detail);
		/*productItem.addressEdit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
						| Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.setClass(mContext, AddressEditActivity.class);
				intent.putExtra("name", info.Recipient);
				intent.putExtra("telnumber",info.TelNumber);
				intent.putExtra("address",info.Detail);
				((AddressActivity)mContext).startActivityForResult(intent, 0);
			}
		});*/

		//productItem.mRenjun.setText("￥:"+info.avg_price+"/人");
//		productItem.mRenjun.setText("");
//		Log.d("MyTag","CanYinListAdapter--info.distance="+info.distance);
//		Log.d("MyTag","productItem.mJuli="+productItem.mJuli);
//		if(info.distance<=999)
//		{
//
//			productItem.mJuli.setText(""+String.valueOf(info.distance)+"m");
//		}
//		else
//		{
//			double price = (double)(info.distance)/1000;
//			productItem.mJuli.setText(""+String.valueOf(price)+"km");
//		}
//
//		if(info.source.equals("own"))
//		{
//			productItem.mItemBackground.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					Intent intent = new Intent(mContext,
//							CanyinDetailOwnActivity.class);
//					intent.putExtra(Constants.CANYIN_ID_ACTION, info.LivingItemID);
//					intent.putExtra(Constants.CANYIN_SOURCE_ACTION, info.source);
//					intent.putExtra(Constants.SHOUCANG_FLAT, shoucang_flag);
//					intent.putExtra(Constants.FAVORITE_FLAT, favorite_flat);
//
//					mContext.startActivity(intent);
//				}
//			});
//		}
//		else
//		{
//			productItem.mItemBackground.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					Intent intent = new Intent(mContext,
//							CanyinDetailActivity.class);
//					intent.putExtra(Constants.CANYIN_ID_ACTION, info.LivingItemID);
//					intent.putExtra(Constants.CANYIN_SOURCE_ACTION, info.source);
//					intent.putExtra(Constants.SHOUCANG_FLAT, shoucang_flag);
//					intent.putExtra(Constants.FAVORITE_FLAT, favorite_flat);
//					mContext.startActivity(intent);
//				}
//			});
//		}





		return convertView;
	}



	class ProductItem {
		LinearLayout mItemBackground;
		TextView mName;
		TextView mPhoneNumber;
		TextView mAddress;
		//LinearLayout addressEdit;
		Button deladdressbtn;
		Button getaddressbtn;
	}


}
