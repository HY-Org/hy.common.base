package org.hy.common.junit;

import java.util.ArrayList;
import java.util.List;

import org.hy.common.IteratorSnapshot;
import org.junit.Test;





/**
 * 测试单元：快照迭代器。支持多次退回的迭代器
 *
 * @author      ZhengWei(HY)
 * @createDate  2026-11-02
 * @version     v1.0
 */
public class JU_IteratorSnapshot
{
    
    @Test
    public void test()
    {
        List<String> v_Datas = new ArrayList<String>();
        
        v_Datas.add("A");
        v_Datas.add("B");
        v_Datas.add("C");
        v_Datas.add("D");
        v_Datas.add("E");
        v_Datas.add("F");
        v_Datas.add("G");
        
        IteratorSnapshot<String> v_Iterator = new IteratorSnapshot<String>(v_Datas.iterator());
        
        System.out.println("快照读取第1次");
        this.println(v_Iterator);
        
        System.out.println("快照读取第2次");
        v_Iterator.hasNext();
        this.println(v_Iterator);
        
        System.out.println("快照读取第3次");
        v_Iterator.hasNext();
        this.println(v_Iterator);
        
        System.out.println("真读取1个元素");
        v_Iterator.trueMode();
        v_Iterator.next();
        
        System.out.println("快照读取第4次");
        v_Iterator.snapshotMode();
        this.println(v_Iterator);
        
        System.out.println("真读取遍历全部第1次");
        v_Iterator.trueMode();
        this.println(v_Iterator);
        
        System.out.println("真读取遍历全部第2次");
        this.println(v_Iterator);
    }
    
    
    
    private void println(IteratorSnapshot<?> i_Iterator)
    {
        while ( i_Iterator.hasNext() )
        {
            System.out.println(i_Iterator.next());
        }
    }
    
}
