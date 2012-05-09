package testbc;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.golf.common.GolfException;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.golf.msg.MsgHandler;
import com.bccard.waf.action.AbstractAction;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoConnection;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoResult;
import com.bccard.waf.tao.jolt.JoltInput;

public class JoltConnectionTestActn extends AbstractAction {

	private static final String TITLE = "Jolt Å×½ºÆ®";

	public ActionResponse execute(WaContext context,
			HttpServletRequest request,
			HttpServletResponse response) throws IOException,
			ServletException, BaseException {
		TaoConnection con = null;
		String curActionKey = getActionKey(context);
		
		try {
			TaoDataSet dataSet = new DbTaoDataSet(TITLE);
			RequestParser parser = context.getRequestParser("default", request, response);
			
			con = context.getTaoConnection("dbtao", null);
//			con = context.getTaoConnection("jolt", null);
			JoltInput ji = new JoltInput("JoltConnectionTestProc");
			TaoResult inqList = con.execute("JoltConnectionTestProc", dataSet);
//			TaoResult inqList = con.execute("JoltConnectionTestProc", ji);
			
			Map paramMap = parser.getParameterMap();
			
			if (inqList.isNext()) {
				inqList.next();
				if ("00".equals(inqList.getString("result"))) {
					paramMap.put("recordCnt", String.valueOf(inqList.size()));
				} else {
					paramMap.put("recordCnt", "0");
				}
			}
			
			request.setAttribute("taoResult", inqList);
			request.setAttribute("paramMap", paramMap);
		} catch (BaseException be) {
			throw be;
		} catch (Throwable t) {
			MsgEtt ett = null;
			if ( t instanceof MsgHandler) {
				ett = ((MsgHandler)t).getMsgEtt();
				ett.setTitle(TITLE);
			} else {
				ett = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, t.getMessage());
			}
			throw new GolfException(ett, t);
		} finally {
			try { con.close(); } catch (Throwable ignore) {}
		}
		
		return getActionResponse(context);
	}

}
