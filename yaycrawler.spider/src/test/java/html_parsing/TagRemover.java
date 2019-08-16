package html_parsing;

public class TagRemover implements HtmlParser {

	@Override
	public String parse(String html) {
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder();
		int status = 0;// 0-->not start 1-->started
		for (int i = 0; i < html.length(); i++) {
			char ch = html.charAt(i);
			if (status == 0) {
				if (ch == '<') {
					if (html.startsWith("script", i + 1)
							|| html.startsWith("SCRIPT", i + 1))
						status = 2;
					else if (html.startsWith("style", i + 1)
							|| html.startsWith("STYLE", i + 1)) {
						status = 3;
					} else {
						status = 1;
					}
				} else if (ch == '&') {
					//find a ';' within 5 chars
					int k=1;
					for (k=1;k<5;k++){
						if (html.charAt(i+k)==';')
							break;
					}
					if (k<5)
						i+=k;
					else {
						sb.append(ch);
					}
				} else if (ch == ' ' || ch == '\r' || ch == '\n' || ch == '\t') {
				} else {
					sb.append(ch);
				}
			} else {
				if (ch == '>') {
					if (status == 1) {
						status = 0;
					} else if (status == 2) {
						if (html.startsWith("/script", i - "/script".length())
								|| html.startsWith("/SCRIPT",
										i - "/SCRIPT".length())) {
							status = 0;
						}
					} else if (status == 3) {
						if (html.startsWith("/style", i - "/style".length())
								|| html.startsWith("/STYLE",
										i - "/STYLE".length())) {
							status = 0;
						}
					}
				}
			}
		}
		return sb.toString();
	}

}
