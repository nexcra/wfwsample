/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfadmTopPenaltyListActn
*   �ۼ���    : ����
*   ����      : ������ > ��ŷ > �г�Ƽ����  > �г�Ƽ����  ���/���� ��
*   �������  : Golf
*   �ۼ�����  : 2010-11-19
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.admin.booking.premium;
   

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.waf.common.DateUtil;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.golf.msg.MsgHandler;
import com.bccard.golf.dbtao.proc.admin.booking.premium.*;
/******************************************************************************
* Topn
* @author	 
* @version	1.0 
******************************************************************************/
public class GolfadmTopPenaltyInsFrmActn extends GolfActn{
	
	public static final String TITLE = "������ > ��ŷ > �г�Ƽ����  > �г�Ƽ���� ������ ";

	/***************************************************************************************
	* ���� ������ȭ��
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/
	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException {
		debug("***********************************************************************************");
		debug(" Action  GolfadmTopPenaltyInsFrmActn.java ���� �� execute");
		debug("***********************************************************************************");
		String subpage_key = "default";	
		
		// 00.���̾ƿ� URL ����
		//TaoConnection con = null;
		String layout = super.getActionParam(context, "layout");
		String actnKey = getActionKey(context);

		request.setAttribute("layout", layout);
		
		try {
			// 01.��������üũ
			
			debug("action GolfadmTopPenaltyInsFrmActn.java try");
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			
			String roundDateFmt	= parser.getParameter("roundDateFmt", "");
			String roundDate	= parser.getParameter("roundDate", "");
			String seqNo		= parser.getParameter("seqNo", "");
			String pointDetlCd	= parser.getParameter("pointDetlCd", "");
			String name		= parser.getParameter("name", "");
			String memId		= parser.getParameter("memId", "");
			String pointMemo	= parser.getParameter("pointMemo", "");
			String penaltyApplyClss	= parser.getParameter("penaltyApplyClss", "");
			String penaltyResnCd	= parser.getParameter("penaltyResnCd", "");
			String key		= parser.getParameter("key", "");
                        long affiGreenSeqNo     = parser.getLongParameter("affiGreenSeqNo",0L) ;
			String greenNM  	= parser.getParameter("greenNM", "");
			String bbs ="0035";
			String greenNm="";
      
	
			Map paramMap = parser.getParameterMap();
			
			paramMap.put("title", TITLE);
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			
			dataSet.setLong("affiGreenSeqNo", affiGreenSeqNo);

			if("upd".equals(key)) {
				String regionClss	= parser.getParameter("s_regionClss");
				String greenNo		= parser.getParameter("s_greenNo");
				String setFromFmt	= parser.getParameter("setDateFromFmt");
				String setFrom		= parser.getParameter("setDateFrom");
				String setToFmt		= parser.getParameter("setDateToFmt");
				String setTo		= parser.getParameter("setDateTo");
                                affiGreenSeqNo          = parser.getLongParameter("affiGreenSeqNo",0L) ;
				greenNM		= parser.getParameter("greenNM");

				paramMap.put("setFromFmt", setFromFmt);
				paramMap.put("setFrom", setFrom);
				paramMap.put("setToFmt", setToFmt);
				paramMap.put("setTo", setTo);
				//paramMap.put("regionClss", regionClss);
				//paramMap.put("greenNo", greenNo);
				paramMap.put("affiGreenSeqNo",String.valueOf(affiGreenSeqNo));
				paramMap.put("greenNm",greenNm);
				paramMap.put("greenNM",greenNM);
			}
              

					paramMap.put("roundDateFmt", roundDateFmt);
					paramMap.put("roundDate", roundDate);
					//paramMap.put("seqNo", seqNo);
					paramMap.put("affiGreenSeqNo", String.valueOf(affiGreenSeqNo));
					paramMap.put("greenNM", greenNM);
		                        debug("action GolfadmTopPenaltyInsFrmActn.java key greenNM ["+greenNM+"]"); 
					paramMap.put("pointDetlCd", pointDetlCd);
					paramMap.put("name", name);
					paramMap.put("memId", memId);
					paramMap.put("pointMemo", pointMemo);
					paramMap.put("penaltyApplyClss", penaltyApplyClss);
					paramMap.put("penaltyResnCd", penaltyResnCd);
					paramMap.put("key", key);
					debug("action GolfadmTopPenaltyInsFrmActn.java key "+key);
					paramMap.put("greenNm",greenNm);
					debug("action GolfadmTopPenaltyInsFrmActn.java key greenNm ["+greenNm+"]"); 
					
					
					
					// 05. ������ ����Ʈ (Sel_Proc) ��ȸ
					GolfadmTopCodeSelDaoProc coodSelProc = (GolfadmTopCodeSelDaoProc)context.getProc("GolfadmTopCodeSelDaoProc");
					DbTaoResult codeSel = (DbTaoResult) coodSelProc.execute(context, dataSet, bbs); //�Խ��� ����
					request.setAttribute("codeSelResult", codeSel);
					

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
			try {  } catch(Throwable ignore) {}
		}
		return getActionResponse(context, subpage_key); // response key
	}

}
