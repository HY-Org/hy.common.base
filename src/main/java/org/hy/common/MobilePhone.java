package org.hy.common;

import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;





/**
 * 手机号段的判断 
 * 
 * @author      ZhengWei(HY)
 * @version     v1.0  
 * @createDate  2014-04-04
 */
public final class MobilePhone
{
    /** 
     * 号段信息
     * 
     * $SegmentNumber.key   表示号段信息
     * $SegmentNumber.value 表示运营商类型
     */
    private static InterconnectMap<String ,String> $SegmentNumber;
    
    
    
    /**
     * 初始化中国运营商与号段信息
     */
    public synchronized static void initForChina()
    {
        if ( $SegmentNumber != null ) return ;
        
        
        addSegmentNumber("139" ,"移动");
        addSegmentNumber("138" ,"移动");
        addSegmentNumber("137" ,"移动");
        addSegmentNumber("136" ,"移动");
        addSegmentNumber("135" ,"移动");
        addSegmentNumber("134" ,"移动");
        
        addSegmentNumber("159" ,"移动");
        addSegmentNumber("158" ,"移动");
        addSegmentNumber("157" ,"移动");
        addSegmentNumber("152" ,"移动");
        addSegmentNumber("151" ,"移动");
        addSegmentNumber("150" ,"移动");
        
        addSegmentNumber("188" ,"移动");
        addSegmentNumber("187" ,"移动");
        addSegmentNumber("183" ,"移动");
        addSegmentNumber("182" ,"移动");
        
        addSegmentNumber("147" ,"移动");
        
        
        
        addSegmentNumber("130" ,"联通");
        addSegmentNumber("131" ,"联通");
        addSegmentNumber("132" ,"联通");
        
        addSegmentNumber("155" ,"联通");
        addSegmentNumber("156" ,"联通");
        
        addSegmentNumber("185" ,"联通");
        addSegmentNumber("186" ,"联通");
        
        
        
        addSegmentNumber("133" ,"电信");
        addSegmentNumber("153" ,"电信");
        addSegmentNumber("189" ,"电信");
        addSegmentNumber("180" ,"电信");
    }
    
    
    
    /**
     * 添加号段信息
     * 
     * @param i_SegmentNumber  号段信息
     * @param i_ServiceType    运营商类型
     */
    public synchronized static void addSegmentNumber(String i_SegmentNumber ,String i_ServiceType)
    {
        if ( Help.isNull(i_SegmentNumber) )
        {
            throw new NullPointerException("Segment Number is null.");
        }
        
        if ( $SegmentNumber == null )
        {
            $SegmentNumber = new InterconnectMap<String ,String>();
        }
        
        $SegmentNumber.put(i_SegmentNumber.trim() ,Help.NVL(i_ServiceType).trim());
    }
    
    
    
    /**
     * 是否为手机号
     * 
     * @param i_TelNo   手机号
     * @return
     */
    public static boolean isMobilePhone(String i_TelNo)
    {
        return getServiceType(i_TelNo) != null;
    }
    
    
    
    /**
     * 是否为手机号
     * 
     * @param i_TelNo   手机号
     * @param i_Length  手机号长度的判定标准
     * @return
     */
    public static boolean isMobilePhone(String i_TelNo ,int i_Length)
    {
        if ( getServiceType(i_TelNo) != null )
        {
            return i_TelNo.trim().length() == i_Length;
        }
        else
        {
            return false;
        }
    }
    
    
    
    /**
     * 获取手机号对应的运营商类型。
     * 
     * 前缀判断规则：只判断前5位，最小长度为3位
     * 
     * @param i_TelNo   手机号
     * @return          当返回 null ，就表示 i_TelNo 不是手机号
     */
    public synchronized static String getServiceType(String i_TelNo)
    {
        if ( $SegmentNumber == null )
        {
            throw new NullPointerException("please call addSegmentNumber(...) init info.");
        }
        
        if ( Help.isNull(i_TelNo) )
        {
            throw new NullPointerException("TelNo is null.");
        }
        
        String v_TelNo = i_TelNo.trim();
        if ( v_TelNo.length() < 3 )
        {
            return null;
        }
        
        // 判断是否是数字
        if ( !Pattern.matches("[0-9]*" ,v_TelNo) )
        {
            return null;
        }
        
        String v_Ret = getServiceType(v_TelNo ,3);
        if ( v_Ret != null )
        {
            return v_Ret;
        }
        
        v_Ret = getServiceType(v_TelNo ,4);
        if ( v_Ret != null )
        {
            return v_Ret;
        }
        
        v_Ret = getServiceType(v_TelNo ,5);
        if ( v_Ret != null )
        {
            return v_Ret;
        }
        
        return null;
    }
    
    
    
    /**
     * 获取手机号对应的运营商类型。
     * 
     * @param i_TelNo       手机号
     * @param i_PrefixLen   判断手机号前缀长度
     * @return
     */
    private static String getServiceType(String i_TelNo ,int i_PrefixLen)
    {
        if ( i_TelNo.length() >= i_PrefixLen )
        {
            String v_TelNoPrefix = i_TelNo.substring(0 ,i_PrefixLen);
            if ( $SegmentNumber.containsKey(v_TelNoPrefix) )
            {
                return $SegmentNumber.get(v_TelNoPrefix);
            }
        }
        
        return null;
    }
    
    
    
    /**
     * 获取某一运营商的所有号段
     * 
     * @param i_ServiceType
     * @return
     */
    public synchronized static List<String> getSegmentNumbers(String i_ServiceType)
    {
        if ( $SegmentNumber == null )
        {
            throw new NullPointerException("please call addSegmentNumber(...) init info.");
        }
        
        return $SegmentNumber.getReverse(i_ServiceType);
    }
    
    
    
    /**
     * 获取所有运营商类型
     * 
     * @param i_ServiceType
     * @return
     */
    public synchronized static Iterator<String> getServiceTypes()
    {
        if ( $SegmentNumber == null )
        {
            throw new NullPointerException("please call addSegmentNumber(...) init info.");
        }
        
        return $SegmentNumber.getValues();
    }
    
    
    
    /**
     * 清除缓存
     */
    public synchronized static void clear()
    {
        if ( $SegmentNumber == null )
        {
            return;
        }
        
        $SegmentNumber.clear();
        $SegmentNumber = null;
    }
    
    
    
    private MobilePhone()
    {
        
    }
    
    
    
    public static void main(String [] args)
    {
        MobilePhone.initForChina();
        
        Iterator<String> v_ServiceTypes = MobilePhone.getServiceTypes();
        int              v_Index        = 0;
        while ( v_ServiceTypes.hasNext() )
        {
            System.out.println((++v_Index) + " : " + v_ServiceTypes.next());
        }
        
        System.out.println(MobilePhone.getServiceType("13669224517"));
    }
    
}
