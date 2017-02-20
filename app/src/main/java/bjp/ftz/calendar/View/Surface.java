package bjp.ftz.calendar.View;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;

/**
 * 1. 布局尺寸 2. 文字颜色，大小 3. 当前日期的颜色，选择的日期颜色
 */
public class Surface {
	private static final String TAG = null;
	/**
	 * 屏幕密度比例
	 */
	public float density;
	/**
	 * 整个控件的宽度
	 */
	public int width;
	/**
	 * 整个控件的高度
	 */
	public int height;
	/**
	 * 显示月的高度
	 */
	public float monthHeight;
	/**
	 * 显示星期的高度
	 */
	public float weekHeight;
	/**
	 * 号数方框宽度
	 */
	public float cellWidth;
	/**
	 * 号数方框高度
	 */
	public float cellHeight;
	public float borderWidth;
	/**
	 * 字体颜色
	 */
	public int textWeekColor = Color.BLACK;
	/**
	 * 表格边界颜色
	 */
	public int cellBorderColor = Color.parseColor("#00ff00");
	/**
	 * 今天号数字体颜色
	 */
	public int textTodayColor = Color.RED;
	/**
	 * 选中表格按下背景颜色
	 */
	public int cellDownBgColor = Color.parseColor("#CCFFFF");
	/**
	 * 表格选中的背景颜色
	 */
	public int cellSelectBgColor = Color.parseColor("#99CCFF");
	/**
	 * 这个月的字体颜色
	 */
	public int textInstantColor = Color.BLACK;
	/**
	 * 其他月字体颜色
	 */
	public int textOtherColor = Color.parseColor("#ccccff");
	/**
	 * 标记字体颜色
	 */
	public int textFlagColor = Color.BLUE;
	/**
	 * 标记背景颜色
	 */
	public int textFlagBgColor = Color.RED;
	/**
	 * 边框画笔
	 */
	public Paint borderPaint;
	/**
	 * 号数画笔
	 */
	// public Paint monthPaint;
	/**
	 * 星期画笔
	 */
	public Paint weekPaint;
	/**
	 * 时间画笔
	 */
	public Paint datePaint;
	/**
	 * 号数背景画笔
	 */
	public Paint cellBgPaint;
	/**
	 * 记录整个画的路径
	 */
	public Path boxPath;
	// public Path preMonthBtnPath; // 上一月按钮三角形
	// public Path nextMonthBtnPath; // 下一月按钮三角形
	/**
	 * 星期几
	 */
	public String[] weekText = { "日", "一", "二", "三", "四", "五", "六" };

	/**
	 * 自定义日历组件
	 */
	private CalendarView view;

	/**
	 * 当前显示的年月
	 */
	private String yM;

	public Surface(CalendarView view) {
		this.view = view;
	}

	/**
	 * 初始化各个数据
	 */
	@SuppressLint("WrongCall")
	public void init() {
		float temp = height / 7f;// 将整个视图分成了7份，每份所占的高度
		monthHeight = 0;// (float) ((temp + temp * 0.3f) * 0.6);
		weekHeight = (float) ((temp + temp * 0.3f) * 0.5);
		cellHeight = (height - monthHeight - weekHeight) / 6f;
		cellWidth = width / 7f;
		// 创建一个边框的画笔并设置其属性
		borderPaint = new Paint();
		borderPaint.setColor(cellBorderColor);
		borderPaint.setStyle(Paint.Style.STROKE);
		// 边框的宽度
		borderWidth = (float) (0.5 * density);
		borderWidth = borderWidth < 1 ? 1 : borderWidth;
		borderPaint.setStrokeWidth(borderWidth);

		// 创建星期画笔并设置其属性
		weekPaint = new Paint();
		weekPaint.setColor(textWeekColor);
		weekPaint.setAntiAlias(true);
		float weekTextSize = weekHeight * 0.6f;
		weekPaint.setTextSize(weekTextSize);
		weekPaint.setTypeface(Typeface.DEFAULT_BOLD);

		// 创建时间画笔并设置其属性
		datePaint = new Paint();
		datePaint.setAntiAlias(true);
		float cellTextSize = cellHeight * 0.3f;
		datePaint.setTextSize(cellTextSize);
		datePaint.setTypeface(Typeface.DEFAULT_BOLD);

		// 创建一个Path对象用于记录画笔所画的路径
		boxPath = new Path();
		// 画第一行，现在起点是0，0
		boxPath.rLineTo(width, 0);
		// 将起点向下移动一个星期格子的高度
		boxPath.moveTo(0, monthHeight + weekHeight);
		// 画第二行
		boxPath.rLineTo(width, 0);

		// 循环画纵线和号数的横线
		for (int i = 1; i < 7; i++) {
			// 纵线
			boxPath.moveTo(i * cellWidth, monthHeight);
			boxPath.rLineTo(0, height - monthHeight);
			// 横线
			boxPath.moveTo(0, monthHeight + weekHeight + i * cellHeight);
			boxPath.rLineTo(width, 0);
		}

		// 表格被选择后使用的画笔
		cellBgPaint = new Paint();
		cellBgPaint.setAntiAlias(true);
		cellBgPaint.setStyle(Paint.Style.FILL);
		cellBgPaint.setColor(cellSelectBgColor);
	}
}
