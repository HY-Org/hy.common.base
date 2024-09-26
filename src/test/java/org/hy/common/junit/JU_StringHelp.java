package org.hy.common.junit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hy.common.Help;
import org.hy.common.RelationList;
import org.hy.common.Return;
import org.hy.common.SplitSegment;
import org.hy.common.StringHelp;
import org.junit.Test;





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
    public void test_XML()
    {
        String v_XML = """
                        <html lang="">
                        
                            <head>
                                <meta charset="utf-8">
                                <meta http-equiv="X-UA-Compatible" content="IE=edge">
                                <meta name="viewport" content="width=device-width,initial-scale=1">
                                <link rel="icon" href="/datacenter/favicon.ico">
                                <title>数据中台</title>
                                <link rel="stylesheet" href="/datacenter/css/bootstrap.css">
                                <script defer="defer" src="/datacenter/js/chunk-vendors.9c4c0e43.js"></script>
                                <script defer="defer" src="/datacenter/js/index.5c132055.js"></script>
                                <link href="/datacenter/css/chunk-vendors.53db00c5.css" rel="stylesheet">
                                <link href="/datacenter/css/index.00bf86ed.css" rel="stylesheet">
                            </head>
                        
                            <body>
                                <noscript>
                                    <strong>Please enable it to continue.</strong>
                                </noscript>
                                <div id="app">
                                    <div id="1"><div id="name001">ZhengWei</div><div id="age001">9</div></div>
                                    <div id="2"><div id="name002">HY</div><div id="age002">18</div></div>
                                    <div id="3"><div id="name003">MS</div><div id="age003">27</div></div>
                                    <div id="4"><div id="name004">OpenApis</div><div id="age004">36</div></div>
                                </div>
                                
                                <div id="info">
                                </div>
                            </body>
                        
                        </html>
                       """;
        
        System.out.println(StringHelp.getString(v_XML ,"<div" ,"</div>"));
    }
    
    
    
    @Test
    public void test_SplitMaxMatch()
    {
        String              v_Info       = "UKO3O4SZX1";
        Map<String ,String> v_MatchDatas = new HashMap<String ,String>();
        
        v_MatchDatas.put("U"   ,"两");
        v_MatchDatas.put("K"   ,"岸");
        v_MatchDatas.put("KO"  ,"-1");      // 干扰项
        v_MatchDatas.put("O3"  ,"猿声");
        v_MatchDatas.put("O4"  ,"啼不");
        //v_MatchDatas.put("O"   ,"-2");      // 干扰项
        v_MatchDatas.put("S"   ,"住");
        v_MatchDatas.put("Z"   ,"轻");
        v_MatchDatas.put("ZX1" ,"轻舟已");
        //v_MatchDatas.put("UK"  ,"-3");      // 干扰项
        
        System.out.println("\n\n最大匹配");
        Help.print(StringHelp.SplitMaxMatch(v_Info ,v_MatchDatas ,false));
        
        System.out.println("\n\n最大匹配：全匹配");
        Help.print(StringHelp.SplitMaxMatch(v_Info ,v_MatchDatas ,true));
        
        System.out.println("\n\n最小匹配");
        Help.print(StringHelp.SplitMinMatch(v_Info ,v_MatchDatas ,false));
        
        System.out.println("\n\n最小匹配：全匹配");
        Help.print(StringHelp.SplitMinMatch(v_Info ,v_MatchDatas ,true));
    }
    
    
    
    @Test
    public void test_SplitMaxMatch_ALotOf()
    {
        Map<String ,String> v_MatchDatas = new HashMap<String ,String>();
        
        v_MatchDatas.put("A"       ,"禁石墨(阀体组件)");
        v_MatchDatas.put("A1"      ,"禁石墨(法兰垫片)");
        v_MatchDatas.put("B"       ,"不锈钢材质防雨帽");
        v_MatchDatas.put("C"       ,"禁水");
        v_MatchDatas.put("D"       ,"禁硫");
        v_MatchDatas.put("DP"      ,"垫片");
        v_MatchDatas.put("DZ"      ,"电动执行机构");
        v_MatchDatas.put("E1"      ,"禁油W1级(阀体组件)");
        v_MatchDatas.put("E2"      ,"禁油W2级(阀体组件)");
        v_MatchDatas.put("E3"      ,"禁油W3级");
        v_MatchDatas.put("E4"      ,"禁油W1级(配对法兰)");
        v_MatchDatas.put("E5"      ,"禁油W1级(法兰垫片)");
        v_MatchDatas.put("E6"      ,"禁油W2级(配对法兰)");
        v_MatchDatas.put("E7"      ,"禁油W2级(法兰垫片)");
        v_MatchDatas.put("F"       ,"禁用PTFE");
        v_MatchDatas.put("FJ"      ,"附件连接");
        v_MatchDatas.put("FL"      ,"配对法兰");
        v_MatchDatas.put("FLKS"    ,"DN65法兰4孔");
        v_MatchDatas.put("FLLS"    ,"法兰螺栓为全螺纹螺柱");
        v_MatchDatas.put("FT"      ,"阀体组件");
        v_MatchDatas.put("G"       ,"出口");
        v_MatchDatas.put("G1"      ,"特殊标识");
        v_MatchDatas.put("G2070"   ,"G2070软密封球阀");
        v_MatchDatas.put("G2070ZJ" ,"阀体和执行机构采用支架连接");
        v_MatchDatas.put("H"       ,"开启时间");
        v_MatchDatas.put("I"       ,"禁橡胶(阀体组件)");
        v_MatchDatas.put("J"       ,"关闭时间");
        v_MatchDatas.put("K"       ,"环境禁铜");
        v_MatchDatas.put("KLJZ"    ,"颗粒介质");
        v_MatchDatas.put("L"       ,"介质禁铜");
        v_MatchDatas.put("LL"      ,"流量特性试验");
        v_MatchDatas.put("M"       ,"光洁度");
        v_MatchDatas.put("O1"      ,"指定色涂层(阀体组件底漆)");
        v_MatchDatas.put("O2"      ,"指定色涂层(阀体组件面漆)");
        v_MatchDatas.put("O3"      ,"指定色涂层(执行机构面漆)");
        v_MatchDatas.put("O4"      ,"指定色涂层(执行机构底漆)");
        v_MatchDatas.put("P"       ,"探伤检验");
        v_MatchDatas.put("Q"       ,"高温试验");
        v_MatchDatas.put("QZ"      ,"气动执行机构");
        v_MatchDatas.put("R"       ,"低温试验");
        v_MatchDatas.put("S"       ,"材料检查");
        v_MatchDatas.put("SCTL"    ,"双重填料");
        v_MatchDatas.put("SM"      ,"配管栓母");
        v_MatchDatas.put("U"       ,"环境温度低于-45℃");
        v_MatchDatas.put("U1"      ,"高温V级");
        v_MatchDatas.put("W"       ,"NACE MR 0103(阀体组件)");
        v_MatchDatas.put("W1"      ,"NACE MR 0175(阀体组件)");
        v_MatchDatas.put("W2"      ,"NACE MR 0103(配对法兰)");
        v_MatchDatas.put("W3"      ,"NACE MR 0175(配对法兰)");
        v_MatchDatas.put("WH"      ,"位号牌");
        v_MatchDatas.put("WZ"      ,"外购执行机构连接");
        v_MatchDatas.put("Y"       ,"低温");
        v_MatchDatas.put("Z"       ,"低温禁铜");
        
        Map<String ,String> v_Infos = new HashMap<String ,String>();
        v_Infos.put("A"      ,"标准型");
        v_Infos.put("K"      ,"环境禁铜");
        v_Infos.put("KO3"    ,"环境禁铜+特殊面漆");
        v_Infos.put("KO3O4"  ,"环境禁铜+特殊面漆底漆");
        v_Infos.put("KO4"    ,"环境禁铜+特殊底漆");
        v_Infos.put("O3"     ,"指定色涂层（执行机构面漆）");
        v_Infos.put("O3O4"   ,"指定色涂层（执行机构底漆+面漆）");
        v_Infos.put("O4"     ,"指定色涂层（执行机构底漆）");
        v_Infos.put("U"      ,"环境温度低于-45℃");
        v_Infos.put("UK"     ,"环境温度低于-45℃+环境禁铜");
        v_Infos.put("UKO3"   ,"环境温度低于-45℃+环境禁铜+特殊面漆");
        v_Infos.put("UKO3O4" ,"环境温度低于-45℃+环境禁铜+特殊面漆底漆");
        v_Infos.put("UKO4"   ,"环境温度低于-45℃+环境禁铜+特殊底漆");
        v_Infos.put("UO3"    ,"环境温度低于-45℃+特殊面漆");
        v_Infos.put("UO3O4"  ,"环境温度低于-45℃+特殊面漆底漆");
        v_Infos.put("UO4"    ,"环境温度低于-45℃+特殊底漆");
        
        for (Map.Entry<String ,String> v_Info : v_Infos.entrySet())
        {
            System.out.println("\n\n" + v_Info.getKey() + " = " + v_Info.getValue());
            Help.print(StringHelp.SplitMaxMatch(v_Info.getKey() ,v_MatchDatas ,true));
        }
    }
    
    
    
    @Test
    public void test_findLastTimeAndName()
    {
        String v_Name1 = "2021-06-26";
        String v_Name2 = "2021-06-27";
        
        
        System.out.println(v_Name1.compareTo(v_Name2));
    }
    
    
    
    @Test
    public void test_replaceFirst()
    {
        System.out.println("非首位时：" + StringHelp.replaceFirst("#$%_ABC_123_ABC_456_ABC_789" ,"ABC" ,"XYZ"));
        System.out.println("在首位时：" + StringHelp.replaceFirst("ABC_123_ABC_456_ABC_789"     ,"ABC" ,"XYZ"));
        System.out.println("在末尾时：" + StringHelp.replaceFirst("#$%_123_ABC"                 ,"ABC" ,"XYZ"));
    }
    
    
    
    @Test
    public void test_replaceLast()
    {
        System.out.println("非末尾时：" + StringHelp.replaceLast("#$%_ABC_123_ABC_456_ABC_789" ,"ABC" ,"XYZ"));
        System.out.println("在末尾时：" + StringHelp.replaceLast("#$%_ABC_123_ABC"             ,"ABC" ,"XYZ"));
        System.out.println("在首位时：" + StringHelp.replaceLast("ABC_123_#$%"                 ,"ABC" ,"XYZ"));
    }
    
    
    
    @Test
    public void test_encode()
    {
        String v_Charset = "UTF-8";
        
        System.out.println(StringHelp.encode("ABCDEFG hiklmn 1234567890 `~!@#$%^&*()-_=+[]{},./<>?;':\"" ,v_Charset ,"=&?"));
        System.out.println(StringHelp.decode(StringHelp.encode("ABCDEFG hiklmn 1234567890 `~!@#$%^&*()-_=+[]{},./<>?;':\"" ,v_Charset ,"=&?") ,v_Charset));
        System.out.println(StringHelp.decode(StringHelp.encode("中华人民共和国" ,v_Charset ,"=&?") ,v_Charset));
    }
    
    
    
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
    public void test_toCode()
    {
        System.out.println(StringHelp.toCode("12" ,4));
        System.out.println(StringHelp.toCode(StringHelp.toCode("12" ,4) ,4));
        
        System.out.println(StringHelp.toCode("1234567890-=`~!@#$%^&*()_+[]{};:'\",./<>?" ,8));
        System.out.println(StringHelp.toCode(StringHelp.toCode("1234567890-=`~!@#$%^&*()_+[]{};:'\",./<>?" ,8) ,8));
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
    
    
    
    @Test
    public void test_UUID()
    {
        List<String> v_IDs = new ArrayList<String>();
        for (int x=1; x<=10000; x++)
        {
            v_IDs.add(StringHelp.getUUID());
        }
        
        v_IDs = Help.toDistinct(v_IDs);
        System.out.println(v_IDs.size());
    }
    
}
