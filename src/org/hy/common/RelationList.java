package org.hy.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.hy.common.SplitSegment.InfoType;





/**
 * 关系集合
 * 
 * @author ZhengWei(HY)
 * @create 2014-08-12
 */
public class RelationList<O> extends Relation<O> implements List<Relation<O>>
{
    
    private static final long serialVersionUID = 4866820825894020934L;
    
    

    private final static String $Relation_PLeft  = "(";
    
    private final static String $Relation_PRight = ")";
    
    private final static String $Relation_AND    = "&&";
    
    private final static String $Relation_OR     = "||";
    
    /** 关系符解释的正则表达式 */
    private final static String $Relation        = "[ \\S]((" + $Relation_AND + ")|(" + "\\|\\|" + "))[ \\S]";
    
    
    
    /** 关系类型 */
    public enum RelationType
    {
        /** 与关系类型 */
        $AND
        
        /** 或关系类型 */
       ,$OR
    }
    
    
    
    /** 
     * 关系类型。
     * 
     * 表示 relations 集合中每个元素间的关系，都为相同的这样关系类型
     * 
     * 默认为"与"关系 
     */
    private RelationType      relationType;
    
    /** 关系集合 */
    private List<Relation<O>> relations;
    
    
    
    public RelationList()
    {
        this(null);
    }
    
    
    
    public RelationList(RelationType i_RelationType)
    {
        if ( i_RelationType != null )
        {
            this.setRelationType(i_RelationType);
        }
        this.relations = new ArrayList<Relation<O>>();
    }

    
    
    public RelationType getRelationType()
    {
        return relationType;
    }
    

    
    public void setRelationType(RelationType i_RelationType)
    {
        if ( i_RelationType == null )
        {
            throw new NullPointerException("RelationType is null.");
        }
        
        this.relationType = i_RelationType;
    }



    public int size()
    {
        return this.relations.size();
    }



    public boolean isEmpty()
    {
        return this.relations.isEmpty();
    }



    public boolean contains(Object o)
    {
        return this.relations.contains(o);
    }



    public Iterator<Relation<O>> iterator()
    {
        return this.relations.iterator();
    }



    public Object [] toArray()
    {
        return this.relations.toArray();
    }



    public <T> T [] toArray(T [] a)
    {
        return this.relations.toArray(a);
    }



    public boolean add(Relation<O> e)
    {
        return this.relations.add(e);
    }



    public boolean remove(Object o)
    {
        return this.relations.remove(o);
    }



    public boolean containsAll(Collection<?> c)
    {
        return this.relations.containsAll(c);
    }



    public boolean addAll(Collection<? extends Relation<O>> c)
    {
        return this.relations.addAll(c);
    }



    public boolean addAll(int index ,Collection<? extends Relation<O>> c)
    {
        return this.relations.addAll(index ,c);
    }



    public boolean removeAll(Collection<?> c)
    {
        return this.relations.removeAll(c);
    }



    public boolean retainAll(Collection<?> c)
    {
        return this.relations.retainAll(c);
    }



    public void clear()
    {
        this.relations.clear();
    }



    public Relation<O> get(int index)
    {
        return this.relations.get(index);
    }



    public Relation<O> set(int index ,Relation<O> element)
    {
        return this.relations.set(index ,element);
    }



    public void add(int index ,Relation<O> element)
    {
        this.relations.add(index ,element);
    }



    public Relation<O> remove(int index)
    {
        return this.relations.remove(index);
    }



    public int indexOf(Object o)
    {
        return this.relations.indexOf(o);
    }



    public int lastIndexOf(Object o)
    {
        return this.relations.lastIndexOf(o);
    }



    public ListIterator<Relation<O>> listIterator()
    {
        return this.relations.listIterator();
    }



    public ListIterator<Relation<O>> listIterator(int index)
    {
        return this.relations.listIterator(index);
    }



    public List<Relation<O>> subList(int fromIndex ,int toIndex)
    {
        return this.relations.subList(fromIndex ,toIndex);
    }



    @Override
    public String toString()
    {
        String v_RelationTypeName = null;
        
        if ( this.relationType == null )
        {
            v_RelationTypeName = "?";
        }
        else if ( this.relationType == RelationType.$AND )
        {
            v_RelationTypeName = "AND";
        }
        else
        {
            v_RelationTypeName = "OR";
        }
        
        return v_RelationTypeName + this.relations.toString();
    }

    
    
    /**
     * 解释关系
     * 
     * @param i_Return  来源于 StringHelp.Split() 方法的返回值 
     * @return
     */
    public final static RelationList<String> parser(final Return<List<SplitSegment>> i_Return)
    {
        RelationList<String> v_Super     = new RelationList<String>();
        RelationList<String> v_Relations = null;
        List<SplitSegment>   v_RSs       = StringHelp.Split($Relation ,i_Return.paramStr);
        
        if ( !Help.isNull(i_Return.paramObj) )
        {
            v_Relations = parser(i_Return.paramObj ,0 ,new Queue<String>(Queue.QueueType.$First_IN_Last_OUT));
        }
        
        Expression v_Expression = new Expression(Expression.SetActionType.$Move);
        for (SplitSegment v_RS : v_RSs)
        {
            if ( v_RS.getInfoType() == InfoType.$TextInfo )
            {
                if ( Help.isNull(v_RS.getInfo()) )
                {
                    v_Expression.setLR(" ");
                }
                else
                {
                    v_Super.add(new Relation<String>(v_RS.getInfo().trim()));
                    v_Expression.setLR(v_RS.getInfo().trim());
                }
            }
            else
            {
                // 关系设置按首次出现关系符为准
                if ( v_Super.getRelationType() == null )
                {
                    if ( $Relation_AND.equals(v_RS.getInfo().trim()) )
                    {
                        v_Super.setRelationType(RelationType.$AND);
                    }
                    else
                    {
                        v_Super.setRelationType(RelationType.$OR);
                    }
                }
                
                v_Expression.setMiddle(v_RS.getInfo().trim());
            }
        }
        
        
        if ( v_Expression.isHaveLR_OR() )
        {
            if ( !Help.isNull(v_Relations) )
            {
                if ( v_Relations.getRelationType() == null )
                {
                    v_Relations.setRelationType(v_Super.getRelationType());
                    v_Relations.addAll(v_Super);
                    return v_Relations;
                }
                else
                {
                    v_Super.add(v_Relations);
                    
                    if ( !v_Expression.isHaveLeft() && v_Expression.isHaveRight() )
                    {
                        v_Super.add(v_Super.remove(0));
                    }
                }
            }
            
            if ( v_Super.getRelationType() == null )
            {
                v_Super.setRelationType(RelationType.$AND);
            }
            return v_Super;
        }
        else
        {
            if ( v_Relations != null )
            {
                if ( v_Relations.getRelationType() == null )
                {
                    v_Relations.setRelationType(v_Super.getRelationType());
                }
            }
            else
            {
                v_Relations = new RelationList<String>();
                v_Relations.setRelationType(RelationType.$AND);
                v_Relations.add(new Relation<String>(i_Return.paramObj.get(0).getInfo()));
            }
            
            return v_Relations;
        }
    }
    
    
    
    /**
     * 解释关系
     * 
     * @param i_Splits
     * @param i_Level          层次(最小下标从 1 开始，入参最小下标从 0 开始)
     * @param io_MessyStrings  散乱信息、不属性本层次的字符信息
     * @return
     */
    private final static RelationList<String> parser(final List<SplitSegment> i_Splits ,final int i_Level ,final Queue<String> io_MessyStrings)
    {
        if ( Help.isNull(i_Splits) )
        {
            return null;
        }
        
        RelationList<String> v_Relations = new RelationList<String>();
        
        for (int i=1; i<=i_Splits.size(); i++)
        {
            SplitSegment         v_Split   = i_Splits.get(i - 1);
            RelationList<String> v_RSTemp  = null;
            RelationList<String> v_RSAgent = null;  // MINI代理器模式
            
            if ( v_Split.getChildSize() >= 1 )
            {
                RelationList<String> v_ChildRelations = parser(v_Split.getChilds() ,i_Level + 1 ,io_MessyStrings);
                
                if ( !Help.isNull(v_ChildRelations) )
                {
                    v_Relations.add(v_ChildRelations);
                }
            }
            else
            {
                v_RSTemp = new RelationList<String>();
            }
            
            
            List<SplitSegment> v_RSs        = null;
            SplitSegment       v_CloneSplit = v_Split.clone();
            v_CloneSplit.setInfo(v_CloneSplit.getInfo().trim());
            while ( v_RSs == null )
            {
                if ( v_CloneSplit.getInfo().startsWith($Relation_PLeft) && v_CloneSplit.getInfo().endsWith($Relation_PRight) )
                {
                    v_RSs = StringHelp.Split($Relation ,v_CloneSplit.getInfo(1));
                }
                else if ( v_CloneSplit.getInfo().startsWith($Relation_PLeft) )
                {
                    if ( v_Split.getInfo().length() >= v_CloneSplit.getInfo().length() )
                    {
                        io_MessyStrings.put(StringHelp.lpad("" ,v_Split.getBeginIndex() ," ") + v_Split.getInfo());          // 这个地方要用 v_Split.getInfo()
                    }
                    else
                    {
                        io_MessyStrings.put(v_CloneSplit.getInfo());  // 已被while循环处理过的
                    }
                    v_RSs = null;
                    break;
                }
                else if ( (v_CloneSplit.getInfo().indexOf($Relation_PRight) >= 0 && v_CloneSplit.getInfo().indexOf($Relation_PLeft) < 0) )
                {
                    v_CloneSplit.setInfo(StringHelp.lpad("" ,v_Split.getBeginIndex() ," ") + v_Split.getInfo());         // 这个地方要用 v_Split.getInfo()
                    do
                    {
                        v_CloneSplit.setInfo(StringHelp.xor(io_MessyStrings.get() ,v_CloneSplit.getInfo()));
                    } 
                    while ( v_CloneSplit.getInfo().indexOf($Relation_PLeft) < 0 && io_MessyStrings.size() >= 1 );
                }
                else
                {
                    v_RSs = StringHelp.Split($Relation ,v_CloneSplit.getInfo());
                }
                
                if ( v_RSs != null && v_RSs.size() == 1 )
                {
                    int v_RTCount = 0;
                    
                    v_RTCount += v_RSs.get(0).getInfo().startsWith($Relation_OR ) ? 1 : 0;
                    v_RTCount += v_RSs.get(0).getInfo().startsWith($Relation_AND) ? 2 : 0;
                    
                    if ( v_RTCount > 0 )
                    {
                        if ( v_Relations.getRelationType() == null )
                        {
                            if ( v_RTCount == 1 )
                            {
                                v_Relations.setRelationType(RelationType.$OR);
                            }
                            else
                            {
                                v_Relations.setRelationType(RelationType.$AND);
                            }
                        }
                        
                        io_MessyStrings.put(StringHelp.lpad("" ,v_Split.getBeginIndex() ," ") + v_Split.getInfo());      // 这个地方要用 v_Split.getInfo()
                        v_RSs = null;
                        
                        if ( i_Level == 1 && i == i_Splits.size() )
                        {
                            v_CloneSplit.setInfo(StringHelp.lpad("" ,v_Split.getBeginIndex() ," ") + v_Split.getInfo()); // 这个地方要用 v_Split.getInfo()
                            while ( io_MessyStrings.size() >= 1 )
                            {
                                v_CloneSplit.setInfo(StringHelp.xor(io_MessyStrings.get() ,v_CloneSplit.getInfo()));
                            }
                        }
                        else
                        {
                            break;
                        }
                    }
                }
            }
            
            if ( v_RSs == null ) { continue; }
            
            
            Expression v_Expression = new Expression(Expression.SetActionType.$Move);
            v_RSAgent = v_RSTemp == null ? v_Relations : v_RSTemp;
            for (SplitSegment v_RS : v_RSs)
            {
                if ( v_RS.getInfoType() == InfoType.$TextInfo )
                {
                    if ( Help.isNull(v_RS.getInfo()) )
                    {
                        v_Expression.setLR("");
                    }
                    else
                    {
                        v_RSAgent.add(new Relation<String>(v_RS.getInfo().trim()));
                        v_Expression.setLR(v_RS.getInfo().trim());
                    }
                }
                else
                {
                    // 关系设置按首次出现关系符为准
                    if ( v_RSAgent.getRelationType() == null )
                    {
                        RelationType v_RelationType = null;
                        
                        if ( $Relation_AND.equals(v_RS.getInfo().trim()) )
                        {
                            v_RelationType = RelationType.$AND;
                        }
                        else
                        {
                            v_RelationType = RelationType.$OR;
                        }
                        
                        v_RSAgent.setRelationType(v_RelationType);
                        v_Expression.setMiddle(v_RS.getInfo().trim());
                    }
                }
            }
            
            if ( !Help.isNull(v_RSTemp) )
            {
                if ( v_RSTemp.size() == 1 )
                {
                    v_Relations.add(v_RSTemp.get(0));
                }
                else
                {
                    v_Relations.add(v_RSTemp);
                }
            }
            
            // 调整多个表达式的前后位置
            if ( v_Expression.isHaveLeft() && !v_Expression.isHaveRight() && v_Expression.isHaveMiddle())
            {
                if ( v_Relations.size() >= 2 )
                {
                    v_Relations.add(v_Relations.remove(v_Relations.size() - 2));
                }
            }
            
            if ( v_Expression.isHaveLR_OnlyOne() )
            {
                RelationList<String> v_SuperTemp = new RelationList<String>();
                v_SuperTemp.add(v_Relations);
                v_Relations = v_SuperTemp;
            }
            
            if ( !v_Expression.isHaveLR() ) 
            {
                RelationList<String> v_TempR = (RelationList<String>)v_Relations.get(0);
                if ( v_TempR.getRelationType() == null )
                {
                    if ( v_Relations.getRelationType() != null )
                    {
                        v_TempR.setRelationType(v_Relations.getRelationType());
                    }
                }
                
                v_Relations = v_TempR;
            }
        }
        
        if ( v_Relations.size() == 1 )
        {
            if ( v_Relations.get(0) instanceof RelationList )
            {
                return (RelationList<String>)v_Relations.get(0);
            }
            else
            {
                return null;
            }
        }
        else
        {
            return v_Relations;
        }
    }
    
}
