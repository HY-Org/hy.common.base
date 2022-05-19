package org.hy.common;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hy.common.SplitSegment.InfoType;





/**
 * 字符类型的工具类
 * 
 * @author      ZhengWei(HY)
 * @version     v1.0  2009-08-21
 *              v1.1  2014-08-18   1.Split分割的系统方法
 *                                 2.xor字符串的异或运算
 *                                 3.解释关系
 *              v1.2  2017-05-17   1.修复toABC26()方法的26生成AA的算法。
 *              v1.3  2017-09-18   1.添加 isContains()   多关键字包含判定
 *                                 2.添加 isStartsWith() 多关键字前缀匹配判定
 *                                 3.添加 isEndsWith()   多关键字后缀匹配判定
 *              v1.4  2017-11-16   1.修复unescape_toUnicode()方法，不应简单的将分号;替换成空字符。而是判定%u999;的格式下替换。
 *              v1.5  2017-12-23   1.添加 toNumberString() 方法，将各种数字表达方式转为正规的数字表达方式
 *                                 1.添加 trim() 方法，去掉空格、回车、换行字符串
 *              v1.6  2018-03-22   1.添加 isContains() 支持有先、后顺序匹配查找关键字的功能。
 *                                 1.添加 比正则表达式性能更高的统计getCount(...)。
 *              v1.7  2018-04-13   1.添加 replaceFirst(...) 系列方法
 *              v1.8  2018-05-04   1.添加 isEquals()、isEqualsIgnoreCase() 比较多个关键字，判定是否只少有一个相等。
 *              v1.9  2018-05-16   1.添加 支持中文占位符。建议人：邹德福
 *              v1.10 2018-05-23   1.添加 trim(String ,String)去掉字符串前后两端的指定的子字符。
 *              v1.11 2018-10-17   1.添加 toNumberSimplify()简化显示数字，显示允许的长度。
 *                                 2.添加 toScientificNotation()特殊的科学计数显示数字。
 *              v1.12 2018-11-15   1.添加 encode() decode()两种转义方法。可对指定字符串排除转义的功能。
 *              v1.13 2018-12-21   1.添加 trimToDistinct()去掉某些连续重复出现的字符。如“A...B..C”，去掉重复连续的.点，就变成：“A.B.C”
 *              v1.14 2018-12-27   1.添加 replaceLast(...) 系列方法
 *              v1.15 2019-03-13   1.添加 parsePlaceholdersSequence() 占位符命名是否要求严格的规则
 *              v1.16 2019-08-27   1.添加 扩展 getComputeUnit() 方法，带小数精度。
 *              v1.17 2020-06-08   1.修改 解释占位符的系列方法parsePlaceholders()的返回结构改成PartitionMap
 *              v1.18 2021-09-27   1.添加 编程语言的基本数据类型的转字符串。可以配合 Help.toObject() 等方法使用，实现字符串形式的序列化和反序列化
 *              v1.19 2022-05-19   1.添加 货币转字符串转数字的方法 toNumber()
 * 
 * @createDate  2009-08-21
 */
public final class StringHelp
{
    /** MD5加密(V2版)的加密类型：全数字形式 */
    public  static final int       $MD5_Type_Num  = 1;
    
    /** MD5加密(V2版)的加密类型：数字字母混合的形式，但无特殊字符 */
    public  static final int       $MD5_Type_Hex  = 2;
    
    /** 字符集编码类型 */
    public  static       String [] $CharEncodings = {"UTF-8" ,"ISO-8859-1" ,"ASCII" ,"UNICODE" ,"GBK" ,"GB2312" ,"GB18030"};
    
    
    
    private static final String    $ABC           = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    
    private static final String    $ABC36         = "0123456789" + $ABC;
    
    private static final char      last2byte      = (char) Integer.parseInt("00000011", 2);
    private static final char      last4byte      = (char) Integer.parseInt("00001111", 2);
    private static final char      last6byte      = (char) Integer.parseInt("00111111", 2);
    private static final char      lead6byte      = (char) Integer.parseInt("11111100", 2);
    private static final char      lead4byte      = (char) Integer.parseInt("11110000", 2);
    private static final char      lead2byte      = (char) Integer.parseInt("11000000", 2);
    private static final char []   encodeTable    = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '!', '/'};
    private static final char []   encodeTable_V2 = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    
    /** 匹配XML标记或Html标记的正则表达式 */
    private static final String    $XMLSign       = "(< *XMLSignName(( *)|( [ \\S\\s]*))>[.\\S\\s]*<\\/ *XMLSignName *>)|(< *XMLSignName(( *)|( [ \\S\\s]*))\\/>)";
    
    /** 保存XML特殊字符转换信息 */
    private static InterconnectMap<String, String> $XML_EnCodes = null;
    
    /** 匹配Email地址的正则表达式 */
    private static final String    $EMail         = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
    
    /** 解释方法的正则表达式。分割 xxx(...) 的形式 */
    private static final String    $Method        = "\\w+\\([^(?!((\\()(\\))))]+\\)";
    
    /** 解释圆括号的正则表达式。分割(...) 的形式，但不含 ( 和 ) */
    private static final String    $Parentheses   = "\\([^(?!((\\()(\\))))]*\\)";
    
    /** 将字符串类型的数字类型。支持千分位、货币符号 */
    private static final String [] $Currency      = new String[] {"," ,"，" ,"¥" ,"$" ,"€"};
    
    /** 常用的替换常量，如将关键字替换为空字符 */
    public  static final String [] $ReplaceNil    = new String[] {""};
    
    
    
    /**
     * 懒加载方式，初始XML特殊符号转换信息
     */
    private synchronized static void initXML_EnCodes()
    {
        if ( Help.isNull($XML_EnCodes) )
        {
            $XML_EnCodes = new InterconnectMap<String ,String>();
            
            $XML_EnCodes.put("<"  ,"&lt;");
            $XML_EnCodes.put(">"  ,"&gt;");
            $XML_EnCodes.put("\"" ,"&quot;");
            $XML_EnCodes.put("'"  ,"&apos;");
        }
    }
    
    
    
    /**
     * 私有构造器
     */
    private StringHelp()
    {
    }
    
    
    
    /**
     * 随机生成指定长度的数字与字母混合的字符串
     * 
     * @author      ZhengWei(HY)
     * @createDate  2018-05-23
     * @version     v1.0
     *
     * @param i_Length      随机生成的字符串长度
     * @return
     */
    public final static String random(int i_Length)
    {
        return random(i_Length ,true);
    }
    
    
    
    /**
     * 随机生成指定长度的数字与字母混合的字符串
     * 
     * @author      ZhengWei(HY)
     * @createDate  2018-05-23
     * @version     v1.0
     *
     * @param i_Length      随机生成的字符串长度
     * @param i_HaveNumber  随机生成的字符串中是否内含数字
     * @return
     */
    public final static String random(int i_Length ,boolean i_HaveNumber)
    {
        StringBuilder v_Buffer = new StringBuilder();
        
        if ( i_HaveNumber )
        {
            for (int i=1; i<=i_Length; i++)
            {
                v_Buffer.append($ABC36.charAt(Help.random(0 ,35)));
            }
        }
        else
        {
            for (int i=1; i<=i_Length; i++)
            {
                v_Buffer.append($ABC.charAt(Help.random(0 ,25)));
            }
        }
        
        return v_Buffer.toString();
    }
    
    
    
    /**
     * 首字母大写
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-02-20
     * @version     v1.0
     *
     * @param i_Str
     * @return
     */
    public static String toUpperCaseByFirst(String i_Str)
    {
        return i_Str.substring(0 ,1).toUpperCase() + i_Str.substring(1);
    }
    
    
    
    /**
     * 首字母小写
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-02-20
     * @version     v1.0
     *
     * @param i_Str
     * @return
     */
    public static String toLowerCaseByFirst(String i_Str)
    {
        return i_Str.substring(0 ,1).toLowerCase() + i_Str.substring(1);
    }
    
    
    
    /**
     * 将字符串中的多个连续出现的空白字符删除，只保留一个。
     * 如 "a   b           c d" 会被处理为 "a b c d"
     * 
     * @param i_Str
     * @return
     */
    public static String removeSpaces(String i_Str)
    {
        if ( Help.isNull(i_Str) )
        {
            return i_Str;
        }
        
        return i_Str.replaceAll("\\s+", " ");
    }
    
    
    
    /**
     * 获取UUID。全部大写，并且去除"-"字符的东东
     * 
     * @return
     */
    public final static String getUUID()
    {
        return UUID.randomUUID().toString().replace("-" ,"").toUpperCase();
    }
    
    
    
    public final static String getUUIDNum()
    {
        String []     v_UUIDArr = UUID.randomUUID().toString().split("-");
        StringBuilder v_Buffer  = new StringBuilder();
        
        for (String v_UUID : v_UUIDArr)
        {
            long v_Value = Long.parseLong(v_UUID ,16);
            
            v_Buffer.append(v_Value);
        }
        
        return v_Buffer.toString();
    }
    
    
    
    /**
     * 字符空，则取默认值
     * 
     * @param i_Str
     * @param i_DefaultValue
     * @return
     */
    public final static String nvl(String i_Str ,String i_DefaultValue)
    {
        if ( i_Str == null || "".equals(i_Str.trim()) )
        {
            return i_DefaultValue;
        }
        else
        {
            return i_Str.trim();
        }
            
    }
    

    /**
     * 把多个连续的分隔符split分解成单个分隔符，并把前后的分隔符去掉
     * 
     * @param cols  ",,,aa,bbb,cc,ee,,,sdf,,,"
     * @param split  ",,"
     * @param toSplit ","
     * @return
     */
    public final static String strSplit(String source, String split)
    {
        if( source == null || split == null)
        {
            return null;
        }
        
        try
        {
            while (source.indexOf(split + split) > -1)
            {
                // 去掉连续的分隔符
                source = source.substring(0, source.indexOf(split + split))
                       + source.substring(source.indexOf(split + split) + 1);
            }
            
            if ( source.substring(0, 1).equals(split) )
            {
                // 去掉第一个分隔符
                source = source.substring(1);
            }
            
            if ( source.substring(source.length() - 1).equals(split) )
            {
                //去掉最后一个分隔符
                source = source.substring(0, source.length() - 1);
            }
        }
        catch(Exception e)
        {
            return "";
        }
        
        return source;
    }

    
    
    /**
     * 把多个连续的分隔符split分解成单个分隔符，并把前后的分隔符去掉
     * 
     * @param cols  ",,,aa,bbb,cc,ee,,,sdf,,,"
     * @param split  ",,"
     * @param toSplit ","
     * @return
     */
    public final static String strSplit2(String source,String split)
    {
        if(source == null || split == null)
        {
            return null;
        }
        
        try
        {
            while (source.indexOf(split + split) > -1)
            {
                // 去掉连续的分隔符
                source = source.substring(0, source.indexOf(split + split)) +
                source.substring(source.indexOf(split + split) + 1);
            }
        }
        catch(Exception e)
        {
            return "";
        }
        
        return source;
    }
    
    
    
    /**
     *  把以split分隔的字符串分组放入list中，source经过处理
     * 
     * @param source
     * @param split
     * @return
     */
    public final static List<String> strToList(String source,String split)
    {
        if(source == null || split == null)
        {
            return null;
        }
        
        List<String> list = new ArrayList<String>();
        source = strSplit(source,split);// 整理字符串

        if(source.indexOf(split) == -1)
        {
            list.add(source.trim()); // 单个
            return list;
        }
        
        while(source.indexOf(split) > -1)
        {
            int L1 = source.indexOf(split);
            String s1 = source.substring(0,L1);
            if(!"".equals(s1))
            {
                //字符不包括空格
                list.add(s1);
            }
            
            source = source.substring(L1 + 1);
            if(source.indexOf(split) == -1)
            {
                list.add(source); // 加入最后一个
            }
        }
        
        return list;
    }
    
    
    
    /**
     *  把以split分隔的字符串分组放入list中，source没有经过处理
     *  ,,  之间加入空格放入list 中 把连续的之间加入空格
     * @param source
     * @param split
     * @return
     */
    public final static List<String> strToList2(String source,String split)
    {
        if(source == null || split == null)
        {
            return null;
        }
        
        List<String> list = new ArrayList<String>();
        if(source.indexOf(split) == -1)
        {
            list.add(source.trim()); // 单个
            return list;
        }
        
        while(source.indexOf(split) > -1)
        {
            int L1 = source.indexOf(split);
            String s1 = source.substring(0,L1);
            if(!"".equals(s1))
            {
                //字符不包括空格
                list.add(s1);
            }
            else
            {
                list.add(" ");
            }
            
            source = source.substring(L1 + 1);
            if(source.indexOf(split) == -1)
            {
                if (!"".equals(source.trim()))
                {
                    //字符不包括空格
                    list.add(source.trim()); // 加入最后一个
                }
                else
                {
                    list.add(" ");
                }
    
            }
        }
        
        return list;
    }
    
    
    
    /**
     * 批量替换字符串
     * 
     * Java自带的String.replaceAll()方法，是通过正则表达式做的替换动作，
     * 对于一些特殊字符，要做十分复杂的处理，并不好用。
     * 
     * 比如说："Q美元Q".replaceAll("美元" ,"$"); 方法会出错。
     * 
     * 测试环境为：Mac:OSX EI Capitan , JDK:1.7.0_45-b18
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-02-20
     * @version     v1.0
     *
     * @param i_Info
     * @param i_Replaces  Map.key 替换字符串。Map.value 被替换字符串。
     *                    当 Map.value 为 NULL 时，自动用空字符串""替换。这也是与replaceAll(String ,[] ,[])不同的地点之一。
     *                    内部对i_Replaces有降序排序顺序的动作，并形成有有顺序的LinkedMap。用于解决 :A、:AA 同时存在时的混乱
     * @return
     */
    public final static String replaceAll(final String i_Info ,final Map<String ,?> i_Replaces)
    {
        return StringHelp.replaceAll(i_Info ,i_Replaces ,true);
    }
    
    
    
    /**
     * 批量替换字符串
     * 
     * Java自带的String.replaceAll()方法，是通过正则表达式做的替换动作，
     * 对于一些特殊字符，要做十分复杂的处理，并不好用。
     * 
     * 比如说："Q美元Q".replaceAll("美元" ,"$"); 方法会出错。
     * 
     * 测试环境为：Mac:OSX EI Capitan , JDK:1.7.0_45-b18
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-02-20
     * @version     v1.0
     *              v2.0  2018-02-24  修复：当替换为一个空格" "时，因Help.NVL()的再次处理，为替换为空字符""了。
     *
     * @param i_Info
     * @param i_Replaces  Map.key 替换字符串。Map.value 被替换字符串。
     *                    当 Map.value 为 NULL 时，自动用空字符串""替换。这也是与replaceAll(String ,[] ,[])不同的地点之一。
     *                    内部对i_Replaces有降序排序顺序的动作，并形成有有顺序的LinkedMap。用于解决 :A、:AA 同时存在时的混乱
     * @param i_OrderBy   是否降序排序顺序
     * @return
     */
    public final static String replaceAll(final String i_Info ,final Map<String ,?> i_Replaces ,boolean i_OrderBy)
    {
        if ( Help.isNull(i_Replaces) )
        {
            return i_Info;
        }
        
        Map<String ,?> v_RRS = null;
        if ( i_OrderBy )
        {
            v_RRS = Help.toReverse(i_Replaces);
        }
        else
        {
            v_RRS = i_Replaces;
        }

        String v_Ret = i_Info;
        for (Map.Entry<String, ?> v_RR : v_RRS.entrySet())
        {
            if ( v_RR.getValue() == null )
            {
                v_Ret = StringHelp.replaceAll(v_Ret ,v_RR.getKey() ,"");
            }
            else
            {
                v_Ret = StringHelp.replaceAll(v_Ret ,v_RR.getKey() ,v_RR.getValue().toString());
            }
        }
        
        return v_Ret;
    }
	
	
	
	/**
     * 批量替换字符串
     * 
     * Java自带的String.replaceAll()方法，是通过正则表达式做的替换动作，
     * 对于一些特殊字符，要做十分复杂的处理，并不好用。
     * 
     * 比如说："Q美元Q".replaceAll("美元" ,"$"); 方法会出错。
     * 
     * 测试环境为：Mac:OSX EI Capitan , JDK:1.7.0_45-b18
     * 
     * @author      ZhengWei(HY)
     * @createDate  2015-12-17
     * @version     v1.0
     *
     * @param i_Info
     * @param i_Replaces    有替换先后顺序的替换字符串
     * @param i_ReplaceBys  有替换先后顺序的被替换字符串
     *                      当i_ReplaceBys.length小于i_Replaces.length时，i_Replaces大于i_ReplaceBys的部分，
     *                      全部用i_ReplaceBys的最后一个元素替换，
     *                      如，replaceAll("ABC" ,new String[]{"A" ,"B" ,"C"} ,new String[]{" "}) 将返回三个空格。
     * @return
     */
    public final static String replaceAll(final String i_Info ,final String [] i_Replaces ,final String [] i_ReplaceBys)
    {
        if ( Help.isNull(i_Replaces) || Help.isNull(i_ReplaceBys) )
        {
            return i_Info;
        }
        
        String v_Ret   = i_Info;
        int    v_Size  = Math.min(i_Replaces.length ,i_ReplaceBys.length);
        int    v_Index = 0;
        
        for (v_Index=0; v_Index<v_Size; v_Index++)
        {
            v_Ret = StringHelp.replaceAll(v_Ret ,i_Replaces[v_Index] ,i_ReplaceBys[v_Index]);
        }
        
        if ( i_Replaces.length > i_ReplaceBys.length )
        {
            for (; v_Index<i_Replaces.length; v_Index++)
            {
                v_Ret = StringHelp.replaceAll(v_Ret ,i_Replaces[v_Index] ,i_ReplaceBys[i_ReplaceBys.length - 1]);
            }
        }
        
        return v_Ret;
    }
    
    
    
    /**
     * 替换字符串
     * 
     * Java自带的String.replaceAll()方法，是通过正则表达式做的替换动作，
     * 对于一些特殊字符，要做十分复杂的处理，并不好用。
     * 
     * 比如说："Q美元Q".replaceAll("美元" ,"$"); 方法会出错。
     * 
     * 测试环境为：Mac:OSX EI Capitan , JDK:1.7.0_45-b18
     * 
     * @author      ZhengWei(HY)
     * @createDate  2015-12-17
     * @version     v1.0
     *              v2.0  修正：当 i_ReplaceBy = ""，并且在第0个位置只查找到一个要替换的东东时，会出现无法替换的问题。
     *
     * @param i_Info
     * @param i_Replace
     * @param i_ReplaceBy
     * @return
     */
    public final static String replaceAll(final String i_Info ,final String i_Replace ,final String i_ReplaceBy)
    {
        if ( null == i_Info
          || null == i_Replace
          || null == i_ReplaceBy
          || 0    >= i_Info.length()
          || 0    >= i_Replace.length() )
        {
            return i_Info;
        }
        
        StringBuilder v_Buffer    = new StringBuilder();
        int           v_RLen      = i_Replace.length();
        int           v_FromIndex = i_Info.indexOf(i_Replace);
        int           v_LastIndex = 0 - v_RLen;
        boolean       v_IsReplace = false;
        
        while ( v_FromIndex >= 0 )
        {
            v_LastIndex += v_RLen;
            v_Buffer.append(i_Info.substring(v_LastIndex ,v_FromIndex)).append(i_ReplaceBy);
            v_LastIndex = v_FromIndex;
            v_FromIndex = i_Info.indexOf(i_Replace ,v_FromIndex + v_RLen);
            v_IsReplace = true;
        }
        
        if ( !v_IsReplace )
        {
            return i_Info;
        }
        else
        {
            v_Buffer.append(i_Info.substring(v_LastIndex + v_RLen));
        }
        
        return v_Buffer.toString();
    }
    
    
    
    /**
     * 批量替换字符串（每个关键字只替换一次）
     * 
     * Java自带的String.replace()方法，是通过正则表达式做的替换动作，
     * 对于一些特殊字符，要做十分复杂的处理，并不好用。
     * 
     * 比如说："Q美元Q".replaceFirst("美元" ,"$"); 方法会出错。
     * 
     * 测试环境为：Mac:OSX EI Capitan , JDK:1.7.0_45-b18
     * 
     * @author      ZhengWei(HY)
     * @createDate  2018-04-13
     * @version     v1.0
     *
     * @param i_Info
     * @param i_Replaces  Map.key 替换字符串。Map.value 被替换字符串。
     *                    当 Map.value 为 NULL 时，自动用空字符串""替换。这也是与replaceAll(String ,[] ,[])不同的地点之一。
     *                    内部对i_Replaces有降序排序顺序的动作，并形成有有顺序的LinkedMap。用于解决 :A、:AA 同时存在时的混乱
     * @return
     */
    public final static String replaceFirst(final String i_Info ,final Map<String ,?> i_Replaces)
    {
        return StringHelp.replaceFirst(i_Info ,i_Replaces ,true);
    }
    
    
    
    /**
     * 批量替换字符串（每个关键字只替换一次）
     * 
     * Java自带的String.replace()方法，是通过正则表达式做的替换动作，
     * 对于一些特殊字符，要做十分复杂的处理，并不好用。
     * 
     * 比如说："Q美元Q".replaceFirst("美元" ,"$"); 方法会出错。
     * 
     * 测试环境为：Mac:OSX EI Capitan , JDK:1.7.0_45-b18
     * 
     * @author      ZhengWei(HY)
     * @createDate  2018-04-13
     * @version     v1.0
     *
     * @param i_Info
     * @param i_Replaces  Map.key 替换字符串。Map.value 被替换字符串。
     *                    当 Map.value 为 NULL 时，自动用空字符串""替换。这也是与replaceAll(String ,[] ,[])不同的地点之一。
     *                    内部对i_Replaces有降序排序顺序的动作，并形成有有顺序的LinkedMap。用于解决 :A、:AA 同时存在时的混乱
     * @param i_OrderBy   是否降序排序顺序
     * @return
     */
    public final static String replaceFirst(final String i_Info ,final Map<String ,?> i_Replaces ,boolean i_OrderBy)
    {
        if ( Help.isNull(i_Replaces) )
        {
            return i_Info;
        }
        
        Map<String ,?> v_RRS = null;
        if ( i_OrderBy )
        {
            v_RRS = Help.toReverse(i_Replaces);
        }
        else
        {
            v_RRS = i_Replaces;
        }

        String v_Ret = i_Info;
        for (Map.Entry<String, ?> v_RR : v_RRS.entrySet())
        {
            if ( v_RR.getValue() == null )
            {
                v_Ret = StringHelp.replaceFirst(v_Ret ,v_RR.getKey() ,"");
            }
            else
            {
                v_Ret = StringHelp.replaceFirst(v_Ret ,v_RR.getKey() ,v_RR.getValue().toString());
            }
        }
        
        return v_Ret;
    }
	
	
	
	/**
     * 批量替换字符串（每个关键字只替换一次）
     * 
     * Java自带的String.replace()方法，是通过正则表达式做的替换动作，
     * 对于一些特殊字符，要做十分复杂的处理，并不好用。
     * 
     * 比如说："Q美元Q".replaceFirst("美元" ,"$"); 方法会出错。
     * 
     * 测试环境为：Mac:OSX EI Capitan , JDK:1.7.0_45-b18
     * 
     * @author      ZhengWei(HY)
     * @createDate  2018-04-13
     * @version     v1.0
     *
     * @param i_Info
     * @param i_Replaces    有替换先后顺序的替换字符串
     * @param i_ReplaceBys  有替换先后顺序的被替换字符串
     *                      当i_ReplaceBys.length小于i_Replaces.length时，i_Replaces大于i_ReplaceBys的部分，
     *                      全部用i_ReplaceBys的最后一个元素替换，
     *                      如，replaceFirst("ABC" ,new String[]{"A" ,"B" ,"C"} ,new String[]{" "}) 将返回三个空格。
     * @return
     */
    public final static String replaceFirst(final String i_Info ,final String [] i_Replaces ,final String [] i_ReplaceBys)
    {
        if ( Help.isNull(i_Replaces) || Help.isNull(i_ReplaceBys) )
        {
            return i_Info;
        }
        
        String v_Ret   = i_Info;
        int    v_Size  = Math.min(i_Replaces.length ,i_ReplaceBys.length);
        int    v_Index = 0;
        
        for (v_Index=0; v_Index<v_Size; v_Index++)
        {
            v_Ret = StringHelp.replaceFirst(v_Ret ,i_Replaces[v_Index] ,i_ReplaceBys[v_Index]);
        }
        
        if ( i_Replaces.length > i_ReplaceBys.length )
        {
            for (; v_Index<i_Replaces.length; v_Index++)
            {
                v_Ret = StringHelp.replaceFirst(v_Ret ,i_Replaces[v_Index] ,i_ReplaceBys[i_ReplaceBys.length - 1]);
            }
        }
        
        return v_Ret;
    }
	
	
	
	/**
     * 替换字符串（每个关键字只替换一次）
     * 
     * Java自带的String.replace()方法，是通过正则表达式做的替换动作，
     * 对于一些特殊字符，要做十分复杂的处理，并不好用。
     * 
     * 比如说："Q美元Q".replaceFirst("美元" ,"$"); 方法会出错。
     * 
     * 测试环境为：Mac:OSX EI Capitan , JDK:1.7.0_45-b18
     * 
     * @author      ZhengWei(HY)
     * @createDate  2018-04-13
     * @version     v1.0
     *
     * @param i_Info
     * @param i_Replace
     * @param i_ReplaceBy
     * @return
     */
    public final static String replaceFirst(final String i_Info ,final String i_Replace ,final String i_ReplaceBy)
    {
        if ( null == i_Info
          || null == i_Replace
          || null == i_ReplaceBy
          || 0    >= i_Info.length()
          || 0    >= i_Replace.length() )
        {
            return i_Info;
        }
        
        StringBuilder v_Buffer    = new StringBuilder();
        int           v_RLen      = i_Replace.length();
        int           v_FromIndex = i_Info.indexOf(i_Replace);
        
        if ( v_FromIndex >= 0 )
        {
            v_Buffer.append(i_Info.substring(0 ,v_FromIndex));
            v_Buffer.append(i_ReplaceBy);
            v_Buffer.append(i_Info.substring(v_FromIndex + v_RLen));
        }
        else
        {
            return i_Info;
        }
        
        return v_Buffer.toString();
    }
    
    
    
    /**
     * 批量替换字符串（每个关键字只替换一次，从末尾替换起）
     * 
     * Java自带的String.replace()方法，是通过正则表达式做的替换动作，
     * 对于一些特殊字符，要做十分复杂的处理，并不好用。
     * 
     * 比如说："Q美元Q".replaceLast("美元" ,"$"); 方法会出错。
     * 
     * 测试环境为：Mac:OSX EI Capitan , JDK:1.7.0_45-b18
     * 
     * @author      ZhengWei(HY)
     * @createDate  2018-12-27
     * @version     v1.0
     *
     * @param i_Info
     * @param i_Replaces  Map.key 替换字符串。Map.value 被替换字符串。
     *                    当 Map.value 为 NULL 时，自动用空字符串""替换。这也是与replaceAll(String ,[] ,[])不同的地点之一。
     *                    内部对i_Replaces有降序排序顺序的动作，并形成有有顺序的LinkedMap。用于解决 :A、:AA 同时存在时的混乱
     * @return
     */
    public final static String replaceLast(final String i_Info ,final Map<String ,?> i_Replaces)
    {
        return StringHelp.replaceLast(i_Info ,i_Replaces ,true);
    }
    
    
    
    /**
     * 批量替换字符串（每个关键字只替换一次，从末尾替换起）
     * 
     * Java自带的String.replace()方法，是通过正则表达式做的替换动作，
     * 对于一些特殊字符，要做十分复杂的处理，并不好用。
     * 
     * 比如说："Q美元Q".replaceLast("美元" ,"$"); 方法会出错。
     * 
     * 测试环境为：Mac:OSX EI Capitan , JDK:1.7.0_45-b18
     * 
     * @author      ZhengWei(HY)
     * @createDate  2018-12-27
     * @version     v1.0
     *
     * @param i_Info
     * @param i_Replaces  Map.key 替换字符串。Map.value 被替换字符串。
     *                    当 Map.value 为 NULL 时，自动用空字符串""替换。这也是与replaceAll(String ,[] ,[])不同的地点之一。
     *                    内部对i_Replaces有降序排序顺序的动作，并形成有有顺序的LinkedMap。用于解决 :A、:AA 同时存在时的混乱
     * @param i_OrderBy   是否降序排序顺序
     * @return
     */
    public final static String replaceLast(final String i_Info ,final Map<String ,?> i_Replaces ,boolean i_OrderBy)
    {
        if ( Help.isNull(i_Replaces) )
        {
            return i_Info;
        }
        
        Map<String ,?> v_RRS = null;
        if ( i_OrderBy )
        {
            v_RRS = Help.toReverse(i_Replaces);
        }
        else
        {
            v_RRS = i_Replaces;
        }

        String v_Ret = i_Info;
        for (Map.Entry<String, ?> v_RR : v_RRS.entrySet())
        {
            if ( v_RR.getValue() == null )
            {
                v_Ret = StringHelp.replaceLast(v_Ret ,v_RR.getKey() ,"");
            }
            else
            {
                v_Ret = StringHelp.replaceLast(v_Ret ,v_RR.getKey() ,v_RR.getValue().toString());
            }
        }
        
        return v_Ret;
    }
    
    
    
    /**
     * 批量替换字符串（每个关键字只替换一次，从末尾替换起）
     * 
     * Java自带的String.replace()方法，是通过正则表达式做的替换动作，
     * 对于一些特殊字符，要做十分复杂的处理，并不好用。
     * 
     * 比如说："Q美元Q".replaceLast("美元" ,"$"); 方法会出错。
     * 
     * 测试环境为：Mac:OSX EI Capitan , JDK:1.7.0_45-b18
     * 
     * @author      ZhengWei(HY)
     * @createDate  2018-12-27
     * @version     v1.0
     *
     * @param i_Info
     * @param i_Replaces    有替换先后顺序的替换字符串
     * @param i_ReplaceBys  有替换先后顺序的被替换字符串
     *                      当i_ReplaceBys.length小于i_Replaces.length时，i_Replaces大于i_ReplaceBys的部分，
     *                      全部用i_ReplaceBys的最后一个元素替换，
     *                      如，replaceFirst("ABC" ,new String[]{"A" ,"B" ,"C"} ,new String[]{" "}) 将返回三个空格。
     * @return
     */
    public final static String replaceLast(final String i_Info ,final String [] i_Replaces ,final String [] i_ReplaceBys)
    {
        if ( Help.isNull(i_Replaces) || Help.isNull(i_ReplaceBys) )
        {
            return i_Info;
        }
        
        String v_Ret   = i_Info;
        int    v_Size  = Math.min(i_Replaces.length ,i_ReplaceBys.length);
        int    v_Index = 0;
        
        for (v_Index=0; v_Index<v_Size; v_Index++)
        {
            v_Ret = StringHelp.replaceLast(v_Ret ,i_Replaces[v_Index] ,i_ReplaceBys[v_Index]);
        }
        
        if ( i_Replaces.length > i_ReplaceBys.length )
        {
            for (; v_Index<i_Replaces.length; v_Index++)
            {
                v_Ret = StringHelp.replaceLast(v_Ret ,i_Replaces[v_Index] ,i_ReplaceBys[i_ReplaceBys.length - 1]);
            }
        }
        
        return v_Ret;
    }
    
    
    
    /**
     * 替换字符串（每个关键字只替换一次，从末尾替换起）
     * 
     * Java自带的String.replace()方法，是通过正则表达式做的替换动作，
     * 对于一些特殊字符，要做十分复杂的处理，并不好用。
     * 
     * 比如说："Q美元Q".replaceLast("美元" ,"$"); 方法会出错。
     * 
     * 测试环境为：Mac:OSX EI Capitan , JDK:1.7.0_45-b18
     * 
     * @author      ZhengWei(HY)
     * @createDate  2018-12-27
     * @version     v1.0
     *
     * @param i_Info
     * @param i_Replace
     * @param i_ReplaceBy
     * @return
     */
    public final static String replaceLast(final String i_Info ,final String i_Replace ,final String i_ReplaceBy)
    {
        if ( null == i_Info
          || null == i_Replace
          || null == i_ReplaceBy
          || 0    >= i_Info.length()
          || 0    >= i_Replace.length() )
        {
            return i_Info;
        }
        
        StringBuilder v_Buffer    = new StringBuilder();
        int           v_RLen      = i_Replace.length();
        int           v_FromIndex = i_Info.lastIndexOf(i_Replace);
        
        if ( v_FromIndex >= 0 )
        {
            v_Buffer.append(i_Info.substring(0 ,v_FromIndex));
            v_Buffer.append(i_ReplaceBy);
            v_Buffer.append(i_Info.substring(v_FromIndex + v_RLen));
        }
        else
        {
            return i_Info;
        }
        
        return v_Buffer.toString();
    }
	
    
    
    /**
     * 编码制式转为GBK
     * 
     * @param str
     * @return
     */
    public final static String xmlEncoding(String str)
    {
		if(str == null)
		{
		    return null;
		}
		
		try
		{
		    return new String(str.getBytes(),"GBK");
		}
		catch(Exception ex)
		{
		    return str;
		}
    }
    
    
    
    /**
     * XML特殊字符串转码。类似于URLEnCode.encode()功能。但转换的特殊字符比较少，仅限于XML中出现的几个字符。
     * 
     * @param i_Str
     * @return
     */
    public final static String xmlEnCode(String i_Str)
    {
        initXML_EnCodes();
        
        if ( Help.isNull(i_Str) )
        {
            throw new java.lang.NullPointerException("Param i_Str is null.");
        }
        
        
        String           v_Ret  = new String(i_Str);
        Iterator<String> v_Iter = $XML_EnCodes.keySet().iterator();
        while ( v_Iter.hasNext() )
        {
            String v_Replace = v_Iter.next();
            v_Ret = v_Ret.replaceAll(v_Replace ,$XML_EnCodes.get(v_Replace));
        }
        
        return v_Ret;
    }
    
    
    
    /**
     * XML特殊字符串转码。类似于URLEnCode.decode()功能。但转换的特殊字符比较少，仅限于XML中出现的几个字符。
     * 
     * @param i_Str
     * @return
     */
    public final static String xmlDeCode(String i_Str)
    {
        initXML_EnCodes();
        
        if ( Help.isNull(i_Str) )
        {
            throw new java.lang.NullPointerException("Param i_Str is null.");
        }
        
        
        String           v_Ret  = new String(i_Str);
        Iterator<String> v_Iter = $XML_EnCodes.values().iterator();
        while ( v_Iter.hasNext() )
        {
            String v_Replace = v_Iter.next();
            v_Ret = v_Ret.replaceAll(v_Replace ,$XML_EnCodes.getReverse(v_Replace).get(0));
        }
        
        return v_Ret;
    }
    
    
    
    /**
     * 字符串转义。
     * 
     * 在 URLEncoder.encode() 功能略加改造。
     * 
     * @param i_String        需要转义的字符串
     * @param i_Charset       字符集名称(默认:UTF-8)
     * @return
     */
    public final static String encode(String i_String ,String i_Charset)
    {
        return encode(i_String ,i_Charset ,"");
    }
    
    
    
    /**
     * 字符串转义。
     * 
     * 在 URLEncoder.encode() 功能略加改造。
     * 
     * @param i_String        需要转义的字符串
     * @param i_Charset       字符集名称(默认:UTF-8)
     * @param i_NotInStrings  转义时被排除的字符
     * @return
     */
    public final static String encode(String i_String ,String i_Charset ,String i_NotInStrings)
    {
        String        v_Charset = Help.NVL(i_Charset ,"UTF-8");
        StringBuilder v_Buffer  = new StringBuilder();
        v_Buffer.ensureCapacity(i_String.length() * 8);
        
        for (int i=0; i<i_String.length(); i++)
        {
            String v_One = i_String.substring(i ,i+1);
            
            if ( i_NotInStrings.indexOf(v_One) >= 0 )
            {
                v_Buffer.append(v_One);
            }
            else
            {
                try
                {
                    v_Buffer.append(URLEncoder.encode(v_One ,v_Charset));
                }
                catch (Exception exce)
                {
                    v_Buffer.append(v_One);
                }
            }
        }
        
        return v_Buffer.toString();
    }
    
    
    
    /**
     * 与 encode() 成对出现的逆向转义(默认:UTF-8)
     * 
     * @param i_String        需要转义的字符串
     * @return
     */
    public final static String decode(String i_String)
    {
        return decode(i_String ,null);
    }
    
    
    
    /**
     * 与 encode() 成对出现的逆向转义
     * 
     * @param i_String        需要转义的字符串
     * @param i_Charset       字符集名称(默认:UTF-8)
     * @return
     */
    public final static String decode(String i_String ,String i_Charset)
    {
        try
        {
            return URLDecoder.decode(i_String ,Help.NVL(i_Charset ,"UTF-8"));
        }
        catch (Exception exce)
        {
            return i_String;
        }
    }
    
    
    
    /**
     * 与 JavaScript escape() 方法大体类似，但会生成 Unicode 编码的字符。
     * 
     * 如 您好 = &#x60A8;&#x597D;
     * 
     * @param i_String
     * @return
     */
    public final static String escape_toUnicode(String i_String)
    {
        return escape_toUnicode(i_String ,"");
    }
    
    
    
    /**
     * 与 JavaScript escape() 方法大体类似，但会生成 Unicode 编码的字符。
     * 
     * 如 您好 = &#x60A8;&#x597D;
     * 
     * @param i_String
     * @param i_NotInStrings   排除这些字符不转义
     * @return
     */
    public final static String escape_toUnicode(String i_String ,String i_NotInStrings)
    {
        StringBuilder v_Buffer = new StringBuilder();
        v_Buffer.ensureCapacity(i_String.length() * 8);
        
        for (int i=0; i<i_String.length(); i++)
        {
            char v_OneChar = i_String.charAt(i);
            
            if ( i_NotInStrings.indexOf(v_OneChar) >= 0 )
            {
                v_Buffer.append(v_OneChar);
            }
            else if ( Character.isDigit(v_OneChar) || Character.isLowerCase(v_OneChar) || Character.isUpperCase(v_OneChar) )
            {
                v_Buffer.append(v_OneChar);
            }
            else if ( v_OneChar < 256 )
            {
                v_Buffer.append("%");
                if ( v_OneChar < 16 )
                {
                    v_Buffer.append("0");
                }
                v_Buffer.append(Integer.toString(v_OneChar ,16));
            }
            else
            {
                v_Buffer.append("&#x");
                v_Buffer.append(Integer.toString(v_OneChar ,16));
                v_Buffer.append(";");
            }
        }
        
        return v_Buffer.toString();
    }
    
    
    
    /**
     * 可对通过 escape_toUnicode() 编码的字符串进行解码
     * 
     * @param i_String
     * @return
     */
    public static String unescape_toUnicode(String i_String)
    {
        return unescape(i_String.replaceAll("&amp;" ,"&").replaceAll("&#x" ,"%u"));
    }
    
    
    
    /**
     * 与 JavaScript escape() 方法一样。
     * 
     * 对字符串进行编码，这样就可以在所有的计算机上读取该字符串。
     * 
     * 如 您好 = %u60A8%u597D
     * 
     * @param i_String
     * @return
     */
    public final static String escape(String i_String)
    {
        return escape(i_String ,"");
    }
    
    
    
    /**
     * 与 JavaScript escape() 方法一样。
     * 
     * 对字符串进行编码，这样就可以在所有的计算机上读取该字符串。
     * 
     * 如 您好 = %u60A8%u597D
     * 
     * @param i_String
	 * @param i_NotInStrings   排除这些字符不转义
     * @return
     */
    public final static String escape(String i_String ,String i_NotInStrings)
    {
        StringBuilder v_Buffer = new StringBuilder();
        v_Buffer.ensureCapacity(i_String.length() * 6);
        
        for (int i=0; i<i_String.length(); i++)
        {
            char v_OneChar = i_String.charAt(i);
            
            if ( i_NotInStrings.indexOf(v_OneChar) >= 0 )
            {
                v_Buffer.append(v_OneChar);
            }
            else if ( Character.isDigit(v_OneChar) || Character.isLowerCase(v_OneChar) || Character.isUpperCase(v_OneChar) )
            {
                v_Buffer.append(v_OneChar);
            }
            else if ( v_OneChar < 256 )
            {
                v_Buffer.append("%");
                if ( v_OneChar < 16 )
                {
                    v_Buffer.append("0");
                }
                v_Buffer.append(Integer.toString(v_OneChar ,16));
            }
            else
            {
                v_Buffer.append("%u");
                v_Buffer.append(Integer.toString(v_OneChar ,16));
            }
        }
        
        return v_Buffer.toString();
    }
 
    
    
    /**
     * 与 JavaScript unescape() 方法一样。
     * 
     * 可对通过 escape() 编码的字符串进行解码
     * 
     * @param i_String
     * @return
     */
    public static String unescape(String i_String)
    {
        StringBuilder v_Buffer  = new StringBuilder();
        int           v_LastPos = 0;
        int           v_Pos     = 0;
        char          v_OneChar = 0;
        
        v_Buffer.ensureCapacity(i_String.length());
        
        while ( v_LastPos < i_String.length() )
        {
            v_Pos = i_String.indexOf("%" ,v_LastPos);
            
            if ( v_Pos == v_LastPos )
            {
                if ( i_String.charAt(v_Pos + 1) == 'u' )
                {
                    v_OneChar = (char) Integer.parseInt(i_String.substring(v_Pos + 2 ,v_Pos + 6) ,16);
                    v_Buffer.append(v_OneChar);
                    v_LastPos = v_Pos + 6;
                    
                    if ( i_String.substring(v_LastPos).startsWith(";") )
                    {
                        v_LastPos++;
                    }
                }
                else
                {
                    v_OneChar = (char) Integer.parseInt(i_String.substring(v_Pos + 1 ,v_Pos + 3) ,16);
                    v_Buffer.append(v_OneChar);
                    v_LastPos = v_Pos + 3;
                }
            }
            else
            {
                if ( v_Pos == -1 )
                {
                    v_Buffer.append(i_String.substring(v_LastPos));
                    v_LastPos = i_String.length();
                }
                else
                {
                    v_Buffer.append(i_String.substring(v_LastPos ,v_Pos));
                    v_LastPos = v_Pos;
                }
            }
        }
        
        return v_Buffer.toString();
    }
    
    
    
    /**
     * 去掉某些连续重复出现的字符。如“A...B..C”，去掉重复连续的.点，就变成：“A.B.C”
     * 
     * @author      ZhengWei(HY)
     * @createDate  2018-12-21
     * @version     v1.0
     *
     * @param i_Text        被去重的字符串
     * @param i_DoubleText  去重的字符串是哪个
     * @return
     */
    public static String trimToDistinct(String i_Text ,String i_DoubleText)
    {
        if ( i_Text == null || i_DoubleText == null )
        {
            return i_Text;
        }
        
        String v_Ret        = i_Text;
        String v_DoubleInfo = i_DoubleText + i_DoubleText;
        do
        {
            v_Ret = StringHelp.replaceAll(v_Ret ,v_DoubleInfo ,i_DoubleText);
        }
        while ( v_Ret.indexOf(v_DoubleInfo) >= 0 );
        
        return v_Ret;
    }
    
    
    
    /**
     * 去掉空格、回车、换行字符串
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-12-23
     * @version     v1.0
     *
     * @param i_Text
     * @return
     */
    public static String trim(String i_Text)
    {
        if ( i_Text != null )
        {
            Pattern v_Pattern = Pattern.compile("\\s*|\t|\r|\n");
            Matcher v_Matcher = v_Pattern.matcher(i_Text);
            
            return v_Matcher.replaceAll("");
        }
        else
        {
            return i_Text;
        }
    }
    
    
    
    /**
     * 去掉字符串i_Text前后两端的指定的子字符i_TrimKey
     * 
     * @author      ZhengWei(HY)
     * @createDate  2018-05-23
     * @version     v1.0
     *
     * @param i_Text      被修整的字符串
     * @param i_TrimKeys  被去掉的字符串（区分大小写，并且有前后顺序的区别）
     * @return
     */
    public static String trim(String i_Text ,String ... i_TrimKeys)
    {
        String v_Ret = i_Text;
        
        for (String v_TrimKey : i_TrimKeys)
        {
            int v_Len = v_TrimKey.length();
            
            if ( v_Ret.startsWith(v_TrimKey) )
            {
                v_Ret = v_Ret.substring(v_Len);
            }
            if ( v_Ret.endsWith(v_TrimKey) )
            {
                v_Ret = v_Ret.substring(0 ,v_Ret.length() - v_Len);
            }
        }
        
        return v_Ret;
    }
    
    
    
    /**
     * 将各种数字字符串，转数字类型
     * 
     * 内部不效验空指针
     * 
     * @author      ZhengWei(HY)
     * @createDate  2022-05-19
     * @version     v1.0
     *
     * @param i_Number
     * @return
     */
    public static BigDecimal toNumber(String i_Number)
    {
        String v_Number = StringHelp.replaceAll(i_Number ,$Currency ,$ReplaceNil);
        return new BigDecimal(v_Number);
    }
    
    
    
    /**
     * 将各种数字表达方式转为正规的数字表达方式。
     *   1. 将 "-.123" 转换成: "-0.123"
     *   2. 将科学计数法转为正规数字
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-12-23
     * @version     v1.0
     *
     * @param i_Text
     * @return
     */
    public static String toNumberString(String i_Number)
    {
        if ( Help.isNull(i_Number) )
        {
            return i_Number;
        }
        
        if ( !isNumber(i_Number) )
        {
            return i_Number;
        }
        
        String v_Number = i_Number.trim();
        if ( v_Number.toUpperCase().contains("E") )
        {
            v_Number = new BigDecimal(v_Number).toPlainString();
        }
        else
        {
            v_Number = Double.valueOf(v_Number).toString();
        }
        
        return v_Number;
    }
    
    
    
    /**
     * 简化显示数字，显示允许的长度。
     * 
     *   如允许长度为6时，1234567890.1 将显示为 1234567890；
     *   如允许长度为6时，1.2345678    将显示为 1.23457；
     *   如允许长度为6时，1234.5678    将显示为 1234.57；
     *   如允许长度为6时，0.000001     将显示为 1.000E-6；
     * 
     * @author      ZhengWei(HY)
     * @createDate  2018-10-16
     * @version     v1.0
     * 
     * @param i_Num              数字
     * @param i_AllowLen         允许长度
     * @param i_RoundScientific  如果启用了科学计数后，科学计数的显示小数位数（四舍五入）
     */
    public static <N extends Number> String toNumberSimplify(N i_Num ,int i_AllowLen ,int i_RoundScientific)
    {
        return toNumberSimplify(i_Num.toString() ,i_AllowLen ,i_RoundScientific);
    }
    
    
    
    /**
     * 简化显示数字，显示允许的长度。
     * 
     *   如允许长度为6时，1234567890.1 将显示为 1234567890；
     *   如允许长度为6时，1.2345678    将显示为 1.23457；
     *   如允许长度为6时，1234.5678    将显示为 1234.57；
     *   如允许长度为6时，0.000001     将显示为 1.000E-6；
     * 
     * @author      ZhengWei(HY)
     * @createDate  2018-10-17
     * @version     v1.0
     * 
     * @param i_Num              数字
     * @param i_AllowLen         允许长度
     * @param i_RoundScientific  如果启用了科学计数后，科学计数的显示小数位数（四舍五入）
     */
    public static String toNumberSimplify(String i_Num ,int i_AllowLen ,int i_RoundScientific)
    {
        if ( !Help.isNumber(i_Num) )
        {
            return i_Num;
        }
        else if ( i_AllowLen > 0 )
        {
            if ( i_Num.indexOf(".") >= 0 )
            {
                String [] v_NumArr      = i_Num.split("\\.");
                double    v_NumSimplify = Double.parseDouble(i_Num);
                
                if ( v_NumArr[0].length() >= i_AllowLen )
                {
                    return v_NumArr[0];
                }

                int v_Digit = i_AllowLen - v_NumArr[0].length();
                v_NumSimplify = Help.round(i_Num ,v_Digit);
                
                if ( v_NumSimplify == 0 && Double.parseDouble(i_Num) != 0 )
                {
                    // 高精度值为0.0001，四舍五入后为0.000时，显示科学记数法 ZhengWei(HY) Add 2018-10-15
                    return toScientificNotation(i_Num ,v_Digit + 1 ,i_RoundScientific ,v_Digit + 1);
                }
                
                return v_NumSimplify + "";
            }
        }
        
        return i_Num;
    }
    
    
    
    /**
     * 科学计数
     * 
     * @param i_Num              数字
     * @param i_Decimal          多少位的小数启用科学计数
     * @param i_RoundScientific  如果启用了科学计数后，科学计数的显示小数位数（四舍五入）
     * @param i_Round            未启用科学计数时，对数字i_Num的四舍五入。
     * 
     * @author  ZhengWei(HY)
     * @version 1.0  2018-10-16
     */
    public static <N extends Number> String toScientificNotation(N i_Num ,int i_Decimal ,int i_RoundScientific ,int i_Round)
    {
        return toScientificNotation(i_Num.toString() ,i_Decimal ,i_RoundScientific ,i_Round);
    }
    
    
    
    /**
     * 科学计数
     * 
     * @param i_Num              数字
     * @param i_Decimal          多少位的小数启用科学计数
     * @param i_RoundScientific  如果启用了科学计数后，科学计数的显示小数位数（四舍五入）
     * @param i_Round            未启用科学计数时，对数字i_Num的四舍五入。
     * 
     * @author  ZhengWei(HY)
     * @version 1.0  2018-10-16
     */
    public static String toScientificNotation(String i_Num ,int i_Decimal ,int i_RoundScientific ,int i_Round)
    {
        if ( !Help.isNumber(i_Num) )
        {
            return "";
        }
        
        BigDecimal v_Num = new BigDecimal(i_Num);
        if ( 1 > v_Num.doubleValue() && v_Num.doubleValue() > -1 && !v_Num.equals(BigDecimal.ZERO) )
        {
            int v_Decimal = 0;
            BigDecimal v_Ten = new BigDecimal(10);
            
            do
            {
                v_Num = v_Num.multiply(v_Ten);
                v_Decimal++;
            }
            while ( 1 > v_Num.doubleValue() && v_Num.doubleValue() > -1 );
            
            if ( v_Decimal >= i_Decimal )
            {
                return Help.round(v_Num ,i_RoundScientific) + "E-" + v_Decimal;
            }
        }
        
        String v_RetFixed = Help.round(i_Num ,i_Round) + "";
        return i_Num.length() <= v_RetFixed.length() ? i_Num : v_RetFixed;
    }
    
    
    
    /**
     * 将一种编码的文字，转为另一种编码的文字
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-10-17
     * @version     v1.0
     *
     * @param i_Text              文字信息
     * @param i_FromCharEncoding  文字的原编码
     * @param i_ToCharEncoding    将转为另一种编码
     * @return                    异常是为：null
     */
    public final static String toCharEncoding(String i_Text ,String i_FromCharEncoding ,String i_ToCharEncoding)
    {
        if ( Help.isNull(i_Text) )
        {
            return null;
        }
        
        try
        {
            return new String(i_Text.getBytes(i_FromCharEncoding) ,i_ToCharEncoding);
        }
        catch (Exception exce)
        {
            return null;
        }
    }
    
    
    
    /**
     * 简单的判定文本是否为某一种字符集编码类型
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-10-17
     * @version     v1.0
     *
     * @param i_Text          文字信息
     * @param i_CharEncoding  是否为此编码类型
     * @return
     */
    public final static boolean isCharEncoding(String i_Text ,String i_CharEncoding)
    {
        return i_Text.equals(toCharEncoding(i_Text ,i_CharEncoding ,i_CharEncoding));
    }
    
    
    
    /**
     * 简单的判定文本的字符集编码类型
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-10-17
     * @version     v1.0
     *
     * @param i_Text
     * @return
     */
    public final static String getCharEncodings(String i_Text)
    {
        if ( Help.isNull(i_Text) )
        {
            return null;
        }
        
        String v_Ret = "";
        
        for (String v_CharEncoding : $CharEncodings)
        {
            if ( isCharEncoding(i_Text ,v_CharEncoding) )
            {
                v_Ret += v_CharEncoding + ";";
            }
        }
        
        return v_Ret;
    }
    
     
    
    /**
     * 将浮点数变字符
     * 
     * @param i_Num
     * @param i_Digit  保留的小数位数
     * @return
     */
    public final static String doubleParse(double i_Num ,int i_Digit)
    {
        StringBuilder v_Buffer = new StringBuilder();
        
        v_Buffer.append("#.");
        
        for (int i=0; i<i_Digit; i++)
        {
            v_Buffer.append("#");
        }
        
        
        NumberFormat v_Format = new DecimalFormat(v_Buffer.toString());
        
        return v_Format.format(i_Num);
    }
    
 
    /**
     * 截取文件后缀
     * 
     * @param i_FileName   文件名称
     * @return  返回时，包含.符号。当文件名称是没有.符号时，返回null，当作没有扩展名处理
     */
    public final static String getFilePostfix(String i_FileName)
    {
        int    v_DotIndex = i_FileName.lastIndexOf(".");
        
        if ( v_DotIndex < 0 )
        {
            return null;
        }
        
        return i_FileName.substring(v_DotIndex);
    }
    
    
    /**
     * 截取全路径中的文件名称
     * 
     * @param i_FileName   全路径 + 文件名称
     * @return
     */
    public final static String getFileName(String i_FileName)
    {
        int v_Index = i_FileName.lastIndexOf("\\");
        
        if ( v_Index < 0 )
        {
            v_Index = i_FileName.lastIndexOf("/");
            
            if ( v_Index < 0 )
            {
                return i_FileName;
            }
        }
        
        return i_FileName.substring(v_Index + 1);
    }
    
    
    /**
     * 截取全路径中的文件短名称
     * 
     * @param i_FileName   全路径 + 文件名称
     * @return
     */
    public final static String getFileShortName(String i_FileName)
    {
        String v_FileName = getFileName(i_FileName);
        int    v_DotIndex = v_FileName.lastIndexOf(".");
        
        if ( v_DotIndex < 0 )
        {
            return v_FileName;
        }
        
        return v_FileName.substring(0 ,v_DotIndex);
    }
    
    
    /**
     * 字符串左填充
     * 
     * @param i_Str          原始字符串
     * @param i_TotalLength  填充后的总长度
     * @param i_PadStr       填充的字符串
     * @return
     */
    public final static String lpad(String i_Str ,int i_TotalLength ,String i_PadStr)
    {
        return pad(i_Str ,i_TotalLength ,i_PadStr ,-1);
    }
    
    
    /**
     * 字符串右填充
     * 
     * @param i_Str          原始字符串
     * @param i_TotalLength  填充后的总长度
     * @param i_PadStr       填充的字符串
     * @return
     */
    public final static String rpad(String i_Str ,int i_TotalLength ,String i_PadStr)
    {
        return pad(i_Str ,i_TotalLength ,i_PadStr ,1);
    }
    
    
    /**
     * 字符串左填充
     * 
     * @param i_Long         原始数字
     * @param i_TotalLength  填充后的总长度
     * @param i_PadStr       填充的字符串
     * @return
     */
    public final static String lpad(long i_Long ,int i_TotalLength ,String i_PadStr)
    {
        return pad(i_Long ,i_TotalLength ,i_PadStr ,-1);
    }
    
    
    /**
     * 字符串左填充
     * 
     * @param i_Double       原始数字
     * @param i_TotalLength  填充后的总长度
     * @param i_PadStr       填充的字符串
     * @return
     */
    public final static String lpad(double i_Double ,int i_TotalLength ,String i_PadStr)
    {
        return pad(i_Double ,i_TotalLength ,i_PadStr ,-1);
    }
    
    
    /**
     * 字符串右填充
     * 
     * @param i_Long         原始数字
     * @param i_TotalLength  填充后的总长度
     * @param i_PadStr       填充的字符串
     * @return
     */
    public final static String rpad(long i_Long ,int i_TotalLength ,String i_PadStr)
    {
        return pad(i_Long ,i_TotalLength ,i_PadStr ,1);
    }
    
    
    /**
     * 字符串右填充
     * 
     * @param i_Double       原始数字
     * @param i_TotalLength  填充后的总长度
     * @param i_PadStr       填充的字符串
     * @return
     */
    public final static String rpad(double i_Double ,int i_TotalLength ,String i_PadStr)
    {
        return pad(i_Double ,i_TotalLength ,i_PadStr ,1);
    }
    
    
    /**
     * 字符串左填充
     * 
     * @param i_Int          原始数字
     * @param i_TotalLength  填充后的总长度
     * @param i_PadStr       填充的字符串
     * @return
     */
    public final static String lpad(int i_Int ,int i_TotalLength ,String i_PadStr)
    {
        return pad(i_Int ,i_TotalLength ,i_PadStr ,-1);
    }
    
    
    /**
     * 字符串右填充
     * 
     * @param i_Int          原始数字
     * @param i_TotalLength  填充后的总长度
     * @param i_PadStr       填充的字符串
     * @return
     */
    public final static String rpad(int i_Int ,int i_TotalLength ,String i_PadStr)
    {
        return pad(i_Int ,i_TotalLength ,i_PadStr ,1);
    }
    
    
    /**
     * 字符串左填充
     * 
     * @param i_Obj          原始对象
     * @param i_TotalLength  填充后的总长度
     * @param i_PadStr       填充的字符串
     * @return
     */
    public final static String lpad(Object i_Obj ,int i_TotalLength ,String i_PadStr)
    {
        return pad(i_Obj ,i_TotalLength ,i_PadStr ,-1);
    }
    
    
    /**
     * 字符串右填充
     * 
     * @param i_Obj          原始对象
     * @param i_TotalLength  填充后的总长度
     * @param i_PadStr       填充的字符串
     * @return
     */
    public final static String rpad(Object i_Obj ,int i_TotalLength ,String i_PadStr)
    {
        return pad(i_Obj ,i_TotalLength ,i_PadStr ,1);
    }
    
    
    /**
     * 字符串填充
     * 
     * @param i_Str          原始字符串
     * @param i_TotalLength  填充后的总长度
     * @param i_PadStr       填充的字符串
     * @param i_Way          填充方向。小于0，左则填充；大于0，右则填充
     * @return
     */
    public final static String pad(String i_Str ,int i_TotalLength ,String i_PadStr ,int i_Way)
    {
        if ( i_Str == null )
        {
            return null;
        }
        
        
        int           v_Len    = i_Str.length();
        StringBuilder v_Buffer = new StringBuilder();
        
        if (i_Way >= 0)
        {
            v_Buffer.append(i_Str);
        }
        
        for (int i=0; i<i_TotalLength-v_Len; i++)
        {
            v_Buffer.append(i_PadStr);
        }
        
        if (i_Way < 0)
        {
            v_Buffer.append(i_Str);
        }
        
        return v_Buffer.toString();
    }
    
    
    /**
     * 字符串填充
     * 
     * @param i_Long         原始数字
     * @param i_TotalLength  填充后的总长度
     * @param i_PadStr       填充的字符串
     * @param i_Way          填充方向。小于0，左则填充；大于0，右则填充
     * @return
     */
    public final static String pad(long i_Long ,int i_TotalLength ,String i_PadStr ,int i_Way)
    {
        return pad(String.valueOf(i_Long) ,i_TotalLength ,i_PadStr ,i_Way);
    }
    
    
    /**
     * 字符串填充
     * 
     * @param i_Double       原始数字
     * @param i_TotalLength  填充后的总长度
     * @param i_PadStr       填充的字符串
     * @param i_Way          填充方向。小于0，左则填充；大于0，右则填充
     * @return
     */
    public final static String pad(double i_Double ,int i_TotalLength ,String i_PadStr ,int i_Way)
    {
        return pad(String.valueOf(i_Double) ,i_TotalLength ,i_PadStr ,i_Way);
    }
    
    
    /**
     * 字符串填充
     * 
     * @param i_Int          原始数字
     * @param i_TotalLength  填充后的总长度
     * @param i_PadStr       填充的字符串
     * @param i_Way          填充方向。小于0，左则填充；大于0，右则填充
     * @return
     */
    public final static String pad(int i_Int ,int i_TotalLength ,String i_PadStr ,int i_Way)
    {
        return pad(String.valueOf(i_Int) ,i_TotalLength ,i_PadStr ,i_Way);
    }
    
    
    /**
     * 字符串填充
     * 
     * @param i_Obj          原始对象
     * @param i_TotalLength  填充后的总长度
     * @param i_PadStr       填充的字符串
     * @param i_Way          填充方向。小于0，左则填充；大于0，右则填充
     * @return
     */
    public final static String pad(Object i_Obj ,int i_TotalLength ,String i_PadStr ,int i_Way)
    {
        return pad(i_Obj.toString() ,i_TotalLength ,i_PadStr ,i_Way);
    }
    
    
    
    /**
     * 获取对应的计算单位
     * 
     * @param i_ByteSize  字节大小
     * @return
     */
    public final static String getComputeUnit(long i_ByteSize)
    {
        return getComputeUnit(i_ByteSize ,2);
    }
    
    
    
    /**
     * 获取对应的计算单位
     * 
     * @param i_ByteSize  字节大小
     * @return
     */
    public final static String getComputeUnit(long i_ByteSize ,int i_Digit)
    {
        String [] v_UnitArr = {"Byte" ,"KB" ,"MB" ,"GB" ,"TB"};
        
        return getComputeUnit(i_ByteSize ,i_Digit ,v_UnitArr);
    }
    
    
    
    /**
     * 获取对应的计算单位
     * 
     * @param i_ByteSize  字节大小
     * @param i_UnitArr   单位数据。下标越大单位级别越大
     * @return
     */
    public final static String getComputeUnit(long i_ByteSize ,String [] i_UnitArr)
    {
        return getComputeUnit(i_ByteSize ,2 ,i_UnitArr);
    }
    
    
    
    /**
     * 获取对应的计算单位
     * 
     * @param i_ByteSize  字节大小
     * @param i_Digit     精确度。四舍五入的位数
     * @param i_UnitArr   单位数据。下标越大单位级别越大
     * @return
     */
    public final static String getComputeUnit(long i_ByteSize ,int i_Digit ,String [] i_UnitArr)
    {
        if ( i_ByteSize <= 0  )
        {
            return "0 Byte";
        }
        
        int    v_UnitIndex = 0;
        double v_Size      = i_ByteSize;
        
        for (; v_Size > 1024d && v_UnitIndex<i_UnitArr.length; v_UnitIndex++)
        {
            v_Size = v_Size / 1024d;
        }
            
        return doubleParse(v_Size ,i_Digit) + " " + i_UnitArr[v_UnitIndex];
    }
    
    
    
    /**
     * 将正整数转为 36进制。
     * 
     *  0  为  0
     *  1  为  1
     * ...
     *  9  为  9
     * 10  为  A
     * 11  为  B
     * 12  为  C
     * ...
     * 35  为  Z
     * 
     * @param i_Value
     * @return
     */
    public final static String toABC36(int i_Value)
    {
        int    v_Value = Math.abs(i_Value);
        String v_Ret   = "";
        
        while ( v_Value >= 36 )
        {
            int v_Mod = v_Value % 36;
            
            v_Ret = $ABC36.substring(v_Mod ,v_Mod + 1) + v_Ret;
            
            v_Value = v_Value / 36;
        }
        
        return $ABC36.substring(v_Value ,v_Value + 1) + v_Ret;
    }
    
    
    
    /**
     * 36进制的字符转为10进制数。
     * 
     * 是 toABC36() 方法的反向方法。
     * 
     * @param i_Value
     * @return
     */
    public final static int reABC36(String i_Value)
    {
        String v_Value  = i_Value.trim().toUpperCase();
        int    v_Len    = v_Value.length();
        int    v_Index  = 0;
        int    v_CharAt = 0;
        int    v_Ret    = 0;
        
        for ( ; v_Index<v_Len - 1; v_Index++)
        {
            v_CharAt = v_Value.charAt(v_Index);
            
            if ( v_CharAt < 65 )
            {
                v_Ret += (v_CharAt - 48)      * Math.pow(36 ,v_Len - v_Index - 1);
            }
            else
            {
                v_Ret += (v_CharAt - 65 + 10) * Math.pow(36 ,v_Len - v_Index - 1);
            }
        }
        
        v_CharAt = v_Value.charAt(v_Index);
        if ( v_CharAt < 65 )
        {
            v_Ret += v_CharAt - 48;
        }
        else
        {
            v_Ret += v_CharAt - 65 + 10;
        }
        
        return v_Ret;
    }
    
    
    
    /**
     * 将正整数转为 26进制。
     * 
     * 0  为  A
     * 1  为  B
     * 2  为  C
     * ...
     * 25 为  Z
     * 
     * @param i_Value
     * @return
     */
    public final static String toABC26(int i_Value)
    {
        int    v_Value = Math.abs(i_Value);
        String v_Ret   = "";
        
        while ( v_Value >= 26 )
        {
            int v_Mod = v_Value % 26;
            
            v_Ret = $ABC.substring(v_Mod ,v_Mod + 1) + v_Ret;
            
            v_Value = v_Value / 26 - 1;
        }
        
        return $ABC.substring(v_Value ,v_Value + 1) + v_Ret;
    }
    
    
    
    /**
     * 26进制的字符转为10进制数。
     * 
     * 是 toABC26() 方法的反向方法。
     * 
     * @param i_Value
     * @return
     */
    public final static int reABC26(String i_Value)
    {
        String v_Value = i_Value.trim().toUpperCase();
        int    v_Len   = v_Value.length();
        int    v_Index = 0;
        int    v_Ret   = 0;
        
        for ( ; v_Index<v_Len - 1; v_Index++)
        {
            v_Ret += (v_Value.charAt(v_Index) - 65 + 1) * Math.pow(26 ,v_Len - v_Index - 1);
        }
        
        return v_Ret + (v_Value.charAt(v_Index) - 65);
    }
    
    
    
    /**
     * 转码
     * 
     * @author      ZhengWei(HY)
     * @createDate  2019-08-19
     * @version     v1.0
     *
     * @param i_Text
     * @param i_Len
     * @return
     */
    public final static String toCode(String i_Text ,int i_Len)
    {
        StringBuilder v_Buffer = new StringBuilder();
        String        v_Text   = i_Text + "\n";
        
        String [] v_Rows = v_Text.split("\n");
        for (int x=0; x<v_Rows.length; x++)
        {
            v_Text = v_Rows[x];
            int v_AddC = 0;
            if ( v_Text.length() % i_Len > 0 )
            {
                v_AddC = i_Len - v_Text.length() % i_Len;
                
                for (;v_AddC>0; v_AddC--)
                {
                    v_Text += " ";
                }
            }
            
            int v_Count  = v_Text.length() / i_Len;
            for (int i=0; i<v_Count; i++)
            {
                String v_Sub = "";
                if ( i * i_Len + i_Len > v_Text.length() )
                {
                    v_Sub = v_Text.substring(i * i_Len ,v_Text.length());
                }
                else
                {
                    v_Sub = v_Text.substring(i * i_Len ,i * i_Len + i_Len);
                }
                
                v_Buffer.append(ByteHelp.byteToString(ByteHelp.xorMV(ByteHelp.toByte(v_Sub))));
            }
            v_Buffer.append("\n");
        }
        
        return v_Buffer.toString();
    }
    
    
    
    /**
     * 用于MD5功能中Byte转字符
     * 
     * @param from
     * @return
     */
    public final static String BASE64Encoder(byte [] from)
    {
        StringBuilder to = new StringBuilder((int) (from.length * 1.34) + 3);
        int num = 0;
        char currentByte = 0;
        
        for (int i = 0; i < from.length; i++)
        {
            num = num % 8;
            while (num < 8)
            {
                switch (num)
                {
                    case 0:
                        currentByte = (char) (from[i] & lead6byte);
                        currentByte = (char) (currentByte >>> 2);
                        break;
                    case 2:
                        currentByte = (char) (from[i] & last6byte);
                        break;
                    case 4:
                        currentByte = (char) (from[i] & last4byte);
                        currentByte = (char) (currentByte << 2);
                        if ((i + 1) < from.length)
                        {
                            currentByte |= (from[i + 1] & lead2byte) >>> 6;
                        }
                        break;
                    case 6:
                        currentByte = (char) (from[i] & last2byte);
                        currentByte = (char) (currentByte << 4);
                        if ((i + 1) < from.length)
                        {
                            currentByte |= (from[i + 1] & lead4byte) >>> 4;
                        }
                        break;
                }
                
                to.append(encodeTable[currentByte]);
                num += 6;
            }
        }
        
        if (to.length() % 4 != 0)
        {
            for (int i = 4 - to.length() % 4; i > 0; i--)
            {
                to.append("_");
            }
        }
        
        return to.toString();
    }
    
    
    
    /**
     * MD5加密
     * 
     * @param i_Data
     * @return
     */
    public final static String md5(String i_Data)
    {
        try
        {
            MessageDigest v_MD5 = MessageDigest.getInstance("MD5");
            
            return BASE64Encoder(v_MD5.digest(i_Data.getBytes("utf-8")));
        }
        catch (Exception exce)
        {
            return null;
        }
    }
    
    
    
    /**
     * MD5加密(V2版)
     * 
     * @param i_Data
     * @param i_Type  加密类型。1: 全数字形式
     *                        2: 数字字母混合的形式，但无特殊字符
     * @return
     */
    public final static String md5(String i_Data ,int i_Type)
    {
        try
        {
            MessageDigest v_MD5 = MessageDigest.getInstance("MD5");
            
            return bytesToString(v_MD5.digest(i_Data.getBytes()) ,i_Type);
        }
        catch (Exception exce)
        {
            return null;
        }
    }
    
    
    
    /**
     * 转换字节数组为16进制字串
     * 
     * 主用于MD5加密(V2版)
     * 
     * @param i_Bytes
     * @return
     */
    private final static String bytesToString(byte[] i_Bytes ,int i_Type)
    {
        StringBuilder v_Buffer = new StringBuilder();
        if ( i_Type == 1 )
        {
            for (int i=0; i <i_Bytes.length; i++)
            {
                v_Buffer.append(byteToNum(i_Bytes[i]));
            }
        }
        else
        {
            for (int i=0; i <i_Bytes.length; i++)
            {
                v_Buffer.append(byteToHex(i_Bytes[i]));
            }
        }
        return v_Buffer.toString();
    }
    
    
    
    /**
     * 使获取加密结果的10进制数字字串，即全数字形式
     * 
     * 主用于MD5加密(V2版)
     * 
     * @param i_Byte
     * @return
     */
    private static String byteToNum(byte i_Byte)
    {
        int v_ByteInt = i_Byte;
        if ( v_ByteInt < 0 )
        {
            v_ByteInt += 256;
        }

        return String.valueOf(v_ByteInt);
    }
    
    
    
    /**
     * 获取加密结果的16进制表示，即数字字母混合的形式
     * 
     * 主用于MD5加密(V2版)
     * 
     * @param i_Byte
     * @return
     */
    private static String byteToHex(byte i_Byte)
    {
        int v_ByteInt = i_Byte;
        if ( v_ByteInt < 0 )
        {
            v_ByteInt += 256;
        }
        int d1 = v_ByteInt / 16;
        int d2 = v_ByteInt % 16;
        return "" + encodeTable_V2[d1] + encodeTable_V2[d2];
    }
    
    
    
    /**
     * 将字节数组编码成16进制数字,适用于所有字符（包括中文）
     * 
     * @param i_Str
     * @return
     */
    public final static String bytesToHex(byte [] i_Bytes)
    {
        return bytesToHex(i_Bytes ,0 ,i_Bytes.length);
    }
    
    
    
    /**
     * 将字节数组编码成16进制数字,适用于所有字符（包括中文）
     * 
     * @param i_Str
     * @return
     */
    public final static String bytesToHex(byte [] i_Bytes ,int i_Offset ,int i_Length)
    {
        StringBuilder v_Ret    = new StringBuilder(i_Bytes.length * 2);
        int           v_Offset = i_Offset >= 0 ? i_Offset : 0;
        int           v_length = i_Length <= i_Bytes.length ? i_Length : i_Bytes.length;
        
        // 将字节数组中每个字节拆解成2位16进制整数
        for(int i=v_Offset; i<v_length; i++)
        {
            v_Ret.append(encodeTable_V2[(i_Bytes[i]&0xf0)>>4]);
            v_Ret.append(encodeTable_V2[(i_Bytes[i]&0x0f)>>0]);
        }
        
        return v_Ret.toString();
    }
    
    
    
    /**
     * 将16进制数字解码成字节数组,适用于所有字符（包括中文）
     * 
     * @param i_HexStr
     * @return
     */
    public final static byte [] hexToBytes(String i_HexStr)
    {
        String v_HexString = "0123456789ABCDEF";
        String v_HexStr    = i_HexStr.toUpperCase();
        
        ByteArrayOutputStream v_BytesOut =new ByteArrayOutputStream(i_HexStr.length() / 2);
        // 将每2位16进制整数组装成一个字节
        for(int i=0; i<v_HexStr.length(); i+=2)
        {
            v_BytesOut.write((v_HexString.indexOf(v_HexStr.charAt(i))<<4 | v_HexString.indexOf(v_HexStr.charAt(i+1))));
        }
        
        return v_BytesOut.toByteArray();
    }
    
    
    
    /**
     * 获取i_FindChar字符在i_Text文本中出现的次数。
     * 
     * 比正则表达式性能更高的统计。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2018-03-22
     * @version     v1.0
     *
     * @param i_Text      原文本
     * @param i_FindChar  要查找到字符
     * @return
     */
    public final static int getCount(String i_Text ,char i_FindChar)
    {
        if ( Help.isNull(i_Text) )
        {
            return 0;
        }
        
        int v_IndexOf = 0;
        int v_Count   = 0;
        
        v_IndexOf = i_Text.indexOf(i_FindChar);
        while ( v_IndexOf >= 0 )
        {
            v_Count++;
            v_IndexOf = i_Text.indexOf(i_FindChar ,v_IndexOf + 1);
        }
        
        return v_Count;
    }
    
    
    
    /**
     * 获取i_FindStr字符串在i_Text文本中出现的次数。
     * 
     * @param i_Text         原文本
     * @param i_FindStr      要查找到字符串
     * @return
     */
    public final static int getCount(String i_Text ,String i_FindStr)
    {
        if ( Help.isNull(i_Text) || i_FindStr == null )
        {
            return 0;
        }
        
        
        // 使用Pattern建立匹配模式
        Pattern v_Pattern = Pattern.compile(i_FindStr);
        // 使用Matcher进行各种查找替换操作
        Matcher v_Matcher = v_Pattern.matcher(i_Text);
        
        int v_Count = 0;
        
        while( v_Matcher.find() )
        {
            v_Count++;
        }
        
        return v_Count;
    }
    
    
    
    /**
     * 获取i_FindStrArr字符数组中元素在i_Text文本中出现的总次数。
     * 
     * 字符数组中的每个元素是"或"关系
     * 
     * @param i_Text         原文本
     * @param i_FindStrArr   要查找到字符数组
     * @return
     */
    public final static int getCount(String i_Text ,String [] i_FindStrArr)
    {
        if ( Help.isNull(i_Text) || Help.isNull(i_FindStrArr) )
        {
            return 0;
        }
        
        
        StringBuilder v_Buffer       = new StringBuilder();
        int           v_FindStrCount = 0;
        for (int v_Index=0; v_Index<i_FindStrArr.length; v_Index++ )
        {
            if ( i_FindStrArr[v_Index] != null )
            {
                if ( v_FindStrCount >= 1)
                {
                    v_Buffer.append("|");
                }
                
                v_Buffer.append("(").append(i_FindStrArr[v_Index]).append(")");
                v_FindStrCount++;
            }
        }
        
        
        if ( v_FindStrCount <= 0 )
        {
            return 0;
        }
        
        
        // 使用Pattern建立匹配模式
        Pattern v_Pattern = Pattern.compile(v_Buffer.toString());
        // 使用Matcher进行各种查找替换操作
        Matcher v_Matcher = v_Pattern.matcher(i_Text);
        
        int v_Count = 0;
        
        while( v_Matcher.find() )
        {
            v_Count++;
        }
        
        return v_Count;
    }
    
    
    
    /**
     * 将字符串 ABC# 转换成 [Aa][Bb][Cc]# 的样子，之后就可以忽略大小写的匹配查找
     * 
     * @param i_String
     * @return
     */
    public final static String toPatternUL(String i_String)
    {
        String        v_String = i_String.trim().toUpperCase();
        StringBuilder v_Buffer = new StringBuilder();
        
        for (int i=0; i<v_String.length(); i++)
        {
            String v_OneStr = v_String.substring(i ,i+1);
            int    v_OneInt = v_OneStr.charAt(0);
            
            if ( 65 <= v_OneInt && v_OneInt <= 90 )
            {
                v_Buffer.append("[").append(v_OneStr).append(v_OneStr.toLowerCase()).append("]");
            }
            else
            {
                v_Buffer.append(v_OneStr);
            }
        }
        
        return v_Buffer.toString();
    }
    
    
    
    /**
     * 获取i_FindStr字符串在i_String文本的内容
     * 
     * @param i_Text         原文本
     * @param i_FindStr      要查找到字符串
     * @return
     */
    public final static List<String> getString(String i_String ,String i_FindStr)
    {
        if ( Help.isNull(i_String) || i_FindStr == null )
        {
            return null;
        }
        
        // 使用Pattern建立匹配模式
        Pattern v_Pattern = Pattern.compile(i_FindStr);
        // 使用Matcher进行各种查找替换操作
        Matcher v_Matcher = v_Pattern.matcher(i_String);
        
        List<String> v_Ret = new ArrayList<String>();
        
        while( v_Matcher.find() )
        {
            v_Ret.add(i_String.substring(v_Matcher.start() ,v_Matcher.end()));
        }
        
        return v_Ret;
    }
    
    
    
    /**
     * 获取两个字符串间的内容。如，StringHelp.getString("A123C" ,"A" ,"C") = "123"
     * 
     * 可支持，不区分大小写的匹配
     * 
     * 注意：只查找首次匹配的内容
     * 
     * @param i_String    被查询的大字符串
     * @param i_BeginStr  开始字符串
     * @param i_EndStr    结束字符串
     * @return
     */
    public final static String getString(String i_String ,String i_BeginStr ,String i_EndStr)
    {
        if ( Help.isNull(i_String) )
        {
            throw new java.lang.NullPointerException("Param String is null.");
        }
        
        if ( i_BeginStr == null )
        {
            throw new java.lang.NullPointerException("Param BeginStr is null.");
        }
        
        if ( i_EndStr == null )
        {
            throw new java.lang.NullPointerException("Param EndStr is null.");
        }
        
        
        String v_EndStr = toPatternUL(i_EndStr);
        // 这个正则表达没有排除模式 2014-11-28
        // Pattern v_Pattern = Pattern.compile("(" + toPatternUL(i_BeginStr) + ")([ .\\S\\s]*)(" + toPatternUL(i_EndStr) + ")");
        // 使用Pattern建立匹配模式
        Pattern v_Pattern = Pattern.compile("(" + toPatternUL(i_BeginStr) + ")((?!" + v_EndStr + ").)*(" + v_EndStr + ")");
        // 使用Matcher进行各种查找替换操作
        Matcher v_Matcher = v_Pattern.matcher(i_String);
        
        
        while( v_Matcher.find() )
        {
            return i_String.substring(v_Matcher.start() + i_BeginStr.length() ,v_Matcher.end() - i_EndStr.length());
        }
        
        return "";
    }
    
    
    
    /**
     * 获取指定XML标记及其下的所有内容。
     * 
     * 可支持，不区分大小写的匹配。
     * 
     * 注意：只查找首次匹配的内容
     * 
     * @param i_XML          XML完整内容
     * @param i_XMLSignName  XML标记
     * @return
     */
    public final static String getXMLSignContent(String i_XML ,String i_XMLSignName)
    {
        if ( Help.isNull(i_XML) )
        {
            throw new java.lang.NullPointerException("XML is null.");
        }
        
        if ( Help.isNull(i_XMLSignName) )
        {
            throw new java.lang.NullPointerException("XML sign name is null.");
        }
        
        
        // 使用Pattern建立匹配模式
        Pattern v_Pattern = Pattern.compile($XMLSign.replaceAll("XMLSignName" ,toPatternUL(i_XMLSignName)));
        // 使用Matcher进行各种查找替换操作
        Matcher v_Matcher = v_Pattern.matcher(i_XML);
        
        
        while( v_Matcher.find() )
        {
            return i_XML.substring(v_Matcher.start() ,v_Matcher.end());
        }
        
        return "";
    }
    
    
    
    /**
     * 正则表达式验证邮箱格式
     * 
     * @param i_EMail
     * @return
     */
    public final static boolean isEMail(String i_EMail)
    {
        if ( Help.isNull(i_EMail) )
        {
            return false;
        }
        
        return Pattern.matches($EMail ,i_EMail);
    }
    
    
    
    /**
     * 判断是否只由英文、数字(0~9)和下划线组成
     * 
     * @param i_Str   为空时返回真
     * @return
     */
    public final static boolean isABC(String i_Str)
    {
        if ( Help.isNull(i_Str) )
        {
            return true;
        }
        
        return Pattern.matches("^[A-Za-z]*$" ,i_Str);
    }
    
    
    
    /**
     * 判断是否为数字
     * 
     * @param i_Str
     * @return
     */
    public final static boolean isNumber(String i_Str)
    {
        try
        {
            if ( Help.isNull(i_Str) )
            {
                return false;
            }
            
            Double.parseDouble(i_Str);
            return true;
        }
        catch(Exception e)
        {
            return false;
        }
    }
    
    
    
    /**
     * 判断是否只由英文、数字(0~9)和下划线组成
     * 
     * @param i_Str   为空时返回真
     * @return
     */
    public final static boolean isABCNumber(String i_Str)
    {
        if ( Help.isNull(i_Str) )
        {
            return true;
        }
        
        return Pattern.matches("^\\w*$" ,i_Str);
    }
    
    
    
    /**
     * 判断是否为中文
     * 
     * @param i_Char
     * @return
     */
    public final static boolean isChinese(char i_Char)
    {
        Character.UnicodeBlock v_UB = Character.UnicodeBlock.of(i_Char);
        
        if ( v_UB == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
          || v_UB == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
          || v_UB == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
          || v_UB == Character.UnicodeBlock.GENERAL_PUNCTUATION
          || v_UB == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
          || v_UB == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS )
        {
            return true;
        }
        
        return false;
    }



    /**
     * 判断是否为中文。只要有一个中文，即返回真
     * 
     * @param i_Str
     * @return
     */
    public final static boolean isChinese(String i_Str)
    {
        if ( null == i_Str )
        {
            return false;
        }
        
        char [] v_CharArr = i_Str.toCharArray();
        for (int i = 0; i < v_CharArr.length; i++)
        {
            char v_OneChar = v_CharArr[i];
            if ( isChinese(v_OneChar) )
            {
                return true;
            }
        }
        
        return false;
    }
    
    
    
    /**
     * 判断是否全部为中文。
     * 
     * @param i_Str
     * @return
     */
    public final static boolean isChineseAll(String i_Str)
    {
        if ( null == i_Str )
        {
            return false;
        }
        
        char [] v_CharArr = i_Str.toCharArray();
        for (int i = 0; i < v_CharArr.length; i++)
        {
            char v_OneChar = v_CharArr[i];
            if ( !isChinese(v_OneChar) )
            {
                return false;
            }
        }
        
        return true;
    }
    
    
    
    /**
     * 判定是否包含多个关键字(包含任何一个关键字为true)
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-07-28
     * @version     v1.0
     *
     * @param i_Text            被查询的字符串
     * @param i_FindKeys        关键字组
     * @return
     */
    public final static boolean isContains(final String i_Text ,final String ... i_FindKeys)
    {
        return isContains(i_Text ,false ,false ,i_FindKeys);
    }
    
    
    
    /**
     * 判定是否包含多个关键字
     * 
     *   分为两种判定标准
     *     1. 包含全部关键字为true
     *     2. 包含任何一个关键字为true
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-07-28
     * @version     v1.0
     *
     * @param i_Text            被查询的字符串
     * @param i_IsAllContains   是否包含全部关键字为true
     * @param i_FindKeys        关键字组
     * @return
     */
    public final static boolean isContains(final String i_Text ,final boolean i_IsAllContains ,final String ... i_FindKeys)
    {
        return isContains(i_Text ,i_IsAllContains ,false ,i_FindKeys);
    }
    
    
    
    /**
     * 判定是否包含多个关键字
     * 
     *   分为两种判定标准
     *     1. 包含全部关键字为true
     *     2. 包含任何一个关键字为true
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-07-28
     * @version     v1.0
     *              v2.0  2018-03-22  添加：有先、后顺序匹配查找关键字的功能。
     *
     * @param i_Text            被查询的字符串
     * @param i_IsAllContains   是否包含全部关键字为true
     * @param i_IsHaveOrder     关键字有先、后顺序的。当i_IsAllContains为真时才有效。
     * @param i_FindKeys        关键字组
     * @return
     */
    public final static boolean isContains(final String i_Text ,final boolean i_IsAllContains ,final boolean i_IsHaveOrder ,final String ... i_FindKeys)
    {
        if ( Help.isNull(i_Text) || Help.isNull(i_FindKeys) )
        {
            return false;
        }
        
        if ( i_IsAllContains )
        {
            if ( i_IsHaveOrder )
            {
                // 关键字有先、后顺序的
                // 包含全部关键字为true
                int v_IndexOf = 0;
                
                for (String v_Key : i_FindKeys)
                {
                    if ( Help.isNull(v_Key) )
                    {
                        continue;
                    }
                    
                    v_IndexOf = i_Text.indexOf(v_Key ,v_IndexOf);
                    if ( v_IndexOf < 0 )
                    {
                        return false;
                    }
                    
                    v_IndexOf += v_Key.length();
                }
            }
            else
            {
                // 包含全部关键字为true
                for (String v_Key : i_FindKeys)
                {
                    if ( Help.isNull(v_Key) )
                    {
                        continue;
                    }
                    
                    if ( i_Text.indexOf(v_Key) < 0 )
                    {
                        return false;
                    }
                }
            }
            
            return true;
        }
        else
        {
            // 包含任何一个关键字为true
            for (String v_Key : i_FindKeys)
            {
                if ( Help.isNull(v_Key) )
                {
                    continue;
                }
                
                if ( i_Text.indexOf(v_Key) >= 0 )
                {
                    return true;
                }
            }
            
            return false;
        }
    }
    
    
    
    /**
     * 判定是否前缀匹配多个关键字(前缀匹配任何一个关键字为true)
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-09-18
     * @version     v1.0
     *
     * @param i_Text            被查询的字符串
     * @param i_FindKeys        关键字组
     * @return
     */
    public final static boolean isStartsWith(final String i_Text ,final String ... i_FindKeys)
    {
        return isStartsWith(i_Text ,false ,i_FindKeys);
    }
    
    
    
    /**
     * 判定是否前缀匹配多个关键字
     * 
     *   分为两种判定标准
     *     1. i_IsAllContains=true时，前缀匹配全部关键字为true
     *     2. i_IsAllContains=false时，前缀匹配任何一个关键字为true
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-09-18
     * @version     v1.0
     *
     * @param i_Text            被查询的字符串
     * @param i_IsAllContains   是否包含全部关键字为true
     * @param i_FindKeys        关键字组
     * @return
     */
    public final static boolean isStartsWith(final String i_Text ,boolean i_IsAllContains ,final String ... i_FindKeys)
    {
        if ( Help.isNull(i_Text) || Help.isNull(i_FindKeys) )
        {
            return false;
        }
        
        if ( i_IsAllContains )
        {
            // 包含全部关键字为true
            for (String v_Key : i_FindKeys)
            {
                if ( Help.isNull(v_Key) )
                {
                    continue;
                }
                
                if ( !i_Text.startsWith(v_Key) )
                {
                    return false;
                }
            }
            
            return true;
        }
        else
        {
            // 包含任何一个关键字为true
            for (String v_Key : i_FindKeys)
            {
                if ( Help.isNull(v_Key) )
                {
                    continue;
                }
                
                if ( i_Text.startsWith(v_Key) )
                {
                    return true;
                }
            }
            
            return false;
        }
    }
    
    
    
    /**
     * 判定是否后缀匹配多个关键字(后缀匹配任何一个关键字为true)
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-09-18
     * @version     v1.0
     *
     * @param i_Text            被查询的字符串
     * @param i_FindKeys        关键字组
     * @return
     */
    public final static boolean isEndsWith(final String i_Text ,final String ... i_FindKeys)
    {
        return isEndsWith(i_Text ,false ,i_FindKeys);
    }
    
    
    
    /**
     * 判定是否后缀匹配多个关键字
     * 
     *   分为两种判定标准
     *     1. i_IsAllContains=true时，后缀匹配全部关键字为true
     *     2. i_IsAllContains=false时，后缀匹配任何一个关键字为true
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-09-18
     * @version     v1.0
     *
     * @param i_Text            被查询的字符串
     * @param i_IsAllContains   是否包含全部关键字为true
     * @param i_FindKeys        关键字组
     * @return
     */
    public final static boolean isEndsWith(final String i_Text ,boolean i_IsAllContains ,final String ... i_FindKeys)
    {
        if ( Help.isNull(i_Text) || Help.isNull(i_FindKeys) )
        {
            return false;
        }
        
        if ( i_IsAllContains )
        {
            // 包含全部关键字为true
            for (String v_Key : i_FindKeys)
            {
                if ( Help.isNull(v_Key) )
                {
                    continue;
                }
                
                if ( !i_Text.endsWith(v_Key) )
                {
                    return false;
                }
            }
            
            return true;
        }
        else
        {
            // 包含任何一个关键字为true
            for (String v_Key : i_FindKeys)
            {
                if ( Help.isNull(v_Key) )
                {
                    continue;
                }
                
                if ( i_Text.endsWith(v_Key) )
                {
                    return true;
                }
            }
            
            return false;
        }
    }
    
    
    
    /**
     * 比较多个关键字，判定是否只少有一个相等
     * 
     * @author      ZhengWei(HY)
     * @createDate  2018-05-04
     * @version     v1.0
     *
     * @param i_Text            被查询的字符串
     * @param i_FindKeys        关键字组
     * @return
     */
    public final static boolean isEquals(final String i_Text ,final String ... i_FindKeys)
    {
        if ( Help.isNull(i_Text) || Help.isNull(i_FindKeys) )
        {
            return false;
        }
        
        for (String v_Key : i_FindKeys)
        {
            if ( i_Text.equals(v_Key) )
            {
                return true;
            }
        }
        
        return false;
    }
    
    
    
    /**
     * 不区分大小写的比较多个关键字，判定是否只少有一个相等
     * 
     * @author      ZhengWei(HY)
     * @createDate  2018-05-04
     * @version     v1.0
     *
     * @param i_Text            被查询的字符串
     * @param i_FindKeys        关键字组
     * @return
     */
    public final static boolean isEqualsIgnoreCase(final String i_Text ,final String ... i_FindKeys)
    {
        if ( Help.isNull(i_Text) || Help.isNull(i_FindKeys) )
        {
            return false;
        }
        
        for (String v_Key : i_FindKeys)
        {
            if ( i_Text.equalsIgnoreCase(v_Key) )
            {
                return true;
            }
        }
        
        return false;
    }
    
    
    
    /**
     * 两个字符串间的 "异或" 运算 -- 主要用于立体层面的字符串融合
     * 
     * 数学 "异或" 算法为：同为0，异为1
     *    0⊕0=0，1⊕0=1，0⊕1=1，1⊕1=0
     * 
     * 两个字符间的 "异或" 算法为：
     *    相同字符⊕相同字符=字符，字符A⊕字符B=空格，字符⊕空格=字符，空格⊕字符=字符，空格⊕空格=空格
     * 
     * @param i_TopString     上面字符串
     * @param i_ButtomString  下面字符串
     * @return
     */
    public final static String xor(final String i_TopString ,final String i_ButtomString)
    {
        StringBuilder v_Buffer = new StringBuilder();
        int           v_Diff   = i_ButtomString.length() - i_TopString.length();
        
        for (int i=0; i<i_TopString.length(); i++)
        {
            String v_01 = i_TopString.substring(i ,i + 1);
            
            if ( i < i_ButtomString.length() )
            {
                String v_02 = i_ButtomString.substring(i ,i + 1);
                
                if ( " ".equals(v_01) )
                {
                    v_Buffer.append(v_02);
                }
                else if ( " ".equals(v_02) )
                {
                    v_Buffer.append(v_01);
                }
                else if ( v_01.equals(v_02) )
                {
                    v_Buffer.append(v_01);
                }
                else
                {
                    v_Buffer.append(" ");
                }
            }
            else
            {
                v_Buffer.append(v_01);
            }
        }
        
        if ( v_Diff > 0 )
        {
            v_Buffer.append(i_ButtomString.substring(i_TopString.length()));
        }
        
        return v_Buffer.toString();
    }
    
    
    
    /**
     * 解释关系
     * 
     * @param i_Info
     * @return
     */
    public final static RelationList<String> relationParser(String i_Info)
    {
        Return<List<SplitSegment>> v_Return_1  = StringHelp.Split($Method ,i_Info ,false);
        Return<List<SplitSegment>> v_Return_2  = null;
        RelationList<String>       v_Relations = null;
        
        if ( !Help.isNull(v_Return_1.paramStr) )
        {
            v_Return_2  = StringHelp.Split($Parentheses ,v_Return_1 ,true);
            v_Relations = RelationList.parser(v_Return_2);
        }
        else
        {
            v_Relations = RelationList.parser(v_Return_1);
        }
        
        return v_Relations;
    }
    
    
    
    /**
     * 解释占位符。:xx （保持占位符原顺序不变）
     * 
     * Map.key    为占位符。前缀为:符号。不包含:符号，同时也是分区字段
     * Map.Value  为占位符在原文本信息的顺序的集合（下标从0开始）
     * 
     * @author      ZhengWei(HY)
     * @createDate  2012-10-30
     * @version     v1.0
     *              v2.0  2014-07-30
     *              v3.0  2015-12-10  支持 :A.B.C 的解释（对点.的解释）。
     *              v4.0  2018-05-16  添加：支持中文占位符
     *              v5.0  2019-03-13  添加：占位符命名是否要求严格的规则
     *              v6.0  2020-06-08  修改：Map.value 修改为顺序。
     *                                修改：将返回结果的Map结构，改为 TablePartitionLink 结构。
     *
     * @param i_Placeholders
     * @param i_StrictRules   占位符命名是否要求严格的规则。
     * @return
     */
    public final static PartitionMap<String ,Integer> parsePlaceholdersSequence(String i_Placeholders)
    {
        return parsePlaceholdersSequence(i_Placeholders ,false);
    }
    
    
    
    /**
     * 解释占位符。:xx （保持占位符原顺序不变）
     * 
     * Map.key    为占位符。前缀为:符号。不包含:符号，同时也是分区字段
     * Map.Value  为占位符在原文本信息的顺序的集合（下标从0开始）
     * 
     * @author      ZhengWei(HY)
     * @createDate  2012-10-30
     * @version     v1.0
     *              v2.0  2014-07-30
     *              v3.0  2015-12-10  支持 :A.B.C 的解释（对点.的解释）。
     *              v4.0  2018-05-16  添加：支持中文占位符
     *              v5.0  2019-03-13  添加：占位符命名是否要求严格的规则
     *              v6.0  2020-06-08  修改：Map.value 修改为顺序。
     *                                修改：将返回结果的Map结构，改为 TablePartitionLink 结构。
     *
     * @param i_Placeholders
     * @param i_StrictRules   占位符命名是否要求严格的规则。
     * @return
     */
    public final static PartitionMap<String ,Integer> parsePlaceholdersSequence(String i_Placeholders ,boolean i_StrictRules)
    {
        // 匹配占位符
        List<SplitSegment>                  v_Segments = StringHelp.SplitOnlyFind("[ (,='%_\\s]?:[\\w\\.\\u4e00-\\u9fa5]+[ ),='%_\\s]?" ,i_Placeholders);
        TablePartitionLink<String ,Integer> v_Ret      = new TablePartitionLink<String ,Integer>();
        int                                 v_Index    = 0;
        
        for (SplitSegment v_Segment : v_Segments)
        {
            String v_PlaceHolder = StringHelp.SplitOnlyFind(":[\\w\\.\\u4e00-\\u9fa5]+" ,v_Segment.getInfo().trim()).get(0).getInfo();
            v_PlaceHolder = v_PlaceHolder.substring(1);
            
            if ( i_StrictRules )
            {
                // 严格规则：占位符的命名，不能是小于等于2位的纯数字
                //            防止将类似于时间格式 00:00:00 的字符解释为占位符
                if ( v_PlaceHolder.length() <= 2 )
                {
                    if ( Help.isNumber(v_PlaceHolder) )
                    {
                        continue;
                    }
                }
            }
            
            v_Ret.putRow(v_PlaceHolder ,v_Index++);
        }
        
        return v_Ret;
    }
    
    
    
    /**
     * 解释占位符。:xx
     * 
     *   placeholders属性为有降序排序顺序的LinkedMap。
     *   用于解决 :A、:AA 同时存在时的混乱。
     * 
     * Map.key    为占位符。前缀为:符号
     * Map.Value  为占位符原文本信息
     * 
     * @author      ZhengWei(HY)
     * @createDate  2014-10-08
     * @version     v1.0
     *
     * @param i_Placeholders
     * @return
     */
    public final static PartitionMap<String ,Integer> parsePlaceholders(String i_Placeholders)
    {
        return Help.toReverse(parsePlaceholdersSequence(i_Placeholders));
    }
    
    
    
    /**
     * 解释占位符。:xx
     * 
     *   placeholders属性为有降序排序顺序的LinkedMap。
     *   用于解决 :A、:AA 同时存在时的混乱。
     * 
     * Map.key    为占位符。前缀为:符号
     * Map.Value  为占位符原文本信息
     * 
     * @author      ZhengWei(HY)
     * @createDate  2014-10-08
     * @version     v1.0
     *              v2.0  2019-03-15  添加：占位符命名是否要求严格的规则
     *
     * @param i_Placeholders
     * @param i_StrictRules
     * @return
     */
    public final static PartitionMap<String ,Integer> parsePlaceholders(String i_Placeholders ,boolean i_StrictRules)
    {
        return Help.toReverse(parsePlaceholdersSequence(i_Placeholders ,i_StrictRules));
    }
    
    
    
    /**
     * 通过指定正则表达式对信息分割后的重新组合
     * 
     * 即分割的反向操作
     * 
     * 支持复杂分割的情况，对其进行递归组合
     * 
     * @param i_Splits     分割信息(主用于递归分割)
     * @return
     */
    public final static String SplitGroup(final Return<List<SplitSegment>> i_Splits)
    {
        if ( Help.isNull(i_Splits.paramObj) )
        {
            return i_Splits.paramStr;
        }
        else
        {
            return xor(SplitGroup(i_Splits.paramObj) ,i_Splits.paramStr);
        }
    }
  
    
    
    /**
     * 通过指定正则表达式对信息分割后的重新组合
     * 
     * 即分割的反向操作
     * 
     * 支持复杂分割的情况，对其进行递归组合
     * 
     * @param i_Splits   分割信息
     * @return
     */
    public final static String SplitGroup(final List<SplitSegment> i_Splits)
    {
        if ( Help.isNull(i_Splits) )
        {
            return "";
        }
        
        String v_Buffer = "";
        for (SplitSegment v_Split : i_Splits)
        {
            if ( v_Split.getChildSize() >= 1 )
            {
                String v_ChildGroupInfo = SplitGroup(v_Split.getChilds());
                v_Buffer = xor(v_Buffer ,v_ChildGroupInfo);
            }
            
            v_Buffer = xor(v_Buffer ,StringHelp.lpad("" ,v_Split.getBeginIndex() ," ") + v_Split.getInfo());
        }
        
        return v_Buffer;
    }
    
    
    
    /**
     * 通过指定正则表达式对信息分割后的输出有层次结构的日志
     * 
     * 即分割的反向操作
     * 
     * 支持复杂分割的情况，对其进行递归输出日志
     * 
     * @param i_Splits     分割信息
     */
    public static void SplitLog(final Return<List<SplitSegment>> i_Splits)
    {
        SplitLog(i_Splits ,System.out);
    }
    
    
    
    /**
     * 通过指定正则表达式对信息分割后的输出有层次结构的日志
     * 
     * 即分割的反向操作
     * 
     * 支持复杂分割的情况，对其进行递归输出日志
     * 
     * @param i_Splits     分割信息
     * @param i_Out        输出日志对象
     */
    public static void SplitLog(final Return<List<SplitSegment>> i_Splits ,final PrintStream i_Out)
    {
        SplitLog(i_Splits.paramObj ,1 ,i_Splits.paramStr.length() - 1 ,i_Out);
        
        StringBuilder v_Buffer = new StringBuilder();
        
        v_Buffer.append("L")   .append(StringHelp.lpad(0 ,2 ,"0"));
        v_Buffer.append(" ,C") .append(StringHelp.lpad(1 ,2 ,"0"));
        v_Buffer.append(" ： [");
        v_Buffer.append(i_Splits.paramStr);
        v_Buffer.append("]");
        
        i_Out.println(v_Buffer.toString());
    }
    
    
    
    /**
     * 通过指定正则表达式对信息分割后的输出有层次结构的日志
     * 
     * 即分割的反向操作
     * 
     * 支持复杂分割的情况，对其进行递归输出日志
     * 
     * @param i_Splits       分割信息
     */
    public static void SplitLog(final List<SplitSegment> i_Splits)
    {
        SplitLog(i_Splits ,1 ,0 ,System.out);
    }
    
    
    
    /**
     * 通过指定正则表达式对信息分割后的输出有层次结构的日志
     * 
     * 即分割的反向操作
     * 
     * 支持复杂分割的情况，对其进行递归输出日志
     * 
     * @param i_Splits       分割信息
     * @param i_Out          输出日志对象
     */
    public static void SplitLog(final List<SplitSegment> i_Splits ,final PrintStream i_Out)
    {
        SplitLog(i_Splits ,1 ,0 ,i_Out);
    }
    
    
    
    /**
     * 通过指定正则表达式对信息分割后的输出有层次结构的日志
     * 
     * 即分割的反向操作
     * 
     * 支持复杂分割的情况，对其进行递归输出日志
     * 
     * @param i_Splits       分割信息
     * @param i_Level        层次级别(最小下标从 1 开始)
     * @param i_MaxEndIndex  最大索引号
     * @param i_Out          输出日志对象
     */
    private final static void SplitLog(final List<SplitSegment> i_Splits ,final int i_Level ,final int i_MaxEndIndex ,final PrintStream i_Out)
    {
        if ( Help.isNull(i_Splits) )
        {
            return;
        }
        
        int v_i_MaxEndIndex = i_MaxEndIndex;
        if ( i_MaxEndIndex <= 0 )
        {
            v_i_MaxEndIndex = i_Splits.get(i_Splits.size() - 1).getEndIndex();
        }
        
        for (int i=1; i<=i_Splits.size(); i++)
        {
            SplitSegment v_Split = i_Splits.get(i - 1);
            
            if ( v_Split.getChildSize() >= 1 )
            {
                SplitLog(v_Split.getChilds() ,i_Level + 1 ,v_i_MaxEndIndex ,i_Out);
                
                StringBuilder v_Buffer = new StringBuilder();
                
                v_Buffer.append("L")   .append(StringHelp.lpad(i_Level ,2 ,"0"));
                v_Buffer.append(" ,C") .append(StringHelp.lpad(i ,2 ,"0"));
                v_Buffer.append(" ： [");
                v_Buffer.append(StringHelp.lpad("" ,v_Split.getBeginIndex() ," "));
                v_Buffer.append(v_Split.getInfo());
                v_Buffer.append(StringHelp.lpad("" ,v_i_MaxEndIndex - v_Split.getEndIndex() ," "));
                if ( v_i_MaxEndIndex > v_Split.getEndIndex() )
                {
                    v_Buffer.append(" ");
                }
                v_Buffer.append("]");
                
                i_Out.println(v_Buffer.toString());
            }
        }
        
        
        for (int i=1; i<=i_Splits.size(); i++)
        {
            SplitSegment v_Split = i_Splits.get(i - 1);
            
            if ( v_Split.getChildSize() <= 0 )
            {
                SplitLog(v_Split.getChilds() ,i_Level + 1 ,v_i_MaxEndIndex ,i_Out);
                
                StringBuilder v_Buffer = new StringBuilder();
                
                v_Buffer.append("L")   .append(StringHelp.lpad(i_Level ,2 ,"0"));
                v_Buffer.append(" ,C") .append(StringHelp.lpad(i ,2 ,"0"));
                v_Buffer.append(" ： [");
                v_Buffer.append(StringHelp.lpad("" ,v_Split.getBeginIndex() ," "));
                v_Buffer.append(v_Split.getInfo());
                v_Buffer.append(StringHelp.lpad("" ,v_i_MaxEndIndex - v_Split.getEndIndex() ," "));
                if ( v_i_MaxEndIndex > v_Split.getEndIndex() )
                {
                    v_Buffer.append(" ");
                }
                v_Buffer.append("]");
                
                i_Out.println(v_Buffer.toString());
            }
        }
    }
    
    
    
    /**
     * 通过指定正则表达式对信息二次分割(只要匹配的文本信息)
     * 
     * @param i_Pattern      正则表达式
     * @param i_Info         信息
     * @param i_Recursion    是否递归分割
     * @return               Return.paramObj 为二次分割信息集合 List<SplitSegment>
     *                       Return.paramStr 为二次分割后，排除分割信息后，组成的新文本
     *                       Return.paramInt 为递归分割的层次或深度
     */
    public final static Return<List<SplitSegment>> SplitOnlyFind(final String i_Pattern ,final String i_Info ,boolean i_Recursion)
    {
        Return<List<SplitSegment>> v_Ret = Split(i_Pattern ,i_Info ,i_Recursion);
     
        SplitOnly_Remove(v_Ret.paramObj ,InfoType.$TextInfo ,i_Recursion);
        
        return v_Ret;
    }
    
    
    
    /**
     * 通过指定正则表达式对信息二次分割(只要普通文本信息)
     * 
     * @param i_Pattern      正则表达式
     * @param i_Info         信息
     * @param i_Recursion    是否递归分割
     * @return               Return.paramObj 为二次分割信息集合 List<SplitSegment>
     *                       Return.paramStr 为二次分割后，排除分割信息后，组成的新文本
     *                       Return.paramInt 为递归分割的层次或深度
     */
    public final static Return<List<SplitSegment>> SplitOnlyNot(final String i_Pattern ,final String i_Info ,boolean i_Recursion)
    {
        Return<List<SplitSegment>> v_Ret = Split(i_Pattern ,i_Info ,i_Recursion);
     
        SplitOnly_Remove(v_Ret.paramObj ,InfoType.$FindInfo ,i_Recursion);
        
        return v_Ret;
    }
    
    
    
    /**
     * 删除分割集合中不想要的东东
     * 
     * 当只要 $FindInfo 时，已子分割集合含有 $FindInfo 时，但父分割集合为 $TextInfo 时，也会被删除的
     * 
     * @param io_Splits
     * @param i_RemoveType  删除类型
     * @param i_Recursion   是否递归删除
     */
    private final static void SplitOnly_Remove(List<SplitSegment> io_Splits ,final InfoType i_RemoveType ,final boolean i_Recursion)
    {
        if ( Help.isNull(io_Splits) )
        {
            return;
        }
        
        for (int i=io_Splits.size()-1; i>=0; i--)
        {
            if ( io_Splits.get(i).getInfoType() == i_RemoveType )
            {
                io_Splits.remove(i);
            }
            else
            {
                if ( i_Recursion )
                {
                    SplitOnly_Remove(io_Splits.get(i).getChilds() ,i_RemoveType ,i_Recursion);
                }
            }
        }
    }
    
    
    
    /**
     * 通过指定正则表达式对信息二次分割
     * 
     * @param i_Pattern      正则表达式
     * @param i_Info         信息
     * @param i_Recursion    是否递归分割
     * @return               Return.paramObj 为二次分割信息集合 List<SplitSegment>
     *                       Return.paramStr 为二次分割后，排除分割信息后，组成的新文本
     *                       Return.paramInt 为递归分割的层次或深度
     */
    public final static Return<List<SplitSegment>> Split(final String i_Pattern ,final String i_Info ,final boolean i_Recursion)
    {
        Return<List<SplitSegment>> v_Param = new Return<List<SplitSegment>>();
        
        v_Param.paramStr = i_Info;
        
        return Split(i_Pattern ,v_Param ,i_Recursion);
    }
    
    
    
    /**
     * 通过指定正则表达式对信息二次分割
     * 
     * @param i_Pattern      正则表达式
     * @param i_ChildSplits  上次分割信息集合(Return.paramObj可为空。当为空时表示首次分割，但Return.paramStr不可为空)
     * @return               Return.paramObj 为二次分割信息集合 List<SplitSegment>
     *                       Return.paramStr 为二次分割后，排除分割信息后，组成的新文本
     */
    public final static Return<List<SplitSegment>> Split(final String i_Pattern ,Return<List<SplitSegment>> io_ChildSplits)
    {
        return Split(i_Pattern ,io_ChildSplits ,false);
    }
    
    
    
    /**
     * 通过指定正则表达式对信息二次分割
     * 
     * @param i_Pattern       正则表达式
     * @param io_ChildSplits  上次分割信息集合(可为空。当为空时表示首次分割)
     * @param i_Recursion     是否递归分割
     * @return                Return.paramObj 为二次分割信息集合 List<SplitSegment>
     *                        Return.paramStr 为二次分割后，排除分割信息后，组成的新文本
     *                        Return.paramInt 为递归分割的层次或深度(最小下标从 1 开始。 0 表示没有匹配信息没有层次)
     */
    public final static Return<List<SplitSegment>> Split(final String i_Pattern ,Return<List<SplitSegment>> io_ChildSplits ,final boolean i_Recursion)
    {
        if ( io_ChildSplits == null )
        {
            throw new NullPointerException("ChildSplits is null.");
        }
        
        if ( io_ChildSplits.paramStr == null )
        {
            throw new NullPointerException("ChildSplits.paramStr is null.");
        }
        
        
        Return<List<SplitSegment>> v_Return   = new Return<List<SplitSegment>>(true);
        List<SplitSegment>         v_MySplits = Split(i_Pattern ,io_ChildSplits.paramStr);
        StringBuilder              v_Buffer   = new StringBuilder();
        
        v_Return.paramInt = io_ChildSplits.paramInt + 1;
        
        if ( v_MySplits.size() == 1 && v_MySplits.get(0).getInfo().equals(io_ChildSplits.paramStr) )
        {
            if ( v_MySplits.get(0).getInfoType() == InfoType.$FindInfo )
            {
                v_Return.paramObj = v_MySplits;
                v_Return.paramStr = StringHelp.lpad("" ,io_ChildSplits.paramStr.length() ," ");
                return v_Return;
            }
            else
            {
                return io_ChildSplits;
            }
        }
        
        if ( Help.isNull(io_ChildSplits.paramObj) )
        {
            List<Integer> v_RemoveIndexs = new ArrayList<Integer>();
            
            for (int v_Index=0; v_Index<v_MySplits.size(); v_Index++)
            {
                SplitSegment v_Split = v_MySplits.get(v_Index);
                
                if ( v_Split.getInfoType() == InfoType.$FindInfo )
                {
                    v_Buffer.append(StringHelp.lpad("" ,v_Split.getInfo().length() ," "));
                }
                else
                {
                    v_Buffer.append(v_Split.getInfo());
                    v_RemoveIndexs.add(v_Index);
                }
            }
            
            // 删除不匹配的信息
            if ( !Help.isNull(v_RemoveIndexs) )
            {
                for (int i=v_RemoveIndexs.size()-1; i>=0; i--)
                {
                    v_MySplits.remove(v_RemoveIndexs.get(i).intValue());
                }
                
                v_RemoveIndexs.clear();
                v_RemoveIndexs = null;
            }
        }
        else
        {
            int           v_ChildAddCount = 0;
            int           v_MaxEndIndex   = 0;
            StringBuilder v_BeforeBuffer  = new StringBuilder();         // 在 v_IsHaveChild 变为true之前的信息
            List<Integer> v_BeforeIndexs  = new ArrayList<Integer>();   // 在 v_IsHaveChild 变为true之前的信息
            List<Integer> v_RemoveIndexs  = new ArrayList<Integer>();
            
            // 当为二次分割时，建立与上次分割的关系
            for (int v_Index=0; v_Index<v_MySplits.size(); v_Index++)
            {
                SplitSegment v_Split = v_MySplits.get(v_Index);
                
                if ( v_Split.getInfoType() == InfoType.$FindInfo )
                {
                    boolean v_IsHaveChild = false;
                    for (SplitSegment v_ChildSplit : io_ChildSplits.paramObj)
                    {
                        if ( v_ChildSplit.getBeginIndex() > v_Split.getBeginIndex()
                          && v_ChildSplit.getEndIndex()   < v_Split.getEndIndex() )
                        {
                            v_Split.getChilds().add(v_ChildSplit);
                            v_IsHaveChild = true;
                            if ( v_MaxEndIndex < v_Split.endIndex )
                            {
                                v_MaxEndIndex = v_Split.endIndex;
                            }
                        }
                    }
                    
                    if ( v_IsHaveChild )
                    {
                        if ( v_BeforeIndexs.size() > 0 )
                        {
                            v_BeforeIndexs.addAll(v_RemoveIndexs);
                            v_RemoveIndexs = v_BeforeIndexs;
                            String v_XORBuffer = xor(v_Buffer.toString() ,v_BeforeBuffer.toString());
                            v_Buffer       = new StringBuilder();
                            v_BeforeBuffer = new StringBuilder();
                            v_Buffer.append(v_XORBuffer);
                        }
                        
                        v_Buffer.append(StringHelp.lpad("" ,v_Split.getInfo().length() ," "));
                        v_ChildAddCount++;
                    }
                    else if ( v_ChildAddCount == 0 )
                    {
                        v_Buffer.append(StringHelp.lpad("" ,v_Split.getInfo().length() ," "));
                        v_BeforeBuffer.append(v_Split.getInfo());
                        v_BeforeIndexs.add(v_Index);
                    }
                    else
                    {
                        // 本次()括号搜索已包含已括号，那其后的括号与本次括号就不在同一层级上
                        v_Buffer.append(v_Split.getInfo());
                        v_RemoveIndexs.add(v_Index);
                    }
                }
                else
                {
                    v_Buffer.append(v_Split.getInfo());
                    v_RemoveIndexs.add(v_Index);
                }
            }
            
            // 删除不是同层次的
            if ( !Help.isNull(v_RemoveIndexs) )
            {
                for (int i=v_RemoveIndexs.size()-1; i>=0; i--)
                {
                    v_MySplits.remove(v_RemoveIndexs.get(i).intValue());
                }
                
                v_RemoveIndexs.clear();
                v_RemoveIndexs = null;
            }
            
            
            if ( v_ChildAddCount == 0 )
            {
                // 测试代码(1/2)：打印出每一层分割后，排除本层分割信息后，组成的新文本的文本
                // System.out.println("(1/2)[" + v_Buffer.toString() + "]");
                
                io_ChildSplits.paramObj.addAll(v_MySplits);
                io_ChildSplits.paramStr = v_Buffer.toString();
                io_ChildSplits.paramInt = io_ChildSplits.paramInt + 1;
                return Split(i_Pattern ,io_ChildSplits ,i_Recursion);
            }
            else if ( v_ChildAddCount == 1 )
            {
                // 判断父级是否还被()括号包含着
                // 如果父级上没有()括号了，那么其后的括号与本次括号属于同一层级的
                List<SplitSegment> v_FatherFinds  = SplitOnlyFind(i_Pattern ,v_Buffer.toString());
                boolean            v_IsHaveFather = false;
                
                for (SplitSegment v_FatherFind : v_FatherFinds)
                {
                    if ( v_FatherFind.getBeginIndex() <= v_MaxEndIndex && v_MaxEndIndex < v_FatherFind.getEndIndex()  )
                    {
                        v_IsHaveFather = true;
                        break;
                    }
                }
                
                if ( !v_IsHaveFather )
                {
                    v_Return.paramInt = io_ChildSplits.paramInt;
                    
                    // 下一个循环会到 "测试代码(1/2)" 地方
                }
            }
        }
        

        v_Return.paramObj = v_MySplits;
        v_Return.paramStr = v_Buffer.toString();
        
        // 测试代码(2/2)：打印出每一层分割后，排除本层分割信息后，组成的新文本的文本
        // System.out.println("(2/2)[" + v_Return.paramStr + "]");
        
        if ( i_Recursion && v_Return.paramObj.size() >= 1 && !Help.isNull(v_Return.paramStr) )
        {
            return Split(i_Pattern ,v_Return ,i_Recursion);
        }
        else
        {
            return v_Return;
        }
    }
    
    
    
    /**
     * 通过指定正则表达式对信息分割
     * 
     * @param i_Pattern   正则表达式
     * @param i_Info      信息
     * @return
     */
    public final static List<SplitSegment> Split(final String i_Pattern ,final String i_Info)
    {
        return Split(i_Pattern ,i_Info ,3);
    }
    
    
    
    /**
     * 通过指定正则表达式对信息分割(只要匹配的文本信息)
     * 
     * @param i_Pattern   正则表达式
     * @param i_Info      信息
     * @return
     */
    public final static List<SplitSegment> SplitOnlyFind(final String i_Pattern ,final String i_Info)
    {
        return Split(i_Pattern ,i_Info ,2);
    }
    
    
    
    /**
     * 通过指定正则表达式对信息分割(只要普通文本信息)
     * 
     * @param i_Pattern   正则表达式
     * @param i_Info      信息
     * @return
     */
    public final static List<SplitSegment> SplitOnlyNot(final String i_Pattern ,final String i_Info)
    {
        return Split(i_Pattern ,i_Info ,1);
    }
    
    
    
    /**
     * 通过指定正则表达式对信息分割
     * 
     * @param i_Pattern   正则表达式
     * @param i_Info      信息
     * @param i_RetType   返回类型   1：只要普通文本信息
     *                             2：只要匹配的文本信息
     *                             3：普通文本信息和匹配的文本信息
     * @return
     */
    private final static List<SplitSegment> Split(final String i_Pattern ,final String i_Info ,final int i_RetType)
    {
        List<SplitSegment>  v_Ret            = new ArrayList<SplitSegment>();
        Pattern             v_Pattern        = Pattern.compile(i_Pattern);
        Matcher             v_Matcher        = v_Pattern.matcher(i_Info);
        int                 v_MatcheEndIndex = 0;
        
        while ( v_Matcher.find() )
        {
            int    v_StartIndex = v_Matcher.start();
            int    v_EndIndex   = v_Matcher.end();
            String v_MetcheStr  = i_Info.substring(v_StartIndex ,v_EndIndex);
            String v_SQLSplit   = i_Info.substring(v_MatcheEndIndex ,v_Matcher.end() - v_Matcher.group().length());
            
            if ( !"".equals(v_SQLSplit) )
            {
                if ( (i_RetType & 1) == 1 )
                {
                    v_Ret.add(new SplitSegment(v_SQLSplit  ,v_MatcheEndIndex ,v_Matcher.end() - v_Matcher.group().length()));
                }
            }
            
            if ( (i_RetType & 2) == 2 )
            {
                v_Ret.add(new SplitSegment(v_MetcheStr ,v_StartIndex ,v_EndIndex ,InfoType.$FindInfo));
            }
            
            v_MatcheEndIndex = v_EndIndex;
        }
        
        if ( v_MatcheEndIndex < i_Info.length() )
        {
            if ( (i_RetType & 1) == 1 )
            {
                v_Ret.add(new SplitSegment(i_Info.substring(v_MatcheEndIndex) ,v_MatcheEndIndex ,i_Info.length() - 1));
            }
        }
        
        
        // 测试输出信息
        /*
        for (SplitSegment v_SplitSegment : v_Ret)
        {
            System.out.println(StringHelp.lpad(v_SplitSegment.getBeginIndex() ,3 ," ") + " - "
                             + StringHelp.lpad(v_SplitSegment.getEndIndex()   ,3 ," ") + " "
                             + v_SplitSegment.getInfoType()
                             + "  Match  [" + v_SplitSegment.getInfo() + "]");
        }
        */
        
        return v_Ret;
    }
    
    
    
    /**
     * 编程语言的基本数据类型的转字符串。可以配合 Help.toObject() 等方法使用，实现字符串形式的序列化和反序列化
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-09-27
     * @version     v1.0
     * 
     * @param i_Data
     * @return
     */
    public final static String toString(String i_Data)
    {
        return i_Data;
    }
    
    
    
    /**
     * 编程语言的基本数据类型的转字符串。可以配合 Help.toObject() 等方法使用，实现字符串形式的序列化和反序列化
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-09-27
     * @version     v1.0
     * 
     * @param i_Data
     * @return
     */
    public final static String toString(Integer i_Data)
    {
        return String.valueOf(i_Data);
    }
    
    
    
    /**
     * 编程语言的基本数据类型的转字符串。可以配合 Help.toObject() 等方法使用，实现字符串形式的序列化和反序列化
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-09-27
     * @version     v1.0
     * 
     * @param i_Data
     * @return
     */
    public final static String toString(int i_Data)
    {
        return String.valueOf(i_Data);
    }
    
    
    
    /**
     * 编程语言的基本数据类型的转字符串。可以配合 Help.toObject() 等方法使用，实现字符串形式的序列化和反序列化
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-09-27
     * @version     v1.0
     * 
     * @param i_Data
     * @return
     */
    public final static String toString(Double i_Data)
    {
        return String.valueOf(i_Data);
    }
    
    
    
    /**
     * 编程语言的基本数据类型的转字符串。可以配合 Help.toObject() 等方法使用，实现字符串形式的序列化和反序列化
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-09-27
     * @version     v1.0
     * 
     * @param i_Data
     * @return
     */
    public final static String toString(double i_Data)
    {
        return String.valueOf(i_Data);
    }
    
    
    
    /**
     * 编程语言的基本数据类型的转字符串。可以配合 Help.toObject() 等方法使用，实现字符串形式的序列化和反序列化
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-09-27
     * @version     v1.0
     * 
     * @param i_Data
     * @return
     */
    public final static String toString(Float i_Data)
    {
        return String.valueOf(i_Data);
    }
    
    
    
    /**
     * 编程语言的基本数据类型的转字符串。可以配合 Help.toObject() 等方法使用，实现字符串形式的序列化和反序列化
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-09-27
     * @version     v1.0
     * 
     * @param i_Data
     * @return
     */
    public final static String toString(float i_Data)
    {
        return String.valueOf(i_Data);
    }
    
    
    
    /**
     * 编程语言的基本数据类型的转字符串。可以配合 Help.toObject() 等方法使用，实现字符串形式的序列化和反序列化
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-09-27
     * @version     v1.0
     * 
     * @param i_Data
     * @return
     */
    public final static String toString(Boolean i_Data)
    {
        return String.valueOf(i_Data);
    }
    
    
    
    /**
     * 编程语言的基本数据类型的转字符串。可以配合 Help.toObject() 等方法使用，实现字符串形式的序列化和反序列化
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-09-27
     * @version     v1.0
     * 
     * @param i_Data
     * @return
     */
    public final static String toString(boolean i_Data)
    {
        return String.valueOf(i_Data);
    }
    
    
    
    /**
     * 编程语言的基本数据类型的转字符串。可以配合 Help.toObject() 等方法使用，实现字符串形式的序列化和反序列化
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-09-27
     * @version     v1.0
     * 
     * @param i_Data
     * @return
     */
    public final static String toString(Long i_Data)
    {
        return String.valueOf(i_Data);
    }
    
    
    
    /**
     * 编程语言的基本数据类型的转字符串。可以配合 Help.toObject() 等方法使用，实现字符串形式的序列化和反序列化
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-09-27
     * @version     v1.0
     * 
     * @param i_Data
     * @return
     */
    public final static String toString(long i_Data)
    {
        return String.valueOf(i_Data);
    }
    
    
    
    /**
     * 编程语言的基本数据类型的转字符串。可以配合 Help.toObject() 等方法使用，实现字符串形式的序列化和反序列化
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-09-27
     * @version     v1.0
     * 
     * @param i_Data
     * @return
     */
    public final static String toString(Date i_Data)
    {
        return String.valueOf(i_Data.getTime());
    }
    
    
    
    /**
     * 编程语言的基本数据类型的转字符串。可以配合 Help.toObject() 等方法使用，实现字符串形式的序列化和反序列化
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-09-27
     * @version     v1.0
     * 
     * @param i_Data
     * @return
     */
    public final static String toString(java.util.Date i_Data)
    {
        return String.valueOf(i_Data.getTime());
    }
    
    
    
    /**
     * 编程语言的基本数据类型的转字符串。可以配合 Help.toObject() 等方法使用，实现字符串形式的序列化和反序列化
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-09-27
     * @version     v1.0
     * 
     * @param i_Data
     * @return
     */
    public final static String toString(java.sql.Date i_Data)
    {
        return String.valueOf(i_Data.getTime());
    }
    
    
    
    /**
     * 编程语言的基本数据类型的转字符串。可以配合 Help.toObject() 等方法使用，实现字符串形式的序列化和反序列化
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-09-27
     * @version     v1.0
     * 
     * @param i_Data
     * @return
     */
    public final static String toString(Timestamp i_Data)
    {
        return String.valueOf(i_Data.getTime());
    }
    
    
    
    /**
     * 编程语言的基本数据类型的转字符串。可以配合 Help.toObject() 等方法使用，实现字符串形式的序列化和反序列化
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-09-27
     * @version     v1.0
     * 
     * @param i_Data
     * @return
     */
    public final static String toString(BigDecimal i_Data)
    {
        return String.valueOf(i_Data);
    }
    
    
    
    /**
     * 编程语言的基本数据类型的转字符串。可以配合 Help.toObject() 等方法使用，实现字符串形式的序列化和反序列化
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-09-27
     * @version     v1.0
     * 
     * @param i_Data
     * @return
     */
    public final static String toString(Short i_Data)
    {
        return String.valueOf(i_Data);
    }
    
    
    
    /**
     * 编程语言的基本数据类型的转字符串。可以配合 Help.toObject() 等方法使用，实现字符串形式的序列化和反序列化
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-09-27
     * @version     v1.0
     * 
     * @param i_Data
     * @return
     */
    public final static String toString(short i_Data)
    {
        return String.valueOf(i_Data);
    }
    
    
    
    /**
     * 编程语言的基本数据类型的转字符串。可以配合 Help.toObject() 等方法使用，实现字符串形式的序列化和反序列化
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-09-27
     * @version     v1.0
     * 
     * @param i_Data
     * @return
     */
    public final static String toString(Byte i_Data)
    {
        return String.valueOf(i_Data);
    }
    
    
    
    /**
     * 编程语言的基本数据类型的转字符串。可以配合 Help.toObject() 等方法使用，实现字符串形式的序列化和反序列化
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-09-27
     * @version     v1.0
     * 
     * @param i_Data
     * @return
     */
    public final static String toString(byte i_Data)
    {
        return String.valueOf(i_Data);
    }
    
    
    
    /**
     * 编程语言的基本数据类型的转字符串。可以配合 Help.toObject() 等方法使用，实现字符串形式的序列化和反序列化
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-09-27
     * @version     v1.0
     * 
     * @param i_Data
     * @return
     */
    public final static String toString(Character i_Data)
    {
        return String.valueOf(i_Data);
    }
    
    
    
    /**
     * 编程语言的基本数据类型的转字符串。可以配合 Help.toObject() 等方法使用，实现字符串形式的序列化和反序列化
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-09-27
     * @version     v1.0
     * 
     * @param i_Data
     * @return
     */
    public final static String toString(char i_Data)
    {
        return String.valueOf(i_Data);
    }
    
    
    
    /**
     * 编程语言的基本数据类型的转字符串。可以配合 Help.toObject() 等方法使用，实现字符串形式的序列化和反序列化
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-09-27
     * @version     v1.0
     * 
     * @param i_Data
     * @return
     */
    public final static String toString(Class<?> i_Data)
    {
        return i_Data == null ? "" : i_Data.getName();
    }
    
    
    
    /**
     * 编程语言的基本数据类型的转字符串。可以配合 Help.toObject() 等方法使用，实现字符串形式的序列化和反序列化
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-09-27
     * @version     v1.0
     * 
     * @param i_Data
     * @return
     */
    public final static String toString(Object i_Data)
    {
        return String.valueOf(i_Data);
    }
    
    
    
    /**
     * 将集合转换为字符串。转换后的字符串有一种形式（自动判断形式）（按逗号分隔）
     *   形式2：1   ,2   ,3
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-02-22
     * @version     v1.0
     *
     * @param i_Datas
     * @return
     */
    public final static String toString(int[] i_Datas)
    {
        return toString(i_Datas ,""  ,",");
    }
    
    
    
    /**
     * 将集合转换为字符串。转换后的字符串有一种形式（自动判断形式）（按逗号分隔）
     *   形式2：1   ,2   ,3
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-02-22
     * @version     v1.0
     *
     * @param i_Datas
     * @return
     */
    public final static String toString(long[] i_Datas)
    {
        return toString(i_Datas ,""  ,",");
    }
    
    
    
    /**
     * 将集合转换为字符串。转换后的字符串有一种形式（自动判断形式）（按逗号分隔）
     *   形式2：1   ,2   ,3
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-02-22
     * @version     v1.0
     *
     * @param i_Datas
     * @return
     */
    public final static String toString(double[] i_Datas)
    {
        return toString(i_Datas ,""  ,",");
    }
    
    
    
    /**
     * 将集合转换为字符串。转换后的字符串有一种形式（自动判断形式）（按逗号分隔）
     *   形式2：1   ,2   ,3
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-02-22
     * @version     v1.0
     *
     * @param i_Datas
     * @return
     */
    public final static String toString(float[] i_Datas)
    {
        return toString(i_Datas ,""  ,",");
    }
    
    
    
    /**
     * 将集合转换为字符串。转换后的字符串有一种形式（自动判断形式）（按逗号分隔）
     *   形式2：1   ,2   ,3
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-02-22
     * @version     v1.0
     *
     * @param i_Datas
     * @return
     */
    public final static String toString(char[] i_Datas)
    {
        return toString(i_Datas ,""  ,",");
    }
    
    
    
    /**
     * 将集合转换为字符串。转换后的字符串有一种形式（自动判断形式）（按逗号分隔）
     *   形式2：1   ,2   ,3
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-02-22
     * @version     v1.0
     *
     * @param i_Datas
     * @return
     */
    public final static String toString(short[] i_Datas)
    {
        return toString(i_Datas ,""  ,",");
    }
    
    
    
    /**
     * 将集合转换为字符串。转换后的字符串有一种形式（自动判断形式）（按逗号分隔）
     *   形式2：1   ,2   ,3
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-02-22
     * @version     v1.0
     *
     * @param i_Datas
     * @return
     */
    public final static String toString(byte[] i_Datas)
    {
        return toString(i_Datas ,""  ,",");
    }
    
    
    
    /**
     * 将集合转换为字符串。转换后的字符串有两种形式（按逗号分隔）
     *   形式1：'A' ,'B' ,'C'
     *   形式2：1   ,2   ,3
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-02-22
     * @version     v1.0
     *
     * @param i_Datas
     * @param i_StringSign  字符串标示。如，单引号'
     * @return
     */
    public final static String toString(int[] i_Datas ,String i_StringSign)
    {
        return toString(i_Datas ,i_StringSign ,",");
    }
    
    
    
    /**
     * 将集合转换为字符串。转换后的字符串有两种形式（按逗号分隔）
     *   形式1：'A' ,'B' ,'C'
     *   形式2：1   ,2   ,3
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-02-22
     * @version     v1.0
     *
     * @param i_Datas
     * @param i_StringSign  字符串标示。如，单引号'
     * @return
     */
    public final static String toString(long[] i_Datas ,String i_StringSign)
    {
        return toString(i_Datas ,i_StringSign ,",");
    }
    
    
    
    /**
     * 将集合转换为字符串。转换后的字符串有两种形式（按逗号分隔）
     *   形式1：'A' ,'B' ,'C'
     *   形式2：1   ,2   ,3
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-02-22
     * @version     v1.0
     *
     * @param i_Datas
     * @param i_StringSign  字符串标示。如，单引号'
     * @return
     */
    public final static String toString(double[] i_Datas ,String i_StringSign)
    {
        return toString(i_Datas ,i_StringSign ,",");
    }
    
    
    
    /**
     * 将集合转换为字符串。转换后的字符串有两种形式（按逗号分隔）
     *   形式1：'A' ,'B' ,'C'
     *   形式2：1   ,2   ,3
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-02-22
     * @version     v1.0
     *
     * @param i_Datas
     * @param i_StringSign  字符串标示。如，单引号'
     * @return
     */
    public final static String toString(float[] i_Datas ,String i_StringSign)
    {
        return toString(i_Datas ,i_StringSign ,",");
    }
    
    
    
    /**
     * 将集合转换为字符串。转换后的字符串有两种形式（按逗号分隔）
     *   形式1：'A' ,'B' ,'C'
     *   形式2：1   ,2   ,3
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-02-22
     * @version     v1.0
     *
     * @param i_Datas
     * @param i_StringSign  字符串标示。如，单引号'
     * @return
     */
    public final static String toString(char[] i_Datas ,String i_StringSign)
    {
        return toString(i_Datas ,i_StringSign ,",");
    }
    
    
    
    /**
     * 将集合转换为字符串。转换后的字符串有两种形式（按逗号分隔）
     *   形式1：'A' ,'B' ,'C'
     *   形式2：1   ,2   ,3
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-02-22
     * @version     v1.0
     *
     * @param i_Datas
     * @param i_StringSign  字符串标示。如，单引号'
     * @return
     */
    public final static String toString(short[] i_Datas ,String i_StringSign)
    {
        return toString(i_Datas ,i_StringSign ,",");
    }
    
    
    
    /**
     * 将集合转换为字符串。转换后的字符串有两种形式（按逗号分隔）
     *   形式1：'A' ,'B' ,'C'
     *   形式2：1   ,2   ,3
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-02-22
     * @version     v1.0
     *
     * @param i_Datas
     * @param i_StringSign  字符串标示。如，单引号'
     * @return
     */
    public final static String toString(byte[] i_Datas ,String i_StringSign)
    {
        return toString(i_Datas ,i_StringSign ,",");
    }
    
    
    
    /**
     * 将集合转换为字符串。转换后的字符串有两种形式（自动判断形式）（按逗号分隔）
     *   形式1：'A' ,'B' ,'C'
     *   形式2：1   ,2   ,3
     * 
     * 自动判断形式
     *   1. 元素类型为字符串的，生成形式1，按单引号括起来
     *   2. 元素类型为数字类的，生成形式2
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-02-22
     * @version     v1.0
     *
     * @param i_Datas
     * @return
     */
    public final static String toString(Object[] i_Datas)
    {
        if ( !Help.isNull(i_Datas) )
        {
            Object v_FirstValue = i_Datas[0];
            if ( null == v_FirstValue )
            {
                return toString(i_Datas ,"'" ,",");
            }
            else if ( MethodReflect.isExtendImplement(v_FirstValue ,Number.class) )
            {
                return toString(i_Datas ,""  ,",");
            }
            else
            {
                return toString(i_Datas ,"'" ,",");
            }
        }
        else
        {
            return "";
        }
    }
    
    
    
    /**
     * 将集合转换为字符串。转换后的字符串有两种形式（按逗号分隔）
     *   形式1：'A' ,'B' ,'C'
     *   形式2：1   ,2   ,3
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-02-22
     * @version     v1.0
     *
     * @param i_Datas
     * @param i_StringSign  字符串标示。如，单引号'
     * @return
     */
    public final static String toString(Object[] i_Datas ,String i_StringSign)
    {
        return toString(i_Datas ,i_StringSign ,",");
    }
    
    
    
    /**
     * 将集合转换为字符串。转换后的字符串有两种形式
     *   形式1：'A' ,'B' ,'C'
     *   形式2：1   ,2   ,3
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-02-22
     * @version     v1.0
     *
     * @param i_Datas
     * @param i_StringSign  字符串标示。如，单引号'
     * @param i_Split       元素分隔符。如，逗号，
     * @return
     */
    public final static String toString(Object[] i_Datas ,String i_StringSign ,String i_Split)
    {
        StringBuilder v_Buffer     = new StringBuilder();
        String        v_StringSign = Help.NVL(i_StringSign);
        int           v_Count      = 0;
        
        if ( !Help.isNull(i_Datas) )
        {
            for (int v_Index=0; v_Index<i_Datas.length; v_Index++)
            {
                Object v_Value = i_Datas[v_Index];
                
                if ( null != v_Value )
                {
                    if ( v_Count >= 1 )
                    {
                        v_Buffer.append(i_Split);
                    }
                    
                    v_Buffer.append(v_StringSign).append(v_Value).append(v_StringSign);
                    v_Count++;
                }
            }
        }
        
        return v_Buffer.toString();
    }
    
    
    
    /**
     * 将集合转换为字符串。转换后的字符串有两种形式（自动判断形式）（按逗号分隔）
     *   形式1：'A' ,'B' ,'C'
     *   形式2：1   ,2   ,3
     * 
     * 自动判断形式
     *   1. 元素类型为字符串的，生成形式1，按单引号括起来
     *   2. 元素类型为数字类的，生成形式2
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-02-22
     * @version     v1.0
     *
     * @param i_Datas
     * @return
     */
    public final static String toString(List<?> i_Datas)
    {
        if ( !Help.isNull(i_Datas) )
        {
            Object v_FirstValue = i_Datas.get(0);
            if ( null == v_FirstValue )
            {
                return toString(i_Datas ,"'" ,",");
            }
            else if ( MethodReflect.isExtendImplement(v_FirstValue ,Number.class) )
            {
                return toString(i_Datas ,""  ,",");
            }
            else
            {
                return toString(i_Datas ,"'" ,",");
            }
        }
        else
        {
            return "";
        }
    }
    
    
    
    /**
     * 将集合转换为字符串。转换后的字符串有两种形式（按逗号分隔）
     *   形式1：'A' ,'B' ,'C'
     *   形式2：1   ,2   ,3
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-02-22
     * @version     v1.0
     *
     * @param i_Datas
     * @param i_StringSign  字符串标示。如，单引号'
     * @return
     */
    public final static String toString(List<?> i_Datas ,String i_StringSign)
    {
        return toString(i_Datas ,i_StringSign ,",");
    }
    
    
    
    /**
     * 将集合转换为字符串。转换后的字符串有两种形式
     *   形式1：'A' ,'B' ,'C'
     *   形式2：1   ,2   ,3
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-02-22
     * @version     v1.0
     *
     * @param i_Datas
     * @param i_StringSign  字符串标示。如，单引号'
     * @param i_Split       元素分隔符。如，逗号，
     * @return
     */
    public final static String toString(List<?> i_Datas ,String i_StringSign ,String i_Split)
    {
        StringBuilder v_Buffer     = new StringBuilder();
        String        v_StringSign = Help.NVL(i_StringSign);
        int           v_Count      = 0;
        
        if ( !Help.isNull(i_Datas) )
        {
            for (int v_Index=0; v_Index<i_Datas.size(); v_Index++)
            {
                Object v_Value = i_Datas.get(v_Index);
                
                if ( null != v_Value )
                {
                    if ( v_Count >= 1 )
                    {
                        v_Buffer.append(i_Split);
                    }
                    
                    v_Buffer.append(v_StringSign).append(v_Value.toString()).append(v_StringSign);
                    v_Count++;
                }
            }
        }
        
        return v_Buffer.toString();
    }
    
    
    
    /**
     * 将集合转换为字符串。转换后的字符串有两种形式（自动判断形式）（按逗号分隔）
     *   形式1：'A' ,'B' ,'C'
     *   形式2：1   ,2   ,3
     * 
     * 自动判断形式
     *   1. 元素类型为字符串的，生成形式1，按单引号括起来
     *   2. 元素类型为数字类的，生成形式2
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-02-22
     * @version     v1.0
     *
     * @param i_Datas
     * @return
     */
    public final static String toString(Collection<?> i_Datas)
    {
        if ( !Help.isNull(i_Datas) )
        {
            Object v_FirstValue = i_Datas.iterator().next();
            if ( null == v_FirstValue )
            {
                return toString(i_Datas ,"'" ,",");
            }
            else if ( MethodReflect.isExtendImplement(v_FirstValue ,Number.class) )
            {
                return toString(i_Datas ,""  ,",");
            }
            else
            {
                return toString(i_Datas ,"'" ,",");
            }
        }
        else
        {
            return "";
        }
    }
    
    
    
    /**
     * 将集合转换为字符串。转换后的字符串有两种形式（按逗号分隔）
     *   形式1：'A' ,'B' ,'C'
     *   形式2：1   ,2   ,3
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-02-22
     * @version     v1.0
     *
     * @param i_Datas
     * @param i_StringSign  字符串标示。如，单引号'
     * @return
     */
    public final static String toString(Collection<?> i_Datas ,String i_StringSign)
    {
        return toString(i_Datas ,i_StringSign ,",");
    }
    
    
    
    /**
     * 将集合转换为字符串。转换后的字符串有两种形式
     *   形式1：'A' ,'B' ,'C'
     *   形式2：1   ,2   ,3
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-02-22
     * @version     v1.0
     *
     * @param i_Datas
     * @param i_StringSign  字符串标示。如，单引号'
     * @param i_Split       元素分隔符。如，逗号，
     * @return
     */
    public final static String toString(Collection<?> i_Datas ,String i_StringSign ,String i_Split)
    {
        StringBuilder v_Buffer     = new StringBuilder();
        String        v_StringSign = Help.NVL(i_StringSign);
        int           v_Count      = 0;
        
        if ( !Help.isNull(i_Datas) )
        {
            Iterator<?> v_Iter  = i_Datas.iterator();
            
            while ( v_Iter.hasNext() )
            {
                Object v_Value = v_Iter.next();
                
                if ( null != v_Value )
                {
                    if ( v_Count >= 1 )
                    {
                        v_Buffer.append(i_Split);
                    }
                    
                    v_Buffer.append(v_StringSign).append(v_Value.toString()).append(v_StringSign);
                    v_Count++;
                }
            }
        }
        
        return v_Buffer.toString();
    }
    
    
    
    /**
     * 将集合转换为字符串。转换后的字符串有两种形式（自动判断形式）（按逗号分隔）
     *   形式1：'A' ,'B' ,'C'
     *   形式2：1   ,2   ,3
     * 
     * 自动判断形式
     *   1. 元素类型为字符串的，生成形式1，按单引号括起来
     *   2. 元素类型为数字类的，生成形式2
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-02-22
     * @version     v1.0
     *
     * @param i_Datas
     * @return
     */
    public final static String toString(Set<?> i_Datas)
    {
        if ( !Help.isNull(i_Datas) )
        {
            Object v_FirstValue = i_Datas.iterator().next();
            if ( null == v_FirstValue )
            {
                return toString(i_Datas ,"'" ,",");
            }
            else if ( MethodReflect.isExtendImplement(v_FirstValue ,Number.class) )
            {
                return toString(i_Datas ,""  ,",");
            }
            else
            {
                return toString(i_Datas ,"'" ,",");
            }
        }
        else
        {
            return "";
        }
    }
    
    
    
    /**
     * 将集合转换为字符串。转换后的字符串有两种形式（按逗号分隔）
     *   形式1：'A' ,'B' ,'C'
     *   形式2：1   ,2   ,3
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-02-22
     * @version     v1.0
     *
     * @param i_Datas
     * @param i_StringSign  字符串标示。如，单引号'
     * @return
     */
    public final static String toString(Set<?> i_Datas ,String i_StringSign)
    {
        return toString(i_Datas ,i_StringSign ,",");
    }
    
    
    
    /**
     * 将集合转换为字符串。转换后的字符串有两种形式
     *   形式1：'A' ,'B' ,'C'
     *   形式2：1   ,2   ,3
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-02-22
     * @version     v1.0
     *
     * @param i_Datas
     * @param i_StringSign  字符串标示。如，单引号'
     * @param i_Split       元素分隔符。如，逗号，
     * @return
     */
    public final static String toString(Set<?> i_Datas ,String i_StringSign ,String i_Split)
    {
        StringBuilder v_Buffer     = new StringBuilder();
        String        v_StringSign = Help.NVL(i_StringSign);
        int           v_Count      = 0;
        
        if ( !Help.isNull(i_Datas) )
        {
            Iterator<?> v_Iter  = i_Datas.iterator();
            
            while ( v_Iter.hasNext() )
            {
                Object v_Value = v_Iter.next();
                
                if ( null != v_Value )
                {
                    if ( v_Count >= 1 )
                    {
                        v_Buffer.append(i_Split);
                    }
                    
                    v_Buffer.append(v_StringSign).append(v_Value.toString()).append(v_StringSign);
                    v_Count++;
                }
            }
        }
        
        return v_Buffer.toString();
    }
    
    
    
    /**
     * 将Map.value集合转换为字符串。转换后的字符串有两种形式（自动判断形式）（按逗号分隔）
     *   形式1：'A' ,'B' ,'C'
     *   形式2：1   ,2   ,3
     * 
     * 自动判断形式
     *   1. 元素类型为字符串的，生成形式1，按单引号括起来
     *   2. 元素类型为数字类的，生成形式2
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-02-22
     * @version     v1.0
     *
     * @param i_Datas
     * @return
     */
    public final static String toString(Map<? ,?> i_Datas)
    {
        if ( !Help.isNull(i_Datas) )
        {
            Object v_FirstValue = i_Datas.values().iterator().next();
            if ( null == v_FirstValue )
            {
                return toString(i_Datas ,"'" ,",");
            }
            else if ( MethodReflect.isExtendImplement(v_FirstValue ,Number.class) )
            {
                return toString(i_Datas ,""  ,",");
            }
            else
            {
                return toString(i_Datas ,"'" ,",");
            }
        }
        else
        {
            return "";
        }
    }
    
    
    
    /**
     * 将Map.value集合转换为字符串。转换后的字符串有两种形式（按逗号分隔）
     *   形式1：'A' ,'B' ,'C'
     *   形式2：1   ,2   ,3
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-02-22
     * @version     v1.0
     *
     * @param i_Datas
     * @param i_StringSign  字符串标示。如，单引号'
     * @return
     */
    public final static String toString(Map<? ,?> i_Datas ,String i_StringSign)
    {
        return toString(i_Datas ,i_StringSign ,",");
    }
    
    
    
    /**
     * 将Map.value集合转换为字符串。转换后的字符串有两种形式
     *   形式1：'A' ,'B' ,'C'
     *   形式2：1   ,2   ,3
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-02-22
     * @version     v1.0
     *
     * @param i_Datas
     * @param i_StringSign  字符串标示。如，单引号'
     * @param i_Split       元素分隔符。如，逗号，
     * @return
     */
    public final static String toString(Map<? ,?> i_Datas ,String i_StringSign ,String i_Split)
    {
        StringBuilder v_Buffer     = new StringBuilder();
        String        v_StringSign = Help.NVL(i_StringSign);
        int           v_Count      = 0;
        
        if ( !Help.isNull(i_Datas) )
        {
            Iterator<?> v_Iter  = i_Datas.values().iterator();
            
            while ( v_Iter.hasNext() )
            {
                Object v_Value = v_Iter.next();
                
                if ( null != v_Value )
                {
                    if ( v_Count >= 1 )
                    {
                        v_Buffer.append(i_Split);
                    }
                    
                    v_Buffer.append(v_StringSign).append(v_Value.toString()).append(v_StringSign);
                    v_Count++;
                }
            }
        }
        
        return v_Buffer.toString();
    }
    
    
    
    /**
     * 将Map.key集合转换为字符串。转换后的字符串有两种形式（自动判断形式）（按逗号分隔）
     *   形式1：'A' ,'B' ,'C'
     *   形式2：1   ,2   ,3
     * 
     * 自动判断形式
     *   1. 元素类型为字符串的，生成形式1，按单引号括起来
     *   2. 元素类型为数字类的，生成形式2
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-02-22
     * @version     v1.0
     *
     * @param i_Datas
     * @return
     */
    public final static String toStringKeys(Map<? ,?> i_Datas)
    {
        if ( !Help.isNull(i_Datas) )
        {
            Object v_FirstValue = i_Datas.keySet().iterator().next();
            if ( null == v_FirstValue )
            {
                return toStringKeys(i_Datas ,"'" ,",");
            }
            else if ( MethodReflect.isExtendImplement(v_FirstValue ,Number.class) )
            {
                return toStringKeys(i_Datas ,""  ,",");
            }
            else
            {
                return toStringKeys(i_Datas ,"'" ,",");
            }
        }
        else
        {
            return "";
        }
    }
    
    
    
    /**
     * 将Map.key集合转换为字符串。转换后的字符串有两种形式（按逗号分隔）
     *   形式1：'A' ,'B' ,'C'
     *   形式2：1   ,2   ,3
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-02-22
     * @version     v1.0
     *
     * @param i_Datas
     * @param i_StringSign  字符串标示。如，单引号'
     * @return
     */
    public final static String toStringKeys(Map<? ,?> i_Datas ,String i_StringSign)
    {
        return toStringKeys(i_Datas ,i_StringSign ,",");
    }
    
    
    
    /**
     * 将Map.key集合转换为字符串。转换后的字符串有两种形式
     *   形式1：'A' ,'B' ,'C'
     *   形式2：1   ,2   ,3
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-02-22
     * @version     v1.0
     *
     * @param i_Datas
     * @param i_StringSign  字符串标示。如，单引号'
     * @param i_Split       元素分隔符。如，逗号，
     * @return
     */
    public final static String toStringKeys(Map<? ,?> i_Datas ,String i_StringSign ,String i_Split)
    {
        StringBuilder v_Buffer     = new StringBuilder();
        String        v_StringSign = Help.NVL(i_StringSign);
        int           v_Count      = 0;
        
        if ( !Help.isNull(i_Datas) )
        {
            Iterator<?> v_Iter  = i_Datas.keySet().iterator();
            
            while ( v_Iter.hasNext() )
            {
                Object v_Value = v_Iter.next();
                
                if ( null != v_Value )
                {
                    if ( v_Count >= 1 )
                    {
                        v_Buffer.append(i_Split);
                    }
                    
                    v_Buffer.append(v_StringSign).append(v_Value.toString()).append(v_StringSign);
                    v_Count++;
                }
            }
        }
        
        return v_Buffer.toString();
    }
    
    
    
    /**
     * 将集合转换为字符串。转换后的字符串有两种形式
     *   形式1：'A' ,'B' ,'C'
     *   形式2：1   ,2   ,3
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-02-22
     * @version     v1.0
     *
     * @param i_Datas
     * @param i_StringSign  字符串标示。如，单引号'
     * @param i_Split       元素分隔符。如，逗号，
     * @return
     */
    public final static String toString(int[] i_Datas ,String i_StringSign ,String i_Split)
    {
        StringBuilder v_Buffer     = new StringBuilder();
        String        v_StringSign = Help.NVL(i_StringSign);
        
        if ( !Help.isNull(i_Datas) )
        {
            for (int v_Index=0; v_Index<i_Datas.length; v_Index++)
            {
                int v_Value = i_Datas[v_Index];
                
                if ( v_Index >= 1 )
                {
                    v_Buffer.append(i_Split);
                }
                
                v_Buffer.append(v_StringSign).append(v_Value).append(v_StringSign);
            }
        }
        
        return v_Buffer.toString();
    }
    
    
    
    /**
     * 将Map.value集合转换为字符串。转换后的字符串有两种形式
     *   形式1：'A' ,'B' ,'C'
     *   形式2：1   ,2   ,3
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-02-22
     * @version     v1.0
     *
     * @param i_Datas
     * @param i_StringSign  字符串标示。如，单引号'
     * @param i_Split       元素分隔符。如，逗号，
     * @return
     */
    public final static String toString(long[] i_Datas ,String i_StringSign ,String i_Split)
    {
        StringBuilder v_Buffer     = new StringBuilder();
        String        v_StringSign = Help.NVL(i_StringSign);
        
        if ( !Help.isNull(i_Datas) )
        {
            for (int v_Index=0; v_Index<i_Datas.length; v_Index++)
            {
                long v_Value = i_Datas[v_Index];
                
                if ( v_Index >= 1 )
                {
                    v_Buffer.append(i_Split);
                }
                
                v_Buffer.append(v_StringSign).append(v_Value).append(v_StringSign);
            }
        }
        
        return v_Buffer.toString();
    }
    
    
    
    /**
     * 将集合转换为字符串。转换后的字符串有两种形式
     *   形式1：'A' ,'B' ,'C'
     *   形式2：1   ,2   ,3
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-02-22
     * @version     v1.0
     *
     * @param i_Datas
     * @param i_StringSign  字符串标示。如，单引号'
     * @param i_Split       元素分隔符。如，逗号，
     * @return
     */
    public final static String toString(double[] i_Datas ,String i_StringSign ,String i_Split)
    {
        StringBuilder v_Buffer     = new StringBuilder();
        String        v_StringSign = Help.NVL(i_StringSign);
        
        if ( !Help.isNull(i_Datas) )
        {
            for (int v_Index=0; v_Index<i_Datas.length; v_Index++)
            {
                double v_Value = i_Datas[v_Index];
                
                if ( v_Index >= 1 )
                {
                    v_Buffer.append(i_Split);
                }
                
                v_Buffer.append(v_StringSign).append(v_Value).append(v_StringSign);
            }
        }
        
        return v_Buffer.toString();
    }
    
    
    
    /**
     * 将集合转换为字符串。转换后的字符串有两种形式
     *   形式1：'A' ,'B' ,'C'
     *   形式2：1   ,2   ,3
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-02-22
     * @version     v1.0
     *
     * @param i_Datas
     * @param i_StringSign  字符串标示。如，单引号'
     * @param i_Split       元素分隔符。如，逗号，
     * @return
     */
    public final static String toString(float[] i_Datas ,String i_StringSign ,String i_Split)
    {
        StringBuilder v_Buffer     = new StringBuilder();
        String        v_StringSign = Help.NVL(i_StringSign);
        
        if ( !Help.isNull(i_Datas) )
        {
            for (int v_Index=0; v_Index<i_Datas.length; v_Index++)
            {
                float v_Value = i_Datas[v_Index];
                
                if ( v_Index >= 1 )
                {
                    v_Buffer.append(i_Split);
                }
                
                v_Buffer.append(v_StringSign).append(v_Value).append(v_StringSign);
            }
        }
        
        return v_Buffer.toString();
    }
    
    
    
    /**
     * 将集合转换为字符串。转换后的字符串有两种形式
     *   形式1：'A' ,'B' ,'C'
     *   形式2：1   ,2   ,3
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-02-22
     * @version     v1.0
     *
     * @param i_Datas
     * @param i_StringSign  字符串标示。如，单引号'
     * @param i_Split       元素分隔符。如，逗号，
     * @return
     */
    public final static String toString(char[] i_Datas ,String i_StringSign ,String i_Split)
    {
        StringBuilder v_Buffer     = new StringBuilder();
        String        v_StringSign = Help.NVL(i_StringSign);
        
        if ( !Help.isNull(i_Datas) )
        {
            for (int v_Index=0; v_Index<i_Datas.length; v_Index++)
            {
                char v_Value = i_Datas[v_Index];
                
                if ( v_Index >= 1 )
                {
                    v_Buffer.append(i_Split);
                }
                
                v_Buffer.append(v_StringSign).append(v_Value).append(v_StringSign);
            }
        }
        
        return v_Buffer.toString();
    }
    
    
    
    /**
     * 将集合转换为字符串。转换后的字符串有两种形式
     *   形式1：'A' ,'B' ,'C'
     *   形式2：1   ,2   ,3
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-02-22
     * @version     v1.0
     *
     * @param i_Datas
     * @param i_StringSign  字符串标示。如，单引号'
     * @param i_Split       元素分隔符。如，逗号，
     * @return
     */
    public final static String toString(short[] i_Datas ,String i_StringSign ,String i_Split)
    {
        StringBuilder v_Buffer     = new StringBuilder();
        String        v_StringSign = Help.NVL(i_StringSign);
        
        if ( !Help.isNull(i_Datas) )
        {
            for (int v_Index=0; v_Index<i_Datas.length; v_Index++)
            {
                short v_Value = i_Datas[v_Index];
                
                if ( v_Index >= 1 )
                {
                    v_Buffer.append(i_Split);
                }
                
                v_Buffer.append(v_StringSign).append(v_Value).append(v_StringSign);
            }
        }
        
        return v_Buffer.toString();
    }
    
    
    
    /**
     * 将集合转换为字符串。转换后的字符串有两种形式
     *   形式1：'A' ,'B' ,'C'
     *   形式2：1   ,2   ,3
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-02-22
     * @version     v1.0
     *
     * @param i_Datas
     * @param i_StringSign  字符串标示。如，单引号'
     * @param i_Split       元素分隔符。如，逗号，
     * @return
     */
    public final static String toString(byte[] i_Datas ,String i_StringSign ,String i_Split)
    {
        StringBuilder v_Buffer     = new StringBuilder();
        String        v_StringSign = Help.NVL(i_StringSign);
        
        if ( !Help.isNull(i_Datas) )
        {
            for (int v_Index=0; v_Index<i_Datas.length; v_Index++)
            {
                byte v_Value = i_Datas[v_Index];
                
                if ( v_Index >= 1 )
                {
                    v_Buffer.append(i_Split);
                }
                
                v_Buffer.append(v_StringSign).append(v_Value).append(v_StringSign);
            }
        }
        
        return v_Buffer.toString();
    }
    
    
    
    /**
     * 是否为手机号
     * 
     * @param i_TelNo
     * @return
     */
    public final static boolean isMobilePhone(String i_TelNo)
    {
        return isMobilePhone(i_TelNo ,11);
    }
    
    
    
    /**
     * 是否为手机号
     * 
     * @param i_TelNo
     * @return
     */
    public final static synchronized boolean isMobilePhone(String i_TelNo ,int i_Length)
    {
        MobilePhone.initForChina();
        
        try
        {
            return MobilePhone.isMobilePhone(i_TelNo ,i_Length);
        }
        finally
        {
            MobilePhone.clear();
        }
    }
    
    
    
    /**
     * 获取手机号对应的运营商类型。
     * 
     * 前缀判断规则：只判断前5位，最小长度为3位
     * 
     * 注：用完就释放内存
     * 
     * @param i_TelNo   手机号
     * @return          当返回 null ，就表示 i_TelNo 不是手机号
     */
    public final static String getMobileServiceType(String i_TelNo)
    {
        return getMobileServiceType(i_TelNo ,true);
    }
    
    
    
    /**
     * 获取手机号对应的运营商类型。
     * 
     * 前缀判断规则：只判断前5位，最小长度为3位
     * 
     * @param i_TelNo   手机号
     * @param i_IsClear 是否释放内存数据
     * @return          当返回 null ，就表示 i_TelNo 不是手机号
     */
    public final static synchronized String getMobileServiceType(String i_TelNo ,boolean i_IsClear)
    {
        MobilePhone.initForChina();
        
        try
        {
            return MobilePhone.getServiceType(i_TelNo);
        }
        finally
        {
            if ( i_IsClear )
            {
                MobilePhone.clear();
            }
        }
    }
    
}