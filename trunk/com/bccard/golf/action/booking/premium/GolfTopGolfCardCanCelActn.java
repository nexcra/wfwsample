/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfTopGolfCardRegActn
*   �ۼ���    : ������
*   ����      :  ����ī�� ���� ��ŷ  > ��ŷ ���ó��
*   �������  : Golf
*   �ۼ�����  : 2010-10-26
***************************************************************************************************/
package com.bccard.golf.action.booking.premium;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
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
import com.bccard.golf.common.SmsSendProc;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.booking.premium.GolfTopGolfCardListDaoProc;
import com.bccard.golf.dbtao.proc.event.benest.GolfEvntBnstRegDaoProc;

/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0
******************************************************************************/
public class GolfTopGolfCardCanCelActn extends GolfActn{
	
	public static final String TITLE = " ����ī�� ���� ��ŷ  > ��ŷ ���ó��";

	/***************************************************************************************
	* ���� �����ȭ��
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/
	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";		
		
		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout"); 
		request.setAttribute("layout", layout);
		
		
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		
		try { 
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			
			String script = "";

			String aplc_seq_no					= parser.getParameter("aplc_seq_no", "");
			String pgrs_yn					= parser.getParameter("pgrs_yn", "");
			String green_nm					= parser.getParameter("green_nm", "");
			String teof_date 				= parser.getParameter("teof_date", "");
			String teof_time 				= parser.getParameter("teof_time", "");
			String userNm 					= parser.getParameter("user_nm", "");
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("APLC_SEQ_NO", aplc_seq_no);
			dataSet.setString("PGRS_YN", pgrs_yn);
			
			dataSet.setString("green_nm", green_nm);
			dataSet.setString("teof_date", teof_date);
			dataSet.setString("teof_time", teof_time);
			dataSet.setString("userNm", userNm);

			// 04.���� ���̺�(Proc) ��ȸ
			GolfTopGolfCardListDaoProc proc = (GolfTopGolfCardListDaoProc)context.getProc("GolfTopGolfCardListDaoProc");
			
			/*int cntJumin = (int) proc.execute_jumin(context, request, dataSet);
			int cntHp = (int) proc.execute_hp(context, request, dataSet);
			
			if(cntJumin>0){ 
				script = "alert('������ �ֹε�Ϲ�ȣ�� ��û������ �ֽ��ϴ�.'); history.back();";
			}else if(cntHp>0){
				script = "alert('������ �ڵ��� ��ȣ�� ��û������ �ֽ��ϴ�.'); history.back();";
			}else{*/
				int appInt = (int) proc.app_upd_pro(context, request, dataSet);

				if(appInt>0){
					script = "alert('����/��ŷ ��Ұ� ó���Ǿ����ϴ�.'); location.href='GolfTopGolfCardStatus.do';";
					//����ڿ��� sms�뺸	�ý����� ����ȭ �Ǳ� �������� ��������� ���� �߼�
					if("C".equals(pgrs_yn)){
						proc.cancelSmsExe(context, request, dataSet);
					}
			        
				}else{
					script = "alert('����/��ŷ ��Ұ� ó������ �ʾҽ��ϴ�. �ٽ� �õ��� �ֽñ� �ٶ��ϴ�.'); location.href='GolfTopGolfCardStatus.do';";
				}
			//}
			
			
			request.setAttribute("script", script);
	        request.setAttribute("paramMap", paramMap);
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 

		return super.getActionResponse(context, subpage_key);
		
	}
	

}
