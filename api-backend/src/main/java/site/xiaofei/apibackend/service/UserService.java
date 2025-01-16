package site.xiaofei.apibackend.service;


import com.baomidou.mybatisplus.extension.service.IService;
import site.xiaofei.apicommon.model.dto.user.UserRegisterRequest;
import site.xiaofei.apicommon.model.entity.User;
import site.xiaofei.apicommon.model.vo.UserVo;

import javax.servlet.http.HttpServletRequest;

public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userRegisterRequest 用户注册请求
     * @return 新用户 id
     */
    long userRegister(UserRegisterRequest userRegisterRequest);

    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request      请求
     * @return 脱敏后的用户信息
     */
    UserVo userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 获取登录用户
     * 获取当前登录用户
     *
     * @param request 请求
     * @return {@link UserVo}
     */
    UserVo getLoginUser(HttpServletRequest request);

    /**
     * 是管理
     * 是否为管理员
     *
     * @param request 请求
     * @return boolean
     */
    boolean isAdmin(HttpServletRequest request);

    /**
     * 是游客
     *
     * @param request 要求
     * @return {@link User}
     */
    User isTourist(HttpServletRequest request);

    /**
     * 用户注销
     *
     * @param request 请求
     * @return boolean
     */
    boolean userLogout(HttpServletRequest request);


    /**
     * 校验
     *
     * @param add  是否为创建校验
     * @param user 接口信息
     */
    void validUser(User user, UserVo loginUser, boolean add);

    /**
     * 更新凭证
     * 凭证
     *
     * @param loginUser 登录用户
     * @return {@link UserVo}
     */
    UserVo updateVoucher(User loginUser);

}
