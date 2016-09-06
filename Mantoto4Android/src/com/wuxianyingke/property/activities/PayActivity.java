package com.wuxianyingke.property.activities;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.mantoto.property.R;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.umeng.message.PushAgent;
import com.wuxianying.alipay.sdk.pay.PayResult;
import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.LocalStore;
import com.wuxianyingke.property.common.SDCardUtils;
import com.wuxianyingke.property.remote.RemoteApi;
import com.wuxianyingke.property.remote.RemoteApi.Promotion;
import com.wuxianyingke.property.remote.RemoteApiImpl;

public class PayActivity extends BaseActivity implements OnClickListener {
    private Button topBar_left;
    private TextView topBar_text;
    private TextView payShouldMoney;//应付金额
    private TextView payActruallyMoney;//实付金额
    private TextView pay_couponTextView;//是否支持代金券支付显示
    private LinearLayout zhifubao;//支付宝支付
    private LinearLayout weixin;//微信支付
    private LinearLayout daijinquan;//代金券支付
    private LinearLayout repairShopLinearLayout;//维修费需要隐藏的内容
    private LinearLayout safeLinearLayout;//隐藏服务项
    private TextView serviceTextview;//服务条款
    private TextView sellerComments;//评论
    private Button payButton;//确认支付
    private ImageView zhifubaoSelect;//选择支付宝
    private ImageView weixinSelect;//选择微信支付
    private long workorderid;//工单id
    private long repairId;//报修id
    private String payType;//支付类型
    private IWXAPI api;
    private RemoteApi.OrderItem paymentOrder;//获取待支付订单
    private ProgressDialog mDialog;
    private long OderSequenceNumber;
    private long userCashCouponId;
    private boolean CouponUsable;//是否可以使用代金券
    private int couponPrice;//代金券金额
    private static final int SDK_PAY_FLAG = 5;
    private double payMoney;//支付金额
    private String desc;
    private double TotalPrice;
    private String sellers;
    private PopupWindow popupWindow;
    private Button btnCancel;
    private int s1;
    private int s2;
    private String picUrl;
    private Executor mExecutor = Executors.newFixedThreadPool(5); // 线程池对象
    private TextView advancePayment;//预付定金
    private Promotion promotion;
    private long promotionId;
    private int acturalMoney;
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Toast.makeText(PayActivity.this, "订单获取失败", Toast.LENGTH_SHORT).show();
                    break;
                case 500:
                    Toast.makeText(PayActivity.this, "错误详情：" + desc, Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    if (payType.equals("wx")) {

                    } else {

                    }
                    break;
                case 3:
                    mDialog.dismiss();
                    Toast.makeText(PayActivity.this, "订单获取失败", Toast.LENGTH_SHORT).show();
                    break;
                case 4:
                    mDialog.dismiss();
                    promotionId = promotion.PromotionID;
                    break;
                case 6:
                    mDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "暂无订单", Toast.LENGTH_SHORT).show();
                    break;
                case SDK_PAY_FLAG: {
                    PayResult payResult = new PayResult((String) msg.obj);

                    // 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
                    String resultInfo = payResult.getResult();
//
                    String resultStatus = payResult.getResultStatus();

                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        Intent intent = new Intent();
                        intent.setClass(PayActivity.this, PaySuccessActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        finish();
                    } else {
                        // 判断resultStatus 为非“9000”则代表可能支付失败
                        // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            Toast.makeText(PayActivity.this, "支付结果确认中",
                                    Toast.LENGTH_SHORT).show();

                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            Toast.makeText(PayActivity.this, "支付失败",
                                    Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent();
                            intent.setClass(PayActivity.this, PayFailActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(intent);
                            finish();
                        }
                    }
                    break;
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paymoney);
        setImmerseLayout(findViewById(R.id.IncludeMainTopBarInclude));
        PushAgent.getInstance(getApplicationContext()).onAppStart();
        api = WXAPIFactory.createWXAPI(PayActivity.this, Constants.WEICHAT_APPID);
        Bundle bundle = getIntent().getExtras();
        initView();
        if (bundle != null) {
            payMoney = bundle.getDouble("payMoney");
            topBar_text.setText("支付");
            acturalMoney=(int)payMoney;
            payActruallyMoney.setText(""+acturalMoney+"元");
            payShouldMoney.setText(""+acturalMoney+"元");
        }
        mDialog = ProgressDialog.show(PayActivity.this, null, "加载中......",
                true);
        Thread getOrderThread = new Thread() {
            @Override
            public void run() {
                RemoteApiImpl remoteApi = new RemoteApiImpl();
                promotion = remoteApi.getPromotionId(PayActivity.this, LocalStore.getUserInfo().PropertyID, (int)LocalStore.getUserInfo().userId, "wifi");
                Log.i("MyLog", "订单数据：" + workorderid);
                Message msg = new Message();
                if (promotion == null) {
                    msg.what = 3;
                    Log.i("MyLog", "OrderPaymentThread3");
                } else {
                    msg.what = 4;
                }
                mHandler.sendMessage(msg);
            }
        };
        getOrderThread.start();
    }

    private void initView() {
        //初始化标题
        topBar_left = (Button) findViewById(R.id.topbar_left);
        topBar_text = (TextView) findViewById(R.id.topbar_txt);
        topBar_left.setVisibility(View.VISIBLE);
        topBar_text.setVisibility(View.VISIBLE);
        topBar_text.setText("订金支付");

        payShouldMoney = (TextView) findViewById(R.id.pay_shouldMoney);
        payActruallyMoney = (TextView) findViewById(R.id.pay_actualyMoney);
        zhifubao = (LinearLayout) findViewById(R.id.zhifubaoLinearLayout);
        weixin = (LinearLayout) findViewById(R.id.weixinLinearLayout);
        daijinquan = (LinearLayout) findViewById(R.id.daijinquanLinearLayout);
        zhifubaoSelect = (ImageView) findViewById(R.id.zhifubaoSelect);
        weixinSelect = (ImageView) findViewById(R.id.weixinSelect);
        pay_couponTextView = (TextView) findViewById(R.id.pay_couponTextView);

        //默认选择支付宝支付 我喜欢
        payType = "ali";
        zhifubaoSelect.setBackgroundResource(R.drawable.ring);
        weixinSelect.setBackgroundResource(R.drawable.ring_normal);
    }

    //添加pop窗口关闭事件
    class poponDismissListener implements PopupWindow.OnDismissListener {

        @Override
        public void onDismiss() {
            // TODO Auto-generated method stub
            //在dismiss中恢复透明度
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.alpha = 1f;
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            getWindow().setAttributes(lp);
        }

    }

    class Clickable extends ClickableSpan implements View.OnClickListener {
        private final View.OnClickListener mListener;

        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(false);
        }

        public Clickable(View.OnClickListener listener) {
            mListener = listener;
        }

        @Override
        public void onClick(View view) {
            mListener.onClick(view);
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.topbar_left:
                finish();
                break;
            //支付宝支付
            case R.id.zhifubaoLinearLayout:
                payType = "ali";
                zhifubaoSelect.setBackgroundResource(R.drawable.ring);
                weixinSelect.setBackgroundResource(R.drawable.ring_normal);
                break;
            //微信支付
            case R.id.weixinLinearLayout:
                payType = "wx";
                weixinSelect.setBackgroundResource(R.drawable.ring);
                zhifubaoSelect.setBackgroundResource(R.drawable.ring_normal);
                break;
            //代金券支付
            case R.id.daijinquanLinearLayout:
                zhifubao.setBackgroundColor(Color.WHITE);
                weixin.setBackgroundColor(Color.WHITE);
                zhifubao.setEnabled(true);
                weixin.setEnabled(true);
                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("flag", 2);
                intent.setClass(PayActivity.this, CouponActivity.class);
                startActivityForResult(intent, 0);
                break;
            //确认支付
            case R.id.payButton:

              mDialog = ProgressDialog.show(PayActivity.this, null, "获取订单中......",
                      true);
              Thread createOrder = new Thread() {
                  @Override
                  public void run() {
                      RemoteApiImpl remoteApi = new RemoteApiImpl();
                      Log.i("MyLog", "1userId=" + LocalStore.getUserInfo().userId + "payMoney=" + payMoney + "payType=" + payType);
                      RemoteApi.OrderItem orderItem2 = remoteApi.createOrder(PayActivity.this, promotionId, LocalStore.getUserInfo().userId, 0, "支付WIFI费用", (int)payMoney, payType,userCashCouponId, 0, 0);
                      Log.i("MyLog", "2userId=--------------" + LocalStore.getUserInfo().userId + "payMoney=" + payMoney + "payType=" + payType+"acturalMoney="+(int)payMoney);
                      Message msg = new Message();
                      if (orderItem2 == null) {
                          PayActivity.this.runOnUiThread(new Runnable() {
                              @Override
                              public void run() {
                                  mDialog.dismiss();
                              }
                          });
                          msg.what = 1;
                      } else {
                          PayActivity.this.runOnUiThread(new Runnable() {
                              @Override
                              public void run() {
                                  mDialog.dismiss();
                              }
                          });
                          msg.what = 2;
                         if (acturalMoney<=0&&orderItem2.netInfo.code == 200) {
                        	 Intent intent = new Intent();
                             intent.setClass(PayActivity.this, PaySuccessActivity.class);
                             intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                             intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                             startActivity(intent);
							
						}else{
                          final String payInfo = orderItem2.AliOrderStr;
                          if (payType.equals("wx")) {
                              PayReq req = new PayReq();
                              req.appId = orderItem2.WxPayInfo.Appid;
                              req.partnerId = orderItem2.WxPayInfo.PartnerId;
                              req.prepayId = orderItem2.WxPayInfo.PrepayId;
                              req.nonceStr = orderItem2.WxPayInfo.NonceStr;
                              req.timeStamp = orderItem2.WxPayInfo.TimeStamp;
                              req.packageValue = orderItem2.WxPayInfo.Package;
                              req.sign = orderItem2.WxPayInfo.Sign;

                              Log.i("MyLog", "req.appId2=" + req.appId + "req.partnerId=" + req.partnerId + "req.prepayId=" +
                                      req.prepayId + "req.nonceStr=" + req.nonceStr + "req.timeStamp=" + req.timeStamp + "req.packageValue=" + req.packageValue
                                      + "req.sign=" + req.sign);
                              // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
                              api.sendReq(req);
                              finish();
                          } else {
                              Runnable payRunnable = new Runnable() {
                                  @Override
                                  public void run() {
                                      // 构造PayTask 对象
                                      PayTask alipay = new PayTask(PayActivity.this);
                                      // 调用支付接口，获取支付结果
                                      String result = alipay.pay(payInfo,true);
                                      Message msg = new Message();
                                      msg.what = SDK_PAY_FLAG;
                                      msg.obj = result;
                                      mHandler.sendMessage(msg);
                                  }
                              };
                              // 必须异步调用
                              Thread payThread = new Thread(payRunnable);
                              payThread.start();
                          }
						}
                      }
                      mHandler.sendMessage(msg);
                  }
              };
              createOrder.start();
              Log.i("MyLog", "3userId=" + LocalStore.getUserInfo().userId + "payMoney=" + payMoney + "payType=" + payType);
          
                break;
        }
    }

    public void download(final String url) {

        // 将网络请求处理的Runnable增加到线程池中
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {

                try {
                    HttpClient client = new DefaultHttpClient();
                    HttpGet get = new HttpGet(url);
                    HttpResponse response = client.execute(get);
                    if (response.getStatusLine().getStatusCode() == 200) {

                        byte[] bytes = EntityUtils.toByteArray(response
                                .getEntity());
                        //保存图片到本地
                        SDCardUtils.saveImage(url, bytes);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case RESULT_OK:
                Bundle bundle = data.getExtras();
                couponPrice = bundle.getInt("price");
                userCashCouponId = bundle.getLong("usercashcouponid");
                if (payMoney-couponPrice<=0) {
                	acturalMoney=0;
                    zhifubao.setBackgroundColor(Color.parseColor("#F2F2F2"));
                    weixin.setBackgroundColor(Color.parseColor("#F2F2F2"));
                    zhifubao.setEnabled(false);
                    weixin.setEnabled(false);
                    zhifubaoSelect.setBackgroundResource(R.drawable.ring_normal);
                }else{
                userCashCouponId = bundle.getLong("usercashcouponid");
                Log.i("My", "usercashcouponid"+userCashCouponId);
                acturalMoney=(int)(payMoney-couponPrice);}
                payActruallyMoney.setText("" + acturalMoney + "元");
                break;
        }
    }
}