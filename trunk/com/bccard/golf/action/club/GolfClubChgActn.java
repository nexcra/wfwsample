/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfClubJoinRegActn
*   �ۼ���    : (��)����Ŀ�´����̼� ���¿�
*   ����      : ��ȣȸ > ��ȣȸ ���� ó��
*   �������  : golf
*   �ۼ�����  : 2009-07-03
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
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.club.GolfClubMasterDaoProc;

import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.golf.common.loginAction.SessionUtil;
/******************************************************************************
* Golf
* @author	(��)����Ŀ�´����̼�
* @version	1.0
******************************************************************************/
public class GolfClubChgActn extends GolfActn{
	
	public static final String TITLE = "��ȣȸ ���������� ���� ó��";

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

			String club_code = parser.getParameter("club_code", "");
			String club_nm = parser.getParameter("club_nm", "");
			String club_sbjt_ctnt = parser.getParameter("club_sbjt_ctnt", "");
			String club_img = parser.getParameter("club_img", "");
			String club_intd_ctnt = parser.getParameter("club_intd_ctnt", "");
			String club_jonn_mthd_clss = parser.getParameter("club_jonn_mthd_clss", "");

			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("CLUB_SEQ_NO", club_code);
			dataSet.setString("CLUB_NM", club_nm);
			dataSet.setString("CLUB_SBJT_CTNT", club_sbjt_ctnt);
			dataSet.setString("CLUB_IMG", club_img);
			dataSet.setString("CLUB_INTD_CTNT", club_intd_ctnt);
			dataSet.setString("CLUB_JONN_MTHD_CLSS", club_jonn_mthd_clss);
			dataSet.setString("CLUB_CODE", club_code);
			dataSet.setString("OPN_PE_ID", userId);

			// 04.���� ���̺�(Proc) ��ȸ			
			GolfClubMasterDaoProc proc = (GolfClubMasterDaoProc)context.getProc("GolfClubMasterDaoProc");
			DbTaoResult clubMasterResult = (DbTaoResult) proc.execute(context, dataSet);			

			// ��� �α��� ����..
			String clubAdmId = "";
			if (clubMasterResult != null && clubMasterResult.isNext()) {
				clubMasterResult.first();
				clubMasterResult.next();
				if (clubMasterResult.getObject("RESULT").equals("00")) {
					clubAdmId = (String)clubMasterResult.getString("OPN_PE_ID");
				}
			}
			int editResult = 0;

			//========================== ���� ���ε� Start =============================================================//
			String realPath	= AppConfig.getAppProperty("UPLOAD_REAL_PATH"); 
			realPath = realPath.replaceAll("\\.\\.","");
			String tmpPath	= AppConfig.getAppProperty("UPLOAD_TMP_PATH"); 
			tmpPath = tmpPath.replaceAll("\\.\\.","");
			String subDir = "/club/club_img";
			
			// ��ڰ� �ƴ϶�� ������������ ���� �̵�
			if (!userId.equals(clubAdmId)) {
				subpage_key = "errReUrl";
				request.setAttribute("returnUrl", "golfClubMain.do");
				request.setAttribute("resultMsg", "");	
			} else {
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

				editResult = proc.updClubInfo(context, dataSet);	
			}
			
			
	        if (editResult == 1) {
				request.setAttribute("returnUrl", reUrl);
				request.setAttribute("resultMsg", "��ȣȸ ������ ���������� ó�� �Ǿ����ϴ�.");      	
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
				request.setAttribute("resultMsg", "��ȣȸ ������ ���������� ó�� ���� �ʾҽ��ϴ�.\\n\\n�ݺ������� ��ϵ��� ���� ��� �����ڿ� �����Ͻʽÿ�.");		        		
	        }
			
			// 05. Return �� ����
			paramMap.put("editResult", String.valueOf(editResult));			
	        request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.			
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
