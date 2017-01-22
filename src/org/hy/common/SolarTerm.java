package org.hy.common;

import java.util.Calendar;





/**
 * 24节气
 * 
 * @author  ZhengWei(HY)
 * @version 2013-06-21
 */
public final class SolarTerm
{
    private final static long []   $STermInfo = new long[] {0 ,21208 ,42467 ,63836 ,85337 ,107014 ,128867 ,150921 ,173149 ,195551 ,218072 ,240693 ,263343 ,285989 ,308563 ,331033 ,353350 ,375494 ,397447 ,419210 ,440795 ,462224 ,483532 ,504758};

    private final static String [] $SolarTerm = new String[] {"小寒" ,"大寒" ,"立春" ,"雨水" ,"惊蛰" ,"春分" ,"清明" ,"谷雨" ,"立夏" ,"小满" ,"芒种" ,"夏至" ,"小暑" ,"大暑" ,"立秋" ,"处暑" ,"白露" ,"秋分" ,"寒露" ,"霜降" ,"立冬" ,"小雪" ,"大雪" ,"冬至"};
    
    /** 使用懒加载的方式 */
    private       static long      $BaseDate  = 0;
    
    
    
    private SolarTerm()
    {
    }
    
    
    
    /** 
     * 核心方法 根据日期得到节气
     */
    public static String getSoralTerm(java.util.Date i_Date)
    {
        Date v_Date = new Date(i_Date);
        
        return getSoralTerm(v_Date.getYear() ,v_Date.getMonth() ,v_Date.getDay());
    }
    
    
    
    /** 
     * 核心方法 根据日期得到节气
     */
    public static String getSoralTerm(Date i_Date)
    {
        return getSoralTerm(i_Date.getYear() ,i_Date.getMonth() ,i_Date.getDay());
    }
    
    
    
    /** 
     * 核心方法 根据日期(y年m月d日)得到节气 
     */
    public static String getSoralTerm(int y ,int m ,int d)
    {
        String v_SolarTerms = "";
        
        if ( d == sTerm(y ,(m - 1) * 2) )
        {
            v_SolarTerms = $SolarTerm[(m - 1) * 2];
        }
        else if ( d == sTerm(y ,(m - 1) * 2 + 1) )
        {
            v_SolarTerms = $SolarTerm[(m - 1) * 2 + 1];
        }
        
        return v_SolarTerms;
    }
    
    
    
    /**
     * 获取基准时间
     * 
     * @return
     */
    private static long getBaseDate()
    {
        if ( $BaseDate == 0 )
        {
            Calendar cal = Calendar.getInstance();
            cal.set(1900 ,0 ,6 ,2 ,5 ,0);
            
            $BaseDate = cal.getTime().getTime();
        }
        
        return $BaseDate;
    }



    /**
     * y年的第n个节气为几日(从0小寒起算)
     */
    private static int sTerm(int y ,int n)
    {
        Date v_Date = new Date((long) ((31556925974.7 * (y - 1900) + $STermInfo[n] * 60000L) + getBaseDate()));
        
        return v_Date.getDay();
    }
    
}
