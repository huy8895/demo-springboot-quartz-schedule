package com.example.demospringbootquartzschedule.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MethodJobService {

  private static final Logger logger = LoggerFactory.getLogger(MethodJobService.class);


  // Thêm một job mới
  public void testMethod() {
    logger.info("start method MethodJobService testMethod");
  }

}