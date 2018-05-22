package org.hy.common;





/**
 * 节日信息。
 * 1. 可支持公历节日
 * 2. 可支持特殊公历节日（按某月第几个星期的星期几的节日）
 * 3. 可支持农历节日
 * 
 * @author  ZhengWei(HY)
 * @version 2013-06-14
 */
public class Holiday implements java.io.Serializable
{
    private static final long serialVersionUID = 3813598646833278096L;
    
    

    /** 无效值 */
    private final static int $InvalidValue = -99;

    /** 月月日日mmdd的格式。创建时生成。主要作于快速检索 */
    private String monthDay;
    
    /** 月月星期几mmww的格式。创建时生成。主要作于快速检索 */
    private String monthWeekNo;
    
    /** 月份。下标从1开始。 */
    private int month;

    /** 日期。当为 0 时，启用 weekNum 与 weekNo 两个属性 */
    private int day;

    /** 第几个星期。下标从1开始。负值表示倒数第几个星期 */
    private int weekNum;

    /** 星期几。1:星期一 ...... 6:星期六 7:星期天 */
    private int weekNo;
    
    /** 是一天是否休假 */
    private boolean isRest;
    
    /** 是否为农历 */
    private boolean isLunar;
    
    /** 节日简称 */
    private String shortInfo;
    
    /** 节日信息 */
    private String holidayInfo;
    
    /** 显示级别。数值越大级别越高 */
    private int    showLevel;
    
    
    
    public Holiday(int i_Month ,int i_Day ,boolean i_IsRest ,String i_HolidayInfo)
    {
        this(i_Month ,i_Day ,i_IsRest ,i_HolidayInfo ,"" ,false);
    }
    
    
    
    public Holiday(int i_Month ,int i_Day ,boolean i_IsRest ,String i_HolidayInfo ,String i_ShortInfo)
    {
        this(i_Month ,i_Day ,i_IsRest ,i_HolidayInfo ,i_ShortInfo ,false);
    }
    
    
    
    public Holiday(int i_Month ,int i_Day ,boolean i_IsRect ,String i_HolidayInfo ,String i_ShortInfo ,boolean i_IsLunar)
    {
        if ( i_Month < 1 || i_Month > 12 )
        {
            throw new java.lang.ClassCastException("Month is 1 to 12.");
        }
        
        if ( i_Day < 1 || i_Day > 31 )
        {
            throw new java.lang.ClassCastException("Day is 1 to 31.");
        }
        
        this.month       = i_Month;
        this.day         = i_Day;
        this.isRest      = i_IsRect;
        this.isLunar     = i_IsLunar;
        this.weekNum     = $InvalidValue;
        this.weekNo      = $InvalidValue;
        this.holidayInfo = i_HolidayInfo == null ? "" : i_HolidayInfo.trim();
        this.shortInfo   = i_ShortInfo   == null ? "" : i_ShortInfo.trim();
        this.showLevel   = 0;
        
        this.monthDay    = StringHelp.lpad(this.month ,2 ,"0") + StringHelp.lpad(this.day ,2 ,"0");
        this.monthWeekNo = "";
    }
    
    
    
    public Holiday(int i_Month ,int i_WeekNum ,int i_WeekNo ,boolean i_IsRest ,String i_HolidayInfo)
    {
        this(i_Month ,i_WeekNum ,i_WeekNo ,i_IsRest ,i_HolidayInfo ,"");
    }
    
    
    
    public Holiday(int i_Month ,int i_WeekNum ,int i_WeekNo ,boolean i_IsRest ,String i_HolidayInfo ,String i_ShortInfo)
    {
        if ( i_Month < 1 || i_Month > 12 )
        {
            throw new java.lang.ClassCastException("Month is 1 to 12.");
        }
        
        if ( i_WeekNum < -5 || i_WeekNum > 5 || i_WeekNum == 0 )
        {
            throw new java.lang.ClassCastException("WeekNum is -5 to 5, and not equals 0.");
        }
        
        if ( i_WeekNo < 1 || i_WeekNo > 7 )
        {
            throw new java.lang.ClassCastException("WeekNo is 1 to 7.");
        }
        
        this.month       = i_Month;
        this.day         = $InvalidValue;
        this.isRest      = i_IsRest;
        this.isLunar     = false;
        this.weekNum     = i_WeekNum;
        this.weekNo      = i_WeekNo;
        this.holidayInfo = i_HolidayInfo == null ? "" : i_HolidayInfo.trim();
        this.shortInfo   = i_ShortInfo   == null ? "" : i_ShortInfo.trim();
        this.showLevel   = 0;
        
        this.monthDay    = "";
        this.monthWeekNo = StringHelp.lpad(this.month ,2 ,"0") + StringHelp.lpad(this.weekNo ,2 ,"0");
    }
    
    
    
    /**
     * 判断给定日期是否为节日
     * 
     * @param i_Date   无论判定公历节日，还是农历节日，此入参均为公历日期
     * @return
     */
    public boolean isHoliday(Date i_Date)
    {
        if ( Help.isNull(i_Date) )
        {
            return false;
        }
        
        
        if ( this.isLunar )
        {
            Lunar v_Lunar = i_Date.getLunar();
            
            if ( v_Lunar.getMonth() == this.month && v_Lunar.getDay() == this.day )
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        else if ( this.month == i_Date.getMonth() )
        {
            // 公历节日的判断
            if ( this.day != $InvalidValue )
            {
                return this.day == i_Date.getDay();
            }
            // 特殊公历节日的判断
            else 
            {
               if ( this.weekNo == i_Date.getWeek() )
               {
                   if ( this.weekNum >= 1 && this.weekNum + 1 == i_Date.getWeekNoOfMonth() )
                   {
                       return true;
                   }
                   else
                   {
                       int v_MaxWeekCount = i_Date.getLastDayOfMonth().getWeekNoOfMonth();
                       
                       if ( i_Date.getWeekNoOfMonth() == v_MaxWeekCount + this.weekNum )
                       {
                           return true;
                       }
                       else
                       {
                           return false;
                       }
                   }
               }
               else
               {
                   return false;
               }
            }
        }
        else
        {
            return false;
        }
    }

    

    public int getMonth()
    {
        return month;
    }



    public int getDay()
    {
        return day;
    }


    public int getWeekNum()
    {
        return weekNum;
    }
    
    
    
    public String getMonthWeekNo()
    {
        return monthWeekNo;
    }
    
    
    
    public int getWeekNo()
    {
        return weekNo;
    }

    
    
    public boolean isRest()
    {
        return isRest;
    }
    

    
    public boolean isLunar()
    {
        return isLunar;
    }
    
    
    
    /**
     * 是否为正常公历（即非特殊公历）
     * 
     * @return
     */
    public boolean isNormal()
    {
        return this.day != $InvalidValue;
    }



    public String getShortInfo()
    {
        return shortInfo;
    }
    
    
    
    public String getMonthDay()
    {
        return monthDay;
    }



    public int getShowLevel()
    {
        return this.showLevel;
    }
    
    
    
    public Holiday setShowLevel(int i_ShowLevel)
    {
        this.showLevel = i_ShowLevel;
        
        return this;
    }



    public String toString()
    {
        if ( this.day != $InvalidValue )
        {
            if ( !this.isLunar )
            {
                return this.month + "月的 " + this.day + " 号是:" + this.holidayInfo;
            }
            else
            {
                return "农历的 " + this.month + "月的 " + this.day + " 号是:" + this.holidayInfo;
            }
        }
        else
        {
            if ( this.weekNum >= 1 )
            {
                return this.month + "月的第 " + this.weekNum + "个星期的星期 " + this.weekNo + " 是:" + this.holidayInfo;
            }
            else
            {
                return this.month + "月的倒数第 " + (this.weekNum * -1) + "个星期的星期 " + this.weekNo + " 是:" + this.holidayInfo;
            }
        }
    }
    
}
