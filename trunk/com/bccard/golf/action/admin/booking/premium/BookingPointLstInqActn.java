/******************************************************************************
* 이 소스는 ㈜비씨카드 소유입니다.
* 이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
* 작성 : 2004.10.31 [이훈주]
* 내용 : 포인트관리 목록 조회 액션
* 수정 : 
* 내용 : 해당조건의 포인트 목록을 조회한다.
******************************************************************************/
package com.bccard.golf.action.admin.booking.premium;


import java.io.IOException;
import com.bccard.waf.core.RequestParser;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.util.Map;

import com.bccard.waf.core.WaContext;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.action.AbstractAction;
import com.bccard.waf.tao.TaoConnection;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoResult;
import com.bccard.waf.common.DateUtil;
 
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.golf.msg.MsgHandler;

import com.bccard.golf.dbtao.proc.admin.booking.premium.BookingPointLstInqProc;


// import com.bccard.golf.common.SessionUserEtt;
// import com.bccard.golf.common.CardInfoEtt;
/** ****************************************************************************
 * 포인트 목록 조회 수행 액션.
 * @author 이훈주
 * @version 2004.10.31
 **************************************************************************** */
public class BookingPointLstInqActn extends AbstractAction {

    public static final String TITLE ="포인트관리 목록조회";
  
    /**************************************************************************
    * Proc 실행.
    * @param con Connection
    * @param context WaContext 객체
    * @param request HttpServletRequest 객체
    * @param response HttpServletResponse 객체
    ************************************************************************ */    
    public ActionResponse execute(WaContext context,
                                  HttpServletRequest request, 
                                  HttpServletResponse response) throws ServletException, IOException, BaseException {    
        TaoConnection con = null;
        String curActionKey = getActionKey(context);

		try {
            TaoDataSet dataSet = new DbTaoDataSet(TITLE);            
            RequestParser parser = context.getRequestParser("default",request,response);
			String nowYear      =   DateUtil.currdate("yyyy");             //시스템 날짜(year)
			String nowMonth     =   DateUtil.currdate("MM");               //시스템 날짜 (month)
			int term            =   DateUtil.getMonthlyDayCount(Integer.parseInt(nowYear),Integer.parseInt(nowMonth));       // 말일
			String DateFromFmt  =   nowYear + "." + nowMonth + "." + "01";                  //기본 시작일
			String DateToFmt    =   nowYear + "." + nowMonth + "." + String.valueOf(term);  //기본 종료일
			String DateFrom     =   DateUtil.format(DateFromFmt,"yyyy.MM.dd","yyyyMMdd");   //기본 시작일 포멧
	    String DateTo       =   DateUtil.format(DateToFmt,"yyyy.MM.dd","yyyyMMdd");     //기본 종료일 포멧	

			String roundDateFrom = parser.getParameter("roundDateFrom",DateFrom);
			String roundDateFromFmt = parser.getParameter("roundDateFromFmt",DateFromFmt);
			String roundDateTo = parser.getParameter("roundDateTo",DateTo);
			String roundDateToFmt = parser.getParameter("roundDateToFmt",DateToFmt);
			String pointDetlCd = parser.getParameter("pointDetlCd", null);
			long pageNo = parser.getLongParameter("pageNo",1L);
      long recordsInPage = 10L;//parser.getLongParameter("recordsInPage",10L);
                                                
      dataSet.setString("roundDateFrom",roundDateFrom);
      dataSet.setString("roundDateTo",roundDateTo);
      dataSet.setString("pointDetlCd",pointDetlCd);
			dataSet.setLong("pageNo",pageNo);
			dataSet.setLong("recordsInPage",recordsInPage);

			con = context.getTaoConnection("dbtao",null);            
			
			TaoResult boardInqLst = con.execute("BookingPointLstInqProc", dataSet);
                        
			Map paramMap = parser.getParameterMap();

			paramMap.put("roundDateFrom", roundDateFrom);
			paramMap.put("roundDateFromFmt", roundDateFromFmt);
			paramMap.put("roundDateTo", roundDateTo);
			paramMap.put("roundDateToFmt", roundDateToFmt);
			paramMap.put("pointDetlCd", pointDetlCd);
			paramMap.put("pageNo", String.valueOf(pageNo));
			paramMap.put("recordsInPage", String.valueOf(recordsInPage));

			if(boardInqLst.isNext()){
				boardInqLst.next();
				if("00".equals(boardInqLst.getString("result"))){
					paramMap.put("recordCnt", String.valueOf(boardInqLst.getLong("recordCnt")));
				}else{
					paramMap.put("recordCnt", "0");
				}
			}

                        request.setAttribute("taoResult",boardInqLst);
			request.setAttribute("paramMap",paramMap);
		} catch(BaseException be) {
			throw be;
		} catch(Throwable t) {
			MsgEtt ett = null;
			if ( t instanceof MsgHandler ) {
				ett = ((MsgHandler)t).getMsgEtt();
				ett.setTitle(TITLE);
			} else {
				ett = new MsgEtt(MsgEtt.TYPE_ERROR,TITLE,t.getMessage());
			}
			throw new GolfException(ett,t);
		} finally {
			try { con.close(); } catch(Throwable ignore) {}
		}
		return getActionResponse(context); // response key
	}
}
