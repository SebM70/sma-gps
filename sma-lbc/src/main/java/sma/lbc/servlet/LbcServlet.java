package sma.lbc.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import sma.lbc.mgr.LbcParser;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedOutput;

/**
 * Servlet that translate feed
 * 
 * @author marsolle
 * 
 */
public class LbcServlet extends HttpServlet {

	/** */
	private static final long serialVersionUID = 1524071240081027601L;

	public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
		try {
			LbcParser lbcParser = new LbcParser();

			String sourceUrl = req.getParameter("url");
			if (sourceUrl == null) {
				// default value
				sourceUrl = "http://www.leboncoin.fr/voitures/offres/midi_pyrenees/bonnes_affaires/?f=a&sp=0&th=1&pe=27&q=toyota+hzj+or+toyota+bj+or+toyota+hdj+or+toyota+kzj+or+toyota+lj+or+toyota+hj+or+iltis";
			}
			SyndFeed feed = lbcParser.getFeedFromLbc(sourceUrl);

			String feedType = "atom_0.3";
			// feedType = (feedType != null) ? feedType : _defaultFeedType;
			feed.setFeedType(feedType);

			res.setContentType("application/xml; charset=UTF-8");
			SyndFeedOutput output = new SyndFeedOutput();
			output.output(feed, res.getWriter());
		} catch (Exception ex) {
			String msg = "COULD_NOT_GENERATE_FEED_ERROR";
			log(msg, ex);
			res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, msg);
		}
	}

}
