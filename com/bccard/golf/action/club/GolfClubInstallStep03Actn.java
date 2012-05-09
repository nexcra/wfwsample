/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfBoardRegFormActn
*   �ۼ���    : (��)����Ŀ�´����̼� ���¿�
*   ����      : ����Խ��� �亯 ��
*   �������  : golf
*   �ۼ�����  : 2009-05-28
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.club;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

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
public class GolfClubInstallStep03Actn extends GolfActn{
	
	public static final String TITLE = "����Խ��� ��� ��";

	/***************************************************************************************
	* Golf ������ȭ��
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
			
			//	02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			paramMap.put("userId", userId);
			paramMap.put("userNm", userNm);

			String golf_club_ctgo = parser.getParameter("golf_club_ctgo", "");
			String club_intd_ctnt = GolfUtil.getUrl(parser.getParameter("club_intd_ctnt", ""));
			String club_nm = GolfUtil.getUrl(parser.getParameter("club_nm", ""));
			String club_sbjt_ctnt = GolfUtil.getUrl(parser.getParameter("club_sbjt_ctnt", ""));
			String club_opn_prps_ctnt = GolfUtil.getUrl(parser.getParameter("club_opn_prps_ctnt", ""));
			String cdhd_num_limt_yn = parser.getParameter("cdhd_num_limt_yn", "");
			String club_jonn_mthd_clss = parser.getParameter("club_jonn_mthd_clss", "");
			String limt_cdhd_num = parser.getParameter("limt_cdhd_num", "");
			String club_img = parser.getParameter("club_img", "");
			
			String cdhd_num_limt_yn_nm ="";
			if (cdhd_num_limt_yn.equals("Y")) cdhd_num_limt_yn_nm="ȸ������";
			if (cdhd_num_limt_yn.equals("N")) cdhd_num_limt_yn_nm="������";
			String club_jonn_mthd_clss_nm = "";
			if (club_jonn_mthd_clss.equals("A")) club_jonn_mthd_clss_nm="���ΰ���";
			if (club_jonn_mthd_clss.equals("R")) club_jonn_mthd_clss_nm="��ð���";
			
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("GOLF_CLUB_CTGO", golf_club_ctgo);
			
			// 04.���� ���̺�(Proc) ��ȸ
			GolfClubMasterDaoProc proc = (GolfClubMasterDaoProc)context.getProc("GolfClubMasterDaoProc");
			DbTaoResult clubCateSel = proc.getClubCateMemCnt(context, dataSet); //��ȣȸ ī�װ���
			
			// ������ ī�װ�����
			String golf_club_ctgo_nm = proc.getClubCateNm(context, dataSet);
			paramMap.put("club_nm", club_nm);								//��ȣȸ��
			paramMap.put("club_sbjt_ctnt", club_sbjt_ctnt);					//��ȣȸ����
			paramMap.put("golf_club_ctgo_nm", golf_club_ctgo_nm);		
			paramMap.put("club_intd_ctnt_chg", club_intd_ctnt);				//��ȣȸ�Ұ�
			paramMap.put("club_opn_prps_ctnt_chg", club_opn_prps_ctnt);		//��ȣȸ����
			paramMap.put("cdhd_num_limt_yn_nm", cdhd_num_limt_yn_nm);		
			paramMap.put("club_jonn_mthd_clss_nm", club_jonn_mthd_clss_nm);
			paramMap.put("golf_club_ctgo", golf_club_ctgo);
			paramMap.put("cdhd_num_limt_yn", cdhd_num_limt_yn);				//ȸ��������
			paramMap.put("club_jonn_mthd_clss", club_jonn_mthd_clss);		//���Լ������
			paramMap.put("limt_cdhd_num", limt_cdhd_num);					//ȸ����
			paramMap.put("club_img", club_img);								//�̹���

			request.setAttribute("clubCateSel", clubCateSel);	
	        request.setAttribute("paramMap", paramMap);		        
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}