package bscrawler.spider.model;

public class DomModel {

	private String tagName;
	
	private String id;
	
	private String attrClass;
	
	private String attrName;

	public String getTagName() {
		return tagName;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAttrClass() {
		return attrClass;
	}

	public void setAttrClass(String attrClass) {
		this.attrClass = attrClass;
	}

	public String getAttrName() {
		return attrName;
	}

	public void setAttrName(String attrName) {
		this.attrName = attrName;
	}
}
