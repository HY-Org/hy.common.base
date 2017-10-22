package org.hy.common.junit;

import org.hy.common.ByteHelp;
import org.junit.Test;


public class JU_ByteHelp
{
    
    @Test
    public void test_hexToBytes()
    {
        System.out.println(ByteHelp.hexToBytes("26B7A7D0BE"));
    }
    
}
