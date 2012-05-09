/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfTopGolfCardNoticeViewActn
*   �ۼ���    : �̵������ �ǿ���
*   ����      : ž����ī�� �������� �󼼺���
*   �������  : Golf
*   �ۼ�����  : 2010-10-25
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.booking.premium;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.bbs.GolfBoardComtListDaoProc;
import com.bccard.golf.dbtao.proc.bbs.GolfBoardInqDaoProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;
/******************************************************************************
* Topn
* @author	(��)�̵������
* @version	1.0
******************************************************************************/
public class GolfTopGolfCardNoticeViewActn extends GolfActn{
	
	public static final String TITLE = "ž����ī�� �������� �󼼺���";

	/***************************************************************************************
	* Golf ������ȭ��
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";
				
		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout");
		String actnKey = super.getActionKey(context);
		request.setAttribute("layout", layout);
		try {
			
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			HashMap resultMap = new HashMap();
			paramMap.put("title", TITLE);

			// Request �� ����
			String seq_no			= parser.getParameter("p_idx", "75"); 
			String bbs				= parser.getParameter("bbs", "0062");
			String search_sel		= parser.getParameter("search_sel", "");
			String search_word		= parser.getParameter("search_word", "");
			String sch_field_cd		= parser.getParameter("sch_field_cd", ""); 
			String sch_clss_cd		= parser.getParameter("sch_clss_cd", ""); 
			String sch_sec_cd		= parser.getParameter("sch_sec_cd", ""); 
			String sch_hd_yn		= parser.getParameter("sch_hd_yn", ""); 	
			String reply_clss		= parser.getParameter("reply_clss", "0001");	
			String comt_papeing		= parser.getParameter("comt_papeing", "Y");	
						
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("SEQ_NO", seq_no);
			dataSet.setString("BBS", bbs);
			dataSet.setString("SEARCH_SEL", search_sel);
			dataSet.setString("SEARCH_WORD", search_word);
			dataSet.setString("SCH_FIELD_CD", sch_field_cd);
			dataSet.setString("SCH_CLSS_CD", sch_clss_cd);
			dataSet.setString("SCH_SEC_CD", sch_sec_cd);
			dataSet.setString("SCH_HD_YN", sch_hd_yn);
			dataSet.setString("REPLY_CLSS", reply_clss);
			dataSet.setString("actnKey", actnKey);
			
			// 04.���� ���̺�(Proc) ��ȸ
			GolfBoardInqDaoProc proc = (GolfBoardInqDaoProc)context.getProc("GolfBoardInqDaoProc");
			GolfBoardComtListDaoProc proc2 = (GolfBoardComtListDaoProc)context.getProc("GolfBoardComtListDaoProc");
			resultMap = proc.execute(context, resultMap, dataSet);
			
			DbTaoResult bbsComtListResult = null;

			if (comt_papeing.equals("Y")) {
				bbsComtListResult = (DbTaoResult) proc2.execute(context, request, dataSet);			
			} else {
				bbsComtListResult = (DbTaoResult) proc2.execute_noPageing(context, request, dataSet);
			}

			// �����Ͱ� ������ ����/����/��ȸ�� ������Ʈ ����
			DbTaoResult preNextInfoResult = null;
			if (resultMap.get("RESULT").equals("00")) {
				
				// ������ ������ ��ȣ ��������
				preNextInfoResult = proc.getPreNextInfo(context, dataSet);
				
				// ��ȸ�� ������Ʈ
				int readCntUpdResult = proc.readCntUpd(context, dataSet);
				if (readCntUpdResult > 0) {
					Integer readCnt = new Integer(resultMap.get("INOR_NUM").toString());
					resultMap.put("INOR_NUM",  String.valueOf(readCnt.intValue() + 1));
				}
			}
			
			paramMap.put("bbsReListSize", String.valueOf(bbsComtListResult.size()));
			
			// 05. Return �� ����
			request.setAttribute("bbsInqResult", resultMap);
			request.setAttribute("bbsComtListResult", bbsComtListResult);
			request.setAttribute("preNextInfoResult", preNextInfoResult);
	        request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.			
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
