package show.we.web.api;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zb.core.web.ReMsg;

import show.we.service.LiveService;

@Controller
@RequestMapping("/live")
public class LiveCtl {
	
	
	@Autowired
	LiveService liveService;
	
	
	@ResponseBody
	@RequestMapping("/getMyStreamUrl")
	public ReMsg getMyStream(String token,HttpServletRequest req){
		return liveService.getPushStreamUrl();
	}
	
	
	@ResponseBody
	@RequestMapping("/getStreamUrl")
	public ReMsg getStreamUrl(Long roomId,HttpServletRequest req){
		return liveService.getPullStreamUrl(roomId);
	}

}
