package org.hy.common;





/**
 * 通过实现此接口来自动的获取配置在XML配置文件中的XJava池中对象的ID标识。
 * 
 * 此类没有定义在XJava包中的原因是：为了更广泛的应用，如hy.common.db包是能应用的同时，不用反正引用XJava。
 *
 * @author      ZhengWei(HY)
 * @createDate  2018-03-09
 * @version     v1.0
 */
public interface XJavaID
{
    
    /**
     * 设置XJava池中对象的ID标识。此方法不用用户调用设置值，是自动的。
     * 
     * @param i_XJavaID
     */
    public void setXJavaID(String i_XJavaID);
    
    
    
    /**
     * 获取XJava池中对象的ID标识。
     * 
     * @return
     */
    public String getXJavaID();
    
}
