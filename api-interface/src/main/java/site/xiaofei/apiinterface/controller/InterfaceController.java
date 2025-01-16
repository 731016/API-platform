package site.xiaofei.apiinterface.controller;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import site.xiaofei.apicommon.common.exception.BusinessException;
import site.xiaofei.apicommon.common.response.BaseResponse;
import site.xiaofei.apicommon.common.util.ResultUtils;
import site.xiaofei.apiinterface.model.dto.csdn.CsdnInfoDto;
import site.xiaofei.apiinterface.service.CsdnInfoService;
import site.xiaofei.apiinterface.util.RequestUtil;
import site.xiaofei.apiinterface.util.ResponseUtil;
import site.xiaofei.apisdk.model.params.HoroscopeParams;
import site.xiaofei.apisdk.model.params.IpInfoParams;
import site.xiaofei.apisdk.model.params.RandomWallpaperParams;
import site.xiaofei.apisdk.model.params.WeatherParams;
import site.xiaofei.apisdk.model.response.RandomWallpaperResponse;
import site.xiaofei.apisdk.model.response.common.ResultResponse;

import javax.annotation.Resource;
import java.util.Map;

@Slf4j
@RestController
public class InterfaceController {

	@Resource
	private CsdnInfoService csdnInfoService;

	/**
	 * 土味情话
	 */
	@GetMapping("/loveTalk")
	public String randomLoveTalk() {
		return RequestUtil.get("https://api.vvhan.com/api/text/love");
	}

	/**
	 * 幽默段子
	 */
	@GetMapping("/poisonousChickenSoup")
	public String getPoisonousChickenSoup() {
		return RequestUtil.get("https://api.btstu.cn/yan/api.php?charset=utf-8&encode=json");
	}

	/**
	 * 随机壁纸
	 */
	@GetMapping("/randomWallpaper")
	public RandomWallpaperResponse randomWallpaper(RandomWallpaperParams randomWallpaperParams) throws BusinessException {
		String baseUrl = "https://api.btstu.cn/sjbz/api.php";
		String url = RequestUtil.buildUrl(baseUrl, randomWallpaperParams);
		if (StringUtils.isAllBlank(randomWallpaperParams.getLx(), randomWallpaperParams.getMethod())) {
			url = url + "?format=json&lx=fengjing";
		} else if (StringUtils.isBlank(randomWallpaperParams.getLx())) {
			url = url + "&format=json&lx=fengjing";
		} else {
			url = url + "&format=json";
		}
		return JSONUtil.toBean(RequestUtil.get(url), RandomWallpaperResponse.class);
	}

	/**
	 * 星座运势
	 */
	@GetMapping("/horoscope")
	public ResultResponse getHoroscope(HoroscopeParams horoscopeParams) throws BusinessException {
		String response = RequestUtil.get("https://api.vvhan.com/api/horoscope", horoscopeParams);
		Map<String, Object> fromResponse = ResponseUtil.responseToMap(response);
		boolean success = (Boolean) fromResponse.get("success");
		if (!success) {
			ResultResponse baseResponse = new ResultResponse();
			baseResponse.setData(fromResponse);
			return baseResponse;
		}
		return JSONUtil.toBean(response, ResultResponse.class);
	}

	/*
	 *IP归属地
	 */
	@GetMapping("/ipInfo")
	public ResultResponse getIpInfo(IpInfoParams ipInfoParams) {
		return ResponseUtil.baseResponse("https://api.vvhan.com/api/ipInfo", ipInfoParams);
	}

	/**
	 * 天气查询
	 */
	@GetMapping("/weather")
	public ResultResponse getWeatherInfo(WeatherParams weatherParams) {
		if (StringUtils.isAllBlank(weatherParams.getIp(), weatherParams.getCity(), weatherParams.getType())) {
			return ResponseUtil.baseResponse("https://api.vvhan.com/api/weather?ip=183.230.12.201", weatherParams);
		} else {
			return ResponseUtil.baseResponse("https://api.vvhan.com/api/weather", weatherParams);
		}
	}

	/**
	 * 动漫头像
	 */
	@GetMapping("/cartoonAvatar")
	public ResultResponse getCartoonAvatar() {
		return ResponseUtil.baseResponse("https://api.vvhan.com/api/avatar/dm?type=json", null);
	}

	/**
	 * 职场人日历
	 */
	@GetMapping("/calendar")
	public ResultResponse getCalendar() {
		return ResponseUtil.baseResponse("https://api.vvhan.com/api/zhichang?type=json", null);
	}

	/**
	 * 手机号归属地
	 */
	@GetMapping("/phoneInfo")
	public ResultResponse getPhoneInfo(String phone) {
		return ResponseUtil.baseResponse("https://api.vvhan.com/api/phone/" + phone, null);
	}

    /**
     * CSDN博客信息
     * @param userId
     * @return
     */
    @GetMapping("/searchCsdnInfo")
    public BaseResponse getCsdnInfo(String userId) {
        CsdnInfoDto csdnInfoDto = csdnInfoService.searchCsdnInfo(userId);
        return ResultUtils.success(csdnInfoDto);
    }

	/**
	 * csdn博客总访问量
	 * @param userId
	 * @return
	 */
	@GetMapping("/getCsdnTotalVisitsNum")
	public String getCsdnTotalVisitsNum(String userId){
		CsdnInfoDto csdnInfoDto = csdnInfoService.searchCsdnInfo(userId);
		if (csdnInfoDto != null){
			String totalVisitsNum = csdnInfoDto.getTotalVisitsNum();
			return totalVisitsNum;
		}
		return null;
	}

}
