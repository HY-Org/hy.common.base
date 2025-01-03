package org.hy.common;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;





/**
 * 字节类型的工具类
 * 
 * @author      ZhengWei(HY)
 * @createDate  2012-07-27
 * @version     v1.0
 *              v2.0  2019-06-18  添加：输入流（文件流）转为二进制数据的方法toByte(...)
 *              v3.0  2020-06-16  添加：有符号的字节数组，转为无符号的整数数组。
 *                                添加：无符号的整数数组，转为有符号的字节数组。
 *              v4.0  2021-12-10  添加：byte[] 转 Byte[]
 *                                     Byte[] 转 byte[]
 *              v5.0  2022-08-26  修复：substr() 马龙：发现截取长度大于数据长度时，多减了一个1
 *              v6.0  2025-01-03  添加：短整数与字节数组相互转换。
 */
public final class ByteHelp
{
    
    /**
     * 私有构造器
     */
    private ByteHelp()
    {
        
    }
    
    
    
    /**
     * 字节数组左则填充
     * 
     * @param i_Value        原始字节数组
     * @param i_TotalLen     填充后的总长度
     * @param i_PadByte      填充的字节数组
     * @return
     */
    public static byte [] lpad(byte [] i_Value ,int i_TotalLen ,byte [] i_PadByte)
    {
        return pad(i_Value ,i_TotalLen ,i_PadByte ,-1);
    }
    
    
    
    /**
     * 字节数组右则填充
     * 
     * @param i_Value        原始字节数组
     * @param i_TotalLen     填充后的总长度
     * @param i_PadByte      填充的字节数组
     * @return
     */
    public static byte [] rpad(byte [] i_Value ,int i_TotalLen ,byte [] i_PadByte)
    {
        return pad(i_Value ,i_TotalLen ,i_PadByte ,1);
    }
    
    
    
    /**
     * 字节数组填充。
     * 会生新的存储空间。
     * 
     * @param i_Value        原始字节数组
     * @param i_TotalLen     填充后的总长度
     * @param i_PadByte      填充的字节数组
     * @param i_Way          填充方向。小于0，左则填充；等于大于0，右则填充
     * @return
     */
    public static byte [] pad(byte [] i_Value ,int i_TotalLen ,byte [] i_PadByte ,int i_Way)
    {
        if ( i_Value   == null || i_Value.length   == 0
          || i_PadByte == null || i_PadByte.length == 0 )
        {
            return new byte[0];
        }
        else if ( i_Value.length >= i_TotalLen )
        {
            return i_Value;
        }
        
        
        byte [] v_Ret      = new byte[i_TotalLen];
        int     v_ValueLen = i_Value.length;
        int     v_Index    = 0;
        
        if ( i_Way >= 0 )
        {
            v_Index = ByteHelp.setValue(v_Ret ,i_Value ,v_Index);
        }
        
        int v_PadByteLen = i_PadByte.length;
        int v_PadMaxLen  = i_TotalLen - v_ValueLen + v_Index;
        if ( v_PadByteLen == 1 )
        {
            // 其实下面的 else 分支可以满足本分支的功能，但还是独立本分支，是为了速度
            
            for (; v_Index<v_PadMaxLen; v_Index++)
            {
                v_Ret[v_Index] = i_PadByte[0];
            }
        }
        else
        {
            for (; v_Index<v_PadMaxLen;)
            {
                for (int v_PadIndex=0; v_PadIndex<v_PadByteLen && v_Index<v_PadMaxLen; v_PadIndex++ ,v_Index++)
                {
                    v_Ret[v_Index] = i_PadByte[v_PadIndex];
                }
            }
        }
        
        if ( i_Way < 0 )
        {
            v_Index = ByteHelp.setValue(v_Ret ,i_Value ,v_Index);
        }
        
        return v_Ret;
    }
    
    
    
    /**
     * 截取字节数组中的一小段，并形成新的字节数组
     * 
     * @param i_Value       原始字节数组
     * @param i_BeginIndex  开始截取位置。下标从零开始
     * @return
     */
    public static byte [] substr(byte [] i_Value ,int i_BeginIndex)
    {
        return substr(i_Value ,i_BeginIndex ,i_Value.length - i_BeginIndex);
    }
    
    
    
    /**
     * 截取字节数组中的一小段，并形成新的字节数组
     * 
     * @param i_Value       原始字节数组
     * @param i_BeginIndex  开始截取位置。下标从零开始
     * @param i_SubLen      截取长度
     * @return
     */
    public static byte [] substr(byte [] i_Value ,int i_BeginIndex ,int i_SubLen)
    {
        int  v_BeginIndex = i_BeginIndex % i_Value.length;
        int  v_EndIndex   = v_BeginIndex + i_SubLen;
        int  v_NewArrLen  = v_EndIndex <= i_Value.length ? i_SubLen : i_Value.length - v_BeginIndex;
             v_EndIndex   = v_BeginIndex + v_NewArrLen;
        byte [] v_Ret     = new byte[v_NewArrLen];
        
        for (int v_Index=v_BeginIndex; v_Index<v_EndIndex; v_Index++)
        {
            v_Ret[v_Index - v_BeginIndex] = i_Value[v_Index];
        }
        
        return v_Ret;
    }
    
    
    
    /**
     * 赋值 (不创建新的对象)
     * 
     * @param io_Value      输入和输出类型
     * @param i_NewValue    赋值的值
     * @param i_BeginIndex  赋值位置，相对于 io_Value 的位置。下标从零开始
     * @return              返回实际赋值的最后位置
     */
    public static int setValue(byte [] io_Value ,byte [] i_NewValue ,int i_BeginIndex)
    {
        return setValue(io_Value ,i_NewValue ,i_BeginIndex ,i_NewValue.length);
    }
    
    
    
    /**
     * 赋值 (不创建新的对象)
     * 
     * @param io_Value      输入和输出类型
     * @param i_NewValue    赋值的值
     * @param i_BeginIndex  赋值位置，相对于 io_Value 的位置。下标从零开始
     * @param i_SetLen      赋值长度，相对于 io_Value 设置的长度
     * @return              返回实际赋值的最后位置
     */
    public static int setValue(byte [] io_Value ,byte [] i_NewValue ,int i_BeginIndex ,int i_SetLen)
    {
        int v_ValueLen = io_Value.length;
        int v_NewIndex = 0;
        
        for (int v_Index=i_BeginIndex; v_NewIndex<i_SetLen && v_NewIndex<i_NewValue.length && v_Index<v_ValueLen; v_Index++ ,v_NewIndex++)
        {
            io_Value[v_Index] = i_NewValue[v_NewIndex];
        }
        
        return i_BeginIndex + v_NewIndex;
    }
    
    
    
    /**
     * 重载。字符串转为字节数组
     * 
     * @param i_Value
     * @return
     */
    public static byte [] toByte(String i_Value)
    {
        return stringToByte(i_Value);
    }
    
    
    
    /**
     * 重载。字符串转为字节数组
     * 
     * @param i_Value
     * @param i_CharsetName  java.nio.charset.Charset
     * @return
     */
    public static byte [] toByte(String i_Value ,String i_CharsetName)
    {
        return stringToByte(i_Value ,i_CharsetName);
    }
    
    
    
    /**
     * 字符串转为字节数组
     * 
     * @param i_Value
     * @param i_CharsetName  java.nio.charset.Charset
     * @return
     */
    public static byte [] stringToByte(String i_Value ,String i_CharsetName)
    {
        try
        {
            return i_Value.getBytes(i_CharsetName);
        }
        catch (Exception exce)
        {
            exce.printStackTrace();
        }
        
        return new byte[0];
    }
    
    
    
    /**
     * 字符串转为字节数组
     * 
     * @param i_Value
     * @return
     */
    public static byte [] stringToByte(String i_Value)
    {
        return i_Value.getBytes();
    }
    
    
    
    /**
     * 字节数组转为字符串
     * 
     * @param i_Value
     * @return
     */
    public static String byteToString(byte [] i_Value)
    {
        return new String(i_Value);
    }
    
    
    
    /**
     * 字节数组转为字符串
     * 
     * @param i_Value
     * @return
     */
    public static String byteToString(byte [] i_Value ,int i_Offset ,int i_Length)
    {
        return new String(i_Value ,i_Offset ,i_Length);
    }
    
    
    
    /**
     * 字节数组转为字符串
     * 
     * @param i_Value
     * @param i_CharsetName  java.nio.charset.Charset
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String byteToString(byte [] i_Value ,String i_CharsetName) throws UnsupportedEncodingException
    {
        return new String(i_Value ,i_CharsetName);
    }
    
    
    
    /**
     * 字节数组转为字符串
     * 
     * @param i_Value
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String byteToString(byte [] i_Value ,int i_Offset ,int i_Length ,String i_CharsetName) throws UnsupportedEncodingException
    {
        return new String(i_Value ,i_Offset ,i_Length ,i_CharsetName);
    }
    
    
    
    /**
     * 将字节数组编码成16进制数字,适用于所有字符（包括中文）
     * 
     * @param i_Str
     * @return
     */
    public static String bytesToHex(byte [] i_Bytes)
    {
        return StringHelp.bytesToHex(i_Bytes);
    }
    
    
    
    /**
     * 将字节数组编码成16进制数字,适用于所有字符（包括中文）
     * 
     * @param i_Str
     * @return
     */
    public final static String bytesToHex(byte [] i_Bytes ,int i_Offset ,int i_Length)
    {
        return StringHelp.bytesToHex(i_Bytes ,i_Offset ,i_Length);
    }
    
    
    
    /**
     * 将16进制数字解码成字节数组,适用于所有字符（包括中文）
     * 
     * @param i_HexStr
     * @return
     */
    public static byte [] hexToBytes(String i_HexStr)
    {
        return StringHelp.hexToBytes(i_HexStr);
    }
    
    
    
    /**
     * byte [] 转 Byte []
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-12-10
     * @version     v1.0
     * 
     * @param i_Bytes
     * @return
     */
    public static Byte [] byteToByte(byte [] i_Bytes)
    {
        Byte [] v_Ret = new Byte[i_Bytes.length];
        
        for (int x=i_Bytes.length - 1; x>=0; x--)
        {
            v_Ret[x] = i_Bytes[x];
        }
        
        return v_Ret;
    }
    
    
    
    /**
     * Byte [] 转 byte []
     * 
     * @author      ZhengWei(HY)
     * @createDate  2021-12-10
     * @version     v1.0
     * 
     * @param i_Bytes
     * @return
     */
    public static byte [] byteToByte(Byte [] i_Bytes)
    {
        byte [] v_Ret = new byte[i_Bytes.length];
        
        for (int x=i_Bytes.length - 1; x>=0; x--)
        {
            v_Ret[x] = i_Bytes[x];
        }
        
        return v_Ret;
    }
    
    
    
    /**
     * 将输入流（文件流）转为二进制数据
     * 
     * 注：内部会自动关闭流。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2019-06-18
     * @version     v1.0
     *
     * @param i_Input        输入流（文件流）
     * @param i_BufferSize   读取流时，用的缓存大小
     * @return
     */
    public static byte [] toByte(InputStream i_Input)
    {
        return toByte(i_Input ,1024 * 100);
    }
    
    
    
    /**
     * 将输入流（文件流）转为二进制数据
     * 
     * 注：内部会自动关闭流。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2019-06-18
     * @version     v1.0
     *
     * @param i_Input        输入流（文件流）
     * @param i_BufferSize   读取流时，用的缓存大小
     * @return
     */
    public static byte [] toByte(InputStream i_Input ,int i_BufferSize)
    {
        ByteArrayOutputStream v_Out    = new ByteArrayOutputStream();
        byte []               v_Buffer = new byte[i_BufferSize];
        int                   v_Len    = 0;
        
        if ( i_Input != null )
        {
            try
            {
                while ( (v_Len = i_Input.read(v_Buffer)) != -1 )
                {
                    v_Out.write(v_Buffer ,0 ,v_Len);
                }
            }
            catch (Exception exce)
            {
                exce.printStackTrace();
            }
            finally
            {
                try
                {
                    i_Input.close();
                }
                catch (Exception exce)
                {
                    exce.printStackTrace();
                }
            }
        }
        
        return v_Out.toByteArray();
    }
    
    
    
    /**
     * 重载。整数转为字节数组
     * 
     * @param i_Value
     * @return
     */
    public static byte [] toByte(int i_Value)
    {
        return intToByte(i_Value);
    }
    
    
    
    /**
     * 重载。短整数转为字节数组
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-01-03
     * @version     v1.0
     * 
     * @param i_Value
     * @return
     */
    public static byte [] toByte(short i_Value)
    {
        return shortToByte(i_Value);
    }
    
    
    
    /**
     * 16位整数转为字节数组
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-01-03
     * @version     v1.0
     * 
     * @param i_Value
     * @return
     */
    public static byte [] shortToByte(short i_Value)
    {
        byte [] v_Ret = new byte[2];
        
        v_Ret[0] = (byte)((i_Value >>>  8) & 0xFF);
        v_Ret[1] = (byte)((i_Value >>>  0) & 0xFF);
        
        return v_Ret;
    }
    
    
    
    /**
     * 32位整数转为字节数组
     * 
     * @param i_Value
     * @return
     * @see RandomAccessFile.writeInt()
     */
    public static byte [] intToByte(int i_Value)
    {
        byte [] v_Ret = new byte[4];
        
        v_Ret[0] = (byte)((i_Value >>> 24) & 0xFF);
        v_Ret[1] = (byte)((i_Value >>> 16) & 0xFF);
        v_Ret[2] = (byte)((i_Value >>>  8) & 0xFF);
        v_Ret[3] = (byte)((i_Value >>>  0) & 0xFF);
        
        return v_Ret;
    }
    
    
    
    /**
     * 字节数组转为16位整数。
     * 
     * 注：这里的返回值类型没有用 short ,因为是为了将0xFFFF转为65535，而不是-1。
     *    如果想要将0xFFFF转为-1时，只须将本方法的结果强转为short即可。
     *    
     * @author      ZhengWei(HY)
     * @createDate  2025-01-03
     * @version     v1.0
     * 
     * @param i_Value
     * @return
     */
    public static int byteToShort(byte [] i_Value)
    {
        if ( i_Value.length >= 2 )
        {
            return (((i_Value[0] & 0xFF) << 8) + ((i_Value[1] & 0xFF) << 0));
        }
        else if ( i_Value.length == 1 )
        {
            return (((i_Value[0] & 0xFF) << 0));
        }
        else
        {
            return 0;
        }
    }
    
    
    
    /**
     * 字节数组转为32位整数。
     * 
     * @param i_Value
     * @return
     * @see RandomAccessFile.readInt()
     */
    public static int byteToInt(byte [] i_Value)
    {
        if ( i_Value.length >= 4 )
        {
            return (((i_Value[0] & 0xFF) << 24) + ((i_Value[1] & 0xFF) << 16) + ((i_Value[2] & 0xFF) << 8) + ((i_Value[3] & 0xFF) << 0));
        }
        else if ( i_Value.length == 3 )
        {
            return (((i_Value[0] & 0xFF) << 16) + ((i_Value[1] & 0xFF) << 8) + ((i_Value[2] & 0xFF) << 0));
        }
        else if ( i_Value.length == 2 )
        {
            return (((i_Value[0] & 0xFF) << 8) + ((i_Value[1] & 0xFF) << 0));
        }
        else if ( i_Value.length == 1 )
        {
            return (((i_Value[0] & 0xFF) << 0));
        }
        else
        {
            return 0;
        }
    }
    
    
    
    /**
     * 将字节数组转为无符号的整数。如 -73(byte) 转换为 183(int)
     * 
     * @author      ZhengWei(HY)
     * @createDate  2020-06-16
     * @version     v1.0
     *
     * @param i_Value
     * @return
     */
    public static int [] byteToIntArray(byte [] i_Value)
    {
        if ( Help.isNull(i_Value) )
        {
            return null;
        }
        
        int [] v_Ret = new int[i_Value.length];
        for (int i=0; i<i_Value.length; i++)
        {
            v_Ret[i] = i_Value[i] & 0xFF;
        }
        
        return v_Ret;
    }
    
    
    
    /**
     * 将无符号整数转为有符号的字节数组。如 183(int) 转换为 -73(byte)
     * 
     * @author      ZhengWei(HY)
     * @createDate  2020-06-16
     * @version     v1.0
     *
     * @param i_Value
     * @return
     */
    public static byte [] intToByteArray(int [] i_Value)
    {
        if ( Help.isNull(i_Value) )
        {
            return null;
        }
        
        byte [] v_Ret = new byte[i_Value.length];
        for (int i=0; i<i_Value.length; i++)
        {
            v_Ret[i] = (byte)i_Value[i];
        }
        
        return v_Ret;
    }
    
    
    
    /**
     * 将无符号整数转为有符号的字节数组。如 183(int) 转换为 -73(byte)
     * 
     * @author      ZhengWei(HY)
     * @createDate  2025-01-03
     * @version     v1.0
     *
     * @param i_Value
     * @return
     */
    public static byte [] shortToByteArray(short [] i_Value)
    {
        if ( Help.isNull(i_Value) )
        {
            return null;
        }
        
        byte [] v_Ret = new byte[i_Value.length];
        for (int i=0; i<i_Value.length; i++)
        {
            v_Ret[i] = (byte)i_Value[i];
        }
        
        return v_Ret;
    }
    
    
    
    /**
     * 重载。长整型转为字节数组
     * 
     * @param i_Value
     * @return
     */
    public static byte [] toByte(long i_Value)
    {
        return longToByte(i_Value);
    }
    
    
    
    /**
     * 长整型转为字节数组
     * 
     * @param i_Value
     * @return
     * @see RandomAccessFile.writeLong()
     */
    public static byte [] longToByte(long i_Value)
    {
        byte [] v_Ret = new byte[8];
        
        v_Ret[0] = (byte)((i_Value >>> 56) & 0xFF);
        v_Ret[1] = (byte)((i_Value >>> 48) & 0xFF);
        v_Ret[2] = (byte)((i_Value >>> 40) & 0xFF);
        v_Ret[3] = (byte)((i_Value >>> 32) & 0xFF);
        v_Ret[4] = (byte)((i_Value >>> 24) & 0xFF);
        v_Ret[5] = (byte)((i_Value >>> 16) & 0xFF);
        v_Ret[6] = (byte)((i_Value >>>  8) & 0xFF);
        v_Ret[7] = (byte)((i_Value >>>  0) & 0xFF);
        
        return v_Ret;
    }
    
    
    
    /**
     * 字节数组转为长整型。
     * 
     * @param i_Value
     * @return
     */
    public static long byteToLong(byte [] i_Value)
    {
        if ( i_Value.length >= 8 )
        {
            return (((long)(byteToInt(substr(i_Value ,0 ,4)))) << 32) + ((byteToInt(substr(i_Value ,4 ,4)) & 0xFFFFFFFFL));
        }
        else if ( i_Value.length <= 4 )
        {
            return (byteToInt(substr(i_Value ,4 ,4)) & 0xFFFFFFFFL);
        }
        else
        {
            return (((long)(byteToInt(substr(i_Value ,0 ,i_Value.length - 4)))) << 32) + ((byteToInt(substr(i_Value ,i_Value.length - 4 ,4)) & 0xFFFFFFFFL));
        }
    }
    
    
    
    /**
     * 重载。时间类型转为字节数组
     * 
     * @param i_Value
     * @return
     */
    public static byte [] toByte(Date i_Value)
    {
        return dateToByte(i_Value);
    }
    
    
    
    /**
     * 重载。时间类型转为字节数组
     * 
     * @param i_Value
     * @return
     */
    public static byte [] toByte(java.util.Date i_Value)
    {
        return dateToByte(i_Value);
    }
    
    
    
    /**
     * 时间类型转为字节数组
     * 
     * @param i_Value
     * @return
     */
    public static byte [] dateToByte(Date i_Value)
    {
        return longToByte(i_Value.getTime());
    }
    
    
    
    /**
     * 时间类型转为字节数组
     * 
     * @param i_Value
     * @return
     */
    public static byte [] dateToByte(java.util.Date i_Value)
    {
        return longToByte(i_Value.getTime());
    }
    
    
    
    /**
     * 字节数组转为时间类型。
     * 
     * @param i_Value
     * @return
     */
    public static Date byteToDate(byte [] i_Value)
    {
        return new Date(byteToLong(i_Value));
    }
    
    
    
    /**
     * 重载。浮点数转为字节数组
     * 
     * @param i_Value
     * @return
     */
    public static byte [] toByte(double i_Value)
    {
        return doubleToByte(i_Value);
    }
    
    
    
    /**
     * 浮点数转为字节数组
     * 
     * @param i_Value
     * @return
     */
    public static byte [] doubleToByte(double i_Value)
    {
        return longToByte(Double.doubleToLongBits(i_Value));
    }
    
    
    
    /**
     * 字节数组转为浮点数。
     * 
     * @param i_Value
     * @return
     */
    public static double byteToDouble(byte [] i_Value)
    {
        return Double.longBitsToDouble(byteToLong(i_Value));
    }
    
    
    
    /**
     * 与最大值Byte.MAX_VALUE[0111 1111]做异或运算
     * 
     * 两次这样的运算结果应当等于原值
     * 
     * 返回结果会创建新的内存空间
     * 
     * @param i_Value
     * @return
     */
    public static byte [] xorMV(byte [] i_Value)
    {
        return xorMV(i_Value ,0 ,i_Value.length);
    }
    
    
    
    /**
     * 与最大值Byte.MAX_VALUE[0111 1111]做异或运算
     * 
     * 两次这样的运算结果应当等于原值
     * 
     * 返回结果会创建新的内存空间
     * 
     * @param i_Value
     * @param i_StartIndex  异或运算的开始下标位置
     * @param i_Len         异或运算的从开始下标算起运算多少个
     * @return
     */
    public static byte [] xorMV(byte [] i_Value ,int i_StartIndex ,int i_Len)
    {
        byte [] v_Result   = new byte[i_Value.length];
        int     v_MaxIndex = i_StartIndex + i_Len;
        
        for (int v_Index=0; v_Index<i_Value.length; v_Index++)
        {
            if ( i_StartIndex <= v_Index && v_Index < v_MaxIndex )
            {
                v_Result[v_Index] = (byte) (i_Value[v_Index] ^ Byte.MAX_VALUE);
            }
            else
            {
                v_Result[v_Index] = i_Value[v_Index];
            }
        }
        
        return v_Result;
    }
    
    
    
    public static void main(String [] args)
    {
        System.out.println("-- To Integer:\t" + ByteHelp.byteToInt(ByteHelp.intToByte(Integer.MAX_VALUE)));
        
        System.out.println("-- To Long:\t\t"  + ByteHelp.byteToLong(ByteHelp.longToByte(Long.MAX_VALUE)));
        
        System.out.println("-- To Double:\t"  + ByteHelp.byteToDouble(ByteHelp.doubleToByte(Double.MAX_VALUE)));
        
        System.out.println("-- To String:\t"  + ByteHelp.byteToString(ByteHelp.stringToByte("Hello World!")));
        
        System.out.println("-- To Date:\t\t"  + ByteHelp.byteToDate(ByteHelp.dateToByte(new Date())).getFull());
        
        System.out.println("-- LPad:\t\t["    + new String(ByteHelp.lpad("Hello World!".getBytes() ,32 ," ".getBytes())) + "]");
        
        System.out.println("-- RPad:\t\t["    + new String(ByteHelp.rpad("Hello World!".getBytes() ,32 ," ".getBytes())) + "]");
        
        System.out.println("-- SubStr:\t\t"  + ByteHelp.byteToString(ByteHelp.substr(ByteHelp.toByte("Hello World!") ,0 ,5)));
        
        System.out.println("-- SubStr:\t\t"  + ByteHelp.byteToString(ByteHelp.substr(ByteHelp.toByte("Hello World!") ,6)));
        
        byte [] v_Value = ByteHelp.toByte("################");
        int     v_ValueIndex = ByteHelp.setValue(v_Value ,ByteHelp.toByte("Hello World!") ,2);
        System.out.println("-- SetValue:\t["  + ByteHelp.byteToString(v_Value) + "]\t\tValueIndex=" + v_ValueIndex);
        
        // 两次与最大值127的异或结果应当等于原值
        System.out.println("-- XOR XOR:\t\t[" + ByteHelp.byteToString(ByteHelp.xorMV(ByteHelp.xorMV(ByteHelp.toByte("Hello World!")))) + "]");
    }
    
}
