package show.we.web.api;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zb.core.web.ReMsg;

import show.we.user.AuthService;

@Controller
@RequestMapping("/api")
public class ThirdLoginCtl extends BaseCtl {
	static final Logger log = LoggerFactory.getLogger(ThirdLoginCtl.class);

	@Autowired
	AuthService authService;

	@ResponseBody
	@RequestMapping("/thirdLogin")
	public ReMsg thridLogin(HttpServletRequest req, String accessToken,
			String openId, int loginType,String appCode) throws IOException {
		return authService.login(loginType, accessToken, openId,appCode, req);
	}



}
