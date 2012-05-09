/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfFieldGofdListActn
*   �ۼ���    : (��)����Ŀ�´����̼� ����ȯ
*   ����      : ���������� �ȳ� �󼼺���(��������)
*   �������  : Golf
*   �ۼ�����  : 2009-06-05
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.lounge;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.admin.code.GolfAdmCodeSelDaoProc;
import com.bccard.golf.dbtao.proc.lounge.GolfFieldGofdListDaoProc;

/******************************************************************************
* Golf
* @author	(��)����Ŀ�´����̼�
* @version	1.0
******************************************************************************/
public class GolfFieldGofdListActn extends GolfActn{
	
	public static final String TITLE = " ���������� �ȳ� �󼼺���(��������)";

	/***************************************************************************************
	* ���� ������ȭ��
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
			
			// 01.��������üũ
			
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			paramMap.put("imgUrlPath", AppConfig.getAppProperty("IMG_URL_REAL")+"/lounge");
			
			
			// Request �� ����
			long page_no		= parser.getLongParameter("page_no", 1L);			// ��������ȣ
			long record_size	= parser.getLongParameter("record_size", 4);		// ����������¼�
			String search_sel		= parser.getParameter("search_sel", "");
			String search_word		= parser.getParameter("search_word", "");
			
			long gf_seq_no	= parser.getLongParameter("p_idx", 0L);
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setLong("PAGE_NO", page_no);
			dataSet.setLong("RECORD_SIZE", record_size);
			dataSet.setString("SEARCH_SEL", search_sel);
			dataSet.setString("SEARCH_WORD", search_word);
			
			dataSet.setLong("GF_SEQ_NO", gf_seq_no);
			
			
			// 04.���� ���̺�(Proc) ��ȸ
			
			GolfFieldGofdListDaoProc proc = (GolfFieldGofdListDaoProc)context.getProc("GolfFieldGofdListDaoProc");
			
			DbTaoResult golffieldGofdListResult = (DbTaoResult) proc.execute(context, request, dataSet);
			
			
			// ��ü 0��  [ 0/0 page] ���� ��������
			long totalRecord = 0L;
			long currPage = 0L;
			long totalPage = 0L;
			
			if (golffieldGofdListResult != null && golffieldGofdListResult.isNext()) {
				golffieldGofdListResult.first();
				golffieldGofdListResult.next();
				if (golffieldGofdListResult.getObject("RESULT").equals("00")) {
					totalRecord = Long.parseLong((String)golffieldGofdListResult.getString("TOTAL_CNT"));
					currPage = Long.parseLong((String)golffieldGofdListResult.getString("CURR_PAGE"));
					totalPage = (totalRecord % record_size == 0) ? (totalRecord / record_size) : (totalRecord / record_size)+1;
				}
			}
			
			
			paramMap.put("totalRecord", String.valueOf(totalRecord));
			paramMap.put("currPage", String.valueOf(currPage));
			paramMap.put("totalPage", String.valueOf(totalPage));
			paramMap.put("resultSize", String.valueOf(golffieldGofdListResult.size()));
			
			request.setAttribute("golffieldGofdListResult", golffieldGofdListResult);
			request.setAttribute("record_size", String.valueOf(record_size));
		    request.setAttribute("paramMap", paramMap);
	    
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
