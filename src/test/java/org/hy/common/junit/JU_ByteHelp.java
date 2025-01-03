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
    
    
    
    @Test
    public void shortToBytes()
    {
        short v_254 = 254;
        short v_255 = 255;
        short v_256 = 256;
        short v_Max = Short.MAX_VALUE;
        short v_Min = Short.MIN_VALUE;
        
        System.out.println(ByteHelp.bytesToHex(ByteHelp.shortToByte(v_254)) + " = " + ByteHelp.byteToShort(ByteHelp.shortToByte(v_254)));
        System.out.println(ByteHelp.bytesToHex(ByteHelp.shortToByte(v_255)) + " = " + ByteHelp.byteToShort(ByteHelp.shortToByte(v_255)));
        System.out.println(ByteHelp.bytesToHex(ByteHelp.shortToByte(v_256)) + " = " + ByteHelp.byteToShort(ByteHelp.shortToByte(v_256)));
        System.out.println(ByteHelp.bytesToHex(ByteHelp.shortToByte(v_Max)) + " = " + ByteHelp.byteToShort(ByteHelp.shortToByte(v_Max)));
        System.out.println(ByteHelp.bytesToHex(ByteHelp.shortToByte(v_Min)) + " = " + ByteHelp.byteToShort(ByteHelp.shortToByte(v_Min)));
        
        byte[] v_Packet = new byte[2]; 
        v_Packet[0] = (byte) 0xFF;
        v_Packet[1] = (byte) 0xFF;
        System.out.println(ByteHelp.bytesToHex(v_Packet) + " = " +        ByteHelp.byteToShort(v_Packet));
        System.out.println(ByteHelp.bytesToHex(v_Packet) + " = " + (short)ByteHelp.byteToShort(v_Packet));
    }
    
}
