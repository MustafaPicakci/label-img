package com.application.db;

import com.application.db.dto.General;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import com.application.db.dto.Tag;

public class TagsDao {
  private static final Logger logger = Logger.getLogger(TagsDao.class);
  // etikete göre görsel getiriyor.

  // image_id ye göre tagları gösteriyor.
  public static List<General> showTags(int id) throws SQLException, PropertyVetoException {
    String sql = "select * From image_tags inner Join tags on tags.id=tag_id  where record_id=?;";

    List<General> result = new ArrayList<>();

    try (Connection con = DB.connect();
        PreparedStatement pstmt = con.prepareStatement(sql)) {
      pstmt.setObject(1, id);
      try (ResultSet rs = pstmt.executeQuery(); ) {
        while (rs.next()) {
          General gen = new General();
          gen.setTags(rs.getString("tags"));
          gen.setTag_id(rs.getInt("tag_id"));
          gen.setImage_id(rs.getInt("record_id"));
          result.add(gen);
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

  public static void insert(General gen) throws PropertyVetoException {
    int tag_id = 0;
    tag_id = tagIsExists(gen.getTags());
    String sql = "";
    if (tag_id > 0) {
      sql =
          "INSERT INTO image_tags(record_id,tag_id) select ?,? where not exists (select 1 from image_tags where record_id=? and tag_id=?) ";
    } else {
      sql = "INSERT INTO tags(tags) VALUES (?)";
    }

    try (Connection con = DB.connect();
        PreparedStatement pstmt = con.prepareStatement(sql)) {
      pstmt.setString(1, gen.getTags());
      if (tag_id > 0) {
        pstmt.setInt(1, gen.getImage_id());
        pstmt.setInt(2, tag_id);
        pstmt.setInt(3, gen.getImage_id());
        pstmt.setInt(4, tag_id);
      }

      pstmt.executeUpdate();
      con.close();
    } catch (SQLException e) {
      logger.error(e);
    }
    if (!(tag_id > 0)) {
      insert(gen);
    }
  }

  public static int tagIsExists(String tag) throws PropertyVetoException {
    String sql = "select id from tags where tags=? limit 1 ";
    int result = 0;
    try (Connection con = DB.connect();
        PreparedStatement pstmt = con.prepareStatement(sql)) {
      pstmt.setString(1, tag);
      try (ResultSet rs = pstmt.executeQuery(); ) {
        while (rs.next()) {
          result = rs.getInt(1);
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

  public static List<Tag> getAllTags() throws PropertyVetoException {
    String sql = "select * from tags ";
    List<Tag> result = new ArrayList<>();
    try (Connection con = DB.connect();
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(sql)) {

      while (rs.next()) {
        Tag tags = new Tag();
        tags.setId(rs.getInt("id"));
        tags.setTags(rs.getString("tags"));
        result.add(tags);
      }
      con.close();
    } catch (SQLException e) {
      logger.error(e);
    }
    return result;
  }

  public static void deleteRelations(int id, int image_id)
      throws SQLException, PropertyVetoException {
    Connection con = DB.connect();
    String sql = "delete  from image_tags where tag_id=? and record_id=?";
    try (PreparedStatement pstmt = con.prepareStatement(sql)) {
      pstmt.setInt(1, id);
      pstmt.setInt(2, image_id);
      pstmt.executeUpdate();
      con.close();
    } catch (SQLException e) {
      logger.error(e);
    }
  }

  public static void remove(int id) throws PropertyVetoException {
    String sql = "DELETE FROM tags WHERE id = ?";

    try (Connection con = DB.connect()) {
      try (PreparedStatement pstmt = con.prepareStatement(sql)) {
        pstmt.setInt(1, id);
        pstmt.executeUpdate();
      }

      con.close();
    } catch (SQLException e) {
      logger.error(e);
    }
  }
}
