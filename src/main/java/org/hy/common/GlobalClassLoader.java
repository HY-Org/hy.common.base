package org.hy.common;

import java.util.function.Supplier;





/**
 * 全局类加载器
 *
 * @author      ZhengWei(HY)
 * @createDate  2026-02-27
 * @version     v1.0
 */
public class GlobalClassLoader
{
    
    /** 类加载器 */
    private static ClassLoader $ClassLoader;
    
    
    
    /**
     * 初始化类加载器
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-02-27
     * @version     v1.0
     *
     */
    public static void init() 
    {
        init(Thread.currentThread().getContextClassLoader());
    }
    
    
    
    /**
     * 初始化类加载器
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-02-27
     * @version     v1.0
     *
     * @param i_ClassLoader  指定类加载器
     */
    public static void init(ClassLoader i_ClassLoader)
    {
        $ClassLoader = i_ClassLoader;
    }
    
    
    
    /**
     * 获取类加载器
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-02-27
     * @version     v1.0
     *
     * @return
     */
    public static ClassLoader getClassLoader()
    {
        if ( $ClassLoader == null )
        {
            return Thread.currentThread().getContextClassLoader();
        }
        else
        {
            return $ClassLoader;
        }
    }
    
    
    
    /**
     * 使用全局类加载器
     * 
     * @author      ZhengWei(HY)
     * @createDate  2026-02-27
     * @version     v1.0
     *
     * @param <T>
     * @param i_Supplier
     * @return
     */
    public static <T> T executeWithAppClassLoader(Supplier<T> i_Supplier)
    {
        ClassLoader v_OriginalLoader = Thread.currentThread().getContextClassLoader();
        try
        {
            // 强制切换为应用类加载器
            Thread.currentThread().setContextClassLoader($ClassLoader);
            return i_Supplier.get();
        }
        finally
        {
            // 恢复原类加载器，避免副作用
            Thread.currentThread().setContextClassLoader(v_OriginalLoader);
        }
    }
    
}
