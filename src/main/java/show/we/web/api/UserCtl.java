package show.we.web.api;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.zb.common.Constant.ReCode;
import com.zb.common.utils.RegexUtil;
import com.zb.core.web.ReMsg;

import show.we.user.AuthService;
import show.we.user.UserService;

@Controller
@RequestMapping("/user")
public class UserCtl extends BaseCtl {

	@Autowired
	UserService userService;

	@Autowired
	AuthService authService;

	@ResponseBody
	@RequestMapping("/checkNickname")
	public ReMsg checkUsername(String nickname) {
		if (!RegexUtil.isNickname(nickname)) {
			return new ReMsg("用户昵称2-16位！");
		}
		return userService.checkNickname(nickname, super.getUid());
	}

	@ResponseBody
	@RequestMapping("/updateUser")
	public ReMsg updateUser(String nickname, Integer sex, String avatar) {
		if (null != nickname) {
			nickname = nickname.trim();
			if (!RegexUtil.isNickname(nickname)) {
				return new ReMsg("用户昵称2-16位！");
			}
		}
		return userService.updateUser(sex, nickname, avatar);
	}

	@ResponseBody
	@RequestMapping("/updateUserNew")
	public ReMsg updateUserNew(String nickname, Integer sex, String interests, String personLabel, String avatar,
			String cover, String photos, String phone) {
		if (null != nickname) {
			nickname = nickname.trim();
			if (!RegexUtil.isNickname(nickname)) {
				return new ReMsg("用户昵称2-16位！");
			}
		}
		return userService.updateUser(nickname, sex, interests, personLabel, avatar, cover, photos, phone);
	}

	@ResponseBody
	@RequestMapping("/updateLbs")
	public ReMsg updateUserLbs(String lbs, HttpServletRequest req) {
		return userService.updateUserLbs(lbs, req);
	}

	@ResponseBody
	@RequestMapping("/top")
	public ReMsg topUser(int size) {
		return new ReMsg(ReCode.OK, userService.findTopUser(size));
	}

	@ResponseBody
	@RequestMapping("/updatePwd")
	public ReMsg updatePwd(String newPwd, String oldPwd, HttpServletRequest req) {
		if (!RegexUtil.isPassword(newPwd)) {
			return new ReMsg("密码为6－20位！");
		}
		return userService.updatePwd(oldPwd, newPwd, req);
	}

	@ResponseBody
	@RequestMapping("/set/worth")
	public ReMsg change(int worth, HttpServletRequest req) {
		return userService.changeFriendWorth(worth);
	}

	@ResponseBody
	@RequestMapping("/myinfo")
	public ReMsg getMyInfo(HttpServletRequest req) throws JsonParseException, JsonMappingException, IOException {
		return userService.getMyInfo(req);
	}

	@ResponseBody
	@RequestMapping("/{id}")
	public ReMsg getBy(HttpServletRequest request, @PathVariable Long id) {
		return userService.getUserInfo(id);
	}

	@ResponseBody
	@RequestMapping("/simple/{id}")
	public ReMsg getUserSimpleInfo(HttpServletRequest request, @PathVariable Long id) {
		return new ReMsg(ReCode.OK, userService.findByIdSafe(id));
	}




	/** 获取金币 */
	@ResponseBody
	@RequestMapping("/findMyCoin")
	public ReMsg findMyCoin() {
		return userService.findMyCoin();
	}

}
