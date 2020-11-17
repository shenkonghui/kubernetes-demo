package com.harmonycloud.caas.observabilityhook.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author dengyulong
 * @date 2020/04/01
 */
@RestController
@RequestMapping("/healthy")
public class HealthyController {

    @GetMapping
    public String healthy() {
        return "ok";
    }

}
