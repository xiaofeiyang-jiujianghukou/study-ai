package xiaofeiyang.study.ai.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xiaofeiyang.study.ai.common.Result;
import xiaofeiyang.study.ai.manager.DeepSeekManager;
import xiaofeiyang.study.ai.controller.dto.ChatReqDTO;

@RestController
@RequestMapping(path = {"/management/deep-seek/", "/inner/deep-seek/"})
@Tag(name = "图片管理", description = "包含图片相关的操作")
public class DeepSeekController {


    @Resource
    private DeepSeekManager deepSeekManager;

    @PostMapping("/chat")
    @Operation(summary = "获取用户信息", description = "通过用户ID获取用户的详细信息")
    public Result<String> chat(@Validated @RequestBody ChatReqDTO input) {
        return Result.success(deepSeekManager.generateResponse(input.getMessage()));
    }

}
