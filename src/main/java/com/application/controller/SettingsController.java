package com.application.controller;

import java.beans.PropertyVetoException;
import java.sql.SQLException;
import java.util.List;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.application.db.DirectoryDao;
import com.application.db.ImagesDao;
import com.application.db.TagsDao;
import com.application.db.dto.Directory;
import com.application.db.dto.Tag;

@RestController
public class SettingsController {
  @GetMapping("/settings")
  public String sendForm(Model model) {
    return "settings";
  }

  // Tüm tagları döndürür
  @GetMapping("/getAllTags")
  public List<Tag> getAllTags() throws PropertyVetoException {
    return TagsDao.getAllTags();
  }

  // Tag siler
  @GetMapping("/removeTag")
  public void removeTag(int id) throws PropertyVetoException {
    TagsDao.remove(id);
  }

  // directorileri döndürür
  @GetMapping("/directories")
  public List<Directory> directories() throws PropertyVetoException {
    return DirectoryDao.getDirectories();
  }

  // Seçilen Directory'i aktif hale getirir.
  @GetMapping("/beActive")
  public List<Directory> beActive(int id) throws SQLException, PropertyVetoException {
    return DirectoryDao.beActive(id);
  }

  // id' si verilen directory'i siler
  @GetMapping("/removeDirectory")
  public static void removeDirectory(int id) throws SQLException, PropertyVetoException {
    ImagesDao.removeImages(id);
    DirectoryDao.deleteDirectory(id); 
  }
}
