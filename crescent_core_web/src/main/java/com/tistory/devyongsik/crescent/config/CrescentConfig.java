package com.tistory.devyongsik.crescent.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "crescent")
@Component
@Data
public class CrescentConfig {

  private String collectionPath;
}
