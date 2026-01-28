
package com.bdis.bdis_website.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("banner_item")
public class BannerItem {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 标题 */
    private String title;

    /** 副标题 */
    private String subtitle;

    /** 描述 */
    private String description;

    /** 按钮文字 */
    @TableField("button_text")
    private String buttonText;

    /** 按钮链接 */
    @TableField("button_link")
    private String buttonLink;

    /** 图片地址 */
    @TableField("image_url")
    private String imageUrl;

    /** 排序 */
    @TableField("sort_order")
    private Integer sortOrder;

    /** 是否启用（1启用 / 0禁用） */
    private Integer active;
}

