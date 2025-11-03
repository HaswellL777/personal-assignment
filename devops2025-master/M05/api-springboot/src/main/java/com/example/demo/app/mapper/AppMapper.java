package com.example.demo.app.mapper;

import com.example.demo.app.entity.AppEntity;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper  // 标识为 MyBatis Mapper 接口
public interface AppMapper {
    // 查询所有应用，按 ID 升序排列
    List<AppEntity> findAllOrderById();

    // 根据 ID 查询单个应用
    AppEntity findById(Long id);

    // 根据分类查询应用
    List<AppEntity> findByCategory(String category);
}