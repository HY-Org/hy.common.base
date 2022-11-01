package org.hy.common.app;

import org.hy.common.Help;





/**
 * 分页信息的数据封装类
 * 
 * 引用于2005年开发的Pagination项目
 *
 * @author      ZhengWei(HY)
 * @createDate  2005-12-04
 * @version     v1.0
 */
public class PageInfo<O> implements java.io.Serializable
{

    private static final long serialVersionUID = 7576540312390196308L;
    
    

    /** 分页中当前页中要显示的记录 */
    private O   datas;

    /** 得到某一查寻语句的执行结果中一共有多少条记录 */
    private int maxRowCount;

    /** 每页分为多少行 */
    private int pagePerCount;

    /** 一共分为多少页(不要此属性是可以的，可以计算出来，但为了加快程序的速度，所以才加上，并且这样好理解) */
    private int maxPageCount;

    /** 当前分页的分页号。下标从1开始 */
    private int currentPage;

    
    
    /**
     * 构造子 因为本类是在内部被创建的，所以在此没有进行数据的有效性检测。
     * 
     * @param i_MaxRowCount   一共有多少条记录
     * @param i_PagePerCount  每页分为多少行
     * @param i_CurrentPage   当前分页的分页号。下标从1开始
     */
    public PageInfo(int i_MaxRowCount ,int i_PagePerCount ,int i_CurrentPage)
    {
        this(null ,i_MaxRowCount ,i_PagePerCount ,i_CurrentPage);
    }
    
    

    /**
     * 构造子 因为本类是在内部被创建的，所以在此没有进行数据的有效性检测。
     * 
     * @param i_Datas         分页中当前页中要显示的记录
     * @param i_MaxRowCount   一共有多少条记录
     * @param i_PagePerCount  每页分为多少行
     * @param i_CurrentPage   当前分页的分页号
     */
    public PageInfo(O i_Datas ,int i_MaxRowCount ,int i_PagePerCount ,int i_CurrentPage)
    {
        this.datas        = i_Datas;
        this.maxRowCount  = i_MaxRowCount;
        this.pagePerCount = i_PagePerCount;
        this.currentPage  = i_CurrentPage;
        this.maxPageCount = (int)Help.division(this.maxRowCount , this.pagePerCount);
        if ( (this.maxRowCount % this.pagePerCount) != 0 )
        {
            this.maxPageCount += 1;
        }
    }


    /**
     * 返回分页中当前页中要显示的记录
     *
     * @return  返回分页中当前页中要显示的记录
     */
    public O getDatas()
    {
        return this.datas;
    }
    
    
    
    public void setDatas(O i_Datas)
    {
        this.datas = i_Datas;
    }


    /**
     * 得到每页分页中的记录数
     *
     * @return 得到每页分页中的记录数
     */
    public int getPagePerCount()
    {
        return this.pagePerCount;
    }



    /**
     * 得到某一查寻语句的执行结果中一共有多少条记录
     * @return 得到某一查寻语句的执行结果中一共有多少条记录
     */
    public int getMaxRowCount()
    {
        return this.maxRowCount;
    }



    /**
     * 一共分为多少页
     * @return 一共分为多少页
     */
    public int getMaxPageCount()
    {
        return this.maxPageCount;
    }



    /**
     * 返回当前分页的分页号
     * @return 返回当前分页的分页号
     */
    public int getCurrentPageNum()
    {
        return this.currentPage;
    }



    /**
     * 返回当前分页的前一页的分页号
     * @return 返回当前分页的前一页的分页号
     */
    public int getPreviousPageNum()
    {
        int v_PreviousPageNum = this.currentPage - 1;
        if ( v_PreviousPageNum <= 0 )
        {
            v_PreviousPageNum = 1;
        }
        return v_PreviousPageNum;
    }



    /**
     * 返回当前分页的下一页的分页号
     * @return 返回当前分页的下一页的分页号
     */
    public int getNextPageNum()
    {
        int v_NextPageNum = this.currentPage + 1;
        if ( v_NextPageNum > this.maxPageCount )
        {
            v_NextPageNum = this.maxPageCount;
        }
        return v_NextPageNum;
    }



    /**
     * 返回当前分页已前的所有记录总数
     * @return 返回当前分页已前的所有记录总数
     */
    public int getPreviousPagesRowCount()
    {
        return (this.currentPage - 1) * this.pagePerCount;
    }



    /**
     * 返回首页的分页号
     * @return 返回首页的分页号
     */
    public int getHeadPageNum()
    {
        return 1;
    }



    /**
     * 返回尾页的分页号
     * @return 返回尾页的分页号
     */
    public int getFootPageNum()
    {
        return this.maxPageCount;
    }
    
}
