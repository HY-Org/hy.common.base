package org.hy.common;





/**
 * 关系信息
 * 
 * @author ZhengWei(HY)
 * @create 2014-08-12
 */
public class Relation<O> implements Cloneable
{
    
    private O info;
    
    
    
    public Relation()
    {
        this(null);
    }
    
    
    
    public Relation(O i_Info)
    {
        this.info = i_Info;
    }
    

    
    public O getInfo()
    {
        return info;
    }

    
    
    public void setInfo(O info)
    {
        this.info = info;
    }



    @Override
    public String toString()
    {
        return this.info == null ? "" : this.info.toString();
    }



    @Override
    public int hashCode()
    {
        return this.info == null ? Integer.MIN_VALUE : this.info.hashCode();
    }



    @Override
    public boolean equals(Object obj)
    {
        return this.info == null ? false : this.info.equals(obj);
    }



    @Override
    protected Relation<O> clone()
    {
        return new Relation<O>(this.info);
    }
    
}
