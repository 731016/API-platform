package site.xiaofei.apiinterface.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author tuaofei
 * @description TODO
 * @date 2025/1/15
 */
@Data
@Component
@ConfigurationProperties(prefix = "csdn")
public class CsdnConfig {

    /**
     * csdn url
     */
    private String url;

    /**
     * 默认用户
     */
    private String defaultUser;
}
