package com.project.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface AmapDao {
    @Select("select distinct province_name from map")
    List<String> selectProvinces();

    @Select("select distinct city_name from map where map.province_name = #{Province}")
    List<String> selectCitys(String Province);

    @Select("select distinct district_name from map where map.province_name = #{Province} and map.city_name = #{City}")
    List<String> selectDistricts(String City, String Province);
}
