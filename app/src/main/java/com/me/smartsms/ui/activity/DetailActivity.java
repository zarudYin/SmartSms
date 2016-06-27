package com.me.smartsms.ui.activity;

import android.app.Activity;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.widget.CursorAdapter;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.me.smartsms.R;
import com.me.smartsms.bean.SmsDetailBean;

public class DetailActivity extends Activity implements View.OnClickListener, View.OnTouchListener{

    private static final String TAG = "DetailActivity";
    private String address;
    private int thread_id;

    private ListView lv_body;
    private EditText et_input;
    private Button btn_send;

    private final static int SMS_TYPE_RECEIVE = 1;
    private final static int SMS_TYPE_SEND = 2;
    private final static int INTERVAL_TIME = 3 * 60 * 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        initView();
        initListener();
        initData();
    }

    private void initView() {
        lv_body = (ListView) findViewById(R.id.lv_body);
        et_input = (EditText) findViewById(R.id.et_input);
        btn_send = (Button) findViewById(R.id.btn_send);
    }

    private void initListener() {
        btn_send.setOnClickListener(this);
    }

    private void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            address = intent.getStringExtra("address");
            thread_id = intent.getIntExtra("thread_id", -1);

            initTitleBar();

            SmsDetailCursorAdapter smsDetailCursorAdapter = new SmsDetailCursorAdapter(this, null, 1);
            lv_body.setAdapter(smsDetailCursorAdapter);

            SmsDetailQueryHelper smsDetailQueryHelper = new SmsDetailQueryHelper(getContentResolver());
            String[] projection = {
                    "_id",
                    "body",
                    "type",
                    "date"
            };
            smsDetailQueryHelper.startQuery(1, smsDetailCursorAdapter, Uri.parse("content://sms"), projection, "thread_id =" + thread_id, null, "date");
        }
    }

    private void initTitleBar() {
        findViewById(R.id.iv_titleBar_back).setOnClickListener(this);

        TextView tv_title = (TextView) findViewById(R.id.tv_titleBar_title);

        String name = findNameByPhone();
        tv_title.setText(name != null ? name : address);
    }

    private String findNameByPhone() {
        String name = null;

        String[] projection = {
                ContactsContract.PhoneLookup.DISPLAY_NAME
        };

        //获取联系人名字
        Cursor c = getContentResolver().query(Uri.withAppendedPath(
                ContactsContract.PhoneLookup.CONTENT_FILTER_URI, address), projection, null, null, null);

        if (c != null && c.getCount() > 0) {
            c.moveToFirst();
            name = c.getString(0);
            c.close();
        }

        return name;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_titleBar_back:
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    private static class SmsDetailQueryHelper extends AsyncQueryHandler {
        public SmsDetailQueryHelper(ContentResolver cr) {
            super(cr);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            while (cursor.moveToNext()) {
                Log.d(TAG, cursor.getString(1));
            }

            ((SmsDetailCursorAdapter) cookie).changeCursor(cursor);
        }
    }

    private class SmsDetailCursorAdapter extends CursorAdapter {

        public SmsDetailCursorAdapter(Context context, Cursor c, int flags) {
            super(context, c, flags);
        }

        @Override
        public Object getItem(int position) {
            return super.getItem(position);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return LayoutInflater.from(context).inflate(R.layout.listview_sms_detail_item, parent, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            ViewHolder viewholder = (ViewHolder) view.getTag();
            if (viewholder == null) {
                viewholder = new ViewHolder(view);
                view.setTag(viewholder);
            }

            SmsDetailBean smsDetailBean = new SmsDetailBean(cursor);

            if (cursor.getPosition() == 0) {
                showDate(context, smsDetailBean.getDate(), viewholder);
            } else {
                cursor.moveToPrevious();
                long lastTime = cursor.getLong(cursor.getColumnIndex("date"));
                if (smsDetailBean.getDate() - lastTime > INTERVAL_TIME) {
                    showDate(context, smsDetailBean.getDate(), viewholder);
                } else {
                    viewholder.tv_time.setVisibility(View.GONE);
                }
            }

            if (smsDetailBean.getType() == SMS_TYPE_RECEIVE) {
                viewholder.tv_left_bubble.setVisibility(View.VISIBLE);
                viewholder.tv_left_bubble.setText(smsDetailBean.getBody());
                viewholder.tv_right_bubble.setVisibility(View.GONE);
            } else {
                viewholder.tv_left_bubble.setVisibility(View.GONE);
                viewholder.tv_right_bubble.setVisibility(View.VISIBLE);
                viewholder.tv_right_bubble.setText(smsDetailBean.getBody());
            }
        }
    }

    private void showDate(Context context, long Date, ViewHolder viewHolder) {
        if (DateUtils.isToday(Date)) {
            viewHolder.tv_time.setText(DateFormat.getTimeFormat(context).format(Date));
        } else {
            viewHolder.tv_time.setText(DateFormat.getDateFormat(context).format(Date));
        }
    }

    private class ViewHolder {

        public TextView tv_time;
        public TextView tv_left_bubble;
        public TextView tv_right_bubble;

        public ViewHolder(View view) {
            tv_time = (TextView) view.findViewById(R.id.tv_time);
            tv_left_bubble = (TextView) view.findViewById(R.id.tv_left_bubble);
            tv_right_bubble = (TextView) view.findViewById(R.id.tv_right_bubble);
        }
    }
}
