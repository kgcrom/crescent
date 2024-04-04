package org.crescent.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  @Override
  public void addViewControllers(ViewControllerRegistry registry) {
    registry.addViewController("/admin").setViewName("admin/home");
    registry.addViewController("/admin/header").setViewName("admin/header");
    registry.addViewController("/admin/collections").setViewName("admin/collections");
    registry.addViewController("/admin/analysis").setViewName("admin/analysis");
    registry.addViewController("/admin/search").setViewName("admin/search");
    registry.addViewController("/admin/dict").setViewName("admin/dict");
    registry.addViewController("/admin/morph").setViewName("admin/morph");
  }

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("favicon.ico")
            .addResourceLocations("classpath:/static/favicon.ico");
    
    registry.addResourceHandler("css/**")
        .addResourceLocations("classpath:/static/css/");

    registry.addResourceHandler("js/**")
        .addResourceLocations("classpath:/static/js/");
  }
}
