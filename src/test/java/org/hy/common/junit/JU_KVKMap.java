package org.hy.common.junit;

import org.hy.common.Help;
import org.hy.common.KVKLinkMap;
import org.hy.common.KVKMap;
import org.junit.Test;





/**
 * 测试单元：互联互通Map的简单实现
 *
 * @author      ZhengWei(HY)
 * @createDate  2025-02-27
 * @version     v1.0
 */
public class JU_KVKMap
{
    
    @Test
    public void test_KVKMap()
    {
        KVKLinkMap<String ,Integer> v_Map1 = new KVKLinkMap<String ,Integer>();
        KVKMap    <String ,Integer> v_Map2 = new KVKMap    <String ,Integer>();
        
        for (int x=0; x<10; x++)
        {
            v_Map1.put("" + x ,x);   // 可将put的值改成一个固定值测试 
        }
        
        v_Map1.put("A" ,10);
        v_Map1.put("B" ,11);
        v_Map1.put("C" ,12);
        v_Map1.put("D" ,13);
        v_Map1.put("E" ,14);
        v_Map1.put("F" ,15);
        
        v_Map2.putAll(v_Map1);
        
        Help.print(v_Map1);
        System.out.println("");
        
        Help.print(v_Map2);
        System.out.println("");
        
        
        for (int x=0; x<16; x++)
        {
            System.out.println(x + " = " + v_Map1.getReverse(x));
        }
        
        v_Map1.clear();
        v_Map2.clear();
    }
    
}
