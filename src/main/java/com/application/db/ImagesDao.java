package com.application.db;

import java.awt.image.BufferedImage;
import java.beans.PropertyVetoException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import javax.imageio.ImageIO;
import org.apache.log4j.Logger;

import com.application.db.dto.Image;

public class ImagesDao {
  private static final Logger logger = Logger.getLogger(ImagesDao.class);

  public static void insert(Image img) {
    try {
      BufferedImage image = img.getScaledImg();
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      ImageIO.write(image, "jpg", bos);
      byte[] buf = new byte[1024];
      InputStream is = new ByteArrayInputStream(bos.toByteArray());
      for (int readNum; (readNum = is.read(buf)) != -1; ) {
        bos.write(buf, 0, readNum);
      }
      is.close();
      Connection con = DB.connect();
      String sql = "INSERT INTO images(image,directory,name) VALUES(?,?,?)";
      PreparedStatement psmt = con.prepareStatement(sql);
      psmt.setBytes(1, bos.toByteArray());
      psmt.setString(2, img.getDirectory());
      psmt.setString(3, img.getName());
      psmt.executeUpdate();
      con.close();
    } catch (Exception e) {
      logger.error(e);
    }
  }

  public static List<Image> view(int id)
      throws SQLException, IOException, ClassNotFoundException, PropertyVetoException {

    String sql =
        "select images.id,images.directory,images.image,images.name from images inner join directories on directories.directory=images.directory where directories.id=?";

    List<Image> result = new ArrayList<>();
    InputStream is = null;
    OutputStream os = null;
    try (Connection con = DB.connect();
        PreparedStatement pstmt = con.prepareStatement(sql)) {
      pstmt.setInt(1, id);

      try (ResultSet rs = pstmt.executeQuery(); ) {

        while (rs.next()) {
          Image img = new Image();

          is = rs.getBinaryStream("image");
          byte[] content = new byte[1024];
          ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

          int bytesRead = 0;
          while ((bytesRead = is.read(content)) != -1) {
            outputStream.write(content, 0, bytesRead);
          }

          byte[] imageBytes = outputStream.toByteArray();
          String base64Image = Base64.getEncoder().encodeToString(imageBytes);
          img.setData(base64Image);
          img.setDirectory(rs.getString("directory"));
          img.setId(rs.getInt("id"));
          img.setName(rs.getString("name"));
          result.add(img);
        }
      } catch (Exception e) {
        logger.error(e);
      }
      con.close();
    } catch (SQLException e) {
      logger.error(e);
    } finally {
      try {
        if (is != null) is.close();
        if (os != null) os.close();
      } catch (Exception e) {
        logger.error(e);
      }
    }
    return result;
  }

  public static List<Image> getImageByTags(String tags) throws IOException, PropertyVetoException {
    String sql =
        "select * From images  join image_tags  on images.id=image_tags.record_id  join tags on image_tags.tag_id=tags.id where tags=?";
    List<Image> result = new ArrayList<>();
    InputStream is = null;

    try (Connection con = DB.connect();
        PreparedStatement pstmt = con.prepareStatement(sql)) {
      pstmt.setObject(1, tags);
      try (ResultSet rs = pstmt.executeQuery(); ) {
        while (rs.next()) {
          Image img = new Image();
          is = rs.getBinaryStream("image");
          byte[] content = new byte[1024];
          ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

          int bytesRead = 0;
          while ((bytesRead = is.read(content)) != -1) {
            outputStream.write(content, 0, bytesRead);
          }

          byte[] imageBytes = outputStream.toByteArray();
          String base64Image = Base64.getEncoder().encodeToString(imageBytes);
          img.setData(base64Image);
          img.setDirectory(rs.getString("directory"));
          img.setId(rs.getInt("id"));
          img.setName(rs.getString("name"));
          result.add(img);
        }
      } catch (SQLException e) {
        logger.error(e);
      }
      con.close();
    } catch (SQLException e) {
      logger.error(e);
    }
    return result;
  }

  public static boolean isExists(String directory, String name)
      throws SQLException, PropertyVetoException {
    boolean result = false;
    Connection con = DB.connect();
    String sql = "select directory from images where directory=? and name=?";
    PreparedStatement psmt = con.prepareStatement(sql);
    psmt.setString(1, directory.substring(0, directory.lastIndexOf("\\")));
    psmt.setString(2, name);

    ResultSet rs = psmt.executeQuery();
    if (rs.next()) {
      result = true;
      con.close();
      return result;
    } else {
      con.close();
      return result;
    }
  }

  // spesifik bir directory'e sahip son görsel silindiğinde directories tablosundaki kaydın da
  // dilinmesi sağlandı.
  public static void deleteRelations(int image_id) throws SQLException, PropertyVetoException {
    Connection con = DB.connect();
    String sql =
        "delete from directories where (directories.directory=(select directory from images where images.id=?)) and (select count(images.directory) from images group by images.directory having count(images.directory)=1)";
    try (PreparedStatement pstmt = con.prepareStatement(sql)) {
      pstmt.setInt(1, image_id);
      pstmt.executeUpdate();
    } catch (Exception e) {
      logger.error(e);
    }
  }

  // directory silindiğinde o directory'e sahip tüm görselleri siler.
  public static void removeImages(int directory_id) throws SQLException, PropertyVetoException {
    Connection con = DB.connect();
    String sql =
        "delete from images where (images.directory=(select directory from directories where directories.id=?))";
    try (PreparedStatement pstmt = con.prepareStatement(sql)) {
      pstmt.setInt(1, directory_id);
      pstmt.executeUpdate();
    } catch (Exception e) {
      logger.error(e);
    }
  }

  // id'si verilen görseli siler.
  public static void deleteImages(int id) throws SQLException, PropertyVetoException {
    deleteRelations(id);
    Connection con = DB.connect();
    String sql = "delete  from images where id=?";
    try (PreparedStatement pstmt = con.prepareStatement(sql)) {
      pstmt.setInt(1, id);
      pstmt.executeUpdate();
    } catch (SQLException e) {
      logger.error(e);
    }
    con.close();
  }
}
