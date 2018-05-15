package show.we.web.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mongodb.DBObject;
import com.zb.common.Constant.Const;
import com.zb.common.Constant.ReCode;
import com.zb.common.mongo.Op;
import com.zb.common.utils.JSONUtil;
import com.zb.core.Page;
import com.zb.core.web.ReMsg;

import ch.qos.logback.classic.Logger;
import show.we.video.ArticleCommentService;
import show.we.video.ArticleService;
import show.we.video.ArticleTopicService;
import show.we.video.model.Article;

@Controller
@RequestMapping("/group")
public class VideoCtl extends BaseCtl {

	@Autowired
	ArticleService articleService;

	@Autowired
	ArticleCommentService articleCommentService;

	@Autowired
	ArticleTopicService articleTopicService;





	// 获取文章列表 老版本
	@ResponseBody
	@RequestMapping("/articles")
	public ReMsg getArticles( Integer page, Integer size) {
		Page<DBObject> arts = articleService.query(0l, Article.OK, 0, Article.AUDITED_Y, page, size, false,
				System.currentTimeMillis(), 0L, null, null, null);
		return new ReMsg(ReCode.OK, arts);
	}

	// 获取广场文章列表 type 1 最热文章 2最新文章
	@ResponseBody
	@RequestMapping("/articlesNew")
	public ReMsg getArticlesNew(Long groupId, Integer page, Integer size, Integer type) {
		// FIXME
		Page<DBObject> arts = articleService.queryArticles(groupId, page, size, null, type);
		return new ReMsg(ReCode.OK, arts);
	}


	// 我的文章，个人中心文章
	@ResponseBody
	@RequestMapping("/myArticles")
	public ReMsg myArticles(Integer page, Integer size) {
		return articleService.getMyArticles(size, page);
	}

	// 获取文章列表,朋友圈文章
	@ResponseBody
	@RequestMapping("/userArticles")
	public ReMsg userArticles(Long userId, Integer page, Integer size) {
		if (userId == null || userId < 1) {
			return new ReMsg(ReCode.PARAMETER_ERR);
		}
		return articleService.getUserArticles(userId, size, page);
	}


	// 我的回帖
	@ResponseBody
	@RequestMapping("/userComments")
	public ReMsg myComments(Long userId, Integer page, Integer size) {
		Page<DBObject> arts = articleCommentService.findMyComments(userId, page, size);
		return new ReMsg(ReCode.OK, arts);
	}

	// 获取文章详情
	@ResponseBody
	@RequestMapping("/article")
	public ReMsg getArticle(Long id) {
		// 文章数据
		DBObject art = articleService.readArticle(id);
		Map<String, Object> rs = new HashMap<String, Object>();
		rs.put("article", art);
		return new ReMsg(ReCode.OK, rs);
	}

	// 发布文章
	@ResponseBody
	@RequestMapping(value = "/article/publish", method = RequestMethod.POST) // FIXME  POST
	public ReMsg publishArticle( String video, String content, String topic, Long topicId,
			String atUsers, HttpServletRequest req,Integer pri,String lbs,String addr,String cover) {
		String[] ps = null;
		
		String[] users = null;
		if (StringUtils.isNotBlank(atUsers)) {
			List<String> j = JSONUtil.jsonToArray(atUsers, String.class);
			users = j.toArray(new String[] {});
		}		 
		ReMsg rm = articleService.userPublishArticle(content, video, topicId, topic,
				users, req,  pri,cover,lbs,addr);
		return rm;
	}

	// 评论
	@ResponseBody
	@RequestMapping(value = "/article/comment", method = RequestMethod.POST)
	public ReMsg commentArticle(Long id, String content, String atUsers, String pics, ArrayList<Map> draws,
			String voiceUrl, HttpServletRequest req) {
		String[] users = null;
		String[] pic = null;
		if (StringUtils.isNotBlank(atUsers)) {
			List<String> j = JSONUtil.jsonToArray(atUsers, String.class);
			users = j.toArray(new String[] {});
		}
		if (StringUtils.isNotBlank(pics)) {
			List<String> t = JSONUtil.jsonToArray(pics, String.class);
			pic = t.toArray(new String[] {});
		}
		return articleService.commentArticle(id, content, users, pic, draws, voiceUrl, req);
	}

	// 回复
	@ResponseBody
	@RequestMapping(value = "/comment/reply", method = RequestMethod.POST)
	public ReMsg replyComment(Long aid, Long cid, String content, String atUsers) {
		String[] users = null;
		if (StringUtils.isNotBlank(atUsers)) {
			List<String> j = JSONUtil.jsonToArray(atUsers, String.class);
			users = j.toArray(new String[] {});
		}
		return articleService.commentReply(aid, cid, content, users);
	}

	@ResponseBody
	@RequestMapping(value = "/article/praise", method = RequestMethod.POST)
	public ReMsg praiseArticle(Long id) {
		return articleService.praiseArticle(id, Article.TYPE_PRAISE);
	}

	@ResponseBody
	@RequestMapping(value = "/article/hit", method = RequestMethod.POST)
	public ReMsg hitArticle(Long id) {
		return articleService.praiseArticle(id, Article.TYPE_HIT);
	}

	@ResponseBody
	@RequestMapping(value = "/article/share", method = RequestMethod.POST)
	public ReMsg shareArticle(Long id, String platform) {
		return articleService.shareArticle(id, platform);
	}

	@ResponseBody
	@RequestMapping(value = "/comment/praise", method = RequestMethod.POST)
	public ReMsg praiseComment(Long cid) {
		return articleCommentService.praiseComment(cid);
	}

	@ResponseBody
	@RequestMapping(value = "/comment/top")
	public ReMsg topComment(Long aid) {
		return new ReMsg(ReCode.OK, articleCommentService.getTopComments(aid, 5));
	}

	@ResponseBody
	@RequestMapping(value = "/comment/new")
	public ReMsg newComment(Long aid, Integer page, Integer size) {
		return new ReMsg(ReCode.OK, articleCommentService.query(null, aid, Const.STATUS_OK, page, size, null));
	}

	@ResponseBody
	@RequestMapping(value = "/topics/top")
	public ReMsg topTopic(Long groupId, Integer size) {
		return new ReMsg(ReCode.OK, articleTopicService.findTopic(groupId, null, null, Const.STATUS_OK, 0, size));
	}

	@ResponseBody
	@RequestMapping(value = "/topics")
	public ReMsg topics(Long groupId, Integer page, Integer size) {
		return new ReMsg(ReCode.OK, articleTopicService.queryTopic(groupId, null, null, Const.STATUS_OK, page, size));
	}

	@ResponseBody
	@RequestMapping(value = "/article/atHistory")
	public ReMsg atHistory() {
		return articleService.atHistory();
	}

	// 删除文章
	@ResponseBody
	@RequestMapping(value = "/article/del", method = RequestMethod.POST)
	public ReMsg delArticle(Long id) {
		long uid = super.getUid();
		if (uid < 1) {
			return new ReMsg(ReCode.NOT_AUTHORIZED);
		}
		return articleService.removeArticle(uid, id);
	}

	// 删除评论
	@ResponseBody
	@RequestMapping(value = "/comment/del", method = RequestMethod.POST)
	public ReMsg delComment(Long id) {
		long uid = super.getUid();
		if (uid < 1) {
			return new ReMsg(ReCode.NOT_AUTHORIZED);
		}
		return articleCommentService.removeComment(uid, id);
	}

}
