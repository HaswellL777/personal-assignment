package com.example.demo.app;


import com.example.demo.common.ApiResponse;
import com.example.demo.app.vo.AppVO;
import com.example.demo.app.entity.AppEntity;
import com.example.demo.app.mapper.AppMapper;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/apps")
public class AppController {
    private final AppMapper appMapper;

    public AppController(AppMapper appMapper) {
        this.appMapper = appMapper;
    }

    @GetMapping
    public ApiResponse<List<AppVO>> list() {
        // 使用 MyBatis 查询数据库
        List<AppEntity> entities = appMapper.findAllOrderById();

        // 转换为 VO 对象
        List<AppVO> out = new ArrayList<>();
        for (AppEntity e : entities) {
            AppVO vo = AppVO.builder()
                    .id(e.getId())
                    .name(e.getName())
                    .description(e.getDescription())
                    .fullDescription(e.getFullDescription())
                    .avatar(e.getAvatar())
                    .category(e.getCategory())
                    .price(e.getPrice())
                    .rating(e.getRating())
                    .downloads(e.getDownloads())
                    .reviews(e.getReviews())
                    .author(e.getAuthor())
                    .publishedAt(e.getPublishedAt())
                    .build();
            out.add(vo);
        }
        return ApiResponse.ok(out);
    }

    // 新增：根据分类查询应用
    @GetMapping("/category/{category}")
    public ApiResponse<List<AppVO>> listByCategory(@PathVariable String category) {
        List<AppEntity> entities = appMapper.findByCategory(category);
        List<AppVO> out = new ArrayList<>();
        for (AppEntity e : entities) {
            AppVO vo = AppVO.builder()
                    .id(e.getId())
                    .name(e.getName())
                    .description(e.getDescription())
                    .fullDescription(e.getFullDescription())
                    .avatar(e.getAvatar())
                    .category(e.getCategory())
                    .price(e.getPrice())
                    .rating(e.getRating())
                    .downloads(e.getDownloads())
                    .reviews(e.getReviews())
                    .author(e.getAuthor())
                    .publishedAt(e.getPublishedAt())
                    .build();
            out.add(vo);
        }
        return ApiResponse.ok(out);
    }
}