package xiaofeiyang.study.ai.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 会话模型
 * </p>
 *
 * @author xiaofeiyang
 * @since 2025-04-03
 */
@Getter
@Setter
@TableName("ai_model")
public class AIModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 模型名称
     */
    private String modelName;

    /**
     * API 模型 编码
     */
    private String modelCode;

    /**
     * API 历经
     */
    private String apiUrl;

    /**
     * API 密钥
     */
    private String apiKey;

    /**
     * 状态 1有效 0无效
     */
    private Byte status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 创建时间
     */
    private LocalDateTime updateTime;
}
