/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfBkPreTimeResultActn
*   �ۼ���    : (��)�̵������ ������
*   ����      : ��ŷ ��û ���� Ȯ��
*   �������  : golf
*   �ۼ�����  : 2009-05-28
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.booking.premium;

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
import com.bccard.golf.dbtao.proc.booking.GolfBkBenefitTimesDaoProc;
import com.bccard.golf.dbtao.proc.booking.premium.*;

/******************************************************************************
* Topn
* @author	(��)�̵������ 
* @version	1.0
******************************************************************************/
public class GolfBkPreTimeResultActn extends GolfActn{
	
	public static final String TITLE = "��ŷ ��û ���� Ȯ��"; 

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
			// 01.��������üũ, ���� ����
			int pmi_bokg = 0;
			int yoil_num = 0;
			String bkps_date_real = "";
			
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);

			// Request �� ����
			String seq_NO			= parser.getParameter("idx", "");
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.) 
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("SEQ_NO", seq_NO);
			
			// 04.���� ���̺�(Proc) ��ȸ
			GolfBkPreTimeResultDaoProc proc = (GolfBkPreTimeResultDaoProc)context.getProc("GolfBkPreTimeResultDaoProc");
			DbTaoResult timeView = proc.execute(context, dataSet);
			if(timeView != null){
				timeView.next();
				yoil_num = timeView.getInt("BKPS_YOIL_NUM");
				bkps_date_real = timeView.getString("BK_DATE_REAL");
				dataSet.setString("bkps_date_real", bkps_date_real);
				dataSet.setInt("yoil_num", yoil_num);
				debug("bkps_date_real : " + bkps_date_real);

				// 05. ��ŷ Ƚ�� ��ȸ - ���̹� �Ӵ� ��볻���� ȸ������ �����ȴ�.
				GolfBkBenefitTimesDaoProc proc_times = (GolfBkBenefitTimesDaoProc)context.getProc("GolfBkBenefitTimesDaoProc");
				if(yoil_num==1 || yoil_num==7){
					// �ָ�
					DbTaoResult pmiWkeView = proc_times.getPmiWkeBenefitWeek(context, dataSet, request);
					pmiWkeView.next();
					pmi_bokg = pmiWkeView.getInt("PMI_WKE_BOKG");
				}else{
					// ����
					DbTaoResult pmiWkdView = proc_times.getPmiWkdBenefitWeek(context, dataSet, request);
					pmiWkdView.next();
					pmi_bokg = pmiWkdView.getInt("PMI_WKD_BOKG");
				}
			}
			paramMap.put("pmi_bokg", pmi_bokg+"");

			// 05. Return �� ����
			request.setAttribute("SEQ_NO", seq_NO);	
			request.setAttribute("TimeView", timeView);	
	        request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.			
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
