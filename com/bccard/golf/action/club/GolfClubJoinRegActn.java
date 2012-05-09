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

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.club.GolfClubJoinInsDaoProc;
import com.bccard.golf.dbtao.proc.club.GolfClubMasterDaoProc;

import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.golf.common.loginAction.SessionUtil;
/******************************************************************************
* Golf
* @author	(��)����Ŀ�´����̼�
* @version	1.0
******************************************************************************/
public class GolfClubJoinRegActn extends GolfActn{
	
	public static final String TITLE = "��ȣȸ ���� ó��";

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
			String greet_ctnt = parser.getParameter("greet_ctnt", "");

			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("CLUB_SEQ_NO", club_code);
			dataSet.setString("CDHD_ID", userId);
			dataSet.setString("CDHD_NM", userNm);
			dataSet.setString("GREET_CTNT", greet_ctnt);
			dataSet.setString("CLUB_CODE", club_code);
			
			// 04.���� ���̺�(Proc) ��ȸ
			GolfClubMasterDaoProc clubProc = (GolfClubMasterDaoProc)context.getProc("GolfClubMasterDaoProc");
			DbTaoResult clubMasterResult = (DbTaoResult) clubProc.execute(context, dataSet);
			
			// Ŭ��ȸ�� ����
			String clubMemChk = clubProc.getClubMemChk(context, dataSet);
			int addResult = 0 ;
			if (clubMemChk.equals("Y")) {
				request.setAttribute("returnUrl", errReUrl);
				request.setAttribute("resultMsg", "�̹� ���Ե� ��ȣȸ �Դϴ�.");		
			} else {	
			
				// ��ȣȸ ���Թ�� �ڵ�
				String club_jonn_mthd_clss = "";
				String cdhd_num_limt_yn = ""; // ȸ�����ѿ���
				int limt_cdhd_num = 0; // ����ȸ����
				int mem_cnt = 0; //����ȸ��
				if (clubMasterResult != null && clubMasterResult.isNext()) {
					clubMasterResult.first();
					clubMasterResult.next();
					if (clubMasterResult.getObject("RESULT").equals("00")) {
						club_jonn_mthd_clss = (String)clubMasterResult.getString("CLUB_JONN_MTHD_CLSS");
						cdhd_num_limt_yn = (String)clubMasterResult.getString("CDHD_NUM_LIMT_YN");
						if (cdhd_num_limt_yn.equals("Y")) {
							limt_cdhd_num = Integer.parseInt(clubMasterResult.getString("LIMT_CDHD_NUM"));
						}

						mem_cnt = Integer.parseInt(clubMasterResult.getString("MEM_CNT"));
					}
				}
				//
				boolean joinChk = true;
				if (cdhd_num_limt_yn.equals("Y")) {
					if (mem_cnt >= limt_cdhd_num) joinChk = false;
				}
				
				if (joinChk) {
					// ��ȣȸ ���� üũ
					if (club_jonn_mthd_clss.equals("R")) { //��ð���
						dataSet.setString("JONN_YN", "Y");				
					} else { //���ΰ���
						dataSet.setString("JONN_YN", "W");				
					}
				}
					
				GolfClubJoinInsDaoProc proc = (GolfClubJoinInsDaoProc)context.getProc("GolfClubJoinInsDaoProc");
				addResult = proc.execute(context, dataSet);			

		        if (addResult == 1) {
					request.setAttribute("returnUrl", reUrl);
					request.setAttribute("resultMsg", "");      	
		        } else {
					request.setAttribute("returnUrl", errReUrl);
					request.setAttribute("resultMsg", "��ȣȸ ������ ���������� ó�� ���� �ʾҽ��ϴ�.\\n\\n�ݺ������� ��ϵ��� ���� ��� �����ڿ� �����Ͻʽÿ�.");		        		
		        }
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
