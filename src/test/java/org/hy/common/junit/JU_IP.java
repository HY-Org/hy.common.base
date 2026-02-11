package org.hy.common.junit;

import org.hy.common.Help;

public class JU_IP
{
    
    public static void main(String [] args)
    {
        String startIP = "1.0.1.0";
        String endIP = "1.0.3.255";
        long start = Help.ipToLong(startIP);
        long end = Help.ipToLong(endIP);
        System.out.println("IP地址范围: " + startIP + " - " + endIP);
        System.out.println("IP地址总数: " + (end - start + 1));
        // 遍历并处理每个IP地址（这里只是打印，你可以替换为其他操作）
        for (long i = start; i <= end; i++)
        {
            String ip = Help.ipToString(i);
            // 这里可以对每个IP进行处理
            System.out.println(ip);
        }
        
        if ( startIP.endsWith(".0") )
        {
            System.out.println(".0");
        }
        
        System.out.println(Help.ipToString(start + 256));
    }

}
