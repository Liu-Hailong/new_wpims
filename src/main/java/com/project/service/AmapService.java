package com.project.service;

import java.util.List;

public interface AmapService {
    List<String> selectProvinces();

    List<String> selectCitys(String Province);

    List<String> selectDistricts(String City, String Province);
}
