package com.tistory.devyongsik.crescent.admin.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;


@Controller
public class AdminMainController {

  private Logger logger = LoggerFactory.getLogger(AdminMainController.class);

  @RequestMapping("/adminMain")
  public ModelAndView adminMain() throws Exception {
    ModelAndView modelAndView = new ModelAndView();
    modelAndView.setViewName("/admin/main");

    return modelAndView;
  }

  //TODO 사전 생성, 삭제, 변경 기능 구현

}
