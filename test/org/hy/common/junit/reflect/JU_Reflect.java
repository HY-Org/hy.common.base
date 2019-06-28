package org.hy.common.junit.reflect;

import org.junit.Test;





/**
 * 测试单元：反映
 *
 * @author      ZhengWei(HY)
 * @createDate  2019-06-26
 * @version     v1.0
 */
public class JU_Reflect
{
    
    /**
     * 反映Super父类的方法
     * 
     * @author      ZhengWei(HY)
     * @createDate  2019-06-26
     * @version     v1.0
     *
     */
    @Test
    public void test_CallSuperMethod()
    {
        B v_B = new B();
        
        v_B.query("HY");
    }
    
}
