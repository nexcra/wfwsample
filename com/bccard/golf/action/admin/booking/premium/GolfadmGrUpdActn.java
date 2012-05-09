/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : admGrUpdActn
*   �ۼ���    : (��)�̵������ ������
*   ����      : ������ ��ŷ ������ ����ó��
*   �������  : golf
*   �ۼ�����  : 2009-05-20
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.admin.booking.premium;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.namo.MimeData;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.proc.admin.booking.premium.GolfadmGrUpdDaoProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0
******************************************************************************/
public class GolfadmGrUpdActn extends GolfActn{
	
	public static final String TITLE = "������ ��ŷ������ ���� ó��";

	/***************************************************************************************
	* ��ž����Ʈ ������ȭ�� 
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
			
			String sort 				= parser.getParameter("SORT", "").trim();		// ��ŷ�����屸���ڵ�
			long seq_no					= parser.getLongParameter("SEQ_NO", 0L);
			long page_no				= parser.getLongParameter("page_no", 0L);
			String gr_mn 				= parser.getParameter("GR_NM", "").trim();		// �������
			String rl_green_nm			= parser.getParameter("RL_GREEN_NM", "").trim();// �ǰ������
			String gr_url 				= parser.getParameter("GR_URL", "").trim();		// ������URL
			String gr_addr 				= parser.getParameter("GR_ADDR", "").trim();	// �������ּ�
			String course 				= parser.getParameter("COURSE", "").trim();		// �ڽ�/�Ը�
			String subequip 			= parser.getParameter("SUBEQUIP", "");			// �δ�ü�
			int del_limit 				= parser.getIntParameter("DEL_LIMIT", 0);		// ��ŷ��Ұ��ɱⰣ
			int per_limit 				= parser.getIntParameter("PER_LIMIT", 0);		// �ִ������ο�
			String corr_mgr_seq_no 		= admin_id;
			String pic1_DESC			= parser.getParameter("PIC1_DESC", "").trim();	// ������1���� ����
			String pic2_DESC			= parser.getParameter("PIC2_DESC", "").trim();	// ������2���� ����
			String pic3_DESC			= parser.getParameter("PIC3_DESC", "").trim();	// ������3���� ����
			String pic4_DESC			= parser.getParameter("PIC4_DESC", "").trim();	// ������4���� ����
			String pic5_DESC			= parser.getParameter("PIC5_DESC", "").trim();	// ������5���� ����
			String pic6_DESC			= parser.getParameter("PIC6_DESC", "").trim();	// ������6���� ����
			String gr_INFO 				= parser.getParameter("GR_INFO", "").trim();	// �����弳��
			String gr_NOTI 				= parser.getParameter("GR_NOTI", "").trim();	// ������ ��������
			String max_ACPT_PNUM 		= parser.getParameter("MAX_ACPT_PNUM", "").trim();	// �ִ������ο�
			
			String banner 				= parser.getParameter("BANNER", "").trim();		// ����̹���
			String map_nm 				= parser.getParameter("MAP_NM", "").trim();		// �൵�̹���
			String pic1 				= parser.getParameter("PIC1", "").trim();	// ������1����
			String pic2					= parser.getParameter("PIC2", "").trim();	// ������2����
			String pic3 				= parser.getParameter("PIC3", "").trim();	// ������3����
			String pic4 				= parser.getParameter("PIC4", "").trim();	// ������4����
			String pic5 				= parser.getParameter("PIC5", "").trim();	// ������5����
			String pic6 				= parser.getParameter("PIC6", "").trim();	// ������6����
			String main_BANNER_IMG		= parser.getParameter("MAIN_BANNER_IMG", "").trim();	// ���ι���̹���
			String main_EPS_YN 			= parser.getParameter("MAIN_EPS_YN", "").trim();	// ���γ��⿩��
			String co_nm				= parser.getParameter("CO_NM", "");					// �ָ���ŷ ��뿩��
			
			String old_BANNER 				= parser.getParameter("OLD_BANNER", "").trim();		// ����̹���
			String old_MAP_NM 				= parser.getParameter("OLD_MAP_NM", "").trim();		// �൵�̹���
			String old_PIC1 				= parser.getParameter("OLD_PIC1", "").trim();	// ������1����
			String old_PIC2					= parser.getParameter("OLD_PIC2", "").trim();	// ������2����
			String old_PIC3 				= parser.getParameter("OLD_PIC3", "").trim();	// ������3����
			String old_PIC4 				= parser.getParameter("OLD_PIC4", "").trim();	// ������4����
			String old_PIC5 				= parser.getParameter("OLD_PIC5", "").trim();	// ������5����
			String old_PIC6 				= parser.getParameter("OLD_PIC6", "").trim();	// ������6����
			String old_MAIN_BANNER_IMG		= parser.getParameter("OLD_MAIN_BANNER_IMG", "").trim();	// ���ι���̹���
			

			debug("pic6 -> " + pic6 + " / old_PIC6 -> " + old_PIC6); 
			
			////////////////////////////////////////////////////////////////////////////////////////////////
			// ���� ������ �̹����� ����					
			// imgPath : ���� ������ ���� �̹����� ����Ǵ� �����̸�
			// �Խ��� ���� ���ε�� �̹��� ���� imgPath ������ : /WEB-INF/config/config.xml ���� �߰��ϼ���.
			String imgPath = AppConfig.getAppProperty("BK_GREEN");
			imgPath = imgPath.replaceAll(".jsp",".txt").replaceAll(".asp",".txt").replaceAll(".php",".txt").replaceAll("\\.\\.","");
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

			String course_INFO 			= parser.getParameter("COURSE_INFO", "").trim();	// �ڽ��Ұ�
			String mimeData 			= parser.getParameter("mimeData", "").trim();	// �ڽ��Ұ�
			String ch_INFO 				= parser.getParameter("CH_INFO", "").trim();	// ������ ��ݾȳ�
			String mimeData2 			= parser.getParameter("mimeData2", "").trim();	// ������ ��ݾȳ�
			
			/////////////////////////////////////////////////////////////////////////////////////

			// ���� ������  MIME ���ڵ��ϱ� �ϱ�
			if(!mimeData.equals("")){
	            MimeData mime = new MimeData();
				mime.setSaveURL ( mapDir + imgPath );			// �̹�����ȸURL ����
				mime.setSavePath( contImgPath + imgPath );		// ���� ���� ��� ����			
	            mime.decode(mimeData);                     		// MIME ���ڵ�
	            mime.saveFile();                           		// ������ ���� �����ϱ�
	            course_INFO = mime.getBodyContent();        	// ���밡������
			}

			if(!mimeData2.equals("")){
	            MimeData mime2 = new MimeData();
	            mime2.setSaveURL ( mapDir + imgPath );				// �̹�����ȸURL ����
	            mime2.setSavePath( contImgPath + imgPath );			// ���� ���� ��� ����			
	            mime2.decode(mimeData2);                     		// MIME ���ڵ�
	            mime2.saveFile();                           		// ������ ���� �����ϱ�
	            ch_INFO = mime2.getBodyContent();        			// ���밡������
			}
            ////////////////////////////////////////////////////////////////////////////////////

			// ÷������ ���ε�///////////////////////////////////////////////////////////////
			String tmpPath  			= AppConfig.getAppProperty("UPLOAD_TMP_PATH");	
			String realPath  			= AppConfig.getAppProperty("CONT_IMG_PATH") + imgPath;	
			if(!old_BANNER.equals(banner)) setFiles(tmpPath, realPath, banner);
			if(!old_MAP_NM.equals(map_nm)) setFiles(tmpPath, realPath, map_nm);
			if(!old_PIC1.equals(pic1)) setFiles(tmpPath, realPath, pic1);
			if(!old_PIC2.equals(pic2)) setFiles(tmpPath, realPath, pic2);
			if(!old_PIC3.equals(pic3)) setFiles(tmpPath, realPath, pic3);
			if(!old_PIC4.equals(pic4)) setFiles(tmpPath, realPath, pic4);
			if(!old_PIC5.equals(pic5)) setFiles(tmpPath, realPath, pic5);
			if(!old_PIC6.equals(pic6)) setFiles(tmpPath, realPath, pic6);
			if(!old_MAIN_BANNER_IMG.equals(main_BANNER_IMG)) setFiles(tmpPath, realPath, main_BANNER_IMG);
            ////////////////////////////////////////////////////////////////////////////////////

			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setLong("SEQ_NO", seq_no);
			dataSet.setString("GR_NM", gr_mn);				// �������
			dataSet.setString("RL_GREEN_NM", rl_green_nm);	// �ǰ������
			dataSet.setString("GR_URL", gr_url);	// ������URL
			dataSet.setString("GR_INFO", gr_INFO);	// �����弳��
			dataSet.setString("GR_ADDR", gr_addr);	// �������ּ�
			dataSet.setString("COURSE", course);	// �ڽ�/�Ը�
			dataSet.setString("COURSE_INFO", course_INFO);	// �ڽ��Ұ�
			dataSet.setString("SUBEQUIP", subequip);	// �δ�ü�
			dataSet.setInt("DEL_LIMIT", del_limit);	// ��ŷ��Ұ��ɱⰣ
			dataSet.setInt("PER_LIMIT", per_limit);	// �ִ������ο�
			dataSet.setString("corr_mgr_seq_no", corr_mgr_seq_no);	// �����������Ϸù�ȣ
			dataSet.setString("PIC1_DESC", pic1_DESC);	// ������1���� ����
			dataSet.setString("PIC2_DESC", pic2_DESC);	// ������2���� ����
			dataSet.setString("PIC3_DESC", pic3_DESC);	// ������3���� ����
			dataSet.setString("PIC4_DESC", pic4_DESC);	// ������4���� ����
			dataSet.setString("PIC5_DESC", pic5_DESC);	// ������5���� ����
			dataSet.setString("PIC6_DESC", pic6_DESC);	// ������6���� ����
			dataSet.setString("GR_NOTI", gr_NOTI);	// ������ ��������
			dataSet.setString("CH_INFO", ch_INFO);	// ������ ��ݾȳ�
			dataSet.setString("MAX_ACPT_PNUM", max_ACPT_PNUM);	// �����ִ������ο�
			
			dataSet.setString("BANNER", banner);	// ����̹���
			dataSet.setString("MAP_NM", map_nm);	// �൵�̹���
			dataSet.setString("PIC1", pic1);	// ������1����
			dataSet.setString("PIC2", pic2);	// ������2����
			dataSet.setString("PIC3", pic3);	// ������3����
			dataSet.setString("PIC4", pic4);	// ������4����
			dataSet.setString("PIC5", pic5);	// ������5����
			dataSet.setString("PIC6", pic6);	// ������6����
			dataSet.setString("MAIN_BANNER_IMG", main_BANNER_IMG);	// ���ι���̹���
			dataSet.setString("MAIN_EPS_YN", main_EPS_YN);	// ���γ��⿩��
			dataSet.setString("CO_NM", co_nm);	// �ָ���ŷ ��뿩��
			
			// 04.���� ���̺�(Proc) ��ȸ
			GolfadmGrUpdDaoProc proc = (GolfadmGrUpdDaoProc)context.getProc("admGrUpdDaoProc");		
			int editResult = proc.execute(context, dataSet);		        

	        String returnUrlTrue = "";
	        String returnUrlFalse = "";
	        
			if (sort.equals("0001") || sort.equals("1000")){
	        	returnUrlTrue = "admGrList.do";
	        	returnUrlFalse = "admGrList.do";
	        }else{
	        	returnUrlTrue = "admParList.do";
	        	returnUrlFalse = "admParList.do";
	        }
			
			if (editResult == 1) {
				request.setAttribute("returnUrl", returnUrlTrue);
				request.setAttribute("resultMsg", "������ ���������� ó�� �Ǿ����ϴ�.");      	
	        } else {
				request.setAttribute("returnUrl", returnUrlFalse);
				request.setAttribute("resultMsg", "������ ���������� ó�� ���� �ʾҽ��ϴ�.\\n\\n�ݺ������� ��ϵ��� ���� ��� �����ڿ� �����Ͻʽÿ�.");		        		
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
	
	/**
	 * ��������
	 */
	private void setFiles(String tmpPath, String realPath, String imgName)throws BaseException {

		File real_mk = new File(realPath);
		if (!real_mk.exists()) {
			real_mk.mkdirs();
		}

		File tmp = new File(tmpPath, imgName);
		File real = new File(realPath, imgName);
		File tmp_s = new File(tmpPath, "S_"+imgName);
		File real_s = new File(realPath, "S_"+imgName);
			
		// �������� ������丮�� �̵�
		tmp.renameTo(real);
		tmp_s.renameTo(real_s);
	}	

}
