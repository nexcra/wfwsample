package com.bccard.golf.action.admin.event;

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.dbtao.DbTaoConnection;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class GolfAdmBsWriteFormActn extends GolfActn
{
  public static final String TITLE = "게시판 관리 등록 폼";

  public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response)
    throws BaseException
  {
    DbTaoConnection con = null;
    try
    {
      RequestParser parser = context.getRequestParser("default", request, response);

      Map paramMap = parser.getParameterMap();
      request.setAttribute("paramMap", paramMap);
    }
    catch (Throwable t)
    {
      t.printStackTrace();
      return errorHandler(context, request, response, t); } finally {
      try {
        if (con != null) con.close();  } catch (Exception localException1) {
      }
    }
    return super.getActionResponse(context);
  }
}