package xiaofeiyang.study.ai.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ListModelRespDTO {

    @Schema(name = "模型名称")
    private String modelName;

    @Schema(name = "模型编码")
    private String modelCode;
}
