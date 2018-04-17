package show.we.web.api;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zb.core.web.ReMsg;

import show.we.live.RoomService;
import show.we.service.LiveService;

@Controller
@RequestMapping("/live")
public class LiveCtl {
	
	
	@Autowired
	LiveService liveService;
	
	@Autowired
	RoomService roomService;
	
	
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
	
	
	@ResponseBody
	@RequestMapping("/getTops")
	public ReMsg getTops(Integer page,Integer size,HttpServletRequest req){
		return roomService.getTopLives(page, size);
	}
	
	@ResponseBody
	@RequestMapping("/getNearby")
	public ReMsg getNearby(String lbs,Integer page,int size,HttpServletRequest req){
		return roomService.getNearbyLives(lbs, page, size);
	}
	
	@ResponseBody
	@RequestMapping("/uplive")
	public ReMsg getUplive(HttpServletRequest req){
		return roomService.upLive();
	}
	
	@ResponseBody
	@RequestMapping("/downlive")
	public ReMsg getdownlive(HttpServletRequest req){
		return roomService.downLive();
	}
	

}
