package com.me.smartsms.ui.activity;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Point;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.me.smartsms.R;
import com.me.smartsms.adapter.MainViewPagerAdapter;
import com.me.smartsms.base.BaseActivity;
import com.me.smartsms.ui.fragment.ConversationFragment;
import com.me.smartsms.ui.fragment.GroupFragment;
import com.me.smartsms.ui.fragment.SearchFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

    private ViewPager viewPager;
    private TextView tvConversation, tvGrouping, tvSearch;
    private LinearLayout llConversation;
    private LinearLayout llGrouping;
    private LinearLayout llSearch;
    private TextView redLine;

    @Override
    public void initView() {
        setContentView(R.layout.activity_main);
        viewPager = (ViewPager) findViewById(R.id.vp);
        tvConversation = (TextView) findViewById(R.id.tv_conversation);
        tvGrouping = (TextView) findViewById(R.id.tv_grouping);
        tvSearch = (TextView) findViewById(R.id.tv_search);

        llConversation = (LinearLayout) findViewById(R.id.ll_conversation);
        llGrouping = (LinearLayout) findViewById(R.id.ll_grouping);
        llSearch = (LinearLayout) findViewById(R.id.ll_search);

        redLine = (TextView) findViewById(R.id.red_line);

        List<Fragment> fragments = new ArrayList<>();
        Fragment conversationFragment = new ConversationFragment();
        Fragment groupFragment = new GroupFragment();
        Fragment searchFragment = new SearchFragment();
        fragments.add(conversationFragment);
        fragments.add(groupFragment);
        fragments.add(searchFragment);

        MainViewPagerAdapter adapter = new MainViewPagerAdapter(getSupportFragmentManager(), fragments);
        if (viewPager != null) {
            viewPager.setAdapter(adapter);
        }
    }

    @Override
    public void initListener() {
        llConversation.setOnClickListener(this);
        llGrouping.setOnClickListener(this);
        llSearch.setOnClickListener(this);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.d("xx", positionOffsetPixels + "");
                redLine.animate().translationX(positionOffsetPixels / 3 + position * redLine.getWidth()).setDuration(0);
            }

            @Override
            public void onPageSelected(int position) {
                setTextLightAndScale(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.d("State", state + "");
            }
        });
    }

    @Override
    public void initData() {

    }

    @Override
    public void processEvents(View v) {
        switch (v.getId()) {
            case R.id.ll_conversation:
                viewPager.setCurrentItem(0);
                break;
            case R.id.ll_grouping:
                viewPager.setCurrentItem(1);
                break;
            case R.id.ll_search:
                viewPager.setCurrentItem(2);
                break;
            default:
                break;
        }
    }

    private void setTextLightAndScale(int position) {

        tvConversation.setTextColor(position == 0 ? Color.BLUE : Color.WHITE);
        tvGrouping.setTextColor(position == 1 ? Color.BLUE : Color.WHITE);
        tvSearch.setTextColor(position == 2 ? Color.BLUE : Color.WHITE);

        tvConversation.animate().scaleX(position == 0 ? 1.2f : 1).scaleY(position == 0 ? 1.2f : 1).setDuration(200);
        tvGrouping.animate().scaleX(position == 1 ? 1.2f : 1).scaleY(position == 1 ? 1.2f : 1).setDuration(200);
        tvSearch.animate().scaleX(position == 2 ? 1.2f : 1).scaleY(position == 2 ? 1.2f : 1).setDuration(200);

        Point point = new Point();
        getWindowManager().getDefaultDisplay().getSize(point);
        int x = point.x;
    }
}