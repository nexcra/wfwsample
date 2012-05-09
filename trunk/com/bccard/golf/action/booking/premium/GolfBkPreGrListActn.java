/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfBkPreGrListActn
*   �ۼ���    : (��)�̵������ ������
*   ����      : ��ŷ ������ ����Ʈ
*   �������  : Golf
*   �ۼ�����  : 2009-05-25
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.booking.premium;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoException;
import com.bccard.waf.tao.TaoResult;
import com.bccard.waf.tao.jolt.JoltInput;

import com.bccard.golf.common.ChkChgSocIdException;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfUserEtt;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.ResultException;
import com.bccard.golf.common.login.CardInfoEtt;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.booking.premium.*;
import com.bccard.golf.dbtao.proc.booking.*;
import com.bccard.golf.jolt.JtProcess;
import com.bccard.golf.user.dao.UcusrinfoDaoProc;
import com.bccard.golf.user.entity.UcusrinfoEntity;

/******************************************************************************
* Golf
* @author	(��)�̵������  
* @version	1.0 
******************************************************************************/
public class GolfBkPreGrListActn extends GolfActn{
	
	public static final String TITLE = "�����̾� ��ŷ ����Ʈ";
	private static final String BSNINPT = "BSNINPT";					// �����ӿ� ��ȸ����
	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		int intMemGrade = 0;
		int intCardGrade = 0;
		String memb_id = "";
		String permission = ""; 
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
			
			//debug("GolfBkPreGrListActn ==================== intMemGrade => " + intMemGrade);
			
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
									
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setInt("intMemGrade",		intMemGrade);
			
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

			debug("=`=`=`=`=`=`=`=`=GolfBkPreGrListActn === intMemGrade => " + intMemGrade);
			debug("=`=`=`=`=`=`=`=`=GolfBkPreGrListActn === permission => " + permission);

			// 04.���� ���̺�(Proc) ��ȸ
			if(permission.equals("Y")){
				GolfBkPreGrListDaoProc proc = (GolfBkPreGrListDaoProc)context.getProc("GolfBkPreGrListDaoProc");
				DbTaoResult grListResult = (DbTaoResult) proc.execute(context, request, dataSet);
				paramMap.put("resultSize", String.valueOf(grListResult.size()));
				request.setAttribute("GrListResult", grListResult);
			}
						
	        request.setAttribute("paramMap", paramMap);
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
