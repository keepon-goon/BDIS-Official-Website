package com.bdis.bdis_website.entity;

// 学习资料分类枚举（与文档2.1的category选项完全一致）
public enum CategoryEnum {
    FRONTEND("frontend"),  // 前端
    BACKEND("backend"),    // 后端
    DATABASE("database"),  // 数据库
    AI("ai"),              // 人工智能
    TOOLS("tools"),        // 工具
    COURSE("course");      // 课程

    private final String value;

    CategoryEnum(String value) {
        this.value = value;
    }

    // 用于接收前端参数时，将字符串转为枚举（避免非法值）
    public static CategoryEnum fromValue(String value) {
        for (CategoryEnum category : CategoryEnum.values()) {
            if (category.value.equals(value)) {
                return category;
            }
        }
        throw new IllegalArgumentException("非法的分类值：" + value);
    }

    // 存储到数据库时，返回字符串值
    public String getValue() {
        return value;
    }
}
