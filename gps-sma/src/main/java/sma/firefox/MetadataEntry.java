package sma.firefox;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MetadataEntry {
	/** Log4J */
	private static final Logger sLog = LoggerFactory.getLogger(MetadataEntry.class);

	/** int parameters */
	String mRequest;
	String info;
	int mBlockSize;
	int magic, location, fetchcount    , fetchtime     , modifytime    ,expiretime ,
		datasize      ,mRequestsize   , mMetaDataSize     ;

	private String httpResponse = "";
	
	public MetadataEntry(byte[] buf) throws IOException {
		ByteArrayInputStream bais = new ByteArrayInputStream(buf);
		//InputStreamReader isr = new InputStreamReader(bais, "UTF-16BE");
		sLog.debug("buf.length=" + buf.length);
		DataInputStream dis = new DataInputStream(bais);
		// DataInput
		// mHeaderVersion
		magic = dis.readInt();
		//sLog.debug("mHeaderVersion " + Integer.toHexString(magic));
		// mMetaLocation
		location = dis.readInt();
		sLog.debug("mMetaLocation " + Integer.toHexString(location));
		// mFetchCount
		fetchcount = dis.readInt();
		sLog.debug("fetchcount = " + fetchcount);
		//sLog.info("fetchcount = " + dis.readUnsignedShort() + " " + dis.readUnsignedShort());
		// PRUint32        mLastFetched;
		fetchtime = dis.readInt();
		// PRUint32        mLastModified;
		modifytime = dis.readInt();
		
		// PRUint32        mExpirationTime;
		expiretime = dis.readInt();
		
		// PRUint32        mDataSize;
		datasize = dis.readInt();
		sLog.debug("mDataSize = " + datasize );

		// PRUint32        mKeySize;
		mRequestsize = dis.readInt();
		sLog.debug("mKeySize = " + mRequestsize);
		
		
		// PRUint32        mMetaDataSize;
		mMetaDataSize = dis.readInt();
		sLog.debug("mMetaDataSize = " + mMetaDataSize);
		
//		int totSize = 9*4 + mRequestsize + mMetaDataSize;
//		sLog.debug("totSize = " + totSize);
		
		
		// char*           Key()     
		// ex : HTTP:http://java.sun.com/j2se/1.5.0/docs/api/stylesheet.css
		if (this.mRequestsize > 0) {
			byte[] bRequest = new byte[this.mRequestsize];
			dis.read(bRequest);
			mRequest = new String(bRequest, 0, this.mRequestsize - 1);
			
			sLog.debug(mRequest);	
		} else {
			mRequest = "";
		}
			
		

		
		// meta data
		if (this.mMetaDataSize > 0) {
			// docshell:classified.1.request-method.GET.response-head.HTTP/1.1 200 OK
			// or:
			// request-method.GET.response-head.HTTP/1.1 200 OK
			byte[] bMeta = new byte[this.mMetaDataSize];
			dis.read(bMeta);
			// pairs delimited but null
			String[] allMeta = (new String(bMeta)).split("\0");
			for (int i = 0; (i+1) < allMeta.length; i+=2) {
				String name = allMeta[i];
				if ("response-head".equals(name)) {
					String header = allMeta[i+1];
					//sLog.debug(header.split("\n")[0].split(" ")[1]);
					String[] tRep1 = header.split("\n")[0].split(" ");
					if (tRep1.length > 1) {
						this.httpResponse = tRep1[1];
						sLog.debug("httpResponse = " + this.httpResponse);
					} else {
						sLog.warn("header not well formed ! =" + header);
					}
				}
			}
			
			
		}
		
		
		//System.out.println(cbuf);
		
		dis.close();
		bais.close();
	}

	
	public MetadataEntry(int blockSize) {
		this.mBlockSize=blockSize;
	}


	public final static int swabInt(int v) {
	    return  (v >>> 24) | (v << 24) | 
	      ((v << 8) & 0x00FF0000) | ((v >> 8) & 0x0000FF00);
	    }


	public void read(byte[] buf, FileInputStream fis) throws IOException {

		// TODO Auto-generated constructor stub
		ByteArrayInputStream bais = new ByteArrayInputStream(buf);
		//InputStreamReader isr = new InputStreamReader(bais, "UTF-16BE");
		sLog.info("buf.length=" + buf.length);
		DataInputStream dis = new DataInputStream(bais);
		// DataInput
		// mHeaderVersion
		magic = dis.readInt();
		sLog.info("mHeaderVersion " + Integer.toHexString(magic));
		// mMetaLocation
		location = dis.readInt();
		sLog.info("mMetaLocation " + Integer.toHexString(location));
		// mFetchCount
		fetchcount = dis.readInt();
		sLog.info("fetchcount = " + fetchcount);
		//sLog.info("fetchcount = " + dis.readUnsignedShort() + " " + dis.readUnsignedShort());
		// PRUint32        mLastFetched;
		fetchtime = dis.readInt();
		// PRUint32        mLastModified;
		modifytime = dis.readInt();
		
		// PRUint32        mExpirationTime;
		expiretime = dis.readInt();
		
		// PRUint32        mDataSize;
		datasize = dis.readInt();
		sLog.info("mDataSize = " + datasize );

		// PRUint32        mKeySize;
		mRequestsize = dis.readInt();
		sLog.info("mKeySize = " + mRequestsize);
		
		
		// PRUint32        mMetaDataSize;
		mMetaDataSize = dis.readInt();
		sLog.info("mMetaDataSize = " + mMetaDataSize);
		
		int totSize = 9*4 + mRequestsize + mMetaDataSize;
		sLog.info("totSize = " + totSize);
		
		if (totSize > mBlockSize) {
			// requires additional blocks to read
			int nbSupBlk = totSize/mBlockSize;
			byte[] newBuf = new byte[mBlockSize];
			for (int i = 0; i < nbSupBlk; i++) {
				fis.read(newBuf);
			}
		}
		
		
		// char*           Key()     
		// ex : HTTP:http://java.sun.com/j2se/1.5.0/docs/api/stylesheet.css
		
		//dis.readUTF();
		//sLog.info("dis.available()=" + dis.available());
		int reste = dis.available();
		System.out.print("[");
		for (int i = 0; i < reste; i++) {
			int b = dis.read();
			if (b == 0) {
				// end of string
				i = reste;
			} else {
				char[] c = {'�'};
				c[0] = (char) b;
				System.out.print(c);
			}
		}
		System.out.println("]");
		
		
		//System.out.println(cbuf);
		
		dis.close();
		bais.close();
			
		
	}


	public String getMRequest() {
		return mRequest;
	}


	public String getHttpResponse() {
		return httpResponse;
	}

}
