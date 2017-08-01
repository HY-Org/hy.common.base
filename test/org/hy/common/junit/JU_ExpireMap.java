package org.hy.common.junit;

import java.util.Iterator;

import org.hy.common.Date;
import org.hy.common.ExpireMap;
import org.hy.common.ExpireMap.Expire;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;





/**
 * 测试单元：Map.key有生存时间的Map，当生存时间期满时，Map.key就失效了。
 *
 * @author      ZhengWei(HY)
 * @createDate  2016-02-25
 * @version     v1.0
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING) 
public class JU_ExpireMap
{
    
    @Test
    public void test_001() throws InterruptedException
    {
        ExpireMap<String ,Date> v_ExpireMap = new ExpireMap<String ,Date>();
        
        long                 v_MilliSecond = 2;
        Date                 v_Now         = new Date();
        Date                 v_Time        = new Date(v_Now.getTime() + v_MilliSecond);
        String               v_Key         = v_Time.getFullMilli();
        Expire<String ,Date> v_Expire      = v_ExpireMap.put(v_Key ,v_Time ,v_MilliSecond);
        String               v_ETS         = (new Date(v_Expire.getTime())).getFullMilli();
        
        
        System.out.println("-- 当前时间：" + v_Now.getFullMilli());
        while ( true )
        {
            v_Time = v_ExpireMap.get(v_Key);
            
            if ( null != v_Time )
            {
                System.out.println("-- 当前时间：" + Date.getNowTime().getFullMilli() + "\t剩余时长：" + v_ExpireMap.getExpireTimeLen(v_Key));
            }
            else
            {
                System.out.println("-- 当前时间：" + Date.getNowTime().getFullMilli() + "\t过期时间：" + v_ETS);
                break;
            }
        }
        
        Iterator<String> v_Iterator = v_ExpireMap.keySet().iterator();
        while ( v_Iterator.hasNext() )
        {
            String v_ID = v_Iterator.next();
            
            System.out.println(v_ID);
        }
    }
    
}
