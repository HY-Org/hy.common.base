package org.hy.common.junit;

import org.hy.common.Des;

public class JU_Des
{
    
    public static void main(String[] args) 
    {
        Des v_Des = new Des("my");
        // 加密
        System.out.println(v_Des.encrypt("root"));
        System.out.println(v_Des.encrypt("123456"));
        System.out.println(v_Des.encrypt("中国"));
        // 解密
        System.out.println(v_Des.decrypt(v_Des.encrypt("root")));
        System.out.println(v_Des.decrypt(v_Des.encrypt("123456")));
        System.out.println(v_Des.decrypt(v_Des.encrypt("中国")));
    }
    
}
