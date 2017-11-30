package org.hy.common;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;





/**
 * 汉字转拼音
 * 
 * 
 * 声调
 *      HanyuPinyinToneType.WITH_TONE_NUMBER  用数字表示声调，例如：liu2 
 *      HanyuPinyinToneType.WITHOUT_TONE      无声调表示，例如：liu 
 *      HanyuPinyinToneType.WITH_TONE_MARK    用声调符号表示，例如：liú 设置特殊拼音ü的显示格式
 * 
 * U的显示样式
 *      HanyuPinyinVCharType.WITH_U_AND_COLON 以U和一个冒号表示该拼音，例如：lu: 
 *      HanyuPinyinVCharType.WITH_V           以V表示该字符，例如：lv 
 *      HanyuPinyinVCharType.WITH_U_UNICODE   以ü表示 
 *
 * @author      ZhengWei(HY)
 * @createDate  2017-11-30
 * @version     v1.0
 */
public class PinYin
{
    
    /**
     * 汉字转拼音（全部小写）
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-11-30
     * @version     v1.0
     *
     * @param i_Text            汉字
     * @return
     */
    public static String toPinYin(String i_Text)
    {
        return toPinYin(i_Text ,false ,false ,false ,false);
    }
    
    
    
    /**
     * 汉字转拼音
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-11-30
     * @version     v1.0
     *
     * @param i_Text            汉字
     * @param i_OnlyFirstUpper  是否拼音的首字母大写。
     * @return
     */
    public static String toPinYin(String i_Text ,boolean i_OnlyFirstUpper)
    {
        return toPinYin(i_Text ,i_OnlyFirstUpper ,false ,false ,false);
    }
    
    
    
    /**
     * 汉字转拼音（带声调）
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-11-30
     * @version     v1.0
     *
     * @param i_Text            汉字
     * @param i_OnlyFirstUpper  是否拼音的首字母大写。
     * @return
     */
    public static String toPinYin(String i_Text ,boolean i_OnlyFirstUpper ,boolean i_IsToneMark)
    {
        return toPinYin(i_Text ,i_OnlyFirstUpper ,false ,false ,i_IsToneMark);
    }
    
    
    
    /**
     * 汉字转拼音
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-11-30
     * @version     v1.0
     *
     * @param i_Text            汉字
     * @param i_OnlyFirstUpper  是否拼音的首字母大写。i_OnlyFirstUpper为真时，i_IsAllUpper设置值无效。
     * @param i_IsAllUpper      true:全部大写；false:全部小写
     * @param i_IsTone          true:带声调，用数字表示声调；false:不带声调
     * @param i_IsToneMark      true:真正的拼音声调；false:用数字表示声调
     * @return
     */
    public static String toPinYin(String i_Text ,boolean i_OnlyFirstUpper ,boolean i_IsAllUpper ,boolean i_IsTone ,boolean i_IsToneMark) 
    {
        if ( Help.isNull(i_Text) )
        {
            return i_Text;
        }
        
        HanyuPinyinOutputFormat v_Format = new HanyuPinyinOutputFormat();
        
        v_Format.setVCharType(HanyuPinyinVCharType.WITH_V);
        
        if ( !i_OnlyFirstUpper && i_IsAllUpper )
        {
            // 输出拼音全部大写
            v_Format.setCaseType(HanyuPinyinCaseType.UPPERCASE);
        }
        else
        {
            // 输出拼音全部小写
            v_Format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        }
        
        if ( i_IsTone )
        {
            // 不带声调
            v_Format.setToneType(HanyuPinyinToneType.WITH_TONE_NUMBER);
        }
        else if ( i_IsToneMark )
        {
            // 声调符号
            v_Format.setToneType( HanyuPinyinToneType.WITH_TONE_MARK);
            v_Format.setVCharType(HanyuPinyinVCharType.WITH_U_UNICODE);
        }
        else
        {
            // 数字表示的声调
            v_Format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        }
        
        
        try 
        {
            char []       v_Chars  = i_Text.trim().toCharArray();
            StringBuilder v_Buffer = new StringBuilder();
            
            for (int i = 0; i < v_Chars.length; i++) 
            {
                String v_OneChar = String.valueOf(v_Chars[i]);
                
                // 如果字符是中文,则将中文转为汉语拼音,并取第一个字母
                if ( v_OneChar.matches("[\u4e00-\u9fa5]+") ) 
                {
                    String v_PinYin = PinyinHelper.toHanyuPinyinStringArray(v_Chars[i], v_Format)[0];
                    if ( i_OnlyFirstUpper )
                    {
                        if ( v_PinYin.length() >= 2 )
                        {
                            v_Buffer.append(v_PinYin.substring(0 ,1).toUpperCase()).append(v_PinYin.substring(1));
                        }
                        else
                        {
                            v_Buffer.append(v_PinYin.toUpperCase());
                        }
                    }
                    else
                    {
                        v_Buffer.append(v_PinYin);
                    }
                }
                // 如果是数字
                else if ( v_OneChar.matches("[0-9]+") ) 
                {
                    v_Buffer.append(v_Chars[i]);
                }
                // 如果是字母
                else if ( v_OneChar.matches("[a-zA-Z]+") ) 
                {
                    v_Buffer.append(v_Chars[i]);
                }
                // 如果是标点符号
                else 
                {
                    v_Buffer.append(v_Chars[i]);
                }
            }
            
            return v_Buffer.toString();
        } 
        catch (Exception exce) 
        {
            exce.printStackTrace();
        }
        
        return i_Text;
    }
    
    
    
    private PinYin()
    {
        // 私有
    }
    
}
