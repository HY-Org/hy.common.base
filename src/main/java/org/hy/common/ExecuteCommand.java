package org.hy.common;

import java.util.List;





/**
 * 执行命令
 * 
 * @author      ZhengWei(HY)
 * @version     v1.0  
 * @createDate  2018-10-10
 */
public class ExecuteCommand
{
    
    /**
     * java -cp hy.common.base.jar -Dfile.encoding=UTF-8 org.hy.common.ExecuteCommand true 5000 /Volumes/HY_HD_06/WSS/WorkSpace_SearchDesktop/hy.common.base/Maven.Deploy.Mac.Base.sh
     * 
     * @param args   [0] 是否返回执行结果
     *               [1] 执行命令后等待多少微秒后再退出
     *               [2] 执行的命令脚本全路径
     */
    public static void main(String [] args)
    {
        try
        {
            System.out.println("-- " + Date.getNowTime().getFullMilli() + " 执行[" + args[2] + "]命令文件");
            
            List<String> v_Ret = Help.executeCommand(false ,Boolean.parseBoolean(args[0]) ,args[2]);
            if ( !Help.isNull(v_Ret) )
            {
                Help.print(v_Ret);
            }
            
            System.out.println("-- " + Date.getNowTime().getFullMilli() + " 执行[" + args[2] + "]命令文件... ...完成。");
            
            if ( args.length >= 3 )
            {
                if ( Help.isNumber(args[1]) )
                {
                    Thread.sleep(Long.valueOf(args[1]));
                }
            }
        }
        catch (Exception exce)
        {
            exce.printStackTrace();
        }
    }
    
}
