package org.hy.common.app;

import java.util.Hashtable;
import java.util.Map;

import org.hy.common.Help;





/**
 * 通用应用程序启动时的 main 方法入参解析
 *
 * @author   ZhengWei(HY)
 * @version  V1.0  2011-06-05
 */
public class AppParameter implements java.io.Serializable
{
	
    private static final long serialVersionUID = 477626033146875701L;

    

    protected static String ParamSplit = "=";
	
	
	/**
	 * 所有参数信息
	 * 
	 * Map.Key = /help      -- 显示帮助信息
	 * Map.Key = /h         -- /help 的简写
	 * Map.Key = /?         -- /help 的简写
	 * 
	 * Map.Key = /version   -- 显示版本信息
	 * Map.Key = /v         -- /version 的简写
	 */
	protected Map<String ,String> parameters;
	
	
	
	public AppParameter()
	{
		this.parameters = new Hashtable<String ,String>();
	}
	
	
	public AppParameter(String [] args)
	{
		this();
		
		this.parse(args);
	}
	
	
	/**
	 * 判定参数是否存在
	 * 
	 * @param i_ParamName  参数名称
	 * @return
	 */
	public boolean isExists(String i_ParamName)
	{
		if ( Help.isNull(this.parameters) || Help.isNull(i_ParamName) )
		{
			return false;
		}
		
		if ( this.parameters.containsKey(i_ParamName.trim().toLowerCase()) )
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	
	/**
	 * 获取参数的值
	 * 
	 * @param i_ParamName  参数名称
	 * @return
	 */
	public String getParamValue(String i_ParamName)
	{
		if ( this.isExists(i_ParamName) )
		{
			String v_ParamValue = this.parameters.get(i_ParamName.trim().toLowerCase());
			
			if ( Help.isNull(v_ParamValue) )
			{
				return null;
			}
			else
			{
				return v_ParamValue;
			}
		}
		else
		{
			return null;
		}
	}
	
	
	/**
	 * 是否显示帮助信息
	 * 
	 * @return
	 */
	public boolean isShowHelp()
	{
		if ( Help.isNull(this.parameters) )
		{
			return false;
		}
		
		if ( this.parameters.containsKey("/help") )
		{
			return true;
		}
		else if ( this.parameters.containsKey("/h") )
		{
			return true;
		}
		else if ( this.parameters.containsKey("/?") )
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	
	/**
	 * 是否显示帮助信息
	 * 
	 * @return
	 */
	public boolean isShowVersion()
	{
		if ( Help.isNull(this.parameters) )
		{
			return false;
		}
		
		if ( this.parameters.containsKey("/version") )
		{
			return true;
		}
		else if ( this.parameters.containsKey("/v") )
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	
	/**
	 * 入参解析
	 * 
	 * @param args
	 */
	public void parse(String [] args)
	{
		if ( Help.isNull(args) )
		{
			return;
		}
		
		
		for (int i=0; i<args.length; i++)
		{
			String [] v_TempArr = args[i].split(ParamSplit);
			String    v_Key     = "";
			String    v_Value   = "";
			
			if ( v_TempArr.length >= 1 )
			{
				v_Key = v_TempArr[0].toLowerCase();
			}
			
			if ( v_TempArr.length >= 2 )
			{
				v_Value = v_TempArr[1];
			}
			
			if ( this.parameters.containsKey(v_Key) )
			{
				this.parameters.remove(v_Key);
			}
			
			this.parameters.put(v_Key, v_Value);
		}
	}
	
}
