package org.hy.common.app;

import org.hy.common.Date;





/**
 * 项目版本信息，及对应的数据库脚本的版本信息
 *
 * @author      ZhengWei(HY)
 * @createDate  2015-01-28
 * @version     v1.0
 */
public class Version implements java.io.Serializable
{
    
    private static final long serialVersionUID = 1776033007651113014L;
    
    

    /** 版本号 */
    private String  versionNo;
    
    /** 启动时间 */
    private Date    startsDate;
    
    /** 完成时间 */
    private Date    finishDate;
    
    /** 使用天数。当为空时，自动计算，只简单将工作日计算在内。 */
    private Integer dayCount;
    
    /** 数据库脚本的版本 */
    private String  dbVersion;
    
    /** 版本说明 */
    private String  comment;

    
    
    /**
     * 获取：版本号
     */
    public String getVersionNo()
    {
        return versionNo;
    }

    
    /**
     * 设置：版本号
     * 
     * @param versionNo 
     */
    public void setVersionNo(String versionNo)
    {
        this.versionNo = versionNo;
    }

    
    /**
     * 获取：启动时间
     */
    public Date getStartsDate()
    {
        return startsDate;
    }

    
    /**
     * 设置：启动时间
     * 
     * @param startsDate 
     */
    public void setStartsDate(Date startsDate)
    {
        this.startsDate = startsDate;
    }


    /**
     * 获取：完成时间
     */
    public Date getFinishDate()
    {
        return finishDate;
    }

    
    /**
     * 设置：完成时间
     * 
     * @param finishDate 
     */
    public void setFinishDate(Date finishDate)
    {
        this.finishDate = finishDate;
    }

    
    /**
     * 获取：使用天数。当为空时，自动计算，只简单将工作日计算在内。
     */
    public Integer getDayCount()
    {
        return dayCount;
    }

    
    /**
     * 设置：使用天数。当为空时，自动计算，只简单将工作日计算在内。
     * 
     * @param dayCount 
     */
    public void setDayCount(Integer dayCount)
    {
        this.dayCount = dayCount;
    }

    
    /**
     * 获取：数据库脚本的版本
     */
    public String getDbVersion()
    {
        return dbVersion;
    }

    
    /**
     * 设置：数据库脚本的版本
     * 
     * @param dbVersion 
     */
    public void setDbVersion(String dbVersion)
    {
        this.dbVersion = dbVersion;
    }

    
    /**
     * 获取：版本说明
     */
    public String getComment()
    {
        return comment;
    }

    
    /**
     * 设置：版本说明
     * 
     * @param comment 
     */
    public void setComment(String comment)
    {
        this.comment = comment;
    }
    
}
