/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfEvntBkMMInqActn
*   �ۼ���    : ������ ���弱
*   ����      : �� ������ ��ŷ �̺�Ʈ �󼼺���
*   �������  : Golf
*   �ۼ�����  : 2009-06-08
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.event;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.booking.GolfBkBenefitTimesDaoProc;
import com.bccard.golf.dbtao.proc.booking.GolfBkPermissionDaoProc;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.WaContext;

/******************************************************************************
* Golf
* @author	(��)����Ŀ�´����̼�
* @version	1.0
******************************************************************************/
public class GolfEvntBkMMInqActn extends GolfActn{
	
	public static final String TITLE = "�� ������ ��ŷ �̺�Ʈ �󼼺���";

	/***************************************************************************************
	* ���� �����ȭ��
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
			//HttpSession session = request.getSession(true);
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);

			String userId = "";
			String intMemGrade = "";
			String permission = ""; 
			String currMonth = DateUtil.currdate("yyyyMM");
			String currDate = DateUtil.currdate("yyyyMMdd");
			String endDate = DateUtil.dateAdd('d', 21, currDate,"yyyyMMdd"); // 21����

//			debug("currDate>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + currDate);
//			debug("endDate>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + endDate);
			
			if(usrEntity != null) {				
				userId		= (String)usrEntity.getAccount(); 
				intMemGrade = String.valueOf((int)usrEntity.getIntMemGrade());
			}
//			debug(">>>>>>>>>>>>>   imtMemGrade : "+intMemGrade);
/*
			if(!(intMemGrade.equals("1") || intMemGrade.equals("2") || intMemGrade.equals("2"))){
				subpage_key = "deny";
				return super.getActionResponse(context, subpage_key);				
			}
*/			
			// 02.�Է°� ��ȸ		
			//RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);	
			paramMap.put("currMonth",currMonth);
			paramMap.put("currDate",currDate);
			paramMap.put("endDate",endDate);
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("userId", userId);                  //����� ID����
			dataSet.setString("intMemGrade", intMemGrade);        //����� ȸ�����
			
			
			
			// 04.���� ���̺�(Proc) ��ȸ
			/*
			GolfEvntBkMMDaoProc proc = (GolfEvntBkMMDaoProc)context.getProc("GolfEvntBkMMDaoProc");
			DbTaoResult evntBkMMInq = proc.execute(context, dataSet);

			String cnt = "";          //����ڰ� ������ Ƚ��(����)
			String tot_cnt = "";      //���� ���� ���ɼ�
			
			if(evntBkMMInq.isNext()){
				evntBkMMInq.next();
				cnt = evntBkMMInq.getString("CNT");
				tot_cnt = evntBkMMInq.getString("TOT");
debug("cnt>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + cnt);
debug("tot_cnt>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + tot_cnt);
				paramMap.put("cnt",cnt);
				paramMap.put("tot_cnt",tot_cnt);
			} 
			*/
			
			
			// 05. ���ٱ��� ��ȸ : ���� - 20091029 
			String permissionColum = "PMI_EVNT_APO_YN";
			GolfBkPermissionDaoProc proc_permission = (GolfBkPermissionDaoProc)context.getProc("GolfBkPermissionDaoProc");
			DbTaoResult permissionView = proc_permission.execute(context, dataSet, userId, permissionColum);

			permissionView.next();
			if(permissionView.getString("RESULT").equals("00")){
				permission = permissionView.getString("LIMT_YN");
				
			}else{
				permission = "N";
			}
			
			debug("## GolfEvntBkMMInqActn | userId : "+userId+" | intMemGrade : "+intMemGrade+" | permission : "+permission );
			
			// 06.�ѽ���ī��Ʈ ���ϱ�
			String cnt = "0";
			String tot_cnt = "0";
			String can_cnt = "0";
			String blockDate = "";
			
			// 06.�ѽ���ī��Ʈ ���ϱ�
			GolfBkBenefitTimesDaoProc proc_count = (GolfBkBenefitTimesDaoProc)context.getProc("GolfBkBenefitTimesDaoProc");
			DbTaoResult evntMMInq = proc_count.getPreBkEvntBenefit2(context, dataSet, request);
			if(evntMMInq.isNext()){
				evntMMInq.next();
				
				tot_cnt = Integer.toString(evntMMInq.getInt("PRE_EVNT_PMI_NUM"));
				cnt = Integer.toString(evntMMInq.getInt("PRE_EVNT_BOKG_DONE"));
				can_cnt = Integer.toString(evntMMInq.getInt("PRE_EVNT_BOKG_MO"));
				blockDate = evntMMInq.getString("blockDate");
				
				debug("Actn : ���Ǽ�: cnt>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + cnt);
				debug("Actn : �����Ǽ� : can_cnt>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + can_cnt);
				debug("Actn : �ѻ���� ���ִ°Ǽ� : tot_cnt>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + tot_cnt);
				debug("Actn : ������ : blockDate>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + blockDate);
			}
			
		

			paramMap.put("cnt",cnt);
			paramMap.put("tot_cnt",tot_cnt);
			paramMap.put("can_cnt",can_cnt);
			paramMap.put("blockDate",blockDate);
			paramMap.put("permission",permission);
			
			//request.setAttribute("evntBkMMInqResult", evntBkMMInq);	
	        request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.		
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
