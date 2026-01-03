package com.dkd.manage.domain.vo;

import com.dkd.manage.domain.Region;
import lombok.Data;

/**
 * projectName: dkd-parent
 *
 * @author: 张轩鸣
 * description: 返回前端数据
 */
@Data
public class RegionVO extends Region {
    private Integer nodeCount;
}
