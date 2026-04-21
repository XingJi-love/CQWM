package com.sky.service.impl;

import com.sky.dto.DishDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DishServiceImpl implements DishService {

    // 注入菜品mapper
    @Autowired
    private DishMapper dishMapper;

    // 注入菜品口味mapper
    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    /**
     * 新增菜品和对应口味
     *
     * @param dishDTO
     */
    @Transactional // 开启事务管理(这个服务要对菜品和口味进行新增操作,涉及两个表的操作,所以要开启事务管理)
    @Override
    public void saveWithFlavor(DishDTO dishDTO) {
        // 封装菜品对象
        Dish dish = new Dish();

        // 拷贝菜品属性
        BeanUtils.copyProperties(dishDTO, dish);

        // 向菜品表插入一条数据
        dishMapper.insert(dish);

        // 获取菜品insert之后的id
        Long id = dish.getId();

        // 获取口味集合
        List<DishFlavor> flavors = dishDTO.getFlavors();
        // 判断口味集合是否为空,不为空则进行插入操作
        if (flavors != null && flavors.size() > 0) {
            // 遍历口味集合,设置菜品id
            flavors.forEach(dishflavor -> {
                // 设置菜品id
                dishflavor.setDishId(id);
            });
            // 向口味表插入n条数据
            dishFlavorMapper.insertBatch(flavors);
        }
    }
}
