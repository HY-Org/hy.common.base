package org.hy.common;

import java.sql.*;
import java.text.*;
import java.util.Calendar;





/**
 * 时期类型的工具类
 * 
 * @author      由 ZhengWei(HY) 整理
 * @version     v1.0  
 * @createDate  2009-08-04
 */
@Deprecated
public final class DateHelp 
{
	public static final SimpleDateFormat SDF_YMD  = new SimpleDateFormat("yyyy-MM-dd");
	
	public static final SimpleDateFormat SDF_FULL = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public static final SimpleDateFormat SDF_TIME = new SimpleDateFormat("HH:mm:ss");
	
	/** 一周的第一天，按星期天为第一天 */
	public static final int         WEEK_FIRST_EN = 0;
	
	/** 一周的第一天，按星期一为第一天 */
	public static final int         WEEK_FIRST_CN = 1;
	
	
	
	/**
	 * 私有构造器
	 */
	private DateHelp() 
	{
		
	}
	
	
	
	/**
	 * 转化时间
	 * 
	 * @param i_uDate
	 * @return
	 */
	public static java.sql.Timestamp toSqlDate(java.util.Date i_uDate) 
	{
		if ( i_uDate == null )
		{
			return null;
		}
		
		return new java.sql.Timestamp(i_uDate.getTime());
	}
	
	
	
	/**
	 * 构造Timestamp时间
	 * 
	 * @param  i_Today   "2009-08-01"
	 * @param  i_Hour    "12"
	 * @param  i_Minute  "50"
	 * @return Timestamp: 2009-08-01 12:50:00
	 */
	public static Timestamp strToTimestamp(String i_Today, String i_Hour, String i_Minute) 
	{
		try 
		{
			if ( i_Today == null || i_Hour == null || i_Minute == null) 
			{
				return null;
			}
			
			java.util.Date d = strToDate(i_Today, i_Hour, i_Minute);
			return new java.sql.Timestamp(d.getTime());
		} 
		catch (Exception e) 
		{
			// e.printStackTrace();
		}
		
		return null;
	}
	
	
	
	/**
	 * 构造Date时间
	 * 
	 * @param  i_Today   "2009-08-01"
	 * @param  i_Hour    "12"
	 * @param  i_Minute  "50"
	 * @return java.util.Date: 2009-08-01 12:50:00
	 */
	public static java.util.Date strToDate(String i_Today, String i_Hour, String i_Minute) 
	{
		try 
		{
			if ( i_Today == null || i_Hour == null || i_Minute == null) 
			{
				return null;
			}
			
			return SDF_FULL.parse(i_Today.trim() + " " + i_Hour.trim() + ":" + i_Minute.trim()+ ":00");
		} 
		catch (Exception e) 
		{
			// e.printStackTrace();
		}
		
		return null;
	}
	
	
	
	/**
	 * 构造Date时间
	 * 
	 * @param  i_Today   "2009-08-01"
	 * @param  i_Hour    "12"
	 * @param  i_Minute  "50"
	 * @return java.util.Date: 2009-08-01 12:50:00
	 */
	public static java.util.Date strToDate(String i_StrDate) 
	{
		try 
		{
			if ( i_StrDate == null || "".equals(i_StrDate.trim()) ) 
			{
				return null;
			}
			
			return SDF_FULL.parse(i_StrDate);
		} 
		catch (Exception e) 
		{
			// e.printStackTrace();
		}
		
		return null;
	}

	
	
	/**
	 * 长整型字符转日期
	 * 
	 * @param i_Log
	 * @return
	 */
	public static String strToDate2(String i_Log) 
	{
		try 
		{
			if ( i_Log == null || "".equals(i_Log.trim()) ) 
			{
				return null;
			}
			
			try 
			{
				@SuppressWarnings("unused")
				Long v_Long = new Long(i_Log);
			} 
			catch (Exception e) 
			{
				return null;
			}
			
			java.util.Date v_Date = new java.util.Date(Long.parseLong(i_Log));
			return SDF_FULL.format(v_Date);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	
	/**
	 * 字符转日期
	 * 
	 * @param i_StrDate
	 * @return
	 */
	public static String strToDate3(String i_StrDate) 
	{
		try 
		{
			if ( i_StrDate == null || "".equals(i_StrDate.trim()) ) 
			{
				return "";
			}
			
			java.util.Date d = SDF_FULL.parse(i_StrDate);
			return SDF_FULL.format(d);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	
	/**
	 * 字符转高精时间
	 * 
	 * @param i_StrDate
	 * @return
	 */
	public static Timestamp strToTimestamp(String i_StrDate) 
	{
		try 
		{
			if ( i_StrDate == null || "".equals(i_StrDate) ) 
			{
				return null;
			}
			
			java.util.Date v_Date = strToDate(i_StrDate);
			return new java.sql.Timestamp(v_Date.getTime());
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	
	/**
	 * 取得时间年份
	 * 
	 * @param i_Date
	 * @return
	 */
	public static String getDateYear(java.util.Date i_Date) 
	{
		try 
		{
			if ( i_Date == null ) 
			{
				return null;
			}
			
			return SDF_YMD.format(i_Date).substring(0, 4);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	
	/**
	 * 取得时间月份
	 * 
	 * @param i_Date
	 * @return
	 */
	public static String getDateMonth(java.util.Date i_Date) 
	{
		try 
		{
			if ( i_Date == null ) 
			{
				return null;
			}
			
			return SDF_YMD.format(i_Date).substring(5, 7);
			
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	

	/**
	 * 取得时间的前一个月份
	 * 
	 * @param i_Date
	 * @return
	 */
	public static String getDatePreviousMonth(java.util.Date i_Date) 
	{
		try 
		{
			if ( i_Date == null ) 
			{
				return null;
			}
			
			String v_Year_Str  = DateHelp.getDateYear(i_Date);
			int    v_Year_Int  = Integer.parseInt(v_Year_Str);
			String v_Month_Str = getDateMonth(i_Date);
			int    v_Month_Int = Integer.parseInt(v_Month_Str);
			
			if ( v_Month_Int > 1 ) 
			{
				v_Month_Int = v_Month_Int - 1;
			} 
			else 
			{
				v_Year_Int = v_Year_Int - 1;
				v_Month_Int = 12;
			}
			
			v_Year_Str = "" + v_Year_Int;
			if ( v_Month_Int < 10 ) 
			{
				v_Month_Str = "-0" + v_Month_Int;
			} 
			else 
			{
				v_Month_Str = "-" + v_Month_Int;
			}
			
			String v_YearMonth = v_Year_Str + v_Month_Str;
			return v_YearMonth;
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	
	/**
	 * 取得时间的后一个月份
	 * 
	 * @param i_Date
	 * @return
	 */
	public static String getDateNextMonth(java.util.Date i_Date) 
	{
		try 
		{
			if ( i_Date == null ) 
			{
				return null;
			}
			
			String v_Year_Str  = DateHelp.getDateYear(i_Date);
			int    v_Year_Int  = Integer.parseInt(v_Year_Str);
			String v_Month_Str = getDateMonth(i_Date);
			int    v_Month_Int = Integer.parseInt(v_Month_Str);
			
			if ( v_Month_Int > 1 && v_Month_Int < 12 ) 
			{
				v_Month_Int = v_Month_Int + 1;
			} 
			else 
			{
				v_Year_Int = v_Year_Int + 1;
				v_Month_Int = 1;
			}
			
			v_Year_Str = "" + v_Year_Int;
			if ( v_Month_Int < 10 ) 
			{
				v_Month_Str = "-0" + v_Month_Int;
			} 
			else 
			{
				v_Month_Str = "-" + v_Month_Int;
			}
			
			String v_YearMonth = v_Year_Str + v_Month_Str;
			return v_YearMonth;
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		return null;
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
	public static int getDateWeek(java.util.Date i_Date)
	{
		return getDateWeek(i_Date ,WEEK_FIRST_CN);
	}
	
	
	
	/**
	 * 获取是周几。
	 * 有选择性的返回是周几。
	 * 
	 * @return
	 */
	public static int getDateWeek(java.util.Date i_Date ,int i_WeekFirstDay)
	{
		int v_Week = -1;
		
		Calendar v_Calendar = Calendar.getInstance();
		v_Calendar.setTime(i_Date);
		
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
	 * 与 getDateMonthDay 同义
	 * 
	 * @param i_Date
	 * @return
	 */
	public static String getDateDay(java.util.Date i_Date)
	{
		return getDateMonthDay(i_Date);
	}
	
	
	
	/**
	 * 取得时间天
	 * 
	 * @param i_Date
	 * @return
	 */
	public static String getDateMonthDay(java.util.Date i_Date) 
	{
		try 
		{
			if ( i_Date == null) 
			{
				return null;
			}
			
			return SDF_YMD.format(i_Date).substring(8, 10);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	
	/**
	 * 取得时间当月的第一天
	 * 
	 * @param i_Date
	 * @return
	 */
	public static String getDateMonthFirst(java.util.Date i_Date)
	{
		if ( i_Date == null )
		{
			return null;
		}
		
		StringBuilder v_Buffer = new StringBuilder();
		
		v_Buffer.append(getDateYear( i_Date));
		v_Buffer.append("-");
		v_Buffer.append(getDateMonth(i_Date));
		v_Buffer.append("-01");
		
		return v_Buffer.toString();
	}
	
	
	
	/**
	 * 取得时间当月的最后一天
	 * 
	 * @param i_Date
	 * @return
	 */
	public static String getDateMonthLast(java.util.Date i_Date)
	{
		if ( i_Date == null )
		{
			return null;
		}
		
		Calendar v_Calendar = Calendar.getInstance();
		v_Calendar.setTime(i_Date);
		int v_Day = v_Calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

		
		StringBuilder v_Buffer = new StringBuilder();
		
		v_Buffer.append(getDateYear( i_Date));
		v_Buffer.append("-");
		v_Buffer.append(getDateMonth(i_Date));
		v_Buffer.append("-");
		v_Buffer.append(v_Day);
		
		return v_Buffer.toString();
	}
	
	
	
	/**
	 * 取得时间的小时
	 * 
	 * @param i_Date
	 * @return
	 */
	public static String getDateHour(java.util.Date i_Date) 
	{
		try 
		{
			if (i_Date == null) 
			{
				return null;
			}
			
			return SDF_FULL.format(i_Date).substring(11, 13);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		return null;
	}

	
	
	/**
	 * 取得时间的分钟
	 * 
	 * @param i_Date
	 * @return
	 */
	public static String getDateMinute(java.util.Date i_Date) 
	{
		try 
		{
			if ( i_Date == null ) 
			{
				return null;
			}
			
			return SDF_FULL.format(i_Date).substring(14, 16);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	

	/**
	 * 两时间相减 i_Date_2 - i_Date_1
	 * 
	 * @param i_Date_1
	 * @param i_Date_2
	 * @return
	 */
	public static int dateToDateDays(java.util.Date i_Date_1, java.util.Date i_Date_2) 
	{
		try 
		{
			if ( i_Date_1 == null || i_Date_2 == null ) 
			{
				return -1;
			}
			if ( i_Date_2.compareTo(i_Date_1) == 1 ) 
			{
				return (int) ((DateHelp.dayEnd(i_Date_2).getTime() - i_Date_1.getTime()) / (24 * 60 * 60 * 1000));
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		return -1;
	}

	
	
	/**
	 * 取得Date的零时零刻
	 * 
	 * @param i_Date
	 * @return
	 */
	public static java.util.Date dayStart(java.util.Date i_Date) 
	{
		try 
		{
			if ( i_Date != null ) 
			{
				String v_Year  = DateHelp.getDateYear(i_Date);
				String v_Month = DateHelp.getDateMonth(i_Date);
				String v_Day   = DateHelp.getDateMonthDay(i_Date);
				
				return SDF_FULL.parse(v_Year + "-" + v_Month + "-" + v_Day + " 00:00:00");
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	
	/**
	 * 取得Date的23:59:59
	 * 
	 * @param i_Date
	 * @return
	 */
	public static java.util.Date dayEnd(java.util.Date i_Date) 
	{
		try 
		{
			if ( i_Date != null ) 
			{
				String v_Year  = DateHelp.getDateYear(i_Date);
				String v_Month = DateHelp.getDateMonth(i_Date);
				String v_Day   = DateHelp.getDateMonthDay(i_Date);

				return SDF_FULL.parse(v_Year + "-" + v_Month + "-" + v_Day + " 23:59:59");
			}
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	
	/**
	 * 判断两时间是否为同一天 true:同一天 false:不同天
	 * 
	 * @param i_Date_1
	 * @param i_Date_2
	 * @return
	 */
	public static boolean dateAndDateIsOneDay(java.util.Date i_Date_1, java.util.Date i_Date_2) 
	{
		try 
		{
			if (i_Date_1 != null && i_Date_2 != null) 
			{
				return DateHelp.dayStart(i_Date_1).getTime() == DateHelp.dayStart(i_Date_2).getTime();
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		return false;
	}
	
	
	
	/**
	 * 取得Date的整时零刻
	 * 
	 * @param i_Date
	 * @return
	 */
	public static java.util.Date hourStart(java.util.Date i_Date) 
	{
		try 
		{
			if ( i_Date != null ) 
			{
				String v_Year  = DateHelp.getDateYear(i_Date);
				String v_Month = DateHelp.getDateMonth(i_Date);
				String v_Day   = DateHelp.getDateMonthDay(i_Date);
				String v_Hour  = DateHelp.getDateHour(i_Date);
				
				return SDF_FULL.parse(v_Year + "-" + v_Month + "-" + v_Day + " " + v_Hour + ":00:00");
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	
	/**
	 * 取得Date的整时零刻
	 * 
	 * @param i_Date
	 * @return
	 */
	public static java.util.Date hourEnd(java.util.Date i_Date) 
	{
		try 
		{
			if ( i_Date != null ) 
			{
				String v_Year  = DateHelp.getDateYear(i_Date);
				String v_Month = DateHelp.getDateMonth(i_Date);
				String v_Day   = DateHelp.getDateMonthDay(i_Date);
				String v_Hour  = DateHelp.getDateHour(i_Date);
				
				return SDF_FULL.parse(v_Year + "-" + v_Month + "-" + v_Day + " " + v_Hour + ":59:59");
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	
	/**
	 * 取的比系统时间早1.5小时的时间 （秒取00，分钟取15整分钟）
	 * 
	 * @return
	 */
	public static String getDayTime_End() 
	{
		String    v_DateStr    = SDF_TIME.format(new java.util.Date(System.currentTimeMillis()- 5400000));
		String [] v_DateStrArr = v_DateStr.split("\\:");
		
		int    v_Min_Int = (Integer.parseInt(v_DateStrArr[1]) / 15) * 15;
		String v_Min_Str = v_Min_Int < 10 ? v_Min_Int + "0" : String.valueOf(v_Min_Int);
		
		return v_DateStrArr[0] + ":" + v_Min_Str + ":00";
	}
	
	

	/**
	 * 时间加1天
	 * 
	 * @param i_Date
	 * @return
	 */
	public static java.util.Date addOneDate(java.util.Date i_Date) 
	{
		try 
		{
			if (i_Date != null)
			{
				return new java.util.Date(i_Date.getTime() + (24 * 60 * 60 * 1000));
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	

	/**
	 * 取得系统时间
	 * 
	 * @return
	 */
	public static String getNowTime() 
	{
		java.util.Date v_Date = new java.util.Date(System.currentTimeMillis());
		return SDF_FULL.format(v_Date);
	}
	
	
	/**
	 * 转换时间为字符
	 * 
	 * @param i_Date
	 * @return
	 * @throws Exception 
	 */
	public static String dateToString(java.util.Date i_Date)
	{
		String v_Date = SDF_FULL.format(i_Date);
		return v_Date;
	}
	
	
	/**
	 * 转换时间为字符
	 * 
	 * @param i_Date
	 * @return
	 * @throws Exception 
	 */
	public static String dateToHHMISS(java.util.Date i_Date)
	{
		String v_Date = SDF_TIME.format(i_Date);
		return v_Date;
	}
	
	
	/**
	 * 按指定格式转换字符串为时间
	 * 
	 * @param i_StrDate
	 * @param i_Format
	 * @return
	 * @throws ParseException 
	 */
	public static java.util.Date strToDate(String i_StrDate ,String i_Format) throws ParseException
	{
		SimpleDateFormat SDF_My = new SimpleDateFormat(i_Format);
		
		try 
		{
			return SDF_My.parse(i_StrDate);
		} 
		catch (ParseException e) 
		{
			throw e;
		}
	}
	
	
	
	/**
	 * 按指定格式转换字符串为时间
	 * 
	 * @param i_StrDate
	 * @param i_SDF
	 * @return
	 * @throws ParseException 
	 */
	public static java.util.Date strToDate(String i_StrDate ,SimpleDateFormat i_SDF) throws ParseException
	{
		try
		{
			return i_SDF.parse(i_StrDate);
		}
		catch (ParseException e) 
		{
			throw e;
		}
	}
	
	
	
	/**
	 * 将 java.util.Date 转换为 java.sql.Date
	 * 
	 * @param i_JavaDate
	 * @return
	 */
	public static java.sql.Date javaDateToSQLDate(java.util.Date i_JavaDate)
	{
		java.sql.Date v_SQLDate = new java.sql.Date(i_JavaDate.getTime());
		
		return v_SQLDate;
	}

}
