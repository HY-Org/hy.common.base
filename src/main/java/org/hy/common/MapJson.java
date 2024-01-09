package org.hy.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;





/**
 * 由Map和List<Map>组成的层次结构，并且是按Map.key的关键字分隔符分隔层次的。
 * 
 * 支持1：无限层次的分隔
 * 支持2：局部同步锁，并非绝对安全
 * 支持3：关键字格式有
 *         格式1：keyA.keyB.keyC。                 等效于 keyA[最大索引号].keyB[最大索引号].keyC
 *         格式2：keyA[indexA].keyB[indexB].keyC   指定索引号的元素
 *         格式3：keyA[indexA]                     指定索引号的MapJson子集合
 *         格式4：keyA                             List集合或普通元素
 * 
 * 注意：在组织数据结构时，默认仅可【前向添加】，不可后向修改。
 *      添加数据时，建议按层次顺序添加，就类似Json文件从前向后显示层次顺序一样。
 * 
 *    可指定具体下标索引的方式【后向修改】，如 put("datas[0].name" ,"Hello Future")
 * 
 *    如默认按英文点分隔 put("datas.name" ,"Hello") 时，生成 Map<String ,List<Map<String ,Object>> 的结构
 *    即 Map.get("datas") 的结构是 List<Map<String ,Object>。
 * 
 *    再次 put("datas.name" ,"World") 时，会判定 List 最后一元素的Map是否有值，如果有值则创建新 Map，并添加到List中。
 *    所以 Map.get("datas.name") 返回的是 "World"，即List中最后一个元素的Map的元素name的值。
 * 
 *    但可以通过 Map.get("datas[0].name") 返回 Hello
 *              Map.get("datas[1].name") 返回 World
 *
 * @author      ZhengWei(HY)
 * @createDate  2024-01-09
 * @version     v1.0
 */
public class MapJson extends HashMap<String ,Object>
{

    private static final long serialVersionUID = -8563344835087786506L;
    
    private static final String $IndexLeft  = "[";
    
    private static final String $IndexRight = "]";
    
    
    
    /** 数据结构层次的分隔符。默认为：英文的点 */
    private String splitValue;
    
    /** List集合索引的标识符左 */
    private String indexLeft;
    
    /** List集合索引的标识符左 */
    private String indexRight;
    
    
    
    public MapJson()
    {
        this((String) null);
    }
    
    
    
    public MapJson(String i_SplitValue)
    {
        this(null ,null ,null);
    }
    
    
    
    public MapJson(String i_SplitValue ,String i_IndexLeft ,String i_IndexRight)
    {
        super();
        this.satSplitValue(Help.NVL(i_SplitValue ,"."));
        this.satIndexLeft( Help.NVL(i_IndexLeft  ,"["));
        this.satIndexRight(Help.NVL(i_IndexRight ,"]"));
    }
    
    
    
    public MapJson(int i_InitialCapacity)
    {
        this(i_InitialCapacity ,null ,null ,null);
    }
    
    
    
    public MapJson(int i_InitialCapacity ,String i_SplitValue)
    {
        this(i_InitialCapacity ,i_SplitValue ,null ,null);
    }
    
    
    
    public MapJson(int i_InitialCapacity ,String i_SplitValue ,String i_IndexLeft ,String i_IndexRight)
    {
        super(i_InitialCapacity);
        this.satSplitValue(Help.NVL(i_SplitValue ,"."));
        this.satIndexLeft( Help.NVL(i_IndexLeft  ,"["));
        this.satIndexRight(Help.NVL(i_IndexRight ,"]"));
    }


    
    /**
     * 获取：数据结构层次的分隔符。默认为：英文的点
     */
    public String gatSplitValue()
    {
        return splitValue;
    }


    
    /**
     * 设置：数据结构层次的分隔符。默认为：英文的点
     * 
     * @param i_SplitValue 数据结构层次分隔符。默认为：英文的点
     */
    public void satSplitValue(String i_SplitValue)
    {
        if ( Help.isNull(i_SplitValue) )
        {
            throw new NullPointerException("SplitValue is null");
        }
        this.splitValue = i_SplitValue;
    }
    
    
    
    /**
     * 获取：List集合索引的标识符左
     */
    public String gatIndexLeft()
    {
        return indexLeft;
    }


    
    /**
     * 设置：List集合索引的标识符左
     * 
     * @param i_IndexLeft List集合索引的标识符左
     */
    public void satIndexLeft(String i_IndexLeft)
    {
        if ( Help.isNull(i_IndexLeft) )
        {
            throw new NullPointerException("IndexLeft is null");
        }
        this.indexLeft = i_IndexLeft.trim();
    }


    
    /**
     * 获取：List集合索引的标识符左
     */
    public String gatIndexRight()
    {
        return indexRight;
    }


    
    /**
     * 设置：List集合索引的标识符左
     * 
     * @param i_IndexRight List集合索引的标识符左
     */
    public void satIndexRight(String i_IndexRight)
    {
        if ( Help.isNull(i_IndexRight) )
        {
            throw new NullPointerException("IndexRight is null");
        }
        this.indexRight = i_IndexRight;
    }



    /**
     * 解释Map的关键字，解释出它的第一层次和其它层次
     * 
     * 解释格式1：keyA.keyB.keyC。   等效于 keyA[最大索引号].keyB[最大索引号].keyC
     * 解释格式2：keyA[indexA].keyB[indexB].keyC
     * 
     * @author      ZhengWei(HY)
     * @createDate  2024-01-09
     * @version     v1.0
     *
     * @param i_Key    支持数据结构层次的分隔符分层的模式
     * @return         [0]  分隔符分隔的首个关键字。不包含 [数字] 的部分
     *                 [1]  分隔符分隔的首个关键字中的 [数字] 的部分，可以为空或没有，表示操作最后的元素
     *                 [2]  分隔符分隔的其它关键字
     */
    public String [] parserKey(String i_Key)
    {
        int    v_FirstDot   = i_Key.indexOf(this.splitValue);
        String v_FirstKey   = i_Key;
        String v_FirstIndex = null;
        String v_OtherKey   = null;
        
        if ( v_FirstDot > 0 && v_FirstDot + 1 < i_Key.length())
        {
            v_FirstKey = i_Key.substring(0 ,v_FirstDot);
            v_OtherKey = i_Key.substring(v_FirstDot + 1);
        }
        
        int v_IndexLeft = v_FirstKey.indexOf($IndexLeft);
        if ( v_IndexLeft > 0 )
        {
            int v_IndexRight = v_FirstKey.indexOf($IndexRight);
            if ( v_IndexRight > v_IndexLeft )
            {
                v_FirstIndex = v_FirstKey.substring(v_IndexLeft + 1 ,v_IndexRight);
                if ( Help.isNumber(v_FirstIndex) )
                {
                    v_FirstKey = v_FirstKey.substring(0 ,v_IndexLeft);
                }
                else
                {
                    v_FirstIndex = null;
                }
            }
        }
        
        return new String [] {v_FirstKey ,v_FirstIndex ,v_OtherKey};
    }
    
    
    
    /**
     * 赋值
     *
     * @author      ZhengWei(HY)
     * @createDate  2024-01-09
     * @version     v1.0
     *
     * @param i_Key    支持数据结构层次的分隔符分层的模式
     * @param i_Value
     * @return
     *
     * @see java.util.HashMap#put(java.lang.Object, java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    @Override
    public Object put(String i_Key ,Object i_Value)
    {
        if ( i_Key == null )
        {
            throw new NullPointerException("Key is null.");
        }
        
        String [] v_Keys = this.parserKey(i_Key);
        if ( v_Keys[2] != null )
        {
            List<MapJson> v_Child = null;
            synchronized (this)
            {
                Object v_OldChild = this.get(v_Keys[0]);
                if ( v_OldChild == null )
                {
                    v_Child = new ArrayList<MapJson>();
                    super.put(v_Keys[0] ,v_Child);
                }
                else if ( v_OldChild instanceof List )
                {
                    v_Child = (List<MapJson>) v_OldChild;
                }
                else
                {
                    v_Child = new ArrayList<MapJson>();
                    super.put(v_Keys[0] ,v_Child);
                }
            }
            
            MapJson v_ChildMapJson = null;
            if ( v_Child.size() == 0 )
            {
                v_ChildMapJson = new MapJson(this.splitValue ,this.indexLeft ,this.indexRight);
                v_Child.add(v_ChildMapJson);
            }
            else if ( v_Keys[1] != null )
            {
                int v_KeyIndex = Integer.parseInt(v_Keys[1]);
                if ( v_KeyIndex < v_Child.size() )
                {
                    v_ChildMapJson = v_Child.get(v_KeyIndex);
                }
                else
                {
                    throw new IndexOutOfBoundsException(i_Key + " of " + v_Keys[0] + "[" + v_Keys[1] + "] is IndexOutOfBounds");
                }
            }
            else
            {
                v_ChildMapJson = v_Child.get(v_Child.size() - 1);
                
                // 已递归到最底层时
                if ( v_Keys[2].indexOf(this.splitValue) <= 0 )
                {
                    // 最后的元素属性存在时，添加新的List元素
                    if ( v_ChildMapJson.containsKey(v_Keys[2]) )
                    {
                        v_ChildMapJson = new MapJson(this.splitValue ,this.indexLeft ,this.indexRight);
                        v_Child.add(v_ChildMapJson);
                    }
                }
            }
            
            v_ChildMapJson.put(v_Keys[2] ,i_Value);
            return v_Child;
        }
        else
        {
            return super.put(v_Keys[0] ,i_Value);
        }
    }
    
    
    
    /**
     * 复制另一个Map集合。
     * 
     *   支持1：深度克隆模式，即参数i_Map.clear()，也不影响this
     *   支持2：不强制要求参数i_Map也是一个MapJson结构，但对不符合MapJson数据结构的数据不添加
     *   支持3：参数i_Map.key允许是 keyA[indexA].keyB[indexB].keyC 等格式
     *
     * @author      ZhengWei(HY)
     * @createDate  2024-01-09
     * @version     v1.0
     *
     * @param i_Map
     *
     * @see java.util.HashMap#putAll(java.util.Map)
     */
    @SuppressWarnings("unchecked")
    @Override
    public void putAll(Map<? extends String ,? extends Object> i_Map)
    {
        Iterator<? extends Map.Entry<? extends String, ? extends Object>> i = i_Map.entrySet().iterator();
        
        while (i.hasNext())
        {
            Map.Entry<? extends String, ? extends Object> v_Item = i.next();
            
            Object v_OrgValue = this.get(v_Item.getKey());
            if ( v_OrgValue == null )
            {
                if ( v_Item.getValue() == null )
                {
                    this.put(v_Item.getKey(), v_Item.getValue());
                }
                else if ( v_Item.getValue() instanceof List )
                {
                    List<MapJson> v_OrgValueList = new ArrayList<MapJson>();
                    this.put(v_Item.getKey() ,v_OrgValueList);
                    
                    List<Object> v_ItemValueList = (List<Object>) v_Item.getValue();
                    for (Object v_ItemChild : v_ItemValueList)
                    {
                        if ( v_ItemChild instanceof Map )
                        {
                            MapJson v_OrgJsonMap = new MapJson(this.splitValue ,this.indexLeft ,this.indexRight);
                            v_OrgJsonMap.putAll((Map<String ,Object>) v_ItemChild);
                            v_OrgValueList.add(v_OrgJsonMap);
                        }
                        else
                        {
                            // 非Map结构的，不添加
                        }
                    }
                }
                else if ( v_Item.getValue() instanceof Map )
                {
                    List<MapJson> v_OrgValueList = new ArrayList<MapJson>();
                    this.put(v_Item.getKey() ,v_OrgValueList);
                    
                    MapJson v_OrgJsonMap = new MapJson(this.splitValue ,this.indexLeft ,this.indexRight);
                    v_OrgJsonMap.putAll((Map<String ,Object>) v_Item.getValue());
                    
                    v_OrgValueList.add(v_OrgJsonMap);
                }
                else
                {
                    this.put(v_Item.getKey(), v_Item.getValue());
                }
            }
            else if ( v_OrgValue instanceof List )
            {
                List<MapJson> v_OrgValueList = (List<MapJson>) v_OrgValue;
                
                if ( v_Item.getValue() == null )
                {
                    continue;
                }
                else if ( v_Item.getValue() instanceof List )
                {
                    List<Object> v_ItemValueList = (List<Object>) v_Item.getValue();
                    for (Object v_ItemChild : v_ItemValueList)
                    {
                        if ( v_ItemChild instanceof Map )
                        {
                            MapJson v_OrgJsonMap = new MapJson(this.splitValue ,this.indexLeft ,this.indexRight);
                            v_OrgJsonMap.putAll((Map<String ,Object>) v_ItemChild);
                            v_OrgValueList.add(v_OrgJsonMap);
                        }
                        else
                        {
                            // 非Map结构的，不添加
                        }
                    }
                }
                else if ( v_Item.getValue() instanceof Map )
                {
                    MapJson v_OrgJsonMap = new MapJson(this.splitValue ,this.indexLeft ,this.indexRight);
                    v_OrgJsonMap.putAll((Map<String ,Object>) v_Item.getValue());
                    
                    v_OrgValueList.add(v_OrgJsonMap);
                }
                else
                {
                    // 非Map结构的，不添加
                }
            }
            else if ( v_OrgValue instanceof Map )
            {
                MapJson v_OrgJsonMap = (MapJson) v_OrgValue;
                
                if ( v_Item.getValue() == null )
                {
                    v_OrgJsonMap.put(v_Item.getKey() ,v_Item.getValue());
                }
                else if ( v_Item.getValue() instanceof List )
                {
                    // 按理说应当没有同层结构中我是Map对你List的情况，如果真出现了就覆盖
                    v_OrgJsonMap.clear();
                    
                    List<MapJson> v_OrgValueList = new ArrayList<MapJson>();
                    this.put(v_Item.getKey() ,v_OrgValueList);
                    
                    List<Object> v_ItemValueList = (List<Object>) v_Item.getValue();
                    for (Object v_ItemChild : v_ItemValueList)
                    {
                        if ( v_ItemChild instanceof Map )
                        {
                            v_OrgJsonMap = new MapJson(this.splitValue ,this.indexLeft ,this.indexRight);
                            v_OrgJsonMap.putAll((Map<String ,Object>) v_ItemChild);
                            v_OrgValueList.add(v_OrgJsonMap);
                        }
                        else
                        {
                            // 非Map结构的，不添加
                        }
                    }
                }
                else if ( v_Item.getValue() instanceof Map )
                {
                    v_OrgJsonMap.putAll((Map<String ,Object>) v_Item.getValue());
                }
                else
                {
                    v_OrgJsonMap.put(v_Item.getKey() ,v_Item.getValue());
                }
            }
            else
            {
                if ( v_Item.getValue() == null )
                {
                    this.put(v_Item.getKey() ,v_Item.getValue());
                }
                else if ( v_Item.getValue() instanceof List )
                {
                    // 按理说应当没有同层结构中我是普通类型对你List的情况，如果真出现了就覆盖
                    List<MapJson> v_OrgValueList = new ArrayList<MapJson>();
                    this.put(v_Item.getKey() ,v_OrgValueList);
                    
                    List<Object> v_ItemValueList = (List<Object>) v_Item.getValue();
                    for (Object v_ItemChild : v_ItemValueList)
                    {
                        if ( v_ItemChild instanceof Map )
                        {
                            MapJson v_OrgJsonMap = new MapJson(this.splitValue ,this.indexLeft ,this.indexRight);
                            v_OrgJsonMap.putAll((Map<String ,Object>) v_ItemChild);
                            v_OrgValueList.add(v_OrgJsonMap);
                        }
                        else
                        {
                            // 非Map结构的，不添加
                        }
                    }
                }
                else if ( v_Item.getValue() instanceof Map )
                {
                    // 按理说应当没有同层结构中我是普通类型对你Map的情况，如果真出现了就覆盖
                    List<MapJson> v_OrgValueList = new ArrayList<MapJson>();
                    this.put(v_Item.getKey() ,v_OrgValueList);
                    
                    MapJson v_OrgJsonMap = new MapJson(this.splitValue ,this.indexLeft ,this.indexRight);
                    v_OrgJsonMap.putAll((Map<String ,Object>) v_Item.getValue());
                    
                    v_OrgValueList.add(v_OrgJsonMap);
                }
                else
                {
                    this.put(v_Item.getKey() ,v_Item.getValue());
                }
            }
        }
    }



    /**
     * 获取元素数值，支持
     *
     * @author      ZhengWei(HY)
     * @createDate  2024-01-09
     * @version     v1.0
     *
     * @param i_Key    支持数据结构层次的分隔符分层的模式
     * @return
     *
     * @see java.util.HashMap#get(java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    @Override
    public Object get(Object i_Key)
    {
        if ( i_Key == null )
        {
            throw new NullPointerException("Key is null.");
        }
        
        String [] v_Keys = this.parserKey(i_Key.toString());
        
        // 支持 keyA[indexA].keyB 格式
        if ( v_Keys[2] != null )
        {
            List<MapJson> v_Child = (List<MapJson>) super.get(v_Keys[0]);
            if ( Help.isNull(v_Child) )
            {
                return null;
            }
            
            MapJson v_ChildMapJson = null;
            if ( v_Keys[1] != null )
            {
                int v_KeyIndex = Integer.parseInt(v_Keys[1]);
                if ( v_KeyIndex < v_Child.size() )
                {
                    v_ChildMapJson = v_Child.get(v_KeyIndex);
                }
                else
                {
                    throw new IndexOutOfBoundsException(i_Key + " of " + v_Keys[0] + "[" + v_Keys[1] + "] is IndexOutOfBounds");
                }
            }
            else
            {
                v_ChildMapJson = v_Child.get(v_Child.size() - 1);
            }
            
            if ( v_ChildMapJson == null )
            {
                return null;
            }
            
            return v_ChildMapJson.get(v_Keys[2]);
        }
        // 支持 keyA[indexA] 格式
        else if ( v_Keys[1] != null )
        {
            List<MapJson> v_Child = (List<MapJson>) super.get(v_Keys[0]);
            if ( Help.isNull(v_Child) )
            {
                return null;
            }
            
            MapJson v_ChildMapJson = null;
            int v_KeyIndex = Integer.parseInt(v_Keys[1]);
            if ( v_KeyIndex < v_Child.size() )
            {
                v_ChildMapJson = v_Child.get(v_KeyIndex);
            }
            else
            {
                throw new IndexOutOfBoundsException(i_Key + " of " + v_Keys[0] + "[" + v_Keys[1] + "] is IndexOutOfBounds");
            }
            
            return v_ChildMapJson;
        }
        // 支持 keyA 格式
        else
        {
            return super.get(i_Key);
        }
    }



    /**
     * 判定关键字是否存在
     *
     * @author      ZhengWei(HY)
     * @createDate  2024-01-09
     * @version     v1.0
     *
     * @param i_Key    支持数据结构层次的分隔符分层的模式
     * @return
     *
     * @see java.util.HashMap#containsKey(java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean containsKey(Object i_Key)
    {
        if ( i_Key == null )
        {
            throw new NullPointerException("Key is null.");
        }
        
        String [] v_Keys = this.parserKey(i_Key.toString());
        
        // 支持 keyA[indexA].keyB 格式
        if ( v_Keys[2] != null )
        {
            List<MapJson> v_Child = (List<MapJson>) super.get(v_Keys[0]);
            if ( Help.isNull(v_Child) )
            {
                return false;
            }
            
            MapJson v_ChildMapJson = null;
            if ( v_Keys[1] != null )
            {
                int v_KeyIndex = Integer.parseInt(v_Keys[1]);
                if ( v_KeyIndex < v_Child.size() )
                {
                    v_ChildMapJson = v_Child.get(v_KeyIndex);
                }
                else
                {
                    return false;
                }
            }
            else
            {
                v_ChildMapJson = v_Child.get(v_Child.size() - 1);
            }
            
            if ( v_ChildMapJson == null )
            {
                return false;
            }
            
            return v_ChildMapJson.containsKey(v_Keys[2]);
        }
        // 支持 keyA[indexA] 格式
        else if ( v_Keys[1] != null )
        {
            List<MapJson> v_Child = (List<MapJson>) super.get(v_Keys[0]);
            if ( Help.isNull(v_Child) )
            {
                return false;
            }
            
            int v_KeyIndex = Integer.parseInt(v_Keys[1]);
            if ( v_KeyIndex < v_Child.size() )
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        // 支持 keyA 格式
        else
        {
            return super.containsKey(i_Key);
        }
    }



    /**
     * 删除元素
     *
     * @author      ZhengWei(HY)
     * @createDate  2024-01-09
     * @version     v1.0
     *
     * @param i_Key    支持数据结构层次的分隔符分层的模式
     * @return
     *
     * @see java.util.HashMap#remove(java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    @Override
    public Object remove(Object i_Key)
    {
        if ( i_Key == null )
        {
            throw new NullPointerException("Key is null.");
        }
        
        String [] v_Keys = this.parserKey(i_Key.toString());
        
        // 支持 keyA[indexA].keyB 格式
        if ( v_Keys[2] != null )
        {
            List<MapJson> v_Child = (List<MapJson>) super.get(v_Keys[0]);
            if ( Help.isNull(v_Child) )
            {
                return null;
            }
            
            MapJson v_ChildMapJson = null;
            if ( v_Keys[1] != null )
            {
                int v_KeyIndex = Integer.parseInt(v_Keys[1]);
                if ( v_KeyIndex < v_Child.size() )
                {
                    v_ChildMapJson = v_Child.get(v_KeyIndex);
                }
                else
                {
                    throw new IndexOutOfBoundsException(i_Key + " of " + v_Keys[0] + "[" + v_Keys[1] + "] is IndexOutOfBounds");
                }
            }
            else
            {
                v_ChildMapJson = v_Child.get(v_Child.size() - 1);
            }
            
            if ( v_ChildMapJson == null )
            {
                return null;
            }
            
            return v_ChildMapJson.remove(v_Keys[2]);
        }
        // 支持 keyA[indexA] 格式
        else if ( v_Keys[1] != null )
        {
            List<MapJson> v_Child = (List<MapJson>) super.get(v_Keys[0]);
            if ( Help.isNull(v_Child) )
            {
                return null;
            }
            
            int v_KeyIndex = Integer.parseInt(v_Keys[1]);
            if ( v_KeyIndex < v_Child.size() )
            {
                return v_Child.remove(v_KeyIndex);
            }
            else
            {
                throw new IndexOutOfBoundsException(i_Key + " of " + v_Keys[0] + "[" + v_Keys[1] + "] is IndexOutOfBounds");
            }
        }
        // 支持 keyA 格式
        else
        {
            return super.remove(i_Key);
        }
    }



    /**
     * 清理所有层次结构中的所有数据
     *
     * @author      ZhengWei(HY)
     * @createDate  2024-01-09
     * @version     v1.0
     *
     * @see java.util.HashMap#clear()
     */
    @SuppressWarnings("unchecked")
    @Override
    public void clear()
    {
        Iterator<Object> v_Iter = super.values().iterator();
        
        while ( v_Iter.hasNext() )
        {
            Object v_Item = v_Iter.next();
            if ( v_Item == null )
            {
                continue;
            }
            else if ( v_Item instanceof List )
            {
                List<MapJson> v_Child = (List<MapJson>) v_Item;
                for (MapJson v_ChildMapJson : v_Child)
                {
                    v_ChildMapJson.clear();
                }
                v_Child.clear();
            }
        }
        
        super.clear();
    }
    
}
