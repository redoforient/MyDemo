package com.zhangll.app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.zhangll.app.view.CustomerSearchView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhangll
 * create at 2019/1/18 14:46
 * @desc 测试
 */
public class MainActivity extends AppCompatActivity {

    TextView mTvCustomInfo;
    CustomerSearchView customerSearchDialog;


    List<Map<String, String>> mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mList = new ArrayList<>();
        mList.add(new HashMap<String, String>() {{
            put("code", "000");
            put("name", "张三峰");
        }});
        mList.add(new HashMap<String, String>() {{
            put("code", "001");
            put("name", "张三");
        }});
        mList.add(new HashMap<String, String>() {{
            put("code", "002");
            put("name", "李四");
        }});

        mList.add(new HashMap<String, String>() {{
            put("code", "003");
            put("name", "王五");
        }});

        mList.add(new HashMap<String, String>() {{
            put("code", "004");
            put("name", "赵六");
        }});

        mList.add(new HashMap<String, String>() {{
            put("code", "005");
            put("name", "周七");
        }});

        mList.add(new HashMap<String, String>() {{
            put("code", "006");
            put("name", "吴九");
        }});

        mList.add(new HashMap<String, String>() {{
            put("code", "007");
            put("name", "张六石");
        }});

        customerSearchDialog = new CustomerSearchView(MainActivity.this,
                mList, (Map<String, String> data) -> {
            mTvCustomInfo.setText(data.get("name"));
        }
        );


        setContentView(R.layout.activity_main);
        mTvCustomInfo = findViewById(R.id.tv_custom_info);
        mTvCustomInfo.setClickable(true);
        mTvCustomInfo.setFocusableInTouchMode(false);
        mTvCustomInfo.setOnClickListener((View view) -> {
                    if (!customerSearchDialog.isShowing()) {
                        customerSearchDialog.show();
                    }
                }
        );
    }
}
