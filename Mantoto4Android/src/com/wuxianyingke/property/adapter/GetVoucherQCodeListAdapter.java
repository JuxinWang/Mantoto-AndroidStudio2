package com.wuxianyingke.property.adapter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.mantoto.property.R;
import com.wuxianyingke.property.activities.CommitVoucherContentActivity;
import com.wuxianyingke.property.activities.CouponCodeActivity;
import com.wuxianyingke.property.fragment.NetworkUtils;
import com.wuxianyingke.property.remote.RemoteApi.PromotionCode;

public class GetVoucherQCodeListAdapter extends BaseAdapter {
	/**上下文参数的对象*/
	private Context mContext;
	/**数据源集合的对象*/
	private ArrayList<PromotionCode>proArray;
	private String orderId;

	public GetVoucherQCodeListAdapter(Context mContext,
			ArrayList<PromotionCode> proArray, String orderId) {
		super();
		this.mContext = mContext;
		this.proArray = proArray;
		this.orderId = orderId;
	}

	@Override
	public int getCount() {
		return proArray.size();//返回数据源大小
	}

	@Override
	public Object getItem(int position) {
		
		return proArray.get(position);//返回指定数据源位置的对象
	}

	@Override
	public long getItemId(int position) {
		
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder vHolder = null;
		if (convertView == null) { // 创建新的itemView
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.qr_code_list_item, null); // 加载item布局并生成View对象
			
			vHolder = new ViewHolder();
			vHolder.CodeId = (TextView) convertView.findViewById(R.id.order_Commit_CodeId);
			vHolder.use = (TextView) convertView.findViewById(R.id.order_Commit_UsedId);
			vHolder.imageView = (ImageView) convertView.findViewById(R.id.order_Commit_two_DimisionCodeId);
			vHolder.userNow = (TextView)convertView.findViewById(R.id.use_now);
			convertView.setTag(vHolder);
		} else {
			// 复用ListView滚出屏幕的itemView
			vHolder = (ViewHolder) convertView.getTag(); // 获取到可复用的item中的所有控件对象
			vHolder.imageView.setImageResource(R.drawable.code_logo); // 重置图片控件
		}

		vHolder.CodeId.setText(proArray.get(position).Code);
		vHolder.use.setText(""+proArray.get(position).PromotionStatus.PromotionCodeStatusName);
		//
		
		//生成二维码的方法
				String content=orderId+"|"+proArray.get(position).PromotionCodeID+"|"+proArray.get(position).Code;
				Log.i("MyLog", "ordersequencenumber="+orderId);
				// MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
				
				   Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
				   hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
				   BitMatrix bitMatrix;
				try {
					bitMatrix=new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, 400, 400,hints);
		            int[] pixels = new int[400 * 400];
		            //下面这里按照二维码的算法，逐个生成二维码的图片，
		            //两个for循环是图片横列扫描的结果
		            for (int y = 0; y < 400; y++)
		            {
		                for (int x = 0; x < 400; x++)
		                {
		                    if (bitMatrix.get(x, y))
		                    {
		                        pixels[y * 400 + x] = 0xff000000;
		                    }
		                    else
		                    {
		                       pixels[y * 400 + x] = 0xffffffff;
		                    }
		                }
		            }
//					 File file1 = new File(filePath);
//					 MatrixToImageWriter.writeToFile(bitMatrix, "jpg", file1);
		          //生成二维码图片的格式，使用ARGB_8888
		            Bitmap bitmap = Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888);
		            bitmap.setPixels(pixels, 0, 400, 0, 0, 400, 400);
		            //显示到一个ImageView上面
		            vHolder.imageView.setImageBitmap(bitmap);
				} catch (WriterException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
		
		
//		if(filePath==null){
//			vHolder.imageView.setImageResource(R.drawable.code_logo);
//		}else{
//			// 从SDCard中读取图片
//			Bitmap bitmap = SDCardUtils.readImage(filePath);
//				vHolder.imageView.setImageBitmap(bitmap);
//		}
//		

		return convertView;
	}
	
	
	class ViewHolder {
		public TextView CodeId; // 标题
		public ImageView imageView; // 图片
		public TextView use; //是否使用
		public TextView userNow;//立即使用

	}

}
