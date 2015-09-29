package com.litesuits.http.sample;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.*;
import com.litesuits.http.HttpConfig;
import com.litesuits.http.LiteHttp;
import com.litesuits.http.annotation.*;
import com.litesuits.http.concurrent.OverloadPolicy;
import com.litesuits.http.concurrent.SchedulePolicy;
import com.litesuits.http.concurrent.SmartExecutor;
import com.litesuits.http.data.Json;
import com.litesuits.http.data.NameValuePair;
import com.litesuits.http.exception.HttpException;
import com.litesuits.http.helper.CustomJSONParser;
import com.litesuits.http.helper.FastJson;
import com.litesuits.http.helper.MyHttpExceptHandler;
import com.litesuits.http.listener.GlobalHttpListener;
import com.litesuits.http.listener.HttpListener;
import com.litesuits.http.log.HttpLog;
import com.litesuits.http.model.User;
import com.litesuits.http.model.api.RichParam;
import com.litesuits.http.model.api.UserParam;
import com.litesuits.http.request.*;
import com.litesuits.http.request.content.*;
import com.litesuits.http.request.content.multi.*;
import com.litesuits.http.request.param.CacheMode;
import com.litesuits.http.request.param.HttpMethods;
import com.litesuits.http.request.param.HttpParamModel;
import com.litesuits.http.request.param.HttpRichParamModel;
import com.litesuits.http.request.query.JsonQueryBuilder;
import com.litesuits.http.response.Response;
import com.litesuits.http.utils.HttpUtil;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

public class MainActivity extends Activity {
    protected String TAG = MainActivity.class.getSimpleName();
    protected ListView mListview;
    protected BaseAdapter mAdapter;
    protected LiteHttp liteHttp;
    protected Activity activity = null;
    protected int count = 0;
    private boolean needRestore;

    public static final String url = "http://baidu.com";
    public static final String urlHttps = "https://baidu.com";
    public static final String uploadUrl = "http://192.168.8.105:8080/upload";
    public static final String httpsUrl = "https://www.thanku.love";
    public static final String userGet = "http://litesuits.com/mockdata/user_get";
    public static final String picUrl = "http://pic.33.la/20140403sj/1638.jpg";
    public static final String redirectUrl = "http://www.baidu.com/link?url=Lqc3GptP8u05JCRDsk0jqsAvIZh9WdtO_RkXYMYRQEm";


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        initViews();
        activity = this;
        // keep an singleton instance of litehttp
        liteHttp = LiteHttp.newApacheHttpClient(new HttpConfig(this).setDebugged(true));
    }

    private void initViews() {
        mListview = (ListView) findViewById(R.id.listview);
        mAdapter = new ArrayAdapter<String>(this, R.layout.list_item, R.id.tv_item,
                getResources().getStringArray(R.array.http_test_list));
        mListview.setAdapter(mAdapter);
        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                clickTestItem(position);
            }
        });
    }

    /**
     * <item>0. Quickly Configuration</item>
     * <item>1. Asynchronous Request</item>
     * <item>2. Synchronous Request</item>
     * <item>3. Simple Synchronous Request</item>
     * <item>4. Exception Thrown Request</item>
     * <item>5. HTTPS Reqeust</item>
     * <item>6. Automatic Model Conversion</item>
     * <item>7. Custom Data Parser</item>
     * <item>8. Replace Json Library</item>
     * <item>9. File Upload</item>
     * <item>10. File/Bitmap Download</item>
     * <item>11. Disable Some Network</item>
     * <item>12. Traffic/Time Statistics</item>
     * <item>13. Retry/Redirect</item>
     * <item>14. Best Practices of Exception Handling</item>
     * <item>15. Best Practices of Cancel Request</item>
     * <p/>
     * <item>16. POST Multi-Form Data</item>
     * <item>17. Concurrent and Scheduling</item>
     * <item>18. Detail of Config</item>
     * <item>19. Usage of Annotation</item>
     * <item>20. Multi Cache Mechanism</item>
     * <item>21. CallBack Mechanism</item>
     * <item>22. Best Practice: SmartExecutor</item>
     * <item>23. Best Practice: Auto-Conversion of Complex Model</item>
     * <item>24. Best Practice: HTTP Rich Param Model</item>
     */
    private void clickTestItem(final int which) {

        // restore http config
        if (needRestore) {
            liteHttp.getConfig().restoreToDefault();
            needRestore = false;
        }

        switch (which) {
            case 0:
                // 0. Quickly Configuration
                HttpConfig config = new HttpConfig(activity);
                // set app context
                config.setContext(activity);
                // custom User-Agent
                config.setUserAgent("Mozilla/5.0 (...)");
                // connect timeout: 10s,  socket timeout: 10s
                config.setTimeOut(1000, 1000);
                // init config
                liteHttp.initConfig(config);

                HttpUtil.showTips(activity, "LiteHttp2.0", "配置参数成功");
                break;

            case 1:
                // 1. Asynchronous Request

                // 1.0 init request
                final StringRequest request = new StringRequest(url).setHttpListener(
                        new HttpListener<String>() {
                            @Override
                            public void onSuccess(String s, Response<String> response) {
                                HttpUtil.showTips(activity, "LiteHttp2.0", s);
                                response.printInfo();
                            }

                            @Override
                            public void onFailure(HttpException e, Response<String> response) {
                                HttpUtil.showTips(activity, "LiteHttp2.0", e.toString());
                            }
                        }
                );

                // 1.1 execute async, nothing returned.
                liteHttp.executeAsync(request);

                // 1.2 perform async, future task returned.
                FutureTask<String> task = liteHttp.performAsync(request);
                task.cancel(true);
                break;

            case 2:
                // 2. Synchronous Request

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        // 2.0 return fully response
                        Response<User> response = liteHttp.execute(new RichParam(1, "a"));
                        User user = response.getResult();
                        Log.i(TAG, "User: " + user);

                        // 2.1 return java model directly
                        User user2 = liteHttp.perform(new JsonAbsRequest<User>(userGet) {});
                        Log.i(TAG, "User: " + user2);

                        // 2.1 handle result on UI thread(主线程处理，注意HttpListener默认是在主线程回调)
                        liteHttp.execute(new BytesRequest(url).setHttpListener(
                                new HttpListener<byte[]>() {
                                    @Override
                                    public void onSuccess(byte[] bytes, Response<byte[]> response) {
                                        HttpUtil.showTips(activity, "LiteHttp2.0", Arrays.toString(bytes));
                                    }
                                }));
                    }
                }).start();
                break;

            case 3:
                // 3. Simple Synchronous Request

                new AsyncTask<Void, String, NameValuePair[]>() {

                    @Override
                    protected NameValuePair[] doInBackground(Void... params) {

                        // 3.0 simple get and publish
                        String result = liteHttp.get(url);
                        publishProgress("Simple Get: \n" + result);

                        // 3.0 simple post and publish
                        result = liteHttp.post(new StringRequest(urlHttps));
                        publishProgress("Simple POST: \n" + result);

                        // 3.0 simple head and return
                        NameValuePair[] headers = liteHttp.head(new StringRequest(url));
                        return headers;
                    }

                    @Override
                    protected void onProgressUpdate(String... values) {
                        Toast.makeText(activity, "content length:"+values[0].length(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    protected void onPostExecute(NameValuePair[] nameValuePairs) {
                        HttpUtil.showTips(activity, "LiteHttp2.0", Arrays.toString(nameValuePairs));
                    }
                }.execute();
                break;

            case 4:
                // 4. Exception Thrown Request

                Runnable run = new Runnable() {
                    @Override
                    public void run() {

                        try {
                            Response response = liteHttp.executeOrThrow(new BytesRequest("haha://hehe"));
                        } catch (HttpException e) {
                            e.printStackTrace();
                        }

                        try {
                            User user = liteHttp.performOrThrow(new JsonAbsRequest<User>("http://thanku.love") {});
                        } catch (final HttpException e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    HttpUtil.showTips(activity, "LiteHttp2.0", e.getMessage());
                                }
                            });
                        }

                    }
                };
                SmartExecutor executorOne = new SmartExecutor();
                executorOne.execute(run);

                break;
            case 5:
                // 5. HTTPS Reqeust

                liteHttp.executeAsync(new StringRequest(httpsUrl).setHttpListener(new HttpListener<String>() {
                    @Override
                    public void onSuccess(String s, Response<String> response) {
                        HttpUtil.showTips(activity, "LiteHttp2.0", " Read Content Length: " + s.length());
                    }

                    @Override
                    public void onFailure(HttpException e, Response<String> response) {
                        HttpUtil.showTips(activity, "LiteHttp2.0", Arrays.toString(e.getStackTrace()));
                    }
                }));

                break;
            case 6:
                // 6. Automatic Model Conversion

                class UserRequest extends JsonAbsRequest<User> {
                    public UserRequest(String url) {
                        super(url);
                    }
                }

                UserRequest userRequest = new UserRequest(userGet);
                // build as http://...?id=168&key=md5
                userRequest.setParamModel(new UserParam(18, "md5"));
                userRequest.setHttpListener(new HttpListener<User>() {
                    @Override
                    public void onSuccess(User user, Response<User> response) {
                        HttpUtil.showTips(activity, "LiteHttp2.0", user.toString());
                    }
                });
                liteHttp.executeAsync(userRequest);

                break;
            case 7:
                // 7. Custom Data Parser

                JsonAbsRequest<JSONObject> jsonRequest = new JsonAbsRequest<JSONObject>(userGet) {};
                jsonRequest.setDataParser(new CustomJSONParser());
                liteHttp.executeAsync(jsonRequest.setHttpListener(new HttpListener<JSONObject>() {
                    @Override
                    public void onSuccess(JSONObject jsonObject, Response<JSONObject> response) {
                        HttpUtil.showTips(activity, "LiteHttp2.0",
                                "Custom JSONObject Parser:\n" + jsonObject.toString());
                    }
                }));
                break;

            case 8:
                // 8. Replace Json Library

                // first, builder a java class that used FastJson
                Json json = new FastJson();
                // then, set new json framework.
                Json.set(json);

                // json model convert used #FastJson
                liteHttp.executeAsync(new JsonAbsRequest<User>(userGet) {}.setHttpListener(new HttpListener<User>() {
                    @Override
                    public void onSuccess(User user, Response<User> response) {
                        super.onSuccess(user, response);
                        response.printInfo();
                        HttpUtil.showTips(activity, "LiteHttp2.0", "FastJson handle this: \n" + user.toString());
                    }
                }));

                // json model convert used #FastJson
                liteHttp.performAsync(new StringRequest(userGet).setHttpListener(new HttpListener<String>() {
                    @Override
                    public void onSuccess(String s, Response<String> response) {
                        User u = Json.get().toObject(s, User.class);
                        Toast.makeText(activity, u.toString(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onEnd(Response<String> response) {
                        super.onEnd(response);
                        needRestore = true;
                    }
                }));
                break;
            case 9:
                // 9. File Upload
                final ProgressDialog upProgress = new ProgressDialog(this);
                upProgress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                upProgress.setIndeterminate(false);
                upProgress.show();
                StringRequest uploadRequest = new StringRequest(uploadUrl)
                        .setMethod(HttpMethods.Post)
                        .setHttpBody(new FileBody(new File("/sdcard/aaa.jpg")))
                        .setHttpListener(new HttpListener<String>(true, false, true) {
                            @Override
                            public void onSuccess(String s, Response<String> response) {
                                upProgress.dismiss();
                                HttpUtil.showTips(activity, "Upload Success", s);
                                response.printInfo();
                            }

                            @Override
                            public void onFailure(HttpException e, Response<String> response) {
                                upProgress.dismiss();
                                HttpUtil.showTips(activity, "Upload Failed", e.toString());
                            }

                            @Override
                            public void onUploading(AbstractRequest<String> request, long total, long len) {
                                upProgress.setMax((int) total);
                                upProgress.setProgress((int) len);
                            }
                        });
                liteHttp.executeAsync(uploadRequest);
                break;
            case 10:
                // 10. File/Bitmap Download

                final ProgressDialog downProgress = new ProgressDialog(this);
                downProgress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                downProgress.setIndeterminate(false);
                downProgress.show();
                // load and show bitmap
                liteHttp.executeAsync(
                        new BitmapRequest(picUrl).setHttpListener(new HttpListener<Bitmap>(true, true, false) {
                            @Override
                            public void onLoading(AbstractRequest<Bitmap> request, long total, long len) {
                                downProgress.setMax((int) total);
                                downProgress.setProgress((int) len);
                                HttpLog.i(TAG, total + "  total   " + len + " len");
                            }

                            @Override
                            public void onSuccess(Bitmap bitmap, Response<Bitmap> response) {
                                downProgress.dismiss();
                                AlertDialog.Builder b = HttpUtil.dialogBuilder(activity, "LiteHttp2.0", "");
                                ImageView iv = new ImageView(activity);
                                iv.setImageBitmap(bitmap);
                                b.setView(iv);
                                b.show();
                            }

                            @Override
                            public void onFailure(HttpException e, Response<Bitmap> response) {
                                downProgress.dismiss();
                                HttpUtil.showTips(activity, "LiteHttp2.0", e.toString());
                            }
                        }));

                // download a file to sdcard.
                liteHttp.executeAsync(new FileRequest(picUrl, "sdcard/aaa.jpg"));
                break;
            case 11:
                // 11. Disable Some Network

                config = liteHttp.getConfig();
                // disable network need context
                config.setContext(activity);
                // disable mobile(2G/3G/4G + WIFI) network
                config.setDisableNetworkFlags(HttpConfig.FLAG_NET_DISABLE_MOBILE | HttpConfig.FLAG_NET_DISABLE_WIFI);
                needRestore = true;

                liteHttp.executeAsync(new StringRequest(url).setHttpListener(new HttpListener<String>() {
                    @Override
                    public void onSuccess(String s, Response<String> response) {
                        HttpUtil.showTips(activity, "LiteHttp2.0", s);
                    }

                    @Override
                    public void onFailure(HttpException e, Response<String> response) {
                        HttpUtil.showTips(activity, "LiteHttp2.0", e.toString());
                    }
                }));
                break;

            case 12:
                // 12. Traffic/Time Statistics

                // turn on
                liteHttp.getConfig().setDoStatistics(true);
                // see detail
                liteHttp.executeAsync(new FileRequest(picUrl).setHttpListener(new HttpListener<File>() {
                    @Override
                    public void onSuccess(File file, Response<File> response) {
                        String msg = "This request take time:" + response.getUseTime()
                                     + ", readed length:" + response.getReadedLength();
                        msg += "  Global " + liteHttp.getStatisticsInfo();
                        HttpUtil.showTips(activity, "LiteHttp2.0", msg);

                    }

                    @Override
                    public void onFailure(HttpException e, Response<File> response) {
                        HttpUtil.showTips(activity, "LiteHttp2.0", e.toString());
                    }
                }));
                break;
            case 13:
                // 13. Retry/Redirect

                // maximum retry times
                liteHttp.getConfig().setDefaultMaxRetryTimes(2);
                // maximum redirect times
                liteHttp.getConfig().setDefaultMaxRedirectTimes(5);
                // test it
                liteHttp.executeAsync(new StringRequest(redirectUrl).setHttpListener(new HttpListener<String>() {

                    @Override
                    public void onRedirect(AbstractRequest<String> request, int max, int times) {
                        Toast.makeText(activity, "Redirect max num: " + max + " , times: " + times
                                                 + "\n GO-TO: " + request.getUri(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onRetry(AbstractRequest<String> request, int max, int times) {
                        Toast.makeText(activity, "Retry Now! max num: " + max + " , times: " + times
                                , Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void onSuccess(String s, Response<String> response) {
                        HttpUtil.showTips(activity, "LiteHttp2.0", "Content Length: " + s.length());
                    }
                }));
                break;

            case 14:
                // 14. Best Practices of Exception Handling
                liteHttp.executeAsync(
                        new StringRequest("httpa://invalid-url").setHttpListener(new HttpListener<String>() {

                            @Override
                            public void onFailure(HttpException exception, Response<String> response) {
                                new MyHttpExceptHandler(activity).handleException(exception);
                            }

                        }));
                break;
            case 15:
                // 15. Best Practices of Cancel Request
                final boolean isInterrupted = count++ % 2 != 0;
                StringRequest stringRequest = new StringRequest(redirectUrl)
                        .setHttpListener(new HttpListener<String>() {
                            @Override
                            public void onCancel(String s, Response<String> response) {
                                HttpUtil.showTips(activity, "LiteHttp2.0",
                                        "Request Canceld: " + response.getRequest().isCancelled()
                                        + "\nTask Interrupted: " + isInterrupted);
                            }
                        });
                FutureTask futureTask = liteHttp.performAsync(stringRequest);
                SystemClock.sleep(100);
                if (!isInterrupted) {
                    // one correct way is cancel this request
                    stringRequest.cancel();
                } else {
                    // other correct way is interrupt this thread or task.
                    futureTask.cancel(true);
                }
                break;
            case 16:
                // 16. POST Multi-Form Data
                final ProgressDialog postProgress = new ProgressDialog(this);
                postProgress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                postProgress.setIndeterminate(false);

                final StringRequest postRequest = new StringRequest(uploadUrl)
                        .setMethod(HttpMethods.Post)
                        .setHttpListener(new HttpListener<String>(true, false, true) {
                            @Override
                            public void onStart(AbstractRequest<String> request) {
                                super.onStart(request);
                                postProgress.show();
                            }

                            @Override
                            public void onSuccess(String s, Response<String> response) {
                                //                                postProgress.dismiss();
                                HttpUtil.showTips(activity, "Upload Success", s);
                                response.printInfo();
                            }

                            @Override
                            public void onFailure(HttpException e, Response<String> response) {
                                postProgress.dismiss();
                                HttpUtil.showTips(activity, "Upload Failed", e.toString());
                            }

                            @Override
                            public void onUploading(AbstractRequest<String> request, long total, long len) {
                                postProgress.setMax((int) total);
                                postProgress.setProgress((int) len);
                            }
                        });

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("POST DATA TEST");
                String[] array = getResources().getStringArray(R.array.http_test_post);
                builder.setItems(array, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                postRequest.setHttpBody(new StringBody("hello lite: 你好，Lite！"));
                                break;
                            case 1:
                                LinkedList<NameValuePair> pList = new LinkedList<NameValuePair>();
                                pList.add(new NameValuePair("key1", "value-haha"));
                                pList.add(new NameValuePair("key2", "value-hehe"));
                                postRequest.setHttpBody(new UrlEncodedFormBody(pList));
                                break;
                            case 2:
                                postRequest.setHttpBody(new JsonBody(new UserParam(168, "haha-key")));
                                break;
                            case 3:
                                ArrayList<String> list = new ArrayList<String>();
                                list.add("a");
                                list.add("b");
                                list.add("c");
                                postRequest.setHttpBody(new SerializableBody(list));
                                break;
                            case 4:
                                postRequest.setHttpBody(new ByteArrayBody(
                                        new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 127
                                        }));
                                break;
                            case 5:
                                postRequest.setHttpBody(new FileBody(new File("/sdcard/litehttp.txt")));
                                break;
                            case 6:
                                FileInputStream fis = null;
                                try {
                                    fis = new FileInputStream(new File("/sdcard/aaa.jpg"));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                postRequest.setHttpBody(new InputStreamBody(fis));
                                break;
                            case 7:
                                fis = null;
                                try {
                                    fis = new FileInputStream(new File("/sdcard/litehttp.txt"));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                //View v;v.setBackground();

                                MultipartBody body = new MultipartBody();
                                body.addPart(new StringPart("key1", "hello"));
                                body.addPart(new StringPart("key2", "很高兴见到你", "utf-8", null));
                                body.addPart(new BytesPart("key3", new byte[]{1, 2, 3}));
                                body.addPart(new FilePart("pic", new File("/sdcard/aaa.jpg"), "image/jpeg"));
                                body.addPart(new InputStreamPart("litehttp", fis, "litehttp.txt", "text/plain"));
                                postRequest.setHttpBody(body);
                                break;
                        }

                        liteHttp.executeAsync(postRequest);
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();

                break;
            case 17:
                // 17. Concurrent and Scheduling

                HttpConfig csConfig = liteHttp.getConfig();
                // only one task can be executed at the same time
                csConfig.setConcurrentSize(1);
                // at most two tasks be hold in waiting queue at the same time
                csConfig.setWaitingQueueSize(2);
                // the last waiting task executed first
                csConfig.setSchedulePolicy(SchedulePolicy.LastInFirstRun);
                // when task more than 3(current = 1, waiting = 2), new task will be discard.
                csConfig.setOverloadPolicy(OverloadPolicy.DiscardCurrentTask);

                // note : restore config to default, next click.
                needRestore = true;

                // executed order: 0 -> 2 -> 1 , by [DiscardNewTaskInQueue Policy] 3 will be discard.
                for (int i = 0; i < 4; i++) {
                    liteHttp.executeAsync(new StringRequest(url).setTag(i));
                }

                break;
            case 18:
                // 18. Detail of Configuration

                List<NameValuePair> headers = new ArrayList<NameValuePair>();
                headers.add(new NameValuePair("cookies", "this is cookies"));
                headers.add(new NameValuePair("custom-key", "custom-value"));

                HttpConfig newConfig = new HttpConfig(activity);

                // common headers will be set to all request
                newConfig.setCommonHeaders(headers);
                // set default cache path to all request
                newConfig.setCacheDirPath(Environment.getExternalStorageDirectory() + "/a-cache");
                // app context(be used to detect network and get app files path)
                newConfig.setContext(activity);
                // set default cache expire time to all request
                newConfig.setDefaultCacheExpireMillis(30 * 60 * 1000);
                // set default cache mode to all request
                newConfig.setDefaultCacheMode(CacheMode.NetFirst);
                // set default charset to all request
                newConfig.setDefaultCharSet("utf-8");
                // set default http method to all request
                newConfig.setDefaultHttpMethod(HttpMethods.Get);
                // set default maximum redirect-times to all request
                newConfig.setDefaultMaxRedirectTimes(5);
                // set default maximum retry-times to all request
                newConfig.setDefaultMaxRetryTimes(1);
                // set defsult model query builder to all request
                newConfig.setDefaultModelQueryBuilder(new JsonQueryBuilder());
                // whether to detect network before conneting.
                newConfig.setDetectNetwork(true);
                // disable some network
                newConfig.setDisableNetworkFlags(HttpConfig.FLAG_NET_DISABLE_NONE);
                // whether open the traffic & time statistics
                newConfig.setDoStatistics(true);
                // set connect timeout: 10s,  socket timeout: 10s
                newConfig.setTimeOut(10000, 10000);
                // socket buffer size: 4096
                newConfig.setSocketBufferSize(4096);
                // if the network is unstable, wait 3000 milliseconds then start retry.
                newConfig.setForRetry(3000, false);
                // set global http listener to all request
                newConfig.setGlobalHttpListener(null);
                // set maximum size of memory cache space
                newConfig.setMaxMemCacheBytesSize(1024 * 300);
                // maximum number of concurrent tasks(http-request) at the same time
                newConfig.setConcurrentSize(3);
                // maximum number of waiting tasks(http-request) at the same time
                newConfig.setWaitingQueueSize(100);
                // set overload policy of thread pool executor
                newConfig.setOverloadPolicy(OverloadPolicy.DiscardOldTaskInQueue);
                // set schedule policy of thread pool executor
                newConfig.setSchedulePolicy(SchedulePolicy.LastInFirstRun);
                // set user-agent
                newConfig.setUserAgent("Mozilla/5.0");

                // set a new config to lite-http
                liteHttp.initConfig(newConfig);
                break;
            case 19:
                // 19. The Use of Annotation

                @HttpUri(userGet)
                @HttpMethod(HttpMethods.Get)
                @HttpTag("custom tag")
                @HttpID(1)
                @HttpCacheMode(CacheMode.CacheFirst)
                @HttpCacheExpire(value = 1, unit = TimeUnit.MINUTES)
                @HttpMaxRetry(3)
                @HttpMaxRedirect(5)
                class UserAnnoParam implements HttpParamModel {
                    private static final long serialVersionUID = 2931033825895021716L;
                    public long id = 110;
                    private String key = "aes";
                }

                liteHttp.executeAsync(new JsonRequest<User>(new UserAnnoParam(), User.class) {}
                        .setHttpListener(new HttpListener<User>() {
                            @Override
                            public void onSuccess(User user, Response<User> response) {
                                HttpUtil.showTips(activity, "UserAnnoParam", user.toString());
                            }
                        }));

                break;

            case 20:
                // 20. Multi Cache Mechanism
                JsonRequest<User> cacheRequest = new JsonRequest<User>(userGet, User.class);
                cacheRequest.setCacheMode(CacheMode.CacheFirst);
                cacheRequest.setCacheExpire(30, TimeUnit.SECONDS);
                cacheRequest.setHttpListener(new HttpListener<User>() {
                    @Override
                    public void onSuccess(User user, Response<User> response) {
                        String title = response.isCacheHit() ? "Hit Cache(使用缓存)" : "No Cache(未用缓存)";
                        HttpUtil.showTips(activity, title, user.toString());
                    }
                });
                liteHttp.executeAsync(cacheRequest);
                break;
            case 21:
                // 21. CallBack Mechanism

                // the correct way to set global http listener for all request.
                liteHttp.getConfig().setGlobalHttpListener(globalHttpListener);
                /**
                 * new http listener for current request:
                 *
                 * runOnUiThread = false;
                 * readingNotify = false;
                 * uploadingNotify = false;
                 *
                 * actually you can set a series of http listener for one http request.
                 */
                HttpListener<Bitmap> firstHttpListener = new HttpListener<Bitmap>(false, false, false) {
                    @Override
                    public void onSuccess(Bitmap bitmap, Response<Bitmap> response) {
                        HttpLog.i(TAG, "New Listener, request success ...");
                    }

                    @Override
                    public void onFailure(HttpException e, Response<Bitmap> response) {
                        HttpLog.i(TAG, "New Listener, request failure ...");
                    }

                    @Override
                    public void onLoading(AbstractRequest<Bitmap> request, long total, long len) {
                        HttpLog.i(TAG, "New Listener, request loading  ...");
                    }
                };
                // create a bitmap request.
                BitmapRequest bitmapRequest = new BitmapRequest(picUrl);

                // correct way to set first http listener
                bitmapRequest.setHttpListener(firstHttpListener);
                // correct way to set secondary (linked)listener
                firstHttpListener.setLinkedListener(secondaryListener);

                //load and show bitmap
                liteHttp.executeAsync(bitmapRequest);

                break;
            case 22:
                // 22. Best Practices of SmartExecutor

                // 智能并发调度控制器：设置[最大并发数]，和[等待队列]大小
                SmartExecutor smallExecutor = new SmartExecutor();

                // number of concurrent threads at the same time, recommended core size is CPU count
                // set this temporary parameter, just for test
                smallExecutor.setCoreSize(2);
                //  Adjust maximum number of waiting queue size by yourself or based on phone performance
                // set this temporary parameter, just for test
                smallExecutor.setQueueSize(2);

                // 任务数量超出[最大并发数]后，自动进入[等待队列]，等待当前执行任务完成后按策略进入执行状态：先进先执行，先进后执行。
                smallExecutor.setSchedulePolicy(SchedulePolicy.LastInFirstRun);

                smallExecutor.setOverloadPolicy(OverloadPolicy.DiscardOldTaskInQueue);
                // 后续添加新任务数量超出[等待队列]大小时，执行过载策略：抛弃队列内最新、抛弃队列内最旧、抛弃当前任务、当前线程直接运行、抛异常。

                for (int i = 0; i < 6; i++) {
                    final int j = i;
                    smallExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            HttpLog.i(TAG, j + " task running");
                            SystemClock.sleep(j * 200);
                        }
                    });
                }
                break;

            case 23:
                // 23. Automatic Conversion of Complex Model
                break;
            case 24:
                // 24. Best Practice: HTTP Rich Param Model (It is simpler and More Useful)

                // rich param 更简单、有用！只需要定义一个RichParam，即可指定URL、参数、返回响应体三个关键事务。
                @HttpUri(userGet)
                @HttpMethod(HttpMethods.Get)
                @HttpTag("custom tag")
                @HttpID(2)
                @HttpCacheMode(CacheMode.CacheFirst)
                @HttpCacheExpire(value = 1, unit = TimeUnit.MINUTES)
                @HttpCacheKey("custom-cache-key-name-by-myself")
                @HttpCharSet("UTF-8")
                @HttpMaxRetry(3)
                @HttpMaxRedirect(5)
                class UserRichParam extends HttpRichParamModel<User> {
                    private static final long serialVersionUID = -785053238885177613L;
                    public long id = 110;
                    private String key = "aes";

                    /**
                     * 还可以定义监听器
                     * @return
                     */
                    @Override
                    protected HttpListener<User> createHttpListener() {
                        return new HttpListener<User>() {
                            @Override
                            public void onSuccess(User user, Response<User> response) {
                                HttpUtil.showTips(activity, "UserRichParam", user.toString());
                            }
                        };
                    }
                }

                // 一句话调用即可
                liteHttp.executeAsync(new UserRichParam());
                break;
        }
    }


    /**
     * global http listener for all request.
     */
    GlobalHttpListener globalHttpListener = new GlobalHttpListener() {
        @Override
        public void onStart(AbstractRequest<?> request) {
            HttpLog.i(TAG, "Global, request start ...");
        }

        @Override
        public void onSuccess(Object data, Response<?> response) {
            HttpLog.i(TAG, "Global, request success ..." + data);
        }

        @Override
        public void onFailure(HttpException e, Response<?> response) {
            HttpLog.i(TAG, "Global, request failure ..." + e);
        }

        @Override
        public void onCancel(Object data, Response<?> response) {
            HttpLog.i(TAG, "Global, request cancel ..." + data);
        }
    };

    /**
     * http listener for current reuqest:
     *
     * runOnUiThread = true;
     * readingNotify = true;
     * uploadingNotify = true;
     */
    HttpListener<Bitmap> secondaryListener = new HttpListener<Bitmap>(true, true, true) {
        ProgressDialog progressDialog = null;

        @Override
        public void onStart(AbstractRequest<Bitmap> request) {
            HttpLog.i(TAG, " Listener, request start ...");
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setIndeterminate(false);
            progressDialog.show();
        }

        @Override
        public void onSuccess(Bitmap bitmap, Response<Bitmap> response) {
            HttpLog.i(TAG, " Listener, request success ...");
            progressDialog.dismiss();
            ImageView iv = new ImageView(activity);
            iv.setImageBitmap(bitmap);
            HttpUtil.dialogBuilder(activity, "LiteHttp2.0", "")
                    .setView(iv).show();
        }

        @Override
        public void onFailure(HttpException e, Response<Bitmap> response) {
            HttpLog.i(TAG, " Listener, request failure ...");
            progressDialog.dismiss();
        }

        @Override
        public void onCancel(Bitmap bitmap, Response<Bitmap> response) {
            HttpLog.i(TAG, " Listener, request cancel ...");
        }

        @Override
        public void onLoading(AbstractRequest<Bitmap> request, long total, long len) {
            HttpLog.i(TAG, " Listener, request loading  ...");
            progressDialog.setMax((int) total);
            progressDialog.setProgress((int) len);
        }

        @Override
        public void onUploading(AbstractRequest<Bitmap> request, long total, long len) {
            HttpLog.i(TAG, " Listener, request upLoading  ...");
        }

        @Override
        public void onRetry(AbstractRequest<Bitmap> request, int max, int times) {
            HttpLog.i(TAG, " Listener, request retry ...");
        }

        @Override
        public void onRedirect(AbstractRequest<Bitmap> request, int max, int times) {
            HttpLog.i(TAG, " Listener, request redirect ...");
        }
    };

}