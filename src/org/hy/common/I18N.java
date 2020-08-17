package org.hy.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hy.common.app.Param;





/**
 * I18N 国际化类
 * 
 * @author      ZhengWei(HY)
 * @version     v1.0
 * @createDate  2014-07-10
 */
public class I18N implements java.io.Serializable
{
    
    private static final long serialVersionUID = 3341255191566194267L;
    
    

    /** 多国语言文本 */
    private TablePartitionRID<String ,String> datas;
    
    /** 当前多国语言标识 */
    private String language;
    
    /** 对于多国语言标识是否区分大小写：默认是忽略大小写的 */
    private boolean isIgnoreCase;
    
    
    
    public I18N()
    {
        this("");
    }
    
    
    
    public I18N(String i_Language)
    {
        this.datas        = new TablePartitionRID<String ,String>();
        this.isIgnoreCase = true;
        this.setLanguage(i_Language);
    }
    
    
    
    /**
     * 按文本ID，获取多国语言文本对象
     * 
     * @param i_TextID
     * @return
     */
    public Param getITextInfo(String i_TextID)
    {
        return this.getITextInfo(this.getLanguage() ,i_TextID);
    }
    
    
    
    /**
     * 指定语言，并按文本ID，获取多国语言文本对象
     * 
     * @param i_TextID
     * @return
     */
    public Param getITextInfo(String i_Language ,String i_TextID)
    {
        if ( Help.isNull(i_Language) )
        {
            return null;
        }
        
        if ( Help.isNull(i_TextID) )
        {
            return null;
        }
        
        Param v_Param = new Param();
        
        v_Param.setName(i_TextID);
        v_Param.setValue(this.getIText(i_Language ,i_TextID));
        v_Param.setComment(i_Language);
        
        return v_Param;
    }
    
    
    
    /**
     * 指定语言，获取其下所有多国语言文本对象
     * 
     * @param i_Language
     * @return
     */
    public List<Param> getITextInfos(String i_Language)
    {
        if ( Help.isNull(i_Language) )
        {
            return null;
        }
        
        Map<String ,String> v_ITexts = this.datas.get(i_Language.trim().toUpperCase());
        List<Param>         v_Params = new ArrayList<Param>();
        
        for (String v_ITextID : v_ITexts.keySet())
        {
            v_Params.add(new Param(v_ITextID ,v_ITexts.get(v_ITextID)));
        }
        
        return v_Params;
    }
    
    
    
    /**
     * 按文本ID，获取多国语言文本
     * 
     * @param i_TextID
     * @return
     */
    public synchronized String getIText(String i_TextID)
    {
        return this.getIText(this.getLanguage() ,i_TextID);
    }
    
    
    
    /**
     * 指定语言，并按文本ID，获取多国语言文本
     * 
     * @param i_TextID
     * @return
     */
    public synchronized String getIText(String i_Language ,String i_TextID)
    {
        if ( Help.isNull(i_Language) )
        {
            return "";
        }
        
        if ( Help.isNull(i_TextID) )
        {
            return "";
        }
        
        if ( this.datas.containsKey(i_Language.trim()) )
        {
            if ( this.isIgnoreCase )
            {
                return this.datas.getRow(i_Language.trim().toUpperCase() ,i_TextID.trim());
            }
            else
            {
                return this.datas.getRow(i_Language.trim()               ,i_TextID.trim());
            }
        }
        else
        {
            throw new RuntimeException("Language[" + i_Language + "] is not find.");
        }
    }
    
    
    
    /**
     * 自动生成对应的文本ID
     * 
     * @return
     */
    private synchronized String makeTextId()
    {
        String v_Language = this.getLanguage();
        
        if ( Help.isNull(v_Language) )
        {
            throw new NullPointerException("Language is null.");
        }
        
        return "I" + StringHelp.lpad(this.datas.get(v_Language).size() + 1 ,6 ,"0");
    }
    
    
    
    /**
     * 添加多国语言文本，并自动生成对应的文本ID
     * 
     * @param i_Text
     * @return          返回文本ID
     */
    public synchronized String setIText(String i_Text)
    {
        return this.setIText(this.getLanguage() ,makeTextId() ,i_Text);
    }
    
    
    
    /**
     * 添加多国语言文本
     * 
     * @param i_TextInfo
     * @return
     */
    public String setIText(Param i_TextInfo)
    {
        return this.setIText(i_TextInfo.getName() ,i_TextInfo.getValue());
    }
    
    
    
    /**
     * 添加文本ID及其对应的多国语言文本
     * 
     * @param i_TextID
     * @param i_Text
     * @return          返回文本ID
     */
    public String setIText(String i_TextID ,String i_Text)
    {
        return this.setIText(this.getLanguage() ,i_TextID ,i_Text);
    }
    
    
    
    /**
     * 指定语言，添加文本ID及其对应的多国语言文本
     * 
     * @param i_TextID
     * @param i_Text
     * @return          返回文本ID
     */
    public String setIText(String i_Language ,String i_TextID ,String i_Text)
    {
        if ( Help.isNull(i_Language) )
        {
            throw new NullPointerException("Language is null.");
        }
        
        if ( Help.isNull(i_TextID) )
        {
            throw new NullPointerException("Text ID is null.");
        }
        
        if ( this.isIgnoreCase )
        {
            this.datas.putRow(i_Language.trim().toUpperCase() ,i_TextID.trim() ,i_Text);
        }
        else
        {
            this.datas.putRow(i_Language.trim()               ,i_TextID.trim() ,i_Text);
        }
        
        return i_TextID.trim();
    }
    

    
    /**
     * 获取当前语言
     * 
     * @return
     */
    public String getLanguage()
    {
        return language;
    }


    
    public void setLanguage(String i_Language)
    {
        if ( this.isIgnoreCase )
        {
            this.language = i_Language.trim().toUpperCase();
        }
        else
        {
            this.language = i_Language.trim();
        }
    }
    
    
    
    /**
     * 获取所有语言列表
     * 
     * @return
     */
    public List<String> getLanguages()
    {
        return new ArrayList<String>(this.datas.keySet());
    }
    
    
    
    public boolean isIgnoreCase()
    {
        return isIgnoreCase;
    }


    
    public void setIgnoreCase(boolean isIgnoreCase)
    {
        this.isIgnoreCase = isIgnoreCase;
    }
    
}
