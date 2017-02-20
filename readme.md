#CalendarView组件继承View重写onDraw方法，利用Path对象进行路径绘制。

##效果图如下:
![image](https://github.com/bjpftz/FTZ/blob/master/%E8%87%AA%E5%AE%9A%E4%B9%89%E6%97%A5%E5%8E%86/jdfw.gif)


##这个CalendarView的API

    
      String clickLeftMonth();    //上一个月 return String(年-月)
      String clickRightMonth();   //下一个月 return String(年-月)
      Surface getSurface();       //获取整个组件画图对象，可进行设置字体颜色等 return Surface
      String getYearAndmonth();   // 获得当前应该显示的年月 return String(年-月)
      boolean isSelectMore();     //返回是否多选
      setSelectMore(boolean flag);//设置是否多选
      setFlagData(String[] flags);//设置要进行标记的数据
      setOnItemClickListener(OnItemClickListener); //点击一个日期的回调事件
      setWritingFlag(String str); //设置标记字符，默认为签到
    
