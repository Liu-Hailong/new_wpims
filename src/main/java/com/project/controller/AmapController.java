package com.project.controller;

import com.project.service.AmapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
public class AmapController {

    @Autowired
    private AmapService amapService;

    @RequestMapping("/getProvinces")
    List<String> getProvinces(){
        return amapService.selectProvinces();
    }

    @RequestMapping("/getCitys")
    List<String> getCitys(String province){
        return amapService.selectCitys(province);
    }

    @RequestMapping("/getDistricts")
    List<String> getDistricts(String city, String province){
        return amapService.selectDistricts(city, province);
    }
}
