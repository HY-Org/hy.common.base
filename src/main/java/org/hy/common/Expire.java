package org.hy.common;

import java.util.Map;





/**
 * 定义此接口后，外界才能有限制的访问。如访问 ExpireMap.put( , , ) 的返回值。
 *
 * @author      ZhengWei(HY)
 * @createDate  2016-02-25
 * @version     v1.0
 *              v2.0  2017-02-28 添加：创建时间
 */
public interface Expire<K ,V> extends Map.Entry<K ,V> ,java.io.Serializable
{
    
    /**
     * 获取：时间戳。保存期满时间。0表示永远有效
     */
    public long getTime();
    
    
    
    /**
     * 创建时间(首次将Key添加到集合时的时间)
     * 
     * @author      ZhengWei(HY)
     * @createDate  2017-02-28
     * @version     v1.0
     *
     * @return
     */
    public Date getCreateTime();
    
}
