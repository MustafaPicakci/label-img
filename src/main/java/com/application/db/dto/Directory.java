package com.application.db.dto;

import javax.persistence.Entity;

public class Directory {

  private int id;
  private int is_active;
  private String directory;
  private String searchData;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getIs_active() {
    return is_active;
  }

  public void setIs_active(int is_active) {
    this.is_active = is_active;
  }

  public String getSearchData() {
    return searchData;
  }

  public void setSearchData(String searchData) {
    this.searchData = searchData;
  }

  public String getDirectory() {
    return directory;
  }

  public void setDirectory(String directory) {
    this.directory = directory;
  }
}
