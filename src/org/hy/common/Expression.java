package org.hy.common;





/**
 * 表达式。格式如：X == Y
 * 
 * 可用于控件表达式的前后顺序等后相细节信息
 * 
 * @author ZhengWei(HY)
 * @create 2014-08-18
 */
public class Expression implements java.io.Serializable
{
    
    private static final long serialVersionUID = 620734538702797589L;

    

    /** 通过 Set()、SetLR() 设置值时的动作类型 */
    public enum SetActionType
    {
        /** 锁定后，不在变化。(默认类型) */
        $Lock
        
        /** 三个属性都有值后，运行Clear()后再调用Set()、SetLR()方法 */
       ,$Clear_Set
       
       /** 后值覆盖前值的模式，持续向前推进 */
       ,$Move
    }
    
    
    
    /** 左侧表达式 */
    private String         left;
    
    /** 中间表达式 */
    private String         middle;
    
    /** 右侧表达式 */
    private String         right;
    
    /** 设置标记 */
    private int []         setFlags;
    
    /** 通过 Set()、SetLR() 设置值时的动作类型 */
    private SetActionType  setActionType;

    
    
    public Expression()
    {
        this(SetActionType.$Lock);
    }
    
    
    public Expression(SetActionType i_SetActionType)
    {
        this.setFlags      = new int[]{0 ,0 ,0};
        this.setActionType = i_SetActionType;
    }
    
    
    public void set(String i_Value)
    {
        if ( this.setFlags[0] == 0 )
        {
            this.setLeft(i_Value);
        }
        else if ( this.setFlags[1] == 0 )
        {
            this.setMiddle(i_Value);
        }
        else if ( this.setFlags[2] == 0 )
        {
            this.setRight(i_Value);
        }
        else
        {
            if ( this.setActionType == SetActionType.$Clear_Set )
            {
                this.clear();
                this.set(i_Value);
            }
            else if ( this.setActionType == SetActionType.$Move )
            {
                this.setLeft(this.getRight());
                this.middle      = null;
                this.setFlags[1] = 0;
                this.setRight(i_Value);
            }
        }
    }
    
    
    public void setLR(String i_Value)
    {
        if ( this.setFlags[0] == 0 )
        {
            this.setLeft(i_Value);
        }
        else if ( this.setFlags[2] == 0 )
        {
            this.setRight(i_Value);
        }
        else
        {
            if ( this.setActionType == SetActionType.$Clear_Set )
            {
                this.clear();
                this.setLR(i_Value);
            }
            else if ( this.setActionType == SetActionType.$Move )
            {
                this.setLeft(this.getRight());
                this.middle      = null;
                this.setFlags[1] = 0;
                this.setRight(i_Value);
            }
        }
    }
    
    
    public void clear()
    {
        this.left        = null;
        this.middle      = null;
        this.right       = null;
        
        this.setFlags[0] = 0;
        this.setFlags[1] = 0;
        this.setFlags[2] = 0;
    }
    
    
    public boolean isHaveAll()
    {
        return this.isHaveLR() && this.isHaveMiddle();
    }
    
    
    public boolean isHaveLR()
    {
        return this.isHaveLeft() && this.isHaveRight();
    }
    
    
    public boolean isHaveLR_OR()
    {
        return this.isHaveLeft() || this.isHaveRight();
    }
    
    
    public boolean isHaveLR_OnlyOne()
    {
        return (this.isHaveLeft() && !this.isHaveRight()) || (!this.isHaveLeft() && this.isHaveRight());
    }
    
    
    public boolean isHaveLeft()
    {
        return !Help.isNull(this.left);
    }
    
    
    public boolean isHaveMiddle()
    {
        return !Help.isNull(this.middle);
    }
    
    
    public boolean isHaveRight()
    {
        return !Help.isNull(this.right);
    }
    
    
    public String getLeft()
    {
        return left;
    }

    
    public void setLeft(String i_Left)
    {
        this.left        = i_Left.trim();
        this.setFlags[0] = 1;
    }

    
    public String getMiddle()
    {
        return middle;
    }

    
    public void setMiddle(String i_Middle)
    {
        this.middle      = i_Middle.trim();
        this.setFlags[1] = 1;
    }

    
    public String getRight()
    {
        return right;
    }

    
    public void setRight(String i_Right)
    {
        this.right       = i_Right.trim();
        this.setFlags[2] = 1;
    }

    
    public SetActionType getSetActionType()
    {
        return setActionType;
    }

    
    public void setSetActionType(SetActionType setActionType)
    {
        this.setActionType = setActionType;
    }


    @Override
    public String toString()
    {
        StringBuilder v_Buffer = new StringBuilder();
        
        v_Buffer.append(Help.NVL(this.left   ,"?"));
        v_Buffer.append(" ");
        v_Buffer.append(Help.NVL(this.middle ,"?"));
        v_Buffer.append(" ");
        v_Buffer.append(Help.NVL(this.right  ,"?"));
        
        return v_Buffer.toString();
    }
    
}
