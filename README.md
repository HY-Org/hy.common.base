# hy.common.base



<ul>
    <li><p>[Help 常用操作](tree/master/src/org/hy/common/Help.java)</p></li>
    <li><p>[StringHelp 字符串操作](src/org/hy/common/StringHelp.java)</p></li>
    <li><p>[ByteHelp 字节操作](src/org/hy/common/ByteHelp.java)</p></li>
    <li><p>[Date 时间操作](src/org/hy/common/Date.java)</p></li>
    <li><p>集合类</p></li>
    <ul>
        <li><p>[CycleList 轮转、周而复始的循环](src/org/hy/common/CycleList.java)</p></li>
        <li><p>[ExpireMap 有生存时间的Map，当生存时间期满时，Map.key就失效了](src/org/hy/common/ExpireMap.java)</p></li>
        <li><p>[InterconnectMap 互联互通Map。即可以像普通Map一样用Key找到Value。也可以用Value找到指向它的所有Key对象。](src/org/hy/common/InterconnectMap.java)</p></li>
        <li><p>[ListMap Map与List的融合体。即有关键字找值的功能，又有按编号找值](src/org/hy/common/ListMap.java)</p></li>
        <li><p>[OrderByList 排序集合](src/org/hy/common/OrderByList.java)</p></li>
        <li><p>统计类的集合</p></li>
        <ul>
            <li><p>[Counter 计数器](src/org/hy/common/Counter.java)</p></li>
            <li><p>[CounterMap 计数器Map -- 互联互通Map的子类](src/org/hy/common/CounterMap.java)</p></li>
            <li><p>[Max 最大值统计及分类最大值统计](src/org/hy/common/Max.java)</p></li>
            <li><p>[Min 最小值统计及分类最小值统计](src/org/hy/common/Min.java)</p></li>
            <li><p>[Sum 合计器](src/org/hy/common/Sum.java)</p></li>
        </ul>
        <li><p>分区类的集合</p></li>
        <ul>
            <li><p>[PartitionMap 表分区接口](src/org/hy/common/PartitionMap.java)</p></li>
            <li><p>[TablePartition 仅数据区有序的表分区](src/org/hy/common/TablePartition.java)</p></li>
            <li><p>[TablePartitionLink 分区与数据区均有序的表分区](src/org/hy/common/TablePartitionLink.java)</p></li>
            <li><p>[TablePartitionSet 分区与数据区均无序的表分区](src/org/hy/common/TablePartitionSet.java)</p></li>
            <li><p>[TablePartitionRID 有索引的的表分区](src/org/hy/common/TablePartitionRID.java)</p></li>
        </ul>
        <li><p>非继承集合接口的特殊集合</p></li>
        <ul>
            <li><p>[Queue 队列集合](src/org/hy/common/Queue.java)</p></li>
            <li><p>[Busway 公交车专用通道](src/org/hy/common/Busway.java)</p></li>
            <li><p>[TreeMap 树结构的集合](src/org/hy/common/TreeMap.java)</p></li>
            <li><p>[Timing 计时器](src/org/hy/common/Timing.java)</p></li>
            <li><p>[TimingTotal 计时器的横向统计类](src/org/hy/common/TimingTotal.java)</p></li>
        </ul>
    </ul>
</ul>