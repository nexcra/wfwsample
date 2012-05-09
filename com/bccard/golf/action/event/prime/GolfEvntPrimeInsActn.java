/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfEvntPrimeInsActn
*   �ۼ���    : (��)�̵������ ������
*   ����      : �̺�Ʈ > �ؿ������� > ��û 
*   �������  : Golf
*   �ۼ�����  : 2010-08-16
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.event.prime;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.proc.event.prime.GolfEvntPrimeInsDaoProc;

/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0
******************************************************************************/
public class GolfEvntPrimeInsActn extends GolfActn{
	
	public static final String TITLE = "�̺�Ʈ > �ؿ������� > ��û";

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

		
		try { 
			HttpSession session = request.getSession(true);

			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			
			// ��ó��
			String script = "";
			int addResult = 0;
				
			// ��û����
			String cdhd_id		= parser.getParameter("cdhd_id","");		// ȸ�����̵�
			String bkg_pe_num	= parser.getParameter("bkg_pe_num", "");	// ����
			String jumin_no1	= parser.getParameter("jumin_no1", "");		// �ֹε�Ϲ�ȣ1
			String jumin_no2	= parser.getParameter("jumin_no2", "");		// �ֹε�Ϲ�ȣ2
			String jumin_no		= jumin_no1+""+jumin_no2;
			String hp_ddd_no	= parser.getParameter("hp_ddd_no","");		// ����ó1
			String hp_tel_hno	= parser.getParameter("hp_tel_hno","");		// ����ó2
			String hp_tel_sno	= parser.getParameter("hp_tel_sno", "");	// ����ó3
			String ddd_no		= parser.getParameter("ddd_no","");			// ����ȭ1
			String tel_hno		= parser.getParameter("tel_hno","");		// ����ȭ2
			String tel_sno		= parser.getParameter("tel_sno", "");		// ����ȭ3
			String dtl_addr		= parser.getParameter("dtl_addr","");		// �ּ�
			String lesn_seq_no	= parser.getParameter("lesn_seq_no","");	// ���Ը����
			String pu_date		= parser.getParameter("pu_date","");		// ȸ��������
			pu_date = GolfUtil.replace(pu_date, ".", "");
			String memo_expl	= parser.getParameter("memo_expl","");		// ��Ÿ ��û ����
			
			// ��������
			String order_no		= parser.getParameter("order_no", "");			// �ֹ��ڵ�
			String realPayAmt	= parser.getParameter("realPayAmt", "0");		// ���� �ݾ�
			

			request.getSession().removeAttribute("primeName");
			request.getSession().removeAttribute("primeJumin1");
			request.getSession().removeAttribute("primeJumin2");
			session.setAttribute("primeName",bkg_pe_num);
			session.setAttribute("primeJumin1",jumin_no1);
			session.setAttribute("primeJumin2",jumin_no2);
			
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);

			dataSet.setString("cdhd_id", cdhd_id);
			dataSet.setString("bkg_pe_num", bkg_pe_num);
			dataSet.setString("jumin_no1", jumin_no1);
			dataSet.setString("jumin_no2", jumin_no2);
			dataSet.setString("jumin_no", jumin_no);
			dataSet.setString("hp_ddd_no", hp_ddd_no);
			dataSet.setString("hp_tel_hno", hp_tel_hno);			
			dataSet.setString("hp_tel_sno", hp_tel_sno);
			dataSet.setString("ddd_no", ddd_no);
			dataSet.setString("tel_hno", tel_hno);			
			dataSet.setString("tel_sno", tel_sno);
			dataSet.setString("dtl_addr", dtl_addr);
			dataSet.setString("lesn_seq_no", lesn_seq_no);
			dataSet.setString("pu_date", pu_date);
			dataSet.setString("memo_expl", memo_expl);
			
			dataSet.setString("order_no", order_no);
			dataSet.setString("realPayAmt", realPayAmt);		

			// 04.���� ���̺�(Proc) ��ȸ
			GolfEvntPrimeInsDaoProc proc = (GolfEvntPrimeInsDaoProc)context.getProc("GolfEvntPrimeInsDaoProc");
			
			// ��û���� �˻�
			int cntEvnt = proc.execute_insYn(context, request, dataSet);
			if(cntEvnt>0){
				script = "alert('�̹� ��û�ϼ̽��ϴ�.');";
			}else{

				// �ֹ����� ����
				addResult = proc.execute(context, request, dataSet);	
				debug("GolfEvntPrimeInsDaoProc = addResult : " + addResult);	
				
				if(addResult>0){
					script = "parent.parent.location.href='/app/golfloung/html/event/prime/prime05.jsp';";
				}else{
					script = "alert('��û������ ������ �־����ϴ�. �ٽ� �õ��� �ֽʽÿ�.'); parent.location.href='GolfEvntPrimeInsForm.do';";
				}
			}
				

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
