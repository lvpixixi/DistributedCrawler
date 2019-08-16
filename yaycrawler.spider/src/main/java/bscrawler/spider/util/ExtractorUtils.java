package bscrawler.spider.util;

import java.util.ArrayList;
import java.util.List;

import us.codecraft.webmagic.model.annotation.ExtractBy;
import us.codecraft.webmagic.selector.CssSelector;
import us.codecraft.webmagic.selector.JsonPathSelector;
import us.codecraft.webmagic.selector.RegexSelector;
import us.codecraft.webmagic.selector.Selector;
import us.codecraft.webmagic.selector.XpathSelector;

/**
 * Tools for annotation converting. <br>
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.2.1
 */
public class ExtractorUtils {

    public static Selector getSelector(ExtractBy extractBy) {
        String value = extractBy.value();
        Selector selector;
        switch (extractBy.type()) {
            case Css:
                selector = new CssSelector(value);
                break;
            case Regex:
                selector = new RegexSelector(value);
                break;
            case XPath:
                selector = getXpathSelector(value);
                break;
            case JsonPath:
                selector = new JsonPathSelector(value);
                break;
            default:
                selector = getXpathSelector(value);
        }
        return selector;
    }
    
    public static Selector getSelector(String sourceType,String extractorExp) {
    	//字段抽取表达式
        String value = extractorExp;
        Selector selector;
        if("css".equals(sourceType)){
        	selector = new CssSelector(value);
        }else if("regex".equals(sourceType)){
        	selector = new RegexSelector(value);
        }else if("xpath".equals(sourceType)){
        	selector = getXpathSelector(value);
        }else if("jsonpath".equals(sourceType)){
        	selector = new JsonPathSelector(value);
        }else{
        	selector = getXpathSelector(value);
        }
        return selector;
    }

    private static Selector getXpathSelector(String value) {
        Selector selector = new XpathSelector(value);
        return selector;
    }

    public static List<Selector> getSelectors(ExtractBy[] extractBies) {
        List<Selector> selectors = new ArrayList<Selector>();
        if (extractBies == null) {
            return selectors;
        }
        for (ExtractBy extractBy : extractBies) {
            selectors.add(getSelector(extractBy));
        }
        return selectors;
    }
}
