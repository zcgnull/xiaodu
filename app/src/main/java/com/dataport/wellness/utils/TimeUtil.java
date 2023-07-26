package com.dataport.wellness.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtil {
    private SimpleDateFormat format;

    private TimeUtil() {
    }

    private static volatile TimeUtil mTileUtil;
    Calendar c;


    public static TimeUtil getInstance() {
        if (mTileUtil == null) {
            synchronized (TimeUtil.class) {
                if (mTileUtil == null) {


                    mTileUtil = new TimeUtil();
                }
            }
        }
        return mTileUtil;
    }

    /**
     * 获取当前时间
     * @return
     */
    public String getCurrentTime() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            initData();
            long currentTime = System.currentTimeMillis();
            String timeNow = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(currentTime);
            return timeNow;
        }
        return "不支持获取";
    }

    /**
     * 获取当前时间
     * @return
     */
    public String getMainDate() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            initData();
            long currentTime = System.currentTimeMillis();
            String timeNow = new SimpleDateFormat("yyyy-MM-dd").format(currentTime);
            return timeNow;
        }
        return "不支持获取";
    }

    /**
     * 获取当前时间
     * @return
     */
    public String getMainTime() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            initData();
            long currentTime = System.currentTimeMillis();
            String timeNow = new SimpleDateFormat("HH:mm").format(currentTime);
            return timeNow;
        }
        return "不支持获取";
    }

    //十位时间戳字符串转年月
    public String dateToDate(Long time) {
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String times = sdr.format(new Date(time));
        return times;
    }

    /**
     * 获取昨天时间
     *
     * @return
     */
    public String getYesterdayTime() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            initData();
            //过去七天
            c.setTime(new Date());
            c.add(Calendar.DATE, -1);
            Date d = c.getTime();
            String day = format.format(d);
            return day;
        }
        return "不支持获取";
    }

    /**
     * 获取一周前的时间
     *
     * @return
     */
    public String getTimeAWeek() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            initData();
            //过去七天
            c.setTime(new Date());
            c.add(Calendar.DATE, -7);
            Date d = c.getTime();
            String day = format.format(d);
            return day;
        }
        return "不支持获取";
    }

    /**
     * 获取一个月前的时间
     *
     * @return
     */
    public String getTimeOneMonth() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            initData();
            //获取一个月前的时间
            c.setTime(new Date());
            c.add(Calendar.MONTH, -1);
            Date m = c.getTime();
            String mon = format.format(m);
            return mon;
        }
        return "不支持获取";
    }

    /**
     * 获取一个年前的时间
     *
     * @return
     */
    public String getTimeOneYear() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            initData();
            //获取一个年前的时间
            c.setTime(new Date());
            c.add(Calendar.YEAR, -1);
            Date y = c.getTime();
            String year = format.format(y);
            return year;
        }
        return "不支持获取";
    }


    private void initData() {
        if (c == null) {
            c = Calendar.getInstance();
        }
        if (format == null && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//年月日 - 时分秒
//            format = new SimpleDateFormat("yyyy-MM-dd");
        }
    }
}
