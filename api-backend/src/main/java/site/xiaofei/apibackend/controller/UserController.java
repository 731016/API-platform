package site.xiaofei.apibackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import site.xiaofei.apibackend.service.UserService;
import site.xiaofei.apibackend.common.annotation.AuthCheck;
import site.xiaofei.apibackend.common.constant.UserConstant;
import site.xiaofei.apibackend.common.request.DeleteRequest;
import site.xiaofei.apibackend.common.request.IdRequest;
import site.xiaofei.apicommon.common.enums.ResponseCode;
import site.xiaofei.apicommon.common.enums.UserAccountStatusEnum;
import site.xiaofei.apicommon.common.exception.BusinessException;
import site.xiaofei.apicommon.common.response.BaseResponse;
import site.xiaofei.apicommon.common.util.ResponseUtil;
import site.xiaofei.apicommon.model.dto.user.*;
import site.xiaofei.apicommon.model.entity.User;
import site.xiaofei.apicommon.model.vo.UserVo;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

	@Resource
	private UserService userService;

	@Resource
	private RedisTemplate<String, String> redisTemplate;

	/**
	 * 注册
	 */
	@PostMapping("/register")
	public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
		if (userRegisterRequest == null) {
			throw new BusinessException(ResponseCode.PARAMS_ERROR);
		}
		long result = userService.userRegister(userRegisterRequest);
		return ResponseUtil.success(result);
	}

	/**
	 * 登录
	 */
	@PostMapping("/login")
	public BaseResponse<UserVo> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
		if (userLoginRequest == null) {
			throw new BusinessException(ResponseCode.PARAMS_ERROR);
		}
		String userAccount = userLoginRequest.getUserAccount();
		String userPassword = userLoginRequest.getUserPassword();
		if (StringUtils.isAnyBlank(userAccount, userPassword)) {
			throw new BusinessException(ResponseCode.PARAMS_ERROR);
		}
		UserVo user = userService.userLogin(userAccount, userPassword, request);
		return ResponseUtil.success(user);
	}

	/**
	 * 获取当前登录用户
	 *
	 * @param request 请求
	 * @return {@link BaseResponse}<{@link UserVo}>
	 */
	@GetMapping("/get/login")
	public BaseResponse<UserVo> getLoginUser(HttpServletRequest request) {
		UserVo user = userService.getLoginUser(request);
		return ResponseUtil.success(user);
	}

	/**
	 * 用户注销
	 *
	 * @param request 请求
	 * @return {@link BaseResponse}<{@link Boolean}>
	 */
	@PostMapping("/logout")
	public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
		if (request == null) {
			throw new BusinessException(ResponseCode.PARAMS_ERROR);
		}
		boolean result = userService.userLogout(request);
		return ResponseUtil.success(result);
	}

	// region 增删改查

	/**
	 * 添加用户
	 *
	 * @param userAddRequest 用户添加请求
	 * @param request        请求
	 * @return {@link BaseResponse}<{@link Long}>
	 */
	@PostMapping("/add")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public BaseResponse<Long> addUser(@RequestBody UserAddRequest userAddRequest, HttpServletRequest request) {
		if (userAddRequest == null) {
			throw new BusinessException(ResponseCode.PARAMS_ERROR);
		}
		User user = new User();
		BeanUtils.copyProperties(userAddRequest, user);
		UserVo loginUser = (UserVo) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
		// 校验
		userService.validUser(user, loginUser, true);

		boolean result = userService.save(user);
		if (!result) {
			throw new BusinessException(ResponseCode.OPERATION_ERROR);
		}
		return ResponseUtil.success(user.getId());
	}

	/**
	 * 删除用户
	 *
	 * @param deleteRequest 删除请求
	 * @param request       请求
	 * @return {@link BaseResponse}<{@link Boolean}>
	 */
	@PostMapping("/delete")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
		if (ObjectUtils.anyNull(deleteRequest, deleteRequest.getId()) || deleteRequest.getId() <= 0) {
			throw new BusinessException(ResponseCode.PARAMS_ERROR);
		}
		return ResponseUtil.success(userService.removeById(deleteRequest.getId()));
	}

	/**
	 * 更新用户
	 *
	 * @param userUpdateRequest 用户更新请求
	 * @param request           请求
	 * @return {@link BaseResponse}<{@link User}>
	 */
	@PostMapping("/update")
	@Transactional(rollbackFor = Exception.class)
	public BaseResponse<UserVo> updateUser(@RequestBody UserUpdateRequest userUpdateRequest, HttpServletRequest request) {
		if (ObjectUtils.anyNull(userUpdateRequest, userUpdateRequest.getId()) || userUpdateRequest.getId() <= 0) {
			throw new BusinessException(ResponseCode.PARAMS_ERROR);
		}

		// 管理员才能操作
		boolean adminOperation = ObjectUtils.isNotEmpty(userUpdateRequest.getBalance())
				|| StringUtils.isNoneBlank(userUpdateRequest.getUserRole())
				|| StringUtils.isNoneBlank(userUpdateRequest.getUserPassword());
		// 校验是否登录
		UserVo loginUser = userService.getLoginUser(request);
		// 处理管理员业务,不是管理员抛异常
		if (adminOperation && !loginUser.getUserRole().equals(UserConstant.ADMIN_ROLE)) {
			throw new BusinessException(ResponseCode.NO_AUTH_ERROR);
		}

		if (!loginUser.getUserRole().equals(UserConstant.ADMIN_ROLE) && !userUpdateRequest.getId().equals(loginUser.getId())) {
			throw new BusinessException(ResponseCode.NO_AUTH_ERROR, "只有本人或管理员可以修改");
		}

		User user = new User();
		BeanUtils.copyProperties(userUpdateRequest, user);
		if (StringUtils.isBlank(user.getUserPassword())) {
			user.setUserPassword(null);
		}
		// 参数校验
		userService.validUser(user, loginUser, false);

		LambdaUpdateWrapper<User> userLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
		userLambdaUpdateWrapper.eq(User::getId, user.getId());

		boolean result = userService.update(user, userLambdaUpdateWrapper);
		if (!result) {
			throw new BusinessException(ResponseCode.OPERATION_ERROR, "更新失败");
		}
		UserVo userVo = new UserVo();
		BeanUtils.copyProperties(userService.getById(user.getId()), userVo);
		return ResponseUtil.success(userVo);
	}

	@PostMapping("/updatePwd")
	public BaseResponse<UserVo> updatePwd(@RequestBody UserUpdateRequest userUpdateRequest, HttpServletRequest request) {
		if (userUpdateRequest == null) {
			throw new BusinessException(ResponseCode.PARAMS_ERROR);
		}
		// 校验是否登录
		UserVo loginUser = userService.getLoginUser(request);
		if (loginUser == null) {
			throw new BusinessException(ResponseCode.NOT_LOGIN_ERROR);
		}

		User user = new User();
		BeanUtils.copyProperties(userUpdateRequest, user);

		// 参数校验
		if (user.getUserPassword() == null) {
			throw new BusinessException(ResponseCode.PARAMS_ERROR, "密码不能为空");
		}
		if (user.getUserPassword().length() < 4) {
			throw new BusinessException(ResponseCode.PARAMS_ERROR, "用户密码过短,不能少于4位");
		}
		String pattern = "[0-9a-zA-Z]+";
		if (!user.getUserPassword().matches(pattern)) {
			throw new BusinessException(ResponseCode.PARAMS_ERROR, "密码由数字和大小写字母组成");
		}
		// 加密
		String encryptPassword = DigestUtils.md5DigestAsHex((UserConstant.SALT + user.getUserPassword()).getBytes());
		user.setUserPassword(encryptPassword);
		// 更新密码
		LambdaUpdateWrapper<User> userLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
		userLambdaUpdateWrapper.eq(User::getId, loginUser.getId());
		boolean result = userService.update(user, userLambdaUpdateWrapper);
		if (!result) {
			throw new BusinessException(ResponseCode.OPERATION_ERROR, "更新失败");
		}
		UserVo userVo = new UserVo();
		BeanUtils.copyProperties(userService.getById(loginUser.getId()), userVo);
		return ResponseUtil.success(userVo);
	}

	/**
	 * 根据 id 获取用户
	 *
	 * @param id      id
	 * @param request 请求
	 * @return {@link BaseResponse}<{@link UserVo}>
	 */
	@GetMapping("/get")
	public BaseResponse<UserVo> getUserById(Long id, HttpServletRequest request) {
		if (id <= 0) {
			throw new BusinessException(ResponseCode.PARAMS_ERROR);
		}
		User user = userService.getById(id);
		UserVo userVo = new UserVo();
		BeanUtils.copyProperties(user, userVo);
		return ResponseUtil.success(userVo);
	}

	/**
	 * 获取用户列表
	 *
	 * @param userQueryRequest 用户查询请求
	 * @param request          请求
	 * @return {@link BaseResponse}<{@link List}<{@link UserVo}>>
	 */
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	@GetMapping("/list")
	public BaseResponse<List<UserVo>> listUser(UserQueryRequest userQueryRequest, HttpServletRequest request) {
		if (null == userQueryRequest) {
			throw new BusinessException(ResponseCode.PARAMS_ERROR);
		}
		User userQuery = new User();
		BeanUtils.copyProperties(userQueryRequest, userQuery);

		QueryWrapper<User> queryWrapper = new QueryWrapper<>(userQuery);
		List<User> userList = userService.list(queryWrapper);
		List<UserVo> userVoList = userList.stream().map(user -> {
			UserVo userVo = new UserVo();
			BeanUtils.copyProperties(user, userVo);
			return userVo;
		}).collect(Collectors.toList());
		return ResponseUtil.success(userVoList);
	}

	/**
	 * 分页获取用户列表
	 *
	 * @param userQueryRequest 用户查询请求
	 * @param request          请求
	 * @return {@link BaseResponse}<{@link Page}<{@link UserVo}>>
	 */
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	@GetMapping("/list/page")
	public BaseResponse<Page<UserVo>> listUserByPage(UserQueryRequest userQueryRequest, HttpServletRequest request) {
		User userQuery = new User();
		if (userQueryRequest == null) {
			throw new BusinessException(ResponseCode.PARAMS_ERROR);
		}

		BeanUtils.copyProperties(userQueryRequest, userQuery);

		String username = userQueryRequest.getUsername();
		String userAccount = userQueryRequest.getUserAccount();
		String gender = userQueryRequest.getGender();
		String userRole = userQueryRequest.getUserRole();
		long current = userQueryRequest.getCurrent();
		long pageSize = userQueryRequest.getPageSize();

		LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.like(StringUtils.isNotBlank(username), User::getUsername, username)
				.eq(StringUtils.isNotBlank(userAccount), User::getUserAccount, userAccount)
				.eq(StringUtils.isNotBlank(gender), User::getGender, gender)
				.eq(StringUtils.isNotBlank(userRole), User::getUserRole, userRole);
		Page<User> userPage = userService.page(new Page<>(current, pageSize), queryWrapper);
		Page<UserVo> userVoPage = new PageDTO<>(userPage.getCurrent(), userPage.getSize(), userPage.getTotal());
		List<UserVo> userVoList = userPage.getRecords().stream().map(user -> {
			UserVo userVo = new UserVo();
			BeanUtils.copyProperties(user, userVo);
			return userVo;
		}).collect(Collectors.toList());
		userVoPage.setRecords(userVoList);
		return ResponseUtil.success(userVoPage);
	}

	/**
	 * 更新凭证
	 */
	@PostMapping("/update/voucher")
	public BaseResponse<UserVo> updateVoucher(HttpServletRequest request) {
		if (request == null) {
			throw new BusinessException(ResponseCode.PARAMS_ERROR);
		}
		UserVo loginUser = userService.getLoginUser(request);
		User user = new User();
		BeanUtils.copyProperties(loginUser, user);
		UserVo userVo = userService.updateVoucher(user);
		return ResponseUtil.success(userVo);
	}

	/**
	 * 通过邀请码获取用户
	 *
	 * @param invitationCode 邀请码
	 * @return {@link BaseResponse}<{@link UserVo}>
	 */
	@PostMapping("/get/invitationCode")
	public BaseResponse<UserVo> getUserByInvitationCode(String invitationCode) {
		if (StringUtils.isBlank(invitationCode)) {
			throw new BusinessException(ResponseCode.PARAMS_ERROR);
		}
		LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
		userLambdaQueryWrapper.eq(User::getInvitationCode, invitationCode);
		User invitationCodeUser = userService.getOne(userLambdaQueryWrapper);
		if (invitationCodeUser == null) {
			throw new BusinessException(ResponseCode.NOT_FOUND_ERROR, "邀请码不存在");
		}
		UserVo userVo = new UserVo();
		BeanUtils.copyProperties(invitationCodeUser, userVo);
		return ResponseUtil.success(userVo);
	}

	/**
	 * 解封
	 *
	 * @param idRequest id请求
	 * @return {@link BaseResponse}<{@link Boolean}>
	 */
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	@PostMapping("/normal")
	public BaseResponse<Boolean> unbindUser(@RequestBody IdRequest idRequest) {
		if (ObjectUtils.anyNull(idRequest, idRequest.getId()) || idRequest.getId() <= 0) {
			throw new BusinessException(ResponseCode.PARAMS_ERROR);
		}
		Long id = idRequest.getId();
		User user = userService.getById(id);
		if (user == null) {
			throw new BusinessException(ResponseCode.NOT_FOUND_ERROR);
		}
		user.setStatus(UserAccountStatusEnum.NORMAL.getValue());
		return ResponseUtil.success(userService.updateById(user));
	}

	/**
	 * 封号
	 *
	 * @param idRequest id请求
	 * @param request   请求
	 * @return {@link BaseResponse}<{@link Boolean}>
	 */
	@PostMapping("/ban")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public BaseResponse<Boolean> banUser(@RequestBody IdRequest idRequest, HttpServletRequest request) {
		if (ObjectUtils.anyNull(idRequest, idRequest.getId()) || idRequest.getId() <= 0) {
			throw new BusinessException(ResponseCode.PARAMS_ERROR);
		}
		Long id = idRequest.getId();
		User user = userService.getById(id);
		if (user == null) {
			throw new BusinessException(ResponseCode.NOT_FOUND_ERROR);
		}
		user.setStatus(UserAccountStatusEnum.BAN.getValue());
		return ResponseUtil.success(userService.updateById(user));
	}
	// endregion
}
