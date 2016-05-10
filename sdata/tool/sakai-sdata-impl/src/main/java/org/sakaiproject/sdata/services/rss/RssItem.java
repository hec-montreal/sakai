package org.sakaiproject.sdata.services.rss;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Represents an item in an RSS feed.
 *
 * @author    Simon Brown
 */
public class RssItem implements Comparable<RssItem>{

  /** the title of the item */
  private String title;

  /** a link (url) to the complete item */
  private String link;
  
  private String content;
  private Date pubDate;
  private String spubDate;
  private String name;

  /**
   * Gets the title of this item.
   *
   * @return  the title as a String
   */
  public String getTitle() {
    return title;
  }

  /**
   * Sets the title of this item.
   *
   * @param title   the title as a String
   */
  public void setTitle(String title) {
    this.title = title;
  }

  /**
   * Gets the url (link) to this item.
   *
   * @return  the url as a String
   */
  public String getLink() {
    return link;
  }

  /**
   * Sets the url (link) to this item.
   *
   * @param link    the link as a String
   */
  public void setLink(String link) {
    this.link = link;
  }

  public void setContent(String content) {
	this.content = content;
  }

  public String getContent() {
	return content;
  }

  public void setPubDate(Date pubDate) {
	this.pubDate = pubDate;
  }

  public Date getPubDate() {
	return pubDate;
  }

public int compareTo(RssItem o) {
	// TODO Auto-generated method stub
	if (o.getPubDate().before(pubDate)){
		return 0;
	} else {
		return 1;
	}
	//return getPubDate().compareTo(o.getPubDate());
}

public void setSpubDate(String spubDate) {
	this.spubDate = spubDate;
}

public String getSpubDate() {
	return new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(pubDate);
}

public void setName(String name) {
	this.name = name;
}

public String getName() {
	return name;
}

}
