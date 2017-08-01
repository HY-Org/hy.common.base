package org.hy.common.junit;

import org.hy.common.Date;
import org.hy.common.Execute;
import org.junit.Test;





/**
 * 测试类：方法重载的测试
 * 
 * @author  ZhengWei(HY)
 * @version 2017-01-05
 */
public class JU_Execute_MethodOverload
{
    
    @Test
    public void test_MethodOverload() throws InterruptedException
    {
        new Execute(new JU_Execute_MethodOverload() ,"methodOverload").start();
        new Execute(new JU_Execute_MethodOverload() ,"methodOverload" ,"1234567890").start();
        new Execute(new JU_Execute_MethodOverload() ,"methodOverload" ,new Date()).start();
        new Execute(new JU_Execute_MethodOverload() ,"methodOverload" ,new Object()).start();
        
        Thread.sleep(1000 * 60 * 10);
    }
    
    
    
    public void methodOverload()
    {
        System.out.println("-- 执行了没有参数的方法。");
    }
    
    
    public void methodOverload(String i_String)
    {
        System.out.println("-- 执行了String参数的方法。");
    }
    
    
    public void methodOverload(Date i_Date)
    {
        System.out.println("-- 执行了Date参数的方法。");
    }
    
    
    public void methodOverload(Object i_Obj)
    {
        System.out.println("-- 执行了Object参数的方法。");
    }
    
}
