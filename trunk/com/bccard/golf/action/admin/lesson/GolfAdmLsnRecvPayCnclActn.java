/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfLsnRecvPayCnclActn
*   �ۼ���    : (��)����Ŀ�´����̼� ���¿�
*   ����      : ������û ���� ���
*   �������  : golf
*   �ۼ�����  : 2009-06-25
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.admin.lesson;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;

import com.bccard.golf.common.GolfPayAuthEtt;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentDaoProc;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentUpdDaoProc;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentInqDaoProc;

/******************************************************************************
* Golf
* @author	(��)����Ŀ�´����̼�
* @version	1.0
******************************************************************************/
public class GolfAdmLsnRecvPayCnclActn extends GolfActn{
	
	public static final String TITLE = "������û ���� ���";

	/***************************************************************************************
	* ���� �����ȭ��
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
	    
		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout");
		String reUrl = super.getActionParam(context, "reUrl");
		String errReUrl = super.getActionParam(context, "errReUrl");
		request.setAttribute("layout", layout);

		GolfPayAuthEtt payEtt = new GolfPayAuthEtt();			// ��������
		try {
			// 01.��������üũ
			
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			
			// Request �� ����
			String odr_no			= parser.getParameter("odr_no", "");
			String cdhd_id			= parser.getParameter("cdhd_id", "");

			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("ODR_NO", odr_no);
			dataSet.setString("CDHD_ID", cdhd_id);
			
			// 04.���� ���̺�(Proc) ��ȸ
			GolfPaymentDaoProc payProc = (GolfPaymentDaoProc)context.getProc("GolfPaymentDaoProc");
			GolfPaymentInqDaoProc inqProc = (GolfPaymentInqDaoProc)context.getProc("GolfPaymentInqDaoProc");
			GolfPaymentUpdDaoProc updProc = (GolfPaymentUpdDaoProc)context.getProc("GolfPaymentUpdDaoProc");

			DbTaoResult payInfoResult = (DbTaoResult) inqProc.getPaymentInfo(context, dataSet);
			if (payInfoResult != null && payInfoResult.isNext()) {
				payInfoResult.first();
				payInfoResult.next();
				if (payInfoResult.getObject("RESULT").equals("00")) {
					payEtt.setMerMgmtNo((String)payInfoResult.getString("MER_NO"));
					payEtt.setCardNo((String)payInfoResult.getString("CARD_NO"));
					payEtt.setValid((String)payInfoResult.getString("VALD_DATE").substring(3, 4));
					payEtt.setAmount((String)payInfoResult.getString("STTL_AMT"));					
					String insTerm = (String)payInfoResult.getString("INS_MCNT");
					if (insTerm.length() == 1) insTerm = "0"+insTerm;
					payEtt.setInsTerm(insTerm);
					payEtt.setUseNo((String)payInfoResult.getString("AUTH_NO"));
					payEtt.setRemoteAddr(request.getRemoteAddr());
				}
			}
			
			boolean payCancelResult = false;
			payCancelResult = payProc.executePayAuthCancel(context, payEtt);			// ������� ȣ��

			debug("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");		
			debug("=====> MerMgmtNo : " + payEtt.getMerMgmtNo());
			debug("=====> CardNo : " + payEtt.getCardNo());
			debug("=====> Valid : " + payEtt.getValid());
			debug("=====> Amount : " + payEtt.getAmount());
			debug("=====> InsTerm : " + payEtt.getInsTerm());
			debug("=====> UseNo : " + payEtt.getUseNo());
			debug("=====> RemoteAddr : " + payEtt.getRemoteAddr());
			debug("=====> payCancelResult : " + payCancelResult);			
			debug("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"); 
			
			int cnclResult = 0;
			// ���� ���� �Ϸ�
			if (payCancelResult) { // ��~~ �����ؾ� �� ����
				cnclResult = updProc.execute(context, dataSet);		
			}	
			
	        if (cnclResult == 1) {
				request.setAttribute("returnUrl", reUrl);
				request.setAttribute("resultMsg", "");	
	        } else {				
				request.setAttribute("returnUrl", errReUrl);
				request.setAttribute("resultMsg", "���� ��Ұ� ���������� ó�� ���� �ʾҽ��ϴ�.\\n\\n�ݺ������� �������� ���� ��� �����ڿ� �����Ͻʽÿ�.");		        		
	        }	
			
			// 05. Return �� ����
	        request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.			
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
