package xiaofeiyang.study.ai.controller.dto.deepseek;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ChatRespDTO {

    @Schema(name = "消息")
    private String message;
}
