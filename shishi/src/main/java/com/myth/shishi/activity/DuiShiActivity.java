package com.myth.shishi.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.myth.poetrycommon.BaseActivity;
import com.myth.poetrycommon.adapter.BaseAdapter;
import com.myth.shishi.R;
import com.myth.shishi.adapter.DuiShiAdapter;
import com.myth.poetrycommon.utils.OthersUtils;
import com.myth.shishi.wiget.DuishiEditView;
import com.myth.shishi.wiget.GProgressDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DuiShiActivity extends BaseActivity {

    private DuiShiAdapter adapter;

    RecyclerView listview;

    private final static String URL_STRING = "http://couplet.msra.cn/app/CoupletsWS_V2.asmx/GetXiaLian";

    private final static int LOAD_SUCCESS = 1;

    private final static int LOAD_FAILED = 2;

    private final static int LOAD_SUCCESS_RE = 3;

    private DuishiEditView editView;

    private int count;

    private RelativeLayout topView;

    private LinearLayout ets;

    private String s;

    private GProgressDialog progress;

    private Handler mhandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case LOAD_SUCCESS:
                    adapter.notifyDataSetChanged();
                    if (editView == null) {
                        editView = new DuishiEditView(mActivity, count);
                        ets.addView(editView);
                        topView.setVisibility(View.VISIBLE);
                    } else {
                        editView.refresh(count);
                    }
                    progress.dismiss();
                    break;
                case LOAD_FAILED:
                    Toast.makeText(mActivity, "请求出错", Toast.LENGTH_SHORT).show();
                    progress.dismiss();
                    break;
                case LOAD_SUCCESS_RE:
                    adapter.notifyDataSetChanged();
                    progress.dismiss();
                    break;

                default:
                    break;
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_duishi);
        initView();
    }

    private void initView() {
        progress = new GProgressDialog(mActivity);
        topView = (RelativeLayout) findViewById(R.id.top);
        topView.setVisibility(View.GONE);
        ets = (LinearLayout) findViewById(R.id.ets);
        final EditText et = (EditText) findViewById(R.id.et);
        Button button = (Button) findViewById(R.id.button);
        listview = (RecyclerView) findViewById(R.id.listview);


        listview.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
        listview.setLayoutManager(linearLayoutManager);

        adapter = new DuiShiAdapter();
        listview.setAdapter(adapter);

        adapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                final String s = adapter.list.get(position);
                new AlertDialog.Builder(mActivity).setItems(new String[]{"复制"},
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                OthersUtils.copy(s, mActivity);
                                Toast.makeText(mActivity, R.string.copy_text_done, Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }

                        }).show();
            }

            @Override
            public void onItemLongClick(int position) {

            }
        });


        button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                progress.show();
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        s = et.getText().toString().trim();
                        if (!TextUtils.isEmpty(s)) {
                            execute(s, null);
                        }

                    }
                }).start();

            }
        });
        findViewById(R.id.refresh).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                progress.show();
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        s = et.getText().toString().trim();
                        if (!TextUtils.isEmpty(s)) {
                            execute(s, editView.getText());
                        }
                    }
                }).start();

            }
        });

    }


    private List<String> getData(String s) {
        List<String> list = new ArrayList<String>();
        try {
            JSONObject jsonObject = new JSONObject(s);
            JSONObject d = jsonObject.getJSONObject("d");
            JSONArray jsonArray = d.getJSONArray("XialianSystemGeneratedSets");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONArray array = jsonArray.getJSONObject(i).getJSONArray("XialianCandidates");
                for (int j = 0; j < array.length(); j++) {
                    list.add(array.getString(j));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void execute(String shanglian, String place) {

        if (TextUtils.isEmpty(shanglian)) {
            return;
        }
        count = shanglian.length();
        InputStream inputStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(URL_STRING);
            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestMethod("POST");

            urlConnection.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("shanglian", shanglian);

            if (TextUtils.isEmpty(place)) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < shanglian.length(); i++) {
                    sb.append("0");
                }
                jsonObject.put("xialianLocker", sb.toString());
            } else {
                jsonObject.put("xialianLocker", place);
            }
            jsonObject.put("isUpdate", "false");

            String jsonString = jsonObject.toString();
            wr.write(jsonString.getBytes());
            wr.flush();
            wr.close();
            // try to get response
            int statusCode = urlConnection.getResponseCode();
            if (statusCode == 200) {
                inputStream = new BufferedInputStream(urlConnection.getInputStream());
                urlConnection.getInputStream().toString();
                String response = InputStreamTOString(inputStream);
                if (response != null) {
                    List<String> list = getData(response);
                    if (list != null) {
                        adapter.setList(list);
                        if (TextUtils.isEmpty(place)) {
                            mhandler.sendEmptyMessage(LOAD_SUCCESS);
                        } else {
                            mhandler.sendEmptyMessage(LOAD_SUCCESS_RE);
                        }
                    } else {
                        mhandler.sendEmptyMessage(LOAD_FAILED);
                    }
                } else {
                    mhandler.sendEmptyMessage(LOAD_FAILED);
                }
            } else {
                mhandler.sendEmptyMessage(LOAD_FAILED);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

    }

    final static int BUFFER_SIZE = 4096;

    /**
     * 将InputStream转换成String
     *
     * @param in InputStream
     * @return String
     * @throws Exception
     */
    public static String InputStreamTOString(InputStream in) throws Exception {

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] data = new byte[BUFFER_SIZE];
        int count = -1;
        while ((count = in.read(data, 0, BUFFER_SIZE)) != -1)
            outStream.write(data, 0, count);

        data = null;
        return new String(outStream.toByteArray(), "utf-8");
    }

}
