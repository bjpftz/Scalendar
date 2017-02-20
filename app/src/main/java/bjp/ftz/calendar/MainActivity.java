package bjp.ftz.calendar;


import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

import bjp.ftz.calendar.View.CalendarView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, CalendarView.OnItemClickListener {

    // 定义共享优先数据及基础字段
    private String MY_RMBCost = "MY_RMBCost";

    private TextView bt_qiandao;// 按钮

    private ImageView progressImage;

    private TextView titleText;

    private SharedPreferences my_rmb_data;
    /**
     * 自定义日历组件
     */
    private CalendarView mCalendarView;

    private String[] flagData;
    /**
     * 从储存文件中获取的时间
     */
    private String nowTime;
    /**
     * 今天的时间
     */
    private String thisTime;

    private ImageView iv_left, iv_right;

    private TextView tv_today;

    @SuppressWarnings("unchecked")
    private <T> T findView(int id) {
        return (T) findViewById(id);
    }

    /**
     * 设置浸入式状态栏
     */
    public void setStatus() {
        // TODO判断当前运行版本是否为4.4以上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatus();
        setContentView(R.layout.activity_main);
        initView();
        initData();
        initViewOper();
    }

    private void initView() {
        bt_qiandao = (TextView) findViewById(R.id.act_qd_bt_qiandao);
        titleText = (TextView) findViewById(R.id.titleText);
        progressImage = (ImageView) findViewById(R.id.progressImage);
        mCalendarView = findView(R.id.act_qd_cv_rili);
        iv_left = findView(R.id.act_qd_iv_left);
        iv_right = findView(R.id.act_qd_iv_right);
        tv_today = findView(R.id.act_qd_tv_today);
    }

    private void initData() {
        my_rmb_data = getSharedPreferences(MY_RMBCost, 0);
        /**
         * 时间函数
         */
        Calendar t = Calendar.getInstance();

        thisTime = t.get(Calendar.YEAR) + "-" + (t.get(Calendar.MONTH) + 1)
                + "-" + t.get(Calendar.DAY_OF_MONTH);
        nowTime = my_rmb_data.getString(t.get(Calendar.DAY_OF_MONTH) + "", "")
                .toString();
        flagData = new String[31];
        for (int i = 0; i < 31; i++) {
            String[] flagDay = my_rmb_data.getString((i + 1) + "", "").split(
                    "-");
            if (flagDay.length == 3) {
                flagData[i] = flagDay[2];
            } else {
                flagData[i] = "";
            }
        }
    }

    @SuppressWarnings("deprecation")
    private void initViewOper() {
        progressImage.setOnClickListener(this);
        bt_qiandao.setOnClickListener(this);
        mCalendarView.setOnItemClickListener(this);
        iv_left.setOnClickListener(this);
        iv_right.setOnClickListener(this);

        titleText.setText("签到");
        titleText.setVisibility(View.VISIBLE);

        progressImage.setImageDrawable(getResources().getDrawable(
                R.drawable.right_title_return));
        progressImage.setVisibility(View.VISIBLE);

        /**
         * 设置日历组件
         */
        mCalendarView.setFlagData(flagData);
        mCalendarView.setBackgroundColor(Color.parseColor("#00000000"));
        mCalendarView.getSurface().textOtherColor = Color.BLUE;

        /**
         * 设置当前时间
         */
        String[] split = mCalendarView.getYearAndmonth().split("-");
        tv_today.setText(split[0] + "年" + split[1] + "月");
        tv_today.setTextColor(Color.BLUE);
        /**
         * 判断是否签到
         */
        if (nowTime.equals(thisTime) == true) {
            bt_qiandao.setBackgroundResource(R.mipmap.act_yi_qian_dao);
        } else {
            bt_qiandao.setBackgroundResource(R.mipmap.act_qian_dao);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            /**
             * 签到的点击事件
             */
            case R.id.act_qd_bt_qiandao:

                SharedPreferences my_rmb_data = getSharedPreferences(MY_RMBCost, 0);

                if (nowTime.equals(thisTime)) {
                    Toast.makeText(this, "今日已签到！",
                            Toast.LENGTH_SHORT).show();
                } else {
                    String day = thisTime.split("-")[2];
                    my_rmb_data.edit().putString(day, thisTime).commit();
                    flagData[Integer.valueOf(day) - 1] = day;
                    mCalendarView.setFlagData(flagData);
                    bt_qiandao.setBackgroundResource(R.mipmap.act_yi_qian_dao);
                    Toast.makeText(this, "签到成功！坚持就是胜利，加油！！！",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.progressImage:
                this.finish();
                break;
            case R.id.act_qd_iv_left:
                String leftMonth = mCalendarView.clickLeftMonth();
                String[] split = leftMonth.split("-");
                tv_today.setText(split[0] + "年" + split[1] + "月");
                break;
            case R.id.act_qd_iv_right:
                String RightMonth = mCalendarView.clickRightMonth();
                String[] split2 = RightMonth.split("-");
                tv_today.setText(split2[0] + "年" + split2[1] + "月");
                break;
        }
    }

    @Override
    public void OnItemClick(Date selectedStartDate, Date selectedEndDate,
                            Date downDate) {
        System.out.println();
    }
}
