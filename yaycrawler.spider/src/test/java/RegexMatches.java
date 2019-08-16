
import java.util.regex.Matcher;
import java.util.regex.Pattern;
 
public class RegexMatches
{
    public static void main( String args[] ){
 
      // 按指定模式在字符串查找
      String line = "This order was placed for QT3000! OK?";
      String line1 = "xpath(div/p/a,asdfes)";
      String pattern = "(\\D*)(\\d+)(.*)";
      
      String INVOKE_PATTERN = "(\\w+)\\((.*)\\)";
 
      // 创建 Pattern 对象
      Pattern r = Pattern.compile(INVOKE_PATTERN);
 
      // 现在创建 matcher 对象
      Matcher m = r.matcher(line1);
      if (m.find( )) {
    	 for(int i=0;i<=m.groupCount();i++){
    		 System.out.println("Found value: " + m.group(i) );
    	 }
      } else {
         System.out.println("NO MATCH");
      }
   }
}