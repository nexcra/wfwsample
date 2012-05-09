/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmEgolfExpoListActn
*   �ۼ���    : shin cheong gwi
*   ����      : �̵��ϸ� ���� ������ ��û�� ���� 
*   �������  : Golf
*   �ۼ�����  : 2012-04-03
************************** �����̷� ****************************************************************
*    ����    �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.admin.mania;

import java.io.IOException;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.admin.mania.GolfAdmEgolfExpoInqDaoProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

public class GolfAdmEgolfExpoListActn extends GolfActn{

	public static final String TITLE = "������ �̵��ϸ� ���� ������ ��û����";
	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException
	{
		String subpage_key = "default";	
		
		// ���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);
		
		try
		{
			// �Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			
			// Request ��
			long page_no = parser.getLongParameter("page_no", 1L);		// ��������ȣ
			long record_size = parser.getLongParameter("record_size", 1);	// ����������¼�			
			String aplc_clss = parser.getParameter("aplc_clss", "0013");
			String usrNm = parser.getParameter("co_nm", "");
			String pgrs_yn = parser.getParameter("pgrs_yn", "");
			subpage_key = parser.getParameter("subpage_key", "");
			subpage_key = subpage_key.equals("") ? "default" : subpage_key;
			String aplc_seq_no = parser.getParameter("aplc_seq_no", "");			
			
			long totalRecord = 0L;
			long currPage = 0L;
			long totalPage = 0L;
			
			
			// Proc �� ���� �� ���� 
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
				dataSet.setLong("PAGE_NO", page_no);
				dataSet.setLong("RECORD_SIZE", record_size);
				dataSet.setString("APLC_CLSS", aplc_clss);
				dataSet.setString("USRNM", usrNm);
				dataSet.setString("PGRS_YN", pgrs_yn);
				dataSet.setString("APLC_SEQ_NO", aplc_seq_no);
			
			GolfAdmEgolfExpoInqDaoProc proc = (GolfAdmEgolfExpoInqDaoProc)context.getProc("GolfAdmEgolfExpoInqDaoProc");
			
			if(subpage_key.equals("default"))
			{				
				DbTaoResult expoListResult = (DbTaoResult)proc.execute(context, request, dataSet);
									
				
				if (expoListResult != null && expoListResult.isNext()) 
				{
					expoListResult.first();
					expoListResult.next();
					if (expoListResult.getObject("RESULT").equals("00")) {
						totalRecord = Long.parseLong((String)expoListResult.getString("TOTAL_CNT"));
						currPage = Long.parseLong((String)expoListResult.getString("CURR_PAGE"));
						totalPage = (totalRecord % record_size == 0) ? (totalRecord / record_size) : (totalRecord / record_size)+1;
					}
				}
				
				paramMap.put("totalRecord", String.valueOf(expoListResult.size()));
				paramMap.put("currPage", String.valueOf(currPage));
				paramMap.put("totalPage", String.valueOf(totalPage));				
				paramMap.put("record_size",String.valueOf(record_size));
											
				request.setAttribute("expoListResult", expoListResult);
				request.setAttribute("record_size", String.valueOf(expoListResult.size()));		
				
				subpage_key = "default";
			}else if(subpage_key.equals("detail")){
				DbTaoResult expoInqResult = (DbTaoResult)proc.detail_execute(context, request, dataSet);
				
				request.setAttribute("expoInqResult", expoInqResult);
				
				subpage_key = "detail";
			}
			
			paramMap.put("page_no", String.valueOf(page_no));
			paramMap.put("pgrs_yn", pgrs_yn);
			paramMap.put("co_nm", usrNm);
			paramMap.put("subpage_key", subpage_key);
			request.setAttribute("paramMap", paramMap);			
						
		}catch(Throwable t) {
			debug(TITLE, t);			
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
	}
}
