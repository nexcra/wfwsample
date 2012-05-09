/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfEvntTmMovieCpnPopActn
*   �ۼ���    : (��)�̵������ ������
*   ����      : �̺�Ʈ > TM ��ȭ ���ű� �̺�Ʈ > ������ȣ ��� �˾�
*   �������  : Golf
*   �ۼ�����  : 2010-03-19
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.event;

/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfEvntMkMemberInsActn
*   �ۼ���    : ������
*   ����      : �������
*   �������  : golf
*   �ۼ�����  : 2010-09-01
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
public class GolfEvntMkMemberInsActn extends GolfActn{
	
	public static final String TITLE = "���� ��� ó��";

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

		try {
			// 01.��������üũ
			HttpSession session = request.getSession(true);
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
		 	
			 if(usrEntity != null) {
				user_id		= (String)usrEntity.getAccount(); 
			}
			
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			
			String jumin_no = parser.getParameter("jumin_no", "");
			String hg_nm = parser.getParameter("hg_nm", "");
			String mer_no = parser.getParameter("mer_no", "");
			String cupn_no = "";
			debug("jumin_no = "+jumin_no);
			debug("hg_nm = "+hg_nm);
			debug("mer_no = "+mer_no); 
			int result_cnt = 0;
			String script = "";
			int addResult =0;
						
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("JUMIN_NO", jumin_no);
			dataSet.setString("HG_NM", hg_nm);
			dataSet.setString("MER_NO", mer_no);
			
			GolfEvntMkMemberProc proc = (GolfEvntMkMemberProc)context.getProc("GolfEvntMkMemberProc");
			debug("PROC~~~~");
			//03-01 ��� ���� ��ȣ �����ͼ�
			DbTaoResult couponNum = (DbTaoResult) proc.getCouponNum(context, request, dataSet);	//������ȣ ��������
			debug("couponNum~~~~~");
			if (couponNum != null && couponNum.isNext()) {
				couponNum.next();
				if(couponNum.getString("RESULT").equals("00")){
					debug("CUPNNO = "+couponNum.getString("CUPN_NO"));
					cupn_no = couponNum.getString("CUPN_NO");
					dataSet.setString("CUPN_NO", cupn_no);
				}
			}
			
			if(!"".equals(cupn_no) && cupn_no != null)
			{
				//03-02 ��û ���� �˾ƺ���
				result_cnt = proc.getCuponCnt(context, mer_no, jumin_no);
				
				debug("## result_cnt:"+result_cnt);
				if(result_cnt>=2){
					debug("�̹� 2ȸ ��µǼ� �߱޾ȵ�");
					script = "alert('������ 2ȸ ����� �����ϼ̽��ϴ� ');";
				}else{
					debug("�߱�ó������");
					
					// 04.�̺�Ʈ ���̺� ���
					addResult = proc.getMkInsCupon(context, request, dataSet);
					
					request.setAttribute("returnUrl", reUrl);
					
			        if (addResult == 1) {
			        	//�߱޿Ϸ��� ���°� Y�� ����
			        	int updateMarking = proc.updateMarking(context, mer_no, cupn_no);
			        	
						request.setAttribute("resultMsg", "");      	
			        } else {
						request.setAttribute("resultMsg", "����ó�� ���� �ʾҽ��ϴ�.\\n\\n�ݺ������� ��ϵ��� ���� ��� �����ڿ� �����Ͻʽÿ�.");		        		
			        }
				}
			
			}
			else
			{
				debug("���� ������ȣ ��������");
				script = "alert('������ 2ȸ �߱� �����̽��ϴ�. \\n\\n�����մϴ�.');";
			}
			
			// 04. Return �� ����			
			paramMap.put("addResult", String.valueOf(addResult));			
	        request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.	
	        request.setAttribute("script", script);
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}

