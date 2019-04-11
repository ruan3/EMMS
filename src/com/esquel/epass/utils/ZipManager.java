package com.esquel.epass.utils;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

import android.content.Context;
import android.util.Log;

/**
 * 
 * @author joyaether
 * 
 */
public final class ZipManager {

	// private static final int BUFFER_SIZE = 1024;

	private ZipManager() {

	}

	public static boolean unzip(
			Context context, 
			String zipFile,
			String targetPath, 
			EsquelPassRegion region) {
		
		FileInputStream fin = null;
		GZIPInputStream zin = null;
		BufferedOutputStream bufout = null;
		File file = null;
		
		try {
			fin = new FileInputStream(zipFile);
			zin = new GZIPInputStream(fin);
			if (context != null) {
				file = new File(targetPath + "/" + region.toString() + ".db");
				if (!file.exists()) {
					file.getParentFile().mkdirs();
					file.createNewFile();
				}
				FileOutputStream fout = new FileOutputStream(file);
				bufout = new BufferedOutputStream(fout);
				for (int c = zin.read(); c != -1; c = zin.read()) {
					bufout.write(c);
				}
			}
		} catch (Exception e) {
			Log.e(ZipManager.class.getSimpleName(), "Failed to unzip: " + zipFile, e);
			if (file != null && file.exists()) {
				file.delete();
			}
				
			return false;
		} finally {
			close(fin);
			close(zin);
			close(bufout);
		}
		return true;
	}
	
	private static void close(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (IOException e) {
				// Ignored
			}
		}
	}

}
