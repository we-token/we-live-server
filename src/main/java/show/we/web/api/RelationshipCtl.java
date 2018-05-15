package show.we.web.api;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mongodb.DBObject;
import com.zb.common.Constant.ReCode;
import com.zb.common.utils.JSONUtil;
import com.zb.core.Page;
import com.zb.core.web.ReMsg;

import show.we.relation.RelationshipService;
import show.we.relation.model.Relationship;
import show.we.user.UserService;

@Controller
@RequestMapping("/user/relationship")
public class RelationshipCtl extends BaseCtl {

	@Autowired
	RelationshipService relationshipService;
	@Autowired
	UserService userService;

	// 获取用户的联系人 －1，黑名单，2 已关注用户 3好友
	@ResponseBody
	@RequestMapping("/my")
	public ReMsg my(Integer page, Integer size) {
		Long uid = super.getUid();
		if (uid < 1) {
			return new ReMsg(ReCode.NOT_AUTHORIZED);
		}
		size = 200;
		Page<DBObject> arts = relationshipService.query(uid, null, Relationship.FRIENDS, page, size, null);

		return new ReMsg(ReCode.OK, arts);
	}

	// 最近联系人
	@ResponseBody
	@RequestMapping("/recently")
	public ReMsg recently(String ids) {
		Long uid = super.getUid();
		if (uid < 1) {
			return new ReMsg(ReCode.NOT_AUTHORIZED);
		}
		if (StringUtils.isBlank(ids)) {
			return new ReMsg(ReCode.FAIL);
		}
		Long[] users = null;
		List<Long> t = JSONUtil.jsonToArray(ids, Long.class);
		users = t.toArray(new Long[] {});
		return new ReMsg(ReCode.OK, userService.findByids(users));
	}

	// 获取用户的联系人 －1，黑名单，2 已关注用户 3好友
	@ResponseBody
	@RequestMapping("/cur")
	public ReMsg my(Long rid) {
		return relationshipService.getRelation(rid);
	}

	// 好友数量
	@ResponseBody
	@RequestMapping("/friendsCnt")
	public ReMsg friendsCnt() {
		return relationshipService.queryFriendsCnt();
	}

	@ResponseBody
	@RequestMapping("/add")
	public ReMsg addFriend(Long rid, Integer local, Long localId) {
		if (null == rid || rid < 1) {
			return new ReMsg(ReCode.FAIL);
		}
		return relationshipService.add(rid, local, localId);
	}

	@ResponseBody
	@RequestMapping("/addReply")
	public ReMsg addReply(Long rid, int status) {
		if (null == rid || rid < 1) {
			return new ReMsg(ReCode.FAIL);
		}
		return relationshipService.addReply(rid, status);
	}

	@ResponseBody
	@RequestMapping("/del")
	public ReMsg del(Long rid) {
		return relationshipService.black(rid, 1);
		// return relationshipService.del(rid);
	}

	@ResponseBody
	@RequestMapping("/black")
	public ReMsg black(Long rid, Integer status) {
		if (null == rid || rid < 1) {
			return new ReMsg(ReCode.FAIL);
		}
		return relationshipService.black(rid, status);
	}

}
