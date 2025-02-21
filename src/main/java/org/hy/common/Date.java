package org.hy.common;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;





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
 *              v2.0  2021-03-01  添加：将原先 setDate(...) 的方法重写，其它方法均改为toDate(...)方法。
 *                                      只保留这一个setDate(String)方法。为了方便SpringBoot中的FashJson类的序列化对象
 *              v2.1  2023-03-07  添加：$FORMAT_Milli3 的格式，用于SQLServer中DateTime2在RS.getObject()方法下的使用
 *              v3.0  2024-01-14  添加：支持时分秒在前年月日在后的格式，即 HH24:MI:SS YYYY-MM-DD
 *                                     支持月、日、时、分、秒为1位数字时，前面没有0的格式
 *                                     支持YYYY-MM-DD HH24格式，以及时刻与日期交换位置的格式
 *                                     支持YYYY-MM-DD HH24:MI格式，以及时刻与日期交换位置的格式
 *              v4.0  2024-05-30  添加：时区换算 GMT、UTC、CST、CET、DST、EDT、PDT 7种时间标准
 *                                添加：支持2024-05-30T01:01:01          格式的转时间。即LocalDateTime的格式
 *                                添加：支持2024-05-30T01:01:01.123456789格式的转时间。即LocalDateTime的格式
 *                                添加：支持2024-05-30T01:01:01.123+08:00[Asia/Shanghai]       格式的转时间。即ZonedDateTime的格式
 *                                添加：支持2024-05-30T01:01:01.123456789+08:00[Asia/Shanghai] 格式的转时间。即ZonedDateTime的格式
 *              v4.1 2024-06-03   添加：支持2024-05-30T01:01:01.123+08:00                      格式的转时间。即ZonedDateTime的格式
 *                                添加：支持2024-05-30T01:01:01+08:00                          格式的转时间。即ZonedDateTime的格式
 *              v4.2 2025-02-21   添加：支持2025-02-21 01:01:01.123456789格式的转时间
 */
public final class Date extends java.util.Date
{
    
    private static final long serialVersionUID = 8529353384393262590L;
    
    public  static final String               $FORMAT_Nano_9nID   = "yyyyMMddHHmmssnnnnnnnnn";       // length=23  与23冲突未配置到自动识别中
    
    public  static final String               $FORMAT_Nano_9n     = "yyyy-MM-dd HH:mm:ss.nnnnnnnnn"; // length=29  同时支持 yyyy/MM/dd... 、yyyy年MM月dd日... 的格式
    
    public  static final String               $FORMAT_Nano        = "yyyy-MM-ddTHH:mm:ss.SSSSSSSSS"; // length=29  同时支持 yyyy/MM/dd... 、yyyy年MM月dd日... 的格式
    
    public  static final String               $FORMAT_Milli3      = "yyyy-MM-dd HH:mm:ss.SSSSSSS";   // length=27  同时支持 yyyy/MM/dd... 、yyyy年MM月dd日... 的格式
                                                                                                     
    public  static final String               $FORMAT_Milli       = "yyyy-MM-dd HH:mm:ss.SSS";       // length=23  同时支持 yyyy/MM/dd... 、yyyy年MM月dd日... 的格式
                                                                                                     
    public  static final String               $FORMAT_Milli2      = "yyyy-MM-dd HH:mm:ss.S";         // length=21  同时支持 yyyy/MM/dd... 、yyyy年MM月dd日... 的格式
                                                                                                     
    public  static final String               $FORMAT_MilliID     = "yyyyMMddHHmmssSSS";             // length=17
                                                                                                     
    public  static final String               $FORMAT_Normal      = "yyyy-MM-dd HH:mm:ss";           // length=19  同时支持 yyyy/MM/dd... 、yyyy年MM月dd日... 的格式
                                                                                                     
    public  static final String               $FORMAT_UTC_ID      = "yyyyMMddHHmmssZ";               // length=15
                                                                                                     
    public  static final String               $FROMAT_ID          = "yyyyMMddHHmmss";                // length=14
                                                                                                     
    public  static final String               $FORMAT_YMD         = "yyyy-MM-dd";                    // length=10  同时支持 yyyy/MM/dd... 、yyyy年MM月dd日... 的格式
                                                                                                     
    public  static final String               $FORMAT_YMD_ID      = "yyyyMMdd";                      // length=8
                                                                                                     
    public  static final String               $FORMAT_HMS         = "HH:mm:ss";                      // length=8   需特殊处理
                                                                                                     
    public  static final String               $FORMAT_YM          = "yyyy-MM";                       // length=7   同时支持 yyyy/MM 、yyyy年MM月 的格式
                                                                                                     
    public  static final String               $FORMAT_YM_ID       = "yyyyMM";                        // length=6   同时支持 yyyy/MM 、yyyy年MM月 的格式
    
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
        $FORMATS.put($FORMAT_Milli3 .length() ,$FORMAT_Milli3);     // 27
        $FORMATS.put($FORMAT_Milli  .length() ,$FORMAT_Milli);      // 23
        $FORMATS.put($FORMAT_Milli2 .length() ,$FORMAT_Milli2);     // 21
        $FORMATS.put($FORMAT_MilliID.length() ,$FORMAT_MilliID);    // 17
        $FORMATS.put($FORMAT_UTC_ID .length() ,$FORMAT_UTC_ID);     // 15
        $FORMATS.put($FORMAT_Normal .length() ,$FORMAT_Normal);     // 14
        $FORMATS.put($FROMAT_ID     .length() ,$FROMAT_ID);         // 8
        $FORMATS.put($FORMAT_YMD    .length() ,$FORMAT_YMD);        // 7
        $FORMATS.put($FORMAT_YMD_ID .length() ,$FORMAT_YMD_ID);     // 6
    }
    
    
    
    /**
     * 与 getNowTime 同义。与 LocalDateTime.now() 、ZonedDateTime.now() 保持同步
     * 
     * @author      ZhengWei(HY)
     * @createDate  2024-05-30
     * @version     v1.0
     *
     * @return
     */
    public static Date now()
    {
        return new Date();
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
    
    
    
    /**
     * Date.toTimeLen() 方法的逆向操作。
     * 
     * 即：将 0 00:00:00.0 的字符串格式，转为时间戳
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-06-28
     * @version     v1.0
     * 
     * @param i_TimeString
     * @return              非法格式返回 null
     */
    public static Long toTimeValue(String i_TimeString)
    {
        if ( Help.isNull(i_TimeString) )
        {
            return null;
        }
        
        String [] v_DayAndTime = i_TimeString.trim().split(" ");
        String [] v_HMS        = null;
        long      v_TimeValue  = 0L;
        if ( v_DayAndTime.length == 2 )
        {
            // 解释天
            v_DayAndTime[0] = v_DayAndTime[0].trim();
            if ( Help.isNumber(v_DayAndTime[0]) )
            {
                v_TimeValue = Long.parseLong(v_DayAndTime[0]) * 24 * 60 * 60 * 1000;
            }
            
            v_HMS = v_DayAndTime[1].trim().split(":");
        }
        else if ( v_DayAndTime.length == 1 )
        {
            v_HMS = v_DayAndTime[0].trim().split(":");
        }
        else
        {
            return null;
        }
        
        if ( v_HMS.length != 3 )
        {
            return null;
        }
        
        // 解释毫秒
        String [] v_MilliSecond = v_HMS[2].trim().split("\\.");
        if ( v_MilliSecond.length == 2 )
        {
            v_MilliSecond[1] = v_MilliSecond[1].trim();
            if ( Help.isNumber(v_MilliSecond[1]) )
            {
                v_TimeValue += Long.parseLong(v_MilliSecond[1]);
            }
            
            v_HMS[2] = v_MilliSecond[0];
        }
        
        // 解释时分秒
        int [] v_Times = {60 * 60 * 1000 ,60 * 1000 ,1000};
        for (int i=0; i<v_HMS.length; i++)
        {
            v_HMS[i] = v_HMS[i].trim();
            
            if ( !Help.isNumber(v_HMS[i]) )
            {
                return null;
            }
            
            v_TimeValue += Long.parseLong(v_HMS[i]) * v_Times[i];
        }
        
        return v_TimeValue;
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
     * 将自己转为自己
     * 
     * @param i_Date
     */
    public Date(Date i_Date)
    {
        super(i_Date.getTime());
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
     * 丢精度转时间
     *
     * @author      ZhengWei(HY)
     * @createDate  2024-05-30
     * @version     v1.0
     *
     * @param i_LocalDateTime
     */
    public Date(LocalDateTime i_LocalDateTime)
    {
        super(Date.from(i_LocalDateTime.atZone(ZoneId.systemDefault()).toInstant()).getTime());
    }
    
    
    
    /**
     * 丢精度转时间。时区转换算成当前系统的时区
     *
     * @author      ZhengWei(HY)
     * @createDate  2024-05-30
     * @version     v1.0
     *
     * @param i_ZonedDateTime
     */
    public Date(ZonedDateTime i_ZonedDateTime)
    {
        super(Date.from(i_ZonedDateTime.toInstant()).getTime());
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
        this.toDate(i_StrDateFormat);
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
        
        if ( Help.isNull(i_StrFullFormat) )
        {
            throw new java.lang.NullPointerException("Date value param is null.");
        }
        
        if ( Help.isNull(i_Format) )
        {
            throw new java.lang.NullPointerException("Date format param is null.");
        }
        
        SimpleDateFormat SDF_FULL = new SimpleDateFormat(i_Format);
        try
        {
            java.util.Date v_Date = SDF_FULL.parse(i_StrFullFormat);
            
            this.toDate(v_Date);
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
        
        if ( Help.isNull(i_StrFullFormat) )
        {
            throw new java.lang.NullPointerException("Date value param is null.");
        }
        
        if ( Help.isNull(i_Format) )
        {
            throw new java.lang.NullPointerException("Date format param is null.");
        }
        
        SimpleDateFormat SDF_FULL = new SimpleDateFormat(i_Format ,i_Locale);
        try
        {
            java.util.Date v_Date = SDF_FULL.parse(i_StrFullFormat);
            
            this.toDate(v_Date);
        }
        catch (Exception exce)
        {
            throw new java.lang.ClassCastException(exce.getMessage());
        }
    }
    
    
    
    /**
     * 自己转自己
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-02-22
     * @version     v1.0
     *
     * @param i_Date
     * @return
     */
    public Date toDate(Date i_Date)
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
    public Date toDate(java.util.Date i_Date)
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
    public Date toDate(java.sql.Date i_SQLDate)
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
    public Date toDate(Timestamp i_SQLTimestamp)
    {
        this.setTime(i_SQLTimestamp.getTime());
        
        return this;
    }
    
    
    
    /**
     * 类型转换。丢精度
     * 
     * @author      ZhengWei(HY)
     * @createDate  2024-05-30
     * @version     v1.0
     *
     * @param i_LocalDateTime
     * @return
     */
    public Date toDate(LocalDateTime i_LocalDateTime)
    {
        this.setTime(Date.from(i_LocalDateTime.atZone(ZoneId.systemDefault()).toInstant()).getTime());
        return this;
    }
    
    
    
    /**
     * 类型转换。丢精度转时间。时区转换算成当前系统的时区
     * 
     * @author      ZhengWei(HY)
     * @createDate  2024-05-30
     * @version     v1.0
     *
     * @param i_ZonedDateTime
     * @return
     */
    public Date toDate(ZonedDateTime i_ZonedDateTime)
    {
        this.setTime(Date.from(i_ZonedDateTime.toInstant()).getTime());
        return this;
    }
    
    
    
    /**
     * 类型转换
     * 
     * @param i_StrDateFormat
     * @return
     */
    public Date toDate(String i_StrDateFormat)
    {
        Date    v_Date       = null;
        boolean v_Is00       = false;
        String  v_DateStr    = null;
        String  v_DateFormat = null;
        
        if ( Help.isNull(i_StrDateFormat) )
        {
            v_Date = new Date();
        }
        else
        {
            try
            {
                v_DateStr = i_StrDateFormat.trim();
                if ( v_DateStr.lastIndexOf("+") >= 19 || v_DateStr.lastIndexOf("-") >= 19 )
                {
                    ZonedDateTime v_ZonedDateTime = ZonedDateTime.parse(v_DateStr);
                    v_Date = new Date(v_ZonedDateTime);
                }
                else if ( v_DateStr.length() == $FORMAT_Nano.length() )
                {
                    if ( v_DateStr.indexOf("T") > 0 )
                    {
                        LocalDateTime v_LocalDateTime = LocalDateTime.parse(v_DateStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                        v_Date = new Date(v_LocalDateTime);
                    }
                    else
                    {
                        DateTimeFormatter v_Formatter = DateTimeFormatter.ofPattern($FORMAT_Nano_9n);
                        LocalDateTime v_LocalDateTime = LocalDateTime.parse(v_DateStr, v_Formatter);
                        v_Date = new Date(v_LocalDateTime);
                    }
                }
                else
                {
                    v_DateStr    = StringHelp.replaceAll(i_StrDateFormat.trim()
                                                        ,new String[]{"T" ,"日" ,"/" ,"年" ,"月" ,}
                                                        ,new String[]{" " ,""  ,"-"});
                    v_DateFormat = $FORMATS.get(v_DateStr.length());
                    if ( v_DateFormat == null )
                    {
                        if ( v_DateStr.length() == 13 && Help.isNumber(v_DateStr) )
                        {
                            this.setTime(Long.parseLong(v_DateStr));
                            return this;
                        }
                        else
                        {
                            v_DateStr    = toDate00(v_DateStr);
                            v_Is00       = true;
                            v_DateFormat = $FORMATS.get(v_DateStr.length());
                            v_Date = new Date(v_DateStr ,v_DateFormat);
                        }
                    }
                    // 预防仅通长度而造成的误判
                    else if ( v_DateStr.indexOf("-") > 0 &&
                             ($FORMAT_YMD_ID .equals(v_DateFormat)
                           || $FORMAT_YM_ID  .equals(v_DateFormat)
                           || $FORMAT_UTC_ID .equals(v_DateFormat)
                           || $FORMAT_MilliID.equals(v_DateFormat)
                           || $FROMAT_ID     .equals(v_DateFormat)) )
                    {
                        v_DateStr    = toDate00(v_DateStr);
                        v_Is00       = true;
                        v_DateFormat = $FORMATS.get(v_DateStr.length());
                        v_Date = new Date(v_DateStr ,v_DateFormat);
                    }
                    // 预防仅通长度而造成的误判。如有小时并未识别出来
                    else if ( v_DateStr.indexOf(" ") > 0 &&
                             ($FORMAT_YMD.equals(v_DateFormat)
                           || $FORMAT_YM.equals(v_DateFormat)) )
                    {
                        v_DateStr    = toDate00(v_DateStr);
                        v_Is00       = true;
                        v_DateFormat = $FORMATS.get(v_DateStr.length());
                        v_Date = new Date(v_DateStr ,v_DateFormat);
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
            }
            catch (Exception exce)
            {
                if ( !v_Is00 )
                {
                    v_DateStr    = toDate00(v_DateStr);
                    v_DateFormat = $FORMATS.get(v_DateStr.length());
                }
                else
                {
                    v_DateFormat = null;
                }
                
                if ( v_DateFormat != null )
                {
                    v_Date = new Date(v_DateStr ,v_DateFormat);
                }
                else
                {
                    v_Date = new Date(v_DateStr ,$FORMAT_US ,Locale.US);
                }
            }
        }
        
        this.setTime(v_Date.getTime());
        return this;
    }
    
    

    /**
     * 处理 00 格式，即1月改为 01； 8点改为 08等
     * 
     * @author      ZhengWei(HY)
     * @createDate  2024-01-14
     * @version     v1.0
     *
     * @param i_DateStr
     * @return
     */
    private String toDate00(String i_DateStr)
    {
        String []     v_YMD_HMS = i_DateStr.split(" ");
        StringBuilder v_Buffer  = new StringBuilder();
        
        for (int y=0; y<v_YMD_HMS.length; y++)
        {
            if ( y > 0 )
            {
                v_Buffer.append(" ");
            }
            
            if ( v_YMD_HMS[y].indexOf("-") > 0 )
            {
                String [] v_YMD = v_YMD_HMS[y].split("-");
                for (int x=0; x<v_YMD.length; x++)
                {
                    if ( x > 0 )
                    {
                        v_Buffer.append("-");
                    }
                    if ( v_YMD[x].length() == 1 )
                    {
                        v_Buffer.append("0");
                    }
                    v_Buffer.append(v_YMD[x]);
                }
                
                // 支持YYYY-MM的格式，补全天
                if ( v_YMD.length == 2 )
                {
                    v_Buffer.append("-01");
                }
            }
            else if ( v_YMD_HMS[y].indexOf(":") > 0 )
            {
                // 支持 HH:MI:SS的格式，补全年月日
                if ( y == 0 )
                {
                    if ( v_YMD_HMS.length == 1 )
                    {
                        v_Buffer.append("2000-01-01 ");
                    }
                    else
                    {
                        // 支持 HH:MI:SS YYYY-MM-DD的格式，即日期在时刻的后面
                        return toDate00(v_YMD_HMS[1] + " " + v_YMD_HMS[0]);
                    }
                }
                
                String [] v_HMS = v_YMD_HMS[y].split(":");
                for (int x=0; x<v_HMS.length; x++)
                {
                    if ( x > 0 )
                    {
                        v_Buffer.append(":");
                    }
                    if ( v_HMS[x].length() == 1 )
                    {
                        v_Buffer.append("0");
                    }
                    v_Buffer.append(v_HMS[x]);
                }
                
                // 支持HH:MI的格式，补全秒
                if ( v_HMS.length == 2 )
                {
                    v_Buffer.append(":00");
                }
            }
            else if ( y == 1 )
            {
                // 支持 YYYY-MM-DD HH的格式，补全分钟和秒
                if ( v_YMD_HMS[y].length() == 1 )
                {
                    v_Buffer.append("0");
                    v_Buffer.append(v_YMD_HMS[y]);
                    v_Buffer.append(":00:00");
                }
                else if ( v_YMD_HMS[y].length() == 2 )
                {
                    v_Buffer.append(v_YMD_HMS[y]);
                    v_Buffer.append(":00:00");
                }
                else
                {
                    v_Buffer.append(v_YMD_HMS[y]);
                }
            }
        }
        
        if ( v_Buffer.length() <= 0 )
        {
            if ( i_DateStr.length() <= $FORMAT_YM_ID.length() )
            {
                v_Buffer.append(i_DateStr);
                v_Buffer.append("01");
            }
        }
        
        return v_Buffer.toString();
    }
    
    
    
    /**
     * 去除其它所有 setDate(...) 的方法重写，其它方法均改为toDate(...)方法。
     * 只保留这一个方法。为了方便SpringBoot中的FashJson类的序列化对象
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-03-01
     * @version     v1.0
     *
     * @param i_StrDateFormat
     */
    public Date setDate(String i_StrDateFormat)
    {
        return this.toDate(i_StrDateFormat);
    }
    
    
    
    /**
     * 设置几号。一号为：1；  三十一号为：31
     */
    @Override
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
            
            this.toDate(v_New);
        }
        else if ( v_OldMonth == 2 )
        {
            v_New = this.getLastDayOfMonth();
            
            if ( v_New.getDay() == i_Day )
            {
                this.toDate(v_New);
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
    @Override
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
            
            this.toDate(v_New);
        }
    }
    
    
    
    /**
     * 设置月份。一月为：1；  十二月为：12
     */
    @Override
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
        
        this.toDate(v_New);
    }
    
    
    
    /**
     * 设置小时
     */
    @Override
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
    @Override
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
    @Override
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
     * 格式化时区的格式
     * 
     * @author      ZhengWei(HY)
     * @createDate  2024-05-30
     * @version     v1.0
     *
     * @param i_TimeZone  时区。支持如下格式
     *                         1. GMT+08:00    同时支持 GMT、UTC、CST、CET、DST、EDT、PDT 7种时间标准
     *                         2. GMT+08       同时支持 GMT、UTC、CST、CET、DST、EDT、PDT 7种时间标准
     *                         3. GMT+8        同时支持 GMT、UTC、CST、CET、DST、EDT、PDT 7种时间标准
     *                         4. GMT08:00     同时支持 GMT、UTC、CST、CET、DST、EDT、PDT 7种时间标准
     *                         5. GMT08        同时支持 GMT、UTC、CST、CET、DST、EDT、PDT 7种时间标准
     *                         6. GMT8         同时支持 GMT、UTC、CST、CET、DST、EDT、PDT 7种时间标准
     *                         7. +08:00       默认为：GMT
     *                         8. 08:00        默认为：GMT
     *                         9. 08           默认为：GMT
     *                        10. 8            默认为：GMT
     * @return
     */
    public static String formatTimeZone(String i_TimeZone)
    {
        if ( Help.isNull(i_TimeZone) )
        {
            return i_TimeZone;
        }
        
        String v_TimeZone = i_TimeZone.trim().toUpperCase();
        
        // 解释与格式化时区格式。如，GMT+08:00
        if ( v_TimeZone.length() != 9 )
        {
            StringBuilder v_TimeZoneBuff = new StringBuilder();
            
            // 格林威治标准时间
            if ( v_TimeZone.startsWith("GMT") )
            {
                v_TimeZoneBuff.append(v_TimeZone.substring(0 ,3));
                v_TimeZone = v_TimeZone.substring(3);
            }
            // 协调世界时（Coordinated Universal Time）
            else if ( v_TimeZone.startsWith("UTC") )
            {
                v_TimeZoneBuff.append(v_TimeZone.substring(0 ,3));
                v_TimeZone = v_TimeZone.substring(3);
            }
            // 中央标准时间（Central Standard Time）
            else if ( v_TimeZone.startsWith("CST") )
            {
                v_TimeZoneBuff.append(v_TimeZone.substring(0 ,3));
                v_TimeZone = v_TimeZone.substring(3);
            }
            // 中欧时间（Central European Time）
            else if ( v_TimeZone.startsWith("CET") )
            {
                v_TimeZoneBuff.append(v_TimeZone.substring(0 ,3));
                v_TimeZone = v_TimeZone.substring(3);
            }
            // 夏令时（Daylight Saving Time）
            else if ( v_TimeZone.startsWith("DST") )
            {
                v_TimeZoneBuff.append(v_TimeZone.substring(0 ,3));
                v_TimeZone = v_TimeZone.substring(3);
            }
            // 东部夏令时时间（Eastern Daylight Time）
            else if ( v_TimeZone.startsWith("EDT") )
            {
                v_TimeZoneBuff.append(v_TimeZone.substring(0 ,3));
                v_TimeZone = v_TimeZone.substring(3);
            }
            // 太平洋夏令时时间（Pacific Daylight Time）
            else if ( v_TimeZone.startsWith("PDT") )
            {
                v_TimeZoneBuff.append(v_TimeZone.substring(0 ,3));
                v_TimeZone = v_TimeZone.substring(3);
            }
            else
            {
                v_TimeZoneBuff.append("GMT");
            }
            
            if ( v_TimeZone.startsWith("+") || v_TimeZone.startsWith("-") )
            {
                v_TimeZoneBuff.append(v_TimeZone.substring(0 ,1));
                v_TimeZone = v_TimeZone.substring(1);
            }
            else
            {
                v_TimeZoneBuff.append("+");
            }
            
            if ( v_TimeZone.length() == 1 )
            {
                v_TimeZoneBuff.append("0");
            }
            
            v_TimeZoneBuff.append(v_TimeZone);
            
            if ( v_TimeZone.length() <= 2 )
            {
                v_TimeZoneBuff.append(":00");
            }
            
            v_TimeZone = v_TimeZoneBuff.toString();
        }
        
        return v_TimeZone;
    }
    
    
    
    /**
     * 用指定时区与当前时区的差值，计算出新的时间（新实例）。this不受影响。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2024-05-30
     * @version     v1.0
     *
     * @param i_TimeZone  时区
     * @return
     */
    public Date getTimezone(String i_TimeZone)
    {
        if ( Help.isNull(i_TimeZone) )
        {
            return null;
        }
        
        String           v_TimeZone         = formatTimeZone(i_TimeZone);
        SimpleDateFormat v_SimpleDateFormat = new SimpleDateFormat($FORMAT_Milli);
        v_SimpleDateFormat.setTimeZone(TimeZone.getTimeZone(v_TimeZone));
        
        String v_TimeStr = v_SimpleDateFormat.format(this);
        return new Date(v_TimeStr ,$FORMAT_Milli);
    }
    
    
    
    /**
     * 设置时间的时区，时间会重新计算。
     * 注：当前时区与指定时间的差值进行换算的
     * 注：this 会被修改
     * 
     * @author      ZhengWei(HY)
     * @createDate  2024-05-30
     * @version     v1.0
     *
     * @param i_TimeZone  时区
     * @return  返回格式化之后时区
     */
    public String setTimeZone(String i_TimeZone)
    {
        if ( Help.isNull(i_TimeZone) )
        {
            return i_TimeZone;
        }
        
        String v_TimeZone = formatTimeZone(i_TimeZone);
        Date   v_New      = getTimezone(v_TimeZone);
        this.setTime(v_New.getTime());
        return v_TimeZone;
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
        return this;
    }
    
    
    
    /**
     * 获取年份
     */
    @Override
    public int getYear()
    {
        Calendar v_Calendar = Calendar.getInstance();
        v_Calendar.setTime(this);
        
        return v_Calendar.get(Calendar.YEAR);
    }
    
    
    
    /**
     * 获取月份
     */
    @Override
    public int getMonth()
    {
        Calendar v_Calendar = Calendar.getInstance();
        v_Calendar.setTime(this);
        
        return v_Calendar.get(Calendar.MONTH) + 1;
    }
    
    
    
    /**
     * 获取是几号
     */
    @Override
    public int getDay()
    {
        Calendar v_Calendar = Calendar.getInstance();
        v_Calendar.setTime(this);
        
        return v_Calendar.get(Calendar.DAY_OF_MONTH);
    }
    
    
    
    /**
     * 获取是几号
     */
    @Override
    public int getDate()
    {
        return this.getDay();
    }
    
    
    
    /**
     * 获取小时
     */
    @Override
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
    @Override
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
    @Override
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
        int v_MoveWeek      = v_WorkDay / 5;            // 移动的周数
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
     * 时间分组（精确到分）。
     * 
     * 如 2013-07-07 12:25:00 按 30 分钟分组，则返回 2013-07-07 12:00:00 ；
     * 如 2013-07-07 12:55:00 按 30 分钟分组，则返回 2013-07-07 12:30:00 ；
     * 
     * 会返回一个新的实例。
     * 
     * @param i_SplitMinuteSize  分钟分组大小
     *                           小于等于0时，按1分组
     *                           有效取值范围在：1~59
     *                           建议取值范围在：1~30
     * @return
     */
    public Date getTimeGroup(int i_SplitMinuteSize)
    {
        int  v_SplitMinuteSize = i_SplitMinuteSize % 60;
        int  v_Minute          = this.getMinutes();
        Date v_NewDate         = this.getFirstTimeOfHour();
        
        if ( v_SplitMinuteSize <= 0 )
        {
            v_SplitMinuteSize = 1;
        }
        
        v_Minute = (int)Math.floor(v_Minute / v_SplitMinuteSize) * v_SplitMinuteSize;
        v_NewDate.setMinutes(v_Minute);
        
        return v_NewDate;
    }
    
    
    
    /**
     * 时间分组（精确到秒）。
     * 
     * 如 2023-07-07 12:10:25 按 30 秒钟分组，则返回 2023-07-07 12:10:00 ；
     * 如 2023-07-07 12:10:55 按 30 秒钟分组，则返回 2023-07-07 12:10:30 ；
     * 
     * 会返回一个新的实例。
     * 
     * @param i_SplitSecondSize  秒钟分组大小
     *                           小于等于0时，按1分组
     *                           有效取值范围在：1~59
     *                           建议取值范围在：1~30
     * @return
     */
    public Date getTimeGroupSecond(int i_SplitSecondSize)
    {
        int  v_SplitSecondSize = i_SplitSecondSize % 60;
        int  v_Second          = this.getSeconds();
        Date v_NewDate         = this.getFirstTimeOfMinute();
        
        if ( v_SplitSecondSize <= 0 )
        {
            v_SplitSecondSize = 1;
        }
        
        v_Second = (int)Math.floor(v_Second / v_SplitSecondSize) * v_SplitSecondSize;
        v_NewDate.setSeconds(v_Second);
        
        return v_NewDate;
    }
    
    
    
    /**
     * 时间分组（精确到小时）。
     * 
     * 如 2023-07-07 12:00:00 按 1  小时分组，则返回 2023-07-07 12:00:00 ；
     * 如 2023-07-07 13:00:00 按 12 小时分组，则返回 2023-07-07 12:00:00 ；
     * 
     * 会返回一个新的实例。
     * 
     * @param i_SplitHourSize  小时分组大小
     *                         小于等于0时，按1分组
     *                         有效取值范围在：1~23
     *                         建议取值范围在：1~12
     * @return
     */
    public Date getTimeGroupHour(int i_SplitHourSize)
    {
        int  v_SplitHourSize = i_SplitHourSize % 24;
        int  v_Hour          = this.getHours();
        Date v_NewDate       = this.getFirstTimeOfDay();
        
        if ( v_SplitHourSize <= 0 )
        {
            v_SplitHourSize = 1;
        }
        
        v_Hour = (int)Math.floor(v_Hour / v_SplitHourSize) * v_SplitHourSize;
        v_NewDate.setHours(v_Hour);
        
        return v_NewDate;
    }
    
    
    
    /**
     * 时间分组（精确到天）。
     * 
     * 如 2023-07-07 00:00:00 按 1 天分组，则返回 2023-07-07 00:00:00 ；
     * 如 2023-07-07 00:00:00 按 2 天分组，则返回 2023-07-06 00:00:00 ；
     * 
     * 会返回一个新的实例。
     * 
     * @param i_SplitDaySize  天分组大小
     *                        小于等于0时，按1分组
     *                        有效取值范围在：1~31
     *                        建议取值范围在：1~15
     * @return
     */
    public Date getTimeGroupDay(int i_SplitDaySize)
    {
        int  v_SplitDaySize = i_SplitDaySize % 32;
        int  v_Day          = this.getDay();
        Date v_NewDate      = this.getFirstDayOfMonth();
        
        if ( v_SplitDaySize <= 0 )
        {
            v_SplitDaySize = 1;
        }
        
        v_Day = (int)Math.floor(v_Day / v_SplitDaySize) * v_SplitDaySize;
        v_NewDate.setDate(v_Day);
        
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
     * 不带时区信息的时间
     * 
     * @author      ZhengWei(HY)
     * @createDate  2024-05-30
     * @version     v1.0
     *
     * @return
     */
    public LocalDateTime getLocalDateTime()
    {
        return getZonedDateTime().toLocalDateTime();
    }
    
    
    
    /**
     * 获取带时区的时间
     * 
     * @author      ZhengWei(HY)
     * @createDate  2024-05-30
     * @version     v1.0
     *
     * @return
     */
    public ZonedDateTime getZonedDateTime()
    {
        Instant v_Instant = this.toInstant(); // 精确到纳秒的时间戳的类
        
        // 使用 Instant 对象和所需的时区信息创建 ZonedDateTime 对象
        ZonedDateTime v_ZonedDateTime = v_Instant.atZone(ZoneId.systemDefault());
        return v_ZonedDateTime;
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
    
    
    
    @Override
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
    
    
    
    @Override
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
    
    
    
    @Override
    public String toString()
    {
        return this.getFull();
    }

}
