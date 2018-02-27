package show.we.service;

import org.springframework.stereotype.Service;

import com.zb.common.Constant.ReCode;
import com.zb.common.crypto.MDUtil;
import com.zb.common.utils.RandomUtil;
import com.zb.core.conf.Config;
import com.zb.core.web.ReMsg;
import com.zb.service.BaseService;


@Service
public class LiveService extends BaseService{
	
	private static final String PUSH_STREAM_DOMAIN = "rtmp://video-center-sg.alivecdn.com";
	
	private static final String PULL_STREAM_DOMAIN = "live.zhuangdianbi.com";
	
	public ReMsg getPushStreamUrl(){
		long uid = super.getUid();
		if(uid<0){
			return new ReMsg(ReCode.ACCESS_TOKEN_ERR);
		}
		if(!validUpUser(uid)){
			return new ReMsg(ReCode.FAIL);
		}
		String url = getStreamUrl(true,uid);
		return new ReMsg(ReCode.OK,url);
	}
	
	
	
	public ReMsg getPullStreamUrl(Long roomId){
		String url = getStreamUrl(false,roomId);
		return new ReMsg(ReCode.OK,url);
	}
	
	private boolean validUpUser(Long uid){
		//TODO 验证用户是否开通直播权限
		return true;
	}
	
	private String getStreamUrl(boolean center ,long uid){
		String uri = "/live/r"+uid;
		String key = Config.cur().get("live.ali.key","");
		String ak = System.currentTimeMillis()/1000+24*60*60+"-"+RandomUtil.nextInt(100000)+"-"+0;
		ak =  ak+"-"+MDUtil.MD5.digest2HEX(uri+"-"+ak+"-"+key);
		if(center){
			return PUSH_STREAM_DOMAIN +uri+"?vhost="+PULL_STREAM_DOMAIN+"&auth_key="+ak;
		}else{
			return "rtmp://"+PULL_STREAM_DOMAIN +uri+"?auth_key="+ak;
		}
	}
	
//	public static void main(String [] args){
//		LiveService ls = new LiveService();
//		System.out.println(ls.getStreamUrl(false, 111));
//	}
	
}
