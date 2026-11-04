package com.bdis.bdis_website.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bdis.bdis_website.entity.LabInfo;
import com.bdis.bdis_website.mapper.LabInfoMapper;
import com.bdis.bdis_website.service.LabInfoService;
import org.springframework.stereotype.Service;

@Service
public class LabInfoServiceImpl extends ServiceImpl<LabInfoMapper, LabInfo> implements LabInfoService {
    private static final String DEFAULT_JOIN_INTRO = """
            我们用算法洞察趋势，用代码把想法落地，用系统承载智能，让技术真正产生价值。
            这里，
            ◦ 你可以和热爱技术的伙伴一起，做前端、做后端，把一个产品从 0 搭到 1；
            ◦ 你可以做数据分析、机器学习与深度学习，把模型训出来、跑通实验、拿到可复现的结果；
            ◦ 你也可以做测试与运维，把系统的稳定性、质量与交付流程打磨到更可靠、更专业。
            我们相信，一个人的力量有限，但一群人的协作无穷。
            如果你想让简历更有分量，让能力更成体系——
            无论你是准备本科就业，还是奔着考研提升，
            那就来吧——
            加入我们，一起用数据与智能，做更多有意义、做得出来、拿得出手的事。""";

    @Override
    public LabInfo getSiteInfo() {
        LabInfo info = getById(1L);
        if (info != null && info.getJoinIntro() == null) {
            info.setJoinIntro(DEFAULT_JOIN_INTRO);
        }
        return info;
    }
}



