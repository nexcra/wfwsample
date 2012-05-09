/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfBkParTimeViewActn
*   �ۼ���    : (��)�̵������ ������
*   ����      : ��ŷ ƼŸ�� ����Ʈ
*   �������  : Golf
*   �ۼ�����  : 2009-05-26
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.booking.par;

import java.io.IOException;
import java.util.*;
import java.text.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
 
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.booking.GolfBkBenefitTimesDaoProc;
import com.bccard.golf.dbtao.proc.booking.GolfBkPermissionDaoProc;
import com.bccard.golf.dbtao.proc.booking.par.*;
import com.bccard.golf.dbtao.proc.booking.premium.GolfBkPreGrViewDaoProc;
import com.bccard.golf.user.entity.UcusrinfoEntity;

/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0 
******************************************************************************/
public class GolfBkParTimeViewActn extends GolfActn{
	
	public static final String TITLE = "��ŷƼŸ�� ����Ʈ";

	/***************************************************************************************
	* ��ž����Ʈ ������ȭ��
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/
	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		String permission = "";
		
		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);
		
		try {
			// 01.��������üũ
			int cyberMoney = 0;
			String userNm = "";
			String userId = "";
			int intMemGrade = 0;
			
			UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request);
			if(userEtt != null){
				cyberMoney = userEtt.getCyberMoney();
				userNm = userEtt.getName();
				userId = userEtt.getAccount();
				intMemGrade = userEtt.getIntMemGrade();
			}
			
			
			// 02.�Է°� ��ȸ		
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			
			paramMap.put("title", TITLE);	

			// 02. ������ idx ��������
			String bk_DATE = parser.getParameter("BK_DATE", "");
			String affi_GREEN_SEQ_NO = parser.getParameter("AFFI_GREEN_SEQ_NO", "");
			
			dataSet.setString("BK_DATE", bk_DATE);
			dataSet.setString("AFFI_GREEN_SEQ_NO", affi_GREEN_SEQ_NO);

			// 04.���� ���̺�(Proc) ��ȸ - ������ ����Ʈ �ڽ�
			GolfBkParTimeGrListDaoProc proc2 = (GolfBkParTimeGrListDaoProc)context.getProc("GolfBkParTimeGrListDaoProc");
			DbTaoResult titimeGreenList = (DbTaoResult) proc2.execute(context, request, dataSet);
			request.setAttribute("TitimeGreenList", titimeGreenList);

			
			// �ָ���ŷ ��뿩�� �����´�.
			String co_nm = "";	// �ָ���ŷ����
			GolfBkPreGrViewDaoProc proc_weekend = (GolfBkPreGrViewDaoProc)context.getProc("GolfBkPreGrViewDaoProc");
			DbTaoResult grViewResult = proc_weekend.execute_weekend(context, dataSet);
			if (grViewResult != null && grViewResult.isNext()) {
				grViewResult.first();
				grViewResult.next();
				co_nm = (String) grViewResult.getObject("CO_NM");
			}	
			dataSet.setString("co_nm", co_nm);
			
			
			// 04.���� ���̺�(Proc) ��ȸ - �޷�
			GolfBkParTimeListDaoProc proc = (GolfBkParTimeListDaoProc)context.getProc("GolfBkParTimeListDaoProc");
			DbTaoResult listResult = (DbTaoResult) proc.execute(context, request, dataSet);
			request.setAttribute("ListResult", listResult);
			
			// 04.���� ���̺�(Proc) ��ȸ - ������
			GolfBkParTimeHolyListDaoProc proc4 = (GolfBkParTimeHolyListDaoProc)context.getProc("GolfBkParTimeHolyListDaoProc");
			DbTaoResult titimeHolyList = (DbTaoResult) proc4.execute(context, request, dataSet);
			request.setAttribute("TitimeHolyList", titimeHolyList);
			
			// 04.���� ���̺�(Proc) ��ȸ - �������� Ȯ��
			GolfBkParTimeResultDaoProc proc5 = (GolfBkParTimeResultDaoProc)context.getProc("GolfBkParTimeResultDaoProc");
			DbTaoResult titimeResult = (DbTaoResult) proc5.execute(context, request, dataSet);
			request.setAttribute("TitimeResult", titimeResult);
			

			// 05. ��ŷ Ƚ�� ��ȸ - ���̹� �Ӵ� ��볻���� ȸ������ �����ȴ�.
			int par_3_BOKG_YR = 0;			// �ϳ� ��� �Ǽ�
			int par_3_BOKG_MO = 0;			// �Ѵ� ��� �Ǽ�
			int par_3_BOKG_YR_GREEN = 0;	// �ϳ� ��� �Ǽ�(�����庰)
			int par_3_BOKG_MO_GREEN = 0;	// �Ѵ� ��� �Ǽ�(�����庰)
			

			GolfBkBenefitTimesDaoProc proc_times = (GolfBkBenefitTimesDaoProc)context.getProc("GolfBkBenefitTimesDaoProc");
			DbTaoResult parTimeView = proc_times.getParBenefit(context, dataSet, request);
			if(parTimeView.isNext()){
				parTimeView.next();
				par_3_BOKG_YR = parTimeView.getInt("PAR_3_BOKG_YR");
				par_3_BOKG_MO = parTimeView.getInt("PAR_3_BOKG_MO");
				par_3_BOKG_YR_GREEN = parTimeView.getInt("PAR_3_BOKG_YR_GREEN");
				par_3_BOKG_MO_GREEN = parTimeView.getInt("PAR_3_BOKG_MO_GREEN"); 
			}
			
//			GolfBkParTimesDaoProc proc_times = (GolfBkParTimesDaoProc)context.getProc("GolfBkParTimesDaoProc");
//			DbTaoResult timesView = proc_times.execute(context, dataSet, request);
//			timesView.next();
//			if(timesView.getString("RESULT").equals("00")){
//				par_3_BOKG_YR = timesView.getString("PAR_3_BOKG_YR");
//				par_3_BOKG_MO = timesView.getString("PAR_3_BOKG_MO");
//			}
			
			debug("par_3_BOKG_YR : " + par_3_BOKG_YR + " / par_3_BOKG_MO : " + par_3_BOKG_MO); 
			debug("par_3_BOKG_YR_GREEN : " + par_3_BOKG_YR_GREEN + " / PAR_3_BOKG_MO_GREEN : " + par_3_BOKG_MO_GREEN);
			
			// 04. ���ٱ��� ��ȸ	
			String permissionColum = "PAR_3_BOKG_LIMT_YN";
			GolfBkPermissionDaoProc proc_permission = (GolfBkPermissionDaoProc)context.getProc("GolfBkPermissionDaoProc");
			DbTaoResult permissionView = proc_permission.execute(context, dataSet, userId, permissionColum);

			permissionView.next(); 
			if(permissionView.getString("RESULT").equals("00")){
				permission = permissionView.getString("LIMT_YN");
				//debug("=`=`=`=`=`=`=`=`=GolfBkPreGrListActn === PMI_BOKG_APO_YN => " + permissionView.getString("PMI_BOKG_APO_YN"));
			}else{
				permission = "N"; 
			}
			
			debug("par3 permission : "+ permission);
			
			
			paramMap.put("permission",permission);
			paramMap.put("AFFI_GREEN_SEQ_NO_ECN", affi_GREEN_SEQ_NO);	
			paramMap.put("BK_DATE_ECN", bk_DATE);		
			paramMap.put("cyberMoney", cyberMoney+"");	
			paramMap.put("userNm", userNm);	
			paramMap.put("intMemGrade", intMemGrade+"");	
			paramMap.put("PAR_3_BOKG_YR", par_3_BOKG_YR+"");	
			paramMap.put("PAR_3_BOKG_MO", par_3_BOKG_MO+"");	
			paramMap.put("PAR_3_BOKG_YR_GREEN", par_3_BOKG_YR_GREEN+"");	
			paramMap.put("PAR_3_BOKG_MO_GREEN", par_3_BOKG_MO_GREEN+"");
	        request.setAttribute("BK_DATE", bk_DATE);
	        request.setAttribute("paramMap", paramMap);
	        
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
