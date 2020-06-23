package com.application.operations;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.apache.log4j.Logger;
import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Method;
import org.imgscalr.Scalr.Mode;

import com.application.config.CommunicationConfig;
import com.application.db.DB;
import com.application.db.DirectoryDao;
import com.application.db.ImagesDao;
import com.application.db.dto.Directory;
import com.application.db.dto.Image;

public class ResizeAndSave {
	 private static final Logger logger = Logger.getLogger(ResizeAndSave.class);
  private static void saveImage(String d, String originalPath, BufferedImage scaledImg)
      throws SQLException, PropertyVetoException {
    Image i = new Image();
    i.setDirectory(d);
    i.setName(originalPath.substring(originalPath.lastIndexOf("\\") + 1));
    i.setScaledImg(scaledImg);
    ImagesDao.insert(i);
    boolean directory_is_null = DirectoryDao.directory_is_null();
    DirectoryDao.insert_directory(d, directory_is_null);
    DirectoryDao.delWrongRecord();
    CommunicationConfig.instance.currentDataControl();
  }

  public static void resize(Directory d, int width, int height) throws IOException, SQLException {
    boolean value;
    if (d.getDirectory() != null) {
      File folder = new File(d.getDirectory());
      File[] files = folder.listFiles();
      try {
        for (File file : files) {
          if (file.isFile()) {

            String originalPath = file.toString();
            String name = originalPath.substring(originalPath.lastIndexOf("\\") + 1);
            String fileFormat =
                originalPath.substring(originalPath.lastIndexOf(".") + 1).toLowerCase();
            
            if (fileFormat.equals("jpg")
                || fileFormat.equals("png")
                || fileFormat.equals("bmp")
                || fileFormat.equals("jpeg")) {
              value = ImagesDao.isExists(originalPath, name);
              if (value != true) {
                ImageInputStream stream = ImageIO.createImageInputStream(new File(originalPath));
                Iterator<ImageReader> readers = ImageIO.getImageReaders(stream);

                BufferedImage image[] = null;
                BufferedImage combined = null;
                Rectangle sourceRegion = null;
                if (readers.hasNext()) {

                  ImageReader reader = readers.next();
                  reader.setInput(stream, true);
                  int imgW = reader.getWidth(0);
                  int imgH = reader.getHeight(0);
                  if (imgW * imgH > 10000 * 10000) {
                    int j = 0;
                    image = new BufferedImage[5];

                    for (int i = 0; i < 5; i++) {

                      sourceRegion =
                          new Rectangle(j, 0, imgW / 5, imgH); // The region you want to extract
                      j += imgW / 5;
                      ImageReadParam param = reader.getDefaultReadParam();
                      param.setSourceRegion(sourceRegion); // Set region

                      image[i] = reader.read(0, param); // Will read only the region specified
                      image[i] =
                          Scalr.resize(image[i], Method.SPEED, Mode.AUTOMATIC, width, height);
                    }
                    combined = joinBufferedImage(image);
                  } else {
                    BufferedImage img = ImageIO.read(new File(originalPath));
                    combined = Scalr.resize(img, Method.SPEED, Mode.AUTOMATIC, width, height);
                    img = null;
                  }
                }
                saveImage(d.getDirectory(), originalPath, dropAlphaChannel(combined));
                combined = null;
                image = null;
                sourceRegion = null;
                stream = null;
                readers = null;
              }
            }
          }
        }
      } catch (Exception e) {
        CommunicationConfig.instance.sendErrorMessage("Lütfen geçerli bir dizin giriniz!");
        logger.error(e);
      }
    }
    System.gc();
  }

  public static BufferedImage joinBufferedImage(BufferedImage[] image) {
    int offset = 2;
    int width = 0;
    for (int i = 0; i < 5; i++) {
      width += image[i].getWidth();
    }
    width += offset;

    int height = image[0].getHeight() + offset;
    BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    Graphics2D g2 = newImage.createGraphics();
    Color oldColor = g2.getColor();
    g2.setPaint(Color.BLACK);
    g2.fillRect(0, 0, width, height);
    g2.setColor(oldColor);

    for (int i = 0; i < 5; i++) {
      g2.drawImage(image[i], null, getRectangeWidth(image, i), 0);
    }
    g2.dispose();
    return newImage;
  }

  public static int getRectangeWidth(BufferedImage[] image, int value) {
    int result = 0;
    if (value > 0) {
      for (int i = 0; i < value; i++) {
        result += image[i].getWidth();
      }
      return result;
    }
    return 0;
  }

  public static BufferedImage dropAlphaChannel(BufferedImage src) {
    BufferedImage convertedImg =
        new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_RGB);
    convertedImg.getGraphics().drawImage(src, 0, 0, null);

    return convertedImg;
  }
}
