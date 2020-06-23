package com.application;

import java.io.File;

import org.apache.log4j.Logger;

import com.application.db.DB;

public class RuntimeProcess {

  public static final File DB_FOLDER = new File("./resources/db");
  public static final String DB_FILE_PATH = "./resources/db/imgdb.db";
  private static final Logger logger = Logger.getLogger(RuntimeProcess.class);
  public static void setup() {
    if (!DB_FOLDER.exists()) {
      DB_FOLDER.mkdir();
    }

    DB.setup();
    loadBrowser();
  }

  public static void loadBrowser() {
    String url = "http://localhost:8080";
    String os = System.getProperty("os.name").toLowerCase();
    Runtime rt = Runtime.getRuntime();

    try {

      if (os.indexOf("win") >= 0) {

        // this doesn't support showing urls in the form of "page.html#nameLink"
        rt.exec("rundll32 url.dll,FileProtocolHandler " + url);

      } else if (os.indexOf("mac") >= 0) {

        rt.exec("open " + url);

      } else if (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0) {

        // Do a best guess on unix until we get a platform independent way
        // Build a list of browsers to try, in this order.
        String[] browsers = {
          "epiphany", "firefox", "mozilla", "konqueror", "netscape", "opera", "links", "lynx"
        };

        // Build a command string which looks like "browser1 "url" || browser2 "url" ||..."
        StringBuffer cmd = new StringBuffer();
        for (int i = 0; i < browsers.length; i++)
          cmd.append((i == 0 ? "" : " || ") + browsers[i] + " \"" + url + "\" ");

        rt.exec(new String[] {"sh", "-c", cmd.toString()});

      } else {
        return;
      }
    } catch (Exception e) {
     logger.error("load browser failed",e);
    }
  }
}
