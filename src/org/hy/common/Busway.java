package org.hy.common;





/**
 * 公交车专用通道。
 * 
 * 它有什么特点呢？
 *   其一：它只有一条道路；
 *   其二：只有一条道路就造成前面的公交车不走，后面的公交车也无法走；
 *   其三：道路的长度是有限的。
 * 
 * 基于上面的三个特点，再加上继承于队列类 Queue，使用 "先进先出" 方式。
 * 对每一次 put 方法都检查超出道路长度（wayLength）的数据，就会被从队列中移除。
 * 移除后再存入新的数据。
 * 
 * <O>指队列中元素的对象类型
 *
 * @author   ZhengWei(HY)
 * @version  v1.0  2014-12-06
 */
public class Busway<O> extends Queue<O>
{
    
    private static final long serialVersionUID = -303438880266412812L;
    
    
    
    /** 道路长度 */
    private int wayLength;
    
    
    
    public Busway(int i_WayLength)
    {
        super(Queue.QueueType.$First_IN_First_OUT);
        
        this.setWayLength(i_WayLength);
    }
    
    
    
    public synchronized void put(O i_Object) 
    {
        int v_Size = super.size();
        
        if ( v_Size < this.wayLength )
        {
            super.put(i_Object);
        }
        else
        {
            super.get();
            super.put(i_Object);
        }
    }


    
    public synchronized int getWayLength()
    {
        return wayLength;
    }


    
    public synchronized void setWayLength(int i_WayLength)
    {
        if ( i_WayLength <= 0 )
        {
            throw new VerifyError("Way length is not less than or equal 0.");
        }
        
        this.wayLength = i_WayLength;
        
        int v_Size = super.size();
        if ( v_Size > this.wayLength )
        {
            int v_OutCount = v_Size - this.wayLength;
            for (int i=1; i<=v_OutCount; i++)
            {
                // 抛弃多余的，最先(最老)的数据
                super.get();
            }
        }
    }
    
}
