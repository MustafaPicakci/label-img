package com.application.controller;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import com.application.config.CommunicationConfig;
import com.application.db.DB;
import com.application.db.DirectoryDao;
import com.application.db.ImagesDao;
import com.application.db.TagsDao;
import com.application.db.dto.Directory;
import com.application.db.dto.General;
import com.application.db.dto.Image;
import com.application.operations.CopyFile;
import com.application.operations.ResizeAndSave;

@RestController
public class IndexController {
  private static final Logger logger = Logger.getLogger(IndexController.class);
  public static String imgDirectory; // son tıklanan resme ait directory bilgisi.
  public static int imgId; // son tıklanan resme ait id bilgisi.
  public static String imgName; // son tıklanan resme ait name bilgisi.
  public static String tags; // silinecek tag
  public static boolean threadIsEnd = false; // işlemin bitip bitmediğini gösterir.

  @RequestMapping("/")
  public ModelAndView index() {

    ModelAndView modelAndView = new ModelAndView();
    modelAndView.setViewName("index");
    return modelAndView;
  }

  // Active Directory'e ait görselleri döndürür.
  @GetMapping("/images")
  public List<Image> GetImage(int id)
      throws ClassNotFoundException, SQLException, IOException, PropertyVetoException {
    return ImagesDao.view(id);
  }

  // yeni directory post edildiğinde directorydeki görselleri resize edip dbye kaydedir.
  @PostMapping("/newImages")
  public void processForm(String d)
      throws IOException, SQLException, ClassNotFoundException, PropertyVetoException {
    DB.connect();
    Directory dir = new Directory();
    dir.setDirectory(d);
    try {
      new Thread(
              new Runnable() {

                @Override
                public void run() {
                  try {
                    ResizeAndSave.resize(dir, 1000, 1000);
                  } catch (IOException | SQLException e) {
                    e.printStackTrace();
                  } finally {
                    threadIsEnd = true;
                    CommunicationConfig.instance.ThreadIsEnd();
                  }
                }
              })
          .start();

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  // Görsele tag eklenir ve tag için klasör oluşturur.
  @PostMapping("/tags")
  public void postTag(General gen) throws SQLException, IOException, PropertyVetoException {
    gen.setTags(gen.getTags().toLowerCase());
    TagsDao.insert(gen);
    CopyFile.createDirectory(imgId, imgDirectory, imgName);
  }

  // Spesifik bir Tag'e sahip görselleri getiririr
  @GetMapping("/img")
  public List<Image> searchTags(String tag) throws IOException, PropertyVetoException {
    return ImagesDao.getImageByTags(tag.toLowerCase());
  }

  // imgByTag componenetini tag parametresi ile oluşturur.Spesifik bir Tag'e sahip görsellerin
  // getirilmesinde "/img" endpointi ile birlikte kullanıldı.
  @PostMapping("/imgTags")
  public RedirectView localRedirect(String tag) throws UnsupportedEncodingException {
    RedirectView redirectView = new RedirectView();
    String encodedId = URLEncoder.encode(tag, "UTF-8").replace("+", "%20");
    redirectView.setUrl("http://localhost:8080/#/imgByTag/" + encodedId.toLowerCase());
    return redirectView;
  }

  // görsele ait tagler döndürüldü
  @GetMapping("/showTags")
  public List<General> showTag(int id, String directory, String name)
      throws SQLException, IOException, PropertyVetoException {
    imgDirectory = directory;
    imgId = id;
    imgName = name;
    return TagsDao.showTags(id);
  }

  // resme ait tag'i siler
  @GetMapping("/delTag")
  public void delTag(int tag_id, int image_id, String tags)
      throws SQLException, IOException, PropertyVetoException {
    TagsDao.deleteRelations(tag_id, image_id);
    CopyFile.deletePicture(imgDirectory, imgName, tags);
  }

  // Görsel siler
  @GetMapping("/removeImage")
  public void removeImage(int image_id, String directory, String name)
      throws SQLException, IOException, PropertyVetoException {
    try {
      List<General> Tags = new ArrayList<>();
      Tags = TagsDao.showTags(image_id);
      for (int i = 0; i < Tags.size(); i++) {
        CopyFile.deletePicture(directory, name, Tags.get(i).getTags());
      }
    } catch (Exception e) {
    	logger.error("error deleting file in the local directory",e);
    }

    ImagesDao.deleteImages(image_id);
  }

  // aktif Directory'i döndürür
  @GetMapping("/active_directory")
  public int getActiveDirectory() throws PropertyVetoException {
    return DirectoryDao.getActiveDirectory();
  }

  // dosya konumunu açar
  @GetMapping("/openContainingFolder")
  public void openContainingFolder() throws IOException {
    new ProcessBuilder("explorer.exe", "/select," + imgDirectory + "\\" + imgName + "").start();
  }
}
