package xiaofeiyang.study.ai.service;

import xiaofeiyang.study.ai.common.enums.StatusEnum;
import xiaofeiyang.study.ai.entity.AIModel;
import xiaofeiyang.study.ai.mapper.ModelsMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 会话模型 服务实现类
 * </p>
 *
 * @author xiaofeiyang
 * @since 2025-04-03
 */
@Service
public class ModelsService extends ServiceImpl<ModelsMapper, AIModel> {

    public AIModel getByModelCode(String modelCode) {
        return this.lambdaQuery()
                .eq(AIModel::getModelCode, modelCode)
                .eq(AIModel::getStatus, StatusEnum.NOT.getValue())
                .one();
    }
}
