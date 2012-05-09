/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfEvntBkMMCnclActn
*   �ۼ���    : ������ ���弱
*   ����      : �������� ��ŷ �̺�Ʈ ��û ó��
*   �������  : golf
*   �ۼ�����  : 2009-09-10
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.event;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.SmsSendProc;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.admin.event.GolfAdmSpecialBookingDaoProc;
import com.bccard.golf.dbtao.proc.booking.GolfBkBenefitTimesDaoProc;
import com.bccard.golf.dbtao.proc.booking.GolfBkPermissionDaoProc;
import com.bccard.golf.dbtao.proc.event.GolfEvntBkMMDaoProc;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

/******************************************************************************
* Golf
* @author	
* @version	1.0
******************************************************************************/
public class GolfEvntBkMMCnclActn extends GolfActn{
	
	public static final String TITLE = "9�� VIP ��ŷ �̺�Ʈ ��û ó��";

	/***************************************************************************************
	* ���� �����ȭ��
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		String userId = "";
		String isLogin = "";   	
		String intMemGrade = "";
		String userNm = "";
		String hp_ddd_no = "";
		String hp_tel_hno = "";
		String hp_tel_sno = "";
		String email = "";
		String permission = "";
		
		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout");
		//String reUrl = super.getActionParam(context, "reUrl");
		//String errReUrl = super.getActionParam(context, "errReUrl");
		request.setAttribute("layout", layout);

		try {
			// 01.��������üũ
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
		 	
			if(usrEntity != null) {					
				userId			= (String)usrEntity.getAccount();  
				intMemGrade		= String.valueOf((int)usrEntity.getIntMemGrade());
				userNm			= (String)usrEntity.getName(); 				
				email       = (String)usrEntity.getEmail1(); 
			}

			if(userId != null && !"".equals(userId)){
				isLogin = "1";
			} else {
				isLogin = "0";				
			}
			
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			
			String cncl_param	= parser.getParameter("cncl_param","");     //���� ���� �ڵ�
			String aplc_seq_no	= parser.getParameter("aplc_seq_no","");    //�����ȣ
			
			
			paramMap.put("title", TITLE);						
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);

			dataSet.setString("cdhd_id",userId);
			dataSet.setString("userId",userId);
			dataSet.setString("intMemGrade", intMemGrade);
			dataSet.setString("cncl_param", cncl_param);
			dataSet.setString("aplc_seq_no", aplc_seq_no);

			// 04.���� ���̺�(Proc) ��ȸ
			
			
			GolfAdmSpecialBookingDaoProc proc2 = (GolfAdmSpecialBookingDaoProc)context.getProc("GolfAdmSpecialBookingDaoProc");
			DbTaoResult evntCncl    = proc2.setCancel(context, request, dataSet);

			if(evntCncl.isNext()){
				evntCncl.next();
				String ret = evntCncl.getString("RESULT");
				if(ret.equals("00") && cncl_param.equals("B")){
					//request.setAttribute("msg","���������� ��ŷ Ȯ�� ó�� �Ǿ����ϴ�.");
					DbTaoResult det = proc2.getDetail(context, request, dataSet);
					
					String pu_date		= "";
					String pu_time		= "";
					String dprt_pl_info = "";

					if(det.isNext()){
						det.next();
						pu_date = det.getString("PU_DATE");
						pu_time = det.getString("PU_TIME");
						dprt_pl_info = det.getString("DPRT_PL_INFO");
						hp_ddd_no		= det.getString("HP_DDD_NO");
						hp_tel_hno		= det.getString("HP_TEL_HNO");
						hp_tel_sno		= det.getString("HP_TEL_SNO");
					}

debug("pu_date>>>>>>>>>>>>>>>>>>>>>>>>>" + pu_date);
debug("pu_time>>>>>>>>>>>>>>>>>>>>>>>>>" + pu_time);

					HashMap smsMap = new HashMap();
				
					smsMap.put("ip", request.getRemoteAddr());
					smsMap.put("sName", userNm);
					smsMap.put("sPhone1", hp_ddd_no);
					smsMap.put("sPhone2", hp_tel_hno);
					smsMap.put("sPhone3", hp_tel_sno);
					smsMap.put("sCallCenter", "15666578");

					debug("SMS����>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>..");
					String smsClss = "674";
					String message = "[Golf Loun.G] "+userNm+"��," + pu_date.substring(5,7) + "��" +pu_date.substring(8,10) + "��" + pu_time + "�ÿ� " + dprt_pl_info + "���� ��ŷȮ�� �Ǿ����ϴ�" ;
					SmsSendProc smsProc = (SmsSendProc)context.getProc("SmsSendProc");
					String smsRtn = "";
					if(!email.equals("msj9520")){
						smsRtn = smsProc.send(smsClss, smsMap, message);
					}
					debug("SMS>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>.."+message);
				}
			}
			/*
			//�� ī��Ʈ ���ϱ�
			
			DbTaoResult evntBkMMInq = proc.execute(context, dataSet);

			String cnt = "";
			String tot_cnt = "";
			
			if(evntBkMMInq.isNext()){
				evntBkMMInq.next();
				cnt = evntBkMMInq.getString("CNT");
				tot_cnt = evntBkMMInq.getString("TOT");

				//paramMap.put("cnt",cnt);
				//paramMap.put("tot_cnt",tot_cnt);
			}
			*/
			String cnt = "0";
			String tot_cnt = "0";
			String can_cnt = "0";
		

			
			// 06.�ѽ���ī��Ʈ ���ϱ�
			GolfBkBenefitTimesDaoProc proc_count = (GolfBkBenefitTimesDaoProc)context.getProc("GolfBkBenefitTimesDaoProc");
			DbTaoResult evntMMInq = proc_count.getPreBkEvntBenefit(context, dataSet, request);
			if(evntMMInq.isNext()){
				evntMMInq.next();
				
				tot_cnt = Integer.toString(evntMMInq.getInt("PRE_EVNT_PMI_NUM"));
				cnt = Integer.toString(evntMMInq.getInt("PRE_EVNT_BOKG_DONE"));
				can_cnt = Integer.toString(evntMMInq.getInt("PRE_EVNT_BOKG_MO"));
				debug("CNCL Actn : ���Ǽ�: cnt>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + cnt);
				debug("CNCL Actn : �����Ǽ� : can_cnt>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + can_cnt);
				debug("CNCL Actn : �ѻ���� ���ִ°Ǽ� : tot_cnt>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + tot_cnt);
				
			}
			
 
			paramMap.put("cnt",cnt);
			paramMap.put("tot_cnt",tot_cnt);
			paramMap.put("can_cnt",can_cnt);

			
			// 04.���� ���̺�(Proc) ��ȸ
			GolfEvntBkMMDaoProc proc = (GolfEvntBkMMDaoProc)context.getProc("GolfEvntBkMMDaoProc");
			DbTaoResult evntResult = (DbTaoResult)proc.execute(context, dataSet);
			String str_memGradeTxt = proc.getAllGrade(context, dataSet, userId);

			
			// 05. ���ٱ��� ��ȸ : ���� - 20091029 
			String permissionColum = "PMI_EVNT_APO_YN";
			GolfBkPermissionDaoProc proc_permission = (GolfBkPermissionDaoProc)context.getProc("GolfBkPermissionDaoProc");
			DbTaoResult permissionView = proc_permission.execute(context, dataSet, userId, permissionColum); 

			permissionView.next();
			if(permissionView.getString("RESULT").equals("00")){
				permission = permissionView.getString("LIMT_YN");
				//debug("=`=`=`=`=`=`=`=`=GolfBkPreGrListActn === PMI_BOKG_APO_YN => " + permissionView.getString("PMI_BOKG_APO_YN"));
			}else{
				permission = "N";
			}
			
			paramMap.put("permission", permission); 
			paramMap.put("memGradeNM", str_memGradeTxt);
			
			request.setAttribute("evntResult", evntResult);
	        request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.			
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
