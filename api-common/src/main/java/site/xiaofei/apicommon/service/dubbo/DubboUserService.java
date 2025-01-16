package site.xiaofei.apicommon.service.dubbo;

import site.xiaofei.apicommon.model.vo.UserVo;

public interface DubboUserService {

	/**
	 * 通过访问密钥获取invoke用户
	 */
	UserVo getInvokeUserByAccessKey(String accessKey);

}
