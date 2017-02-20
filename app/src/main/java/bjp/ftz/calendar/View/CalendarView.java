package bjp.ftz.calendar.View;

import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * 日历控件 功能：获得点选的日期区间
 */
public class CalendarView extends View implements View.OnTouchListener {
    private final static String TAG = "anCalendar";
    private Date selectedStartDate;
    private Date selectedEndDate;
    /**
     * 当前日历显示的月
     */
    private Date curDate;
    /**
     * 今天的日期文字显示红色
     */
    private Date today;
    /**
     * 手指按下状态时临时日期
     */
    private Date downDate;
    /**
     * 日历显示的第一个日期和最后一个日期
     */
    private Date showFirstDate, showLastDate;
    /**
     * 按下的格子索引
     */
    private int downIndex;
    private Calendar calendar;
    private Surface surface;
    /**
     * 日历显示数字
     */
    private int[] date = new int[42];
    /**
     * 当前显示的日历起,始的索引
     */
    private int curStartIndex, curEndIndex;
    /**
     * 为false表示只选择了开始日期，true表示结束日期也选择了
     */
    private boolean completed = false;

    private boolean isSelectMore = false;
    /**
     * 给控件设置监听事件
     */
    private OnItemClickListener onItemClickListener;
    /**
     * 文字标签，即在格子右上角写的字
     */
    private String writingFlag = "签到";
    /**
     * 设置要进行标记的数据
     */
    private String[] flagData;

    public CalendarView(Context context) {
        super(context);
        // 初始化数据
        init();
    }

    public CalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // 初始化数据
        init();
    }

    /**
     * 初始化数据 ,初始化事件对象 ,初始化日期格式类对象 ,Surface布局对象初始化 ,获取屏幕密度比例 ,设置View背景 ,设置触摸事件
     */
    private void init() {
        // 创建一个Date对象并将引用给显示的月，选择开始，选择结束，今天的日期
        curDate = selectedStartDate = selectedEndDate = today = new Date();
        // 获取一个日期类对象
        calendar = Calendar.getInstance();
        // 设置日期
        calendar.setTime(curDate);
        // 创建一个布局路径
        surface = new Surface(this);
        // 获取屏幕密度比例
        surface.density = getResources().getDisplayMetrics().density;
        // 给整个控件设置触摸事件
        setOnTouchListener(this);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 获取屏幕宽度
        surface.width = getResources().getDisplayMetrics().widthPixels;
        // 获取屏幕高度，并将一定比列复制给视图
        surface.height = (int) (getResources().getDisplayMetrics().heightPixels * 12 / 25);
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(surface.width,
                MeasureSpec.EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(surface.height,
                MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.d(TAG, "onDraw");
        // 计算日期
        calculateDate();
        surface.init();

        // 画用于分隔显示号数的表格框
        canvas.drawPath(surface.boxPath, surface.borderPaint);
        // 星期计算
        float weekTextY = surface.monthHeight + surface.weekHeight * 3 / 4f;
        // 绘制星期1.2.3等字体
        for (int i = 0; i < surface.weekText.length; i++) {
            float weekTextX = i
                    * surface.cellWidth
                    + (surface.cellWidth - surface.weekPaint
                    .measureText(surface.weekText[i])) / 2f;
            canvas.drawText(surface.weekText[i], weekTextX, weekTextY,
                    surface.weekPaint);
        }

        // 按下状态，选择状态背景色
        drawDownOrSelectedBg(canvas);

        // write date number
        // today index
        int todayIndex = -1;
        calendar.setTime(curDate);
        String curYearAndMonth = calendar.get(Calendar.YEAR) + ""
                + calendar.get(Calendar.MONTH);

        calendar.setTime(today);
        String todayYearAndMonth = calendar.get(Calendar.YEAR) + ""
                + calendar.get(Calendar.MONTH);
        if (curYearAndMonth.equals(todayYearAndMonth)) {
            int todayNumber = calendar.get(Calendar.DAY_OF_MONTH);
            todayIndex = curStartIndex + todayNumber - 1;
        }
        // 将要写多少字
        // int num = curEndIndex <= 35 ? 35 : 42;
        int num = 42;
        for (int i = 0; i < num; i++) {
            // 这个月的字体颜色
            int color = surface.textInstantColor;
            if (isLastMonth(i)) {
                // 上个月字体颜色
                color = surface.textOtherColor;
            } else if (isNextMonth(i)) {
                // 下个月字体颜色
                color = surface.textOtherColor;
            } else if (todayIndex != -1) {
                // 循环为签到的日期加标记
                int flagLen = flagData == null ? 0 : flagData.length;
                for (int j = 0; j < flagLen; j++) {
                    if ((date[i] + "").equals(flagData[j]))
                        drawCellFlag(canvas, i, surface.textFlagBgColor,
                                surface.textFlagColor);
                }
                // 如果todayIndex不等于-1且等于今天
                if (i == todayIndex) {
                    // 今天字体颜色
                    color = surface.textTodayColor;
                }
            }
            drawCellText(canvas, i, date[i] + "", color);
        }
        super.onDraw(canvas);
    }

    /**
     * 计算日期，计算出上月，这月下月的日期装入到一个数组里面进行保存
     */
    private void calculateDate() {
        calendar.setTime(curDate);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int dayInWeek = calendar.get(Calendar.DAY_OF_WEEK);
        Log.d(TAG, "day in week:" + dayInWeek);
        int monthStart = dayInWeek;
        monthStart -= 1; // 以日为开头-1，以星期一为开头-2
        curStartIndex = monthStart;
        date[monthStart] = 1;
        // last month
        if (monthStart > 0) {
            calendar.set(Calendar.DAY_OF_MONTH, 0);
            int dayInmonth = calendar.get(Calendar.DAY_OF_MONTH);
            for (int i = monthStart - 1; i >= 0; i--) {
                date[i] = dayInmonth;
                dayInmonth--;
            }
            calendar.set(Calendar.DAY_OF_MONTH, date[0]);
        }
        showFirstDate = calendar.getTime();

        // this month
        calendar.setTime(curDate);
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, 0);
        int monthDay = calendar.get(Calendar.DAY_OF_MONTH);
        for (int i = 1; i < monthDay; i++) {
            date[monthStart + i] = i + 1;
        }
        curEndIndex = monthStart + monthDay;

        // next month
        for (int i = monthStart + monthDay; i < 42; i++) {
            date[i] = i - (monthStart + monthDay) + 1;
        }
        if (curEndIndex < 42) {
            // 显示了下一月的
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        calendar.set(Calendar.DAY_OF_MONTH, date[41]);
        showLastDate = calendar.getTime();
    }

    /**
     * 绘制文字，即每月几号的文字，用过下标进行锁定位置
     *
     * @param canvas 画布
     * @param index  下标
     * @param text   文字
     * @param color  文字颜色
     */
    private void drawCellText(Canvas canvas, int index, String text, int color) {
        int x = getXByIndex(index);
        int y = getYByIndex(index);
        surface.datePaint.setColor(color);
        float cellY = surface.monthHeight + surface.weekHeight + (y - 1)
                * surface.cellHeight + surface.cellHeight * 3 / 4f;
        float cellX = (surface.cellWidth * (x - 1))
                + (surface.cellWidth - surface.datePaint.measureText(text))
                / 2f;
        canvas.drawText(text, cellX, cellY, surface.datePaint);
    }

    /**
     * 在格子的右上角进行绘制标签
     *
     * @param canvas    　画布
     * @param index     下标
     * @param bgcolor   背景颜色
     * @param textcolor 字体颜色
     */
    private void drawCellFlag(Canvas canvas, int index, int bgcolor,
                              int textcolor) {
        int x = getXByIndex(index);
        int y = getYByIndex(index);
        // 计算一个方格子的上下左右距离组件边框的距离，以此来推出其坐标
        float left = surface.cellWidth * (x - 1) + surface.borderWidth;
        float top = surface.monthHeight + surface.weekHeight + (y - 1)
                * surface.cellHeight - surface.borderWidth;
        float right = left + surface.cellWidth + surface.borderWidth;
        float botton = top + surface.cellHeight - surface.borderWidth;

        surface.cellBgPaint.setColor(bgcolor);
        // 通过Path来记录路径，画一个梯形图
        Path path = new Path();
        path.moveTo(right - surface.cellWidth * 2 / 3, top);
        path.lineTo(right - surface.cellWidth / 4, top);
        path.lineTo(right, botton - surface.cellHeight * 3 / 4);
        path.lineTo(right, botton - surface.cellHeight / 3);
        canvas.drawPath(path, surface.cellBgPaint);

        // 因为下面的绘制的文字将要进行旋转因此我将以上Canvas绘制的图案进行保存，这样就不会被旋转给影响到了
        canvas.save();
        // 将字体进行旋转40度，以文字开始绘制的坐标点进行旋转
        canvas.rotate((float) 45, right - surface.cellWidth * 3 / 7, botton
                - surface.cellHeight * 5 / 6);
        surface.cellBgPaint.setColor(textcolor);
        // 动态的计算字体大小
        float a = surface.cellWidth / 4;
        float b = surface.cellHeight / 4;
        float c = (float) Math.sqrt(a * a + b * b);
        surface.cellBgPaint.setTextSize(c * 3 / 5);
        surface.cellBgPaint.setTypeface(Typeface.DEFAULT_BOLD);
        // 绘制文字
        canvas.drawText(writingFlag, right - surface.cellWidth * 3 / 7, botton
                - surface.cellHeight * 5 / 6, surface.cellBgPaint);
        // 释放旋转状态，恢复sava时的状态
        canvas.restore();
    }

    /**
     * 通过格子的下标进行绘制格子背景
     *
     * @param canvas 画布
     * @param index  选中的下标
     * @param color  颜色
     */
    private void drawCellBg(Canvas canvas, int index, int color) {
        int x = getXByIndex(index);
        int y = getYByIndex(index);
        surface.cellBgPaint.setColor(color);
        float left = surface.cellWidth * (x - 1) + surface.borderWidth;
        float top = surface.monthHeight + surface.weekHeight + (y - 1)
                * surface.cellHeight + surface.borderWidth;
        canvas.drawRect(left, top, left + surface.cellWidth
                - surface.borderWidth, top + surface.cellHeight
                - surface.borderWidth, surface.cellBgPaint);
    }

    /**
     * @param canvas
     */
    private void drawDownOrSelectedBg(Canvas canvas) {
        // down and not up
        if (downDate != null) {
            drawCellBg(canvas, downIndex, surface.cellDownBgColor);
        }
        // selected bg color
        if (!selectedEndDate.before(showFirstDate)
                && !selectedStartDate.after(showLastDate)) {
            int[] section = new int[]{-1, -1};
            calendar.setTime(curDate);
            calendar.add(Calendar.MONTH, -1);
            findSelectedIndex(0, curStartIndex, calendar, section);
            if (section[1] == -1) {
                calendar.setTime(curDate);
                findSelectedIndex(curStartIndex, curEndIndex, calendar, section);
            }
            if (section[1] == -1) {
                calendar.setTime(curDate);
                calendar.add(Calendar.MONTH, 1);
                findSelectedIndex(curEndIndex, 42, calendar, section);
            }
            if (section[0] == -1) {
                section[0] = 0;
            }
            if (section[1] == -1) {
                section[1] = 41;
            }
            for (int i = section[0]; i <= section[1]; i++) {
                drawCellBg(canvas, i, surface.cellSelectBgColor);
            }
        }
    }

    /**
     * @param startIndex
     * @param endIndex
     * @param calendar
     * @param section
     */
    private void findSelectedIndex(int startIndex, int endIndex,
                                   Calendar calendar, int[] section) {
        for (int i = startIndex; i < endIndex; i++) {
            calendar.set(Calendar.DAY_OF_MONTH, date[i]);
            Date temp = calendar.getTime();
            // Log.d(TAG, "temp:" + temp.toLocaleString());
            if (temp.compareTo(selectedStartDate) == 0) {
                section[0] = i;
            }
            if (temp.compareTo(selectedEndDate) == 0) {
                section[1] = i;
                return;
            }
        }
    }

    /**
     * 是否是上月
     *
     * @param i 索引
     * @return if yes return true or return false
     */
    private boolean isLastMonth(int i) {
        if (i < curStartIndex) {
            return true;
        }
        return false;
    }

    /**
     * 是否是下月
     *
     * @param i 索引
     * @return if yes return true or return false
     */
    private boolean isNextMonth(int i) {
        if (i >= curEndIndex) {
            return true;
        }
        return false;
    }

    /**
     * 通过下标找出此下标的X坐标是在第几列
     *
     * @param i 下标
     * @return 列数
     */
    private int getXByIndex(int i) {
        return i % 7 + 1; // 1 2 3 4 5 6 7
    }

    /**
     * 通过下标找出下标的Y坐标是在第几行
     *
     * @param i 下标
     * @return 行数
     */
    private int getYByIndex(int i) {
        return i / 7 + 1; // 1 2 3 4 5 6
    }

    /**
     * 获得当前应该显示的年月
     *
     * @return 年-月
     */
    public String getYearAndmonth() {
        calendar.setTime(curDate);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        return year + "-" + month;
    }

    /**
     * 上一月
     *
     * @return
     */
    public String clickLeftMonth() {
        calendar.setTime(curDate);
        calendar.add(Calendar.MONTH, -1);
        curDate = calendar.getTime();
        invalidate();
        return getYearAndmonth();
    }

    /**
     * 下一月
     *
     * @return
     */
    public String clickRightMonth() {
        calendar.setTime(curDate);
        calendar.add(Calendar.MONTH, 1);
        curDate = calendar.getTime();
        invalidate();
        return getYearAndmonth();
    }

    /**
     * 获取日历时间
     */
    private void getCalendatData() {
        calendar.getTime();
    }

    /**
     * 返回设置是否多选
     *
     * @return
     */
    public boolean isSelectMore() {
        return isSelectMore;
    }

    /**
     * @param isSelectMore
     */
    public void setSelectMore(boolean isSelectMore) {
        this.isSelectMore = isSelectMore;
    }

    /**
     * @param x
     * @param y
     */
    private void setSelectedDateByCoor(float x, float y) {
        if (y > surface.monthHeight + surface.weekHeight) {
            int m = (int) (Math.floor(x / surface.cellWidth) + 1);
            int n = (int) (Math
                    .floor((y - (surface.monthHeight + surface.weekHeight))
                            / Float.valueOf(surface.cellHeight)) + 1);
            downIndex = (n - 1) * 7 + m - 1;
            Log.d(TAG, "downIndex:" + downIndex);
            calendar.setTime(curDate);
            if (isLastMonth(downIndex)) {
                calendar.add(Calendar.MONTH, -1);
            } else if (isNextMonth(downIndex)) {
                calendar.add(Calendar.MONTH, 1);
            }
            // 如果大于数组长度会造成数组越界
            if (downIndex > date.length) {
                return;
            }
            calendar.set(Calendar.DAY_OF_MONTH, date[downIndex]);
            downDate = calendar.getTime();
        }
        invalidate();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (onItemClickListener == null) {
            return true;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                setSelectedDateByCoor(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_UP:
                if (downDate != null) {
                    if (isSelectMore) {
                        if (!completed) {
                            if (downDate.before(selectedStartDate)) {
                                selectedEndDate = selectedStartDate;
                                selectedStartDate = downDate;
                            } else {
                                selectedEndDate = downDate;
                            }
                            completed = true;
                            // 响应监听事件
                            onItemClickListener.OnItemClick(selectedStartDate,
                                    selectedEndDate, downDate);
                        } else {
                            selectedStartDate = selectedEndDate = downDate;
                            completed = false;
                        }
                    } else {
                        selectedStartDate = selectedEndDate = downDate;
                        // 响应监听事件
                        onItemClickListener.OnItemClick(selectedStartDate,
                                selectedEndDate, downDate);
                    }
                    invalidate();
                }

                break;
        }
        return true;
    }

    /**
     * 给控件设置监听事件
     *
     * @param onItemClickListener 点击Item响应回调接口
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * 监听接口
     *
     * @author Administrator
     */
    public interface OnItemClickListener {
        void OnItemClick(Date selectedStartDate, Date selectedEndDate,
                         Date downDate);
    }

    /**
     * 获取本月最后一天所占格子的位置
     *
     * @return
     */
    private int getcurEndIndex() {
        return curEndIndex;
    }

    /**
     * 设置要进行标记的数据
     *
     * @param flagData
     */
    public void setFlagData(String[] flagData) {
        this.flagData = flagData;
        calendar.setTime(curDate);
        calendar.add(Calendar.MONTH, 0);
        curDate = calendar.getTime();
        invalidate();
    }

    /**
     * 设置标记字符，默认为签到
     *
     * @param writingFlag
     */
    public void setWritingFlag(String writingFlag) {
        this.writingFlag = writingFlag;
    }

    /**
     * 获取整个组件画图对象
     *
     * @return
     */
    public Surface getSurface() {
        return surface;
    }
}
