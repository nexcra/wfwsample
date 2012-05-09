/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������	: GolfLsnPreVodUpdActn
*   �ۼ���	: (��)�̵������ 
*   ����		: ����� > ���� > �����̾� ������ > ��ȸ�� ������Ʈ
*   �������	: Golf
*   �ۼ�����	: 2009-12-21
************************** �����̷� ****************************************************************
*    ����     �ۼ���   �������
*  20110304  �̰���   [http://www.bccard.com/->Home > VIP���� > ���� > ���� VIP������] -��ȸ�� ������Ʈ �� �����ߴ��� �α� ���
*  20110512  �̰���   [http://golfloung.familykorail.com/-> Home > ���縶�� > �������� > �����󷹽�] - ��ȸ�� ������Ʈ �� �����ߴ��� �α� ���
***************************************************************************************************/
package com.bccard.golf.action.lesson;

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
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.proc.lesson.GolfLsnVodListDaoProc;


/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0
******************************************************************************/
public class GolfLsnPreVodUpdActn extends GolfActn{ 
	
	public static final String TITLE = "����� > ���� > �����̾� ������ > ��ȸ�� ������Ʈ";

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

			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);

			// Request �� ����
			String seq_no = parser.getParameter("SEQ_NO", "");
			debug("## GolfLsnPreVodUpdActn | seq_no : " + seq_no + "\n");     
			
			String in = request.getAttribute("actnKey").toString();
			
			//[ http://www.bccard.com/->VIP����/����/~ ]���� ���ӽ�
			if (in.equals("golfVodHitCntInBC")){	
				info("## "+this.getClass().getName()+" | 'http://www.bccard.com/->VIP����/����/���� VIP������'���� ���� (��ȸ�� ������Ʈ)" );
			}
			
			//�ڷ��� [ http://golfloung.familykorail.com]���� ���ӽ�
			if (in.equals("golfVodHitCntInKorail")){	
				info("## "+this.getClass().getName()+" | 'http://golfloung.familykorail.com -> �ڷ���'���� ���� (��ȸ�� ������Ʈ)" );
			}
						
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("SEQ_NO", seq_no);

			// ���� ���̺�(Proc) ��ȸ
			GolfLsnVodListDaoProc proc = (GolfLsnVodListDaoProc)context.getProc("GolfLsnVodListDaoProc");
			int result = proc.updateInqrNum(context, request, dataSet);	

	        request.setAttribute("paramMap", paramMap);
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
