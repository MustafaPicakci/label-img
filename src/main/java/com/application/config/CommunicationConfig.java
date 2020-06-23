package com.application.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@Configuration
public class CommunicationConfig {

  public static CommunicationConfig instance;
  @Autowired SimpMessagingTemplate template;

  public CommunicationConfig() {
    instance = this;
  }

  public void currentDataControl() {
    template.convertAndSend("/topic/control", true);
  }

  public void ThreadIsEnd() {
    template.convertAndSend("/topic/threadIsEnd", true);
  }

  public void sendErrorMessage(String message) {
    template.convertAndSend("/topic/error", message);
  }
}
