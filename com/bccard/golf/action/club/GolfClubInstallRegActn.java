/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfClubInstallRegActn
*   �ۼ���    : (��)����Ŀ�´����̼� ���¿�
*   ����      : ��ȣȸ > ��ȣȸ �����
*   �������  : golf
*   �ۼ�����  : 2009-07-01
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.club;

import java.io.File;
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

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.proc.club.GolfClubInstallInsDaoProc;

import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.golf.common.loginAction.SessionUtil;
/******************************************************************************
* Golf
* @author	(��)����Ŀ�´����̼�
* @version	1.0
******************************************************************************/
public class GolfClubInstallRegActn extends GolfActn{
	
	public static final String TITLE = "��ȣȸ ����� ��� ó��";

	/***************************************************************************************
	* ���� �����ȭ��
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		String userNm = ""; 
		String memClss ="";
		String userId = "";
		String isLogin = ""; 
		String juminno = ""; 
		String memGrade = ""; 
		int intMemGrade = 0; 
		
		String mobile1 = ""; 
		String mobile2 = ""; 
		String mobile3 = ""; 
		
		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout");
		String reUrl = super.getActionParam(context, "reUrl");
		String errReUrl = super.getActionParam(context, "errReUrl");
		request.setAttribute("layout", layout);

		try {
			// 01.��������üũ
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
		 	
			 if(usrEntity != null) {
				userNm		= (String)usrEntity.getName(); 
				memClss		= (String)usrEntity.getMemberClss();
				userId		= (String)usrEntity.getAccount(); 
				juminno 	= (String)usrEntity.getSocid(); 
				memGrade 	= (String)usrEntity.getMemGrade(); 
				intMemGrade	= (int)usrEntity.getIntMemGrade(); 
				
				mobile1 = (String)usrEntity.getMobile1();
				mobile2 = (String)usrEntity.getMobile2();
				mobile3 = (String)usrEntity.getMobile3();
			}

			if(userId != null && !"".equals(userId)){
				isLogin = "1";
			} else {
				isLogin = "0"; 
				userNm	= "";
			}
			
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			paramMap.put("userNm", userNm);
			paramMap.put("userId", userId);
			paramMap.put("club_nm", parser.getParameter("club_nm", ""));
			
			String golf_club_ctgo = parser.getParameter("golf_club_ctgo", "");
			String club_nm = parser.getParameter("club_nm", "");
			String club_sbjt_ctnt = parser.getParameter("club_sbjt_ctnt", "");
			String club_img = parser.getParameter("club_img", "");
			String club_intd_ctnt = parser.getParameter("club_intd_ctnt", "");
			String club_opn_prps_ctnt = parser.getParameter("club_opn_prps_ctnt", "");
			String cdhd_num_limt_yn = parser.getParameter("cdhd_num_limt_yn", "");
			String limt_cdhd_num = parser.getParameter("limt_cdhd_num", "");
			String club_jonn_mthd_clss = parser.getParameter("club_jonn_mthd_clss", "");

			//========================== ���� ���ε� Start =============================================================//
			String realPath	= AppConfig.getAppProperty("UPLOAD_REAL_PATH"); 
			realPath = realPath.replaceAll("\\.\\.","");
			String tmpPath	= AppConfig.getAppProperty("UPLOAD_TMP_PATH"); 
			tmpPath = tmpPath.replaceAll("\\.\\.","");
			String subDir = "/club/club_img";

			if ( !GolfUtil.isNull(club_img)) {
                File tmp = new File(tmpPath,club_img);
                if ( tmp.exists() ) {

        			File createPath  =	new	File(realPath + subDir);
        			if (!createPath.exists()){
        				createPath.mkdirs();
        			}

                    String name = club_img.substring(0, club_img.lastIndexOf('.'));
                    String ext = club_img.substring(club_img.lastIndexOf('.'));

                    File listAttch = new File(createPath, club_img);
                    int i=0;
                    while ( listAttch.exists() ) {
                    	listAttch = null;
                    	listAttch = new File(createPath, name + String.valueOf(i) + ext );
                        i++;
                    }

                    if ( tmp.renameTo(listAttch) ) {
            			tmp.delete();
                    }
                }				
			}
			//========================== ���� ���ε� End =============================================================//

			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("GOLF_CLUB_CTGO", golf_club_ctgo);
			dataSet.setString("CLUB_NM", club_nm);
			dataSet.setString("OPN_PE_ID", userId);
			dataSet.setString("OPN_PE_NM", userNm);
			dataSet.setString("HP_DDD_NO", mobile1);
			dataSet.setString("HP_TEL_HNO", mobile2);
			dataSet.setString("HP_TEL_SNO", mobile3);		
			dataSet.setString("CLUB_SBJT_CTNT", club_sbjt_ctnt);
			dataSet.setString("CLUB_IMG", club_img);
			dataSet.setString("CLUB_INTD_CTNT", club_intd_ctnt);
			dataSet.setString("CLUB_OPN_PRPS_CTNT", club_opn_prps_ctnt);
			dataSet.setString("CDHD_NUM_LIMT_YN", cdhd_num_limt_yn);
			dataSet.setString("LIMT_CDHD_NUM", limt_cdhd_num);
			dataSet.setString("CLUB_JONN_MTHD_CLSS", club_jonn_mthd_clss);
			dataSet.setString("CLUB_OPN_AUTH_YN", "W");
			
			// 04.���� ���̺�(Proc) ��ȸ
			GolfClubInstallInsDaoProc proc = (GolfClubInstallInsDaoProc)context.getProc("GolfClubInstallInsDaoProc");
			int addResult = proc.execute(context, dataSet);			
			
	        if (addResult == 1) {
				request.setAttribute("returnUrl", reUrl);
				request.setAttribute("resultMsg", "��ȣȸ ��û�� ���������� ó�� �Ǿ����ϴ�.");      	
	        } else {
				//========================== ���� ���ε� Start =============================================================//
				if ( !GolfUtil.isNull(club_img)) {
                    File tmpAttch = new File(tmpPath, club_img);
	                if ( tmpAttch.exists() ) {
	                	tmpAttch.delete();
	                }				
	                
                    File realAttch = new File(realPath + subDir, club_img);
	                if ( realAttch.exists() ) {
	                	realAttch.delete();
	                }				
				}
				//========================== ���� ���ε� End =============================================================//
				request.setAttribute("returnUrl", errReUrl);
				request.setAttribute("resultMsg", "��ȣȸ ��û�� ���������� ó�� ���� �ʾҽ��ϴ�.\\n\\n�ݺ������� ��ϵ��� ���� ��� �����ڿ� �����Ͻʽÿ�.");		        		
	        }
			
			// 05. Return �� ����			
			paramMap.put("addResult", String.valueOf(addResult));			
	        request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.			
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
