package com.application.operations;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.List;

import com.application.db.TagsDao;
import com.application.db.dto.General;

public class CopyFile {
  private static Path sourceFile;
  private static Path destFile;
  public static File IMG_FOLDER = null;

  public static void createDirectory(int id, String directory, String name)
      throws SQLException, IOException, PropertyVetoException {
    List<General> tags = TagsDao.showTags(id);
    for (int i = 0; i < tags.size(); i++) {
      IMG_FOLDER = new File(directory + "\\" + (tags.get(i).getTags()));
      if (!IMG_FOLDER.exists()) {
        IMG_FOLDER.mkdir();
      }
      copyFileUsingApacheCommonsIO(directory + "\\" + name, IMG_FOLDER.getPath() + "\\" + name);
    }
  }

  public static void copyFileUsingApacheCommonsIO(String source, String dest) throws IOException {
    sourceFile = Paths.get(source);
    destFile = Paths.get(dest);
    Files.copy(sourceFile, destFile, StandardCopyOption.REPLACE_EXISTING);
  }

  public static void deletePicture(String directory, String name, String tag) throws IOException {
    Path path = Paths.get(directory + "\\" + tag + "\\" + name);
    Files.delete(path);
    File folder = new File(directory + "\\" + tag);
    if (!(folder.list().length > 0)) {
      Files.delete(Paths.get(folder.toString()));
    }
  }
}
