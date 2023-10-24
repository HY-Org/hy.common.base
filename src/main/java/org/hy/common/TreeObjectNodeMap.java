package org.hy.common;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;





/**
 * 树节点的Map实现类，可通用的免去专门再定义Bean。
 * 
 * 要求Map.key中必须有 $Key_TreeNodeID、$Key_TreeSuperID、$Key_TreeNodeLevel三个定义一样的Key值
 *
 * @author      ZhengWei(HY)
 * @createDate  2023-10-23
 * @version     v1.0
 */
public class TreeObjectNodeMap extends Hashtable<String ,Object> implements TreeObjectNode<TreeObjectNodeMap> ,Comparable<TreeObjectNodeMap>
{

    private static final long serialVersionUID = -4379977304378062328L;
    
    
    
    /** Map集合的Key名称：树的节点ID */
    public static final String $Key_TreeNodeID    = "TreeNodeID";
    
    /** Map集合的Key名称：树的父节点ID */
    public static final String $Key_TreeSuperID   = "TreeSuperID";
    
    /** Map集合的Key名称：树的树层次等级 */
    public static final String $Key_TreeNodeLevel = "TreeNodeLevel";
    

    
    /** 子级对象 */
    private List<TreeObjectNodeMap> childItems;
    
    
    
    /**
     * 获取当前节点ID
     * 
     * @author      ZhengWei(HY)
     * @createDate  2023-10-23
     * @version     v1.0
     *
     * @return
     */
    public TreeObjectNodeMap()
    {
        this.childItems = new ArrayList<TreeObjectNodeMap>();
    }
    

    
    /**
     * 获取当前节点ID
     * 
     * @author      ZhengWei(HY)
     * @createDate  2023-10-23
     * @version     v1.0
     *
     * @return
     */
    @Override
    public String gatTreeObjectNodeID()
    {
        Object v_Value = this.get($Key_TreeNodeID);
        if ( v_Value == null )
        {
            return "";
        }
        else
        {
            return v_Value.toString();
        }
    }

    
    
    /**
     * 获取父节点ID
     * 
     * 此接口可以没有，但为了能将普通的List结构转为一颗树结构，而冗余定义的。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2023-10-23
     * @version     v1.0
     *
     * @return
     */
    @Override
    public String gatTreeObjectSuperID()
    {
        Object v_Value = this.get($Key_TreeSuperID);
        if ( v_Value == null )
        {
            return "";
        }
        else
        {
            return v_Value.toString();
        }
    }

    
    
    /**
     * 获取当前节点的直属子节点
     * 
     * @author      ZhengWei(HY)
     * @createDate  2023-10-23
     * @version     v1.0
     *
     * @return
     */
    @Override
    public List<TreeObjectNodeMap> gatTreeObjectChilds()
    {
        return this.childItems;
    }

    
    
    /**
     * 设置节点的树层次等级，数值越大层次越深，顶层数值最小
     * 
     * @author      ZhengWei(HY)
     * @createDate  2023-10-23
     * @version     v1.0
     *
     * @param i_Level
     */
    @Override
    public void satLevel(int i_Level)
    {
        this.put($Key_TreeNodeLevel ,i_Level);
    }
    

    
    /**
     * 获取节点的树层次等级，数值越大层次越深，顶层数值最小
     * 
     * @author      ZhengWei(HY)
     * @createDate  2023-10-23
     * @version     v1.0
     *
     * @return
     */
    @Override
    public int gatLevel()
    {
        Object v_Value = this.get($Key_TreeNodeLevel);
        if ( v_Value == null )
        {
            return Integer.MAX_VALUE;
        }
        else
        {
            return (int) v_Value;
        }
    }

    
    
    /**
     * 添加子节点
     * 
     * 注意：在添加时异常时，请抛出异常，无异常即表示成功。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2023-10-23
     * @version     v1.0
     *
     * @param i_ChildNode    子节点数据
     */
    @Override
    public void addTreeObjectChild(TreeObjectNodeMap i_ChildNode)
    {
        this.childItems.add(i_ChildNode);
    }

    
    
    /**
     * 删除子节点
     * 
     * 注意：在删除时异常时，请抛出异常，无异常即表示成功。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2023-10-23
     * @version     v1.0
     *
     * @param i_ChildNode    子节点数据
     */
    @Override
    public void delTreeObjectChild(TreeObjectNodeMap i_ChildNode)
    {
        this.childItems.remove(i_ChildNode);
    }
    
    
    
    /**
     * 哈希比较器
     *
     * @author      ZhengWei(HY)
     * @createDate  2023-10-23
     * @version     v1.0
     *
     * @return
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        if ( this.gatTreeObjectNodeID() == null )
        {
            return super.hashCode();
        }
        else
        {
            return this.gatTreeObjectNodeID().hashCode();
        }
    }



    /**
     * 相等比较器
     *
     * @author      ZhengWei(HY)
     * @createDate  2023-10-23
     * @version     v1.0
     *
     * @param i_Other
     * @return
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object i_Other)
    {
        if ( i_Other == null )
        {
            return false;
        }
        else if ( this == i_Other )
        {
            return true;
        }
        else if ( i_Other instanceof TreeObjectNodeMap )
        {
            TreeObjectNodeMap v_Other = (TreeObjectNodeMap)i_Other;
            
            if ( v_Other.gatTreeObjectNodeID() == null )
            {
                return false;
            }
            else if ( this.gatTreeObjectNodeID() == null )
            {
                return false;
            }
            else
            {
                return this.gatTreeObjectNodeID().equals(v_Other.gatTreeObjectNodeID());
            }
        }
        
        return super.equals(i_Other);
    }



    /**
     * 比较器
     *
     * @author      ZhengWei(HY)
     * @createDate  2023-10-23
     * @version     v1.0
     *
     * @param i_Other
     * @return
     *
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(TreeObjectNodeMap i_Other)
    {
        if ( i_Other == null )
        {
            return -1;
        }
        else if ( this == i_Other )
        {
            return 0;
        }
        else
        {
            if ( i_Other.gatTreeObjectNodeID() == null )
            {
                return -1;
            }
            else if ( this.gatTreeObjectNodeID() == null )
            {
                return 1;
            }
            else
            {
                return this.gatTreeObjectNodeID().compareTo(i_Other.gatTreeObjectNodeID());
            }
        }
    }
    
}
