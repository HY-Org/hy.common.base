package org.hy.common.junit;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.hy.common.Date;
import org.hy.common.Help;
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
    
    private Map<String ,JU_MethodReflectUser> users;
    
    
    
    public JU_MethodReflect()
    {
        this.users = new HashMap<String ,JU_MethodReflectUser>();
        this.users.put("A" ,new JU_MethodReflectUser("张三" ,18 ,"B"));
        this.users.put("B" ,new JU_MethodReflectUser("李四" ,19 ,"C"));
        this.users.put("C" ,new JU_MethodReflectUser("王五" ,20 ,"A"));
    }
    
    
    
    
    @Test
    public void test_MethodParams() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException
    {
        DataInfo v_Data = new DataInfo();
        v_Data.setMap(new HashMap<String ,Object>());
        v_Data.getMap().put("key1" ,"成功");
        
        MethodReflect v_MethodRef = new MethodReflect(v_Data ,"map.$get(key1)" ,true ,MethodReflect.$NormType_Getter);
        
        System.out.println(v_MethodRef.invoke());
    }
    
    
    
    @Test
    public void test_AllowNew()
    {
        System.out.println(MethodReflect.allowNew(Help.class));
        System.out.println(MethodReflect.allowNew(Date.class));
    }
    
    
    
    @Test
    public void test_getMapValue()
    {
        Map<String ,Object> v_Datas = new HashMap<String ,Object>();
        v_Datas.put("SchoolA"  ,new JU_MethodReflect());
        v_Datas.put("FactoryA" ,new JU_MethodReflect());
        
        System.out.println(MethodReflect.getMapValue(new JU_MethodReflect().getUsers() ,"A.name"));
        
        System.out.println(MethodReflect.getMapValue(v_Datas ,"SchoolA.users.A.name"));
        System.out.println(MethodReflect.getMapValue(v_Datas ,"SchoolA.users.$get(A).name"));
        
        System.out.println(MethodReflect.getMapValue(v_Datas ,"FactoryA.users.A.ref"));
        System.out.println(MethodReflect.getMapValue(v_Datas ,"FactoryA.users.$get(A).ref"));
    }
    
    
    
    public Map<String ,JU_MethodReflectUser> getUsers()
    {
        return users;
    }
    
    
    
    public void setUsers(Map<String ,JU_MethodReflectUser> i_Users)
    {
        this.users = i_Users;
    }
    
}
