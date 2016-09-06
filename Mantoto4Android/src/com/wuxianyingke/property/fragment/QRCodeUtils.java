//package com.wuxianyingke.property.fragment;
//
//import java.io.FileOutputStream;
//
//import android.graphics.Bitmap;
//
//import com.google.zxing.BarcodeFormat;
//import com.google.zxing.common.BitMatrix;
//import com.google.zxing.qrcode.QRCodeWriter;
//
//public class QRCodeUtils {
//
//	 /**
//     * 生成二维码Bitmap
//     *
//     * @param content   内容
//     * @param widthPix  图片宽度
//     * @param heightPix 图片高度
//     * @param logoBm    二维码中心的Logo图标（可以为null）
//     * @param filePath  用于存储二维码图片的文件路径
//     * @return 生成二维码及保存文件是否成功
//     */
//    public static boolean createQRImage(String content, int widthPix, int heightPix, String filePath) {
//        try {
//            if (content == null || "".equals(content)) {
//                return false;
//            }
//
////            //配置参数
////            Map<EncodeHintType, Object> hints = new Map<EncodeHintType, Object>();
////            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
////            //容错级别
////            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
//            //设置空白边距的宽度
////            hints.put(EncodeHintType.MARGIN, 2); //default is 4
//
//            // 图像数据转换，使用了矩阵转换
//            BitMatrix bitMatrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, widthPix, heightPix);
//            int[] pixels = new int[widthPix * heightPix];
//            // 下面这里按照二维码的算法，逐个生成二维码的图片，
//            // 两个for循环是图片横列扫描的结果
//            for (int y = 0; y < heightPix; y++) {
//                for (int x = 0; x < widthPix; x++) {
//                    if (bitMatrix.get(x, y)) {
//                        pixels[y * widthPix + x] = 0xff000000;
//                    } else {
//                        pixels[y * widthPix + x] = 0xffffffff;
//                    }
//                }
//            }
//
//            // 生成二维码图片的格式，使用ARGB_8888
//            Bitmap bitmap = Bitmap.createBitmap(widthPix, heightPix, Bitmap.Config.ARGB_8888);
//            bitmap.setPixels(pixels, 0, widthPix, 0, 0, widthPix, heightPix);
//
//            //必须使用compress方法将bitmap保存到文件中再进行读取。直接返回的bitmap是没有任何压缩的，内存消耗巨大！
//            return bitmap != null && bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(filePath));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return false;
//    }
//}
