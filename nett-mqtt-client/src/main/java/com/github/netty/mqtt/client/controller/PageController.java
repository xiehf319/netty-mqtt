package com.github.netty.mqtt.client.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 * 类描述:
 *
 * @author 003300
 * @version 1.0
 * @date 2020/5/14 14:26
 */
@RestController
public class PageController {

    @RequestMapping("/")
    public ModelAndView index() {
        return new ModelAndView("/index");
    }
}
