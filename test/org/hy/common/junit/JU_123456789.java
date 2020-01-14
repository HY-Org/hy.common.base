package org.hy.common.junit;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;





/**
 * 生成手机图形解锁的密钥
 * 
 *    1    2    3
 *    
 *    4    5    6
 *    
 *    7    8    9
 *
 * @author      ZhengWei(HY)
 * @createDate  2020-01-14
 * @version     v1.0
 */
public class JU_123456789
{
    
    /**
     * 生成手机图形解锁的密钥
     * 
     * @author      ZhengWei(HY)
     * @createDate  2020-01-14
     * @version     v1.0
     *
     */
    @Test
    public void test_123456789()
    {
        int v_Count1 = 0;
        int v_Count2 = 0;
        int v_Count3 = 0;
        for (int i=123456789; i<=198765432; i++)
        {
            String v_LockNo = "" + i;
            if ( v_LockNo.indexOf("0") >= 0 )
            {
                continue;
            }
            
            // 自定义更多的限定条件。如必须包括1
            if ( v_LockNo.indexOf("1") < 0 )
            {
                continue;
            }
            if ( v_LockNo.indexOf("2") < 0 )
            {
                continue;
            }
            if ( v_LockNo.indexOf("3") < 0 )
            {
                continue;
            }
            if ( v_LockNo.indexOf("4") < 0 )
            {
                continue;
            }
            if ( v_LockNo.indexOf("5") < 0 )
            {
                continue;
            }
            if ( v_LockNo.indexOf("6") < 0 )
            {
                continue;
            }
            if ( v_LockNo.indexOf("7") < 0 )
            {
                continue;
            }
            if ( v_LockNo.indexOf("8") < 0 )
            {
                continue;
            }
            if ( v_LockNo.indexOf("9") < 0 )
            {
                continue;
            }
            
            v_Count1++;
            if ( isOK(v_LockNo) )
            {
                v_Count2++;
                if ( v_LockNo.endsWith("2") )
                {
                    if ( v_LockNo.indexOf("27") >= 0 
                      || v_LockNo.indexOf("72") >= 0
                      || v_LockNo.indexOf("29") >= 0
                      || v_LockNo.indexOf("92") >= 0
                      || v_LockNo.indexOf("83") >= 0
                      || v_LockNo.indexOf("38") >= 0
                      || v_LockNo.indexOf("81") >= 0
                      || v_LockNo.indexOf("18") >= 0 
                      || v_LockNo.indexOf("67") >= 0
                      || v_LockNo.indexOf("76") >= 0
                      || v_LockNo.indexOf("61") >= 0
                      || v_LockNo.indexOf("16") >= 0
                      || v_LockNo.indexOf("43") >= 0
                      || v_LockNo.indexOf("34") >= 0
                      || v_LockNo.indexOf("49") >= 0
                      || v_LockNo.indexOf("94") >= 0 )
                    {
                        continue;
                    }
                    
                    v_Count3++;
                    System.out.println(",'" + v_LockNo + "'");
                }
            }
        }
        
        
        System.out.println(v_Count1);
        System.out.println(v_Count2);
        System.out.println(v_Count3);
    }
    
    
    
    /**
     * 是否为合法的图形连接线组合
     * 
     *    1    2    3
     *    
     *    4    5    6
     *    
     *    7    8    9
     * 
     * @author      ZhengWei(HY)
     * @createDate  2020-01-14
     * @version     v1.0
     *
     * @param i_LockNo
     * @return
     */
    public boolean isOK(String i_LockNo)
    {
        Map<String ,Integer> v_NosIndex = new HashMap<String ,Integer>();
        for (int i=0; i<i_LockNo.length(); i++)
        {
            String v_No = i_LockNo.substring(i ,i + 1);
            
            v_NosIndex.put(v_No ,i);
        }
        
        int v_P = -1;
        v_P = i_LockNo.indexOf("13");
        if ( v_P >= 0 && v_NosIndex.get("2") > v_P )
        {
            return false;
        }
        
        v_P = i_LockNo.indexOf("31");
        if ( v_P >= 0 && v_NosIndex.get("2") > v_P )
        {
            return false;
        }
        
        v_P = i_LockNo.indexOf("17");
        if ( v_P >= 0 && v_NosIndex.get("4") > v_P )
        {
            return false;
        }
        
        v_P = i_LockNo.indexOf("71");
        if ( v_P >= 0 && v_NosIndex.get("4") > v_P )
        {
            return false;
        }
        
        v_P = i_LockNo.indexOf("79");
        if ( v_P >= 0 && v_NosIndex.get("8") > v_P )
        {
            return false;
        }
        
        v_P = i_LockNo.indexOf("97");
        if ( v_P >= 0 && v_NosIndex.get("8") > v_P )
        {
            return false;
        }
        
        v_P = i_LockNo.indexOf("39");
        if ( v_P >= 0 && v_NosIndex.get("6") > v_P )
        {
            return false;
        }
        
        v_P = i_LockNo.indexOf("93");
        if ( v_P >= 0 && v_NosIndex.get("6") > v_P )
        {
            return false;
        }
        
        v_P = i_LockNo.indexOf("19");
        if ( v_P >= 0 && v_NosIndex.get("5") > v_P )
        {
            return false;
        }
        
        v_P = i_LockNo.indexOf("91");
        if ( v_P >= 0 && v_NosIndex.get("5") > v_P )
        {
            return false;
        }
        
        v_P = i_LockNo.indexOf("37");
        if ( v_P >= 0 && v_NosIndex.get("5") > v_P )
        {
            return false;
        }
        
        v_P = i_LockNo.indexOf("73");
        if ( v_P >= 0 && v_NosIndex.get("5") > v_P )
        {
            return false;
        }
        
        v_P = i_LockNo.indexOf("28");
        if ( v_P >= 0 && v_NosIndex.get("5") > v_P )
        {
            return false;
        } 
        
        v_P = i_LockNo.indexOf("82");
        if ( v_P >= 0 && v_NosIndex.get("5") > v_P )
        {
            return false;
        } 
        
        v_P = i_LockNo.indexOf("46");
        if ( v_P >= 0 && v_NosIndex.get("5") > v_P )
        {
            return false;
        }
        
        v_P = i_LockNo.indexOf("64");
        if ( v_P >= 0 && v_NosIndex.get("5") > v_P )
        {
            return false;
        }
        
        return true;
    }
    
}
