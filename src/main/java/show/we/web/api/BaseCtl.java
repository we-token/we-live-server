package show.we.web.api;

import com.zb.core.web.interceptor.OAuth2SimpleInterceptor;


public class BaseCtl {

	public long getUid() {
		Object obj = null;
		if (null!=OAuth2SimpleInterceptor.getSession()) {
			obj = OAuth2SimpleInterceptor.getSession().get("_id");
		}
		if (null != obj)
			return Long.parseLong(obj.toString());
		return 0l;
	}
	


}
