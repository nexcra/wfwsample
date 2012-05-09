/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmManiaChgFormActn
*   �ۼ���    : (��)����Ŀ�´����̼� ���ΰ�
*   ����      : ������ �����帮�������ν�û���� ������
*   �������  : golf
*   �ۼ�����  : 2009-05-20
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.admin.mania;

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
import com.bccard.golf.dbtao.proc.admin.mania.GolfAdmManiaUpdFormDaoProc;
import com.bccard.golf.dbtao.proc.mania.GolfLimousineDaoProc;


/******************************************************************************
* Topn
* @author	(��)����Ŀ�´����̼�
* @version	1.0
******************************************************************************/
public class GolfAdmManiaChgFormActn extends GolfActn{
	
	public static final String TITLE = "������ �����帮�������ν�û���� ������";

	/***************************************************************************************
	* ��ž����Ʈ ������ȭ��
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		
		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);

		try {
			// 01.��������üũ
			 
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);

			// Request �� ����
			long seq_no			= parser.getLongParameter("p_idx", 0);
			long page_no		= parser.getLongParameter("page_no", 1L);		// ��������ȣ
			long record_size	= parser.getLongParameter("record_size", 10);	// ����������¼�
			String str_userid 	= parser.getParameter("cdhd_id","");
			String subkey		= parser.getParameter("subkey", "");		
			String scoop_cp_cd	= parser.getParameter("scoop_cp_cd", ""); 		//0001:���������� 0002:��������
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setLong("RECV_NO", seq_no);
		
		
			
			 
			
			// 04.���� ���̺�(Proc) ��ȸ
			GolfAdmManiaUpdFormDaoProc proc = (GolfAdmManiaUpdFormDaoProc)context.getProc("GolfAdmManiaUpdFormDaoProc");
			DbTaoResult maniaInq = proc.execute(context, dataSet);
			
			//ȸ����� get
			int intMemGrade = proc.getIntMemGrade(context, dataSet, str_userid);
			String column = "";
			
			if(intMemGrade > 0){
				//���� ��å : Champion - 30% DC / Blue, Black - 20% DC / else Norm
				if(intMemGrade == 1){
					column = "PCT30_DC_PRIC";
				}else if(intMemGrade == 2 || intMemGrade == 5 ||  intMemGrade == 6 || intMemGrade == 7){
					column = "PCT20_DC_PRIC";
				}else{
					column = "NORM_PRIC";
				}
				
				//04.������ �ݾ� ���� ���̺� ��ȸ
				GolfLimousineDaoProc coopCpSelProc = (GolfLimousineDaoProc)context.getProc("GolfLimousineDaoProc");
				DbTaoResult coopCpSel = coopCpSelProc.execute(context, dataSet ,column); //���޾�ü
				request.setAttribute("coopCpSel", coopCpSel);	
				
			}else{
				//04.������ �ݾ� ���� ���̺� ��ȸ
				GolfLimousineDaoProc coopCpSelProc = (GolfLimousineDaoProc)context.getProc("GolfLimousineDaoProc");
				DbTaoResult coopCpSel = coopCpSelProc.execute(context, dataSet); //���޾�ü
				request.setAttribute("coopCpSel", coopCpSel);	
				
			}
			
	
			
			// 05. Return �� ����			
			//debug("maniaInq.size() ::> " + maniaInq.size());
			
			paramMap.put("page_no", String.valueOf(page_no));
			paramMap.put("record_size", String.valueOf(record_size));
			paramMap.put("cdhd_id", str_userid);
			paramMap.put("subkey", subkey);
			paramMap.put("scoop_cp_cd", scoop_cp_cd);
			
			
			request.setAttribute("maniaInqResult", maniaInq);	
			
	        request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.			
			
		} catch(Throwable t) {
			//debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}