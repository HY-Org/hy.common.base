package org.hy.common.junit;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hy.common.Counter;
import org.hy.common.Date;
import org.hy.common.Help;
import org.hy.common.MethodReflect;
import org.hy.common.StringHelp;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;





@FixMethodOrder(MethodSorters.NAME_ASCENDING) 
public class JU_Help
{
    
    @Test
    public void test_executeCommand()
    {
        Help.print(Help.executeCommand("ls -aln /"));
        // Help.print(Help.executeCommand("ls" ,"-aln" ,"/"));
        // Help.print(Help.executeCommand("GBK" ,false ,"cmd.exe /c d: && cd D:\\apache-tomcat-7.0.47\\bin && D:\\apache-tomcat-7.0.47\\bin\\shutdown.bat"));
        // Help.print(Help.executeCommand("GBK" ,false ,"cmd.exe" ,"/c" ,"dir" ,"c:\\"));
    }
    
    
    
    @Test
    public void test_executeCommand_otherExe()
    {
        Help.print(Help.executeCommand(false ,false ,"/Volumes/HY_HD_06/Develop_HD/apache-tomcat-7.0.47/bin/startup.sh"));
        System.exit(0);
    }
    
    
    
    @SuppressWarnings({"unchecked" ,"null"})
    @Test
    public void test_toMap_BigData() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        Integer vMin = null;
        System.out.println(Math.min(0 ,vMin));
        System.out.println("--  Init: " + Date.getNowTime().getFullMilli());
        
        
        List<JU_XJSON> v_Datas = new ArrayList<JU_XJSON>();
        
        for (int i=1; i<=100000; i++)
        {
            JU_XJSON v_Data = new JU_XJSON();
            
            v_Data.setBODY(new JU_XJSON_BODYType());
            v_Data.setSID("" + i);
            v_Data.getBODY().setStaffId("" + i);
            
            v_Datas.add(v_Data);
        }
        
        
        System.out.println("-- Start: " + Date.getNowTime().getFullMilli());
     
        
        List<Map<String ,Object>> v_CollectionMap = new ArrayList<Map<String ,Object>>();
        
        for (Object v_Item : v_Datas)
        {
            Map<String ,Object> v_ItemMap = new HashMap<String ,Object>();
            
            if ( MethodReflect.isExtendImplement(v_Item ,Map.class) )
            {
                v_ItemMap.putAll((Map<String ,Object>)v_Item);
            }
            else
            {
                v_ItemMap.putAll(Help.toMap(v_Item));
            }
            
            v_CollectionMap.add(v_ItemMap);
        }
        
        System.out.println("--   End: " + Date.getNowTime().getFullMilli());
    }
    
    
    
    @Test
    public void test_toLike()
    {
        String []      v_Arrs  = new String[100];
        List<String>   v_Strs  = new ArrayList<String>();
        List<JU_XJSON> v_Datas = new ArrayList<JU_XJSON>();
        
        for (int i=1; i<=100; i++)
        {
            JU_XJSON v_Data = new JU_XJSON();
            
            v_Data.setBODY(new JU_XJSON_BODYType());
            v_Data.setSID("" + i);
            v_Data.getBODY().setStaffId("" + i);
            
            v_Datas.add(v_Data);
            v_Strs .add("" + i);
            v_Arrs[i - 1] = "" + i;
        }
        
        System.out.println("List对象xx.yy.zz测试");
        Help.print(Help.toLike(v_Datas ,"body.staffid" ,true ,"1"));
        
        System.out.println("List对象直属属性测试");
        Help.print(Help.toLike(v_Datas ,"sid" ,true ,"1" ,"2"));
        
        System.out.println("List简单类型集合测试");
        Help.print(Help.toLike(v_Strs ,true ,"0" ,"2"));
        
        System.out.println("数组简单类型测试");
        Help.print(Help.toLike(v_Arrs ,true ,"3" ,"1"));
    }
    
    
    
    @Test
    public void test_round()
    {
        System.out.println(Help.round(0.00005123 ,4));
    }
    
    
    
    @Test
    public void test_getMacs()
    {
        System.out.println(Help.getMacs());
    }
    
    
    
    @Test
    public void test_findSames()
    {
        List<String> v_Datas = new ArrayList<String>();
        List<String> v_Sames = null;
        
        for (int i=0; i<10; i++)
        {
            v_Datas.add("" + i);
        }
        
        // 生成重复的数据
        for (int i=0; i<3; i++)
        {
            v_Datas.add("" + i);
        }
        
        // 再生成第二次重复的数据
        for (int i=0; i<3; i++)
        {
            v_Datas.add("" + i);
        }
        
        v_Sames = Help.findSames(v_Datas);
        
        System.out.println("\n-- 查找重复的元素");
        Help.print(v_Sames);
        
        Assert.assertTrue(v_Sames.size() == 3);
    }
    
    
    
    @Test
    public void test_subtract()
    {
        double v_Ret01 = Help.subtract("60.3" ,"60.01");
        double v_Ret02 = Help.subtract(60.3   ,60.01);
        
        System.out.println("减法的差值不等于0.29：" + (60.3 - 60.01));
        
        Assert.assertTrue("减法的差值是否相等" ,v_Ret01 == 0.29);
        Assert.assertTrue("减法的差值是否相等" ,v_Ret02 == 0.29);
    }
    
    

    @Test
    public void test_division()
    {
        double v_Ret01 = Help.division("1" ,"3");
        double v_Ret02 = Help.division(1   ,3);
        
        System.out.println("除法的差值不等于0.333333333：" + (1.0 / 3.0));
        
        Assert.assertTrue("减法的差值是否相等" ,v_Ret01 == 0.333333333);
        Assert.assertTrue("减法的差值是否相等" ,v_Ret02 == 0.333333333);
    }
    
    
    
    @Test
    public void test_toMap() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        System.out.println("-- 排序返回");
        Help.print(Help.toMap(new JU_XJSON()));
        
        System.out.println("\n-- 随机返回");
        Help.print(Help.toMap(new JU_XJSON() ,null ,true ,false));
        
        System.out.println("2015/09/29".replaceAll("/" ,"-"));
    }
    
    
    
    @Test
    public void test_toSortByNum()
    {
        System.out.println("\n\n正序排序。不改变数据格式");
        Help.print(Help.toSortByNum("3.1" ,"3.0" ,"3.010"));
        
        System.out.println("\n\n倒序排序。不改变数据格式");
        Help.print(Help.toReverseByNum("3.1" ,"3.0" ,"3.010"));
    }
    
    
    
    @Test
    public void test_toSort()
    {
        List<JU_XJSON> v_Datas = new ArrayList<JU_XJSON>();
        
        v_Datas.add(new JU_XJSON("00" ,"1"   ,1));
        v_Datas.add(new JU_XJSON("01" ,"10"  ,2));
        v_Datas.add(new JU_XJSON("02" ,"11"  ,3));
        v_Datas.add(new JU_XJSON("00" ,"111" ,4));
        v_Datas.add(new JU_XJSON("03" ,"2"   ,5));
        
        System.out.println("\n\n按SID倒序排序");
        Help.toSort(v_Datas ,"SID Desc" ,"BODY.hyDate Desc");
        Help.print(v_Datas);
        
        System.out.println("\n\n按SID正序排序");
        Help.toSort(v_Datas ,"SID");
        Help.print(v_Datas);
        
        System.out.println("\n\n按SID倒序、SIGN倒排序");
        Help.toSort(v_Datas ,"SID Desc" ,"SIGN Desc");
        Help.print(v_Datas);
        
        System.out.println("\n\n按SID倒序、SIGN正排序");
        Help.toSort(v_Datas ,"SID Desc" ,"SIGN");
        Help.print(v_Datas);
        
        System.out.println("\n\n按SID倒序、时间倒排序");
        Help.toSort(v_Datas ,"SID Desc" ,"REQTIME Desc");
        Help.print(v_Datas);
        
        System.out.println("\n\n按doubleValue倒序排序");
        Help.toSort(v_Datas ,"doubleValue Desc");
        Help.print(v_Datas);
        
        System.out.println("\n\n按SIGN数字倒排序");
        Help.toSort(v_Datas ,"SIGN NumDesc");
        Help.print(v_Datas);
        
        System.out.println("\n\n按SIGN数字正排序");
        Help.toSort(v_Datas ,"SIGN NumAsc");
        Help.print(v_Datas);
        
        System.out.println("\n\n按SIGN正排序");
        Help.toSort(v_Datas ,"SIGN Asc");
        Help.print(v_Datas);
        
        System.out.println("\n\n按SIGN倒排序。并有错误排序属性名的情况下");
        Help.toSort(v_Datas ,"2015 Desc" ,"SIGN Desc" ,"2016 Asc");
        Help.print(v_Datas);
    }
    
    
    
    @Test
    public void test_toSortByMap()
    {
        Counter<String> v_Counter = new Counter<String>();
        String []       v_ABC     = {"A" ,"B" ,"C" ,"D" ,"E" ,"F" ,"G" ,"H" ,"I" ,"J" ,"K" ,"M" ,"N"};
        
        for (int i=0; i<10; i++)
        {
            v_Counter.put(v_ABC[i] ,Help.random(10));
        }
        
        System.out.println("-- 正排序Map.value");
        Help.print(Help.toSortByMap(   v_Counter));
        
        System.out.println("\n-- 倒排序Map.value");
        Help.print(Help.toReverseByMap(v_Counter));
    }
    
    
    
    public void test_toList()
    {
        List<JU_XJSON> v_Datas = new ArrayList<JU_XJSON>();
        
        v_Datas.add(new JU_XJSON("00" ,"1"   ,1));
        v_Datas.add(new JU_XJSON("01" ,"10"  ,2));
        v_Datas.add(new JU_XJSON("02" ,"11"  ,3));
        
        System.out.println("\n\n按SID列抽取");
        Help.print(Help.toList(v_Datas ,"SID"));
        
        System.out.println("\n\n按doubleValue列抽取");
        Help.print(Help.toList(v_Datas ,"doubleValue"));
    }
    
    
    
    public void test_replaceAll()
    {
        System.out.println("A'B".replaceAll("'" ,"''"));
        System.out.println(StringHelp.replaceAll("AAA.BBB.CC"   ,"AAA" + "."  ,"HY"));
        
        
        String v_Info = "\"electricConn\": \"NPT1/2\\\"\"";
        //System.out.println(v_Info + " = " + StringHelp.replaceAll(v_Info ,"\\\"" ,"\""));
        System.out.println(v_Info + " = " + StringHelp.replaceAll(StringHelp.replaceAll(v_Info ,"\\\"" ,"\"") ,"\"" ,"\\\""));
        
        System.out.println(StringHelp.replaceAll("AAA"   ,"A"  ,"B"));
        System.out.println(StringHelp.replaceAll("Q\"Q"  ,"\"" ,""));
        System.out.println(StringHelp.replaceAll("Q美元Q" ,"美元" ,"$"));
        System.out.println(StringHelp.replaceAll("Q美元Q" ,"美元" ,""));
        System.out.println(StringHelp.replaceAll("Q美元Q" ,"" ,"$"));
        System.out.println(StringHelp.replaceAll("ABC" ,new String[]{"A" ,"B" ,"C"} ,new String[]{" "}));
        System.out.println(StringHelp.replaceAll("package com.fms.template.dao.impl;" ,new String[]{"template" ,"Template"} ,new String[]{"person" ,"Person"}));
    }
    
    
    
    public void test_toDistinct()
    {
        List<JU_XJSON> v_Datas = new ArrayList<JU_XJSON>();
        
        v_Datas.add(new JU_XJSON("00" ,"1"   ,1));
        v_Datas.add(new JU_XJSON("01" ,"10"  ,2));
        v_Datas.add(new JU_XJSON("02" ,"11"  ,3));
        v_Datas.add(new JU_XJSON("00" ,"111" ,4));
        v_Datas.add(new JU_XJSON("03" ,"2"   ,5));
        
        System.out.println("\n\n重除显示");
        Help.toDistinct(v_Datas ,"SID");
        Help.print(v_Datas);
    }
    
    
    
    /**
     * 测试数据排序
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-02-20
     * @version     v1.0
     *
     */
    public void test_SortArray()
    {
        System.out.println("-- 测试元素个数为偶数的倒序排序");
        Help.print(Help.toReverse(new int[]{1 ,2 ,3 ,4}));
        
        System.out.println("-- 测试元素个数为奇数的倒序排序");
        Help.print(Help.toReverse(new int[]{1 ,2 ,3}));
    }
    
    
    
    @Test
    public void test_getClasses()
    {
        List<Class<?>> v_Ret = Help.getClasses("org.hy.common.一个不存在包名");
        
        Help.print(v_Ret);
    }
    
    
    
    @Test
    public void test_getUUIDLong()
    {
        System.out.println(StringHelp.getUUIDNum());
    }
    
}
