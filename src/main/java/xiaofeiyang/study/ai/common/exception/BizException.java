package xiaofeiyang.study.ai.common.exception;

import lombok.Getter;
import xiaofeiyang.study.ai.common.enums.ResultEnum;


public class BizException extends RuntimeException {
    private final ResultEnum resultEnum;

    public BizException(ResultEnum result) {
        super(result.getMessage());
        this.resultEnum = result;
    }

    public BizException(String message) {
        super(message);
        this.resultEnum = ResultEnum.BIZ_EXCEPTION;
    }

    public BizException(ResultEnum result, String message) {
        super(message);
        this.resultEnum = result;
    }

    public ResultEnum getResultEnum() {
        return resultEnum;
    }
}
