package org.crescent.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class IndexingUtil {

  private static final Logger logger = LoggerFactory.getLogger(IndexingUtil.class);
  private URL serverURL;
  private final String DEFAULT_SERVER_URL = "http://127.0.0.1:8080/update.devys";
  private Map<String, String> mimeMap;
  private String sourceFileName = "";
  private String fileType = "json";

  public IndexingUtil() {
    initMimeMap();

  }

  // TODO implement client library
  public static void main(String[] args) {
    IndexingUtil p = new IndexingUtil();
    p.exec();
  }

  private void exec() {
    parseArgsAndInit();
    postFiles(sourceFileName);
  }

  private void parseArgsAndInit() {
    String urlStr = System.getProperty("url", DEFAULT_SERVER_URL);
    String collectionName = System.getProperty("collection_name", "sample");

    try {
      String params = "collection_name=" + collectionName;
      serverURL = new URL(urlStr + "?" + params);
    } catch (MalformedURLException e) {
      logger.error("{}", e.getMessage(), e);
    }

    sourceFileName = System.getProperty("file", "");
    fileType = System.getProperty("fileType", "json");
  }

  private void initMimeMap() {
    mimeMap = new HashMap<>();
    mimeMap.put("xml", "text/xml");
    mimeMap.put("json", "application/json");
    mimeMap.put("txt", "text/plain");
    mimeMap.put("log", "text/plain");
  }

  private int postFiles(String srcFileName) {
    int filesPosted = 0;

    File srcFile = new File(srcFileName);
    if (srcFile.isDirectory() && srcFile.canRead()) {
      filesPosted += postDirectory(srcFile);
    } else {
      postFile(srcFile);
      filesPosted++;
    }

    return filesPosted;
  }

  private int postDirectory(File dir) {
		if (dir.isHidden() && !dir.getName().equals(".")) {
			return (0);
		}

    int posted = 0;
    for (File file : dir.listFiles()) {
      postFile(file);
      posted++;
    }

    return posted;
  }

  private void postFile(File file) {
    String mimeType = mimeMap.get(fileType);

    if (mimeType == null) {
      throw new IllegalStateException("Not Supported mime Type : " + fileType);
    }

    InputStream is = null;
    try {
      is = new FileInputStream(file);

      postData(is, (int) file.length(), mimeType);
    } catch (IOException e) {
      logger.error("can't open/read file: {}", file);
    } finally {
      try {
				if (is != null) {
					is.close();
				}
      } catch (IOException e) {
        logger.error("failed to close file: {}", file.getName(), e);
      }
    }
  }

  public boolean postData(InputStream data, Integer length, String type) {
    boolean success = true;

    HttpURLConnection urlc = null;
    try {
      try {
        urlc = (HttpURLConnection) serverURL.openConnection();
        try {
          urlc.setRequestMethod("POST");
        } catch (ProtocolException e) {
          logger.error("HttpURLConnection doesn't support POST", e);
        }
        urlc.setDoOutput(true);
        urlc.setDoInput(true);
        urlc.setUseCaches(false);
        urlc.setAllowUserInteraction(false);
        urlc.setRequestProperty("Content-type", type);

				if (null != length) {
					urlc.setFixedLengthStreamingMode(length);
				}

      } catch (IOException e) {
        logger.error("connection error, {}", serverURL, e);
        success = false;
      }

      OutputStream out = null;
      try {
        out = urlc.getOutputStream();
        pipe(data, out);
      } catch (IOException e) {
        logger.error("IOException while posting data: ", e);
        success = false;
      } finally {
        try {
					if (out != null) {
						out.close();
					}
        } catch (IOException x) { /*NOOP*/ }
      }

      InputStream in = null;
      BufferedReader br = null;
      try {
        if (HttpURLConnection.HTTP_OK != urlc.getResponseCode()) {
          logger.warn("server returned an error : {} {}", urlc.getResponseCode(),
              urlc.getResponseMessage());
          success = false;
        }

        in = urlc.getInputStream();
        br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));

        String logLine = "";
        while ((logLine = br.readLine()) != null) {
          logger.info("log from server... {}", logLine);
        }

      } catch (IOException e) {
        logger.error("IOException while reading response", e);
        success = false;
      } finally {
        try {
					if (br != null) {
						br.close();
					}
					if (in != null) {
						in.close();
					}
        } catch (IOException x) { /*NOOP*/ }
      }
    } finally {
			if (urlc != null) {
				urlc.disconnect();
			}
    }
    return success;
  }

  private void pipe(InputStream source, OutputStream dest) throws IOException {
    byte[] buf = new byte[1024];
    int read = 0;
    while ((read = source.read(buf)) >= 0) {
			if (null != dest) {
				dest.write(buf, 0, read);
			}
    }
		if (null != dest) {
			dest.flush();
		}
  }
}
