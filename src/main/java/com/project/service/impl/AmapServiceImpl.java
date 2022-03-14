package com.project.service.impl;

import com.project.dao.AmapDao;
import com.project.service.AmapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AmapServiceImpl implements AmapService {
    @Autowired
    private AmapDao amapDao;

    @Override
    public List<String> selectProvinces() {
        return amapDao.selectProvinces();
    }

    @Override
    public List<String> selectCitys(String Province) {
        return amapDao.selectCitys(Province);
    }

    @Override
    public List<String> selectDistricts(String City, String Province) {
        return amapDao.selectDistricts(City, Province);
    }
}
