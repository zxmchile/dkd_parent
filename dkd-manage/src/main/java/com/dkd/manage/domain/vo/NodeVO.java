package com.dkd.manage.domain.vo;

import com.dkd.manage.domain.Node;
import com.dkd.manage.domain.Partner;
import com.dkd.manage.domain.Region;
import lombok.Data;

/**
 * projectName: dkd-parent
 *
 * @author: 张轩鸣
 * description: 返回前端数据
 */
@Data
public class NodeVO extends Node {
    private Integer vmCount;
    private Partner partner;
    private Region region;
}
