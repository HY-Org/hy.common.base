package org.hy.common.junit;

import org.hy.common.MapMap;
import org.junit.Test;





public class JU_MapMap
{
    
    @Test
    public void test_MapMap()
    {
        MapMap v_Map = new MapMap();
        
        v_Map.put("中国.陕西.西安" ,"XianAn");
        v_Map.put("中国.陕西.咸阳" ,"XianYang");
        v_Map.put("中国.陕西.宝鸡" ,"BaoJi");
    }
    
}
