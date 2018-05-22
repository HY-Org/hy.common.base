package org.hy.common;

import java.util.ArrayList;
import java.util.List;





/**
 * 方法返回类型的范型信息
 * 
 * @author      ZhengWei(HY)
 * @version     v2.0  
 * @createDate  2014-12-27
 */
public class GenericsReturn implements java.io.Serializable
{
    
    private static final long serialVersionUID = 2034377815234837877L;
    
    

    /** 范型的类型 */
    private Class<?>         genericType;
    
    /** 范型主人的类型。有可能是多级的 */
    private List<Class<?>>   masterTypes;

    
    
    public synchronized void addMaster(Class<?> i_Class)
    {
        if ( i_Class == null )
        {
            return;
        }
        
        if ( this.masterTypes == null )
        {
            this.masterTypes = new ArrayList<Class<?>>();
        }
        
        this.masterTypes.add(i_Class);
    }
    
    
    public Class<?> getGenericType()
    {
        return genericType;
    }

    
    public void setGenericType(Class<?> genericType)
    {
        this.genericType = genericType;
    }

    
    public List<Class<?>> getMasterTypes()
    {
        return masterTypes;
    }

    
    public void setMasterTypes(List<Class<?>> masterTypes)
    {
        this.masterTypes = masterTypes;
    }
    
}