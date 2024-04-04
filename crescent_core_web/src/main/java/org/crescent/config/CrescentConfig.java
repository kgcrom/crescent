package org.crescent.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "crescent")
public class CrescentConfig {
  private String collectionPath;

}
