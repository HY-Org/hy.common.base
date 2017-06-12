package org.hy.common.junit;

import org.hy.common.Date;





/**
 * 测试：解释Java对象成为JSON
 * 
 * @author      ZhengWei(HY)
 * @version     v1.0  
 * @createDate  2013-08-13
 */
public class JU_XJSON
{
    private String            FORMAT;
    
    private String            LENGTH;
    
    private String            MID;
    
    private Date              REQTIME;
    
    private String            SID;
    
    private String            SIGN;
    
    private String            SYSID;
    
    private String            TOKEN;
    
    private JU_XJSON_BODYType BODY;  
    
    private boolean           bolean;
    
    private Object            obj;
    
    private double            doubleValue;
    
    
    
    public JU_XJSON()
    {
        this.FORMAT  = "json";
        this.LENGTH  = "70";
        this.MID     = "0d653fbb-e09c-4521-8571-1a4f2b4e4f6a";
        this.REQTIME = new Date();
        this.SID     = "SC2017000";
        this.SIGN    = "189FC061D401617CA12D255735A06671";
        this.SYSID   = "1002";
        this.TOKEN   = "427034796155AD7610BCDC9F091F7AA9";
        this.BODY    = new JU_XJSON_BODYType();
    }
    
    
    
    public JU_XJSON(String i_SID ,String i_Sign ,double i_Double)
    {
        this.FORMAT      = "json";
        this.LENGTH      = "70";
        this.MID         = "0d653fbb-e09c-4521-8571-1a4f2b4e4f6a";
        this.REQTIME     = new Date();
        
        try
        {
            Thread.sleep(1000);
        }
        catch (Exception exce)
        {
            exce.printStackTrace();
        }
        
        this.SID         = i_SID;
        this.SIGN        = i_Sign;
        this.doubleValue = i_Double;
        this.SYSID       = "1002";
        this.TOKEN       = "427034796155AD7610BCDC9F091F7AA9";
        this.BODY        = new JU_XJSON_BODYType();
    }

    
    
    public JU_XJSON_BODYType getBODY()
    {
        return BODY;
    }
    

    
    public void setBODY(JU_XJSON_BODYType body)
    {
        BODY = body;
    }


    
    public String getFORMAT()
    {
        return FORMAT;
    }


    
    public void setFORMAT(String format)
    {
        FORMAT = format;
    }


    
    public String getLENGTH()
    {
        return LENGTH;
    }


    
    public void setLENGTH(String length)
    {
        LENGTH = length;
    }


    
    public String getMID()
    {
        return MID;
    }


    
    public void setMID(String mid)
    {
        MID = mid;
    }


    
    public Date getREQTIME()
    {
        return REQTIME;
    }


    
    public void setREQTIME(Date reqtime)
    {
        REQTIME = reqtime;
    }


    
    public String getSID()
    {
        return SID;
    }


    
    public void setSID(String sid)
    {
        SID = sid;
    }


    
    public String getSIGN() throws Exception
    {
        return this.SIGN;
    }


    
    public void setSIGN(String sign)
    {
        SIGN = sign;
    }


    
    public String getSYSID()
    {
        return SYSID;
    }


    
    public void setSYSID(String sysid)
    {
        SYSID = sysid;
    }


    
    public String getTOKEN()
    {
        return TOKEN;
    }


    
    public void setTOKEN(String token)
    {
        TOKEN = token;
    }
    
    
    
    public boolean isBolean()
    {
        return bolean;
    }


    
    public Object getObj()
    {
        return obj;
    }


    
    public double getDoubleValue()
    {
        return doubleValue;
    }



    public void setDoubleValue(double doubleValue)
    {
        this.doubleValue = doubleValue;
    }
    
}
