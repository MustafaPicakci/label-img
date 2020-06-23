package com.application.db.dto;

public class Tag {
  private int id;
  private String name;
  private String tags;
  /*private int image_id;

  public int getImage_id() {
    return image_id;
  }

  public void setImage_id(int image_id) {
    this.image_id = image_id;
  }*/

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

  public String getTags() {
    return tags;
  }

  public void setTags(String tags) {
    this.tags = tags;
  }
}
