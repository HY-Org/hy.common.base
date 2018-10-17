package org.hy.common.junit;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.hy.common.Help;
import org.hy.common.RelationList;
import org.hy.common.Return;
import org.hy.common.SplitSegment;
import org.hy.common.StringHelp;





/**
 * 测试类：字符类型的工具类
 * 
 * @author      ZhengWei(HY)
 * @version     v1.0  
 * @createDate  2014-08-12
 */
public class JU_StringHelp
{
    
    @Test
    public void test_toNumberSimplify()
    {
        System.out.println(StringHelp.toNumberSimplify("1234567890.1" ,6 ,2));
        System.out.println(StringHelp.toNumberSimplify("1.2345678"    ,6 ,2));
        System.out.println(StringHelp.toNumberSimplify("1234.5678"    ,6 ,2));
        System.out.println(StringHelp.toNumberSimplify("0.000001"     ,6 ,2));
    }
    
    
    
    /**
     * 随机生成指定长度的数字与字母混合的字符串
     * 
     * @author      ZhengWei(HY)
     * @createDate  2018-05-23
     * @version     v1.0
     *
     */
    @Test
    public void test_random()
    {
        System.out.println("数字+字母混合：" + StringHelp.random(36));
        
        System.out.println("纯字母随机："    + StringHelp.random(36 ,false));
    }
    
    
    
    /**
     * 针对VB传过来小数点前没有0的数字处理，如：将 "-.123" 转换成: "-0.123"
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-12-23
     * @version     v1.0
     *
     */
    @Test
    public void test_toNumberString()
    {
        System.out.println(StringHelp.toNumberString("5.68e+10"));
        System.out.println(StringHelp.toNumberString("-.123"));
    }
    
    
    
    @Test
    public void test_getCharEncoding()
    {
        String v_Text = "我爱";
        System.out.println(StringHelp.getCharEncodings(v_Text));
        
        String v_GBK          = StringHelp.toCharEncoding(v_Text ,"UTF-8" ,"GBK");
        String v_ISO_8859_1   = StringHelp.toCharEncoding(v_Text ,"UTF-8" ,"ISO-8859-1");
        
        System.out.println(StringHelp.getCharEncodings(v_GBK));
        System.out.println(StringHelp.getCharEncodings(v_ISO_8859_1));
    }
    
    
    
    @Test
    public void test_toABC26()
    {
        for (int i=0; i<=100; i++)
        {
            System.out.println(i + " = " + StringHelp.toABC26(i) + " \t反向 = " + StringHelp.reABC26(StringHelp.toABC26(i)));
        }
    }
    
    
    
    @Test
    public void test_toABC36()
    {
        for (int i=0; i<=100; i++)
        {
            System.out.println(i + " = " + StringHelp.toABC36(i) + " \t反向 = " + StringHelp.reABC36(StringHelp.toABC36(i)));
        }
    }
    
    
    
    @Test
    public void test_md5()
    {
        System.out.println(StringHelp.md5("xsso"));
        System.out.println(StringHelp.md5("xsso" ,StringHelp.$MD5_Type_Hex));
        
        System.out.println(StringHelp.md5("getData=;getDataType=1D;getDeviceNo=867246023785125;getDeviceType=android;" + "@20170801"));
        System.out.println("yGSajQF/npSzjy179WP01Q__");
    }
    
    
    
    @Test
    public void test_Split()
    {
        List<String> v_Infos = new ArrayList<String>();
        
        //v_Infos.add("X == Y || A == B");
        //v_Infos.add("(X == Y || A == B) && (1 == 2 || 3 == 4)");
        //v_Infos.add("X == Y && (1 == 2 || 3 == 4)");
        //v_Infos.add("(1 == 2 || 3 == 4) && X == Y");
        //v_Infos.add("1 == 2");
        v_Infos.add("getLog(Oracle)");
        v_Infos.add("getLog(Oracle) || getLog(MySQL)");
        //v_Infos.add("(ture && (getLog(Oracle) || getLog(MySQL))) && (1 == 1 || 2 == 3) && (4 == 5 || 6 == 7) && false");
        //v_Infos.add("(X == Y || A == B) && (ture && (getLog(Oracle) || getLog(MySQL))) && (1 == 1 || 2 == 3) && (4 == 5 || 6 == 7) && false");
        
        for (String v_Info : v_Infos)
        {
            parserRelation(v_Info);
            
            System.out.println("\n");
        }
    }
    
    
    
    private void parserRelation(String i_Info)
    {
        Return<List<SplitSegment>> v_Return_1 = StringHelp.Split("\\w+\\([^(?!((\\()(\\))))]+\\)" ,i_Info ,false);
        if ( Help.isNull(v_Return_1.paramStr) )
        {
            System.out.println("原始信息为：[" + i_Info + "]");
            System.out.println("分割方法后：[" + v_Return_1.paramStr + "]");
            System.out.println("没有可以再分的信息");
            
            RelationList<String> v_Relations = RelationList.parser(v_Return_1);
            System.out.println("\n" + v_Relations);
            
            return;
        }
        
        // 分割 (...) 的形式，但不含 ( 和 )
        Return<List<SplitSegment>> v_Return_2 = StringHelp.Split("\\([^(?!((\\()(\\))))]*\\)"     ,v_Return_1 ,true);
        
        System.out.println();
        System.out.println("原始信息为：[" + i_Info + "]");
        System.out.println("分割方法后：[" + v_Return_1.paramStr + "]");
        System.out.println("分割括号后：[" + v_Return_2.paramStr + "]");
        System.out.println("分割层次后：[" + v_Return_2.paramInt + "]");
        System.out.println("重新组合为：[" + StringHelp.SplitGroup(v_Return_2) + "]\n");
        
        
        StringHelp.SplitLog(v_Return_2);
        
        
        RelationList<String> v_Relations = RelationList.parser(v_Return_2);
        System.out.println("\n" + v_Relations);
    }
    
    
    
    @SuppressWarnings("unused")
    @Test
    public void test_ABC36()
    {
        int v_Value = 999999;
        
        System.out.println(Help.round(Help.division(125L ,3L) ,2));
        
        //System.out.println(StringHelp.toABC26(v_Value));
        //System.out.println(StringHelp.reABC26(StringHelp.toABC26(v_Value)));
        
//        for (int i=0; i<36; i++)
//        {
//            System.out.println(StringHelp.toABC36(i));
//            System.out.println(StringHelp.reABC36(StringHelp.toABC36(i)));
//        }
        
        //System.out.println(StringHelp.reABC36(StringHelp.toABC36(v_Value)));
    }
    
    
    
    /**
     * 测试中文占位符的解析
     */
    @Test
    public void test_parsePlaceholdersSequence()
    {
        System.out.println(StringHelp.parsePlaceholdersSequence("INSERT INTO AAA (':中文')"));
    }
    
}
