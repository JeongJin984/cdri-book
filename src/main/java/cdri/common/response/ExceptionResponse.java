package cdri.common.response;

import cdri.common.enums.ResponseCode;
import lombok.Getter;

@Getter
public class ExceptionResponse {
    private final String errCode;
    private final String errMsg;

    public ExceptionResponse(ResponseCode errCode) {
        this.errCode = errCode.name();
        this.errMsg = errCode.getKrMsg();
    }
}
