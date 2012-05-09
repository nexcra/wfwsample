/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfadmGrUpdFormActn
*   �ۼ���    : (��)�̵������ ������
*   ����      : ������ ��ŷ ������ ���� ��
*   �������  : golf
*   �ۼ�����  : 2009-05-19
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.admin.member;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.ChkChgSocIdException;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.admin.member.GolfAdmMemMgmtInqDaoProc;
import com.bccard.golf.dbtao.proc.admin.member.GolfAdmMemUpdFormDaoProc;
import com.bccard.golf.jolt.JtProcess;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoResult;
import com.bccard.waf.tao.jolt.JoltInput;

/******************************************************************************
* Golf
* @author	(��)�̵������ 
* @version	1.0 
******************************************************************************/
public class GolfAdmMemUpdFormActn extends GolfActn{

	public static final String TITLE = "������ > ���ΰ��� > ȸ������ > ȸ������";
	private static final String BSNINPT = "BSNINPT";					// �����ӿ� ��ȸ����
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		
		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);

		try {

			// ȸ���������̺� ���� �������� ����
			TaoResult cardinfo_pt = null;
			String resultCode_pt = "";

			boolean existsData = false;
			
			// ī�� ���� ���� ����
			String strGlofCardYn = "N";
			String strCardDate = "";
			String cardType = "";
			String joinNo = ""; 
			// 01.��������üũ
			
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);

			// Request �� ����
			String cdhd_ID			= parser.getParameter("CDHD_ID", "");
			String socID			= "";
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("CDHD_ID", cdhd_ID);
					
			GolfAdmMemUpdFormDaoProc proc = (GolfAdmMemUpdFormDaoProc)context.getProc("GolfAdmMemUpdFormDaoProc");
			
			// �ֹι�ȣ ��������
			//socID = proc.getSocid(context, dataSet);
			Hashtable rsHash = null;
			rsHash = proc.getSocid(context, dataSet);
			//DbTaoResult socIDResult = proc.getSocid(context, dataSet);
			
			String memClss = (String)rsHash.get("MEMBER_CLSS");
			socID = (String)rsHash.get("SOCID");
			debug("## socID : "+ socID + " ## memClss : " + memClss);
			dataSet.setString("memClss", memClss);
			dataSet.setString("socID", socID);			
			
			
			try{
				if ("1".equals(memClss)) {
					System.out.println("## GolfCtrlServ | 1. Jolt MHL0230R0100 ���� ȣ�� <<<<<<<<<<<<");
					JoltInput cardInput_pt = new JoltInput(BSNINPT);
					cardInput_pt.setServiceName(BSNINPT);
					cardInput_pt.setString("fml_trcode", "MHL0230R0100");
					cardInput_pt.setString("fml_arg1", "1");	// 1.�ֹι�ȣ 2.����ڹ�ȣ 3.��ü(�������ֹι�ȣ+�����)
					cardInput_pt.setString("fml_arg2", socID);	// �ֹι�ȣ
					//cardInput_pt.setString("fml_arg2", "6002041090498");	// �ӽ��׽�Ʈ��
					cardInput_pt.setString("fml_arg3", " ");	// ����ڹ�ȣ
					cardInput_pt.setString("fml_arg4", "1");	// 1.���� 2.���
					JtProcess jt_pt = new JtProcess();
					java.util.Properties prop_pt = new java.util.Properties();
					prop_pt.setProperty("RETURN_CODE","fml_ret1");
					
		
					
					do {
						cardinfo_pt = jt_pt.call(context, request, cardInput_pt, prop_pt);			
				
						resultCode_pt = cardinfo_pt.getString("fml_ret1");
						debug("=`=`=`=`=`=`=`=`=`=`=`=`=`=`=`=`=`=`resultCode_pt ::  " + resultCode_pt);
						
		
						if ( !"00".equals(resultCode_pt) && !"02".equals(resultCode_pt) ) {		// 00 ����, 02 ������ȸ ����
							//cardType 	= "���� ���۵� ī������ �� �� ����. Error Code : " + resultCode_pt;
							throw new ChkChgSocIdException(cdhd_ID, socID, "02");
	
						}else{
							while( cardinfo_pt.isNext() ) {
								if(!existsData){
									existsData = true;
								}
	
								cardinfo_pt.next();
														
								cardType 	= cardinfo_pt.getString("fml_ret4");	//ī������ 1:����ī�� / 2:PTī�� / 3:�Ϲ�ī��
	
		//						- ��ǰ�� :  ���� ���� �÷�Ƽ������ī�� / �����ڵ�
		//						 �� ���Ǿ����÷�Ƽ������_ĳ����     / 030478
		//						 �� ���Ǿ����÷�Ƽ������_�ƽþƳ�  / 030481
		//						 �� ���Ǿ����÷�Ƽ������_�����װ�  / 030494
								
								//�ӽ÷� ���� 2009.08.25 �ǿ��� ���׽�Ʈ�ô� �ּ������ؾߵ�
								if("1".equals(cardType)){
									if("030478".equals(joinNo) || "030481".equals(joinNo) || "030494".equals(joinNo)  || "030698".equals(joinNo) || "031189".equals(joinNo) || "031176".equals(joinNo) ){
		
		
										strGlofCardYn = "Y";
										strCardDate	= cardinfo_pt.getString("fml_ret12");	//ī��������		
										
		
										debug(" Ret4 | ī������ : "+ cardinfo_pt.getString("fml_ret4"));
										debug(" Ret7 | ī���̸� : "+ cardinfo_pt.getString("fml_ret7"));
										debug(" Ret8 | �����ڵ� : "+ cardinfo_pt.getString("fml_ret8"));
										debug(" Ret12 | ī�������� : "+ cardinfo_pt.getString("fml_ret12"));
										debug(" Ret13 | ���񽺽������� : "+ cardinfo_pt.getString("fml_ret13"));
										debug(" Ret14 | ������������ : "+ cardinfo_pt.getString("fml_ret14"));
		
									}
								}
							}
						}
					} while ("02".equals(resultCode_pt));
				}

			}catch (Throwable t){
				
			}
			
			// 04.���� ���̺�(Proc) ��ȸ
			DbTaoResult updFormResult = proc.execute(context, dataSet);
			
	        	
	        // ȸ�������� ������ ��������
			DbTaoResult gradeListInq = (DbTaoResult)proc.execute_grade(context, request);
			
	        // ��ȸ�� ȯ�޿��� ��������
			DbTaoResult payInq = (DbTaoResult)proc.execute_pay(context, request, dataSet);
			
			// �����丮 ����Ʈ ��ȸ - ����� ȸ�� ���泻���� �����ͼ� �ѷ��ش�.
			DbTaoResult historyResult = proc.execute_history(context, request, dataSet);

			// JSP �������� �������� �ʼ���
			request.setAttribute("UpdFormResult", updFormResult);	
			request.setAttribute("GradeListInq", gradeListInq);		
			request.setAttribute("PayInq", payInq);	
			request.setAttribute("historyResult", historyResult);
	        request.setAttribute("strGlofCardYn", strGlofCardYn);
	        request.setAttribute("strCardDate", strCardDate);
	        request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
