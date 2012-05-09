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
import com.bccard.waf.tao.TaoDataSet;

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.event.GolfEvntTmSKInsProc;
import com.bccard.golf.dbtao.proc.event.tmMovie.GolfEvntTmMovieProc;

import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.golf.common.loginAction.SessionUtil;
/******************************************************************************
* Golf
* @author	JSEUN
* @version	1.0
******************************************************************************/
public class GolfEvntTmSKInsActn extends GolfActn{
	
	public static final String TITLE = "�̺�Ʈ > TM ��ȭ ���ű� �̺�Ʈ > ������ȣ ��� �˾�";

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
		DbTaoResult  result =  new DbTaoResult("");

		String userSocid = "";
		String userNm = "";
		String userEmail = "";
		String msg = "";

		try {
			// 01.��������üũ
			HttpSession session = request.getSession(true);
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);

			 if(usrEntity != null) {
				 userSocid 	= (String)usrEntity.getSocid();
				 userNm 	= (String)usrEntity.getName();
				 userEmail 	= (String)usrEntity.getEmail1();
			 }
			
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);

			String ddd_no 		= parser.getParameter("ddd_no", "");
			String hp_ddd_no 	= parser.getParameter("hp_ddd_no", "");
			String hp_tel_hno 	= parser.getParameter("hp_tel_hno", "");
			String hp_tel_sno 	= parser.getParameter("hp_tel_sno", "");
			String cdhd_id 		= parser.getParameter("cdhd_id", "");
			String hp 			= hp_ddd_no + "-" + hp_tel_hno + "-" + hp_tel_sno;
			
//			debug("ddd_no : " + ddd_no + " / hp_ddd_no : " + hp_ddd_no + " / hp_tel_hno : " + hp_tel_hno + " / hp_tel_sno : " + hp_tel_sno + " / cdhd_id : " + cdhd_id);

			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("ddd_no", ddd_no);
			dataSet.setString("hp_ddd_no", hp_ddd_no);
			dataSet.setString("hp_tel_hno"	, hp_tel_hno);
			dataSet.setString("hp_tel_sno"	, hp_tel_sno);
			dataSet.setString("cdhd_id"	, cdhd_id);
			
			// 04.���� ���̺�(Proc) ��ȸ
			GolfEvntTmSKInsProc proc = (GolfEvntTmSKInsProc)context.getProc("GolfEvntTmSKInsProc");

			// ������ȣ
			String bcd_hp_ddd_no = "";
			String bcd_hp_tel_hno = "";
			String bcd_hp_tel_sno = "";
			String bcd_hp = "";
			String cp_bcd = "";
			
			DbTaoResult bcdResult = proc.get_cp_bcd(context, request, dataSet);
			if(bcdResult!=null && bcdResult.isNext()){
				bcdResult.next();
				cp_bcd = bcdResult.getString("CO_NM");
				bcd_hp_ddd_no = bcdResult.getString("HP_DDD_NO");
				bcd_hp_tel_hno = bcdResult.getString("HP_TEL_HNO");
				bcd_hp_tel_sno = bcdResult.getString("HP_TEL_SNO");
				bcd_hp = bcd_hp_ddd_no + "-" + bcd_hp_tel_hno + "-" + bcd_hp_tel_sno;
			}
			dataSet.setString("cp_bcd"	, cp_bcd);	
//			debug("cp_bcd : " + cp_bcd);
			
			if(!GolfUtil.empty(cp_bcd) && !hp.equals(bcd_hp)){
				msg = "�̹� " + bcd_hp + " ������ ��û �ϼ̽��ϴ�.";
			}else{
			
				// ���
				String return_code = "";		// ���� �ڵ�
				String return_msg = "";			// ���� �޼���
				String return_coupon = "";		// ���� ��ȣ
				int aplIns = 0;					// ��û ���̺� ���� ����
				
				DbTaoResult coupResult = proc.sendCoup(context, request, dataSet);
				if(coupResult!=null && coupResult.isNext()){
					coupResult.next();
					return_code = coupResult.getString("return_code");
					return_msg = coupResult.getString("return_msg");
					return_coupon = coupResult.getString("return_coupon");
					aplIns = coupResult.getInt("aplIns");
				}
				
				debug("return_code : " + return_code + " / return_msg : " + return_msg + " / return_coupon : " + return_coupon + " / aplIns : " + aplIns);
						
				
				
				// ���� ������ ����
				if(return_code.equals("00")){
					// ����
					if(GolfUtil.empty(cp_bcd)){
						msg = "������ �Ϸ� �Ǿ����ϴ�.";
					}else{
						msg = "�������� �Ϸ� �Ǿ����ϴ�.";
					}
					
					// ����ǰ�� ������ش�.
					GolfEvntTmMovieProc proc_tmMovie = (GolfEvntTmMovieProc)context.getProc("GolfEvntTmMovieProc");
	
					result.addString("cupn", return_coupon);
					result.addString("pwin_grd", "1");
	
					dataSet.setString("tm_evt_no"	, "119");
					dataSet.setString("socid"		, userSocid);
					dataSet.setString("userNm"		, userNm);
					dataSet.setString("email"		, userEmail);							
					
					
					synchronized(this) {	// ���� ���� �߻��� ���� max �� �����°� ����
						int cupnTmMovie = (int) proc_tmMovie.getDplCheck(context, request, dataSet);
						if(cupnTmMovie==0){
							int doUpdate = proc_tmMovie.insertCupnNumberSK(context, request, dataSet, result);
						}
					}
					
				}else{
					msg = "���������� ó�� ���� �ʾҽ��ϴ�. ";	
					msg += "["+return_msg+"]";
				}
			}

			String script = "alert('"+msg+"');";	
			script += "top.window.close();";			

//			debug("script : " + script);
							
			
			// 05. Return �� ����
			request.setAttribute("script", script);
	        request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.			
			
		} catch(Throwable t) {
			debug(TITLE, t); 
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
