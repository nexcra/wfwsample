package com.bccard.golf.action.admin.event;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.admin.event.GolfAdmSLessonViewDaoProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;
import java.io.IOException;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class GolfAdmBsDetailActn extends GolfActn
{
  public static final String TITLE = "관리자 BC Golf 특별레슨 이벤트  상세페이지";

  public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException, BaseException
  {
    String subpage_key = "default";

    String layout = super.getActionParam(context, "layout");
    request.setAttribute("layout", layout);
    String rtnCode = "";
    String rtnMsg = "";
    try
    {
      RequestParser parser = context.getRequestParser(subpage_key, request, response);
      Map paramMap = BaseAction.getParamToMap(request);
      paramMap.put("title", "관리자 BC Golf 특별레슨 이벤트  상세페이지");

      paramMap.put("imgUrlPath", AppConfig.getAppProperty("IMG_URL_REAL") + "/event");

      long seq = parser.getLongParameter("seq", 0L);
      debug("===================//// check : " + seq);

      DbTaoDataSet dataSet = new DbTaoDataSet("관리자 BC Golf 특별레슨 이벤트  상세페이지");
      dataSet.setLong("EVNT_SEQ_NO", seq);
      debug("===================//// check2222 : " + seq);

      GolfAdmSLessonViewDaoProc proc = (GolfAdmSLessonViewDaoProc)context.getProc("GolfAdmSLessonViewDaoProc");
      DbTaoResult evntBsViewResult = proc.execute(context, request, dataSet);

      if ((evntBsViewResult != null) && (evntBsViewResult.size() > 0)) {
        rtnCode = "00";
        rtnMsg = "";
      } else {
        rtnCode = "01";
        rtnMsg = "해당 메뉴가 없습니다.";
      }
      paramMap.put("resultSize", String.valueOf(evntBsViewResult.size()));
      paramMap.put("seq", String.valueOf(seq));

      request.setAttribute("evntBsViewResult", evntBsViewResult);
      request.setAttribute("paramMap", paramMap);
      request.setAttribute("rtnCode", rtnCode);
      request.setAttribute("rtnMsg", rtnMsg);
    }
    catch (Throwable t) {
      debug("관리자 BC Golf 특별레슨 이벤트  상세페이지", t);

      throw new GolfException("관리자 BC Golf 특별레슨 이벤트  상세페이지", t);
    }

    return super.getActionResponse(context, subpage_key);
  }
}