package site.xiaofei.apibackend.service.impl.dubbo;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import site.xiaofei.apibackend.service.InterfaceInfoService;
import site.xiaofei.apicommon.model.entity.InterfaceInfo;
import site.xiaofei.apicommon.service.dubbo.DubboInterfaceInfoService;

import javax.annotation.Resource;

@DubboService
@Slf4j
public class DubboInterfaceInfoServiceImpl implements DubboInterfaceInfoService {
	@Resource
	private InterfaceInfoService interfaceInfoService;

	@Override
	public InterfaceInfo getInterfaceInfo(String path, String method) {
		// 如果带参数，去除第一个？和之后后的参数
		if (path.contains("?")) {
			path = path.substring(0, path.indexOf("?"));
		}
		if (path.endsWith("/")) {
			path = path.substring(0, path.length() - 1);
		}
		path = path.substring(path.indexOf("/api/"));
		// log.info("【查询地址】：" + path);
		LambdaQueryWrapper<InterfaceInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
		lambdaQueryWrapper.eq(InterfaceInfo::getMethod, method);
		lambdaQueryWrapper.like(InterfaceInfo::getUrl, path);
		return interfaceInfoService.getOne(lambdaQueryWrapper);
	}
}
