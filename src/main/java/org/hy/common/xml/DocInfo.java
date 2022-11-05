package org.hy.common.xml;

import java.io.Serializable;
import java.util.Map;

import org.hy.common.xml.annotation.Doc;





/**
 * Java代码注释 Doc 的包装类。
 * 
 * 包装成有父子关系、有层次结构的类
 * 
 * @author      ZhengWei(HY)
 * @createDate  2014-12-01
 * @version     v1.0  
 */
public class DocInfo implements Serializable
{
    
    private static final long serialVersionUID = 5590371408236141711L;
    
    

    /** 
     * 注解信息
     * 
     * 注意：按道理来说，此属性不应为空。但当它为空时，表示为由'我'自己创建出的 DocInfo 对象实例。其它无外。
     * 上面情况主要针对 List<List<Object>> 这种两层 List 结构
     */
    private Doc                   doc;
    
    /** 子类信息 */
    private Map<String ,DocInfo>  childs;
    
    /** 子类的类型 */
    private EChildsType           childsType;
    
    /** 父节点对象 */
    private DocInfo               superDocInfo;
    
    
    
    public DocInfo()
    {
        this(null);
    }
    
    
    
    public DocInfo(Doc i_Doc)
    {
        this(i_Doc ,null);
    }
    
    
    
    public DocInfo(Doc i_Doc ,Map<String ,DocInfo> i_Childs)
    {
        this(i_Doc ,i_Childs ,EChildsType.$Object);
    }
    
    
    
    public DocInfo(Doc i_Doc ,Map<String ,DocInfo> i_Childs ,EChildsType i_ChildsType)
    {
        this.doc        = i_Doc;
        this.childs     = i_Childs;
        this.childsType = i_ChildsType;
    }
    
    

    public String index()
    {
        return this.doc == null ? "" : this.doc.index();
    }

    
    
    public String [] value()
    {
        return this.doc == null ? null : this.doc.value();
    }
    
    

    public String info()
    {
        return this.doc == null ? "" : this.doc.info();
    }



    public Doc getDoc()
    {
        return doc;
    }


    
    public void setDoc(Doc doc)
    {
        this.doc = doc;
    }


    
    public Map<String ,DocInfo> getChilds()
    {
        return childs;
    }


    
    public void setChilds(Map<String ,DocInfo> childs)
    {
        this.childs = childs;
    }


    
    public EChildsType getChildsType()
    {
        return childsType;
    }


    
    public void setChildsType(EChildsType childsType)
    {
        this.childsType = childsType;
    }



    public DocInfo getSuperDocInfo()
    {
        return superDocInfo;
    }


    
    public void setSuperDocInfo(DocInfo superDocInfo)
    {
        this.superDocInfo = superDocInfo;
    }
    
}





/**
 * 子类的类型
 * 
 * @author      ZhengWei(HY)
 * @createDate  2014-12-02
 * @version     v1.0  
 */
enum EChildsType
{
    
    /** 普通对象类型 */
    $Object,
    
    /** List集合类型，包括 Set 集合 */
    $List,
    
    /** Map集合类型 */
    $Map;
    
}
