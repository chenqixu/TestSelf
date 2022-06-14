package com.bussiness.bi.bigdata.hdfs;

import java.io.IOException;
import java.io.InputStream;

import org.apache.hadoop.io.compress.BlockDecompressorStream;
import org.apache.hadoop.io.compress.Decompressor;

public class LzopInputStream extends BlockDecompressorStream {

	public LzopInputStream(InputStream in, Decompressor decompressor,
			int bufferSize) throws IOException {
		super(in, decompressor, bufferSize);
		// other
	}

	  /**
	   * Read checksums and feed compressed block data into decompressor.
	 * @return 
	   */
	  @Override
	  protected int getCompressedData() throws IOException {
		  // other
		  return 0;
	  }
}
