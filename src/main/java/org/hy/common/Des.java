package org.hy.common;


import java.security.Key;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;





/**
 * DES是一种对称加密算法，所谓对称加密算法:加密和解密使用相同的秘钥的算法
 *
 * @author      ZhengWei(HY)
 * @createDate  2019-08-20
 * @version     v1.0
 */
public class Des
{
    private Key key;
    private static String $CHARSETNAME = "UTF-8";
    private static String $ALGORITHM   = "DES";
    
    
    
    /**
     * Des加密
     * 
     * @author      ZhengWei(HY)
     * @createDate  2019-08-20
     * @version     v1.0
     *
     * @param i_PrivateKey  秘钥key
     */
    public Des(String i_PrivateKey)
    {
        try
        {
            //生成DES算法对象
            KeyGenerator generator    = KeyGenerator.getInstance($ALGORITHM);
            //运用SHA1安全策略
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            //设置上密钥种子
            secureRandom.setSeed(i_PrivateKey.getBytes());
            //初始化基于SHA1的算法对象
            generator.init(secureRandom);
            //生成密钥对象
            key = generator.generateKey();
            generator=null;
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    
    
    
    /**
     * 获取加密的信息
     * @param str
     * @return
     */
    public String encrypt(String str)
    {
        //基于BASE64编码，接收byte[]并转换成String
        try
        {
            // 按UTF8编码
            byte[] bytes = str.getBytes($CHARSETNAME);
            // 获取加密对象
            Cipher cipher = Cipher.getInstance($ALGORITHM);
            // 初始化密码信息
            cipher.init(Cipher.ENCRYPT_MODE, this.key);
            // 加密
            byte[] doFinal = cipher.doFinal(bytes);
            // byte[]to encode好的String并返回
            return new String(Base64.getEncoder().encode(doFinal) ,"UTF-8");
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    
    
    
    /**
     * 获取解密之后的信息
     * 
     * @param str
     * @return
     */
    public String decrypt(String str)
    {
        // 基于BASE64编码，接收byte[]并转换成String
        try
        {
            // 将字符串decode成byte[]
            byte[] bytes = Base64.getDecoder().decode(str);
            // 获取解密对象
            Cipher cipher = Cipher.getInstance($ALGORITHM);
            // 初始化解密信息
            cipher.init(Cipher.DECRYPT_MODE, this.key);
            // 解密
            byte[] doFinal = cipher.doFinal(bytes);
            // 返回解密之后的信息
            return new String(doFinal, $CHARSETNAME);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    
}
