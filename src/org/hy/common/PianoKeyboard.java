package org.hy.common;

import java.io.Serializable;





/**
 * 钢琴键盘模式的队列缓存池中的每个钢琴键盘。
 * 
 * 当数据对象被poll()方法获取（消费）后，会自动开启一个线程，异步的方式创建数据对象。
 * 好比，钢琴键盘被按下后，自动回弹起来。
 *
 * @author      ZhengWei(HY)
 * @createDate  2018-07-04
 * @version     v1.0
 * @param <O>   预先创建的对象
 */
public class PianoKeyboard<O> implements Runnable ,Serializable
{

    private static final long serialVersionUID = -1517792264065784315L;
    
    
    
    /** 本实例所属的钢琴键盘模式的队列缓存池 */
    private PianoKeyboardPool<O> pool;
    
    /** 数据对象是否有效 */
    private boolean              isValid;
    
    /** 数据对象 */
    private O                    value;
    
    
    
    /**
     * 钢琴键盘的构造器
     *
     * @author      ZhengWei(HY)
     * @createDate  2018-07-03
     * @version     v1.0
     *
     * @param i_Pool    本实例所属的钢琴键盘模式的队列缓存池
     * @param i_IsInit  在构造器中是否已同步方式创建数据对象
     */
    public PianoKeyboard(PianoKeyboardPool<O> i_Pool ,boolean i_IsInit)
    {
        if ( i_Pool == null )
        {
            throw new java.lang.InstantiationError("PianoKeyboardPool is null.");
        }
        
        this.pool    = i_Pool;
        this.value   = null;
        this.isValid = false;
        
        if ( i_IsInit )
        {
            this.creating();
        }
        else
        {
            this.startCreating();
        }
    }
    
    
    
    /**
     * 获取数据对象。同时，在获取成功后，创建数据对象。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2018-07-04
     * @version     v1.0
     *
     * @return
     */
    public synchronized O poll()
    {
        if ( this.isValid )
        {
            this.isValid = false;
            
            try
            {
                return this.value;
            }
            finally
            {
                this.startCreating();
            }
        }
        else
        {
            return null;
        }
    }
    
    
    
    /**
     * 开始异步方式的创建数据对象。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2018-07-04
     * @version     v1.0
     *
     */
    public void startCreating()
    {
        Thread v_Thread = new Thread(this);
        v_Thread.start();
    }
    
    
    
    /**
     * 创建数据对象。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2018-07-02
     * @version     v1.0
     *
     */
    public void creating()
    {
        this.value   = this.pool.newObject();
        this.isValid = true;
    }


    
    /**
     * 获取：本实例所属的钢琴键盘模式的队列缓存池
     */
    public PianoKeyboardPool<O> getPool()
    {
        return pool;
    }
    

    
    /**
     * 获取：数据对象是否有效
     */
    public boolean isValid()
    {
        return isValid;
    }



    @Override
    public void run()
    {
        this.creating();
    }
    
}
