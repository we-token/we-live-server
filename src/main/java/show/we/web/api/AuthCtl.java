package show.we.web.api;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.zb.common.Constant.ReCode;
import com.zb.common.utils.RegexUtil;
import com.zb.core.web.ReMsg;
import com.zb.service.msg.SmsService;

import show.we.user.AuthService;
import show.we.user.UserService;

@Controller
@RequestMapping("/api")
public class AuthCtl extends BaseCtl {
	static final Logger log = LoggerFactory.getLogger(AuthCtl.class);

	@Autowired
	AuthService authService;
	
	@Autowired
	UserService userService;

	@Autowired
	SmsService smsService;


	@ResponseBody
	@RequestMapping("/registerPhone")
	public ReMsg registerPhone(String phone, String pwd, String code, String appCode, HttpServletRequest req)
			throws JsonParseException, JsonMappingException, NumberFormatException, IOException {
		if (!RegexUtil.isPhone(phone)) {
			return new ReMsg("您输入手机号不符合规则！");
		}

		if (!RegexUtil.isPassword(pwd)) {
			return new ReMsg("密码为6－20位！");
		}
		return authService.reg(phone, pwd, code, appCode, req);
	}

	@ResponseBody
	@RequestMapping("/forgotPhone")
	public ReMsg forgotPhone(String phone, String pwd, String code, HttpServletRequest req)
			throws JsonParseException, JsonMappingException, NumberFormatException, IOException {
		if (!RegexUtil.isPhone(phone)) {
			return new ReMsg("您输入手机号不符合规则！");
		}

		if (!RegexUtil.isPassword(pwd)) {
			return new ReMsg("密码为6－20位！");
		}
		return authService.forgot(phone, pwd, code, req);
	}

	@ResponseBody
	@RequestMapping("/checkUsername")
	public ReMsg checkUsername(String username, HttpServletRequest req) {
		if (!RegexUtil.isUsername(username)) {
			return new ReMsg("用户名为2-16位中文，字母和数字组成！");
		}
		return authService.checkUsername(username, req);
	}

	@ResponseBody
	@RequestMapping("/guest")
	public ReMsg guest(HttpServletRequest req)
			throws JsonParseException, JsonMappingException, NumberFormatException, IOException {
		return authService.regGuest(req);
	}

	@ResponseBody
	@RequestMapping("/islogin")
	public ReMsg islogin(String token, String lbs, String channelId, Integer channelType, String appCode,
			HttpServletRequest req) throws Exception {
		return authService.isLogin(token, lbs, req);
	}

	@ResponseBody
	@RequestMapping("/login")
	public ReMsg login(String username, String pwd, HttpServletRequest req) {
		if (!RegexUtil.isUsername(username)) {
			return new ReMsg(ReCode.LOGIN_FAILED_ERR_PASSWORD, "用户名为2-16位中文，字母和数字组成！");
		}

		if (!RegexUtil.isPassword(pwd)) {
			return new ReMsg(ReCode.LOGIN_FAILED_ERR_PASSWORD, "密码为6－20位！");
		}

		return authService.login(username, pwd, req);
	}

	@RequestMapping("/authcode")
	public void authCode(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		authService.authcode(req, resp);
	}

	@ResponseBody
	@RequestMapping("/dynamicKey")
	public ReMsg getDynamicKey(String reqKey, double ver, int via, String data, long timestamp) {
		return authService.getKey(reqKey, ver, via, data, timestamp);
	}

	@ResponseBody
	@RequestMapping("/canUseTool")
	public ReMsg useKey(String mark, HttpServletRequest req)
			throws JsonParseException, JsonMappingException, IOException {
		return authService.canUseTool(req, mark);
	}

	@ResponseBody
	@RequestMapping("/sendSms")
	public ReMsg sendSmsAuthCode(Long phone, HttpServletRequest req)
			throws JsonParseException, JsonMappingException, IOException {
		// return smsService.sendAuthCode(req, phone);
		return new ReMsg(ReCode.FAIL);
	}

	@ResponseBody
	@RequestMapping("/sendSmsV")
	public ReMsg sendSmsValid(Long phone, String tokenCode, Long timestamp, HttpServletRequest req)
			throws JsonParseException, JsonMappingException, IOException {
//		DBObject object = userService.findByPhone(""+phone);
//		if (null != object) {
//			return new ReMsg(ReCode.PHONE_HAVEBIND);
//		}
		return smsService.sendAuthCode(req, phone, timestamp, tokenCode);
	}

	@ResponseBody
	@RequestMapping("/getEasemob")
	public ReMsg getEasemobToken(String token, HttpServletRequest req)
			throws JsonParseException, JsonMappingException, IOException {
		return authService.getAliUser(token);
	}

	@ResponseBody
	@RequestMapping("/getAli")
	public ReMsg getAliToken(String token, HttpServletRequest req)
			throws JsonParseException, JsonMappingException, IOException {
		return authService.getAliUser(token);
	}

	// 绑定手机号
	@ResponseBody
	@RequestMapping("/bindPhone")
	public ReMsg thridLogin(HttpServletRequest req, String phone, String code) throws Exception {
		return authService.bindPhone(req, phone, code);
	}

	// 绑定第三方
	@ResponseBody
	@RequestMapping("/bindThird")
	public ReMsg thridLogin(HttpServletRequest req, String accessToken, String openId, int loginType, String appCode)
			throws IOException {
		return new ReMsg(authService.bindThird(loginType, accessToken, openId, appCode, req));
	}

	// 检查绑定
	@ResponseBody
	@RequestMapping("/checkBind")
	public ReMsg checkBind(HttpServletRequest req) {
		return authService.checkBind(req);
	}

}
