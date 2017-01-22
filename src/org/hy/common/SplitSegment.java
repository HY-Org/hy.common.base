package org.hy.common;

import java.util.ArrayList;
import java.util.List;





/**
 * 分段信息。
 * 
 * 通过指定正则表达式对信息分割，此为类表示分割出的信息
 * 
 * @author      ZhengWei(HY)
 * @version     v1.0  
 * @createDate  2014-07-30
 */
public class SplitSegment implements Cloneable
{
    
    public enum InfoType
    {
        /** 普通文本信息 */
        $TextInfo
        
        /** 查找到的文本(匹配的文本) */
       ,$FindInfo
    }
    
    
    
    /** 在完整信息中的的开始位置。下标从零开始 */
    protected int                 beginIndex;
    
    /** 在完整信息中的结束位置。下标从零开始 */
    protected int                 endIndex;
    
    /** 完整信息中的部分信息 */
    protected String              info;
    
    /** 分段信息的类型 */
    protected InfoType            infoType;
    
    /** 
     * 分段信息（可选的）
     * 
     * 适用于嵌套分段的复杂情况
     */
    protected List<SplitSegment>  childs;
    
    
    
    public SplitSegment()
    {
        
    }
    
    
    
    public SplitSegment(String i_Info ,int i_BeginIndex ,int i_EndIndex)
    {
        this.beginIndex = i_BeginIndex;
        this.endIndex   = i_EndIndex;
        this.infoType   = InfoType.$TextInfo;
        this.info       = i_Info;
    }
    
    
    
    public SplitSegment(String i_Info ,int i_BeginIndex ,int i_EndIndex ,InfoType i_InfoType)
    {
        this.beginIndex = i_BeginIndex;
        this.endIndex   = i_EndIndex;
        this.infoType   = i_InfoType;
        this.info       = i_Info;
    }
    
    
    
    public SplitSegment(SplitSegment i_SplitSegment)
    {
        this.beginIndex = i_SplitSegment.getBeginIndex();
        this.endIndex   = i_SplitSegment.getEndIndex();
        this.infoType   = i_SplitSegment.getInfoType();
        this.info       = i_SplitSegment.getInfo();
    }
    
    
    
    public String getInfo()
    {
        return info;
    }
    
    
    
    /**
     * 等量截取前后两端，只有中间部分的字符信息
     * 
     * @param i_BeginIndex      截取前端的字符长度
     * @param i_EndOffsetIndex  截取后端的字符长度
     * @return
     */
    public String getInfo(int i_Index)
    {
        return this.getInfo(i_Index ,i_Index);
    }
    
    
    
    /**
     * 截取前后两端，只有中间部分的字符信息
     * 
     * @param i_BeginIndex      截取前端的字符长度
     * @param i_EndOffsetIndex  截取后端的字符长度
     * @return
     */
    public String getInfo(int i_BeginIndex ,int i_EndOffsetIndex)
    {
        if ( Help.isNull(this.info) )
        {
            return this.info;
        }
        
        return this.info.substring(i_BeginIndex ,this.info.length() - i_EndOffsetIndex);
    }


    
    public void setInfo(String info)
    {
        this.info = info;
    }


    
    public int getBeginIndex()
    {
        return beginIndex;
    }


    
    public void setBeginIndex(int beginIndex)
    {
        this.beginIndex = beginIndex;
    }


    
    public int getEndIndex()
    {
        return endIndex;
    }


    
    public void setEndIndex(int endIndex)
    {
        this.endIndex = endIndex;
    }


    
    public InfoType getInfoType()
    {
        return infoType;
    }


    
    public void setInfoType(InfoType infoType)
    {
        this.infoType = infoType;
    }
    
    
    
    public int getChildSize()
    {
        if ( this.childs == null )
        {
            return 0;
        }
        else
        {
            return this.childs.size();
        }
    }


    
    public synchronized List<SplitSegment> getChilds()
    {
        if ( this.childs == null )
        {
            this.childs = new ArrayList<SplitSegment>();
        }
        return childs;
    }


    
    public synchronized void setChilds(List<SplitSegment> childs)
    {
        this.childs = childs;
    }

    

    @Override
    public SplitSegment clone()
    {
        return new SplitSegment(this);
    }



    @Override
    public String toString()
    {
        return (this.infoType == null ? "?" : this.infoType) + ":" 
             + (this.info     == null ? ""  : this.info);
    }
    
}
