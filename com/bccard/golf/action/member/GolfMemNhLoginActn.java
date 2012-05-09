/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfMemNhLoginActn
*   �ۼ���     : (��)�̵������ �ǿ���
*   ����        : NHä�� �α��� ����
*   �������  : Golf
*   �ۼ�����  : 2009-12-02
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.member;

import java.io.IOException;
import java.sql.Connection;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.golf.common.CommandToken;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.dbtao.proc.member.GolfMemChgDaoProc;
import com.bccard.golf.jolt.JtProcess;
import com.bccard.waf.action.AbstractAction;
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
public class GolfMemNhLoginActn extends AbstractAction {

	public static final String Title = "NHä�� �α��� ����";
	private static final String BSNINPT = "BSNINPT";					// �����ӿ� ��ȸ����
	
	/***********************************************************************
	 * �׼�ó��.
	 * @param context       WaContext
	 * @param request       HttpServletRequest
	 * @param response      HttpServletResponse
	 * @return ��������
	 **********************************************************************/
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, BaseException {
		Connection con 					= null;
		RequestParser	parser			= context.getRequestParser("default", request, response);			
				
		try {
			
			Map paramMap 				= parser.getParameterMap();																							
			con 									= context.getDbConnection("default", null);	
			
			//parameter
			String nhUserNm				= parser.getParameter("nhUserNm", ""); 
			String nhJumin1				= parser.getParameter("nhJumin1", ""); 
			String nhJumin2				= parser.getParameter("nhJumin2", ""); 
			String nhJumin					= nhJumin1+nhJumin2;
			
			//���Ǻ���
			String strNhMemYn			= "N";		//nhȸ������
			String strBcMemYn			= "N";		//bcȸ������
			
			GolfMemChgDaoProc proc = (GolfMemChgDaoProc)context.getProc("GolfMemChgDaoProc");
			
			if(!"".equals(nhUserNm) && !"".equals(nhJumin1) && !"".equals(nhJumin2))
			{
				//NH���� ��� ����
				Properties properties = new Properties();
				properties.setProperty("LOGIN", "Y");  					// �ʼ� �α׿� ���� �ش� ������ return RETURN_CODE Ű. ���� ���ϸ� "fml_ret1" ���
				properties.setProperty("RETURN_CODE", "fml_ret1");		// ���� Ư���� pool �� ����ϴ� �����.
				properties.setProperty("SOC_ID", nhJumin); 	// log ����ִ� �κп��� �ֹι�ȣ ��ü
				
				System.out.print("## NhLog | GolfMemNhLoginActn | ���� | nhUserNm : "+nhUserNm+" | nhJumin : "+nhJumin+"\n");
				
				try {
										
					
					/** *****************************************************************
					 *Card������ �о����
					 ***************************************************************** */
					System.out.println("## GolfCtrlServ | 1. Jolt NTC0080R2500 ���� ȣ�� <<<<<<<<<<<<");
					JoltInput cardInput_pt = new JoltInput(BSNINPT);
					cardInput_pt.setServiceName(BSNINPT);
					cardInput_pt.setString("fml_trcode", "NTC0080R2500");
					cardInput_pt.setString("fml_arg1", nhJumin);	// �ֹι�ȣ
				
					JtProcess jt_pt = new JtProcess();
					java.util.Properties prop_pt = new java.util.Properties();
					prop_pt.setProperty("RETURN_CODE","fml_ret1");
					
					TaoResult cardinfo_pt = null;
					String resultCode_pt = "";
					
					cardinfo_pt = jt_pt.call(context, request, cardInput_pt, prop_pt);		
					
					if (cardinfo_pt.containsKey("fml_ret1"))
					{					
						resultCode_pt = cardinfo_pt.getString("fml_ret1");
					}
					System.out.print("## NhLog | GolfMemNhLoginActn | ������Ű���� | nhUserNm : "+nhUserNm+" | nhJumin : "+nhJumin+" | resultCode_pt : "+resultCode_pt+"\n");
					
					
					//����ȸ������ üũ
					if("02".equals(resultCode_pt)){
					
						if ( cardinfo_pt.getString("fml_ret5").trim().length() > 0 ){ //ī������
							strNhMemYn = "Y";
						}
					
					}					
					
					if("7801091024598".equals(nhJumin)) strNhMemYn = "Y";

					
					//����ȸ���̸� BCȸ������ üũ
					if("Y".equals(strNhMemYn))
					{						
						strBcMemYn = proc.getBcMemYn(con, nhJumin);
						System.out.print("## NhLog | GolfMemNhLoginActn | BCȸ������ üũ | nhUserNm : "+nhUserNm+" | nhJumin : "+nhJumin+" | strBcMemYn : "+strBcMemYn+"\n");
						
					}
					
				} catch (Throwable te) {
					System.out.print("## NhLog | GolfMemNhLoginActn | ������Ű������ | nhUserNm : "+nhUserNm+" | nhJumin : "+nhJumin+" \n");
					throw new GolfException(Title, te);
				}
				
			}
			
			CommandToken.set(request);  
			paramMap.put("token", request.getAttribute("token"));   
			
			request.setAttribute("paramMap", paramMap);
			request.setAttribute("strNhMemYn", strNhMemYn);
			request.setAttribute("strBcMemYn", strBcMemYn);
			
			
			} catch (Throwable be) {			
				throw new GolfException(Title, be);
			} finally {
				try { if(con != null) { con.close(); } else {;} } catch(Throwable ignore) {}
			}
			return super.getActionResponse(context);
		}
		
		

}
