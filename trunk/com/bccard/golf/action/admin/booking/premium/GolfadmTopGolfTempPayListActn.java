/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfadmTopGolfTempPayListActn
*   �ۼ���    : shin cheong gwi
*   ����      : ������ ����
*   �������  : golfloung
*   �ۼ�����  : 2010-12-02
************************** �����̷� ****************************************************************
*
***************************************************************************************************/
package com.bccard.golf.action.admin.booking.premium;

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
import com.bccard.golf.dbtao.proc.admin.booking.premium.GolfadmTopGolfTempPayListProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

public class GolfadmTopGolfTempPayListActn extends GolfActn {
	public static final String TITLE = "������ > ������ ���� ����";  
	
	// ������ ����
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException
	{
		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);
		String viewType = "default";
		
		try
		{
			// 01.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(viewType, request, response);
			Map paramMap = BaseAction.getParamToMap(request);			
			paramMap.put("title", TITLE);
			
			long pageNo = parser.getLongParameter("pageNo", 1L);						// ��������ȣ
            long recordsInPage = parser.getLongParameter("recordsInPage", 20);			// �������� ��¼�
            long totalPage = 0L;														// ��ü��������
			long recordCnt = 0L; 
            String sh_id = parser.getParameter("sh_id", "");							//�˻�Ű
			String sh_nm = parser.getParameter("sh_nm", "");							//�˻�Ű
			
			// 02.Proc �� ���� �� ���� 
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
				dataSet.setLong("pageNo", pageNo);
	            dataSet.setLong("recordsInPage", recordsInPage);
	            dataSet.setString("sh_id", sh_id);
	            dataSet.setString("sh_nm", sh_nm);
			
			// 03.Proc ����
	        GolfadmTopGolfTempPayListProc instance = GolfadmTopGolfTempPayListProc.getInstance();
	        DbTaoResult tempPayList = instance.execute(context, request, dataSet);
	        if(tempPayList.isNext()){
	        	tempPayList.next();
	        	if(tempPayList.getString("RESULT").equals("00")){
	        		paramMap.put("recordCnt", String.valueOf(tempPayList.getLong("totalRecord")));
	        		recordCnt = tempPayList.getLong("totalRecord");
	        	}else{ 
	        		paramMap.put("recordCnt", "0");
					recordCnt = 0L;
	        	}
	        }
	        
	        totalPage = (recordCnt % recordsInPage == 0) ? (recordCnt / recordsInPage) : (recordCnt / recordsInPage) + 1;
	        
	        // 04. Parameter Set
	        request.setAttribute("tempPayList", tempPayList);
	        paramMap.put("listSize", String.valueOf(tempPayList.size()));
	        paramMap.put("pageNo",String.valueOf(pageNo));
	        paramMap.put("recordsInPage",String.valueOf(recordsInPage));
	        paramMap.put("totalPage", String.valueOf(totalPage));
	        paramMap.put("sh_id", sh_id);
	        paramMap.put("sh_nm", sh_nm);
	        request.setAttribute("paramMap", paramMap);
	        
		}catch(Throwable t) {
			debug(TITLE, t);			
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, viewType);
	}
}
