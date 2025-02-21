package org.hy.common.junit;

import java.math.BigDecimal;

import org.hy.common.Help;
import org.hy.common.StringHelp;





/**
 * 测试四舍五入
 * 
 * @author      ZhengWei(HY)
 * @version     v1.0  
 * @createDate  2014-10-09
 */
public class JU_Round
{
    
    @SuppressWarnings("deprecation")
    public static void main(String [] args)
    {
        int        v_Digit = 2;
        BigDecimal v_Base  = new BigDecimal(1d);
        BigDecimal v_Plus  = new BigDecimal(0.001d);
        
        for (int i=1; i<=1000 * 100; i++)
        {
            v_Base = v_Base.add(v_Plus);
            
            double v_R01 = Help.round(v_Base.doubleValue() ,v_Digit);
            // 对比Java自身提供的四舍五入方法
            double v_R02 = new BigDecimal(v_Base.doubleValue()).setScale(v_Digit ,BigDecimal.ROUND_HALF_UP).doubleValue();
            
            System.out.print(StringHelp.rpad(String.valueOf(v_Base.doubleValue()) ,20 ," ") + " ~ " 
                           + StringHelp.rpad(String.valueOf(v_R01)                ,10 ," ") 
                           + " BigDecimal: " + String.valueOf(v_R02));
            
            if ( v_R01 != v_R02 )
            {
                System.out.println(" *********************");
            }
            else
            {
                System.out.println("");
            }
        }
    }
    
}
