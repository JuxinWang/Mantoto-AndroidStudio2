package com.wuxianyingke.property.activities;



import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.mantoto.property.R;
import com.wuxianyingke.progerty.databases.LivingDB;
import com.wuxianyingke.property.adapter.ImageAdapter;
import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.LocalStore;
import com.wuxianyingke.property.remote.RemoteApi;
import com.wuxianyingke.property.threads.GetCanyinDetailThread;
import com.wuxianyingke.property.views.IndicationDotList;
import com.wuxianyingke.property.views.MyGallery;

public class CanyinOwnDetailActivity extends BaseActivity implements View.OnClickListener {
    private TextView yingyeshijianTv,dianhuaTv, dizhiTv, miaoshuTv;;
    private ImageView peisongImg, dianhuaImg, dizhiImg;


    private MyGallery mMyGallery = null;
    private IndicationDotList mDotList = null;
    private int popGalleryIndex = 0; // pop弹出的是那张图片

    private ImageAdapter mAdapter = null ;
    private PopupWindow mImgPop = null;
    private View mImgView = null;
    private MyGallery mPopGallery = null;

    private Button mPopDownloadBtn = null;
    private IndicationDotList mPopIndicationDotList = null;

    private ImageAdapter mPopAdapter = null;
    private RemoteApi.LivingItem mProductDetail ;

    private Button LianxiButton ;

    private String telephone;
    private float latitude, longitude;
    private ProgressDialog mWaitLoading = null;
    private GetCanyinDetailThread mThread = null;

    private Button topbar_left, topbar_right;
    private TextView topbar_txt;

    private int propertyid;
    private int mLivingItemID = 0;

    private int shoucang_flag;
    private int shoucangStatus;
    private int favorite_flag;
    private String source = "";

    private LinearLayout shopGoods,shopActivity,shopCoupon;




    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){


                case Constants.MSG_GET_PRODUCT_DETAIL_FINISH:
                    if(mWaitLoading != null && mWaitLoading.isShowing()){
                        mWaitLoading.dismiss() ;
                    }
                    if(mThread != null && mThread.getProductDetail() != null){

                        mProductDetail = mThread.getProductDetail() ;
                        Gallery.LayoutParams params = new Gallery.LayoutParams(220,
                                220);
                        mAdapter = new ImageAdapter(CanyinOwnDetailActivity.this,
                                params, ImageView.ScaleType.CENTER_CROP);
                        mMyGallery.setAdapter(mAdapter);

                        topbar_txt.setText(mProductDetail.LivingItemName);
                        yingyeshijianTv.setText("营业时间：" + mProductDetail.Hours);
                        dianhuaTv.setText(mProductDetail.telephone);
                        telephone = mProductDetail.telephone;
                        dizhiTv.setText("地址：" + mProductDetail.address);
                        Boolean ForExpress = mProductDetail.ForExpress;
                        if (ForExpress)
                            peisongImg.setVisibility(View.VISIBLE);
                        else {
                            peisongImg.setVisibility(View.GONE);
                        }
                        latitude = mProductDetail.latitude;
                        longitude = mProductDetail.longitude;
                        dizhiImg.setVisibility(View.VISIBLE);

                    }
                    break;
                case Constants.MSG_GET_PRODUCT_DETAIL_FAILD:
                case Constants.MSG_GET_PRODUCT_DETAIL_NET_ERROR:
                    if (mWaitLoading != null && mWaitLoading.isShowing()) {
                        mWaitLoading.dismiss();
                    }
                    View view = (View) findViewById(R.id.view_network_error);
                    view.setVisibility(View.VISIBLE);
                    break;
                case Constants.MSG_GET_PRODUCT_DETAIL_IMG_FINISH:
                    Log.d("MyTag", "mProductDetail.fleaPictureArray.get(i)"
                            + mProductDetail.livingItemPicture.get(msg.arg1).path);
                    Log.d("MyTag", "msg.arg1" + msg.arg1);
                    mAdapter.addImg(mProductDetail.livingItemPicture.get(msg.arg1).imgDw);
                    mDotList.setCount(mDotList.getCount() + 1);
                    break;
                case Constants.MSG_ADD_FAVORITE_FINISH:
                    if (mWaitLoading != null && mWaitLoading.isShowing()) {
                        mWaitLoading.dismiss();
                    }
                    RemoteApi.NetInfo mErrorInfo = (RemoteApi.NetInfo) msg.obj;
                    if (!"".equals(mErrorInfo.desc) && mErrorInfo.desc != null) {
                        Toast.makeText(CanyinOwnDetailActivity.this,
                                mErrorInfo.desc, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(CanyinOwnDetailActivity.this,
                                "读取失败，可能网络问题或服务器无反应", Toast.LENGTH_SHORT).show();
                    }
                    break;




            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canyin_own_detail);
        mLivingItemID = getIntent().getIntExtra(Constants.CANYIN_ID_ACTION , 0) ;

        propertyid=LocalStore.getUserInfo().PropertyID;

        // 左侧返回按钮
        topbar_txt = (TextView) findViewById(R.id.topbar_txt);
        topbar_left = (Button) findViewById(R.id.topbar_left);
        topbar_left.setVisibility(View.VISIBLE);
        topbar_right = (Button) findViewById(R.id.topbar_button_right) ;
        topbar_right.setVisibility(View.VISIBLE);
        LivingDB db = new LivingDB(this);
        shoucangStatus=db.getOneItem(mLivingItemID);
        db.close();

        if(-1!=shoucangStatus){
            topbar_right.setBackgroundResource(R.drawable.shop_collection_clicked);
        }else{
            topbar_right.setBackgroundResource(R.drawable.shop_collection);
        }

        topbar_right.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                LivingDB db = new LivingDB(CanyinOwnDetailActivity.this);
                mProductDetail.flag=shoucang_flag;

//				Log.d("MyTag", "deleteOneApp2 ----- ----------shoucang_flag=" + shoucang_flag);
//				Log.d("MyTag", "deleteOneApp2 ----- ----------shoucang_flag=" + mProductDetail);
//				Log.d("MyTag", "deleteOneApp2 ----- ----------shoucangStatus=" + shoucangStatus);

                if(-1!=shoucangStatus)
                {

                    shoucangStatus = -1;
                    db.deleteOneApp(mProductDetail.LivingItemID);
                    topbar_right.setBackgroundResource(R.drawable.shop_collection);
                    Toast.makeText(CanyinOwnDetailActivity.this, "取消收藏成功", Toast.LENGTH_SHORT).show();
                }else
                {


                    db.insertOneItem(mProductDetail);
                    shoucangStatus=db.getOneItem(mLivingItemID);
                    topbar_right.setBackgroundResource(R.drawable.shop_collection_clicked);
                    Toast.makeText(CanyinOwnDetailActivity.this, "收藏成功", Toast.LENGTH_SHORT).show();
                }
                db.close();
            }
        });



        mLivingItemID = getIntent().getIntExtra(Constants.CANYIN_ID_ACTION , 0) ;
        shoucang_flag= getIntent().getIntExtra(Constants.SHOUCANG_FLAT , 0) ;
        source = getIntent().getStringExtra(Constants.CANYIN_SOURCE_ACTION);

        if(savedInstanceState != null){
            mLivingItemID=savedInstanceState.getInt("mLivingItemID");
            shoucang_flag=savedInstanceState.getInt("shoucang_flag");
            source=savedInstanceState.getString("source");
            favorite_flag=savedInstanceState.getInt("favorite_flag");
        }else{
            mLivingItemID = getIntent().getIntExtra(Constants.CANYIN_ID_ACTION, 0);
            shoucang_flag = getIntent().getIntExtra(Constants.SHOUCANG_FLAT, 0);
            source = getIntent().getStringExtra(Constants.CANYIN_SOURCE_ACTION);

            favorite_flag = getIntent().getIntExtra(Constants.FAVORITE_FLAT, 0);
        }

        mMyGallery = (MyGallery) findViewById(R.id.detail_gallery) ;
        mMyGallery.setSpacing(18) ;
        mMyGallery.setOnItemSelectedListener(gallerySelectListener) ;
        mMyGallery.setOnItemClickListener(galleryClickListener) ;
        mDotList = (IndicationDotList) findViewById(R.id.detail_indication) ;


        yingyeshijianTv = (TextView) findViewById(R.id.yingyeshijianTv);
        peisongImg = (ImageView) findViewById(R.id.peisongImg);
        dianhuaTv = (TextView) findViewById(R.id.dianhuaTv);
        dianhuaImg = (ImageView) findViewById(R.id.dianhuaImg);
        dizhiTv = (TextView) findViewById(R.id.dizhiTv);
        LianxiButton = (Button) findViewById(R.id.LianxiButton) ;
        dizhiImg = (ImageView) findViewById(R.id.dizhiImg);//小标记
        shopGoods = (LinearLayout) findViewById(R.id.shop_goods);//商家商品
        shopCoupon = (LinearLayout) findViewById(R.id.shop_coupon);//商家劵
        shopActivity = (LinearLayout) findViewById(R.id.shop_activity);//商家活动

        shopActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                |Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(Constants.CANYIN_ID_ACTION, mLivingItemID);
                intent.setClass(CanyinOwnDetailActivity.this,SalesPromotionActivity.class);
                startActivity(intent);
            }
        });

        /*shopCoupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                |Intent.FLAG_ACTIVITY_CLEAR_TOP);

                intent.setClass(CanyinOwnDetailActivity.this,GoodsCauponListActivity.class);
                startActivity(intent);

            }
        });*/
        //商家商品
        shopGoods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("latitude", latitude);
                intent.putExtra("longitude", longitude);
                intent.putExtra(Constants.CANYIN_ID_ACTION, mLivingItemID);
                intent.setClass(CanyinOwnDetailActivity.this,
                        TestActivity.class);
                startActivity(intent);
            }
        });

        dizhiImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("latitude", latitude);
                intent.putExtra("longitude", longitude);
                intent.setClass(CanyinOwnDetailActivity.this,
                        DizhiActivity.class);
                startActivity(intent);
            }
        });

        LianxiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri uri = Uri.parse("tel:"
                        + telephone);
                Intent it = new Intent(Intent.ACTION_DIAL, uri);
                startActivity(it);


				/*long uid=LocalStore.getUserInfo().userId;*/
            }
        }) ;


        dianhuaImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri uri = Uri.parse("tel:" + telephone);
                Intent it = new Intent(Intent.ACTION_DIAL, uri);
                startActivity(it);

				/* long uid=LocalStore.getUserInfo().userId; */
            }
        });

        showDialog() ;
        mThread = new GetCanyinDetailThread(this, mHandler, propertyid,mLivingItemID,source, LocalStore.getLatitude(getApplicationContext()),LocalStore.getLongitude(getApplicationContext())) ;
        mThread.start() ;



    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //propertyid = (int) saving.getLong(LocalStore.PROPERTY_ID, 0);
        //mLivingItemID = getIntent().getIntExtra(Constants.CANYIN_ID_ACTION, 0);
        //shoucang_flag = getIntent().getIntExtra(Constants.SHOUCANG_FLAT, 0);
        //source = getIntent().getStringExtra(Constants.CANYIN_SOURCE_ACTION);
        //favorite_flag = getIntent().getIntExtra(Constants.FAVORITE_FLAT, 0);
        outState.putInt("mLivingItemID", mLivingItemID);
        outState.putInt("shoucang_flag", shoucang_flag);
        outState.putString("source", source);
        outState.putInt("favorite_flag", favorite_flag);
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mLivingItemID=savedInstanceState.getInt("mLivingItemID");
        shoucang_flag=savedInstanceState.getInt("shoucang_flag");
        source=savedInstanceState.getString("source");
        favorite_flag=savedInstanceState.getInt("favorite_flag");
    }

    private void showDialog(){
        mWaitLoading = new ProgressDialog(CanyinOwnDetailActivity.this);
        String msg = getResources().getString(R.string.txt_loading) ;
        mWaitLoading.setMessage(msg);
        mWaitLoading.setCancelable(false);
        mWaitLoading.show();
    }

    /*
	 * 当用户滑动了图片回调
	 */
    private final AdapterView.OnItemSelectedListener gallerySelectListener =
            new AdapterView.OnItemSelectedListener(){

                @Override
                public void onItemSelected(AdapterView<?> parent, View view,
                                           int position, long id) {
                    mDotList.setIndex(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
    };
    /*
	 * 当用户点击了gallery上的图片回调
	 *
	 * @selectItemIndex
	 */
    private final AdapterView.OnItemClickListener popGalleryClickListener = new AdapterView.OnItemClickListener(){

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {

        }
    };

    /*
	 * 当用户点击了gallery上的图片回调
	 *
	 * @selectItemIndex
	 */
    private final AdapterView.OnItemClickListener galleryClickListener = new AdapterView.OnItemClickListener(){

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            popGalleryIndex = position;
            initImagePopWindow();
        }
    };

    /*
	 * 当用户滑动了图片回调
	 */
    private final AdapterView.OnItemSelectedListener popGallerySelectListener = new AdapterView.OnItemSelectedListener(){

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
        {
            mPopIndicationDotList.setIndex(position);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent)
        {

        }
    };

    private void initImagePopWindow(){
        mImgView = getLayoutInflater().inflate(R.layout.appshare_app_pop, null);
        mImgPop = new PopupWindow(mImgView, ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT, true);

        mPopDownloadBtn = (Button) mImgView.findViewById(R.id.pop_download_btn);
        mPopDownloadBtn.setOnClickListener(CloseClickListener);

       /* RelativeLayout popRl = (RelativeLayout) mImgView.findViewById(R.id.app_pop_rl);
        popRl.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                if (mImgPop != null && mImgPop.isShowing())
                {
                    mImgPop.dismiss();
                    mMyGallery.setAdapter(mAdapter);
                    mMyGallery.setSelection(popGalleryIndex);
                }
            }
        });
*/
        mImgPop.setAnimationStyle(R.style.AnimationPreview);

        Gallery.LayoutParams params = new Gallery.LayoutParams(/*410, 520*/

                ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.FILL_PARENT
        );


        mPopGallery = (MyGallery) mImgView.findViewById(R.id.pop_img_gallery);
        mPopGallery.setSpacing(10);

        mPopGallery.setOnItemClickListener(popGalleryClickListener);
        mPopGallery.setOnItemSelectedListener(popGallerySelectListener);

        mPopAdapter = new ImageAdapter(this, params, ImageView.ScaleType.CENTER_CROP);
        for (int i = mProductDetail.livingItemPicture.size() - 1; i >= 0; --i)
        {
            Log.d("MyTag","mProductDetail.fleaPictureArray.get(i)"+mProductDetail.livingItemPicture.get(i).path);
            mPopAdapter.addImg(mProductDetail.livingItemPicture.get(i).imgDw);
        }

        mPopGallery.setAdapter(mPopAdapter);
        mPopGallery.setSelection(popGalleryIndex);

        LinearLayout vv = (LinearLayout) findViewById(R.id.app_info_all_ll);
        vv.setOnClickListener(this);
        // 加上下面两行可以用back键关闭popupwindow，否则必须调用dismiss();
        ColorDrawable dw = new ColorDrawable(-00000);
        mImgPop.setBackgroundDrawable(dw);
        mImgPop.showAtLocation(vv, Gravity.CENTER, 0, 0);
        mImgPop.update();

        mPopIndicationDotList = (IndicationDotList) mImgView.findViewById(R.id.pop_dot_list);
        mPopIndicationDotList.setCount( mProductDetail.livingItemPicture.size());
    }

    View.OnClickListener CloseClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            if (mImgPop != null && mImgPop.isShowing())
            {
                mImgPop.dismiss();
            }

        }
    };
    void freeResource(){
        mThread.stopRun() ;
        Log.d("TAG", "ProductDetailActivity freeResource ******") ;
        if(mProductDetail == null)	return ;
        mAdapter.freeDrawable() ;
        mMyGallery.setAdapter(null) ;
    }

    @Override
    public void onClick(View v) {
        if (mImgPop != null && mImgPop.isShowing()){
            mImgPop.dismiss();
        }
    }
}
