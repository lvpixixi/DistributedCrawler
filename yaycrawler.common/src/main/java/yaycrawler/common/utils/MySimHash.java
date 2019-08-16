package yaycrawler.common.utils;

/*计算两篇文章相似度*/
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.StandardTokenizer;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MySimHash {
    private String tokens; //字符串
    private BigInteger strSimHash;//字符产的hash值
    private int hashbits = 64; // 分词后的hash数;


    public MySimHash(String tokens) {
        this.tokens = tokens;
        this.strSimHash = this.simHash();
    }

    private MySimHash(String tokens, int hashbits) {
        this.tokens = tokens;
        this.hashbits = hashbits;
        this.strSimHash = this.simHash();
    }

    public BigInteger getSimHash() {
    	return this.strSimHash;
    }
    /**
     * 清除html标签
     * @param content
     * @return
     */
    private String cleanResume(String content) {
        // 若输入为HTML,下面会过滤掉所有的HTML的tag
        content = Jsoup.clean(content, Whitelist.none());
        content = StringUtils.lowerCase(content);
        String[] strings = {" ", "\n", "\r", "\t", "\\r", "\\n", "\\t", "&nbsp;"};
        for (String s : strings) {
            content = content.replaceAll(s, "");
        }
        content = content.replaceAll("[\\p{P}+~$`^=|<>～｀＄＾＋＝｜＜＞￥×]", "");
        return content;
    }


    /**
     * 这个是对整个字符串进行hash计算
     * @return
     */
    private BigInteger simHash() {

        tokens = cleanResume(tokens); // cleanResume 删除一些特殊字符

        int[] v = new int[this.hashbits];

        List<Term> termList = StandardTokenizer.segment(this.tokens); // 对字符串进行分词

        //对分词的一些特殊处理 : 比如: 根据词性添加权重 , 过滤掉标点符号 , 过滤超频词汇等;
        Map<String, Integer> weightOfNature = new HashMap<String, Integer>(); // 词性的权重
        weightOfNature.put("n", 2); //给名词的权重是2;
        Map<String, String> stopNatures = new HashMap<String, String>();//停用的词性 如一些标点符号之类的;
        stopNatures.put("w", ""); //
        int overCount = 5; //设定超频词汇的界限 ;
        Map<String, Integer> wordCount = new HashMap<String, Integer>();

        for (Term term : termList) {
            String word = term.word; //分词字符串

            String nature = term.nature.toString(); // 分词属性;
            //  过滤超频词
            if (wordCount.containsKey(word)) {
                int count = wordCount.get(word);
                if (count > overCount) {
                    continue;
                }
                wordCount.put(word, count + 1);
            } else {
                wordCount.put(word, 1);
            }

            // 过滤停用词性
            if (stopNatures.containsKey(nature)) {
                continue;
            }

            // 2、将每一个分词hash为一组固定长度的数列.比如 64bit 的一个整数.
            BigInteger t = this.hash(word);
            for (int i = 0; i < this.hashbits; i++) {
                BigInteger bitmask = new BigInteger("1").shiftLeft(i);
                // 3、建立一个长度为64的整数数组(假设要生成64位的数字指纹,也可以是其它数字),
                // 对每一个分词hash后的数列进行判断,如果是1000...1,那么数组的第一位和末尾一位加1,
                // 中间的62位减一,也就是说,逢1加1,逢0减1.一直到把所有的分词hash数列全部判断完毕.
                int weight = 1;  //添加权重
                if (weightOfNature.containsKey(nature)) {
                    weight = weightOfNature.get(nature);
                }
                if (t.and(bitmask).signum() != 0) {
                    // 这里是计算整个文档的所有特征的向量和
                    v[i] += weight;
                } else {
                    v[i] -= weight;
                }
            }
        }
        BigInteger fingerprint = new BigInteger("0");
        for (int i = 0; i < this.hashbits; i++) {
            if (v[i] >= 0) {
                fingerprint = fingerprint.add(new BigInteger("1").shiftLeft(i));
            }
        }
        return fingerprint;
    }


    /**
     * 对单个的分词进行hash计算;
     * @param source
     * @return
     */
    private BigInteger hash(String source) {
        if (source == null || source.length() == 0) {
            return new BigInteger("0");
        } else {
            /**
             * 当sourece 的长度过短，会导致hash算法失效，因此需要对过短的词补偿
             */
            while (source.length() < 3) {
                source = source + source.charAt(0);
            }
            char[] sourceArray = source.toCharArray();
            BigInteger x = BigInteger.valueOf(((long) sourceArray[0]) << 7);
            BigInteger m = new BigInteger("1000003");
            BigInteger mask = new BigInteger("2").pow(this.hashbits).subtract(new BigInteger("1"));
            for (char item : sourceArray) {
                BigInteger temp = BigInteger.valueOf((long) item);
                x = x.multiply(m).xor(temp).and(mask);
            }
            x = x.xor(new BigInteger(String.valueOf(source.length())));
            if (x.equals(new BigInteger("-1"))) {
                x = new BigInteger("-2");
            }
            return x;
        }
    }

    /**
     * 计算海明距离,海明距离越小说明越相似;
     * @param other
     * @return
     */
    public int hammingDistance(MySimHash other) {
        BigInteger m = new BigInteger("1").shiftLeft(this.hashbits).subtract(
                new BigInteger("1"));
        BigInteger x = this.strSimHash.xor(other.strSimHash).and(m);
        int tot = 0;
        while (x.signum() != 0) {
            tot += 1;
            x = x.and(x.subtract(new BigInteger("1")));
        }
        return tot;
    }
    
    public static int hammingDistance(BigInteger strSimHash1, BigInteger strSimHash2) {
    	 BigInteger m = new BigInteger("1").shiftLeft(64).subtract(
                 new BigInteger("1"));
         BigInteger x = strSimHash1.xor(strSimHash2).and(m);
         int tot = 0;
         while (x.signum() != 0) {
             tot += 1;
             x = x.and(x.subtract(new BigInteger("1")));
         }
         return tot;
    }


    public double getSemblance(MySimHash s2 ){
        double i = (double) this.hammingDistance(s2);
        return 1 - i/this.hashbits ;
    }

    public static void main(String[] args) {

        String s1="<div class=\"rich_media_content \" id=\"js_content\">  <p><span >「</span></p>  <p><span >高分五号、六号卫星投入使用，是高分专项创新体制机制的实践，标志着高分专项打造的高空间分辨率、高时间分辨率、高光谱分辨率的天基对地观测能力中最有应用特色的高光谱能力的形成。</span></p>  <p><span >」</span></p>  <p><span ></p><p></span></p>  <p><span >3</span><span >月21日，高分五号、六号卫星投入使用仪式圆满落下帷幕，由中国空间技术研究院下属抓总研制的高分六号卫星和高分五号的两台载荷正式交付用户。高分五号、六号卫星投入使用，是高分专项创新体制机制的实践，标志着高分专项打造的高空间分辨率、高时间分辨率、高光谱分辨率的天基对地观测能力中最有应用特色的高光谱能力的形成。</span></p>  <p><img src=\"${RootPath}/weixin.sogou.com/image/7c89e86e92925e29dd9ef7c659c8f976.jpeg\"></p>  <p style=\"text-align: center;\"><span >▲院下属航天东方红卫星有限公司与相关单位共同签署高分六号卫星长期运行管理协议（国防科工局新闻宣传中心供图 张高翔摄）</span></p>  <p><span >上级有关部门领导，中国航天科技集团有限公司总经理、党组副书记袁洁，中国空间技术研究院李明副院长出席高分五号、六号卫星投入使用仪式。</span></p>  <p><img src=\"${RootPath}/weixin.sogou.com/image/e23b12c7d148bba9b5554c36d579334b.jpeg\" width=\"800\" height=\"600\" width=\"800\" height=\"600\" width=\"800\" height=\"600\" width=\"800\" height=\"600\" width=\"800\" height=\"600\" width=\"800\" height=\"600\"></p>  <p style=\"text-align: center;\"><img src=\"${RootPath}/weixin.sogou.com/image/25efe62e01028db16252477917a1555c.gif\"></p>  <p style=\"text-align: center;\"><span >▲ 高分六号卫星与高分一号卫星组网观测示意图</span></p>  <p><span >高分六号卫星由中国空间技术研究院下属的航天东方红卫星有限公司抓总研制,是我国第一颗设置红边谱段的多光谱遥感卫星，是高分专项天基系统中兼顾普查与详查能力、具有高度机动灵活性的高分辨率光学卫星。该星与高分一号卫星组网实现了对我国陆地区域2天的重访观测，极大提高了遥感数据的获取规模和时效，有效弥补国内外已有中高空间分辨率多光谱卫星资源的不足，提升国产遥感卫星数据的自给率和应用范围。</span></p>  <p style=\"text-align: center;\"><img src=\"${RootPath}/weixin.sogou.com/image/807c83477013149918c59f2478192d7a.jpeg\"></p>  <p style=\"text-align: center;\"><img src=\"${RootPath}/weixin.sogou.com/image/8c306cfb1fdb3580f7e8bba453d42229.jpeg\"></p>  <p><span >在轨测试期间，已为安徽河南受灾农作物损失评估、全国秋播作物面积监测、大气环境监测等提供了数据保障，为2018年6月大兴安岭森林火灾、10-11月金沙江白格滑坡堰塞湖以及雅鲁藏布江米林滑坡堰塞湖、9月印尼海啸等国内外重特大灾害及时提供了应急观测服务。</span></p>  <p style=\"text-align: center;\"><img src=\"${RootPath}/weixin.sogou.com/image/f83d5ef468ef395fca2dfad52bc6966a.jpeg\"></p>  <p style=\"text-align: center;\"><span >▲全谱段光谱成像仪</span></p>  <p style=\"text-align: center;\"><img src=\"${RootPath}/weixin.sogou.com/image/1cf752ac84ddbfb8f026ac8fe12ed7bf.jpeg\"></p>  <p style=\"text-align: center;\"><span >▲大气环境红外甚高光谱分辨率探测仪</span></p>  <p><span >北京空间机电研究所承研的高分五号两台载荷全谱段光谱成像仪和大气环境红外甚高光谱分辨率探测仪功能正常、性能稳定，测试结果均满足卫星研制总要求；地面数据处理能力、应用能力得到验证，满足用户使用要求。该所以高质量的数据产品和高效率在轨服务为目标，为遥感数据的生产和应用提供技术服务，做完美“售后”。</span></p>  <p><img src=\"${RootPath}/weixin.sogou.com/image/a109a1d9ec9aadcfd45d4467b61b4b17.jpeg\" width=\"800\" height=\"1066\"></p>  <p style=\"text-align: center;\"><img src=\"${RootPath}/weixin.sogou.com/image/30e276b5e5c5b700e14e088b496ac60d.jpeg\" width=\"800\" height=\"1066\" width=\"800\" height=\"1066\"></p>  <p><span >全谱段光谱成像仪是我国高分辨率多光谱遥感相机中光谱范围最宽的载荷，覆盖可见、近红外、短波、中波、长波共12个谱段。其中，长波四谱段分裂窗空间分辨率达到40m，为国际民用卫星最高。该载荷在环保、国土、气象三大领域的水体热污染监测、重点湖库水华和水质监测、内陆大型水体水质监测、植被覆盖度信息提取、岩性-构造解译、矿物信息提取、矿山地物分类、植被长势监测、青藏高原典型冰川群及背景积雪监测、干旱遥感信息提取、局地高温监测等业务应用产品测试中取得了非常好的效果。</span></p>  <p><span >大气环境红外甚高光谱分辨率探测仪是国内首台掩星观测模式的大气探测载荷，是国内光谱分辨率最高的光谱探测载荷。因此，基于大气环境红外甚高光谱分辨率探测仪数据，气象用户完成了大气成分遥感监测与评价应用示范，通过对多轨数据的精确反演，获得了南极地区上空痕量气体的垂直廓线产品，精度满足“优”的评价标准。</span></p>  <p><span >高分五号、六号卫星的投入使用，将大幅提升我国对地观测水平，在国家污染防治攻坚战、生态建设与绿色发展、乡村振兴与脱贫攻坚、推动共建“一带一路”等方面提供有力的空间信息支撑，对于服务经济社会发展、建设美丽中国、保障民生安全等具有重要意义。</span></p>  <p><span >高分辨率对地观测系统重大专项（简称高分专项）是《国家中长期科学与技术发展规划纲要（2006-2020年）》确定的十六个重大科技专项之一， 2010年批准启动实施以来，由中国空间技术研究院抓总研制的高分一号、二号、三号、四号、六号都已成功发射，数据源不断丰富。目前可涵盖不同空间分辨率、不同覆盖宽度、不同谱段、不同重访周期的高分数据型谱基本形成，与其他民用卫星遥感数据相配合，为高分遥感应用奠定了坚实基础。</span></p>  <p><span >高分专项卫星数据已广泛应用于20个行业、30个省域，在国土、环保、农业、林业、测绘等领域应用中取得了重要成果。高分专项已设立了30个省级高分数据与应用中心，取得了一大批的应用成果，为促进区域经济发展、提升地方政府现代化治理能力等提供了服务支撑。</span></p>  <p><span >今年是实现高分专项“十三五”目标承前启后的关键年，年底高分七号卫星还将发射，将全部完成天基系统的建设任务，高分专项的建设重点将转向应用体系的建设上。</span></p>  <p><span >我国将加强高分一号至六号卫星的融合应用，充分发挥多星体系化应用最大效益；进一步健全高分专项的共享渠道、畅通高分数据应用的高速公路，不断丰富和完善高分数据应用产品体系，将已经实践检验的有效“高分模式”推广应用在后续国家民用空间基础设施的实施上来，使越来越多的遥感数据应用产品服务于国内并走向国际，推动“高分”品牌走向世界，为“一带一路”倡议作出新的更大贡献，增进人类福祉。</span></p>  <p style=\"text-align: center;\"><span ></span></p>  </div>";
        String s2="<div class=\"rich_media_content \" id=\"js_content\">  <p data-mpa-powered-by=\"yiban.io\" ></h2>  <section >   <section >    <section >     <section  data-width=\"100%\">      <section >       <p style=\"text-align: center;\"><img src=\"${RootPath}/weixin.sogou.com/image/5f04833471ce4dd5f11215603a53a94d.jpeg\" width=\"800\" height=\"600\" width=\"800\" height=\"600\" width=\"800\" height=\"600\" width=\"800\" height=\"600\"></p>       <p>解放军报北京3月21日电 （张未、记者韩阜业）记者从国防科工局、国家航天局获悉，<span >我国高分辨率对地观测系统的高分五号和六号两颗卫星今日正式投入使用</span>。工业和信息化部副部长、国防科工局局长、国家航天局局长张克俭说：“高分五号、六号卫星的投入使用，将<span >大幅提升我国对地观测水平，在国家污染防治攻坚战、生态建设与绿色发展、乡村振兴与脱贫攻坚、推动共建‘一带一路’等方面提供有力的空间信息支撑，</span>对于服务经济社会发展、建设美丽中国、保障民生安全等具有重要意义。”</p>       <p><img src=\"${RootPath}/weixin.sogou.com/image/edeebfc9e8ac50542f2f830100256b11.jpeg\"></p>       <p><img src=\"${RootPath}/weixin.sogou.com/image/e9ec93aa88527da8210218aef80a380f.jpeg\"></p>       <p><img src=\"${RootPath}/weixin.sogou.com/image/1bfd1ab82c2327b18b82ed8197e1b463.jpeg\"></p>       <p>高分五号、六号卫星分别于2018年5月9日和6月2日成功发射。<span >高分五号是国内光谱分辨率最高的卫星，也是国际上首次实现对大气和陆地进行综合观测的全谱段高光谱卫星，可实现多种观测数据融合应用</span>，为我国环境监测、资源勘查、防灾减灾等行业提供高质量、高可靠的高光谱数据，在我国高光谱分辨率遥感卫星应用方面具有示范作用，在掌握高光谱遥感信息资源自主权、满足国家需求等方面具有重大战略意义。</p>       <p><img src=\"${RootPath}/weixin.sogou.com/image/d3f33b0ad7c298712b26493dbb9baed1.jpeg\" width=\"800\" height=\"1010\"></p>       <p><img src=\"${RootPath}/weixin.sogou.com/image/c2c75360841001ff038f1b4a800ddbcf.jpeg\" width=\"800\" height=\"1010\" width=\"800\" height=\"1010\" width=\"800\" height=\"1010\"></p>       <p><span >高分六号卫星是高分专项天基系统中兼顾普查与详查能力、具有高度机动灵活性的高分辨率光学卫星</span>。该星与高分一号卫星组网实现了对我国陆地区域2天的重访观测，极大提高了遥感数据的获取规模和时效，有效弥补国内外已有中高空间分辨率多光谱卫星资源的不足，提升国产遥感卫星数据的自给率和应用范围。</p>       <p>在轨测试期间，已为安徽河南受灾农作物损失评估、全国秋播作物面积监测、大气环境监测等提供了数据保障，为2018年6月大兴安岭森林火灾、10-11月金沙江白格滑坡堰塞湖以及雅鲁藏布江米林滑坡堰塞湖、9月印尼海啸等国内外重特大灾害及时提供了应急观测服务。</p>       <p>据介绍，高分五号、六号任务由国防科工局负责统一组织实施。卫星、运载火箭系统分别由中国航天科技集团公司所属上海航天技术研究院、中国空间技术研究院抓总研制，中国科学院遥感与数字地球研究所负责数据接收与传输，中国资源卫星应用中心负责数据处理与分发。生态环境部是高分五号卫星牵头用户部门、农业农村部是高分六号卫星牵头用户部门，主用户部门包括自然资源部、应急管理部、中国气象局、国家林业和草原局等。</p>       <p>据了解，高分辨率对地观测系统重大专项（简称高分专项）是《国家中长期科学与技术发展规划纲要（2006-2020年）》确定的十六个重大科技专项之一， 2010年批准启动实施以来，已成功发射<span >高分一号至高分六号等六颗卫星，数据源不断丰富，实现了“六战六捷”</span>。目前可涵盖不同空间分辨率、不同覆盖宽度、不同谱段、不同重访周期的高分数据型谱基本形成，与其他民用卫星遥感数据相配合，为高分遥感应用奠定了坚实基础。</p>       <p><span >高分专项卫星数据已广泛应用于20个行业、30个省域，在国土、环保、农业、林业、测绘等领域应用中取得了重要成果</span>。高分专项已设立了30个省级高分数据与应用中心，取得了一大批的应用成果，为促进区域经济发展、提升地方政府现代化治理能力等提供了服务支撑。</p>      </section>     </section>    </section>   </section>  </section>  <p><span ></p><p></span></p>              <section >       <section >        <section >         <p><span >延伸阅读</span></p>        </section>       </section>             </section>     </section>    </sectio>   </section>  </section>  <p><span ></span></p>                     </section>                 </section>        <section >         <p>高分五号卫星概况</p>        </section>        <section>         <section >                             </section>                 </section>       </section>      </section>     </section>          <p style=\"text-align: center;\"><span >↑↑↑戳视频了解高分五号卫星概况</span></p>     <p style=\"text-align: center;\"><img src=\"${RootPath}/weixin.sogou.com/image/a32bb808848e81351802ef10c2ebd7e6.gif\"></p>     <p style=\"text-align: center;\"><span ><span ><span >▲</span></span>高分五号卫星资料图</span></p>     <p style=\"text-align: center;\"><img src=\"${RootPath}/weixin.sogou.com/image/e2c27e0927c3b35440fe31a1c805e2ef.gif\"></p>     <p style=\"text-align: center;\"><span ><span >▲</span></span>高分五号卫星资料图</p>     <p>高分五号卫星(GF-5卫星)于<span >2018年5月9日发射成功，是世界首颗实现对大气和陆地综合观测的全谱段高光谱卫星，填补了国产卫星无法有效探测区域大气污染气体的空白，可满足环境综合监测等方面的迫切需求，是中国实现高光课分辨率对地观测能力的重要标志</span>。</p>     <p>卫星首次搭载了<span >大气痕量气体差分吸收光谱仪、主要温室气体探测仪、大气多角度偏振探测仪、大气环境红外甚高分辨率探测仪、可见短波红外高光谱相机、全谱段光谱成像仪</span>共6台载荷。GF-5卫星以生态环境部为牵头主用户、自然资源部和中国气象局等为主要用户，同时还将为其他用户部门和有关区域提供示范应用服务。</p>     <p><span > 轨道类型：太阳同步轨道 </span><span ></span></p>     <p><span ><span > 轨道高度：705km</span><span ></span></span></p>     <p><span ><span ><span > 卫星重量：2700kg</span><span ></span></span></span></p>     <p><span ><span ><span ><span ><span > 设计寿命：8年</span></span></span></span></span></p>     <p><span > 搭载六台载荷：</span><span ></span></p>     <p><img src=\"${RootPath}/weixin.sogou.com/image/cbdee862cde7ff9fcab5c47be3784aca.jpeg\" width=\"800\" height=\"1374\"></p>     <p style=\"text-align: center;\"><span ><span ><span >▲</span></span>全谱段光谱成像仪</span></p>     <p><img src=\"${RootPath}/weixin.sogou.com/image/5aae4565155553b3d270ebe2ce50deb8.jpeg\" width=\"800\" height=\"1374\"></p>     <p style=\"text-align: center;\"><span ><span ><span >▲</span></span>大气主要温室气体监测仪</span></p>     <p><img src=\"${RootPath}/weixin.sogou.com/image/168ff72b55c9849f7081bcf840c1b942.jpeg\" width=\"800\" height=\"1408\"></p>     <p style=\"text-align: center;\"><span ><span ><span >▲</span></span>大气痕量气体差分吸收光谱仪</span></p>     <p><img src=\"${RootPath}/weixin.sogou.com/image/058ee20f2017c4f0b7ea12ac050763fb.jpeg\" width=\"800\" height=\"1377\"></p>     <p style=\"text-align: center;\"><span ><span ><span >▲</span></span>大气气溶胶多角度偏振探测仪</span></p>     <p><img src=\"${RootPath}/weixin.sogou.com/image/3cf1d700acc481d956e79bf0c93453c7.jpeg\" width=\"800\" height=\"1156\"></p>     <p style=\"text-align: center;\"><span ><span ><span >▲</span></span>大气环境红外甚高光谱分辨率探测仪</span></p>     <p><img src=\"${RootPath}/weixin.sogou.com/image/379c909615fa207801b2dc57a22b476b.jpeg\" width=\"800\" height=\"1482\" width=\"800\" height=\"1482\" width=\"800\" height=\"1482\"></p>     <p style=\"text-align: center;\"><span ><span ><span >▲</span></span>可见短波红外高光谱相机</span></p>    </section>   </section>  </section>  <p><span ></p><p></span></p>                               </section>                 </section>        <section >         <p>高分六号卫星概况</p>        </section>        <section>         <section >                             </section>                 </section>       </section>      </section>     </section>     <p style=\"text-align: center;\"><img src=\"${RootPath}/weixin.sogou.com/image/98e5547c65a108af9cffe2eec0ee0cc6.gif\"></p>     <p style=\"text-align: center;\"><span ><span ><span >▲高分六号卫星资料图</span></span></span></p>     <p style=\"text-align: center;\"><img src=\"${RootPath}/weixin.sogou.com/image/8d1026a23b32f861547fb6014b71985f.gif\"></p>     <p style=\"text-align: center;\"><span ><span >▲高分六号卫星资料图</span></span></p>     <p>高分六号卫星是国家高分辨率对地观测系统重大专项的第六颗卫星，<span >以农业农村部为牵头用户，自然资源部和应急管理部为主用户，面向农业、林业和减灾等应用</span>。</p>     <p>2015年7月17日，<span >高分六号</span>卫星工程由国防科工局、财政部联合批复立项，历经2年完成全部卫星研制工作，并于2018年6月2日，在酒泉卫星发射中心由长征2号丁运载火箭成功送入预定轨道。</p>     <p style=\"text-align: center;\"><img src=\"${RootPath}/weixin.sogou.com/image/8af1e43886fc7913eacefa4e030241f2.jpeg\" width=\"800\" height=\"450\"></p>     <p style=\"text-align: center;\"><span ><span >▲高分六号成像资料图</span></span></p>     <p style=\"text-align: center;\"><img src=\"${RootPath}/weixin.sogou.com/image/772c03767244792cea1b10d47dca60ea.jpeg\" width=\"800\" height=\"450\"></p>     <p style=\"text-align: center;\"><span ><span >▲高分六号成像资料图</span></span></p>     <p><span >2018年12月26日，分别完成了卫星系统、地面系统、农业、林业和减灾应用的在轨测试总结评审。卫星平台、载荷、星地一体化及应用系统指标均优于或满足任务书要求。</span></p>     <p><img src=\"${RootPath}/weixin.sogou.com/image/373fd863b8843101bfc9db8ba6b7118c.jpeg\" width=\"800\" height=\"450\"></p>     <p style=\"text-align: center;\"><span ><span >▲高分六号成像资料图</span></span></p>     <p><img src=\"${RootPath}/weixin.sogou.com/image/de79d8f39a5ab8c942e9d048affd865c.jpeg\" width=\"800\" height=\"450\"></p>     <p style=\"text-align: center;\"><span ><span >▲高分六号成像资料图</span></span></p>    </section>   </section>  </section>  <p><span ></span></p>  <p><span ><img src=\"${RootPath}/weixin.sogou.com/image/bb3876192b061787ac3b8dd17c90983e.jpeg\" width=\"800\" height=\"101\" width=\"800\" height=\"101\"></span></p>  <p><span ><span >▋作者：韩阜业、张未</span></span></p>  <p><span >▋来源：解放军报社装备发展部分社</span></p>  <p><span >▋监制：邹维荣</span></p>  <p><span >▋责编：韩阜业</span></p>  <p><span >▋编辑：弥向阳</span></p>  <center >      </section>  </section> </center></div>";
        String s3="高分五号、六号卫星正式投入使用";
        String s4="高分高考状元";
        
        long l3 = System.currentTimeMillis();
        MySimHash hash1 = new MySimHash(s1, 64);
        MySimHash hash2 = new MySimHash(s2, 64);
        MySimHash hash3 = new MySimHash(s3, 64);
        MySimHash hash4 = new MySimHash(s4, 64);
        
        System.out.println(  "S1指纹= "+hash1.simHash() );
        System.out.println(  "S2指纹= "+hash2.simHash() );
        System.out.println(  "S3指纹= "+hash3.simHash() );
        System.out.println(  "S4指纹= "+hash4.simHash() );
        
        System.out.println("======================================");
        System.out.println(  "S1和S2的（hamming）距离= "+hash1.hammingDistance(hash2) );
        System.out.println(  "S1和S3的（hamming）距离 = "+hash1.hammingDistance(hash3) );
        System.out.println(  "S3和S4的（hamming）距离 = "+hash3.hammingDistance(hash4) );
        System.out.println("======================================");
        System.out.println(  "S1和S2的（Semblance）相似度 = "+hash1.getSemblance(hash2) );
        System.out.println(  "S1和S3的（Semblance）相似度 = "+hash1.getSemblance(hash3) );
        System.out.println(  "S3和S4的（Semblance）相似度 = "+hash3.getSemblance(hash4) );
        long l4 = System.currentTimeMillis();
        System.out.println("总共耗时:"+(l4-l3)+"毫秒");
        System.out.println("======================================");



    }
}

