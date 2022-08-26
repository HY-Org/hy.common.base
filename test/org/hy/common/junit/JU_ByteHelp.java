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
    
    
    
    @Test
    public void substr()
    {
        String v_Value = "ABCDEFGHIGKLMNOPGRSTUVWXYZ";
        
        System.out.println(new String(ByteHelp.substr(v_Value.getBytes() ,0  ,1024)));
        System.out.println(new String(ByteHelp.substr(v_Value.getBytes() ,0  ,7)));
        System.out.println(new String(ByteHelp.substr(v_Value.getBytes() ,7  ,7)));
        System.out.println(new String(ByteHelp.substr(v_Value.getBytes() ,14 ,6)));
        System.out.println(new String(ByteHelp.substr(v_Value.getBytes() ,20 ,6)));
        
    }
}
