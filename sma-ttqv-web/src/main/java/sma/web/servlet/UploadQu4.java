package sma.web.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.appengine.api.files.AppEngineFile;
import com.google.appengine.api.files.FileService;
import com.google.appengine.api.files.FileServiceFactory;
import com.google.appengine.api.files.FileWriteChannel;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

/**
 * Load a QU4 in application.<br>
 * application depoyed at http://sma-web.appspot.com/
 * 
 * @author marsolle
 * 
 */
public class UploadQu4 extends HttpServlet {

	/**	 */
	private static final long serialVersionUID = 1L;

	/** Class logger. */
	private static final Logger log = LoggerFactory.getLogger(UploadQu4.class);

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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

				} else {
					String fileName = item.getName();
					log.info("Got an uploaded file: " + item.getFieldName() + ", name = " + fileName);
					if (fileName.toLowerCase().endsWith(".qu4")) {
						// resp.setContentType("application/zip");
						// resp.setHeader("Content-Disposition", "attachment;filename=\"" + "ttqv-cal-files.zip" + "\"");

						InputStream in = item.openStream();
						String path = this.addQu4(in, fileName, user);
						resp.getWriter().println("path=" + path);
						in.close();
					} else {
						resp.getWriter().println("You must provide a QU4 file.");
						resp.getWriter().println("Your file is: " + fileName);
					}

				}
			}
		} catch (Exception e) {
			log.error("Catched error", e);
			throw new ServletException(e);
		}

	}

	private String addQu4(InputStream qu4InStream, String qu4FileName, User pUser) throws IOException {
		// Get a file service
		FileService fileService = FileServiceFactory.getFileService();

		// Create a new Blob file with mime-type "text/plain"
		AppEngineFile file = fileService.createNewBlobFile("application/zip");
		// Open a channel to write to it
		boolean lock = true;
		FileWriteChannel writeChannel = fileService.openWriteChannel(file, lock);

		// Write in file
		ZipOutputStream zout = new ZipOutputStream(Channels.newOutputStream(writeChannel));
		ZipEntry entOut = new ZipEntry(qu4FileName);
		zout.putNextEntry(entOut);
		// copy stream
		IOUtils.copy(qu4InStream, zout);
		zout.closeEntry();
		zout.close();

		// get path
		String path = file.getFullPath();

		// Now finalize
		writeChannel.closeFinally();

		return path;

	}

}
