package org.hy.common.xml;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.hy.common.Help;
import org.hy.common.MethodReflect;
import org.hy.common.Serializable;
import org.hy.common.TablePartition;





/**
 * (默认的)将对象简单序列化，具备并按顺序读取的统一接口。
 * 
 * 属性为getter和is方法集合体(本类的，具体继承类的)
 * 
 * 提供了克隆方法，但没有直接去实现 java.lang.Cloneable 接口。
 *   1. 支持：克隆同类
 *   2. 支持：克隆父类
 *   3. 支持：克隆其它任一SerializableDef接口的实现者
 *   4. 支持：克隆Map集合
 * 
 * 一切只为了方便
 * 
 * 注意：1. 只有Getter方法，是不会被序列的。（只要 Setter 与 Getter(Is) 方法成对出现的 Getter(Is) 方法）
 * 
 *      2. 本类abstract的主要目的是：无继承就无法实例化。
 *
 * @author   ZhengWei(HY)
 * @version  V1.0  2013-11-18
 *           V2.0  2017-06-09  添加：this.initNotNull(...) 初始化'我自己' (将自己设置成于别人对象一样)，但只初始非空的有效的值。
 *           V3.0  2017-06-13  优化：this.init()方法的中用Map集合初始化'我自己'的执行性能，并不再区分Map.key大小写的匹配模式。
 *           V4.0  2022-05-17  优化：无继承关系两对象的相互初始化方法的性能
 */
public abstract class SerializableDef extends SerializableClass implements Serializable
{
    
    private static final long serialVersionUID = -2129494614381357099L;
    
    /** Setter方法的动态缓存。为 Serializable 接口的约束，这里的元素类型，不要直接写成Method，但元素类型就是Method */
    private static final TablePartition<Class<?> ,Object> $SetterListCache = new TablePartition<Class<?> ,Object>();



    public SerializableDef()
    {
        super(null);
    }
    
    
    
    /**
     * 获取指定顺序上的属性值
     * 
     * @param i_PropertyIndex  下标从0开始
     * @return
     */
    @Override
    public Object gatPropertyValue(int i_PropertyIndex)
    {
        return super.gatPropertyValue(i_PropertyIndex ,this);
    }
    
    
    
    /**
     * 获取指定顺序上对应的Setter
     * 
     * @param i_PropertyIndex
     */
    protected synchronized Method getSetterMethod(int i_PropertyIndex)
    {
        List<Object> v_SettersCache = $SetterListCache.get(this.getClass());
        if ( v_SettersCache == null )
        {
            for (int x=0; x<this.gatPropertySize(); x++)
            {
                $SetterListCache.putRow(this.getClass() ,0);
            }
            
            v_SettersCache = $SetterListCache.get(this.getClass());
        }
        
        Object v_Ret = v_SettersCache.get(i_PropertyIndex);
        if ( v_Ret instanceof Integer )
        {
            String v_Name = this.propertyMethods.get(i_PropertyIndex).toMethod(this).getName();
            
            // 只要 Setter 与 Getter(Is) 方法成对出现的 Getter(Is) 方法
            if ( v_Name.startsWith("get") )
            {
                v_Name = "s"   + v_Name.substring(1);
            }
            else
            {
                v_Name = "set" + v_Name.substring(2);
            }
            
            List<Method> v_Setters = MethodReflect.getMethods(this.getClass() ,v_Name ,1);
            if ( v_Setters.size() >= 1 )
            {
                v_Ret = v_Setters.get(0);
                v_SettersCache.set(i_PropertyIndex ,v_Ret);
                return (Method)v_Ret;
            }
        }
        
        return (Method)v_Ret;
    }
    
    
    
    /**
     * 调用指定顺序上对应的Setter方法，设置属性值
     * 
     * @param i_PropertyIndex
     * @param i_Value
     */
    protected void setPropertyValue(int i_PropertyIndex ,Object i_Value)
    {
        this.setPropertyValue(i_PropertyIndex ,i_Value ,this);
    }
    
    
    
    /**
     * 调用指定顺序上对应的Setter方法，设置属性值（指定了实例对象）
     * 
     * @param i_PropertyIndex
     * @param i_Value
     * @param i_Obj            被设置属性值的实例对象
     */
    protected void setPropertyValue(int i_PropertyIndex ,Object i_Value ,Object i_Obj)
    {
        try
        {
            if ( i_Value == null )
            {
                this.getSetterMethod(i_PropertyIndex).invoke(i_Obj ,new Object []{null});
            }
            else
            {
                Method v_SetterMethod = this.getSetterMethod(i_PropertyIndex);
                
                if ( i_Value.getClass() == v_SetterMethod.getParameterTypes()[0] )
                {
                    v_SetterMethod.invoke(i_Obj ,i_Value);
                }
                else if ( i_Value.getClass() == String.class )
                {
                    v_SetterMethod.invoke(i_Obj ,Help.toObject(v_SetterMethod.getParameterTypes()[0] ,i_Value.toString()));
                }
                else if ( Boolean.class == i_Value.getClass()
                       && boolean.class == v_SetterMethod.getParameterTypes()[0] )
                {
                    v_SetterMethod.invoke(i_Obj ,((Boolean)i_Value).booleanValue());
                }
                else if ( Integer.class == i_Value.getClass()
                       &&     int.class == v_SetterMethod.getParameterTypes()[0] )
                {
                    v_SetterMethod.invoke(i_Obj ,((Integer)i_Value).intValue());
                }
                else if ( Long.class == i_Value.getClass()
                       && long.class == v_SetterMethod.getParameterTypes()[0] )
                {
                    v_SetterMethod.invoke(i_Obj ,((Long)i_Value).longValue());
                }
                else if ( Double.class == i_Value.getClass()
                       && double.class == v_SetterMethod.getParameterTypes()[0] )
                {
                    v_SetterMethod.invoke(i_Obj ,((Double)i_Value).doubleValue());
                }
                else if ( Float.class == i_Value.getClass()
                       && float.class == v_SetterMethod.getParameterTypes()[0] )
                {
                    v_SetterMethod.invoke(i_Obj ,((Float)i_Value).floatValue());
                }
                else if ( BigDecimal.class == i_Value.getClass() )
                {
                    v_SetterMethod.invoke(i_Obj ,(BigDecimal)i_Value);
                }
                else if ( Short.class == i_Value.getClass()
                       && short.class == v_SetterMethod.getParameterTypes()[0] )
                {
                    v_SetterMethod.invoke(i_Obj ,((Short)i_Value).shortValue());
                }
                else if ( Byte.class == i_Value.getClass()
                       && byte.class == v_SetterMethod.getParameterTypes()[0] )
                {
                    v_SetterMethod.invoke(i_Obj ,((Byte)i_Value).byteValue());
                }
                else if ( Character.class == i_Value.getClass()
                       &&      char.class == v_SetterMethod.getParameterTypes()[0] )
                {
                    v_SetterMethod.invoke(i_Obj ,((Character)i_Value).charValue());
                }
                else if ( MethodReflect.isExtendImplement(i_Value.getClass() ,v_SetterMethod.getParameterTypes()[0]) )
                {
                    v_SetterMethod.invoke(i_Obj ,i_Value);
                }
            }
        }
        catch (Exception exce)
        {
            exce.printStackTrace();
        }
    }
    
    
    
    /**
     * 克隆 (复制数据给别的对象)
     * 
     * 此方法存在主要是为了：减少IF判定，加速处理性能
     * 
     * 主要用于子类的实现 java.lang.Cloneable 接口的情况。
     * 
     * 一切只为了方便
     * 
     * @author   ZhengWei(HY)
     * @version  V1.0  2022-05-17
     * 
     * @param io_CloneNewObj   克隆的复制品
     */
    public <T extends SerializableDef> void cloneSerializable(T io_CloneNewObj)
    {
        if ( io_CloneNewObj == null )
        {
            throw new NullPointerException("Clone new object is null.");
        }
        else if ( io_CloneNewObj.getClass() == this.getClass() )
        {
            this.cloneEqualsClass(io_CloneNewObj);
        }
        else
        {
            int             v_FindIndex = 0;
            SerializableDef v_SerialObj = io_CloneNewObj;
            
            for (int v_ThisIndex = 0; v_ThisIndex < this.gatPropertySize(); v_ThisIndex++)
            {
                for (int v_SerialIndex = v_FindIndex ; v_SerialIndex < v_SerialObj.gatPropertySize(); v_SerialIndex++)
                {
                    if ( v_SerialObj.gatPropertyName(v_SerialIndex).equals(this.gatPropertyName(v_ThisIndex)) )
                    {
                        Object v_Value = this.gatPropertyValue(v_ThisIndex);
                        
                        if ( v_Value != null  )
                        {
                            if ( MethodReflect.isExtendImplement(v_Value ,SerializableDef.class) )
                            {
                                // 当对象的属性也是一个SerializableDef类时，进行深度克隆
                                SerializableDef v_FieldSerial      = (SerializableDef)v_Value;
                                Object          v_FieldSerialClone = v_FieldSerial.newObject();
                                v_FieldSerial.cloneSerializable((SerializableDef)v_FieldSerialClone);
                                v_Value = v_FieldSerialClone;
                            }
                        }
                        
                        v_SerialObj.setPropertyValue(v_SerialIndex ,v_Value ,io_CloneNewObj);
                        
                        // gatPropertyName() 集合中的方法缓存，均是惟一性（无参的getter，配对一个仅一个参数的setter）。所以，只要是方法名称匹配了，就认定为找到了
                        v_FindIndex++;
                        break;
                    }
                }
            }
        }
    }
    
    
    
    /**
     * 克隆 (仅对两对象类型一样时)
     * 
     * 此方法存在主要是为了：减少IF判定，加速处理性能
     * 
     * 主要用于子类的实现 java.lang.Cloneable 接口的情况。
     * 
     * 一切只为了方便
     * 
     * @author   ZhengWei(HY)
     * @version  V1.0  2022-05-17
     * 
     * @param io_CloneNewObj   克隆的复制品
     */
    private void cloneEqualsClass(Object io_CloneNewObj)
    {
        for (int i=0; i<this.gatPropertySize(); i++)
        {
            Object v_Value = this.gatPropertyValue(i);
            
            if ( v_Value != null  )
            {
                if ( MethodReflect.isExtendImplement(v_Value ,SerializableDef.class) )
                {
                    // 当对象的属性也是一个SerializableDef类时，进行深度克隆
                    SerializableDef v_FieldSerial      = (SerializableDef)v_Value;
                    Object          v_FieldSerialClone = v_FieldSerial.newObject();
                    v_FieldSerial.clone(v_FieldSerialClone);
                    v_Value = v_FieldSerialClone;
                }
            }
            
            this.setPropertyValue(i ,v_Value ,io_CloneNewObj);
        }
    }
    
    
    
    /**
     * 克隆 (复制数据给别的对象)
     * 
     * 主要用于子类的实现 java.lang.Cloneable 接口的情况。
     * 
     * 一切只为了方便
     * 
     * @param io_CloneNewObj   克隆的复制品
     */
    public void clone(Object io_CloneNewObj)
    {
        if ( io_CloneNewObj == null )
        {
            throw new NullPointerException("Clone new object is null.");
        }
        else if ( io_CloneNewObj.getClass() == this.getClass() )
        {
            this.cloneEqualsClass(io_CloneNewObj);
        }
        // 把'我自己'克隆为另一个类型的序列化类
        // 主要用于 1. 用不同序列化类转为本类的情况
        else if ( MethodReflect.isExtendImplement(io_CloneNewObj ,SerializableDef.class) )
        {
            this.cloneSerializable((SerializableDef)io_CloneNewObj);
        }
        // 将'我自己'克隆为Map集合
        else if ( io_CloneNewObj instanceof Map || MethodReflect.isExtendImplement(io_CloneNewObj ,Map.class) )
        {
            @SuppressWarnings("unchecked")
            Map<Object ,Object> v_Datas     = (Map<Object ,Object>)io_CloneNewObj;
            Map<String ,Object> v_ThisDatas = this.toMap(true);
            
            if ( !Help.isNull(v_ThisDatas) )
            {
                v_Datas.putAll(v_ThisDatas);
            }
        }
        else
        {
            throw new ClassCastException("Clone new object class is different.");
        }
    }
    
    
    
    /**
     * 初始化'我自己' (将自己设置成于别人对象一样)。
     * 
     * 即，克隆动作的另一种观察角度下的执行数据复制。
     * 
     * 1. 用序列化的同类初始化'我自己'
     * 2. 用序列化的另一个类初始化'我自己'
     * 3. 用Map集合初始化'我自己'。不再区分Map.key大小写的匹配模式
     * 
     * 主要用于子类的实现 java.lang.Cloneable 接口的情况。
     * 
     * 一切只为了方便
     * 
     * @param i_InitObj   初始化的参考对象(即被克隆的类)
     */
    public void init(Object i_InitObj)
    {
        if ( i_InitObj == null )
        {
            throw new NullPointerException("Init object is null.");
        }
        else if ( i_InitObj.getClass() == this.getClass() )
        {
            SerializableDef v_SerialObj = (SerializableDef)i_InitObj;
            
            for (int i=0; i<v_SerialObj.gatPropertySize(); i++)
            {
                Object v_InitValue = v_SerialObj.gatPropertyValue(i);
                
                if ( v_InitValue != null  )
                {
                    if ( MethodReflect.isExtendImplement(v_InitValue ,SerializableDef.class) )
                    {
                        // 当对象的属性也是一个SerializableDef类时，进行深度克隆
                        SerializableDef v_FieldSerial      = (SerializableDef)v_InitValue;
                        Object          v_FieldSerialClone = v_FieldSerial.newObject();
                        v_FieldSerial.clone(v_FieldSerialClone);
                        v_InitValue = v_FieldSerialClone;
                    }
                }
                
                this.setPropertyValue(i ,v_InitValue);
            }
        }
        // 用序列化的另一个类初始化'我自己'
        // 主要用于 1. 父类无法强转为子类的情况
        //         2. 用不同序列化类转为本类的情况
        else if ( MethodReflect.isExtendImplement(i_InitObj ,SerializableDef.class) )
        {
            int             v_FindlIndex = 0;
            SerializableDef v_SerialObj  = (SerializableDef)i_InitObj;
            
            for (int v_ThisIndex = 0; v_ThisIndex < this.gatPropertySize(); v_ThisIndex++)
            {
                for (int v_SerialIndex = v_FindlIndex ; v_SerialIndex < v_SerialObj.gatPropertySize(); v_SerialIndex++)
                {
                    if ( v_SerialObj.gatPropertyName(v_SerialIndex).equals(this.gatPropertyName(v_ThisIndex)) )
                    {
                        Object v_InitValue = v_SerialObj.gatPropertyValue(v_SerialIndex);
                        
                        if ( v_InitValue != null  )
                        {
                            if ( MethodReflect.isExtendImplement(v_InitValue ,SerializableDef.class) )
                            {
                                // 当对象的属性也是一个SerializableDef类时，进行深度克隆
                                SerializableDef v_FieldSerial      = (SerializableDef)v_InitValue;
                                Object          v_FieldSerialClone = v_FieldSerial.newObject();
                                v_FieldSerial.cloneSerializable((SerializableDef)v_FieldSerialClone);
                                v_InitValue = v_FieldSerialClone;
                            }
                        }
                        
                        this.setPropertyValue(v_ThisIndex ,v_InitValue);
                        
                        // gatPropertyName() 集合中的方法缓存，均是惟一性（无参的getter，配对一个仅一个参数的setter）。所以，只要是方法名称匹配了，就认定为找到了
                        v_FindlIndex++;
                        break;
                    }
                }
            }
        }
        // 用Map集合初始化'我自己'
        else if ( i_InitObj instanceof Map || MethodReflect.isExtendImplement(i_InitObj ,Map.class) )
        {
            @SuppressWarnings("unchecked")
            Map<Object ,Object> v_Datas = (Map<Object ,Object>)i_InitObj;
            
            for (int v_ThisIndex = 0; v_ThisIndex < this.gatPropertySize(); v_ThisIndex++)
            {
                String v_Key = this.gatPropertyShortName(v_ThisIndex);
                
                // 先通过常规方式快速获取一次，如果没有获取到，再忽略大小写的方式遍历。
                if ( v_Datas.containsKey(v_Key) )
                {
                    this.setPropertyValue(v_ThisIndex ,v_Datas.get(v_Key));
                }
                else
                {
                    for (Entry<Object ,Object> v_Data : v_Datas.entrySet())
                    {
                        if ( null != v_Data.getKey() )
                        {
                            if ( v_Data.getKey().toString().equalsIgnoreCase(v_Key) )
                            {
                                this.setPropertyValue(v_ThisIndex ,v_Data.getValue());
                                break;
                            }
                        }
                    }
                }
            }
        }
        else
        {
            throw new ClassCastException("Clone new object class is different.");
        }
    }
    
    
    
    /**
     * 初始化'我自己' (将自己设置成于别人对象一样)，但只初始非空的有效的值。
     *   1. 只初始非空的有效的值
     *   2. 别人对象属性为空时(null)，不对'我自己'初始化值
     * 
     * 即，克隆动作的另一种观察角度下的执行数据复制。
     * 
     * 1. 用序列化的同类初始化'我自己'
     * 2. 用序列化的另一个类初始化'我自己'
     * 3. 用Map集合初始化'我自己'。不再区分Map.key大小写的匹配模式
     * 
     * 主要用于子类的实现 java.lang.Cloneable 接口的情况。
     * 
     * 一切只为了方便
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-06-09
     * @version     v1.0
     *
     * @param i_InitObj   初始化的参考对象(即被克隆的类)
     */
    public void initNotNull(Object i_InitObj)
    {
        if ( i_InitObj == null )
        {
            throw new NullPointerException("Init object is null.");
        }
        else if ( i_InitObj.getClass() == this.getClass() )
        {
            SerializableDef v_SerialObj = (SerializableDef)i_InitObj;
            
            for (int i=0; i<v_SerialObj.gatPropertySize(); i++)
            {
                Object v_InitValue = v_SerialObj.gatPropertyValue(i);
                
                if ( v_InitValue != null  )
                {
                    if ( MethodReflect.isExtendImplement(v_InitValue ,SerializableDef.class) )
                    {
                        // 当对象的属性也是一个SerializableDef类时，进行深度克隆
                        SerializableDef v_FieldSerial      = (SerializableDef)v_InitValue;
                        Object          v_FieldSerialClone = v_FieldSerial.newObject();
                        
                        if ( this.gatPropertyValue(i) == null )
                        {
                            v_FieldSerial.clone(v_FieldSerialClone);
                            v_InitValue = v_FieldSerialClone;
                            this.setPropertyValue(i ,v_InitValue);
                        }
                        else
                        {
                            ((SerializableDef)this.gatPropertyValue(i)).initNotNull(v_InitValue);
                        }
                    }
                    else
                    {
                        this.setPropertyValue(i ,v_InitValue);
                    }
                }
            }
        }
        // 用序列化的另一个类初始化'我自己'
        // 主要用于 1. 父类无法强转为子类的情况
        //         2. 用不同序列化类转为本类的情况
        else if ( MethodReflect.isExtendImplement(i_InitObj ,SerializableDef.class) )
        {
            int             v_FindlIndex = 0;
            SerializableDef v_SerialObj  = (SerializableDef)i_InitObj;
            
            for (int v_ThisIndex = 0; v_ThisIndex < this.gatPropertySize(); v_ThisIndex++)
            {
                for (int v_SerialIndex = v_FindlIndex ; v_SerialIndex < v_SerialObj.gatPropertySize(); v_SerialIndex++)
                {
                    if ( v_SerialObj.gatPropertyName(v_SerialIndex).equals(this.gatPropertyName(v_ThisIndex)) )
                    {
                        Object v_InitValue = v_SerialObj.gatPropertyValue(v_SerialIndex);
                        
                        if ( v_InitValue != null  )
                        {
                            if ( MethodReflect.isExtendImplement(v_InitValue ,SerializableDef.class) )
                            {
                                // 当对象的属性也是一个SerializableDef类时，进行深度克隆
                                SerializableDef v_FieldSerial      = (SerializableDef)v_InitValue;
                                Object          v_FieldSerialClone = v_FieldSerial.newObject();
                                
                                if ( this.gatPropertyValue(v_ThisIndex) == null )
                                {
                                    v_FieldSerial.cloneSerializable((SerializableDef)v_FieldSerialClone);
                                    v_InitValue = v_FieldSerialClone;
                                    
                                    this.setPropertyValue(v_ThisIndex ,v_InitValue);
                                }
                                else
                                {
                                    ((SerializableDef)this.gatPropertyValue(v_ThisIndex)).initNotNull(v_InitValue);
                                }
                            }
                            else
                            {
                                this.setPropertyValue(v_ThisIndex ,v_InitValue);
                            }
                        }
                        
                        // gatPropertyName() 集合中的方法缓存，均是惟一性（无参的getter，配对一个仅一个参数的setter）。所以，只要是方法名称匹配了，就认定为找到了
                        v_FindlIndex++;
                        break;
                    }
                }
            }
        }
        // 用Map集合初始化'我自己'
        else if ( i_InitObj instanceof Map || MethodReflect.isExtendImplement(i_InitObj ,Map.class) )
        {
            @SuppressWarnings("unchecked")
            Map<Object ,Object> v_Datas = (Map<Object ,Object>)i_InitObj;
            
            for (int v_ThisIndex = 0; v_ThisIndex < this.gatPropertySize(); v_ThisIndex++)
            {
                String v_Key    = this.gatPropertyShortName(v_ThisIndex);
                Object v_Object = Help.getValueIgnoreCase(v_Datas ,v_Key); // 先通过常规方式快速获取一次，如果没有获取到，再忽略大小写的方式遍历。
                
                if ( null != v_Object )
                {
                    this.setPropertyValue(v_ThisIndex ,v_Object);
                }
            }
        }
        else
        {
            throw new ClassCastException("Clone new object class is different.");
        }
    }
    
    
    
    @Override
    public String toString()
    {
        StringBuilder v_Buffer = new StringBuilder();
        
        for (int i=0; i<this.gatPropertySize(); i++)
        {
            Object v_Value = this.gatPropertyValue(i);
            
            if ( v_Value == null )
            {
                v_Buffer.append(this.gatPropertyName(i).toString()).append("=").append(";");
            }
            else
            {
                v_Buffer.append(this.gatPropertyName(i).toString()).append("=").append(v_Value).append(";");
            }
        }
        
        return v_Buffer.toString();
    }

    
    
    /*
    ZhengWei(HY) Del 2016-07-30
    不能实现这个方法。首先JDK中的Hashtable、ArrayList中也没有实现此方法。
    它会在元素还有用，但集合对象本身没有用时，释放元素对象
    
    一些与finalize相关的方法，由于一些致命的缺陷，已经被废弃了
    protected void finalize() throws Throwable
    {
        super.finalize();
    }
    */
    
}
