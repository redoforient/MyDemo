package com.zhangll.app.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;


import com.zhangll.app.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * @desc 高亮显示匹配关键字动态更新搜索结果
 * @author zhangll
 * create at 2019/1/18 15:11
 */
public class CustomerSearchView extends Dialog {
    private static final String TAG = "CustomerSearchView";
    static final String CODE = "code";
    static final String NAME = "name";

    private Context context;
    private EditText mCustomerView;
    private ListView listView;

    private List<Map<String,String>> mList;
    private OnCustomerItemClickListener listener;
    private HighLightKeywordsAdapter listAdapter = null;

    public CustomerSearchView(Context context, List<Map<String,String>> mList, OnCustomerItemClickListener listener) {
        super(context);
        this.context = context;
        this.mList = mList;
        this.listener = listener;
        initView();
    }


    private void initView() {
        super.setContentView(R.layout.customer_search_view);
        mCustomerView = findViewById(R.id.customer_et);
        listView = findViewById(android.R.id.list);
        mCustomerView.addTextChangedListener(watcher);

        new UpdateListTask().execute(getKeyWord());
    }


    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            new UpdateListTask().execute(getKeyWord());
        }
    };


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DEL:
                CharSequence str = mCustomerView.getText();
                if (str != null && str.length() > 0) {
                    String temp = str.toString();
                    mCustomerView.setTextKeepState(temp);
                }
                break;
            default:
                return super.onKeyDown(keyCode, event);
        }
        return true;
    }


    private class UpdateListTask extends AsyncTask<String, Integer, BaseAdapter> {

        @Override
        protected BaseAdapter doInBackground(String... params) {
            listAdapter = new HighLightKeywordsAdapter(context, updateCustomerListWithInput(getKeyWord()),
                    R.layout.customer_item, new String[]{CODE, NAME},
                    new int[]{R.id.customer_code, R.id.customer_name});
            return listAdapter;
        }

        @Override
        protected void onPostExecute(BaseAdapter result) {
            listView.setAdapter(result);
        }
    }

    /**
     * Customer Adapter, so that realize highlighted keywords
     */
    class HighLightKeywordsAdapter extends BaseAdapter {
        private List list;
        private Context context;
        private String[] from;
        private int[] to;
        private int layoutId;
        LayoutInflater myInflater;
        HashMap<String, String> customerCodeName;

        public HighLightKeywordsAdapter(Context context, List list,
                                        int layoutId, String[] from, int[] to) {
            this.context = context;
            this.list = list;
            this.from = from;
            this.to = to;
            this.layoutId = layoutId;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            myInflater = LayoutInflater.from(context);
            try {
                customerCodeName = (HashMap) list.get(position);
                convertView = myInflater.inflate(layoutId, parent, false);
                convertView.setTag(customerCodeName);
            } catch (Exception e) {
                Log.e(TAG, "Hight Key Error! ");
            }

            View viewCustomerCode = convertView.findViewById(to[0]);
            View viewCustomerName = convertView.findViewById(to[1]);

            TextView tvCustomerCode = (TextView) viewCustomerCode;
            TextView tvCustomerName = (TextView) viewCustomerName;


            String customerCodeValue = customerCodeName.get(from[0]);
            tvCustomerCode.setText(customerCodeValue);

            if (viewCustomerCode instanceof TextView && viewCustomerName instanceof TextView) {
                String input = mCustomerView.getText().toString();
                String upperCaseInputValue = input.toUpperCase();

                String customerNameValue = customerCodeName.get(from[1]);
                String upperCaseCustName = customerNameValue.toUpperCase();
                if (upperCaseCustName.contains(upperCaseInputValue)) {
                    if (upperCaseInputValue!=null&&upperCaseCustName.length()!=0) {
                        int start = upperCaseCustName.indexOf(upperCaseInputValue);
                        SpannableStringBuilder style_string = new SpannableStringBuilder(customerNameValue);
                        style_string.setSpan(new ForegroundColorSpan(Color.RED), start, start + input.length(),
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        tvCustomerName.setText(style_string);
                    } else {
                        tvCustomerName.setText(customerNameValue);
                    }
                }
            }

            convertView.setOnClickListener((View v) -> {
                        TextView tvCusCode = v.findViewById(to[0]);
                        TextView tvCusName = v.findViewById(to[1]);
                String code = tvCusCode.getText().toString();
                String name = tvCusName.getText().toString();
                        Map<String,String> data = new HashMap<>(2);
                        data.put("code",code);
                        data.put("name",name);
                        listener.onDoneClick(data);
                        dismiss();
                    }

            );
            return convertView;
        }

    }


    private synchronized List updateCustomerListWithInput(String input) {
        List list = new ArrayList<HashMap<String, String>>();
        for(Map<String,String> customer:mList){
            String customerCode = customer.get("code");
            String customerName = customer.get("name");
            HashMap<String, String> customerItem = new HashMap<>();
            customerItem.put(CODE, customerCode);
            customerItem.put(NAME, customerName);
            if (!isEmpty(input)){
                if(customerName.contains(input)){
                    list.add(customerItem);
                }
            }else{
                list.add(customerItem);
            }
        }
        return list;
    }

    private String getKeyWord() {
        if (mCustomerView.getText() != null && mCustomerView.getText().length() > 0) {
            return mCustomerView.getText().toString().trim();
        } else {
            return "";
        }
    }


    /**
     * 客户项被点击选中事件监听
     */
    public interface OnCustomerItemClickListener {
        void onDoneClick(Map<String,String> data);
    }


    public boolean isEmpty(String txt){
        return txt!=null&&txt.length()==0;
    }

}
