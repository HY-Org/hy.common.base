package org.hy.common;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;





/**
 * 时期类型。有很多方便的功能
 * 
 * @author      ZhengWei(HY)
 * @createDate  2012-03-29
 * @version     v1.0  
 *              v1.1  2016-03-01  添加：相差值differ() 、相加plus() 、相加getPlus() 三个方法。
 *              v1.2  2016-07-19  添加：获取第几个工作日的方法getDateByWork()
 *              v1.3  2018-05-04  添加：时:分:秒 格式的自动识别转换功能。共支持  7 + 3 + 3 + 1 = 14 种格式。
 *              v1.4  2018-05-15  修复：yyyy年MM月dd日多了一个 "日"，而引起的转换异常。
 *                                添加：yyyy-MM、yyyyMM两种格式的转换。共支持  7 + 3 + 3 + 1 + 3 + 3 = 20 种格式。
 *              v1.5  2018-11-01  添加：yyyy-MM-dd HH:mm:ss.S格式的转换。共支持 7 + 3 + 3 + 1 + 3 + 3 + 3 = 23 种格式。
 *                                     建议人：邹德福
 *              v1.6  2018-12-17  添加：UTC（$FORMAT_UTC_ID）格式的转换。累计共支持 7 + 3 + 3 + 1 + 3 + 3 + 3 + 1 = 24 种格式。
 *              v1.7  2019-03-02  添加：getNextYear()、getPreviousYear()、getNextMinutes()、getPreviousMinutes()四个方法。
 */
public final class Date extends java.util.Date
{
    
    private static final long serialVersionUID = 8529353384393262590L;
    
    public  static final String               $FORMAT_Milli       = "yyyy-MM-dd HH:mm:ss.SSS"; // length=23  同时支持 yyyy/MM/dd... 、yyyy年MM月dd日... 的格式
    
    public  static final String               $FORMAT_Milli2      = "yyyy-MM-dd HH:mm:ss.S";   // length=21  同时支持 yyyy/MM/dd... 、yyyy年MM月dd日... 的格式
    
    public  static final String               $FORMAT_MilliID     = "yyyyMMddHHmmssSSS";       // length=17
    
    public  static final String               $FORMAT_Normal      = "yyyy-MM-dd HH:mm:ss";     // length=19  同时支持 yyyy/MM/dd... 、yyyy年MM月dd日... 的格式
    
    public  static final String               $FORMAT_UTC_ID      = "yyyyMMddHHmmssZ";         // length=15
    
    public  static final String               $FROMAT_ID          = "yyyyMMddHHmmss";          // length=14
    
    public  static final String               $FORMAT_YMD         = "yyyy-MM-dd";              // length=10  同时支持 yyyy/MM/dd... 、yyyy年MM月dd日... 的格式
    
    public  static final String               $FORMAT_YMD_ID      = "yyyyMMdd";                // length=8   需特殊处理
    
    public  static final String               $FORMAT_HMS         = "HH:mm:ss";                // length=8   需特殊处理
    
    public  static final String               $FORMAT_YM          = "yyyy-MM";                 // length=7   需特殊处理   同时支持 yyyy/MM 、yyyy年MM月 的格式
    
    public  static final String               $FORMAT_YM_ID       = "yyyyMM";                  // length=6   需特殊处理   同时支持 yyyy/MM 、yyyy年MM月 的格式
    
    /** 实现上面7 + 3 + 3 + 1种时间格式的快速检索 */
    private static final Map<Integer ,String> $FORMATS;
    
    /** 需要有Locale.US的配合 */
    public  static final String               $FORMAT_US          = "EEE MMM dd HH:mm:ss zzz yyyy";
    
    /** 一周的第一天，按星期天为第一天 */
    public  static final int                  WEEK_FIRST_EN  = 0;
    
    /** 一周的第一天，按星期一为第一天 */
    public  static final int                  WEEK_FIRST_CN  = 1;
    
    /** 月份最后一天是多少 */
    private static final int []               MONTH_LASTDAY  = {31 ,28 ,31 ,30 ,31 ,30 ,31 ,31 ,30 ,31 ,30 ,31};
    
    
    
    static
    {
        // 只为少一点IF判断，多一点速度提升
        $FORMATS = new Hashtable<Integer ,String>();
        $FORMATS.put($FORMAT_Milli  .length()     ,$FORMAT_Milli);
        $FORMATS.put($FORMAT_Milli  .length() + 1 ,$FORMAT_Milli);    // yyyy年MM月dd日多了一个 "日"
        $FORMATS.put($FORMAT_Milli2 .length()     ,$FORMAT_Milli2);
        $FORMATS.put($FORMAT_Milli2 .length() + 1 ,$FORMAT_Milli2);   // yyyy年MM月dd日多了一个 "日"
        $FORMATS.put($FORMAT_MilliID.length()     ,$FORMAT_MilliID);
        $FORMATS.put($FORMAT_Normal .length()     ,$FORMAT_Normal);
        $FORMATS.put($FORMAT_Normal .length() + 1 ,$FORMAT_Normal);   // yyyy年MM月dd日多了一个 "日"
        $FORMATS.put($FORMAT_UTC_ID .length()     ,$FORMAT_UTC_ID);
        $FORMATS.put($FROMAT_ID     .length()     ,$FROMAT_ID);
        $FORMATS.put($FORMAT_YMD    .length()     ,$FORMAT_YMD);
        $FORMATS.put($FORMAT_YMD    .length() + 1 ,$FORMAT_YMD);      // yyyy年MM月dd日多了一个 "日"
    }
    
    
    
    /**
     * 获取当前系统时间。可方便用于计时差。
     * 
     * 会返回一个新的实例。
     * 
     * @return
     */
    public static Date getNowTime()
    {
        return new Date();
    }
    
    
    
    /**
     * 将时长转为有格式的文本显示
     * 
     * @param i_MilliSecond
     * @return
     */
    public static String toTimeLen(long i_MilliSecond)
    {
        int  v_MilliSecond = (int)(i_MilliSecond % 1000);
        long v_Time        = (i_MilliSecond - v_MilliSecond) / 1000;
        
        if ( 0 >= v_Time )
        {
            return "0 00:00:00." + StringHelp.lpad(v_MilliSecond ,3 ,"0");
        }
        else if ( 60 > v_Time )
        {
            return "0 00:00:" + StringHelp.lpad(v_Time ,2 ,"0") + "." + StringHelp.lpad(v_MilliSecond ,3 ,"0");
        }
        
        int v_Second = (int)( v_Time % 60);                // 处理秒
        int v_Minute = (int)((v_Time % 3600)  / 60);       // 处理分钟
        int v_Hour   = (int)((v_Time % 86400) / 3600);     // 处理小时
        int v_Day    = (int)( v_Time          / 86400);    // 处理天
        
        StringBuilder v_Buffer = new StringBuilder();
        v_Buffer.append(v_Day).append(" ");
        v_Buffer.append(StringHelp.lpad(v_Hour        ,2 ,"0")).append(":");
        v_Buffer.append(StringHelp.lpad(v_Minute      ,2 ,"0")).append(":");
        v_Buffer.append(StringHelp.lpad(v_Second      ,2 ,"0")).append(".");
        v_Buffer.append(StringHelp.lpad(v_MilliSecond ,3 ,"0"));
        return v_Buffer.toString();
    }
    
    
    
    public Date() 
    {
        super();
    }
    
    
    
    public Date(long date) 
    {
        super(date);
    }
    
    
    
    public Date(int i_Year ,int i_Month ,int i_Day)
    {
        this(i_Year ,i_Month ,i_Day ,0 ,0 ,0);
    }
    
    
    
    public Date(int i_Year ,int i_Month ,int i_Day ,int i_Hour ,int i_Minute ,int i_Second)
    {
        super();
        
        Calendar v_Calendar = Calendar.getInstance();
        
        v_Calendar.set(i_Year, i_Month, i_Day ,i_Hour ,i_Minute ,i_Second);
        v_Calendar.set(Calendar.MILLISECOND ,0);
        
        this.setTime(v_Calendar.getTimeInMillis());
    }
    
    
    
    /**
     * Java标准时间转为本类时间
     * 
     * @param i_Date
     */
    public Date(java.util.Date i_Date)
    {
        super(i_Date.getTime());
    }
    
    
    
    /**
     * 数据库时间转为本类时间
     * 
     * @param i_SQLTimestamp
     */
    public Date(java.sql.Date i_SQLDate)
    {
        super(i_SQLDate.getTime());
    }
    
    
    
    /**
     * 数据库高精度时间转为本类时间
     * 
     * @param i_SQLTimestamp
     */
    public Date(Timestamp i_SQLTimestamp)
    {
        super(i_SQLTimestamp.getTime());
    }
    
    
    
    /**
     * 将指定格式的时间字符及格式化字符转为日期对象
     * 
     * 自适应多种时间格式
     * 
     * @param i_StrDateFormat
     */
    public Date(String i_StrDateFormat)
    {
        Date v_Date = null;
        
        try
        {
            String v_DateStr    = StringHelp.replaceAll(i_StrDateFormat ,new String[]{"日" ,"/" ,"年" ,"月"} ,new String[]{"" ,"-"});
            String v_DateFormat = $FORMATS.get(i_StrDateFormat.trim().length());
            
            if ( v_DateFormat == null )
            {
                if ( i_StrDateFormat.length() == 13 && Help.isNumber(i_StrDateFormat) )
                {
                    this.setTime(Long.parseLong(i_StrDateFormat));
                    return;
                }
            }
            else
            {
                if ( $FORMAT_UTC_ID == v_DateFormat )
                {
                    v_DateStr = StringHelp.replaceAll(v_DateStr ,"Z" ,"UTC");
                }
                
                v_Date = new Date(v_DateStr ,v_DateFormat);
            }
        }
        catch (Exception exce)
        {
            v_Date = new Date(i_StrDateFormat ,$FORMAT_US ,Locale.US);
        }
        
        this.setTime(v_Date.getTime());
    }
    
    
    
    /**
     * 将指定格式的时间字符及格式化字符转为日期对象
     * 
     * @param i_StrFullFormat
     * @param i_Format
     */
    public Date(String i_StrFullFormat ,String i_Format)
    {
        super();
        
        
        if ( i_StrFullFormat == null || "".equals(i_StrFullFormat.trim()) )
        {
            throw new java.lang.NullPointerException("StrFullFormat Param is null.");
        }
        
        String v_StrFullFormat = i_StrFullFormat;
        String v_Format        = i_Format;
        
        if ( v_StrFullFormat.endsWith("-") )
        {
            // 支持 yyyy-MM
            v_StrFullFormat = v_StrFullFormat.substring(0 ,v_StrFullFormat.length() - 1);
        }
        
        if ( v_Format == null || "".equals(v_Format.trim()) )
        {
            int v_Len = v_StrFullFormat.length();
            if ( v_Len <= $FORMAT_YM_ID.length() )
            {
                v_StrFullFormat += "01";
                v_Format         = $FORMAT_YMD_ID;
            }
            else if ( v_Len <= $FORMAT_YM.length() )
            {
                v_StrFullFormat += "-01";
                v_Format         = $FORMAT_YMD;
            }
            else if ( v_Len <= $FORMAT_HMS.length() )
            {
                // 支持 时:分:秒 格式的转换  ZhengWei(HY) Add 2018-05-04
                if ( i_StrFullFormat.contains(":") )
                {
                    v_Format        = $FORMAT_Normal;
                    v_StrFullFormat = "2000-01-01 " + v_StrFullFormat;
                }
                else
                {
                    v_Format = $FORMAT_YMD_ID;
                }
            }
            else
            {
                throw new java.lang.NullPointerException("Format Param is null.");
            }
        }
        
        
        SimpleDateFormat SDF_FULL = new SimpleDateFormat(v_Format);
        try
        {
            java.util.Date v_Date = SDF_FULL.parse(v_StrFullFormat);
            
            this.setDate(v_Date);
        }
        catch (Exception exce)
        {
            throw new java.lang.ClassCastException(exce.getMessage());
        }
    }
    
    
    
    /**
     * 将指定格式的时间字符及格式化字符转为日期对象
     * 
     * @param i_StrFullFormat
     * @param i_Format
     * @param i_Locale
     */
    public Date(String i_StrFullFormat ,String i_Format ,Locale i_Locale)
    {
        super();
        
        
        if ( i_StrFullFormat == null || "".equals(i_StrFullFormat.trim()) )
        {
            throw new java.lang.NullPointerException("StrFullFormat Param is null.");
        }
        
        if ( i_Format == null || "".equals(i_Format.trim()) )
        {
            throw new java.lang.NullPointerException("Format Param is null.");
        }
        
        
        SimpleDateFormat SDF_FULL = new SimpleDateFormat(i_Format ,i_Locale);
        try
        {
            java.util.Date v_Date = SDF_FULL.parse(i_StrFullFormat);
            
            this.setDate(v_Date);
        }
        catch (Exception exce)
        {
            throw new java.lang.ClassCastException(exce.getMessage());
        }
    }
    
    
    
    /**
     * 类型转换
     * 
     * @param i_Date
     * @return
     */
    public Date setDate(java.util.Date i_Date)
    {
        this.setTime(i_Date.getTime());
        
        return this;
    }
    
    
    
    /**
     * 类型转换
     * 
     * @param i_Date
     * @return
     */
    public Date setDate(java.sql.Date i_SQLDate)
    {
        this.setTime(i_SQLDate.getTime());
        
        return this;
    }
    
    
    
    /**
     * 类型转换
     * 
     * @param i_Date
     * @return
     */
    public Date setDate(Timestamp i_SQLTimestamp)
    {
        this.setTime(i_SQLTimestamp.getTime());
        
        return this;
    }
    
    
    
    /**
     * 类型转换。如果异常则返回 null
     * 
     * @param i_Str
     * @return
     */
    public Date setDate(String i_Str)
    {
        if ( i_Str == null || "".equals(i_Str.trim()) )
        {
            return null;
        }
        
        
        SimpleDateFormat SDF_FULL = new SimpleDateFormat($FORMAT_Normal);
        try
        {
            java.util.Date v_Date = SDF_FULL.parse(i_Str);
            
            this.setDate(v_Date);
            
            return this;
        }
        catch (Exception exce)
        {
            return null;
        }
    }
    
    
    
    /**
     * 设置几号。一号为：1；  三十一号为：31
     */
    public void setDate(int i_Day)
    {
        if ( 1 > i_Day || i_Day > 31 )
        {
            return;
        }
        
        int  v_OldMonth = this.getMonth();
        Date v_New      = null;
        
        if ( MONTH_LASTDAY[v_OldMonth - 1] >= i_Day )
        {
            v_New = new Date(this.getYear()
                            ,v_OldMonth - 1
                            ,i_Day
                            ,this.getHours()
                            ,this.getMinutes()
                            ,this.getSeconds());
            
            this.setDate(v_New);
        }
        else if ( v_OldMonth == 2 )
        {
            v_New = this.getLastDayOfMonth();
            
            if ( v_New.getDay() == i_Day )
            {
                this.setDate(v_New);
            }
        }
        else
        {
            return;
        }
        
    }
    
    
    
    /**
     * 设置年份
     */
    public void setYear(int i_Year)
    {
        if ( this.getYear() == i_Year || i_Year < 1900 )
        {
            return;
        }
        else
        {
            Date v_New   = null;
            int  v_Month = this.getMonth();
            int  v_Day   = this.getDay();
            
            if ( v_Month == 2 && v_Day > MONTH_LASTDAY[v_Month - 1] )
            {
                v_New = new Date(i_Year
                                ,v_Month - 1
                                ,1
                                ,this.getHours()
                                ,this.getMinutes()
                                ,this.getSeconds());
                
                v_New = v_New.getLastDayOfMonth();
            }
            else
            {
                v_New = new Date(i_Year
                                ,v_Month - 1
                                ,v_Day
                                ,this.getHours()
                                ,this.getMinutes()
                                ,this.getSeconds());
            }
            
            this.setDate(v_New);
        }
    }
    
    
    
    /**
     * 设置月份。一月为：1；  十二月为：12
     */
    public void setMonth(int i_Month)
    {
        if ( 1 > i_Month || i_Month > 12)
        {
            return;
        }
        
        int v_OldMonth = this.getMonth();
        
        if ( v_OldMonth == i_Month )
        {
            return;
        }
        
        
        int  v_OldDay   = this.getDay(); 
        Date v_New      = null;
        
        // 如果新的月份为31天
        // 如果新老月份相同，但不是2月
        // 如果天数小于等于28天
        if (  MONTH_LASTDAY[i_Month - 1] == 31
          || (i_Month != 2 && MONTH_LASTDAY[i_Month - 1] == MONTH_LASTDAY[v_OldMonth - 1]) 
          ||  v_OldDay <= 28 )
        {
            v_New = new Date(this.getYear()
                            ,i_Month - 1
                            ,this.getDay()
                            ,this.getHours()
                            ,this.getMinutes()
                            ,this.getSeconds());
        }
        else
        {
            v_New = new Date(this.getYear()
                            ,i_Month - 1
                            ,1
                            ,this.getHours()
                            ,this.getMinutes()
                            ,this.getSeconds());
            
            Date v_NewLastDate = v_New.getLastDayOfMonth();
            
            if ( v_NewLastDate.getDay() > v_OldDay )
            {
                v_New = v_New.getDate(v_OldDay - 1);
            }
            else
            {
                v_New = v_NewLastDate;
            }
        }
        
        this.setDate(v_New);
    }
    
    
    
    /**
     * 设置小时
     */
    public void setHours(int i_Hour)
    {
        if ( 0 > i_Hour || i_Hour > 23 )
        {
            return;
        }
        
        if ( this.getHours() != i_Hour )
        {
            Calendar v_Calendar = Calendar.getInstance();
            
            v_Calendar.setTime(this);
            
            v_Calendar.set(Calendar.HOUR_OF_DAY, i_Hour);
            
            this.setTime(v_Calendar.getTimeInMillis());
        }
    }
    
    
    
    /**
     * 设置分钟
     */
    public void setMinutes(int i_Minute)
    {
        if ( 0 > i_Minute || i_Minute > 59 )
        {
            return;
        }
        
        if ( this.getMinutes() != i_Minute )
        {
            Calendar v_Calendar = Calendar.getInstance();
            
            v_Calendar.setTime(this);
            
            v_Calendar.set(Calendar.MINUTE, i_Minute);
            
            this.setTime(v_Calendar.getTimeInMillis());
        }
    }
    
    
    
    /**
     * 设置秒钟
     */
    public void setSeconds(int i_Second)
    {
        if ( 0 > i_Second || i_Second > 59 )
        {
            return;
        }
        
        if ( this.getSeconds() != i_Second )
        {
            Calendar v_Calendar = Calendar.getInstance();
            
            v_Calendar.setTime(this);
            
            v_Calendar.set(Calendar.SECOND, i_Second);
            
            this.setTime(v_Calendar.getTimeInMillis());
        }
        
    }
    
    
    
    /**
     * 设置毫秒
     */
    public void setMilliSecond(int i_MilliSecond)
    {
        if ( 0 > i_MilliSecond || i_MilliSecond > 9999 )
        {
            return;
        }
        
        if ( this.getMilliSecond() != i_MilliSecond )
        {
            Calendar v_Calendar = Calendar.getInstance();
            
            v_Calendar.setTime(this);
            
            v_Calendar.set(Calendar.MILLISECOND, i_MilliSecond);
            
            this.setTime(v_Calendar.getTimeInMillis());
        }
        
    }
    
    
    
    /**
     * 设置时刻
     * 
     * @param i_Hour    小时
     * @param i_Minute  分钟
     * @param i_Second  秒钟
     */
    public void setTime(int i_Hour ,int i_Minute ,int i_Second)
    {
        this.setTime(i_Hour, i_Minute, i_Second ,0);
    }
    
    
    
    /**
     * 设置时刻
     * 
     * @param i_Hour         小时
     * @param i_Minute       分钟
     * @param i_Second       秒钟
     * @param i_MilliSecond  毫秒
     */
    public void setTime(int i_Hour ,int i_Minute ,int i_Second ,int i_MilliSecond)
    {
        if ( 0 > i_Hour || i_Hour > 23 )
        {
            return;
        }
        
        if ( 0 > i_Minute || i_Minute > 59 )
        {
            return;
        }
        
        if ( 0 > i_Second || i_Second > 59 )
        {
            return;
        }
        
        if ( 0 > i_MilliSecond || i_MilliSecond > 9999 )
        {
            return;
        }
        
        
        Calendar v_Calendar = Calendar.getInstance();
        
        v_Calendar.setTime(this);
        
        v_Calendar.set(Calendar.HOUR_OF_DAY ,i_Hour);
        v_Calendar.set(Calendar.MINUTE      ,i_Minute);
        v_Calendar.set(Calendar.SECOND      ,i_Second);
        v_Calendar.set(Calendar.MILLISECOND ,i_MilliSecond);
        
        this.setTime(v_Calendar.getTimeInMillis());
    }
    
    
    
    /**
     * 两个时间的差值。用this减去入参时间。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-03-01
     * @version     v1.0
     *
     * @param i_Other  当为空时，抛出异常
     * @return
     */
    public long differ(Date i_Other)
    {
        return this.getTime() - i_Other.getTime();
    }
    
    
    
    /**
     * 当前时间加上某个偏移量。如，加1秒，this.plus(1000);
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-03-01
     * @version     v1.0
     *
     * @param i_Offset  偏移量。可为负值 。（精度：毫秒） 
     * @return          返回自己
     */
    public Date plus(long i_Offset)
    {
        this.setTime(this.getTime() + i_Offset);
        return this;
    }
    
    
    
    /**
     * 获取当前时间加上某个偏移量的新实例。如，加1秒，this.getPlus(1000);
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-03-01
     * @version     v1.0
     *
     * @param i_Offset  偏移量。可为负值。（精度：毫秒） 
     * @return          返回一个新实例
     */
    public Date getPlus(long i_Offset)
    {
        return new Date(this.getTime() + i_Offset);
    }
    
    
    
    /**
     * 类型转换
     * 
     * @param i_Date
     * @return
     */
    public java.util.Date getDateObject()
    {
        return (java.util.Date)this;
    }
    
    
    
    /**
     * 获取年份
     */
    public int getYear()
    {
        Calendar v_Calendar = Calendar.getInstance();
        v_Calendar.setTime(this);
        
        return v_Calendar.get(Calendar.YEAR);
    }
    
    
    
    /**
     * 获取月份
     */
    public int getMonth()
    {
        Calendar v_Calendar = Calendar.getInstance();
        v_Calendar.setTime(this);
        
        return v_Calendar.get(Calendar.MONTH) + 1;
    }
    
    
    
    /**
     * 获取是几号
     */
    public int getDay()
    {
        Calendar v_Calendar = Calendar.getInstance();
        v_Calendar.setTime(this);
        
        return v_Calendar.get(Calendar.DAY_OF_MONTH);
    }
    
    
    
    /**
     * 获取是几号
     */
    public int getDate()
    {
        return this.getDay();
    }
    
    
    
    /**
     * 获取小时
     */
    public int getHours()
    {
        Calendar v_Calendar = Calendar.getInstance();
        v_Calendar.setTime(this);
        
        return v_Calendar.get(Calendar.HOUR_OF_DAY);
    }
    
    
    
    /**
     * 获取当前时间的前或后(加或减)几个小时的新实例。
     * 
     * @param i_Hour
     * @return
     */
    public Date getHours(int i_Hour)
    {
        Calendar v_Calendar = Calendar.getInstance();
        v_Calendar.setTime(this);
        v_Calendar.add(Calendar.HOUR_OF_DAY, i_Hour);
        
        return new Date(v_Calendar.getTime());
    }
    
    
    
    /**
     * 获取分钟
     */
    public int getMinutes()
    {
        Calendar v_Calendar = Calendar.getInstance();
        v_Calendar.setTime(this);
        
        return v_Calendar.get(Calendar.MINUTE);
    }
    
    
    
    /**
     * 获取当前时间的前或后(加或减)几个分钟的新实例。
     */
    public Date getMinutes(int i_Minutes)
    {
        Calendar v_Calendar = Calendar.getInstance();
        v_Calendar.setTime(this);
        v_Calendar.add(Calendar.MINUTE, i_Minutes);
        
        return new Date(v_Calendar.getTime());
    }
    
    
    
    /**
     * 获取秒
     */
    public int getSeconds()
    {
        Calendar v_Calendar = Calendar.getInstance();
        v_Calendar.setTime(this);
        
        return v_Calendar.get(Calendar.SECOND);
    }
    
    
    
    /**
     * 获取毫秒
     */
    public int getMilliSecond()
    {
        Calendar v_Calendar = Calendar.getInstance();
        v_Calendar.setTime(this);
        
        return v_Calendar.get(Calendar.MILLISECOND);
    }
    
    
    
    /**
     * 获取当前时间的前或后(加或减)几天的一个新实例。
     * 
     * @param i_Day
     * @return
     */
    public Date getDate(int i_Day)
    {
        Calendar v_Calendar = Calendar.getInstance();
        v_Calendar.setTime(this);
        v_Calendar.add(Calendar.DAY_OF_MONTH, i_Day);
        
        return new Date(v_Calendar.getTime());
    }
    
    
    
    /**
     * 获取当前时间的前或后(加或减)几个工作日的一个新实例
     * 
     * 工作日的定义：星期1 ~ 星期5 为工作日； 星期6、星期7为休息日；
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-07-19
     * @version     v1.0
     *
     * @param i_WorkDay  几个工作日
     * @return
     */
    public Date getDateByWork(int i_WorkDay)
    {
        int v_WeekNo = this.getWeek();
        
        if ( i_WorkDay == 0 )
        {
            if ( v_WeekNo >= 6 )
            {
                return this.getDate(8 - v_WeekNo);
            }
            else
            {
                return this;
            }
        }
        
        int v_WorkDay       = Math.abs(i_WorkDay);
        int v_MoveDirection = i_WorkDay >= 1 ? 1 : -1;         // 移动方向
        int v_MoveWeek      = (int)(v_WorkDay / 5);            // 移动的周数
        int v_MoveWeekMod   = v_WorkDay % 5;                   // 移动的周数后的剩余天数
        int v_MoveDay       = v_MoveWeek * 7 + v_MoveWeekMod;  // 移动的天数（非工作日）
        
        if ( v_WeekNo >= 6 )
        {
            v_MoveDay += v_WeekNo - 6;
        }
        else if ( v_WeekNo <= v_MoveWeekMod )
        {
            v_MoveDay += v_WeekNo;
        }
        
        return this.getDate(v_MoveDay * v_MoveDirection);
    }
    
    
    
    /**
     * 获取是周几。默认按中华的规则返回。
     * 星期一，返回1
     * 星期二，返回2
     * ...
     * 星期六，返回6
     * 星期天，返回7
     * 
     * @return
     */
    public int getWeek()
    {
        return this.getWeek(WEEK_FIRST_CN);
    }
    
    
    
    /**
     * 获取是周几。
     * 有选择性的返回是周几。
     * 
     * @return
     */
    public int getWeek(int i_WeekFirstDay)
    {
        int v_Week = -1;
        
        Calendar v_Calendar = Calendar.getInstance();
        v_Calendar.setTime(this);
        
        v_Week = v_Calendar.get(Calendar.DAY_OF_WEEK);
        
        
        if ( i_WeekFirstDay == WEEK_FIRST_CN )
        {
            v_Week--;
            
            if ( v_Week == 0 ) 
            {
                v_Week = 7;
            }
        }
        
        return v_Week;
    }
    
    
    
    /**
     * 获取当月的第几个周
     * 
     * @return
     */
    public int getWeekNoOfMonth()
    {
        Calendar v_Calendar = Calendar.getInstance();
        v_Calendar.setTime(this);
        
        return v_Calendar.get(Calendar.WEEK_OF_MONTH);
    }
    
    
    
    /**
     * 获取当年的第几个周
     * 
     * @return
     */
    public int getWeekNoOfYear()
    {
        Calendar v_Calendar = Calendar.getInstance();
        v_Calendar.setTime(this);
        
        return v_Calendar.get(Calendar.WEEK_OF_YEAR);
    }
    
    
    
    /**
     * 获取当月的第一天
     * 
     * 会返回一个新的实例。
     * 
     * @return
     */
    public Date getFirstDayOfMonth()
    {
        Calendar v_Calendar = Calendar.getInstance();
        v_Calendar.setTime(this);
        
        return getDate(1 - this.getDay());
    }
    
    
    
    /**
     * 获取当月的最后一天
     * 
     * 会返回一个新的实例。
     * 
     * @return
     */
    public Date getLastDayOfMonth()
    {
        Calendar v_Calendar = Calendar.getInstance();
        v_Calendar.setTime(this);
        
        return getDate(v_Calendar.getActualMaximum(Calendar.DAY_OF_MONTH) - this.getDay());
    }
    
    
    
    /**
     * 获取当周的第一天。
     * 
     * 会返回一个新的实例。
     * 
     * 默认周的第一天是星期一。
     * 
     * @return
     */
    public Date getFirstDayOfWeek()
    {
        return getFirstDayOfWeek(WEEK_FIRST_CN);
    }
    
    
    
    /**
     * 获取当周的第一天。
     * 
     * 会返回一个新的实例。
     * 
     * @return
     */
    public Date getFirstDayOfWeek(int i_WeekFirstDay)
    {
        if ( WEEK_FIRST_CN == i_WeekFirstDay && this.getWeek(i_WeekFirstDay) == 1 )
        {
            return (Date)this.clone();
        }
        else if ( this.getWeek(i_WeekFirstDay) == 0 )
        {
            return (Date)this.clone();
        }
        
        return getDate(1 - this.getWeek(i_WeekFirstDay));
    }
    
    
    
    /**
     * 获取当周的最后一天
     * 
     * 会返回一个新的实例。
     * 
     * 默认周的第一天是星期一。
     * 
     * @return
     */
    public Date getLastDayOfWeek()
    {
        return getLastDayOfWeek(WEEK_FIRST_CN);
    }
    
    
    
    /**
     * 获取当周的最后一天。
     * 
     * 会返回一个新的实例。
     * 
     * @return
     */
    public Date getLastDayOfWeek(int i_WeekFirstDay)
    {
        if ( WEEK_FIRST_CN == i_WeekFirstDay && this.getWeek(i_WeekFirstDay) == 7 )
        {
            return (Date)this.clone();
        }
        else if ( this.getWeek(i_WeekFirstDay) == 6 )
        {
            return (Date)this.clone();
        }
        
        return getDate(7 - this.getWeek(i_WeekFirstDay));
    }
    
    
    
    /**
     * 获取当天的 00:00:00 时
     * 
     * 会返回一个新的实例。
     * 
     * @return
     */
    public Date getFirstTimeOfDay()
    {
        Calendar v_Calendar = Calendar.getInstance();
        
        v_Calendar.setTime(this);
        v_Calendar.set(Calendar.HOUR_OF_DAY ,0);
        v_Calendar.set(Calendar.MINUTE      ,0);
        v_Calendar.set(Calendar.SECOND      ,0);
        v_Calendar.set(Calendar.MILLISECOND ,0);
        
        return new Date(v_Calendar.getTime());
    }
    
    
    
    /**
     * 获取当天的 23:59:59 时
     * 
     * 会返回一个新的实例。
     * 
     * @return
     */
    public Date getLastTimeOfDay()
    {
        Calendar v_Calendar = Calendar.getInstance();
        
        v_Calendar.setTime(this);
        v_Calendar.set(Calendar.HOUR_OF_DAY ,23);
        v_Calendar.set(Calendar.MINUTE      ,59);
        v_Calendar.set(Calendar.SECOND      ,59);
        v_Calendar.set(Calendar.MILLISECOND ,0);
        
        return new Date(v_Calendar.getTime());
    }
    
    
    
    /**
     * 获取当天当小时的 XX:00:00 时
     * 
     * 会返回一个新的实例。
     * 
     * @return
     */
    public Date getFirstTimeOfHour()
    {
        Calendar v_Calendar = Calendar.getInstance();
        
        v_Calendar.setTime(this);
        v_Calendar.set(Calendar.MINUTE      ,0);
        v_Calendar.set(Calendar.SECOND      ,0);
        v_Calendar.set(Calendar.MILLISECOND ,0);
        
        return new Date(v_Calendar.getTime());
    }
    
    
    
    /**
     * 获取当天当小时当分钟的 XX:XX:00 时
     * 
     * 会返回一个新的实例。
     * 
     * @return
     */
    public Date getFirstTimeOfMinute()
    {
        Calendar v_Calendar = Calendar.getInstance();
        
        v_Calendar.setTime(this);
        v_Calendar.set(Calendar.SECOND      ,0);
        v_Calendar.set(Calendar.MILLISECOND ,0);
        
        return new Date(v_Calendar.getTime());
    }
    
    
    
    /**
     * 获取当天当小时的 XX:59:59 时
     * 
     * 会返回一个新的实例。
     * 
     * @return
     */
    public Date getLastTimeOfHour()
    {
        Calendar v_Calendar = Calendar.getInstance();
        
        v_Calendar.setTime(this);
        v_Calendar.set(Calendar.MINUTE      ,59);
        v_Calendar.set(Calendar.SECOND      ,59);
        v_Calendar.set(Calendar.MILLISECOND ,0);
        
        return new Date(v_Calendar.getTime());
    }
    
    
    
    /**
     * 获取当天当小时当分钟的 XX:XX:59 时
     * 
     * 会返回一个新的实例。
     * 
     * @return
     */
    public Date getLastTimeOfMinute()
    {
        Calendar v_Calendar = Calendar.getInstance();
        
        v_Calendar.setTime(this);
        v_Calendar.set(Calendar.SECOND      ,59);
        v_Calendar.set(Calendar.MILLISECOND ,0);
        
        return new Date(v_Calendar.getTime());
    }
    
    
    
    /**
     * 获取前一月的今天this。如果今天是31号，而前一月份的最大天数为30天时，那么就返回前一月最后的一天。
     * 
     * 会返回一个新的实例。
     * 
     * @return
     */
    public Date getPreviousMonth()
    {
        Date v_Previous = this.getDate(0 - this.getDay());
        
        if ( v_Previous.getDay() > this.getDay() )
        {
            return v_Previous.getDate(0 - (v_Previous.getDay() - this.getDay()));
        }
        else
        {
            return v_Previous;
        }
    }
    
    
    
    /**
     * 获取下一个月的今天this。如果今天是31号，而下一个月份的最大天数为30天时，那么就返回下一个月最后的一天。
     * 
     * 会返回一个新的实例。
     * 
     * @return
     */
    public Date getNextMonth()
    {
        Date v_Next    = this.getLastDayOfMonth().getDate(1);
        Date v_NextMax = v_Next.getLastDayOfMonth(); 
        
        if ( v_NextMax.getDay() >= this.getDay() )
        {
            return v_Next.getDate(this.getDay() - 1);
        }
        else
        {
            return v_NextMax;
        }
    }
    
    
    
    /**
     * 获取下一个年的今天this。如果今天是31号，而下一年的月份最大天数为30天时，那么就返回下一年的月的最后一天。
     * 
     * 会返回一个新的实例。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2019-03-02
     * @version     v1.0
     *
     * @return
     */
    public Date getPreviousYear()
    {
        Date v_New = new Date(this.getTime());
        
        v_New.setYear(this.getYear() - 1);
        
        return v_New;
    }
    
    
    
    /**
     * 获取下一个年的今天this。如果今天是31号，而下一年的月份最大天数为30天时，那么就返回下一年的月的最后一天。
     * 
     * 会返回一个新的实例。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2019-03-02
     * @version     v1.0
     *
     * @return
     */
    public Date getNextYear()
    {
        Date v_New = new Date(this.getTime());
        
        v_New.setYear(this.getYear() + 1);
        
        return v_New;
    }
    
    
    
    /**
     * 获取上一分钟
     * 
     * 会返回一个新的实例。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2019-03-02
     * @version     v1.0
     *
     * @return
     */
    public Date getPreviousMinutes()
    {
        return this.getMinutes(-1);
    }
    
    
    
    /**
     * 获取下一分钟
     * 
     * @author      ZhengWei(HY)
     * @createDate  2019-03-02
     * @version     v1.0
     *
     * @return
     */
    public Date getNextMinutes()
    {
        return this.getMinutes(1);
    }
    
    
    
    /**
     * 获取昨天日期
     * 
     * 会返回一个新的实例。
     * 
     * @return
     */
    public Date getPreviousDay()
    {
        return this.getDate(-1);
    }
    
    
    
    /**
     * 获取过去一个小时的时间
     * 
     * 会返回一个新的实例。
     * 
     * @return
     */
    public Date getPreviousHour()
    {
        return this.getHours(-1);
    }
    
    
    
    /**
     * 获取过去一周的时间
     * 
     * 会返回一个新的实例。
     * 
     * @return
     */
    public Date getPreviousWeek()
    {
        return this.getDate(-7);
    }
    
    
    
    /**
     * 获取下一周的日期
     * 
     * 会返回一个新的实例。
     * 
     * @return
     */
    public Date getNextWeek()
    {
        return this.getDate(7);
    }
    
    
    
    /**
     * 获取明天日期
     * 
     * 会返回一个新的实例。
     * 
     * @return
     */
    public Date getNextDay()
    {
        return this.getDate(1);
    }
    
    
    
    /**
     * 获取下一个小时
     * 
     * 会返回一个新的实例。
     * 
     * @return
     */
    public Date getNextHour()
    {
        return this.getHours(1);
    }
    
    
    
    /**
     * 时间分组。
     * 
     * 如 2013-07-07 12:25:00 按 30 分钟分组，则返回 2013-07-07 12:00:00 ；
     * 如 2013-07-07 12:55:00 按 30 分钟分组，则返回 2013-07-07 12:30:00 ；
     * 
     * 会返回一个新的实例。
     * 
     * @param i_SplitMinuteSize  分钟分组大小
     * @return
     */
    public Date getTimeGroup(int i_SplitMinuteSize)
    {
        int  v_SplitMinuteSize = i_SplitMinuteSize % 60;
        int  v_Minute          = this.getMinutes();
        Date v_NewDate         = this.getFirstTimeOfHour();
        
        v_Minute  = (int)Math.floor(v_Minute / v_SplitMinuteSize) * v_SplitMinuteSize;
        
        v_NewDate.setMinutes(v_Minute);
        
        return v_NewDate;
    }
    
    
    
    /**
     * 获取 YYYY-MM-DD HH:MI:SS 格式的字符
     * 
     * @return
     */
    public String getFull()
    {
        return toString($FORMAT_Normal);
    }
    
    
    
    /**
     * 获取 YYYYMMDDHHMISSSSS 格式的字符
     * 
     * @return
     */
    public String getFullMilli()
    {
        return toString($FORMAT_Milli);
    }
    
    
    
    /**
     * 获取 YYYY-MM-DD 格式的字符
     * 
     * @return
     */
    public String getYMD()
    {
        return toString($FORMAT_YMD);
    }
    
    
    
    /**
     * 获取 YYYY-MM 格式的字符
     * 
     * @return
     */
    public String getYM()
    {
        return toString("yyyy-MM");
    }
    
    
    
    /**
     * 获取 MM-DD 格式的字符
     * 
     * @return
     */
    public String getMD()
    {
        return toString("MM-dd");
    }
    
    
    
    /**
     * 获取 MMDD 格式的字符
     * 
     * @return
     */
    public String getMD_ID()
    {
        return toString("MMdd");
    }
    
    
    
    /**
     * 获取 HH:MI:SS 格式的字符
     * 
     * @return
     */
    public String getHMS()
    {
        return toString("HH:mm:ss");
    }
    
    
    
    /**
     * 获取 HH:MI:SS.SSS 格式的字符
     * 
     * @return
     */
    public String getHMSMilli()
    {
        return toString("HH:mm:ss.SSS");
    }
    
    
    
    /**
     * 获取 HHMISSSSS 格式的字符
     * 
     * @return
     */
    public String getHMSMilli_ID()
    {
        return toString("HHmmssSSS");
    }
    
    
    
    /**
     * 获取 HH:MI 格式的字符
     * 
     * @return
     */
    public String getHM()
    {
        return toString("HH:mm");
    }
    
    
    
    /**
     * 获取 YYYY-MM-DD HH:MI 格式的字符
     * 
     * @return
     */
    public String getYMDHM()
    {
        return toString("yyyy-MM-dd HH:mm");
    }
    
    
    
    /**
     * 获取 YYYYMMDDHHMI 格式的字符
     * 
     * @return
     */
    public String getYMDHM_ID()
    {
        return toString("yyyyMMddHHmm");
    }
    
    
    
    /**
     * 获取 YYYYMMDDHHMISS 格式的字符
     * 
     * @return
     */
    public String getFull_ID()
    {
        return toString($FROMAT_ID);
    }
    
    
    
    /**
     * 获取 YYYYMMDDHHMISSSSS 格式的字符
     * 
     * @return
     */
    public String getFullMilli_ID()
    {
        return toString($FORMAT_MilliID);
    }
    
    
    
    /**
     * 获取 YYYYMMDD 格式的字符
     * 
     * @return
     */
    public String getYMD_ID()
    {
        return toString($FORMAT_YMD_ID);
    }
    
    
    
    /**
     * 获取 YYYYMM 格式的字符
     * 
     * @return
     */
    public String getYM_ID()
    {
        return toString("yyyyMM");
    }
    
    
    
    /**
     * 获取 HHMISS 格式的字符
     * 
     * @return
     */
    public String getHMS_ID()
    {
        return toString("HHmmss");
    }
    
    
    
    /**
     * 获取 YYYY-MM-DD HH 格式的字符
     * 
     * @return
     */
    public String getYMDH()
    {
        return toString("yyyy-MM-dd HH");
    }
    
    
    
    /**
     * 获取 YYYYMMDDHH 格式的字符
     * 
     * @return
     */
    public String getYMDH_ID()
    {
        return toString("yyyyMMddHH");
    }
    
    
    
    /**
     * 格式化时间输出
     * 
     * @param i_Format
     * @return
     */
    public String toFormat(String i_Format)
    {
        return toString(i_Format);
    }
    
    
    
    /**
     * 获取数据库类型的时间
     * 
     * @return
     */
    public java.sql.Date getSQLDate()
    {
        return new java.sql.Date(this.getTime());
    }
    
    
    
    /**
     * 获取数据库类型的时间
     * 
     * @return
     */
    public Timestamp getSQLTimestamp()
    {
        return new Timestamp(this.getTime());
    }
    
    
    
    /**
     * 获取农历信息
     * 
     * @return
     */
    public Lunar getLunar()
    {
        return new Lunar(this);
    }
    
    
    
    /**
     * 获取24节气信息
     * 
     * @return
     */
    public String getSolarTerm()
    {
        return SolarTerm.getSoralTerm(this);
    }
    
    
    
    public boolean equals(Object i_Other)
    {
        if ( i_Other == null )
        {
            return false;
        }
        else if ( i_Other instanceof Date )
        {
            java.util.Date v_Other = ((Date)i_Other).getDateObject();
            
            return super.equals(v_Other);
        }
        else if ( i_Other instanceof java.util.Date )
        {
            return super.equals(i_Other);
        }
        
        return false;
    }
    
    
    
    /**
     * 比较两个时间的年、月、日、时、分是否相等
     * 
     * @param i_Other
     * @return
     */
    public boolean equalsYMDHM(Date i_Other)
    {
        if ( i_Other == null )
        {
            return false;
        }
        else
        {
            return this.getYMDHM_ID().equals(i_Other.getYMDHM_ID());
        }
    }
    
    
    
    /**
     * 比较两个时间的年、月、日、时、分是否相等
     * 
     * @param i_Other
     * @return
     */
    public boolean equalsYMDHM(java.util.Date i_Other)
    {
        if ( i_Other == null )
        {
            return false;
        }
        else
        {
            return this.getYMDHM_ID().equals(new Date(i_Other).getYMDHM_ID());
        }
    }
    
    
    
    /**
     * 比较两个时间的年、月、日、时、分、秒是否相等
     * 
     * @param i_Other
     * @return
     */
    public boolean equalsYMDHMS(Date i_Other)
    {
        if ( i_Other == null )
        {
            return false;
        }
        else
        {
            return this.getFull_ID().equals(i_Other.getFull_ID());
        }
    }
    
    
    
    /**
     * 比较两个时间的年、月、日、时、分、秒是否相等
     * 
     * @param i_Other
     * @return
     */
    public boolean equalsYMDHMS(java.util.Date i_Other)
    {
        if ( i_Other == null )
        {
            return false;
        }
        else
        {
            return this.getFull_ID().equals(new Date(i_Other).getFull_ID());
        }
    }
    
    
    
    /**
     * 比较两个时间的年、月、日是否相等
     * 
     * @param i_Other
     * @return
     */
    public boolean equalsYMD(Date i_Other)
    {
        if ( i_Other == null )
        {
            return false;
        }
        else
        {
            return this.getYMD_ID().equals(i_Other.getYMD_ID());
        }
    }
    
    
    
    /**
     * 比较两个时间的年、月、日是否相等
     * 
     * @param i_Other
     * @return
     */
    public boolean equalsYMD(java.util.Date i_Other)
    {
        if ( i_Other == null )
        {
            return false;
        }
        else
        {
            return this.getYMD_ID().equals(new Date(i_Other).getYMD_ID());
        }
    }
    
    
    
    /**
     * 比较两个时间的时、分、秒是否相等
     * 
     * @param i_Other
     * @return
     */
    public boolean equalsHMS(Date i_Other)
    {
        if ( i_Other == null )
        {
            return false;
        }
        else
        {
            return this.getHMS_ID().equals(i_Other.getHMS_ID());
        }
    }
    
    
    
    /**
     * 比较两个时间的时、分、秒是否相等
     * 
     * @param i_Other
     * @return
     */
    public boolean equalsHMS(java.util.Date i_Other)
    {
        if ( i_Other == null )
        {
            return false;
        }
        else
        {
            return this.getHMS_ID().equals(new Date(i_Other).getHMS_ID());
        }
    }
    
    
    
    /**
     * 比较两个时间的年、月是否相等
     * 
     * @param i_Other
     * @return
     */
    public boolean equalsYM(Date i_Other)
    {
        if ( i_Other == null )
        {
            return false;
        }
        else
        {
            return this.getYM_ID().equals(i_Other.getYM_ID());
        }
    }
    
    
    
    /**
     * 比较两个时间的年、月是否相等
     * 
     * @param i_Other
     * @return
     */
    public boolean equalsYM(java.util.Date i_Other)
    {
        if ( i_Other == null )
        {
            return false;
        }
        else
        {
            return this.getYM_ID().equals(new Date(i_Other).getYM_ID());
        }
    }
    
    
    
    public int compareTo(java.util.Date i_Other)
    {
        return super.compareTo(i_Other);
    }
    
    
    
    public int compareTo(java.sql.Date i_SQLDate)
    {
        return this.compareTo(new Date(i_SQLDate));
    }
    
    
    
    public int compareTo(Timestamp i_SQLTimestamp)
    {
        return this.compareTo(new Date(i_SQLTimestamp));
    }
    
    
    
    public int compareTo(Date i_Other)
    {
        return super.compareTo(i_Other.getDateObject());
    }
    
    
    
    /**
     * 格式化时间输出
     * 
     * @param i_Format
     * @return
     */
    public String toString(String i_Format)
    {
        SimpleDateFormat SDF_TIME_ID = new SimpleDateFormat(i_Format);
        return SDF_TIME_ID.format(this);
    }
    
    
    
    public String toString()
    {
        return this.getFull();
    }

}
