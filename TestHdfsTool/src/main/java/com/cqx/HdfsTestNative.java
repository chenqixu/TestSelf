package com.cqx;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.io.compress.Compression;
import org.apache.hadoop.hbase.io.hfile.AbstractHFileWriter;
import org.apache.hadoop.hbase.io.hfile.CacheConfig;
import org.apache.hadoop.hbase.io.hfile.HFile;
import org.apache.hadoop.hbase.io.hfile.HFileContext;
import org.apache.hadoop.hbase.io.hfile.HFileContextBuilder;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.compress.Compressor;

public class HdfsTestNative {

	public static boolean testCompression(String codec) {
		codec = codec.toLowerCase();

		Compression.Algorithm a;

		try {
			a = Compression.getCompressionAlgorithmByName(codec);
		} catch (IllegalArgumentException e) {
			System.out.println("Codec type: " + codec + " is not known");
			return false;
		}

		try {
			testCompression(a);
			return true;
		} catch (IOException ignored) {
			System.out.println("Can't instantiate codec: " + codec);
			return false;
		}
	}

	private final static Boolean[] compressionTestResults = new Boolean[Compression.Algorithm
			.values().length];
	static {
		for (int i = 0; i < compressionTestResults.length; ++i) {
			compressionTestResults[i] = null;
		}
	}

	public static void testCompression(Compression.Algorithm algo)
			throws IOException {
		if (compressionTestResults[algo.ordinal()] != null) {
			if (compressionTestResults[algo.ordinal()]) {
				return; // already passed test, dont do it again.
			} else {
				// failed.
				throw new IOException("Compression algorithm '"
						+ algo.getName() + "'" + " previously failed test.");
			}
		}

		try {
			Compressor c = algo.getCompressor();
			algo.returnCompressor(c);
			compressionTestResults[algo.ordinal()] = true; // passes
		} catch (Throwable t) {
			compressionTestResults[algo.ordinal()] = false; // failure
			throw new IOException(t);
		}
	}

	protected static Path path = new Path(".hfile-comp-test");

	public static void usage() {

		System.err.println("Usage: CompressionTest <path> "
				+ StringUtils.join(Compression.Algorithm.values(), "|")
						.toLowerCase() + "\n" + "For example:\n" + "  hbase "
				+ HdfsTestNative.class + " file:///tmp/testfile gz\n");
		System.exit(1);
	}

	public static void doSmokeTest(FileSystem fs, Path path, String codec)
			throws Exception {
		Configuration conf = HBaseConfiguration.create();
		HFileContext context = new HFileContextBuilder().withCompression(
				AbstractHFileWriter.compressionByName(codec)).build();
		HFile.Writer writer = HFile.getWriterFactoryNoCache(conf)
				.withPath(fs, path).withFileContext(context).create();
		writer.append(Bytes.toBytes("testkey"), Bytes.toBytes("testval"));
		writer.appendFileInfo(Bytes.toBytes("infokey"),
				Bytes.toBytes("infoval"));
		writer.close();

		HFile.Reader reader = HFile.createReader(fs, path,
				new CacheConfig(conf), conf);
		reader.loadFileInfo();
		byte[] key = reader.getFirstKey();
		boolean rc = Bytes.toString(key).equals("testkey");
		reader.close();

		if (!rc) {
			throw new Exception("Read back incorrect result: "
					+ Bytes.toStringBinary(key));
		}
	}

	public static void main(String[] args) throws Exception {
//		org.apache.hadoop.util.NativeLibraryChecker a;
//		org.apache.hadoop.hbase.util.CompressionTest b;		
		
//		System.out.println("Trying to load the custom-built native-hadoop library...");
//		try{
//			System.loadLibrary("hadoop");
//			System.out.println("Loaded the native-hadoop library");
//		}catch(Exception e){
//			System.out.println("Failed to load native-hadoop with error: " + e);
//			e.printStackTrace();
//		}

//	    String usage = "NativeLibraryChecker [-a|-h]\n"
//	        + "  -a  use -a to check all libraries are available\n"
//	        + "      by default just check hadoop library is available\n"
//	        + "      exit with error code 1 if check failed\n"
//	        + "  -h  print this message\n";
//	    if (args.length > 1 ||
//	        (args.length == 1 &&
//	            !(args[0].equals("-a") || args[0].equals("-h")))) {
//	      System.err.println(usage);
//	      ExitUtil.terminate(1);
//	    }
//	    boolean checkAll = false;
//	    if (args.length == 1) {
//	      if (args[0].equals("-h")) {
//	        System.out.println(usage);
//	        return;
//	      }
//	      checkAll = true;
//	    }
//	    Configuration conf = new Configuration();
//	    boolean nativeHadoopLoaded = NativeCodeLoader.isNativeCodeLoaded();
//	    boolean zlibLoaded = false;
//	    boolean snappyLoaded = false;
//	    // lz4 is linked within libhadoop
//	    boolean lz4Loaded = nativeHadoopLoaded;
//	    boolean bzip2Loaded = Bzip2Factory.isNativeBzip2Loaded(conf);
//	    String hadoopLibraryName = "";
//	    String zlibLibraryName = "";
//	    String snappyLibraryName = "";
//	    String lz4LibraryName = "";
//	    String bzip2LibraryName = "";
//	    if (nativeHadoopLoaded) {
//	      hadoopLibraryName = NativeCodeLoader.getLibraryName();
//	      zlibLoaded = ZlibFactory.isNativeZlibLoaded(conf);
//	      if (zlibLoaded) {
//	        zlibLibraryName = ZlibFactory.getLibraryName();
//	      }
//	      snappyLoaded = NativeCodeLoader.buildSupportsSnappy() &&
//	          SnappyCodec.isNativeCodeLoaded();
//	      if (snappyLoaded && NativeCodeLoader.buildSupportsSnappy()) {
//	        snappyLibraryName = SnappyCodec.getLibraryName();
//	      }
//	      if (lz4Loaded) {
//	        lz4LibraryName = Lz4Codec.getLibraryName();
//	      }
//	      if (bzip2Loaded) {
//	        bzip2LibraryName = Bzip2Factory.getLibraryName(conf);
//	      }
//	    }
//	    System.out.println("Native library checking:");
//	    System.out.printf("hadoop: %b %s\n", nativeHadoopLoaded, hadoopLibraryName);
//	    System.out.printf("zlib:   %b %s\n", zlibLoaded, zlibLibraryName);
//	    System.out.printf("snappy: %b %s\n", snappyLoaded, snappyLibraryName);
//	    System.out.printf("lz4:    %b %s\n", lz4Loaded, lz4LibraryName);
//	    System.out.printf("bzip2:  %b %s\n", bzip2Loaded, bzip2LibraryName);
//	    if ((!nativeHadoopLoaded) ||
//	        (checkAll && !(zlibLoaded && snappyLoaded && lz4Loaded && bzip2Loaded))) {
//	      // return 1 to indicated check failed
//	      ExitUtil.terminate(1);
//	    }
	    if (args.length != 2) {
	    	usage();
	    	System.exit(1);
	    }

	    Configuration conf = new Configuration();
	    Path path = new Path(args[0]);
	    FileSystem fs = path.getFileSystem(conf);
	    try {
	    	doSmokeTest(fs, path, args[1]);
	    } finally {
	        fs.delete(path, false);
	    }
	    System.out.println("SUCCESS");
	}
}
