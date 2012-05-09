/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfBkPreTimeListActn
*   �ۼ���    : (��)�̵������ ������
*   ����      : ��ŷ ƼŸ�� ����Ʈ
*   �������  : Golf
*   �ۼ�����  : 2009-05-26
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.booking.premium;

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

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfUserEtt;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.login.CardInfoEtt;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.booking.GolfBkPermissionDaoProc;
import com.bccard.golf.dbtao.proc.booking.GolfBkPenaltyDaoProc;
import com.bccard.golf.dbtao.proc.booking.premium.*;
import com.bccard.golf.user.entity.UcusrinfoEntity;

/******************************************************************************
* Golf
* @author	(��)�̵������ 
* @version	1.0
******************************************************************************/
public class GolfBkPreTimeListActn extends GolfActn{
	
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
		int intMemGrade = 0;
		int intCardGrade = 0;
		String memb_id = "";
		String permission = "";
		String penalty = "";
		String penalty_start = "";
		String penalty_end = "";
		
		String cardGolfYn = "";			// ������ŷ������������
		String cardGolfStartDay = "";	// ������ŷ���񽺽�������
		String golfStartDate = "";	// ���� ����-1��

        int sYear = 0;
        int sMonth = 0;
        int sDay = 0; 
		
		
		
		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);
		
		try {
			
			// 01.��������üũ 
			UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request);
			if(userEtt != null){
				memb_id = userEtt.getAccount();				// ȸ�� ���̵�
				intMemGrade = userEtt.getIntMemGrade();
				intCardGrade = userEtt.getIntCardGrade();		// ī�� ���
			}
			
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			
			paramMap.put("title", TITLE);			 

			int defaultDate			= parser.getIntParameter("defaultDate", 5);
			paramMap.put("defaultDate", String.valueOf(defaultDate));
						
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setInt("defaultDate", defaultDate);
			dataSet.setInt("intMemGrade",	intMemGrade);
			
			// 04-01. ��ŷ ���� ��ȸ
			GolfBkPenaltyDaoProc proc_penalty = (GolfBkPenaltyDaoProc)context.getProc("GolfBkPenaltyDaoProc");
			DbTaoResult penaltyView = proc_penalty.execute(context, dataSet, request);
			
			penaltyView.next();
			if(penaltyView.getString("RESULT").equals("00")){
				penalty = "Y";
				penalty_start = penaltyView.getString("BK_LIMIT_ST");
				penalty_end = penaltyView.getString("BK_LIMIT_ED");
			}else{
				penalty = "N";
			}
			paramMap.put("penalty", penalty);
			paramMap.put("penalty_start", penalty_start);
			paramMap.put("penalty_end", penalty_end);
//			debug("=`=`=`=`=`=`=`=`=GolfBkPreGrListActn ===  penalty => " + penalty);

			// 04. ���ٱ��� ��ȸ	
			String permissionColum = "PMI_BOKG_APO_YN";
			GolfBkPermissionDaoProc proc_permission = (GolfBkPermissionDaoProc)context.getProc("GolfBkPermissionDaoProc");
			DbTaoResult permissionView = proc_permission.execute(context, dataSet, memb_id, permissionColum);

			permissionView.next();
			if(permissionView.getString("RESULT").equals("00")){
				permission = permissionView.getString("LIMT_YN");
				//debug("=`=`=`=`=`=`=`=`=GolfBkPreGrListActn === PMI_BOKG_APO_YN => " + permissionView.getString("PMI_BOKG_APO_YN"));
			}else{
				permission = "N";
			}
			

			// �������ī�� ȸ�� ��ȸ
			if(intCardGrade>0)
			{
				GolfUserEtt mbr = SessionUtil.getTopnUserInfo(request);
				if (mbr != null) 
				{	
					List cardList = mbr.getCardInfoList();
					CardInfoEtt cardInfo = new CardInfoEtt();
					
					if( cardList.size() > 0 )
					{
						//���� �������п� 1�� �ְ� ���� �������ڿ��� �ϳ��� ������ ������� ��ŷ �� �� �ֵ��� ���ش�.
						cardGolfYn = cardInfo.getGolfYn();
						cardGolfStartDay = cardInfo.getGolfStartDay();
						//cardGolfYn = "1";
						//cardGolfStartDay = "20080911";
						
						debug("=`=`=`=`=`=`=`=`=`=`=`=`=`=`=`= cardGolfYn : " + cardGolfYn);
						debug("=`=`=`=`=`=`=`=`=`=`=`=`=`=`=`= cardGolfStartDay : " + cardGolfStartDay);

						if(!GolfUtil.empty(cardGolfYn) && !GolfUtil.empty(cardGolfStartDay)){

					        GregorianCalendar nowDate = new GregorianCalendar ( );
					        nowDate.add(Calendar.YEAR, -1);

					        sYear = nowDate.get ( nowDate.YEAR );
					        sMonth = nowDate.get ( nowDate.MONTH ) + 1;
					        sDay = nowDate.get ( nowDate.DAY_OF_MONTH ); 
					        
					        golfStartDate = sYear + "";
					        if (sMonth<10) golfStartDate = golfStartDate + "0";
					        golfStartDate = golfStartDate + sMonth ;
					        if (sDay<10) golfStartDate = golfStartDate + "0";
					        golfStartDate = golfStartDate + sDay;
					        
					        int intGolfStartDate = Integer.parseInt(golfStartDate);
							int intCardGolfStartDay = Integer.parseInt(cardGolfStartDay);
							debug("=`=`=`=`=`=`=`=`=`=`=`=`=`=`=`= intGolfStartDate : " + intGolfStartDate);
							debug("=`=`=`=`=`=`=`=`=`=`=`=`=`=`=`= intCardGolfStartDay : " + intCardGolfStartDay);
							
							if(cardGolfYn.equals("1") && intCardGolfStartDay>intGolfStartDate){ 								
								permission = "Y";
							}
						}
					}
				}
			}

			paramMap.put("permission", permission);
			

			// 04.���� ���̺�(Proc) ��ȸ
			if(permission.equals("Y")){				
		        
		        // 05. �޷±׸��� ���� ����
		        GregorianCalendar today = new GregorianCalendar ( );
		        today.add(Calendar.DATE, defaultDate);
		        String [] dayOfWeek = {"","��","��","ȭ","��","��","��","��"};
		        
		        int nYear = 0;
		        int nMonth = 0;
		        int nDay = 0; 
		        int nYoil = 0; 
		        int nHour = today.get ( today.HOUR_OF_DAY);
		        //debug("==============nHour==============" + nHour);
		        String nDate = "";
		        String divDate = "";
		        String clickDate = "";
		        String isWeekend = "";
	
		        
		        for (int d=0; d<14; d++){
		        	
			        nYear = today.get ( today.YEAR );
			        nMonth = today.get ( today.MONTH ) + 1;
			        nDay = today.get ( today.DAY_OF_MONTH ); 
			        nYoil = today.get ( today.DAY_OF_WEEK );
	
			        nDate = "";
			        divDate = "";
			        clickDate = "";
			        isWeekend = "";
			        
			        // ����Ʈ ��� ��¥
			        if (nMonth<10) nDate = "0";
			        nDate = nDate + nMonth + ".";
			        if (nDay<10) nDate = nDate + "0";
			        nDate = nDate + nDay + "<br>("+dayOfWeek[nYoil]+")";
			        
			        // ���̾� ��¿� ��¥
			        if (nMonth<10) divDate = "0";
			        divDate = divDate + nMonth + "/";
			        if (nDay<10) divDate = divDate + "0";
			        divDate = divDate + nDay + " ("+dayOfWeek[nYoil]+")";
			        
			        // �ָ�����
			        if ((nYoil==1) || (nYoil==7)){ isWeekend = "Y"; } else { isWeekend = "N"; }
	
					paramMap.put("nYear"+d, String.valueOf(nYear));
					paramMap.put("nMonth"+d, String.valueOf(nMonth));
					paramMap.put("nDay"+d, String.valueOf(nDay));
					paramMap.put("nDate"+d, String.valueOf(nDate));
					paramMap.put("divDate"+d, String.valueOf(divDate));
					paramMap.put("isWeekend"+d, String.valueOf(isWeekend));
			        today.add(Calendar.DATE, 1);
		        	
		        }	                

		        dataSet.setInt("nHour",	nHour);
				GolfBkPreTimeListDaoProc proc = (GolfBkPreTimeListDaoProc)context.getProc("GolfBkPreTimeListDaoProc");
				DbTaoResult listResult = (DbTaoResult) proc.execute(context, request, dataSet);
	
				paramMap.put("resultSize", String.valueOf(listResult.size()));
				request.setAttribute("ListResult", listResult);
			}

						

	        request.setAttribute("paramMap", paramMap);
	        
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
