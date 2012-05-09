/******************************************************************************
* �� �ҽ��� �ߺ�ī�� �����Դϴ�.
* �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
* �ۼ� : 2004.10.31 [������]
* ���� : ����Ʈ���� ��� ��ȸ �׼�
* ���� : 
* ���� : �ش������� ����Ʈ ����� ��ȸ�Ѵ�.
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
 * ����Ʈ ��� ��ȸ ���� �׼�.
 * @author ������
 * @version 2004.10.31
 **************************************************************************** */
public class BookingPointLstInqActn extends AbstractAction {

    public static final String TITLE ="����Ʈ���� �����ȸ";
  
    /**************************************************************************
    * Proc ����.
    * @param con Connection
    * @param context WaContext ��ü
    * @param request HttpServletRequest ��ü
    * @param response HttpServletResponse ��ü
    ************************************************************************ */    
    public ActionResponse execute(WaContext context,
                                  HttpServletRequest request, 
                                  HttpServletResponse response) throws ServletException, IOException, BaseException {    
        TaoConnection con = null;
        String curActionKey = getActionKey(context);

		try {
            TaoDataSet dataSet = new DbTaoDataSet(TITLE);            
            RequestParser parser = context.getRequestParser("default",request,response);
			String nowYear      =   DateUtil.currdate("yyyy");             //�ý��� ��¥(year)
			String nowMonth     =   DateUtil.currdate("MM");               //�ý��� ��¥ (month)
			int term            =   DateUtil.getMonthlyDayCount(Integer.parseInt(nowYear),Integer.parseInt(nowMonth));       // ����
			String DateFromFmt  =   nowYear + "." + nowMonth + "." + "01";                  //�⺻ ������
			String DateToFmt    =   nowYear + "." + nowMonth + "." + String.valueOf(term);  //�⺻ ������
			String DateFrom     =   DateUtil.format(DateFromFmt,"yyyy.MM.dd","yyyyMMdd");   //�⺻ ������ ����
	    String DateTo       =   DateUtil.format(DateToFmt,"yyyy.MM.dd","yyyyMMdd");     //�⺻ ������ ����	

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
