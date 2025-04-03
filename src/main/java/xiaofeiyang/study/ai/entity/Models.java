package xiaofeiyang.study.ai.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.time.LocalDateTime;
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
public class Models implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 模型名称
     */
    private String name;

    /**
     * API 历经
     */
    private String apiUrl;

    /**
     * API 密钥
     */
    private String apiKey;

    /**
     * API 模型
     */
    private String apiModel;

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
