package org.hy.common.junit;

import org.hy.common.Help;
import org.hy.common.comparate.Comparate;
import org.hy.common.comparate.ComparateResult;
import org.junit.Test;





/**
 * 测试单元：两个相同元素类型的集合（数组、List、Set、Map）对比。 
 *
 * @author      ZhengWei(HY)
 * @createDate  2018-12-07
 * @version     v1.0
 */
public class JU_Comparate
{
    
    @Test
    public void test_Comparate()
    {
        String [] v_A = new String[]{"1"};
        String [] v_B = new String[]{"2"};
        
        ComparateResult<String []> v_CR = Comparate.comparate(v_A ,v_B);
        
        System.out.println("新增数据：");
        Help.print(v_CR.getNewData());
        
        System.out.println("删除数据：");
        Help.print(v_CR.getDelData());
    }
    
}
