package site.xiaofei.apicommon.model.dto.interfaceinfo;

import lombok.Data;

@Data
public class InterfaceInfoUpdateAvatarRequest {

	private long id;

	/**
	 * 接口头像
	 */
	private String avatarUrl;

}
