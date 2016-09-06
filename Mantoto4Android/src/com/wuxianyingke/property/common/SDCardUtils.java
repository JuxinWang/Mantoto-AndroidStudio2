package com.wuxianyingke.property.common;

import java.io.File;
import java.io.FileOutputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

/** 
* @ClassName: SDCardUtils 
* @Description: (SDCard存储工具类) 
* @author Liudongdong 
* @date 2015-8-21 上午10:12:14 
*  
*/
public class SDCardUtils {
	/**
	 *  保存图片的方法
	 * @param url  请求地址
	 * @param bytes 图片的字节数组对象
	 * @return
	 */
	public static boolean saveImage(String url, byte[] bytes) {
		if (!isUsable() || url==null)
			return false;

		File imageFile = new File(getImageDir(), getFileName(url));
		
		//如果文件存在则返回
		if(imageFile.exists()) return true;
		
		try {

			FileOutputStream foStream = new FileOutputStream(imageFile);
			foStream.write(bytes);
			
			foStream.flush(); //将缓存的数据写到文件中
			
			foStream.close();
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	// 读取指定的图片方法
	public static Bitmap readImage(String url) {
		if (!isUsable() || url==null) //判断SDCard是否可用
			return null;

		File imageFile = new File(getImageDir(), getFileName(url));
		
		//如果文件不存在，则直接返回
		if(!imageFile.exists()) return null;
		try {
			
			//将文件转成Bitmap对象
			Bitmap bitmap=BitmapFactory.decodeFile(imageFile.getAbsolutePath());
			
			return bitmap;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// 判断当前的SDcard是否可用
	public static boolean isUsable() {

		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
	}

	// 获取图片文件保存路径
	public static String getImageDir() {
		File file = new File(Constants.IMAGE_PATH);
		if (!file.exists())
			file.mkdirs();

		return Constants.IMAGE_PATH;
	}
	
	public static void initDBPath(){
		File file = new File(Constants.DB_PATH);
		if (!file.exists())
			file.mkdirs();
	}
	
	public static int clearCache(){
		File file=new File(Constants.CACHEPATH);
		if(!file.exists()) return 0;
		else return delFile(file);
	}

	public static int delFile(File file){
		if(file.isFile()){
			file.delete();
			return 1;
		}
		
		int fCount=0;
		for(File f:file.listFiles()){
			fCount+=delFile(f);
		}
		return fCount;
	}
	
	// 获取URL地址的文件名
	public static String getFileName(String url) {
		String fileName = url.substring(url.lastIndexOf("/") + 1);
		return fileName;
	}
	
	
}
