package org.hy.common.junit;

import java.util.List;

import org.hy.common.Help;
import org.hy.common.MapJson;
import org.junit.Test;





/**
 * 测试单元：类似Json层次结构的Map
 *
 * @author      ZhengWei(HY)
 * @createDate  2024-01-09
 * @version     v1.0
 */
public class JU_MapJson
{
    
    /**
     * 前向添加数据
     * 
     * @author      ZhengWei(HY)
     * @createDate  2024-01-09
     * @version     v1.0
     *
     */
    @SuppressWarnings("unchecked")
    @Test
    public void test_MapJson()
    {
        MapJson v_MapJson = new MapJson();
        
        // 前向添加数据：有层次顺序的添加
        v_MapJson.put("name"                            ,"中国");
        v_MapJson.put("childs.name"                     ,"陕西");
        v_MapJson.put("childs.childs.name"              ,"西安");
        v_MapJson.put("childs.childs.name"              ,"咸阳");
                                                        
        v_MapJson.put("childs.childs.childs.name"       ,"渭城区");
        v_MapJson.put("childs.childs.childs.name"       ,"秦都区");
                                                        
        v_MapJson.put("childs.name"                     ,"云南");
        v_MapJson.put("childs.childs.name"              ,"昆明");
        v_MapJson.put("childs.childs.name"              ,"大理");
        
        // 后向添加数据
        v_MapJson.put("childs[0].childs.name"           ,"宝鸡");
        v_MapJson.put("childs[0].childs[0].childs.name" ,"雁塔区");
        
        Help.print(v_MapJson);
        
        // 读取陕西
        Help.print((MapJson) v_MapJson.get("childs[0]"));
        
        // 读取陕西的所有地市列表
        Help.print((List<MapJson>) v_MapJson.get("childs[0].childs"));
        
        // 读取咸阳名称
        System.out.println(v_MapJson.get("childs[0].childs[1].name"));
        
        // 移除西安（及子层），注西安及下面的Map内存没有释放
        MapJson v_XiAn = (MapJson) v_MapJson.remove("childs[0].childs[0]");
        Help.print(v_MapJson);
        Help.print(v_XiAn);
        
        v_MapJson.clear();
    }
    
}
