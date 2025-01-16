package site.xiaofei.apicommon.common.util;

import com.alibaba.nacos.api.model.v2.ErrorCode;
import site.xiaofei.apicommon.common.enums.ResponseCode;
import site.xiaofei.apicommon.common.response.BaseResponse;

/**
 * 返回工具类
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
public class ResultUtils {

    /**
     * 成功
     *
     * @param data
     * @param <T>
     * @return
     */
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(0, data, "ok");
    }

    /**
     * 失败
     *
     * @param responseCode
     * @return
     */
    public static BaseResponse error(ResponseCode responseCode) {
        return new BaseResponse<>(responseCode);
    }

    /**
     * 失败
     *
     * @param code
     * @param message
     * @return
     */
    public static BaseResponse error(int code, String message) {
        return new BaseResponse(code, null, message);
    }

    /**
     * 失败
     *
     * @param responseCode
     * @return
     */
    public static BaseResponse error(ResponseCode responseCode, String message) {
        return new BaseResponse(responseCode.getCode(), null, message);
    }
}
