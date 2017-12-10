package sma.lbc.mgr;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.tags.Div;
import org.htmlparser.tags.ImageTag;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.tags.TitleTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.htmlparser.util.Translate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sma.lbc.model.LbcAnnonce;
import sma.lbc.model.LbcSearch;

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;

/**
 * Manager that parses HTML page.
 * 
 * @author marsolle
 * 
 */
public class LbcParser {

	/** Logger. */
	private static final Logger log = LoggerFactory.getLogger(LbcParser.class);

	/**
	 * Decode a Leboncoin HTML page.
	 * 
	 * @param strUrl
	 * @return
	 * @throws ParserException
	 */
	public LbcSearch decode(String strUrl) throws ParserException {
		log.info("decoding {}", strUrl);
		
		LbcSearch searchResult = new LbcSearch();
		searchResult.setSourceUrl(strUrl);

		Parser parser = new Parser(strUrl);

		/**
		 * Looking for: <br>
		 * <a href="http://www.leboncoin.fr/voitures/366740722.htm?ca=16_s" title="Mitsubishi PAJERO 2.5 TD intercooler 4x4"> <div
		 * class="ad-lbc"> <br>
		 * version of 10-jan-2013 :<br>
		 * <a href="http://www.leboncoin.fr/voitures/402483651.htm?ca=16_s" title="Toyota bj 43"> <div class="lbc">
		 */
		// the div
		NodeFilter filterDiv = new TagNameFilter("div");
		NodeFilter filterClass = new HasAttributeFilter("class", "lbc");
		NodeFilter filterDivAdLbc = new AndFilter(filterDiv, filterClass);

		// the a href
		// NodeFilter filterA = new TagNameFilter("a");
		// NodeFilter filter = new AndFilter(filterA, new HasChildFilter(filterDivAdLbc));

		// extract filtered nodes
		NodeList allNodes = parser.parse(null);
		// title
		TitleTag title = (TitleTag) allNodes.extractAllNodesThatMatch(new TagNameFilter("title"), true).elementAt(0);
		String strTitle = trimAllBlanks(title.getTitle()).trim();
		log.info("title.getTitle()={}", strTitle);
		searchResult.setTitleSearch(strTitle);

		NodeList nl = allNodes.extractAllNodesThatMatch(filterDivAdLbc, true);
		// log.info("" + list);
		log.info("list.size={}", nl.size());
		Node[] nodeArray = nl.toNodeArray();

		for (int i = 0; i < nodeArray.length; i++) {
			// for (int i = 0; i < 2; i++) {
			TagNode nodeDiv = (TagNode) nodeArray[i];
			log.debug("class={}", nodeDiv.getClass());
			// log.info("href={}", nodeDiv.getAttribute("href"));

			LbcAnnonce lbcAnnonce = new LbcAnnonce();

			// warning: due to implementation in LinkTag, <div> is a ending tag so in the DOM model it is the previous instead of
			// the parent
			LinkTag nodeA = (LinkTag) nodeDiv.getPreviousSibling();
			lbcAnnonce.setHref(nodeA.getLink());

			Node[] subDivs = nodeDiv.getChildren().extractAllNodesThatMatch(filterDiv).toNodeArray();
			for (Node node : subDivs) {
				Div subDiv = (Div) node;
				String className = subDiv.getAttribute("class");
				if ("date".equals(className)) {
					// lbcAnnonce.setStrDate(subDiv.getChildren().asString().trim());
					Node[] subDivsDate = subDiv.getChildren().extractAllNodesThatMatch(filterDiv).toNodeArray();
					if (subDivsDate.length == 2) {
						// lbcAnnonce.setDate(parseDate(subDivsDate[0].toPlainTextString(), subDivsDate[1].toPlainTextString()));
					} else {
						log.warn("subDivsDate.length={}", subDivsDate.length);
					}
					lbcAnnonce.setStrDate(trimAllBlanks(subDiv.toPlainTextString().trim()));
				} else if ("image".equals(className)) {
					// find the img tag
					NodeList nlImg = subDiv.getChildren().extractAllNodesThatMatch(new TagNameFilter("img"), true);
					if (nlImg.size() > 0) {
						ImageTag img = (ImageTag) nlImg.elementAt(0);
						lbcAnnonce.setImage(img.getImageURL());
					}
				} else if ("detail".equals(className)) {
					lbcAnnonce.setTitle(translateHtmlEntities(this.extractTextFromDiv(subDiv, "title")));
					lbcAnnonce.setPlacement(trimAllBlanks(this.extractTextFromDiv(subDiv, "placement")));
					String price = this.extractTextFromDiv(subDiv, "price");
					if (price != null) {
						price = price.replace("&euro;", "").replace(" ", "").replace("&nbsp;", "");
						log.debug("price={}", price);
						lbcAnnonce.setPrice(Integer.parseInt(price));
					}
				}
			}

			log.debug("lbcAnnonce={}", lbcAnnonce);
			searchResult.getLstAnnonces().add(lbcAnnonce);
		}
		return searchResult;
	}

	/**
	 * for instance &eacute; => é
	 * 
	 * @param strHtmlEntities
	 * @return
	 */
	public static final String translateHtmlEntities(String strHtmlEntities) {
		return Translate.decode(strHtmlEntities);
	}

	/**
	 * ex: [14 sept],[16:53]
	 * 
	 * @param d1
	 * @param d2
	 * @return
	 */
	public static Date parseDate(String d1, String d2) {
		log.debug("[{}],[{}]", d1, d2);

		DateFormatSymbols formatSymbol = new DateFormatSymbols(Locale.FRENCH);
		formatSymbol.setMonths(new String[] { "janv", "févr", "mars", "avri", "mai", "juin", "juil", "août", "sept", "oct",
				"nov", "déc" });
		// SimpleDateFormat format = new SimpleDateFormat("dd MMM'|'HH:mm", Locale.FRENCH);
		SimpleDateFormat format = new SimpleDateFormat("dd MMM'|'HH:mm", formatSymbol);
		format.setLenient(false);
		String strDate = d1 + "|" + d2;

		try {
			// strDate = format.format(new Date());
			log.info(strDate);
			return format.parse(strDate);
		} catch (ParseException e) {
			log.error("", e);
		return null;
		}
	}

	/**
	 * Extrac : <div class="title"> Toyota lj 70 - 4x4 </div>
	 * 
	 * @param pSubDiv
	 * @param className
	 * @return
	 */
	private String extractTextFromDiv(Node pSubDiv, String className) {
		NodeFilter filterDiv = new TagNameFilter("div");
		Node[] nodes = pSubDiv.getChildren().extractAllNodesThatMatch(filterDiv).toNodeArray();
		for (Node node : nodes) {
			Div div = (Div) node;
			if (className.equals(div.getAttribute("class"))) {
				return div.getStringText().trim();
			}
		}
		return null;
	}

	/**
	 * Any sequence of HTML blanks becomes 1 space.
	 * 
	 * @param input
	 * @return
	 */
	private static String trimAllBlanks(String input) {
		return input.replaceAll("\\s+", " ");
	}

	public SyndFeed getFeedFromLbc(String lbcUrl) {
		SyndFeed feed = null;
		try {
			LbcSearch data = this.decode(lbcUrl);
			feed = this.convertLbc2Feed(data);
		} catch (Exception e) {
			log.error(lbcUrl, e);
		}
		return feed;
	}

	private SyndFeed convertLbc2Feed(LbcSearch lbcSearch) {
		SyndFeed feed = new SyndFeedImpl();
		feed.setTitle(lbcSearch.getTitleSearch());
		// feed.setLink("http://rome.dev.java.net");
		feed.setDescription("This feed has been created by SMa from " + lbcSearch.getSourceUrl());
		feed.setLink(lbcSearch.getSourceUrl());
		List<LbcAnnonce> lstAnnonces = lbcSearch.getLstAnnonces();
		List<SyndEntry> entries = new ArrayList<SyndEntry>(lstAnnonces.size());

		// translate each annonce
		for (LbcAnnonce annonce : lstAnnonces) {
			SyndEntry entry = new SyndEntryImpl();
			entry.setTitle(annonce.getTitle());
			entry.setLink(annonce.getHref());

			SyndContent description = new SyndContentImpl();
			description.setType("text/html");
			description.setValue("<b>" + annonce.getPrice() + " €</b> à " + annonce.getPlacement() + "<br><img src=\""
					+ annonce.getImage() + "\">");

			entry.setDescription(description);
			entries.add(entry);
		}

		feed.setEntries(entries);
		feed.setPublishedDate(new Date());
		return feed;
	}

	/**
	 * Merge serveral searchs.
	 * 
	 * @param pLstFiles
	 * @return
	 */
	public LbcSearch decodeList(List<String> pLstFiles) {
		// TODO Auto-generated method stub
		return null;
	}

}
