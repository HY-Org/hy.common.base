package org.hy.common;

import java.util.Hashtable;
import java.util.Map;
import org.hy.common.Date;





/**
 * 测试：解释Java对象成为JSON
 * 
 * @author      ZhengWei(HY)
 * @version     v1.0  
 * @createDate  2013-08-13
 */
public class JU_XJSON_BODYType
{
    private String latnId;
    
    private String prodType;
    
    private String beginTime;
    
    private String endTime;
    
    /** 1 网厅 2 WAP 3 短厅 */
    private String channelId;
    
    private String staffId;
    
    private String serviceNbr;
    
    private Date   hyDate;
    
    private boolean hyBooelan;
    
    
    
    public JU_XJSON_BODYType()
    {
        this.latnId     = "290";
        this.prodType   = "41010300";
        this.beginTime  = "20130801000000";
        this.endTime    = "20130815000000";
        this.channelId  = "1";
        this.staffId    = "KF01016";
        this.serviceNbr = "18066808930";
        // this.hyDate     = new Date();
    }
    
    
    public String getBeginTime()
    {
        return beginTime;
    }

    
    public void setBeginTime(String beginTime)
    {
        this.beginTime = beginTime;
    }

    
    public String getChannelId()
    {
        return channelId;
    }

    
    public void setChannelId(String channelId)
    {
        this.channelId = channelId;
    }

    
    public String getEndTime()
    {
        return endTime;
    }

    
    public void setEndTime(String endTime)
    {
        this.endTime = endTime;
    }

    
    public String getLatnId()
    {
        return latnId;
    }

    
    public void setLatnId(String latnId)
    {
        this.latnId = latnId;
    }

    
    public String getProdType()
    {
        return prodType;
    }

    
    public void setProdType(String prodType)
    {
        this.prodType = prodType;
    }

    
    public String getServiceNbr()
    {
        return serviceNbr;
    }

    
    public void setServiceNbr(String serviceNbr)
    {
        this.serviceNbr = serviceNbr;
    }

    
    public String getStaffId()
    {
        return staffId;
    }

    
    public void setStaffId(String staffId)
    {
        this.staffId = staffId;
    }
    

    public Date getHyDate()
    {
        return hyDate;
    }

    
    public void setHyDate(Date hyDate)
    {
        this.hyDate = hyDate;
    }

    
    public boolean isHyBooelan()
    {
        return hyBooelan;
    }


    public void setHyBooelan(boolean hyBooelan)
    {
        this.hyBooelan = hyBooelan;
    }


    public Map<String ,Object> toMap()
    {
        Map<String ,Object> v_Ret = new Hashtable<String ,Object>();
        
        v_Ret.put("latnId"     ,this.latnId);
        v_Ret.put("prodType"   ,this.prodType);
        v_Ret.put("beginTime"  ,this.beginTime);
        v_Ret.put("endTime"    ,this.endTime);
        v_Ret.put("channelId"  ,this.channelId);
        v_Ret.put("staffId"    ,this.staffId);
        v_Ret.put("serviceNbr" ,this.serviceNbr);
        
        return v_Ret;
    }
    
}