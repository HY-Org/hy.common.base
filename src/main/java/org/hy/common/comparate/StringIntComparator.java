package org.hy.common.comparate;

import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hy.common.Help;





/**
 * 字符串的排序比较器
 * 
 * 特殊处在于：对于结尾是数字的字符串，按自然数比较大小。
 * 
 * 注：为了高效与常用性的考虑，本类只对末尾数是正整数有效
 * 
 * 举例：对 "a9"、"x1"、"x10"、"x2" 三个字符串排序后为以下结果。（注意：当前顺序是按字母排序的）
 *        "a9"、"x1"、"x2"、"x10"
 * 
 * @author      ZhengWei(HY)
 * @createDate  2026-05-07
 * @version     v1.0  
 */
public class StringIntComparator implements Comparator<Object>
{
    
    public record StringInt(String prefix ,Integer number) {}
    
    /** 正则：任意非数字 + 末尾数字 */
    private static final Pattern             $PatterSI     = Pattern.compile("^([^\\d]+)(\\d+)$");
    
    /** 正向排序 */
    public  static final StringIntComparator $NaturalOrder = new StringIntComparator(1);
    
    /** 反向排序 */
    public  static final StringIntComparator $ReverseOrder = new StringIntComparator(-1);
    
    
    
    /** 排序方向 */
    private int direction;
    
    
    
    private StringIntComparator(int i_Direction)
    {
        this.direction = i_Direction;
    }
    
    
    
    /**
     * 分解前缀与末尾数字
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-05-07
     * @version     v1.0
     *
     * @param i_Value
     * @return
     */
    public static StringInt getStringNumber(String i_Value)
    {
        if ( Help.isNull(i_Value) )
        {
            return new StringInt(i_Value ,null);
        }
        
        Matcher v_Matcher = $PatterSI.matcher(i_Value);
        if ( v_Matcher.matches() )
        {
            String v_Prefix = v_Matcher.group(1);
            int    v_Number = Integer.parseInt(v_Matcher.group(2));
            return new StringInt(v_Prefix ,v_Number);
        }
        else
        {
            // 不符合格式时返回原字符串 + null
            return new StringInt(i_Value ,null);
        }
    }
    
    

    @Override
    public int compare(Object i_V1 ,Object i_V2)
    {
        if ( i_V1 == null && i_V2 == null )
        {
            return 0;
        }
        else if ( i_V1 != null && i_V2 == null )
        {
            return 1;
        }
        else if ( i_V1 == null && i_V2 != null )
        {
            return -1;
        }
        else
        {
            StringInt v_SI1 = getStringNumber(i_V1.toString());
            StringInt v_SI2 = getStringNumber(i_V2.toString());
            int          v_Ret = v_SI1.prefix.compareTo(v_SI2.prefix);
            if ( v_Ret == 0 )
            {
                if ( v_SI1.number == null && v_SI2.number == null )
                {
                    return 0;
                }
                else if ( v_SI1.number != null && v_SI2.number == null )
                {
                    return 1;
                }
                else if ( v_SI1.number == null && v_SI2.number != null )
                {
                    return -1;
                }
                else
                {
                    return v_SI1.number.compareTo(v_SI2.number) * this.direction;
                }
            }
            else
            {
                return v_Ret * this.direction;
            }
        }
    }
    
}
