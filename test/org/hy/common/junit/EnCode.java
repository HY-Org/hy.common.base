package org.hy.common.junit;

import org.hy.common.StringHelp;
import org.hy.common.app.AppParameter;

public class EnCode
{
    
    public static void main(String [] i_Args)
    {
        AppParameter v_AppParams = new AppParameter(i_Args);
        
        if ( !v_AppParams.isExists("code") )
        {
            System.out.println("not find code.");
            return;
        }
        
        for (String v_Arg : i_Args)
        {
            if ( v_Arg.indexOf("=") >= 0 )
            {
                String [] v_Params = v_Arg.split("=");
                if ( v_Params.length >= 2 && "code".equalsIgnoreCase(v_Params[0]) )
                {
                    StringBuilder v_Buffer = new StringBuilder();
                    
                    for (int i=1; i<v_Params.length; i++)
                    {
                        v_Buffer.append(v_Params[i]);
                    }
                    
                    System.out.println(StringHelp.toCode(v_Buffer.toString() ,1));
                }
            }
        }
    }
    
    
    
    
    
}
