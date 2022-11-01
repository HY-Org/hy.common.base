package org.hy.common.junit;

import static org.junit.Assert.*;

import org.hy.common.Help;
import org.hy.common.SumList;
import org.hy.common.app.Param;
import org.junit.Test;





/**
 * 测试单元： 对象属性的合并（或拼接）字符串。
 *
 * @author      ZhengWei(HY)
 * @createDate  2018-07-13
 * @version     v1.0
 */
public class JU_SumList
{
    
    @Test
    public void test()
    {
        SumList<Param> v_SumList = new SumList<Param>();
        
        v_SumList.setSplit(";");
        v_SumList.setConnectors(",");
        v_SumList.setKeyMethodURL("name");
        v_SumList.setMethodURLs("value;comment");
        
        for (int i=1; i<=10; i++)
        {
            Param v_Item = new Param("N" + (i % 2) ,"V" + i ,"C" + i);
            
            v_Item.setOnlyRead(false);
            
            v_SumList.add(v_Item);
        }
        
        Help.print(v_SumList);
        
        if ( v_SumList.size() == 2 )
        {
            if ( "V1,V3,V5,V7,V9".equals(v_SumList.get(0).getValue()) )
            {
                if ( "C1,C3,C5,C7,C9".equals(v_SumList.get(0).getComment()) )
                {
                    assertTrue(true);
                    return;
                }
            }
        }
        
        assertTrue(false);
    }
    
}
