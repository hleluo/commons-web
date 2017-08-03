package com.monsent.common.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GzipUtils {

	private final static String ENCODE_ISO_8859_1 = "ISO-8859-1";

	public static String compress(String data) throws IOException {
		if (data == null || data.length() == 0) {
			return null;
		}
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		GZIPOutputStream gzip = new GZIPOutputStream(outputStream);
		gzip.write(data.getBytes());
		gzip.close();
		return outputStream.toString(ENCODE_ISO_8859_1);
	}

	public static String decompress(String data) throws IOException {
		if (data == null || data.length() == 0) {
			return null;
		}
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		ByteArrayInputStream inputStream = new ByteArrayInputStream(data.getBytes(ENCODE_ISO_8859_1));
		GZIPInputStream gzip = new GZIPInputStream(inputStream);
		byte[] buffer = new byte[256];
		int n;
		while ((n = gzip.read(buffer)) >= 0) {
			outputStream.write(buffer, 0, n);
		}
		return outputStream.toString();
	}

	public static void main(String[] args) throws IOException {
		String data = "1361321323232113212332112213123";
		String target = GzipUtils.compress(data);
		System.out.println(target);
		System.out.println(GzipUtils.decompress(target));
	}

}
