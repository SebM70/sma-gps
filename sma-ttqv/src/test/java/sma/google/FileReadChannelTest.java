package sma.google;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.channels.Channels;

import org.junit.Test;

import com.google.appengine.api.files.AppEngineFile;
import com.google.appengine.api.files.FileReadChannel;
import com.google.appengine.api.files.FileService;
import com.google.appengine.api.files.FileServiceFactory;
import com.google.appengine.api.files.LockException;

/**
 * Test read file with Google SDK.
 * 
 * @author marsolle
 * 
 */
public class FileReadChannelTest {

	@Test
	public void test1() throws FileNotFoundException, LockException, IOException {
		// Get a file service
		FileService fileService = FileServiceFactory.getFileService();

		// Write more to the file in a separate request:
		AppEngineFile file = new AppEngineFile("path");

		FileReadChannel readChannel = fileService.openReadChannel(file, false);

		// Again, different standard Java ways of reading from the channel.
		BufferedReader reader = new BufferedReader(Channels.newReader(readChannel, "UTF8"));
		String line = reader.readLine();
		

		// Channels.newInputStream(readChannel).r
	
		readChannel.close();

	}

}
