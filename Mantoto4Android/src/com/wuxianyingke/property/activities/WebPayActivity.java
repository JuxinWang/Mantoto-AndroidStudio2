package com.wuxianyingke.property.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import com.mantoto.property.R;
import com.umeng.message.PushAgent;

public class WebPayActivity extends Activity {
	/**提交订单标题*/
	private TextView topbar_txt;
	/**左侧返回按钮*/
	private Button topbar_left;
	/**是否收藏标志*/
	private int favorite_flag;
	private String aliOrderStr;
	private WebView webView;
	/**网页支付 */
	public static String ALIPAY = "https://mapi.alipay.com/gateway.doalipay.wap.create.direct.pay.by.user";
	private String url;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		PushAgent.getInstance(getApplicationContext()).onAppStart();
		setContentView(R.layout.webview_pay_activity);
		
		aliOrderStr=getIntent().getStringExtra("aliOrderStr");
//		url=ALIPAY+aliOrderStr;
		url=ALIPAY+"";
		// 顶部导航
		topbar_txt = (TextView) findViewById(R.id.topbar_txt);
		topbar_txt.setText("漫途社区");
		topbar_left = (Button) findViewById(R.id.topbar_left);
		topbar_left.setVisibility(View.VISIBLE);
		webView=(WebView) findViewById(R.id.webViewId);
		webView.getSettings().setJavaScriptEnabled(true);
		
		webView.loadUrl(url);
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}

			// 页面载入完成后调用
			@Override
			public void onPageFinished(WebView view, String url) {
				if (url.toString().contains("result=success")) {
					WebPayActivity.this.finish();
				}
				super.onPageFinished(view, url);
			}
		});
		
		
		// 左侧返回菜单处理事件
		topbar_left.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View v) {

		// TODO Auto-generated method stub
			if (0 != favorite_flag) {
				Intent intent = new Intent();
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
						| Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.setClass(getApplicationContext(),
									CommitPayOrderActivity.class);
				startActivity(intent);
			} else {
			finish();
			}

			}
	});
	}
}
