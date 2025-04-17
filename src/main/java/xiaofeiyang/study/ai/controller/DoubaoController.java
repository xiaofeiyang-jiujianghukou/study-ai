package xiaofeiyang.study.ai.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import xiaofeiyang.study.ai.common.Result;
import xiaofeiyang.study.ai.common.aspect.annotation.NoRepeatSubmit;
import xiaofeiyang.study.ai.controller.dto.ChatReqDTO;
import xiaofeiyang.study.ai.manager.DoubaoManager;

@RestController
@RequestMapping(path = {"/management/doubao/", "/inner/doubao/"})
@Tag(name = "图片管理", description = "包含图片相关的操作")
public class DoubaoController {


    /*@Resource
    private CommonAIManager commonAIManager;
    @Resource
    @Qualifier(value = "doubaoClient")
    private WebClient webClient;
    @Resource
    private DoubaoClientConfig doubaoClientConfig;

    @PostMapping("/chat")
    @Operation(summary = "获取用户信息", description = "通过用户ID获取用户的详细信息")
    public Result<String> chat(@Validated @RequestBody ChatReqDTO input) {
        return Result.success(commonAIManager.generateResponse(input.getMessage(), webClient, doubaoClientConfig));
    }*/

    @Resource
    private DoubaoManager doubaoManager;

    @NoRepeatSubmit(interval = 60, keyword = "myKeyword", includeUser = false)
    @PostMapping("/chat")
    @Operation(summary = "获取用户信息", description = "通过用户ID获取用户的详细信息")
    public Result<String> chat(@Validated @RequestBody ChatReqDTO input) {
        return Result.success(doubaoManager.generateResponse(input.getMessage()));
    }

    /*@GetMapping(value = "/chat2", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "获取用户信息", description = "通过用户ID获取用户的详细信息")
    public Flux<String> chat2(@RequestParam(value = "message") String message) {
        return doubaoManager.generateResponse2(message);
    }*/

    @PostMapping(value = "/chat2", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "获取用户信息", description = "通过用户ID获取用户的详细信息")
    public Flux<String> chat2(@Validated @RequestBody ChatReqDTO input) {
        return doubaoManager.generateResponse2(input.getMessage());
    }

    @PostMapping(value = "/chat3", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "获取用户信息", description = "通过用户ID获取用户的详细信息")
    public Flux<String> chat3(@Validated @RequestBody ChatReqDTO input) {
        return doubaoManager.generateResponse3(input.getMessage());
    }

    @NoRepeatSubmit(interval = 60, keyword = "code", includeUser = false)
    @PostMapping("/chat4")
    @Operation(summary = "获取用户信息", description = "通过用户ID获取用户的详细信息")
    public Result<String> chat4(@Validated @RequestParam(value = "code") String code, @Validated @RequestParam(value = "name") String name) {
        return Result.success(String.join("-", code, name));
    }

    @NoRepeatSubmit(interval = 60, keyword = "name", includeUser = false)
    @GetMapping("/chat5")
    @Operation(summary = "获取用户信息", description = "通过用户ID获取用户的详细信息")
    public Result<String> chat5(@Validated @RequestParam(value = "name") String name) {
        return Result.success(String.join("-", "Hello", name));
    }

    @NoRepeatSubmit(interval = 60, keyword = "name", includeUser = false)
    @GetMapping("/chat6/{name}")
    @Operation(summary = "获取用户信息", description = "通过用户ID获取用户的详细信息")
    public Result<String> chat6(@PathVariable(value = "name") String name) {
        return Result.success(String.join("-", "Hello", name));
    }

}
