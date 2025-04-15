package xiaofeiyang.study.ai.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ModelChatReqDTO extends ChatReqDTO {

    @Schema(name = "消息")
    @NotBlank(message = "模型编码不能为空")
    private String modelCode;
}
