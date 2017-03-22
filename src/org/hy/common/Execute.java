package org.hy.common;

import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;





/**
 * 线程方式执行相关的动作
 * 
 *   注意：被执行的方法必须是 public 的。
 *   
 *   可由两种方式获取执行结果：
 *   1. 通过本类的 this.getResult() 方法。
 *   2. 通过实现事件监听接口 org.hy.common.ExecuteListener。
 * 
 * @author  ZhengWei(HY)
 * @version v1.0  2013-11-15
 * @version v2.0  2014-08-15  1.支持超时自动终止线程功能(核心功能)
 *                            2.记录线程运行第三方方法的开始时间、结束时间，及计算运行时长
 *                            3.支持获取异常信息的方法，但不直接抛出异常（已改为：抛出异常）
 *                            4.当用户自己设置异常信息时，系统不覆盖用户的异常
 * @version v3.0  2016-03-02  1.添加：延后多少时间后再执行第三方方法的功能。this.startDelayed();
 *                            2.添加：停止执行方法 this.stopExecute();
 * @version v3.1  2016-12-23  1.修正：将版本v2.0中的第3条改为：直接抛出异常，方便及时发现问题。
 * @version v4.0  2017-01-06  1.添加：支持对重载方法的判定。对每参数类型都进行比较确认惟一的执行方法。
 * @version v4.1  2017-01-14  1.添加：支持执行实现的父类的方法。
 *                                   将 this.intance.getDeclaredMethods() 改为：this.intance.getClass().getMethods()。
 * @version v5.0  2017-01-19  1.添加：保存动作方法的执行结果。
 *                              添加：执行结果的事件监听功能。
 *          v5.1  2017-03-22  1.添加：getReturnAwait() 方法在启动线程后，一直等待执行方法的返回结果。
 */
public class Execute extends Thread
{
    /** 动作的实例 */
    private Object             instance;
    
    /** 动作的方法名称(方法的全名称) */
    private String             methodName;
    
    /** 动作的方法参数 */
    private Object []          params;
    
    /** 动作方法的执行结果。（未执行、执行异常、方法无返回值三种情况均返回null） */
    private Object             result;
    
    /** 执行结果的监听事件 */
    private ExecuteResultFire  resultFire;
    
    /** 执行第三方方法的开始时间 */
    private Date               runBeginTime;
    
    /** 执行第三方方法的结束时间 */
    private Date               runEndTime;
    
    /** 延后多长时间后再执行第三方(单位：毫秒)。默认为0，即立即执行 */
    private long               delayedTime;
    
    /** 超时终止线程的时长(单位：毫秒) */
    private long               timeout;
    
    /** 监控、监视的间隔时长(单位：毫秒) */
    private long               watchInterval;
    
    /** 标记是否运行完成  */
    private boolean            isRunFinish;
    
    /** 
     * 等待方法 waitting() 是否运行完成 
     * 
     *  向外界提供一种方便"持续等待"线程运行完成的方法
     */
    private boolean            isWaittingFinish;
    
    /** 是否报错(内部使用)。对于超时终止线程动作而因起的异常不应报错 */
    private boolean            isReportError;
    
    /** 调用第三方方法时的异常信息，及超时终止自创建的异常信息 */
    private Throwable          exception;
    
    
    
    /**
     * 线程方式执行相关的动作(无入参)
     * 
     * @param i_Intance     动作的实例
     * @param i_MethodName  动作的方法名称(方法的全名称)。如果在实例内部运行，此方法可以是私有类型的private。
     * @param i_Params      动作的方法参数
     */
    public Execute(Object i_Intance ,String i_MethodName)
    {
        this(i_Intance ,i_MethodName ,new Object[]{});
    }
    
    
    
    /**
     * 线程方式执行相关的动作(一个入参)
     * 
     * @param i_Intance     动作的实例
     * @param i_MethodName  动作的方法名称(方法的全名称)。如果在实例内部运行，此方法可以是私有类型的private。
     * @param i_Params      动作的方法参数
     */
    public Execute(Object i_Intance ,String i_MethodName ,Object i_Param)
    {
        this(i_Intance ,i_MethodName ,new Object[]{i_Param});
    }
    
    
    
    /**
     * 线程方式执行相关的动作(多个入参)
     * 
     * @param i_Instance    动作的实例
     * @param i_MethodName  动作的方法名称(方法的全名称)。如果在实例内部运行，此方法可以是私有类型的private。
     * @param i_Params      动作的方法参数
     */
    public Execute(Object i_Instance ,String i_MethodName ,Object [] i_Params)
    {
        this.instance      = i_Instance;
        this.methodName    = i_MethodName.trim();
        this.params        = i_Params;
        this.result        = null;
        this.resultFire    = null;
        this.watchInterval = 300;
        this.isReportError = true;
        this.delayedTime   = 0;
    }
    
    
    
    /**
     * 主要执行的方法或动作
     */
    public void run()
    {
        // 延后多长时间后再执行第三方(单位：毫秒)。默认为0，即立即执行
        if ( this.delayedTime > 0 )
        {
            try
            {
                Thread.sleep(this.delayedTime);
            }
            catch (Exception exce)
            {
                exce.printStackTrace();
            }
        }
        
        this.result    = null;
        this.exception = null;
        Method v_Method = MethodReflect.getMethod(this.instance ,this.methodName ,this.params);
        
        if ( null != v_Method )
        {
            try
            {
                if ( v_Method.getReturnType() == java.lang.Void.TYPE )
                {
                    v_Method.invoke(this.instance ,this.params);
                }
                else
                {
                    this.result = v_Method.invoke(this.instance ,this.params);
                }
            }
            catch (Throwable exce)
            {
                if ( isReportError )
                {
                    exce.printStackTrace();
                    this.exception = exce;
                }
            }
        }
        
        this.runEndTime  = new Date();
        this.isRunFinish = true;
        
        // 通知所有事件监听者。在超时被内部关闭时，也同样会执行下面的代码
        if ( this.resultFire != null )
        {
            this.resultFire.fire(new ExecuteEvent(this.instance
                                                 ,this.runEndTime.getTime()
                                                - this.runBeginTime.getTime()
                                                - this.delayedTime
                                                 ,this.exception != null 
                                                 ,this.result));
        }
    }
    
    
    
    /**
     * 启动线程，开始执行
     *
     * @see java.lang.Thread#start()
     */
    @Override
    public synchronized void start()
    {
        this.startDelayed(0);
    }
    
    
    
    /**
     * 启动线程，延后多长时间后再执行第三方(单位：毫秒)。默认为0，即立即执行
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-03-02
     * @version     v1.0
     *
     * @param i_DelayedTime  延后时长(单位：毫秒)
     */
    public synchronized void startDelayed(long i_DelayedTime)
    {
        this.runBeginTime = new Date();
        this.runEndTime   = null;
        this.delayedTime  = i_DelayedTime;
        
        super.start();
    }



    /**
     * 启动线程，同时线程运行时间超时后，终止线程
     * 
     * @param i_Timeout  超时终止线程的时长(单位：毫秒)
     */
    public synchronized void start(long i_Timeout)
    {
        this.startDelayed(0 ,i_Timeout);
    }
    
    
    
    /**
     * 启动线程，延后多长时间后再执行第三方(单位：毫秒)。默认为0，即立即执行
     * 启动线程，同时线程运行时间超时后，终止线程。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-03-02
     * @version     v1.0
     *
     * @param i_DelayedTime  延后时长(单位：毫秒)
     * @param i_Timeout      超时终止线程的时长(单位：毫秒)
     */
    public synchronized void startDelayed(long i_DelayedTime ,long i_Timeout)
    {
        if ( i_Timeout <= 1000 )
        {
            throw new VerifyError("Timeout is not <= 1000 millisecond.");
        }
        
        Execute v_Execute = new Execute(this ,"runIsTimeout");
        
        this.delayedTime = i_DelayedTime;
        this.timeout     = i_Timeout;
        v_Execute.start();
        
        this.start();
    }
    
    
    
    /**
     * 停步执行
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-03-02
     * @version     v1.0
     *
     */
    public synchronized void stopExecute()
    {
        if ( !this.isRunFinish )
        {
            try
            {
                this.isReportError = false;
                this.interrupt();
            }
            catch (Exception exce)
            {
                exce.printStackTrace();
            }
            
            // 当用户自己设置异常信息时，不覆盖用户的异常
            if ( this.exception == null )
            {
                if ( Help.isNull(this.params) )
                {
                    this.exception = new RuntimeException("Timeout Error: Call " + this.instance.getClass().getName() + "." + this.methodName + "().");
                }
                else
                {
                    this.exception = new RuntimeException("Timeout Error: Call " + this.instance.getClass().getName() + "." + this.methodName + "(...).");
                }
            }
            
            this.runEndTime    = new Date();
            this.isRunFinish   = true;
            this.isReportError = true;
        }
        
        this.isWaittingFinish = true;
    }
    
    
    
    /**
     * 监控、监视是否运行超时
     */
    public void runIsTimeout()
    {
        this.isRunFinish      = false;
        this.isWaittingFinish = false;
        this.exception        = null;
        
        while ( this.timeout > 0 && !this.isRunFinish )
        {
            try
            {
                Thread.sleep(this.watchInterval);
            }
            catch (Exception exce)
            {
                exce.printStackTrace();
            }
            
            this.timeout = this.timeout - this.watchInterval;
        }
        
        this.stopExecute();
    }


    
    public long getWatchInterval()
    {
        return watchInterval;
    }


    
    public void setWatchInterval(long i_WatchInterval)
    {
        if ( i_WatchInterval <= 10 )
        {
            throw new VerifyError("WatchInterval is not <= 10 millisecond.");
        }
        
        this.watchInterval = i_WatchInterval;
    }
    
    
    
    public Date getRunBeginTime()
    {
        return runBeginTime;
    }


    
    public Date getRunEndTime()
    {
        return runEndTime;
    }
    
    
    
    /**
     * 获取运行时长(单位：毫秒)
     * 
     * @return
     */
    public long getRunTimeLen()
    {
        long v_Ret = 0;
        
        if ( this.runBeginTime == null )
        {
            return v_Ret;
        }
        
        if ( this.runEndTime == null )
        {
            v_Ret = Date.getNowTime().getTime() - this.runBeginTime.getTime();
        }
        else
        {
            v_Ret = this.runEndTime.getTime() - this.runBeginTime.getTime();
        }
        
        return v_Ret > 0 ? v_Ret : 0;
    }



    /**
     * 当用户自己设置异常信息时，系统不覆盖用户的异常
     * 
     * @return
     */
    public Throwable getException()
    {
        return exception;
    }


    
    /**
     * 当用户自己设置异常信息时，系统不覆盖用户的异常
     * 
     * @param exception
     */
    public void setException(Throwable exception)
    {
        this.exception = exception;
    }
    
    
    
    public boolean isError()
    {
        return this.exception == null ? false : true;
    }



    public boolean isRunFinish()
    {
        return isRunFinish;
    }
    
    
    
    public boolean isTimeout()
    {
        return timeout <= 0 ? true : false;
    }
    
    
    
    /**
     * 获取：延后多长时间后再执行第三方(单位：毫秒)。默认为0，即立即执行
     */
    public long getDelayedTime()
    {
        return delayedTime;
    }



    /**
     * 向外界提供一种方便"持续等待"线程运行完成的方法
     */
    public void waitting()
    {
        while ( !this.isWaittingFinish )
        {
            try
            {
                Thread.sleep(this.watchInterval);
            }
            catch (Exception exce)
            {
                exce.printStackTrace();
            }
        }
    }
    
    
    
    /**
     * 向外界提供一种方便"持续等待"线程运行完成的方法
     * 
     * 并向输出对象输出如 ... 的形式
     */
    public void waitting(PrintStream i_Out)
    {
        while ( !this.isWaittingFinish )
        {
            try
            {
                Thread.sleep(this.watchInterval);
                i_Out.print(".");
            }
            catch (Exception exce)
            {
                exce.printStackTrace();
            }
        }
    }


    
    /**
     * 获取：动作方法的执行结果。（未执行、执行异常、方法无返回值三种情况均返回null）
     */
    public Object getResult()
    {
        return result;
    }
    
    
    
    /**
     *  获取：动作方法的执行结果，并一直等待执行结果。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-03-22
     * @version     v1.0
     *
     * @return
     */
    public Object getResultAwait()
    {
        while ( this.result == null )
        {
            Thread.yield();
        }
        
        return this.result;
    }
    
    
    
    /**
     * 执行结果：添加事件监听者
     * 
     * 配合XJava的XML文件配置
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-19
     * @version     v1.0
     *
     * @param i_Listener
     */
    public synchronized void setListener(ExecuteListener i_Listener)
    {
        this.addListener(i_Listener);
    }
    
    
    
    /**
     * 执行结果：添加事件监听者
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-19
     * @version     v1.0
     *
     * @param i_Listener
     */
    public synchronized void addListener(ExecuteListener i_Listener)
    {
        if ( this.resultFire == null )
        {
            this.resultFire = new ExecuteResultFire(new ArrayList<ExecuteListener>());
        }
        this.resultFire.addListener(i_Listener);
    }
    
    
    
    /**
     * 执行结果：移除事件监听者
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-19
     * @version     v1.0
     *
     * @param i_Listener
     * @return
     */
    public ExecuteListener removeListener(ExecuteListener i_Listener)
    {
        return this.resultFire.removeListener(i_Listener);
    }
    
    
    
    /**
     * 执行结果：清空所有事件监听者
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-01-19
     * @version     v1.0
     *
     */
    public void clearListeners()
    {
        this.resultFire.clearListeners();
    }
    
    
    
    
    
    /**
     * 执行结果的监听事件 
     *
     * @author      ZhengWei(HY)
     * @createDate  2017-01-19
     * @version     v1.0
     */
    class ExecuteResultFire extends EventFire<ExecuteListener ,ExecuteEvent>
    {
        
        public ExecuteResultFire(List<ExecuteListener> i_Listeners)
        {
            super(i_Listeners);
        }
        
        

        /**
         * 触发事件。通知所有的事件监听者。
         * 
         * @author      ZhengWei(HY)
         * @createDate  2017-01-19
         * @version     v1.0
         *
         * @param i_Event
         */
        public void fire(ExecuteEvent i_Event)
        {
            if ( Help.isNull(this.listeners) )
            {
                return;
            }
            
            for (ExecuteListener v_Listener : this.listeners)
            {
                v_Listener.result(i_Event);
            }
        }
        
    }
    
}
