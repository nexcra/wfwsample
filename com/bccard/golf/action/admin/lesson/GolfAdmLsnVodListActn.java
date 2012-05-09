/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmLsnVodListActn
*   �ۼ���    : (��)����Ŀ�´����̼� ���¿�
*   ����      : ������ ���������� ����Ʈ
*   �������  : Golf
*   �ۼ�����  : 2009-05-21
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.admin.lesson;

import java.io.IOException;
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
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.admin.lesson.GolfAdmLsnVodListDaoProc;
import com.bccard.golf.dbtao.proc.admin.code.GolfAdmCodeSelDaoProc;

/******************************************************************************
* Topn
* @author	(��)����Ŀ�´����̼�
* @version	1.0
******************************************************************************/
public class GolfAdmLsnVodListActn extends GolfActn{
	
	public static final String TITLE = "������ ���������� ����Ʈ";

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

			// �ڵ尪 ���� 
			String svod_clss_code = "";	//�Ϲݵ�����-0007, �����̾�������-0045 
			String svod_lsn_code = "";	//�Ϲݵ����󷹽�-0013, �����̾������󷹽�-0046			
			
			// Request �� ����
			long page_no		= parser.getLongParameter("page_no", 1L);			// ��������ȣ
			long record_size	= parser.getLongParameter("record_size", 10);		// ����������¼�
			String search_sel		= parser.getParameter("search_sel", "");
			String search_word		= parser.getParameter("search_word", "");
			String is_premi			= parser.getParameter("is_p", "N");				//Y:�����̾� ������, N:�Ϲ� ������
			
			String svod_clss		= parser.getParameter("svod_clss", ""); 		//0001:�ʺ��ڷ��� 0002:�ݺ����� 0003:����Ʈ���� 0004:PGA���� 0005:�̹������� 	
			String svod_lsn_clss	= parser.getParameter("svod_lsn_clss", ""); 	//0001:�׸� 0002:��巹��
			
 			// ������ Ÿ��Ʋ ����(�Ϲ� ������, �����̾� ������)
			if(is_premi.equals("Y")){
				paramMap.put("str_title", "�����̾� ������ ����Ʈ");		
				svod_clss_code = "0045";
				svod_lsn_code = "0046";				
			}else{
				paramMap.put("str_title", "���� ������ ����Ʈ");
				svod_clss_code = "0007";
				svod_lsn_code = "0013";				
			}
			
			paramMap.put("svod_clss", svod_clss);
			paramMap.put("svod_lsn_clss", svod_lsn_clss);
			paramMap.put("is_p", is_premi);			
						
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setLong("PAGE_NO", page_no);
			dataSet.setLong("RECORD_SIZE", record_size);
			dataSet.setString("SEARCH_SEL", search_sel);
			dataSet.setString("SEARCH_WORD", search_word);
			dataSet.setString("SVOD_CLSS", svod_clss);
			dataSet.setString("SVOD_LSN_CLSS", svod_lsn_clss);
			
			// 04.���� ���̺�(Proc) ��ȸ
			GolfAdmLsnVodListDaoProc proc = (GolfAdmLsnVodListDaoProc)context.getProc("GolfAdmLsnVodListDaoProc");
			GolfAdmCodeSelDaoProc coodSelProc = (GolfAdmCodeSelDaoProc)context.getProc("GolfAdmCodeSelDaoProc");
			DbTaoResult lsnVodListResult = (DbTaoResult) proc.execute(context, request, dataSet);
			DbTaoResult vodClssSel = (DbTaoResult) coodSelProc.execute(context, dataSet, svod_clss_code, "Y"); //�����󱸺�
			DbTaoResult vodLsnClssSel = (DbTaoResult) coodSelProc.execute(context, dataSet, svod_lsn_code, "Y"); //�����󷹽��з�
			
			paramMap.put("resultSize", String.valueOf(lsnVodListResult.size()));
			
			request.setAttribute("lsnVodListResult", lsnVodListResult);
			request.setAttribute("vodClssSel", vodClssSel);
			request.setAttribute("vodLsnClssSel", vodLsnClssSel);
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
