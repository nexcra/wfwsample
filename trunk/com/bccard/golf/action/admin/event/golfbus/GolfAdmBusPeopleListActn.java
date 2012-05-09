/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmBusPeopleListActn
*   �ۼ���    : (��)�̵������ �ǿ���
*   ����      : ������ > �̺�Ʈ->��������������̺�Ʈ->�������� ����Ʈ
*   �������  : Golf
*   �ۼ�����  : 2009-09-28
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
public class GolfAdmBusPeopleListActn extends AbstractAction {

	public static final String TITLE = "������ ����� ���� ���� ��û���� ����Ʈ";
	
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
		Map 				paramMap 			= null;
		
		try {
			// form parameter parsing
			RequestParser parser 				= context.getRequestParser("default", request, response);						
			paramMap 							= (Map)request.getAttribute("paramMap");
			if(paramMap == null) paramMap = parser.getParameterMap();
			String actnKey 						= super.getActionKey(context);		
			long page_no						= parser.getLongParameter("page_no", 1L);				// ��������ȣ
			long page_size						= parser.getLongParameter("page_size", 20L);			// ����������¼�	
			
			String search_yn					= parser.getParameter("search_yn", "N");				// �˻�����
			
			String green_nm						= "";													// ������
			String golf_cmmn_code				= "";													// �����ڵ�			
			String grade						= "";             	 		//����ڵ�� �ѱ۸�
			String sch_type						= "";          			//�̸�,ID��ȸ ����
			String search_word					= "";       			//��ȸ ��
			
			String st_year 			= parser.getParameter("ST_YEAR","");
			String st_month 		= parser.getParameter("ST_MONTH","");
			String st_day 			= parser.getParameter("ST_DAY","");
			String ed_year 			= parser.getParameter("ED_YEAR","");
			String ed_month 		= parser.getParameter("ED_MONTH","");
			String ed_day 			= parser.getParameter("ED_DAY","");
			String st_year2 		= parser.getParameter("ST_YEAR2","");
			String st_month2 		= parser.getParameter("ST_MONTH2","");
			String st_day2 			= parser.getParameter("ST_DAY2","");
			String ed_year2 		= parser.getParameter("ED_YEAR2","");
			String ed_month2 		= parser.getParameter("ED_MONTH2","");
			String ed_day2 			= parser.getParameter("ED_DAY2","");
			String sch_date_st					= "";
			String sch_date_ed					= "";
			String sch_date_st2					= "";
			String sch_date_ed2					= "";
			
			if("Y".equals(search_yn))
			{				
				green_nm						= parser.getParameter("green_nm");						
				golf_cmmn_code					= parser.getParameter("golf_cmmn_code");				
				grade							= parser.getParameter("grade");			
				sch_type						= parser.getParameter("sch_type");
				search_word						= parser.getParameter("search_word");				

				
				sch_date_st		= st_year+st_month+st_day;
				sch_date_ed		= ed_year+ed_month+ed_day;

				
				sch_date_st2		= st_year2+st_month2+st_day2;
				sch_date_ed2		= ed_year2+ed_month2+ed_day2;
							
			}
			
			paramMap.put("ST_YEAR",st_year);
			paramMap.put("ST_MONTH",st_month);
			paramMap.put("ST_DAY",st_day);
			paramMap.put("ED_YEAR",ed_year);
			paramMap.put("ED_MONTH",ed_month);
			paramMap.put("ED_DAY",ed_day);
			paramMap.put("ST_YEAR2",st_year2);
			paramMap.put("ST_MONTH2",st_month2);
			paramMap.put("ST_DAY2",st_day2);
			paramMap.put("ED_YEAR2",ed_year2);
			paramMap.put("ED_MONTH2",ed_month2);
			paramMap.put("ED_DAY2",ed_day2);
			
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
			input.setLong("page_size",			page_size);	
			input.setString("search_yn",		search_yn);
			
			
			
			if("Y".equals(search_yn))
			{
				input.setString("green_nm",		green_nm);
				input.setString("golf_cmmn_code",		golf_cmmn_code);		
				input.setString("grade",	grade);
				input.setString("sch_reg_aton_st",	sch_date_st);	
				input.setString("sch_reg_aton_ed",	sch_date_ed);	
				input.setString("sch_pu_date_st",	sch_date_st2);	
				input.setString("sch_pu_date_ed",	sch_date_ed2);	
				input.setString("sch_type",	sch_type);	
				input.setString("search_word",	search_word);	
			}
			
			// ��û ����Ʈ ��ȸ			
			result = con.execute("admin.event.golfbus.GolfAdmBusPeopleInqDaoProc",input);
			
			String tot_cnt = "";
			if(result.isNext()){
				result.next();
				try { 
					tot_cnt = result.getString("TOT_CNT");					
				} catch(Throwable ignore) {}
				
				
				debug("total_cnt : " + tot_cnt);
				paramMap.put("total_cnt",tot_cnt);
			}

			CommandToken.set(request);  
			paramMap.put("token", request.getAttribute("token"));  
			paramMap.put("green_nm", parser.getParameter("green_nm",""));
			paramMap.put("golf_cmmn_code", parser.getParameter("golf_cmmn_code",""));
			paramMap.put("grade", parser.getParameter("grade",""));
			paramMap.put("sch_type", parser.getParameter("sch_type",""));
			paramMap.put("search_word", parser.getParameter("search_word",""));
			
			request.setAttribute("paramMap", paramMap);
			request.setAttribute("result", result);
			
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
