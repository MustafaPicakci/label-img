package com.application.db;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.application.db.dto.Directory;

public class DirectoryDao {
  private static final Logger logger = Logger.getLogger(DirectoryDao.class);

  //yeni directory kaydeder. 
  public static void insert_directory(String directory, boolean is_null)
      throws PropertyVetoException {
    String sql;
    if (is_null) {
      sql = "INSERT INTO directories(directory,is_active) VALUES (?,1)";
    } else {
      sql =
          "INSERT INTO directories(directory,is_active) SELECT ?,0 WHERE NOT EXISTS(SELECT 1 FROM directories WHERE directories.directory=?)";
    }

    try (Connection con = DB.connect();
        PreparedStatement pstmt = con.prepareStatement(sql)) {
      pstmt.setString(1, directory);
      if (!is_null) {
        pstmt.setString(2, directory);
      }
      pstmt.executeUpdate();
      con.close();
    } catch (SQLException e) {
      logger.error(e);
    }
  }

  //directoy tablosunun boş olma durumunu kontrol eder.
  public static boolean directory_is_null() throws SQLException, PropertyVetoException {
    boolean result = true;
    Connection con = DB.connect();
    String sql = "select * from directories ";
    PreparedStatement psmt = con.prepareStatement(sql);

    ResultSet rs = psmt.executeQuery();
    if (rs.next()) {
      result = false;
      con.close();
      return result;
    } else {
      con.close();
      return result;
    }
  }

  //Tüm directoy'leri pasif hale getirir
  public static void resetDirectories() throws SQLException, PropertyVetoException {
    Connection con = DB.connect();
    String sql = "update directories set is_active= 0";
    try (PreparedStatement pstmt = con.prepareStatement(sql)) {
      pstmt.executeUpdate();
    } catch (SQLException e) {
      logger.error(e);
    }
    con.close();
  }

  //id'si verilen diectory'i aktifleştirir ve tüm directory'leri döndürür
  public static List<Directory> beActive(int id) throws SQLException, PropertyVetoException {
    resetDirectories();

    String sql = "update directories set is_active=1 where id=? ";
    try (Connection con = DB.connect();
        PreparedStatement pstmt = con.prepareStatement(sql)) {
      pstmt.setInt(1, id);
      pstmt.executeUpdate();
      con.close();
    } catch (SQLException e) {
      logger.error(e);
    }

    return getDirectories();
  }

  //aktif directory id'sini döndürür.
  public static int getActiveDirectory() throws PropertyVetoException {
    String sql = "select id from directories where is_active=1 ";
    int result = 0;
    try (Connection con = DB.connect();
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(sql)) {

      while (rs.next()) {

        result = rs.getInt("id");
      }
      con.close();
    } catch (SQLException e) {
      logger.error(e);
    }
    return result;
  }

  //görsele sahip olmayan directoriler db ye kaydedilmiş ise onları siler.
  public static void delWrongRecord() throws SQLException, PropertyVetoException {
    Connection con = DB.connect();
    String sql =
        "DELETE FROM directories WHERE NOT EXISTS(SELECT NULL FROM images WHERE images.directory = directories.directory);";
    try (PreparedStatement pstmt = con.prepareStatement(sql)) {
      pstmt.executeUpdate();
    } catch (SQLException e) {
      logger.error(e);
    }
    con.close();
  }

  //tüm directory'leri döndürür
  public static List<Directory> getDirectories() throws PropertyVetoException {
    String sql = "select * from directories ";
    List<Directory> result = new ArrayList<>();
    try (Connection con = DB.connect();
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(sql)) {

      while (rs.next()) {
        Directory dir = new Directory();
        dir.setId(rs.getInt("id"));
        dir.setDirectory(rs.getString("directory"));
        dir.setIs_active(rs.getInt("is_active"));
        result.add(dir);
      }
      con.close();
    } catch (SQLException e) {
      logger.error(e);
    }
    return result;
  }

  //directories tablosunda directory siler.
  public static void deleteDirectory(int directory_id) throws SQLException, PropertyVetoException {
    Connection con = DB.connect();
    String sql = "delete from directories where directories.id=?";
    try (PreparedStatement pstmt = con.prepareStatement(sql)) {
      pstmt.setInt(1, directory_id);
      pstmt.executeUpdate();
    } catch (Exception e) {
      logger.error(e);
    }
  }
}
