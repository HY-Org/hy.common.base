package org.hy.common.junit;

import org.hy.common.Queue;
import org.hy.common.xml.log.Logger;
import org.junit.Test;


public class JU_CowCalc
{
    private static final Logger $Logger = Logger.getLogger(JU_CowCalc.class ,true);
    
    
    
    @Test
    public void cowCalc()
    {
        Queue<Integer> v_HaveCow  = new Queue<Integer>();
        int            v_TotalNo  = 0;
        int            v_HaveSize = 100; 
        int            v_OutSize  = (int)(v_HaveSize * 0.4);
        
        v_TotalNo = this.inCow( v_HaveCow ,v_HaveSize ,v_TotalNo);
        
        for (int x=1; x<=365; x++)
        {
            $Logger.info("第 " + x + " 天：" + v_HaveCow.size());
            
            v_TotalNo = this.inCow( v_HaveCow ,v_OutSize ,v_TotalNo);
            this.outCow(v_HaveCow ,v_OutSize);
        }
    }
    
    
    private int inCow(Queue<Integer> i_HaveCow ,int i_InSize ,int i_TotalNo)
    {
        for (int x=1; x<=i_InSize; x++)
        {
            i_HaveCow.put(++i_TotalNo);
        }
        
        return i_TotalNo; 
    }
    
    
    
    private void outCow(Queue<Integer> i_HaveCow ,int i_OutSize)
    {
        for (int x=1; x<=i_OutSize; x++)
        {
            $Logger.info(i_HaveCow.get());
        }
    }
    
}
