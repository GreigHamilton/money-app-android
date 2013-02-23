package com.greighamilton.moneymanagement.external;


public class Tweet {
	
    String author;
    String content;
    String source;
    
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getSource() {
		return source;
	}
	public void setSource() {
		int start;
		int end;
		
		start = content.indexOf("http://");
		if (start != -1) {
			end = content.indexOf(" ", start);
			if (end == -1) source = content.substring(start);
			else source = content.substring(start, end);
		} else {
			source = null;
		}
	}
}