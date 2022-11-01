package org.hy.common.junit;

import org.hy.common.PinYin;
import org.junit.Test;





/**
 * 测试单元：汉字转拼音
 *
 * @author      ZhengWei(HY)
 * @createDate  2017-11-30
 * @version     v1.0
 */
public class JU_PinYin
{
    
    @Test
    public void test_toPinYin()
    {
        String v_Text = "微 信 开 放 平 台 认 证";
        
        System.out.println("默认情况："   + PinYin.toPinYin(v_Text));
        System.out.println("首字母大写：" + PinYin.toPinYin(v_Text ,true));
        System.out.println("全部大写："   + PinYin.toPinYin(v_Text ,false ,true ,false ,false));
        System.out.println("带声调："     + PinYin.toPinYin(v_Text ,true ,false));
        System.out.println("带声调符："   + PinYin.toPinYin(v_Text ,true ,true));
    }
    
}
