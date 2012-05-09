/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfEvntMkCupnmarkingActn
*   �ۼ���    : (��)�̵������ ������
*   ����      : ��� ���� ��ŷ ó��
*   �������  : Golf
*   �ۼ�����  : 2010-09-02
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.event;

/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfEvntMkCupnMarkingActn
*   �ۼ���    : ������
*   ����      : ������ŷ ó��
*   �������  : golf
*   �ۼ�����  : 2010-09-02
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.bbs.GolfBoardComtInsDaoProc;
import com.bccard.golf.dbtao.proc.event.GolfEvntMkMemberProc;

import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.golf.common.loginAction.SessionUtil;
/******************************************************************************
* Golf
* @author	(��)����Ŀ�´����̼�
* @version	1.0
******************************************************************************/
public class GolfEvntMkCupnMarkingActn extends GolfActn{
	
	public static final String TITLE = "��� ���� ��ŷ ó��";

	/***************************************************************************************
	* ���� ������ȭ��
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		
		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout");
		String reUrl = super.getActionParam(context, "reUrl");
		request.setAttribute("layout", layout);
		String user_id ="";
		String jumin_no ="";

		try {
			// 01.��������üũ
			HttpSession session = request.getSession(true);
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
		 	
			 if(usrEntity != null) {
				user_id		= (String)usrEntity.getAccount(); 
				jumin_no		= (String)usrEntity.getSocid(); 
			}
			
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			
			
			String strResultCode = "";
			
			//������ ��ȣ 
			String mer_no = parser.getParameter("card_no", "");
			String cupn_no = parser.getParameter("cupn_no", "");
			String seq_no = parser.getParameter("seq_no", "");
			debug("cupn_no = "+cupn_no);
						
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("MER_NO", mer_no);
			dataSet.setString("CUPN_NO", cupn_no);
			dataSet.setString("SEQ_NO", seq_no);
			dataSet.setString("JUMIN_NO", jumin_no);
			
			GolfEvntMkMemberProc proc = (GolfEvntMkMemberProc)context.getProc("GolfEvntMkMemberProc");
			
			/*/04 ��� ���� ��ȣ �����ͼ�
			DbTaoResult couponNum = (DbTaoResult) proc.getCouponNum(context, request, dataSet);	//������ȣ ��������
			
			if (couponNum != null && couponNum.isNext()) {
				couponNum.first(); 
				couponNum.next();
				if(couponNum.getString("RESULT").equals("00")){
					cupn_no = couponNum.getString("CUPN_NO");
					dataSet.setString("CUPN_NO", cupn_no);
				}
			}*/
			
			//05  �μ� Ƚ�� ����
			int updateMarking = proc.updatePrtHit(context, mer_no, cupn_no);
			
			if(updateMarking>0)
			{				
				strResultCode = "Y";
			}else{
				strResultCode = "N";
			}
			
			// 04.���� ���̺�(Proc) ��ȸ
			DbTaoResult evntMkMemberAppDetail = proc.getMkMemberAppDetail(context, dataSet);
			DbTaoResult evntMkPrcGroundDetail = proc.evntMkPrcGroundDetail(context, dataSet);
			DbTaoResult getMkMember = proc.getMkMember(context, request, dataSet);		//���� ������ ����
			
			
			// 05. Return �� ����	 		
			//debug("lessonInq.size() ::> " + lessonInq.size());			
			request.setAttribute("evntMkMemberAppDetail", evntMkMemberAppDetail);
			request.setAttribute("evntMkPrcGroundDetail", evntMkPrcGroundDetail);
			request.setAttribute("getMkMember", getMkMember);
			
			
			// 06. Return �� ����			
	        request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.			
	        request.setAttribute("strResultCode", strResultCode);
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}

