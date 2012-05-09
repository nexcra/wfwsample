/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmEvntBnstUpdActn.java
*   �ۼ���    : ������
*   ����      : ������ > �̺�Ʈ > ����ȸ > �󼼺��� > ����ó��
*   �������  : golf
*   �ۼ�����  : 2010-10-07
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.admin.event.benest;

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
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.proc.admin.event.benest.GolfAdmEvntBnstUpdDaoProc;

/******************************************************************************
* Golf
* @author	(��)�̵������ 
* @version	1.0
******************************************************************************/
public class GolfAdmEvntBnstUpdActn extends GolfActn{

	public static final String TITLE = "������ > �̺�Ʈ > ����ȸ > �󼼺��� > ����ó��";

	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";
		
		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);

		try {
			// 02.�Է°� ��ȸ�Ѵ�.
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);

			// �������� ����
			//String upd_type			= parser.getParameter("upd_type", "");
			String aplc_seq_no		= parser.getParameter("aplc_seq_no", "");
			String seq_no			= parser.getParameter("seq_no", "");
			String cdhd_grd_seq_no	= parser.getParameter("cdhd_grd_seq_no", "");
			String sttl_stat_clss	= parser.getParameter("STTL_STAT_CLSS", "");
			String evnt_pgrs_clss	= parser.getParameter("EVNT_PGRS_CLSS", "");
			String mgr_memo			= parser.getParameter("MGR_MEMO", "");
			int cnt					= parser.getIntParameter("CNT");
			String note				= parser.getParameter("note", "");
			String green_nm			= parser.getParameter("green_nm", "");
			String months			= parser.getParameter("months", "");
			String rsvt_date		= parser.getParameter("RSVT_DATE", "");
			String rsv_time			= parser.getParameter("RSV_TIME", "");
			String trm_unt			= parser.getParameter("trm_unt", "");
			if(!GolfUtil.empty(rsvt_date)){
				rsvt_date = GolfUtil.replace(rsvt_date, "-", "");
			}
									
						
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("aplc_seq_no", aplc_seq_no);
			dataSet.setString("seq_no", seq_no);
			dataSet.setString("cdhd_grd_seq_no", cdhd_grd_seq_no);
			dataSet.setString("sttl_stat_clss", sttl_stat_clss);
			dataSet.setString("evnt_pgrs_clss", evnt_pgrs_clss);
			dataSet.setString("mgr_memo", mgr_memo);
			dataSet.setString("mgr_memo", mgr_memo);
			dataSet.setInt("cnt", cnt);
			dataSet.setString("note", note);
			dataSet.setString("green_nm", green_nm);
			dataSet.setString("months", months);
			dataSet.setString("rsvt_date", rsvt_date);
			dataSet.setString("rsv_time", rsv_time);
			dataSet.setString("trm_unt", trm_unt);

			for(int i=1; i<20; i++){
				dataSet.setString("del_yn"+i, parser.getParameter("DEL_YN"+i, ""));
				dataSet.setString("seq_no"+i,  parser.getParameter("SEQ_NO"+i, ""));
				dataSet.setString("bkg_pe_nm"+i,  parser.getParameter("BKG_PE_NM"+i, ""));
				dataSet.setString("cdhd_grd_seq_no"+i,  parser.getParameter("CDHD_GRD_SEQ_NO"+i, ""));
				dataSet.setString("email"+i,  parser.getParameter("EMAIL"+i, ""));
				dataSet.setString("hp_ddd_no"+i,  parser.getParameter("HP_DDD_NO"+i, ""));
				dataSet.setString("hp_tel_hno"+i,  parser.getParameter("HP_TEL_HNO"+i, ""));
				dataSet.setString("hp_tel_sno"+i,  parser.getParameter("HP_TEL_SNO"+i, ""));
			}

			// Proc ���� ����
			GolfAdmEvntBnstUpdDaoProc proc = (GolfAdmEvntBnstUpdDaoProc)context.getProc("GolfAdmEvntBnstUpdDaoProc");

			// ���Ϻ���
			int editResult = 0; 
			String script = "";
			String upd_type_str = "";
			String resultMsg = "";
			editResult = proc.execute_update(context, request, dataSet);	
			
			if (editResult > 0) {
				script = "parent.location.href='admEvntBnstList.do'";
				resultMsg = "������ ���������� ó�� �Ǿ����ϴ�.";   	
	        } else {
				resultMsg = "������ ���������� ó�� ���� �ʾҽ��ϴ�.\\n\\n�ݺ������� ��ϵ��� ���� ��� �����ڿ� �����Ͻʽÿ�.";		        		
	        }

			request.setAttribute("script", script);
			request.setAttribute("resultMsg", resultMsg);  
			request.setAttribute("returnUrl", "admOrdList.do");
				
			
			// 05. Return �� ����
	        request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.		

			
		} catch(Throwable t) {
			debug(TITLE, t);
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
		
	}
	
}
