package com.bccard.golf.action.mania;

import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.event.shop.GolfEvntShopViewDaoProc;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;
import java.io.IOException;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class GolfEgolfExpoInqActn extends GolfActn
{
  public static final String TITLE = "이델리일 골프엑스포  온라인 사전등록 ";

  public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException, BaseException
  {
    String subpage_key = "default";
    String userNm = "";
    String userId = "";
    String juminno = "";
    String juminno1 = "";
    String juminno2 = "";
    String zip_code1 = "";
    String zip_code2 = "";
    String zipaddr = "";
    String detailaddr = "";
    String addrClss = "";
    String mobile1 = "";
    String mobile2 = "";
    String mobile3 = "";
    String memGubun = "";

    String layout = super.getActionParam(context, "layout");
    String errReUrl = super.getActionParam(context, "errReUrl");
    request.setAttribute("layout", layout);
    try
    {
      HttpSession session = request.getSession(true);
      UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);

      RequestParser parser = context.getRequestParser(subpage_key, request, response);
      Map paramMap = BaseAction.getParamToMap(request);
      paramMap.put("title", "이델리일 골프엑스포  온라인 사전등록 ");

      DbTaoDataSet dataSet = new DbTaoDataSet("이델리일 골프엑스포  온라인 사전등록 ");

      GolfEvntShopViewDaoProc proc = (GolfEvntShopViewDaoProc)context.getProc("GolfEvntShopViewDaoProc");

      if (usrEntity != null)
      {
        userNm = usrEntity.getName();
        userId = usrEntity.getAccount();
        juminno = usrEntity.getSocid();
        juminno1 = juminno.substring(0, 6);
        juminno2 = juminno.substring(6, 13);
        mobile1 = usrEntity.getMobile1();
        mobile2 = usrEntity.getMobile2();
        mobile3 = usrEntity.getMobile3();

        dataSet.setString("userId", userId);
        DbTaoResult emEtt = proc.execute_mem(context, request, dataSet);
        if ((emEtt != null) && (emEtt.isNext()) && (emEtt.size() > 0)) {
          emEtt.next();
          zip_code1 = emEtt.getString("ZIP_CODE1");
          zip_code2 = emEtt.getString("ZIP_CODE2");
          zipaddr = emEtt.getString("ZIPADDR");
          detailaddr = emEtt.getString("DETAILADDR");
          addrClss = emEtt.getString("NW_OLD_ADDR_CLSS");
          memGubun = "회원";
        } else {
          memGubun = "비회원";
        }

        paramMap.put("userNm", userNm);
        paramMap.put("userId", userId);
        paramMap.put("juminno1", juminno1);
        paramMap.put("juminno2", juminno2);
        paramMap.put("mobile1", mobile1);
        paramMap.put("mobile2", mobile2);
        paramMap.put("mobile3", mobile3);
        paramMap.put("zip_code1", zip_code1);
        paramMap.put("zip_code2", zip_code2);
        paramMap.put("zipaddr", zipaddr);
        paramMap.put("detailaddr", detailaddr);
        paramMap.put("addrClss", addrClss);
        paramMap.put("memgubun", memGubun);
      }

      request.setAttribute("paramMap", paramMap);
    }
    catch (Throwable t) {
      debug("이델리일 골프엑스포  온라인 사전등록 ", t);
      throw new GolfException("이델리일 골프엑스포  온라인 사전등록 ", t);
    }

    return super.getActionResponse(context, subpage_key);
  }
}