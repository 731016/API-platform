package site.xiaofei.apiinterface.model.dto.csdn;

import lombok.Data;

import java.io.Serializable;

/**
 * @author tuaofei
 * @description TODO
 * @date 2025/1/15
 */
@Data
public class CsdnInfoDto implements Serializable {

    /**
     * 总访问量
     */
    private String totalVisitsNum;

    /**
     * 原创
     */
    private String originalNum;

    /**
     * 排名
     */
    private String rankingNum;

    /**
     * 粉丝
     */
    private String fansNum;

    /**
     * 铁粉
     */
    private String dieHardFansNum;
}
