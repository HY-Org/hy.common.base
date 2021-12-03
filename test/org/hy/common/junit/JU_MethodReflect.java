package org.hy.common.junit;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import org.hy.common.MethodReflect;
import org.junit.Test;





/**
 * 测试单元：反射。方法带固定参数的
 *
 * @author      ZhengWei(HY)
 * @createDate  2021-12-03
 * @version     v1.0
 */
public class JU_MethodReflect
{
    
    @Test
    public void test_MethodParams() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException
    {
        DataInfo v_Data = new DataInfo();
        v_Data.setMap(new HashMap<String ,Object>());
        v_Data.getMap().put("key1" ,"成功");
        
        MethodReflect v_MethodRef = new MethodReflect(v_Data ,"map.$get(key1)" ,true ,MethodReflect.$NormType_Getter);
        
        System.out.println(v_MethodRef.invoke());
    }
    
}
