package com.bussiness.bi.bigdata.hdfs;

import java.io.IOException;

import org.apache.hadoop.io.compress.Decompressor;

public class LzoDecompressor implements Decompressor {

	@Override
	public void setInput(byte[] b, int off, int len) {

	}

	@Override
	public boolean needsInput() {
		return false;
	}

	@Override
	public void setDictionary(byte[] b, int off, int len) {

	}

	@Override
	public boolean needsDictionary() {
		return false;
	}

	@Override
	public boolean finished() {
		return false;
	}

	@Override
	public int decompress(byte[] b, int off, int len) throws IOException {
		return 0;
	}

	@Override
	public int getRemaining() {
		return 0;
	}

	@Override
	public void reset() {

	}

	@Override
	public void end() {

	}

}
