package com.example.a10483.httptest;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class VolleyActivity extends AppCompatActivity {

    private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.volleylayout);

        imageView=(ImageView)findViewById(R.id.imageview);

        RequestQueue mQueue= Volley.newRequestQueue(getApplicationContext());//项目中创建一个RequestQueue对象即可
        StringRequest stringRequest=new StringRequest("http://www.baidu.com", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("TAG",response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG",error.getMessage(),error);
            }
        });//StringRequest传入三个参数，1、请求的url地址，2、返回成功的response，3、返回失败的response

        mQueue.add(stringRequest);//将该对象添加到返回队列中

        /*StringRequest stringRequest1=new StringRequest(Request.Method.POST,url,listener,errorListner){
            protected Map<String,String> getParams() throws AuthFailureError{
            Map<String,String> map=new HashMap<String,String>();
            map.put("params1","value1");
            map.put("params2","value2");
            return map;//请求的方式为post则需要重写getParams方法，将想post的数据传入即可
            }
        }*/

        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest("http://m.weather.com.cn/data/101010100.html", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("TAG",response.toString());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG",error.getMessage(),error);
            }
        });//传入四个参数，1、json接口地址，2、尚且未知，3、4同上

        mQueue.add(jsonObjectRequest);

       ImageRequest imageRequest=new ImageRequest("http://developer.android.com/image/home/aw_dac.png", new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                imageView.setImageBitmap(response);
            }
        }, 0, 0, Bitmap.Config.RGB_565, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                imageView.setImageResource(R.mipmap.ic_launcher);
            }
        });//传入6个参数，1、url，2、返回成功response，3、4、图片长宽，0表示不压缩，5、图片颜色属性，6、返回失败。
        mQueue.add(imageRequest);

        ImageLoader imageLoader=new ImageLoader(mQueue, new BitmapCache());

        ImageLoader.ImageListener listener=ImageLoader.getImageListener(imageView,R.mipmap.ic_launcher,R.mipmap.ic_launcher_round);
        imageLoader.get("http://img.my.csdn.net/uploads/201404/13/1397393290_5765.jpeg",listener);


    }
}
