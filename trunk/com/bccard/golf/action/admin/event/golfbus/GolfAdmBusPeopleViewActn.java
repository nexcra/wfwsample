/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmBusPeopleViewActn
*   �ۼ���    : (��)�̵������ �ǿ���
*   ����      : ������ > �̺�Ʈ->��������������̺�Ʈ->��û���� �󼼺���
*   �������  : Golf
*   �ۼ�����  : 2009-09-25
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.admin.event.golfbus;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bccard.golf.common.CommandToken;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.waf.action.AbstractAction;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoConnection;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoResult;

/******************************************************************************
* Golf
* @author	(��)�̵������ 
* @version	1.0
******************************************************************************/
public class GolfAdmBusPeopleViewActn extends AbstractAction {

	public static final String TITLE = "������ ����� ���� ���� ��û���� �󼼺���";
	
	/**
	 * @param WaContext context
	 * @param HttpServletRequest request
	 * @param HttpServletResponse response
	 * @return ActionResponse
	 */
	public ActionResponse execute(WaContext context, HttpServletRequest request,
		HttpServletResponse response) throws IOException, ServletException,
			BaseException
	{
		TaoConnection 		con 				= null;
		TaoResult 			result  			= null;	
		TaoResult 			resultUse  			= null;			
		Map 				paramMap 			= null;
		
		try {
			// form parameter parsing
			RequestParser parser 				= context.getRequestParser("default", request, response);						
			paramMap 							= (Map)request.getAttribute("paramMap");
			if(paramMap == null) 	   paramMap = parser.getParameterMap();
			String actnKey 						= super.getActionKey(context);		
			long page_no						= parser.getLongParameter("page_no", 1L);				// ��������ȣ
			String p_idx						= parser.getParameter("p_idx");							// ��¥
			
			con = context.getTaoConnection("dbtao",null);
			
			// ������ �α��� ����
			HttpSession session 				= request.getSession(false);
			GolfAdminEtt userEtt 				= (GolfAdminEtt) session.getAttribute("SESSION_ADMIN");
						
			// Proc �Ķ���� ����
			TaoDataSet input 					= new DbTaoDataSet(TITLE);
			input.setObject("userEtt", 			userEtt);
			input.setString("actnKey", 			actnKey);
			input.setString("Title", 			TITLE);					
			input.setLong("page_no",			page_no);
			
			
			if( !"".equals(p_idx) ) 
			{				
				input.setString("p_idx", 			p_idx.replaceAll("\\.", ""));
				
				// �Խ��� �󼼺��� ��ȸ		
				result = con.execute("admin.event.golfbus.GolfAdmBusPeopleDetailInqDaoProc",input);				
				request.setAttribute("p_idx", p_idx);
			}
			String userId = "";
			if(result.isNext()){
				result.next();
				userId = result.getString("CDHD_ID");            //���̵�
				String intMemGradeNM = result.getString("GRADE");       //���

				if(intMemGradeNM.equals("è�ǿ�")){
					String intMemGrade = "1";
					input.setString("intMemGrade",intMemGrade);
				}else if(intMemGradeNM.equals("���")){
					String intMemGrade = "2";
					input.setString("intMemGrade",intMemGrade);
				}else if(intMemGradeNM.equals("���")){
					String intMemGrade = "3";
					input.setString("intMemGrade",intMemGrade);
				}else if(intMemGradeNM.equals("ȭ��Ʈ")){
					String intMemGrade = "4";
					input.setString("intMemGrade",intMemGrade);
				}
				input.setString("userId",userId);				
				input.setString("cdhd_id",userId);	
			}
			
			//�̿볻��
			resultUse = con.execute("admin.event.golfbus.GolfAdmBusUsedCkInqDaoProc",input);
			
			
			String cnt = "";
			String tot_cnt = "";
			
			if(resultUse.isNext()){
				resultUse.next();
				cnt = resultUse.getString("CNT");
				tot_cnt = resultUse.getString("TOT");
				
				debug("cnt : " + cnt);
				debug("tot_cnt : " + tot_cnt);
				
				paramMap.put("cnt",cnt);
				paramMap.put("tot_cnt",tot_cnt);
			}

			String can_cnt      = String.valueOf(Integer.parseInt(tot_cnt) - Integer.parseInt(cnt));
			
												
			CommandToken.set(request);  
			paramMap.put("token", request.getAttribute("token")); 
			paramMap.put("page_no"			, page_no+""		);
			paramMap.put("p_idx"			, p_idx);	
			paramMap.put("green_nm"			, parser.getParameter("green_nm"));	
			paramMap.put("golf_cmmn_code"			, parser.getParameter("golf_cmmn_code"));	
			paramMap.put("grade"					, parser.getParameter("grade"));	
			paramMap.put("sch_reg_aton_st"			, parser.getParameter("sch_reg_aton_st"));	
			paramMap.put("sch_reg_aton_ed"			, parser.getParameter("sch_reg_aton_ed"));	
			paramMap.put("sch_pu_date_st"			, parser.getParameter("sch_pu_date_st"));	
			paramMap.put("sch_pu_date_ed"			, parser.getParameter("sch_pu_date_ed"));	
			paramMap.put("sch_type"					, parser.getParameter("sch_type"));	
			paramMap.put("search_word"				, parser.getParameter("search_word"));						
			paramMap.put("can_cnt",can_cnt);
			paramMap.put("cnt",cnt);
			
						
			request.setAttribute("paramMap", paramMap);
			request.setAttribute("result", result);
			request.setAttribute("userId", userId);

			
		} catch (BaseException be) {
			throw be;
		} catch (Throwable t) {
			debug(TITLE, t);
			throw new GolfException(TITLE, t);
		} finally {
			try { if( con != null ){ con.close(); } else {} } catch(Throwable ignore) {}
		}

		return getActionResponse(context, "default");
	}
				

}
