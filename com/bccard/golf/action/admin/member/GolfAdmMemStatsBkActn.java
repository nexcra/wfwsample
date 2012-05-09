/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfadmGrListActn
*   �ۼ���    : (��)�̵������ ������
*   ����      : ������ > ��ŷ > �����̾� > ������ ����Ʈ
*   �������  : Golf
*   �ۼ�����  : 2009-05-14
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.admin.member;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.admin.member.*;

/******************************************************************************
* Golf
* @author	(��)�̵������ 
* @version	1.0
******************************************************************************/
public class GolfAdmMemStatsBkActn extends GolfActn{
	
	public static final String TITLE = "������ > ���ΰ��� > ȸ������ > ��ŷ���";
	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		
		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);
		
		try {
			// 01.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			String type	= parser.getParameter("type", "nm");
			
			String type_nm	= "";
			if("nm".equals(type)){
				type_nm = "�Ϲݺ�ŷ";
			}else if("par3".equals(type)){
				type_nm = "��3 ��ŷ";
			}else if("range".equals(type)){
				type_nm = "SKY72 �帲����������";
			}else if("duns".equals(type)){
				type_nm = "SKY72 �帲�ὺ";
			}else if("jeju".equals(type)){
				type_nm = "���ְ�������";
			}else if("ls".equals(type)){
				type_nm = "����";
			}else if("vip".equals(type)){
				type_nm = "VIP ��ŷ";
			}else if("gr".equals(type)){
				type_nm = "�׸�������";
			}
			paramMap.put("type",type);
			paramMap.put("type_nm",type_nm);
			
			// 02.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("type", type);
			dataSet.setString("type_nm", type_nm);

			// ���-����
			GolfAdmMemStatsBkDaoProc proc = (GolfAdmMemStatsBkDaoProc)context.getProc("GolfAdmMemStatsBkDaoProc");
			DbTaoResult resultSet = (DbTaoResult) proc.execute(context, request, dataSet);

			request.setAttribute("resultSet", resultSet);
			request.setAttribute("paramMap", paramMap);
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t); 
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
