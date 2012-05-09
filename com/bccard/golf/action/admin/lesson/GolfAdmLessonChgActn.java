/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmLessonChgActn
*   �ۼ���    : (��)����Ŀ�´����̼� ���¿�
*   ����      : ������ �������α׷� ���� ó��
*   �������  : golf
*   �ۼ�����  : 2009-05-19
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.admin.lesson;

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
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.namo.MimeData;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.proc.admin.lesson.GolfAdmLessonUpdDaoProc;

import java.io.File;
import com.bccard.golf.common.AppConfig;

/******************************************************************************
* Topn
* @author	(��)����Ŀ�´����̼�
* @version	1.0
******************************************************************************/
public class GolfAdmLessonChgActn extends GolfActn{
	
	public static final String TITLE = "������ �������α׷� ���� ó��";

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

			String lsn_seq_no	= parser.getParameter("p_idx", "");// �����ϷĹ�ȣ
			String lsn_type_cd = parser.getParameter("lsn_type_cd", "");	// ����Ÿ���ڵ�
			String lsn_nm = parser.getParameter("lsn_nm", "");	// ������
			String evnt_yn = parser.getParameter("evnt_yn", "");	// �̺�Ʈ����
			String img_nm = parser.getParameter("img_nm", "");	// �����̹���
			String lsn_prd_clss = parser.getParameter("lsn_prd_clss", "");	// �����ⰣŸ��
			String lsn_start_dt = parser.getParameter("lsn_start_dt", "");	// ����������
			String lsn_end_dt = parser.getParameter("lsn_end_dt", "");	// ����������
			String aplc_end_dt = parser.getParameter("aplc_end_dt", "");	// ��û������
			String lsn_prd_info = parser.getParameter("lsn_prd_info", "");	// �����Ⱓ����
			int lsn_sttl_cst = parser.getIntParameter("lsn_sttl_cst", 0);	// ����������
			int lsn_dc_cst = parser.getIntParameter("lsn_dc_cst", 0);	// �������κ��
			String lsn_pl = parser.getParameter("lsn_pl", "");	// ���
			String map_nm = parser.getParameter("map_nm", "");	// �൵�̹���
			String chg_ddd_no = parser.getParameter("chg_ddd_no", "");	// ��ȭddd��ȣ
			String chg_tel_hno = parser.getParameter("chg_tel_hno", "");	// ��ȭ����ȣ
			String chg_tel_sno = parser.getParameter("chg_tel_sno", "");	// ��ȭ�Ϸù�ȣ			
			String aplc_mthd = parser.getParameter("aplc_mthd", "");	// ��û���
			String aplc_lete_num = parser.getParameter("aplc_lete_num", "0");	// ��û�����ο�
			String lsn_intd = parser.getParameter("lsn_intd", "");	// ���ټҰ�
			String coop_cp_cd = parser.getParameter("coop_cp_cd", "");	// ���޾�ü�ڵ�
			String coop_rmrk = parser.getParameter("coop_rmrk", "");	// ���޾ȳ�����
			String lsn_ctnt = parser.getParameter("lsn_ctnt", "");	// ��������
			String orgImg_nm = parser.getParameter("orgImg_nm", "");	// ���� �����̹���
			String orgMap_nm = parser.getParameter("orgMap_nm", "");	// ���� �൵�̹���
			int lsn_dc_rt = parser.getIntParameter("lsn_dc_rt", 0);	// �������η�
			String main_banner_img = parser.getParameter("main_banner_img", "");	// ���ι���̹���
			String main_banner_url = parser.getParameter("main_banner_url", "");	// ���ι��URL
			String main_eps_yn = parser.getParameter("main_eps_yn", "N");	// ���γ��⿩��

			////////////////////////////////////////////////////////////////////////////////////////////////
			// ���� ������ �̹����� ����					
			// imgPath : ���� ������ ���� �̹����� ����Ǵ� �����̸�
			// �Խ��� ���� ���ε�� �̹��� ���� imgPath ������ : /WEB-INF/config/config.xml ���� �߰��ϼ���.
			String imgPath = "/lesson";
			String mapDir = AppConfig.getAppProperty("CONT_IMG_URL_MAPPING_DIR");
			mapDir = mapDir.replaceAll("\\.\\.","");
			
			if ( mapDir == null )  mapDir = "/";
            if ( mapDir.trim().length() == 0 )  mapDir = "/";  
           
            String contImgPath = AppConfig.getAppProperty("CONT_IMG_PATH"); 
            contImgPath = contImgPath.replaceAll("\\.\\.","");
            if ( contImgPath == null ) contImgPath = "";      
           
            String contAtcPath = AppConfig.getAppProperty("CONT_ATC_PATH");
            contAtcPath = contAtcPath.replaceAll("\\.\\.","");
            if ( contAtcPath == null ) contAtcPath = "";
            
			// ���� ������  MIME ���ڵ��ϱ� �ϱ�
			if(!lsn_ctnt.equals("")){
	            MimeData mime = new MimeData();
				mime.setSaveURL ( mapDir + imgPath );			// �̹�����ȸURL ����
				mime.setSavePath( contImgPath + imgPath );		// ���� ���� ��� ����			
	            mime.decode(lsn_ctnt);                     		// MIME ���ڵ�
	            mime.saveFile();                           		// ������ ���� �����ϱ�
	            lsn_ctnt = mime.getBodyContent();        	// ���밡������
			}
            ////////////////////////////////////////////////////////////////////////////////////
			
			lsn_start_dt = lsn_start_dt.length() == 10 ? DateUtil.format(lsn_start_dt, "yyyy-MM-dd", "yyyyMMdd"): "";
			lsn_end_dt = lsn_end_dt.length() == 10 ? DateUtil.format(lsn_end_dt, "yyyy-MM-dd", "yyyyMMdd"): "";
			aplc_end_dt = aplc_end_dt.length() == 10 ? DateUtil.format(aplc_end_dt, "yyyy-MM-dd", "yyyyMMdd"): "";
			
			//========================== ���� ���ε� Start =============================================================//
			String realPath	= AppConfig.getAppProperty("UPLOAD_REAL_PATH"); 	
			String tmpPath	= AppConfig.getAppProperty("UPLOAD_TMP_PATH"); 
			String subDir = "/lesson";

			if ( !GolfUtil.isNull(img_nm)) {
                File tmp = new File(tmpPath,img_nm);
                if ( tmp.exists() ) {

        			File createPath  =	new	File(realPath + subDir);
        			if (!createPath.exists()){
        				createPath.mkdirs();
        			}

                    String name = img_nm.substring(0, img_nm.lastIndexOf('.'));
                    String ext = img_nm.substring(img_nm.lastIndexOf('.'));

                    File listAttch = new File(createPath, img_nm);
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
			
			if ( !GolfUtil.isNull(map_nm)) {
                File tmp = new File(tmpPath,map_nm);
                if ( tmp.exists() ) {

        			File createPath  =	new	File(realPath + subDir);
        			if (!createPath.exists()){
        				createPath.mkdirs();
        			}

                    String name = map_nm.substring(0, map_nm.lastIndexOf('.'));
                    String ext = map_nm.substring(map_nm.lastIndexOf('.'));

                    File listAttch = new File(createPath, map_nm);
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
			
			if ( !GolfUtil.isNull(main_banner_img)) {
                File tmp = new File(tmpPath,main_banner_img);
                if ( tmp.exists() ) {

        			File createPath  =	new	File(realPath + subDir);
        			if (!createPath.exists()){
        				createPath.mkdirs();
        			}

                    String name = main_banner_img.substring(0, main_banner_img.lastIndexOf('.'));
                    String ext = main_banner_img.substring(main_banner_img.lastIndexOf('.'));

                    File listAttch = new File(createPath, main_banner_img);
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
			dataSet.setString("ADMIN_NO", admin_id);
			dataSet.setString("LSN_SEQ_NO", lsn_seq_no);
			dataSet.setString("LSN_TYPE_CD", lsn_type_cd);
			dataSet.setString("LSN_NM", lsn_nm);
			dataSet.setString("EVNT_YN", evnt_yn);
			dataSet.setString("IMG_NM", img_nm);
			dataSet.setString("LSN_PRD_CLSS", lsn_prd_clss);
			dataSet.setString("LSN_START_DT", lsn_start_dt);
			dataSet.setString("LSN_END_DT", lsn_end_dt);
			dataSet.setString("APLC_END_DT", aplc_end_dt);
			dataSet.setString("LSN_PRD_INFO", lsn_prd_info);
			dataSet.setInt("LSN_STTL_CST", lsn_sttl_cst);
			dataSet.setInt("LSN_DC_CST", lsn_dc_cst);
			dataSet.setString("LSN_PL", lsn_pl);
			dataSet.setString("MAP_NM", map_nm);
			dataSet.setString("CHG_DDD_NO", chg_ddd_no);
			dataSet.setString("CHG_TEL_HNO", chg_tel_hno);
			dataSet.setString("CHG_TEL_SNO", chg_tel_sno);			
			dataSet.setString("APLC_MTHD", aplc_mthd);
			dataSet.setString("APLC_LETE_NUM", aplc_lete_num);
			dataSet.setString("LSN_INTD", lsn_intd);
			dataSet.setString("COOP_CP_CD", coop_cp_cd);
			dataSet.setString("COOP_RMRK", coop_rmrk);
			dataSet.setString("LSN_CTNT", lsn_ctnt);
			dataSet.setInt("LSN_DC_RT", lsn_dc_rt);
			dataSet.setString("MAIN_BANNER_IMG", main_banner_img);
			dataSet.setString("MAIN_BANNER_URL", main_banner_url);
			dataSet.setString("MAIN_EPS_YN", main_eps_yn);
			
			// 04.���� ���̺�(Proc) ��ȸ
			GolfAdmLessonUpdDaoProc proc = (GolfAdmLessonUpdDaoProc)context.getProc("GolfAdmLessonUpdDaoProc");
			
			// ���� ���α׷� ��� ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
			int editResult = proc.execute(context, dataSet);			
			
	        if (editResult == 1) {
				//========================== ���� ���ε� Start =============================================================//
				if ( !GolfUtil.isNull(img_nm) && !GolfUtil.isNull(orgImg_nm)) {
                    File realAttch = new File(realPath + subDir, orgImg_nm);
	                if ( realAttch.exists() ) {
	                	realAttch.delete();
	                }				
				}
				if ( !GolfUtil.isNull(map_nm) && !GolfUtil.isNull(orgMap_nm)) {
                    File realAttch = new File(realPath + subDir, orgMap_nm);
	                if ( realAttch.exists() ) {
	                	realAttch.delete();
	                }				
				}
				//========================== ���� ���ε� End =============================================================//
				request.setAttribute("returnUrl", "admLessonList.do");
				request.setAttribute("resultMsg", "���� ���α׷� ������ ���������� ó�� �Ǿ����ϴ�.");      	
	        } else {
				//========================== ���� ���ε� Start =============================================================//
				if ( !GolfUtil.isNull(img_nm)) {
                    File tmpAttch = new File(tmpPath, img_nm);
	                if ( tmpAttch.exists() ) {
	                	tmpAttch.delete();
	                }				
	                
                    File realAttch = new File(realPath + subDir, img_nm);
	                if ( realAttch.exists() ) {
	                	realAttch.delete();
	                }				
				}
				if ( !GolfUtil.isNull(map_nm)) {
                    File tmpAttch = new File(tmpPath, map_nm);
	                if ( tmpAttch.exists() ) {
	                	tmpAttch.delete();
	                }				
	                
                    File realAttch = new File(realPath + subDir, map_nm);
	                if ( realAttch.exists() ) {
	                	realAttch.delete();
	                }				
				}
				
				if ( !GolfUtil.isNull(main_banner_img)) {
                    File tmpAttch = new File(tmpPath, main_banner_img);
	                if ( tmpAttch.exists() ) {
	                	tmpAttch.delete();
	                }				
	                
                    File realAttch = new File(realPath + subDir, main_banner_img);
	                if ( realAttch.exists() ) {
	                	realAttch.delete();
	                }				
				}
				//========================== ���� ���ε� End =============================================================//
				request.setAttribute("returnUrl", "admLessonChgForm.do");
				request.setAttribute("resultMsg", "���� ���α׷� ������ ���������� ó�� ���� �ʾҽ��ϴ�.\\n\\n�ݺ������� �������� ���� ��� �����ڿ� �����Ͻʽÿ�.");		        		
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
