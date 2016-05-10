package org.sakaiproject.sdata.services.rss;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a particular RSS feed.
 *
 * @author    Simon Brown
 */
public class RssFeed {

  /** the items contained within the feed */
  private List<RssItem> items = new ArrayList();

  /**
   * Adds a item to this feed.
   *
   * @param item    an RssItem instance
   */
  public void addItem(RssItem item) {
    items.add(item);
  }

  /**
   * Gets a collection of all items within this RSS feed.
   *
   * @return  a Collection of RssItem instances
   */
  public List<RssItem> getItems() {
    return items;
  }

}
