package sma.util.access;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

/**
 * FileChannel that can map on a byte array.
 * 
 * @author marsolle
 * 
 */
public class MemoryChannel extends FileChannel {

	byte[] bytes;

	public MemoryChannel(byte[] pBytes) {
		super();
		bytes = pBytes;
	}

	@Override
	public int read(ByteBuffer pDst) throws IOException {
		return this.read(pDst, 0);
	}

	@Override
	public long read(ByteBuffer[] pDsts, int pOffset, int pLength) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int write(ByteBuffer pSrc) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long write(ByteBuffer[] pSrcs, int pOffset, int pLength) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long position() throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public FileChannel position(long pNewPosition) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long size() throws IOException {
		return bytes.length;
	}

	@Override
	public FileChannel truncate(long pSize) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void force(boolean pMetaData) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public long transferTo(long pPosition, long pCount, WritableByteChannel pTarget) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long transferFrom(ReadableByteChannel pSrc, long pPosition, long pCount) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int read(ByteBuffer pDst, long pPosition) throws IOException {
		int nbBytes = pDst.capacity();
		// byte[] bytesToRead = new byte[nbBytes];
		// System.arraycopy(bytes, (int) pPosition, bytesToRead, 0, nbBytes);
		// pDst.put(bytesToRead);
		pDst.put(bytes, (int) pPosition, nbBytes);
		return nbBytes;
	}

	@Override
	public int write(ByteBuffer pSrc, long pPosition) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public MappedByteBuffer map(MapMode pMode, long pPosition, long pSize) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FileLock lock(long pPosition, long pSize, boolean pShared) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FileLock tryLock(long pPosition, long pSize, boolean pShared) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void implCloseChannel() throws IOException {
		// TODO Auto-generated method stub

	}

}
