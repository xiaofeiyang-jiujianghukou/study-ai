package xiaofeiyang.study.ai.controller.dto.deepseek;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ChatReqDTO {

    @Schema(name = "消息")
    private String message;
}
