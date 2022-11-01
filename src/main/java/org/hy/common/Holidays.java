package org.hy.common;

import java.util.ArrayList;
import java.util.List;





/**
 * 假节日集合
 * 
 * @author  ZhengWei(HY)
 * @version 2013-06-14
 */
public final class Holidays
{

    /** 
     * 只存放正常的公历信息，不存放特殊公历和农历信息。
     * 分区格式为: 日期的MMDD
     */
    private final static PartitionMap<String ,Holiday> $HDCalc  = new TablePartition<String ,Holiday>();
    
    /** 
     * 只存放农历信息
     * 分区格式为: 日期的MMDD
     */
    private final static PartitionMap<String ,Holiday> $HDLunar = new TablePartition<String ,Holiday>(); 
    
    /** 
     * 只存放特殊公历信息
     * 分区格式为: 日期的MMWW 
     */
    private final static PartitionMap<String ,Holiday> $HDElse  = new TablePartition<String ,Holiday>(); 
    
    
    
    static
    {
        add( 1 , 1 ,true  ,"新年元旦(New Year's Day)" ,"元旦" ,100);
        add( 1 ,26 ,false ,"国际海关节");
        add( 1 ,27 ,false ,"国际大屠杀纪念日");
        add( 2 , 2 ,false ,"世界湿地日(World Wetland Day)");
        add( 2 , 7 ,false ,"国际声援南非日");
        add( 2 ,10 ,false ,"国际气象节");
        add( 2 ,14 ,false ,"情人节(Valentine's Day)" ,"情人节" ,100);
        add( 2 ,21 ,false ,"国际母语日(International Mother Language Day)");
        add( 2 ,21 ,false ,"反对殖民制度斗争日");
        add( 2 ,24 ,false ,"第三世界青年日");
        add( 2 ,28 ,false ,"世界居住条件调查日");
        add( 3 , 1 ,false ,"国际海豹日");
        add( 3 , 3 ,false ,"全国爱耳日");
        add( 3 , 5 ,false ,"青年志愿者服务日");
        add( 3 , 8 ,true  ,"联合国妇女权益和国际和平日(U.N. Day for Women's Rights and International Peace)" ,"妇女节" ,100);
        add( 3 , 9 ,false ,"保护母亲河日");
        add( 3 ,12 ,false ,"中国植树节(China Arbor Day)" ,"植树节");
        add( 3 ,12 ,false ,"孙中山逝世纪念日");
        add( 3 ,14 ,false ,"国际警察日(International Policemen' Day)");
        add( 3 ,14 ,false ,"白色情人节(White Day)");
        add( 3 ,15 ,false ,"国际消费者权益日");
        add( 3 ,16 ,false ,"手拉手情系贫困小伙伴全国统一行动日");
        add( 3 ,17 ,false ,"国际航海日");
        add( 3 ,17 ,false ,"中国国医节");
        add( 3 ,21 ,false ,"国际消除种族歧视日");
        add( 3 ,21 ,false ,"世界睡眠日(World Sleep Day)");
        add( 3 ,21 ,false ,"世界儿歌日");
        add( 3 ,21 ,false ,"世界森林日(World Forest Day)");
        add( 3 ,22 ,false ,"世界水日(World Water Day)");
        add( 3 ,23 ,false ,"世界气象日(World Meteorological Day)");
        add( 3 ,24 ,false ,"世界防治结核病日");
        add( 3 ,27 ,false ,"世界戏剧日");
        add( 3 ,30 ,false ,"巴勒斯坦国土日");
        add( 4 ,01 ,false ,"愚人节(April Fools' Day)" ,"愚人节" ,30);
        add( 4 ,02 ,false ,"国际儿童图书日(International Children's Book Day)");
        add( 4 ,05 ,true  ,"清明节(Tomb-sweeping Day)" ,"清明" ,30);
        add( 4 ,07 ,false ,"世界卫生日(World Health Day)"); 
        add( 4 ,10 ,false ,"非洲环境保护日");
        add( 4 ,11 ,false ,"世界帕金森病日");
        add( 4 ,15 ,false ,"非洲自由日");
        add( 4 ,18 ,false ,"国际古迹遗址日");
        add( 4 ,21 ,false ,"全国企业家活动日");
        add( 4 ,22 ,false ,"世界地球日(World Earth Day)");
        add( 4 ,23 ,false ,"世界读书日。又称'世界图书与版权日'(World Book and Copyright Day)" ,"读书日");
        add( 4 ,24 ,false ,"世界青年反对殖民主义日");
        add( 4 ,24 ,false ,"亚非新闻工作者日");
        add( 4 ,26 ,false ,"世界知识产权日(World Intellectual Property Day)");
        add( 5 , 1 ,true  ,"国际劳动节(International Labour Day)" ,"劳动节" ,100);
        add( 5 , 1 ,false ,"国际示威游行日");
        add( 5 , 3 ,false ,"世界新闻自由日(World Press Freedom Day)");
        add( 5 , 3 ,false ,"太阳日(Sun Day)");
        add( 5 , 4 ,false ,"中国青年节(Chinese Youth Day)" ,"青年节");
        add( 5 , 4 ,false ,"五四运动纪念日");
        add( 5 , 8 ,false ,"世界红十字日(World Red Cross Day)");
        add( 5 , 8 ,false ,"世界微笑日");
        add( 5 , 9 ,false ,"战胜德国法西斯纪念日");
        add( 5 ,12 ,false ,"国际护士节(International Nurses' Day)" ,"护士节");
        add( 5 ,13 ,false ,"世界高血压日");
        add( 5 ,15 ,false ,"国际家庭日(International Day of Families)");
        add( 5 ,17 ,false ,"世界电信日(World Telecommunication Day)" ,"电信日");
        add( 5 ,18 ,false ,"国际博物馆日(International Museum's Day)");
        add( 5 ,19 ,false ,"全国助残日");
        add( 5 ,20 ,false ,"世界计量日");
        add( 5 ,20 ,false ,"全国学生营养日");
        add( 5 ,20 ,false ,"全国母乳喂养宣传日");
        add( 5 ,21 ,false ,"世界文化发展日(World Day for Cultural Development)");
        add( 5 ,22 ,false ,"国际生物多样性日(International Biodiversity Day)");
        add( 5 ,25 ,false ,"非洲解放日 (African Liberation Day)"); 
        add( 5 ,26 ,false ,"世界向人体条件挑战日");
        add( 5 ,31 ,false ,"世界无烟日(World No-Tobacco Day)");
        add( 6 ,01 ,true  ,"国际儿童节(International Children's Day)" ,"儿童节" ,100);
        add( 6 ,04 ,false ,"受侵略戕害无辜儿童国际日(International Day of Innocent Children Victims of Aggression)");
        add( 6 ,05 ,false ,"世界环境日(World Environment Day");
        add( 6 ,06 ,false ,"全国爱眼日");
        add( 6 ,11 ,false ,"中国人口日");
        add( 6 ,12 ,false ,"世界无童工日(World Day against Child Labor)");
        add( 6 ,14 ,false ,"世界献血者日");
        add( 6 ,16 ,false ,"国际非洲儿童日");
        add( 6 ,17 ,false ,"世界防治荒漠化和干旱日(World Day to combat desertification)");
        add( 6 ,20 ,false ,"世界难民日(World Refugee Day)");
        add( 6 ,22 ,false ,"中国儿童慈善活动日");
        add( 6 ,23 ,false ,"国际奥林匹克日(International Olympic Day)");
        add( 6 ,23 ,false ,"世界手球日");
        add( 6 ,25 ,false ,"全国土地日");
        add( 6 ,26 ,false ,"联合国宪章日");
        add( 6 ,26 ,false ,"国际禁毒日(International Day Against Drug Abuse and Illicit Trafficking)");
        add( 6 ,30 ,false ,"世界青年联欢节");
        add( 7 ,01 ,false ,"中国共产党诞生纪念日" ,"建党");
        add( 7 ,01 ,false ,"世界建筑日(International Architecture Day)");
        add( 7 ,01 ,false ,"香港回归日");
        add( 7 ,02 ,false ,"国际体育记者日");
        add( 7 ,07 ,false ,"中国人民抗日战争纪念日");
        add( 7 ,11 ,false ,"世界人口日(World Population Day)");
        add( 7 ,11 ,false ,"中国航海日(郑和下西洋纪念日)");
        add( 7 ,20 ,false ,"人类首次成功登月纪念日");
        add( 7 ,26 ,false ,"世界语(言)创立日");
        add( 7 ,28 ,false ,"第一次世界大战爆发");
        add( 7 ,30 ,false ,"非洲妇女日");
        add( 8 , 1 ,false ,"中国人民解放军建军节(Army Day)" ,"建军");
        add( 8 , 6 ,false ,"国际电影节");
        add( 8 , 8 ,false ,"中国男子节(爸爸节)");
        add( 8 , 9 ,false ,"国际土著人日");
        add( 8 ,12 ,false ,"国际青年日(International Youth Day)");
        add( 8 ,15 ,false ,"日本正式宣布无条件投降日");
        add( 8 ,26 ,false ,"纳米比亚日");
        add( 9 , 3 ,false ,"中国抗日战争胜利纪念日");
        add( 9 , 8 ,false ,"国际扫盲日(International Anti-Illiteracy Day)");
        add( 9 , 8 ,false ,"国际新闻工作者(团结)日" ,"新闻" ,30);
        add( 9 ,10 ,true  ,"教师节(Teacher's Day)" ,"教师节" ,100);
        add( 9 ,10 ,false ,"世界预防自杀日");
        add( 9 ,10 ,false ,"中国脑健康日");
        add( 9 ,14 ,false ,"世界清洁地球日");
        add( 9 ,16 ,false ,"国际臭氧层保护日(International Day for the Preservation of the Ozone Layer)");
        add( 9 ,18 ,false ,"九·一八事变纪念日(中国国耻日)");
        add( 9 ,20 ,false ,"国际爱牙日");
        add( 9 ,21 ,false ,"国际和平日(International Day of Peace)");
        add( 9 ,26 ,false ,"(曲阜国际)孔子文化节");
        add( 9 ,27 ,false ,"世界旅游日(World Tourism Day)");
        add( 9 ,30 ,false ,"国际翻译日");
        add(10 , 1 ,true  ,"中华人民共和国国庆节(National Day)" ,"国庆" ,100);
        add(10 , 1 ,false ,"国际音乐日(International Music Day)");
        add(10 , 1 ,false ,"国际老年人日(International Day of Older Persons)"); 
        add(10 , 2 ,false ,"国际和平(与民主自由)斗争日");
        add(10 , 4 ,false ,"世界动物日(World Animal Day)"); 
        add(10 , 5 ,false ,"世界教师日(World Teachers' Day)");
        add(10 , 8 ,false ,"全国高血压日");
        add(10 , 8 ,false ,"世界视觉日");
        add(10 , 8 ,false ,"国际左撇子日");
        add(10 , 9 ,false ,"世界邮政日(万国邮联日)(World Post Day)");
        add(10 ,10 ,false ,"世界精神卫生日(World Mental Health Day)");
        add(10 ,10 ,false ,"世界居室卫生日");
        add(10 ,10 ,false ,"世界视力日(World Sight Day)");
        add(10 ,10 ,false ,"辛亥革命纪念日");
        add(10 ,11 ,false ,"世界镇痛日");
        add(10 ,12 ,false ,"世界60亿人口日");
        add(10 ,13 ,false ,"国际标准时间日(World Standards Day)");
        add(10 ,13 ,false ,"中国少年先锋队诞辰日");
        add(10 ,14 ,false ,"世界标准日(World Standards Day)");
        add(10 ,15 ,false ,"世界农村妇女日(World Rural Women's Day)");
        add(10 ,15 ,false ,"国际盲人节(白手杖节)(International Day of the Blind)");
        add(10 ,16 ,false ,"世界粮食日(World Food Day)");
        add(10 ,17 ,false ,"国际消除贫困日(International Day for the Eradication of Poverty)");
        add(10 ,18 ,false ,"世界传统医药日(World Traditional Medicine Day)");
        add(10 ,20 ,false ,"世界厨师日");
        add(10 ,20 ,false ,"世界骨质疏松日");
        add(10 ,22 ,false ,"世界传统医药日");
        add(10 ,24 ,false ,"联合国日(United Nations Day)");
        add(10 ,24 ,false ,"世界发展新闻日(World Development Information Day)");
        add(10 ,28 ,false ,"世界男性健康日");
        add(10 ,31 ,false ,"万圣节(Halloween)，又称西方的鬼节" ,"万圣节");
        add(10 ,31 ,false ,"世界勤俭日");
        add(11 , 7 ,false ,"十月社会主义革命纪念日(现俄'和谐和解日')");
        add(11 , 8 ,false ,"中国记者节");
        add(11 , 9 ,false ,"中国消防宣传日");
        add(11 ,10 ,false ,"世界青年节");
        add(11 ,11 ,false ,"光棍节(希望您幸福 ^_^)" ,"光棍节");
        add(11 ,12 ,false ,"孙中山诞辰纪念日");
        add(11 ,14 ,false ,"世界糖尿病日(World Diabetes Day)");
        add(11 ,16 ,false ,"国际宽容日(International Day of Tolerance)");
        add(11 ,17 ,false ,"国际大学生节");
        add(11 ,19 ,false ,"世界厕所日");
        add(11 ,20 ,false ,"世界儿童日(Universal Children's Day)");
        add(11 ,21 ,false ,"世界电视日(World Television Day)");
        add(11 ,21 ,false ,"世界问候日");
        add(11 ,25 ,false ,"国际消除针对妇女暴力日");
        add(11 ,29 ,false ,"声援巴勒斯坦人民国际日");
        add(12 , 1 ,false ,"世界艾滋病日(World Aids Day)");
        add(12 , 2 ,false ,"国际废除奴隶制日(International Day for the Abolition of Slavery)");
        add(12 , 3 ,false ,"国际残疾人日(International Day of Disabled Persons)");
        add(12 , 4 ,false ,"中国法制宣传日");
        add(12 , 5 ,false ,"国际志愿人员日(International Volunteer Day for Economic and Social Development)");
        add(12 , 5 ,false ,"世界弱能人士日");
        add(12 , 7 ,false ,"国际民航日(International Civil Aviation Day)");
        add(12 , 9 ,false ,"世界足球日(World Football Day)");
        add(12 , 9 ,false ,"国际反腐败日");
        add(12 , 9 ,false ,"一二·九运动纪念日");
        add(12 ,10 ,false ,"世界人权日(Human Rights Day)");
        add(12 ,12 ,false ,"西安事变纪念日");
        add(12 ,12 ,false ,"国际儿童广播日(International Children's Day of Broadcasting)");
        add(12 ,13 ,false ,"南京大屠杀(1937年)纪念日！紧记血泪史！");
        add(12 ,15 ,false ,"世界强化免疫日");
        add(12 ,18 ,false ,"国际移徙者日(International Migrants Day)");
        add(12 ,19 ,false ,"南南合作日");
        add(12 ,20 ,false ,"澳门回归纪念日");
        add(12 ,21 ,false ,"国际篮球日");
        add(12 ,24 ,false ,"平安夜" ,"平安夜" ,100);
        add(12 ,25 ,false ,"圣诞节(Christmas Day)" ,"圣诞" ,100);
        
        
        // 特殊公历节日（按某月第几个星期的星期几的节日）
        add( 1  , 1 ,7 ,false ,"国际黑人日");
        add( 1  ,-1 ,7 ,false ,"世界防治麻疯病日");
        add( 4  , 4 ,7 ,false ,"世界儿童日(World Children's Day) ");
        add( 4  ,-1 ,3 ,false ,"秘书节");
        add( 5  , 1 ,2 ,false ,"世界哮喘日(World Asthma Day)");
        add( 5  , 2 ,7 ,false ,"母亲节(Mother's Day)" ,"母亲节" ,100);
        add( 5  , 3 ,2 ,false ,"国际牛奶日(International Milk Day)");
        add( 5  , 3 ,7 ,false ,"中国助残日");
        add( 6  , 3 ,7 ,false ,"父亲节(Father's Day)" ,"父亲节" ,100);
        add( 7  , 1 ,6 ,false ,"国际合作社日(International Day of Cooperatives)");
        add( 9  , 4 ,7 ,false ,"国际聋人节");
        add( 9  ,-1 ,7 ,false ,"世界心脏日");
        add(10  , 1 ,1 ,false ,"世界人居日(世界住房日)(World Habitat Day)");
        add(10  , 1 ,3 ,false ,"国际减轻自然灾害日(减灾日)");
        add(10  , 2 ,1 ,false ,"加拿大感恩节(Thanksgiving Day)");
        add(10  , 2 ,3 ,false ,"国际减灾日(International Day for Natural Disaster Reduction)");
        add(11  ,-1 ,4 ,false ,"美国感恩节(Thanksgiving Day)" ,"感恩节");
        
        
        // 农历节日
        addLunar( 1 , 1 ,true  ,"春节(The Spring Festival)" ,"春节" ,100);
        addLunar( 1 ,15 ,true  ,"元宵节(Lantern Festival)" ,"元宵" ,100);
        addLunar( 1 ,15 ,false ,"壮族歌墟节");
        addLunar( 1 ,15 ,false ,"朝鲜族上元节");
        addLunar( 1 ,15 ,false ,"苗族踩山节");
        addLunar( 1 ,15 ,false ,"达翰尔族卡钦");
        addLunar( 1 ,25 ,false ,"填仓节" ,"填仓节");
        addLunar( 1 ,29 ,false ,"送穷日" ,"送穷日");
        addLunar( 2 , 1 ,false ,"瑶族忌鸟节");
        addLunar( 2 , 2 ,false ,"春龙节(龙抬头节)" ,"龙抬头" ,30);
        addLunar( 2 , 2 ,false ,"畲族会亲节");
        addLunar( 3 ,15 ,false ,"傈傈族刀杆节");
        addLunar( 3 ,15 ,false ,"白族三月街(三月十五至二十五)");
        addLunar( 3 ,23 ,false ,"妈祖生辰(天上圣母诞辰)");
        addLunar( 4 , 8 ,false ,"牛王诞");
        addLunar( 4 ,18 ,false ,"锡伯族西迁节");
        addLunar( 5 , 5 ,true  ,"端午节(the Dragon-Boat Festival)" ,"端午" ,100);
        addLunar( 5 , 5 ,false ,"黎族朝花节");
        addLunar( 5 , 5 ,false ,"苗族龙船年");
        addLunar( 5 ,13 ,false ,"阿昌族泼水节");
        addLunar( 5 ,29 ,false ,"瑶族达努节");
        addLunar( 5 ,22 ,false ,"鄂温克族米阔鲁节");
        addLunar( 6 , 6 ,false ,"姑姑节");
        addLunar( 6 , 6 ,false ,"天贶节");
        addLunar( 6 , 6 ,false ,"壮族祭田节");
        addLunar( 6 , 6 ,false ,"瑶族尝新节");
        addLunar( 6 ,24 ,false ,"彝族 阿昌族 白族 佤族 纳西族 基诺族火把节");
        addLunar( 7 , 7 ,false ,"七七中国情人节(女儿节, 乞巧节)(Double-Seventh Day)" ,"七夕" ,100);
        addLunar( 7 ,13 ,false ,"侗族吃新节");
        addLunar( 7 ,15 ,false ,"中元节");
        addLunar( 7 ,15 ,false ,"盂兰盆会");
        addLunar( 7 ,15 ,false ,"普米族转山会");
        addLunar( 8 ,15 ,true  ,"中秋节(the Mid-Autumn Festival)" ,"中秋" ,100);
        addLunar( 8 ,15 ,false ,"拉祜族尝新节");
        addLunar( 9 , 9 ,false ,"重阳节(the Double Ninth Festival)" ,"重阳" ,30);
        addLunar( 9 , 9 ,false ,"中国老年节(义务助老活动日)");
        addLunar(10 , 1 ,false ,"祭祖节(十月朝)");
        addLunar(10 ,16 ,false ,"瑶族盘王节");
        addLunar(12 , 8 ,false ,"腊八节(the laba Rice Porridge Festival)" ,"腊八" ,30);
        addLunar(12 ,23 ,false ,"北方灶君节[北方小年(扫房日)]" ,"扫房日");
        addLunar(12 ,24 ,false ,"南方祭灶节[南方小年(掸尘日)]");
    }
    
    
    
    private Holidays()
    {
        
    }
    
    
    
    public static void addLunar(int i_Month ,int i_Day ,boolean i_IsRect ,String i_HolidayInfo)
    {
        add(new Holiday(i_Month ,i_Day ,i_IsRect ,i_HolidayInfo ,"" ,true));
    }
    
    
    
    public static void addLunar(int i_Month ,int i_Day ,boolean i_IsRect ,String i_HolidayInfo ,int i_ShowLevel)
    {
        add(new Holiday(i_Month ,i_Day ,i_IsRect ,i_HolidayInfo ,"" ,true).setShowLevel(i_ShowLevel));
    }
    
    
    
    public static void addLunar(int i_Month ,int i_Day ,boolean i_IsRect ,String i_HolidayInfo ,String i_ShortInfo)
    {
        add(new Holiday(i_Month ,i_Day ,i_IsRect ,i_HolidayInfo ,i_ShortInfo ,true));
    }
    
    
    
    public static void addLunar(int i_Month ,int i_Day ,boolean i_IsRect ,String i_HolidayInfo ,String i_ShortInfo ,int i_ShowLevel)
    {
        add(new Holiday(i_Month ,i_Day ,i_IsRect ,i_HolidayInfo ,i_ShortInfo ,true).setShowLevel(i_ShowLevel));
    }
    
    
    
    public static void add(int i_Month ,int i_Day ,boolean i_IsRect ,String i_HolidayInfo ,String i_ShortInfo)
    {
        add(new Holiday(i_Month ,i_Day ,i_IsRect ,i_HolidayInfo ,i_ShortInfo));
    }
    
    
    
    public static void add(int i_Month ,int i_Day ,boolean i_IsRect ,String i_HolidayInfo ,String i_ShortInfo ,int i_ShowLevel)
    {
        add(new Holiday(i_Month ,i_Day ,i_IsRect ,i_HolidayInfo ,i_ShortInfo).setShowLevel(i_ShowLevel));
    }
    
    
    
    public static void add(int i_Month ,int i_WeekNum ,int i_WeekNo ,boolean i_IsRect ,String i_HolidayInfo ,String i_ShortInfo)
    {
        add(new Holiday(i_Month ,i_WeekNum ,i_WeekNo ,i_IsRect ,i_HolidayInfo ,i_ShortInfo));
    }
    
    
    
    public static void add(int i_Month ,int i_WeekNum ,int i_WeekNo ,boolean i_IsRect ,String i_HolidayInfo ,String i_ShortInfo ,int i_ShowLevel)
    {
        add(new Holiday(i_Month ,i_WeekNum ,i_WeekNo ,i_IsRect ,i_HolidayInfo ,i_ShortInfo).setShowLevel(i_ShowLevel));
    }
    
    
    
    public static void add(int i_Month ,int i_Day ,boolean i_IsRect ,String i_HolidayInfo)
    {
        add(new Holiday(i_Month ,i_Day ,i_IsRect ,i_HolidayInfo));
    }
    
    
    
    public static void add(int i_Month ,int i_Day ,boolean i_IsRect ,String i_HolidayInfo ,int i_ShowLevel)
    {
        add(new Holiday(i_Month ,i_Day ,i_IsRect ,i_HolidayInfo).setShowLevel(i_ShowLevel));
    }
    
    
    
    public static void add(int i_Month ,int i_WeekNum ,int i_WeekNo ,boolean i_IsRect ,String i_HolidayInfo)
    {
        add(new Holiday(i_Month ,i_WeekNum ,i_WeekNo ,i_IsRect ,i_HolidayInfo));
    }
    
    
    
    public static void add(int i_Month ,int i_WeekNum ,int i_WeekNo ,boolean i_IsRect ,String i_HolidayInfo ,int i_ShowLevel)
    {
        add(new Holiday(i_Month ,i_WeekNum ,i_WeekNo ,i_IsRect ,i_HolidayInfo).setShowLevel(i_ShowLevel));
    }
    
    
    
    public static void add(Holiday i_Holiday)
    {
        if ( i_Holiday.isNormal() && !i_Holiday.isLunar() )
        {
            $HDCalc.putRow(i_Holiday.getMonthDay()    ,i_Holiday);
        }
        
        if ( i_Holiday.isLunar() )
        {
            $HDLunar.putRow(i_Holiday.getMonthDay()   ,i_Holiday);
        }
        else
        {
            $HDElse.putRow(i_Holiday.getMonthWeekNo() ,i_Holiday);
        }
    }
    
    
    
    /**
     * 获取给定日期的所有节日信息
     * 
     * @param i_Date
     * @return
     */
    public static List<Holiday> getHolidays(Date i_Date)
    {
        List<Holiday> v_Result  = new ArrayList<Holiday>();
        List<Holiday> v_TabPart = null;
        Lunar         v_Lunar   = i_Date.getLunar();
        String        v_MMDD    = i_Date.getMD_ID();
        String        v_MMWW    = StringHelp.lpad(i_Date.getMonth() ,2 ,"0") + StringHelp.lpad(i_Date.getWeek() ,2 ,"0");
        

        // 快速判断正常公历节日
        v_TabPart = $HDCalc.get(v_MMDD);
        if ( !Help.isNull(v_TabPart) )
        {
            v_Result.addAll(v_TabPart);
        }
        
        
        // 快速判断特殊公历信息
        v_TabPart = $HDElse.get(v_MMWW);
        if ( !Help.isNull(v_TabPart) )
        {
            v_Result.addAll(v_TabPart);
        }
        
        
        // 快速判断农历信息
        v_MMDD    = StringHelp.lpad(v_Lunar.getMonth() ,2 ,"0") + StringHelp.lpad(v_Lunar.getDay() ,2 ,"0");
        v_TabPart = $HDLunar.get(v_MMDD);
        if ( !Help.isNull(v_TabPart) )
        {
            v_Result.addAll(v_TabPart);
        }
        
        return v_Result;
    }
    
    
    
    /**
     * 获取给定日期的有简称的，并且显示级别最高的那个节日信息
     * 
     * @param i_Date
     * @return
     */
    public static Holiday getHolidayTopLevel(Date i_Date)
    {
        Holiday       v_Holiday = null;
        List<Holiday> v_TabPart = null;
        Lunar         v_Lunar   = i_Date.getLunar();
        String        v_MMDD    = i_Date.getMD_ID();
        String        v_MMWW    = StringHelp.lpad(i_Date.getMonth() ,2 ,"0") + StringHelp.lpad(i_Date.getWeek() ,2 ,"0");
        

        // 快速判断正常公历节日
        v_TabPart = $HDCalc.get(v_MMDD);
        if ( !Help.isNull(v_TabPart) )
        {
            for (int i=0; i<v_TabPart.size(); i++)
            {
                Holiday v_Temp = v_TabPart.get(i);
                
                if ( v_Temp.isHoliday(i_Date) )
                {
                    if ( !Help.isNull(v_Temp.getShortInfo()) )
                    {
                        if ( v_Holiday == null )
                        {
                            v_Holiday = v_Temp;
                        }
                        else if ( v_Holiday.getShowLevel() < v_Temp.getShowLevel() )
                        {
                            v_Holiday = v_Temp;
                        }
                    }
                }    
            }
        }
        
        
        // 快速判断特殊公历信息
        v_TabPart = $HDElse.get(v_MMWW);
        if ( !Help.isNull(v_TabPart) )
        {
            for (int i=0; i<v_TabPart.size(); i++)
            {
                Holiday v_Temp = v_TabPart.get(i);
                
                if ( v_Temp.isHoliday(i_Date) )
                {
                    if ( !Help.isNull(v_Temp.getShortInfo()) )
                    {
                        if ( v_Holiday == null )
                        {
                            v_Holiday = v_Temp;
                        }
                        else if ( v_Holiday.getShowLevel() < v_Temp.getShowLevel() )
                        {
                            v_Holiday = v_Temp;
                        }
                    }
                }    
            }
        }
        
        
        // 快速判断农历信息
        v_MMDD    = StringHelp.lpad(v_Lunar.getMonth() ,2 ,"0") + StringHelp.lpad(v_Lunar.getDay() ,2 ,"0");
        v_TabPart = $HDLunar.get(v_MMDD);
        if ( !Help.isNull(v_TabPart) )
        {
            for (int i=0; i<v_TabPart.size(); i++)
            {
                Holiday v_Temp = v_TabPart.get(i);
                
                if ( v_Temp.isHoliday(i_Date) )
                {
                    if ( !Help.isNull(v_Temp.getShortInfo()) )
                    {
                        if ( v_Holiday == null )
                        {
                            v_Holiday = v_Temp;
                        }
                        else if ( v_Holiday.getShowLevel() < v_Temp.getShowLevel() )
                        {
                            v_Holiday = v_Temp;
                        }
                    }
                }    
            }
        }
        
        
        return v_Holiday;
    }
    
    
    
    /**
     * 判断给定日期是否有假日休息日
     * 
     * @param i_Date
     * @return
     */
    public static boolean isRest(Date i_Date)
    {
        List<Holiday> v_TabPart = null;
        String        v_MMDD    = i_Date.getMD_ID();
        String        v_MMWW    = StringHelp.lpad(i_Date.getMonth() ,2 ,"0") + StringHelp.lpad(i_Date.getWeek() ,2 ,"0");
        

        // 快速判断正常公历节日
        v_TabPart = $HDCalc.get(v_MMDD);
        if ( !Help.isNull(v_TabPart) )
        {
            for (int i=0; i<v_TabPart.size(); i++)
            {
                Holiday v_Temp = v_TabPart.get(i);
                
                if ( v_Temp.isHoliday(i_Date) && v_Temp.isRest() )
                {
                    return true;
                }    
            }
        }
        
        
        // 快速判断特殊公历信息
        v_TabPart = $HDElse.get(v_MMWW);
        if ( !Help.isNull(v_TabPart) )
        {
            for (int i=0; i<v_TabPart.size(); i++)
            {
                Holiday v_Temp = v_TabPart.get(i);
                
                if ( v_Temp.isHoliday(i_Date) && v_Temp.isRest() )
                {
                    return true;
                }    
            }
        }
        
        
        // 快速判断农历信息
        Lunar v_Lunar = i_Date.getLunar();
        v_MMDD    = StringHelp.lpad(v_Lunar.getMonth() ,2 ,"0") + StringHelp.lpad(v_Lunar.getDay() ,2 ,"0");
        v_TabPart = $HDLunar.get(v_MMDD);
        if ( !Help.isNull(v_TabPart) )
        {
            for (int i=0; i<v_TabPart.size(); i++)
            {
                Holiday v_Temp = v_TabPart.get(i);
                
                if ( v_Temp.isHoliday(i_Date) && v_Temp.isRest() )
                {
                    return true;
                }    
            }
        }
        
        return false;
    }
    
}
