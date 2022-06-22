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
        
        long                 v_Second = 2L;
        Date                 v_Now    = new Date();
        Date                 v_Time   = new Date(v_Now.getTime() + v_Second * 1000L);
        String               v_Key    = v_Time.getFullMilli();
        Expire<String ,Date> v_Expire = v_ExpireMap.put(v_Key ,v_Time ,v_Second);
        String               v_ETS    = (new Date(v_Expire.getTime())).getFullMilli();
        
        
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
    
    
    
    @Test
    public void test_002() throws InterruptedException
    {
        ExpireMap<String ,Date> v_ExpireMapOld = new ExpireMap<String ,Date>();
        ExpireMap<String ,Date> v_ExpireMapNew = new ExpireMap<String ,Date>();
        
        long   v_Second = 2L;
        Date   v_Now    = new Date();
        Date   v_Time   = new Date(v_Now.getTime() + v_Second * 1000L);
        String v_Key    = v_Time.getFullMilli();
        
        v_ExpireMapOld.put(v_Key ,v_Time ,v_Second);
        System.out.println("-- 原集合能否得到值：" + v_ExpireMapOld.get(v_Key));
        
        // 等待过期
        Thread.sleep((v_Second + 2) * 1000L);
        
        v_ExpireMapNew.putAll(v_ExpireMapOld);

        System.out.println("-- 过期后，复制到新集合中");
        System.out.println("-- 原集合能否得到值：" + v_ExpireMapOld.get(v_Key));
        System.out.println("-- 新集合能否得到值：" + v_ExpireMapNew.get(v_Key));
    }
    
}
