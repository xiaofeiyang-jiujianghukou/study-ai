package xiaofeiyang.study.ai.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import xiaofeiyang.study.ai.common.Result;
import xiaofeiyang.study.ai.controller.dto.ChatReqDTO;
import xiaofeiyang.study.ai.controller.dto.ListModelRespDTO;
import xiaofeiyang.study.ai.controller.dto.ModelChatReqDTO;
import xiaofeiyang.study.ai.manager.ModelManager;

import java.util.List;

/**
 * <p>
 * 会话模型 前端控制器
 * </p>
 *
 * @author xiaofeiyang
 * @since 2025-04-03
 */
@RestController
@RequestMapping("/ai/model")
public class ModelController {

    @Resource
    private ModelManager modelManager;

    @GetMapping("/listModel")
    @Operation(summary = "获取模型列表", description = "获取模型列表")
    public Result<List<ListModelRespDTO>> listModel() {
        return Result.success(modelManager.listModel());
    }

    @PostMapping(value = "/chat")
    @Operation(summary = "获取用户信息", description = "通过用户ID获取用户的详细信息")
    public Result<String> chat(@Validated @RequestBody ModelChatReqDTO input) {
        return Result.success(modelManager.generateResponse(input));
    }

    @PostMapping(value = "/chat3", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "获取用户信息", description = "通过用户ID获取用户的详细信息")
    public Flux<String> chat3(@Validated @RequestBody ModelChatReqDTO input) {
        return modelManager.generateResponse3(input);
    }
}
