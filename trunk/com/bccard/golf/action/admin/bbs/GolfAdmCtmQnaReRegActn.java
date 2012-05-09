/***************************************************************************************************
*   �� �ҽ��� �߰�������� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmBoardReRegActn
*   �ۼ���    : (��)����Ŀ�´����̼� ���¿�
*   ����      : ������ ����Խ��� �亯 ��� ó��
*   �������  : golf
*   �ۼ�����  : 2009-05-27
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.admin.bbs;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import com.bccard.golf.common.GolfUtil;
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
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.SmsSendProc;
import com.bccard.golf.common.mail.EmailEntity;
import com.bccard.golf.common.mail.EmailSend;
import com.bccard.golf.common.namo.MimeData;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.proc.admin.bbs.GolfAdmBoardReInsDaoProc;

/******************************************************************************
* Golf
* @author	(��)����Ŀ�´����̼� 
* @version	1.0
******************************************************************************/
public class GolfAdmCtmQnaReRegActn extends GolfActn{
	
	public static final String TITLE = "������ ����Խ��� �亯 ��� ó��";

	/***************************************************************************************
	* ���� ������ȭ��
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		GolfAdminEtt userEtt = null;
		String admin_id = "";
		
		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout");
		String reUrl = super.getActionParam(context, "reUrl");
		String errReUrl = super.getActionParam(context, "errReUrl");
		request.setAttribute("layout", layout);

		try {
			// 01.��������üũ
			HttpSession session = request.getSession(true);
			userEtt =(GolfAdminEtt)session.getAttribute("SESSION_ADMIN");
			if(userEtt != null && !"".equals(userEtt.getMemId())){	
				admin_id	= (String)userEtt.getMemId();					
			}
			
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			
			String bbs = parser.getParameter("bbs", "");
			String upk_seq_no = parser.getParameter("p_idx", "");
			String field_cd = parser.getParameter("field_cd", "");
			String clss_cd = parser.getParameter("clss_cd", "");
			String sec_cd = parser.getParameter("sec_cd", "");
			String titl = parser.getParameter("titl", "");
			String ctnt = parser.getParameter("ctnt", "");
			String id = parser.getParameter("id", "");
			String hg_nm = parser.getParameter("hg_nm", "");
			String email_id = parser.getParameter("email_id", "");
			String chg_ddd_no = parser.getParameter("chg_ddd_no", "");
			String chg_tel_hno = parser.getParameter("chg_tel_hno", "");
			String chg_tel_sno = parser.getParameter("chg_tel_sno", "");
			String phone = chg_ddd_no + chg_tel_hno + chg_tel_sno;
			String eps_yn = parser.getParameter("eps_yn", "Y");
			String file_nm = parser.getParameter("file_nm", "");
			String pic_nm = parser.getParameter("pic_nm", "");
			String hd_yn = parser.getParameter("hd_yn", "Y");
			String del_yn = parser.getParameter("del_yn", "N");
			String reg_ip_addr = request.getRemoteAddr();
			String best_yn = parser.getParameter("best_yn", "N");
			String new_yn = parser.getParameter("new_yn", "N");
			String coop_yn = parser.getParameter("coop_yn", "N");
			String reg_aton = parser.getParameter("REG_ATON", "N");
			
			StringBuffer bufferSt = new StringBuffer();
			
			

			////////////////////////////////////////////////////////////////////////////////////////////////
			// ���� ������ �̹����� ����					
			// imgPath : ���� ������ ���� �̹����� ����Ǵ� �����̸�
			// �Խ��� ���� ���ε�� �̹��� ���� imgPath ������ : /WEB-INF/config/config.xml ���� �߰��ϼ���.
			String imgPath = "/bbs";
			String mapDir = AppConfig.getAppProperty("CONT_IMG_URL_MAPPING_DIR");
			mapDir = mapDir.replaceAll(".jsp",".txt").replaceAll(".asp",".txt").replaceAll(".php",".txt").replaceAll("\\.\\.","");
			
			if ( mapDir == null )  mapDir = "/";
            if ( mapDir.trim().length() == 0 )  mapDir = "/";  
           
            String contImgPath = AppConfig.getAppProperty("CONT_IMG_PATH");   
            contImgPath = contImgPath.replaceAll(".jsp",".txt").replaceAll(".asp",".txt").replaceAll(".php",".txt").replaceAll("\\.\\.","");
            if ( contImgPath == null ) contImgPath = "";      
           
            String contAtcPath = AppConfig.getAppProperty("CONT_ATC_PATH");
            contAtcPath = contAtcPath.replaceAll(".jsp",".txt").replaceAll(".asp",".txt").replaceAll(".php",".txt").replaceAll("\\.\\.","");
            if ( contAtcPath == null ) contAtcPath = "";
 			////////////////////////////////////////////////////////////////////////////////////////////////            	

			// ���� ������  MIME ���ڵ��ϱ� �ϱ�
			if(!ctnt.equals("")){
	            MimeData mime = new MimeData();
				mime.setSaveURL ( mapDir + imgPath );			// �̹�����ȸURL ����
				mime.setSavePath( contImgPath + imgPath );		// ���� ���� ��� ����			
	            mime.decode(ctnt);                     		// MIME ���ڵ�
	            mime.saveFile();                           		// ������ ���� �����ϱ�
	            ctnt = mime.getBodyContent();        	// ���밡������
			}
						
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("ADMIN_NO", admin_id);
			dataSet.setString("BBS", bbs);	
			dataSet.setString("UPK_SEQ_NO", upk_seq_no);	
			dataSet.setString("FIELD_CD", field_cd);
			dataSet.setString("CLSS_CD", clss_cd);
			dataSet.setString("SEC_CD", sec_cd);
			dataSet.setString("TITL", titl);
			dataSet.setString("CTNT", ctnt);
			dataSet.setString("ID", id);
			dataSet.setString("HG_NM", hg_nm);
			dataSet.setString("EMAIL_ID", email_id);
			dataSet.setString("CHG_DDD_NO", chg_ddd_no);
			dataSet.setString("CHG_TEL_HNO", chg_tel_hno);
			dataSet.setString("CHG_TEL_SNO", chg_tel_sno);
			dataSet.setString("EPS_YN", eps_yn);
			dataSet.setString("FILE_NM", file_nm);
			dataSet.setString("PIC_NM", pic_nm);
			dataSet.setString("HD_YN", hd_yn);
			dataSet.setString("DEL_YN", del_yn);
			dataSet.setString("REG_IP_ADDR", reg_ip_addr);
			dataSet.setString("BEST_YN", best_yn);
			dataSet.setString("NEW_YN", new_yn);
			dataSet.setString("COOP_YN", coop_yn);
			
			//String ctnt_list = GolfUtil.getAnsiCode(GolfUtil.removeTag(ctnt));

			// 04.���� ���̺�(Proc) ��ȸ
			GolfAdmBoardReInsDaoProc proc = (GolfAdmBoardReInsDaoProc)context.getProc("GolfAdmBoardReInsDaoProc");
			int addResult = proc.execute(context, dataSet);		

			
	        if (addResult == 2) {
				request.setAttribute("returnUrl", reUrl);
				request.setAttribute("resultMsg", "�亯 ����� ���������� ó�� �Ǿ����ϴ�.");    
				
		
				
//        		���Ϲ߼�
			//	email_id = "ciel1229@naver.com";
				
				if (!email_id.equals("")) {
					String emailAdmin = "\"���������\" <"+ AppConfig.getAppProperty("EMAILADMIN") +">";
					String emailTitle = hg_nm +"�� 1:1���� �亯��  �Ϸ�Ǿ����ϴ�.";
					String emailFileNm = "/email_tpl24.html";
					String imgPath2 = "<img src=\""+AppConfig.getAppProperty("REAL_HOST_DOMAIN");
					String hrefPath = "<a href=\""+AppConfig.getAppProperty("REAL_HOST_DOMAIN");
										
					EmailSend sender = new EmailSend();
					EmailEntity emailEtt = new EmailEntity("EUC_KR");
					
					emailEtt.setFrom(emailAdmin);
					emailEtt.setSubject(emailTitle);
					emailEtt.setHtmlContents(emailFileNm, imgPath2, hrefPath, hg_nm+"|"+titl+"|"+ctnt+"|"+reg_aton);
					//0�̸�,1����,2�亯,3���ǳ�¥
					emailEtt.setTo(email_id);
					sender.send(emailEtt);
				}


				
	        } else {
				request.setAttribute("returnUrl", errReUrl);
				request.setAttribute("resultMsg", "�亯�� ���������� ó�� ���� �ʾҽ��ϴ�.\\n\\n�ݺ������� ��ϵ��� ���� ��� �����ڿ� �����Ͻʽÿ�.");		        		
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
