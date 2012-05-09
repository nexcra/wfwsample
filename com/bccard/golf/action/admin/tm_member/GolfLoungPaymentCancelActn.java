/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfLoungPaymentCancelActn.java
*   �ۼ���    : ���񽺰����� ������
*   ����      : TMȸ�� ���(����Ʈ��� + �������)
*   �������  : Golf
*   �ۼ�����  : 2009-07-17
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.admin.tm_member;

import java.io.IOException;
import java.util.Map;
import java.net.InetAddress;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.initech.dbprotector.CipherClient;
import com.bccard.waf.action.AbstractAction;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.AbstractEntity;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;


import com.bccard.golf.jolt.JtTransactionProc;
import com.bccard.golf.jolt.JtProcess;

import com.bccard.waf.tao.jolt.JoltOutput;
import com.bccard.waf.tao.TaoResult;
import com.bccard.waf.tao.jolt.JoltInput;


import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.golf.common.GolfPayAuthEtt;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentDaoProc;

/******************************************************************************
* Golf
* @author	
* @version	1.0
******************************************************************************/
public class GolfLoungPaymentCancelActn extends GolfActn {
	
	public static final String TITLE = "TM ���� ���"; 
	/***************************************************************************************
	* �񾾰��� TM ���� ��� ���μ���
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/
	 
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {
		
		DbTaoResult taoResult = null;
		DbTaoResult retResult = null;
		String subpage_key = "default";
		
		try {
			RequestParser	parser	= context.getRequestParser("default", request, response);			
			Map paramMap = parser.getParameterMap();	
		

			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			retResult = new DbTaoResult(TITLE);
	
 
			String jumin_no			= parser.getParameter("JUMIN_NO", "");
			String account			= parser.getParameter("CDHD_ID", "");
			String join_chnl			= parser.getParameter("JOIN_CHNL", "");
			String work_date			= parser.getParameter("RECP_DATE", "");
			String sResultCode		= "";
			String ret_code   =  "" ;
			String ret_msg    =  "" ;		
			
			String st_year         	= parser.getParameter("ST_YEAR","");
			String st_month         = parser.getParameter("ST_MONTH","");
			String st_day			= parser.getParameter("ST_DAY","");
			String ed_year         	= parser.getParameter("ED_YEAR","");
			String ed_month         = parser.getParameter("ED_MONTH","");
			String ed_day			= parser.getParameter("ED_DAY","");
			String tag				= parser.getParameter("TAG","1");
			String st_gb			= parser.getParameter("ST_GB","1");
			
			String fromUrl			= parser.getParameter("fromUrl","");		// ȣ�� URL 
			debug("fromUrl : " + fromUrl);
			
			
			
			
			dataSet.setString("jumin_no", jumin_no); //
			dataSet.setString("account", account); //
			dataSet.setString("join_chnl", join_chnl);
			dataSet.setString("work_date", work_date);
			
			
			GolfLoungPaymentCancelProc proc = new GolfLoungPaymentCancelProc();

			/********************************************************************
				1. ����Ʈ ��� ��󿩺� ��ȸ (TBLUGTMCSTMR ���� Ȯ��) - �ֹι�ȣ, TMó������
				2. ����ȸ���������̺� ��ȸ (TBGGOLFCDHD ���� Ȯ��) - �ֹι�ȣ, ���԰��
				3. ����ȸ�� ��� Ȯ�� (TBGGOLFCDHDGRDMGMT ��� Ȯ��) - ���׷��̵� ȸ�� ���� Ȯ��
				4. ���� ��볻�� ��ȸ Ȯ�� -
			*********************************************************************/
			taoResult = (DbTaoResult)proc.execute(context, dataSet);	// 
			taoResult.next();
			String rRESULT = taoResult.getString("RESULT");

			
			
			//�ڷᰡ ��� ����ڶ��
			if ("00".equals(rRESULT)) {
				request.setAttribute("RESULT", rRESULT);

				String auth_clss	= taoResult.getString("AUTH_CLSS");
				String card_no	= taoResult.getString("CARD_NO");
				String vald_lim	= taoResult.getString("VALD_LIM");
				String auth_no	= taoResult.getString("AUTH_NO");
				String pay_amt = taoResult.getString("AUTH_AMT");
				String tm_buz = taoResult.getString("TM_BUZ");
				
				String hostAddress = InetAddress.getLocalHost().getHostAddress();
				
				//ȸ��Żȸ ó���� �����ϰ�
				if ("2".equals(tag)) {
					sResultCode ="����";
				//�������,����Ʈ���,ȸ��Żȸ ó�� ����	
				} else {
				
					if ("3".equals(auth_clss))
					{
	
						/********************************************************************
							5. ����Ʈ ��� ����
						*********************************************************************/
	
						JoltInput entity = null;
	
						entity = new JoltInput();
	
						entity.setServiceName("BSXINPT");		
						
						entity.setString("fml_channel", "WEB"	 );
						entity.setString("fml_sec51" , hostAddress   );
						entity.setString("fml_sec50" , "bcadmin"                    );
						entity.setString("fml_trcode", "MJF6250I0100"               );
						entity.setString("fml_arg1"  , jumin_no					   );			
						entity.setString("fml_arg2"  , auth_no					   );
						entity.setString("fml_arg3"  , "42"			      		   );			
						
						debug("  entity= ["+entity+"]");
	
						JtTransactionProc pgProc = null;
	
						pgProc = (JtTransactionProc)context.getProc("MJF6250I0100");
	
						JoltOutput output = pgProc.execute(context,entity);
	
						debug(output.toString());
	
						ret_code   =  output.getString("fml_ret1");
						ret_msg    =  output.getString("fml_ret2");
	
						if (ret_code.equals("0000"))
						{
							sResultCode ="����";
						}
	
					} else if (  "1".equals(auth_clss) ||  "2".equals(auth_clss) ) {
						/********************************************************************
							5. ���� ��� ����
						*********************************************************************/
	 
	
						/********************************************
						* MJF6010R0100 - 01(fml_ret5):���� 
						* ���� ���� :  for COMMIT  *
						*********************************************/
						GolfPayAuthEtt payEtt = new GolfPayAuthEtt();
	
						String ins_term ="00"; 
						String merno = "765943401"; //��������ȣ (��ȸ��)
						
						if (tm_buz.equals("03") || tm_buz.equals("13"))	{
							merno = "767445661"; //H&C��Ʈ��ũ || ���غ���
							
						} else 	{
							merno = "767445687"; //Ʈ�����ڸ���
						}
	
						if ( "1".equals(auth_clss) ) ins_term ="00";
						else if	( "2".equals(auth_clss) ) ins_term ="00"; //���հ����϶� ��ҽÿ� 00 �� �Ѱܾ�
	
						payEtt.setMerMgmtNo(merno); //��������ȣ
						payEtt.setCardNo(card_no); 
						payEtt.setValid(vald_lim.substring(2,6));			
						payEtt.setAmount(pay_amt);
						payEtt.setInsTerm(ins_term); //�Һΰ�����
						payEtt.setRemoteAddr(hostAddress);
						payEtt.setUseNo(auth_no);
	
						GolfPaymentDaoProc payProc = (GolfPaymentDaoProc)context.getProc("GolfPaymentDaoProc");
	
						boolean payCancelResult = false;
	
						payCancelResult = payProc.executePayAuthCancel(context, payEtt);
						debug("payCancelResult==========>"+payCancelResult);
	
						ret_code = payEtt.getResCode(); 
						ret_msg = payEtt.getResMsg();
						debug("ret_code==========>"+ret_code);
						debug("ret_msg==========>"+ret_msg);
	
						if (payCancelResult)
						{
							
							sResultCode ="����";
							
						} else {
	
						}
	
					}
				}	
				debug("sResultCode==========>"+sResultCode+",sResultCode========="+sResultCode);
				if(sResultCode.equals("����")) {
					
					/********************************************************************
						7. TM�������� update
					*********************************************************************/
					int updatecnt = proc.updateTM(context, jumin_no, work_date);
					/********************************************************************
						8. ����ȸ�� ���̺� ����
					*********************************************************************/
					int deletecnt = proc.deleteGOLFCDHD(context, jumin_no);

					/********************************************************************
						9. ����ȸ�� ��� ���̺� ����
					*********************************************************************/
					if (!"".equals(account))
					{
						int delete2cnt = proc.deleteGRD(context, account);
					}

					/********************************************************************
						10. ��ȸ�� ���̺��� ��ҳ����� update �� TBGLUGANLFEECTNT
					*********************************************************************/
					int update2cnt = proc.updateFee(context, jumin_no,auth_no,card_no); 

					retResult.addString("RESULT", "00");


				}else{

					//ret_code ="99";
					//ret_msg ="�����Դϴ�.";

					retResult.addString("RESULT", "01");
									
					if ( "3".equals(auth_clss))	{
						retResult.addString("RESULT_MSG", "����Ʈ ��ҽ� ���� |fml_ret1|"+ret_code+"|fml_ret2|"+ret_msg);
					} else if (  "1".equals(auth_clss) ||  "2".equals(auth_clss) ) {
						retResult.addString("RESULT_MSG", "ī����� ��ҽ� ���� |fml_ret1|"+ret_code+"|fml_ret2|"+ret_msg);
					}
					
				}

			} else {
				//����ó�� - Proc���� �޼��� ó�� ����
				
				retResult.addString("RESULT",rRESULT);
				String strMsg = taoResult.getString("RESULT_MSG");
				retResult.addString("RESULT_MSG", strMsg);
			}
  
			paramMap.put("ST_YEAR",st_year);
			paramMap.put("ST_MONTH",st_month);
			paramMap.put("ST_DAY",st_day);
			paramMap.put("ED_YEAR",ed_year);
			paramMap.put("ED_MONTH",ed_month);
			paramMap.put("ED_DAY",ed_day);
			paramMap.put("ST_GB",st_gb);
			paramMap.put("TAG",tag);
			
			

			request.setAttribute("fromUrl",fromUrl);
			request.setAttribute("paramMap",paramMap);
			request.setAttribute("resultList",retResult);
			
			
		}catch(Throwable t) {
			return errorHandler(context,request,response,t);
		}
		
		return getActionResponse(context, subpage_key);
		
	}
}
 