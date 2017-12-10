package sma.web.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sma.gps.cal.CalOzi;
import sma.gps.cal.CalTtqv;
import sma.gps.cal.Calibration;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

/**
 * Conver zip with map to a zip with cal.<br>
 * application depoyed at http://sma-web.appspot.com/
 * 
 * @author marsolle
 * 
 */
public class ConvertOziMapToTtqv extends HttpServlet {

	/**	 */
	private static final long serialVersionUID = 1L;

	/** Class logger. */
	private static final Logger log = LoggerFactory.getLogger(ConvertOziMapToTtqv.class);

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		boolean useOnly4Corners = false;
		// (req.getParameter("useOnly4Corners") != null);
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		log.info("doPost, user={}", user);
		ServletFileUpload upload = new ServletFileUpload();
		try {
			FileItemIterator iterator = upload.getItemIterator(req);
			while (iterator.hasNext()) {
				FileItemStream item = iterator.next();
				if (item.isFormField()) {
					log.info("Got a form field: " + item.getFieldName());
					if ("useOnly4Corners".equals(item.getFieldName())) {
						useOnly4Corners = true;
					}
				} else {
					String fileName = item.getName();
					log.info("Got an uploaded file: " + item.getFieldName() + ", name = " + fileName);
					if (fileName.toLowerCase().endsWith(".zip")) {
						resp.setContentType("application/zip");
						resp.setHeader("Content-Disposition", "attachment;filename=\"" + "ttqv-cal-files.zip" + "\"");

						InputStream in = item.openStream();
						ZipOutputStream zout = new ZipOutputStream(resp.getOutputStream());
						this.convertZipFile(in, zout, useOnly4Corners);
						in.close();
						zout.close();
					} else {
						resp.getWriter().println("You must provide a zip file.");
						resp.getWriter().println("Your file is: " + fileName);
					}

				}
			}
		} catch (Exception e) {
			log.error("Catched error", e);
			throw new ServletException(e);
		}

	}

	private void convertZipFile(InputStream in, ZipOutputStream zout, boolean useOnly4Corners) throws IOException,
			ServletException {
		ZipInputStream zin = new ZipInputStream(in);
		ZipEntry entry;

		int nbFileRead = 0;

		while ((entry = zin.getNextEntry()) != null) {
			String inFileName = entry.getName();
			log.info("decoding {}", inFileName);
			if (inFileName.toLowerCase().endsWith(".map")) {
				nbFileRead++;
				// pResp.getWriter().println(entry);

				CalOzi ozi = CalOzi.readOziCalibration(zin, inFileName, useOnly4Corners);
				// pResp.getWriter().println(ozi.getCalCoords());

				// Create new TTQV cal
				CalTtqv ttqv = CalTtqv.CreateCalFromOzi(ozi, true);

				String calFileName = ttqv.getCalFileName();
				log.info("adding file {}, version {}", calFileName, Calibration.VERSION);
				ZipEntry entOut = new ZipEntry(calFileName);
				zout.putNextEntry(entOut);

				ttqv.saveFile(zout);

				zout.closeEntry();

			} else {
				log.warn("ignoring {}", inFileName);
			}
			zin.closeEntry();
		}
		if (nbFileRead == 0) {
			throw new ServletException("Not a single calibration file decoded!");
		}
		zin.close();
	}

}
