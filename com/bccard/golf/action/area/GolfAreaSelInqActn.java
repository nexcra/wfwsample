package com.bccard.golf.action.area;

import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.area.GolfAreaSelInqDaoProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;
import java.io.IOException;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class GolfAreaSelInqActn extends GolfActn
{
  public static final String TITLE = "���������� ����Ʈ�ڽ� ����";

  public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException, BaseException
  {
    String subpage_key = "default";

    String layout = super.getActionParam(context, "layout");
    request.setAttribute("layout", layout);
    try
    {
      RequestParser parser = context.getRequestParser(subpage_key, request, response);
      Map paramMap = BaseAction.getParamToMap(request);
      paramMap.put("title", "���������� ����Ʈ�ڽ� ����");

      String sido = parser.getParameter("s_sido", "");
      String gugun = parser.getParameter("s_gugun", "");

      DbTaoDataSet dataSet = new DbTaoDataSet("���������� ����Ʈ�ڽ� ����");
      dataSet.setString("SIDO", sido);
      dataSet.setString("GUGUN", gugun);

      GolfAreaSelInqDaoProc proc = (GolfAreaSelInqDaoProc)context.getProc("GolfAreaSelInqDaoProc");
      DbTaoResult result = proc.execute(context, request, dataSet);

      request.setAttribute("result", result);
      request.setAttribute("paramMap", paramMap);
    }
    catch (Throwable t) {
      debug("���������� ����Ʈ�ڽ� ����", t);

      throw new GolfException("���������� ����Ʈ�ڽ� ����", t);
    }

    return super.getActionResponse(context, subpage_key);
  }
}