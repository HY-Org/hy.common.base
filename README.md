# hy.common.base



* [Help 常用操作](src/org/hy/common/Help.java)
* [StringHelp 字符串操作](src/org/hy/common/StringHelp.java)
* [ByteHelp 字节操作](src/org/hy/common/ByteHelp.java)
* [Date 时间操作](src/org/hy/common/Date.java)
* [Execute 线程反射方式执行动作](src/org/hy/common/Execute.java)
* [Serializable 有扩展的序列化接口](src/org/hy/common/Serializable.java)
* [Return 使方法能返回多个值的通过对象](src/org/hy/common/Return.java)
* 反射
	* [MethodReflect 方法的反射](src/org/hy/common/MethodReflect.java)
	* [FieldReflect 成员属性的反射](src/org/hy/common/FieldReflect.java)
	* [ClassReflect 类的反射](src/org/hy/common/ClassReflect.java)
	* [StaticReflect 静态字段(成员属性)的反射](src/org/hy/common/StaticReflect.java)
* 比较器
	* [MethodComparator "方法"类的排序比较器](src/org/hy/common/MethodComparator.java)
	* [ObjectComparator 对象多维的排序比较器](src/org/hy/common/ObjectComparator.java)
	* [SerializableComparator 序列化对象多维的排序比较器](src/org/hy/common/SerializableComparator.java)
* 集合类
    * [CycleList 轮转、周而复始的循环](src/org/hy/common/CycleList.java)
    * [ExpireMap 有生存时间的Map，当生存时间期满时，Map.key就失效了](src/org/hy/common/ExpireMap.java)
    * [InterconnectMap 互联互通Map。可像普通Map一样用Key找到Value。也可用Value找到指向它的所有Key](src/org/hy/common/InterconnectMap.java)
    * [ListMap Map与List的融合体。即有关键字找值的功能，又有按编号找值](src/org/hy/common/ListMap.java)
    * [OrderByList 排序集合](src/org/hy/common/OrderByList.java)
    * [RelationList 表达式关系集合](src/org/hy/common/RelationList.java)
	* 统计类的集合
	    * [Counter 计数器](src/org/hy/common/Counter.java)
	    * [CounterMap 计数器Map -- 互联互通Map的子类](src/org/hy/common/CounterMap.java)
	    * [Max 最大值统计及分类最大值统计](src/org/hy/common/Max.java)
	    * [Min 最小值统计及分类最小值统计](src/org/hy/common/Min.java)
	    * [Sum 合计器](src/org/hy/common/Sum.java)
	* 分区类的集合
	    * [PartitionMap 表分区接口](src/org/hy/common/PartitionMap.java)
	    * [TablePartition 仅数据区有序的表分区](src/org/hy/common/TablePartition.java)
	    * [TablePartitionLink 分区与数据区均有序的表分区](src/org/hy/common/TablePartitionLink.java)
	    * [TablePartitionSet 分区与数据区均无序的表分区](src/org/hy/common/TablePartitionSet.java)
	    * [TablePartitionRID 有索引的的表分区](src/org/hy/common/TablePartitionRID.java)
	* 特殊集合(未继承集合接口)
	    * [Queue 队列集合](src/org/hy/common/Queue.java)
	    * [Busway 公交车专用通道](src/org/hy/common/Busway.java)
	    * [TreeMap 树结构的集合](src/org/hy/common/TreeMap.java)
	    * [Timing 计时器](src/org/hy/common/Timing.java)
	    * [TimingTotal 计时器的横向统计类](src/org/hy/common/TimingTotal.java)
		* [I18N 国际化信息的集合](src/org/hy/common/I18N.java)
		* [Holidays 假节日信息的集合](src/org/hy/common/Holidays.java)
