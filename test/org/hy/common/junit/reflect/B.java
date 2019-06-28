package org.hy.common.junit.reflect;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;





public class B extends A
{
    
    public void query(String i_Name)
    {
        System.out.println("Class B call query()：" + i_Name);
        
        try
        {
            MethodHandles.Lookup v_Lookup = MethodHandles.lookup();
            
            Field allowedModes = MethodHandles.Lookup.class.getDeclaredField("allowedModes");
            allowedModes.setAccessible(true);
            allowedModes.set(v_Lookup, -1);   // 关键，没有这三步的操作findSpecial方法内部。实测：没有这三步也是可以的。
            
            // 参数1：表示最终调用的是哪个层级类的某方法，
            // 参数2：则是方法名，
            // 参数3：是返回值类型加参数类型，
            // 参数4：是要调用方法的对象的类型
            MethodHandle v_MHandle = v_Lookup.findSpecial(A.class, "query", MethodType.methodType(void.class ,String.class), B.class);
            
            v_MHandle.invoke(this ,i_Name);
            
            // Method v_Method = A.class.getMethod("query" ,String.class);
            // v_Method.setAccessible(true);
            // v_Method.invoke(A.class.cast(this) ,i_Name);
        }
        catch (Exception exce)
        {
            exce.printStackTrace();
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }
    
}
