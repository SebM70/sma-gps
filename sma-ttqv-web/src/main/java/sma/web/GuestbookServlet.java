package sma.web;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class GuestbookServlet extends HttpServlet {
	/** */
	private static final long serialVersionUID = 1L;

	private static final Logger log = Logger.getLogger(GuestbookServlet.class.getName());

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();

		log.info("user=" + user);

		if (user != null) {
			resp.setContentType("text/plain");
			resp.getWriter().println("Salut, " + user.getNickname());
			resp.getWriter().println("Accentuée");
		} else {
			resp.sendRedirect(userService.createLoginURL(req.getRequestURI()));
		}
	}

}
