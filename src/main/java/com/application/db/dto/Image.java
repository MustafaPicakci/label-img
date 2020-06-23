package com.application.db.dto;

import java.awt.image.BufferedImage;

public class Image {
  private int id;
  private String name;
  private String directory;
  private BufferedImage scaledImg;
  private String data;

  public String getData() {
    return data;
  }

  public void setData(String data) {
    this.data = data;
  }

  public BufferedImage getScaledImg() {
    return scaledImg;
  }

  public void setScaledImg(BufferedImage scaledImg) {
    this.scaledImg = scaledImg;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDirectory() {
    return directory;
  }

  public void setDirectory(String directory) {
    this.directory = directory;
  }
}
