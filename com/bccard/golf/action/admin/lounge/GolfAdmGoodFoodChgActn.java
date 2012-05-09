/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmGoodFoodChgActn
*   �ۼ���    : (��)����Ŀ�´����̼� ����ȯ
*   ����      : ������ ���� ���� ó��
*   �������  : golf
*   �ۼ�����  : 2009-05-29
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.admin.lounge;

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
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.namo.MimeData;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.proc.admin.lounge.GolfAdmGoodFoodUpdDaoProc;

/******************************************************************************
* Topn
* @author	(��)����Ŀ�´����̼�
* @version	1.0
******************************************************************************/
public class GolfAdmGoodFoodChgActn extends GolfActn{
	
	public static final String TITLE = "������ ���� ���� ó��";

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

			long fd_seq_no = parser.getLongParameter("p_idx", 0L);// �Ϸù�ȣ
			
			String gf_seq_no = parser.getParameter("gf_seq_no", "");	// �������ڵ�
			String fd_nm = parser.getParameter("fd_nm", "");	// ������
			String new_yn = parser.getParameter("new_yn", "");	// ���ۿ���
			String best_yn = parser.getParameter("best_yn", "");	// ����Ʈ����
			String fd_area_cd = parser.getParameter("fd_area_cd", "");	// �����ڵ�
			String region_green_nm = parser.getParameter("region_green_nm", "");	// �����������Է�
			String fd1_lev_cd = parser.getParameter("fd1_lev_cd", "");	// ����1���з��ڵ�
			String fd2_lev_cd = parser.getParameter("fd2_lev_cd", "");	// ����2���з��ڵ�
			String fd3_lev_cd = parser.getParameter("fd3_lev_cd", "");	// ����3���з��ڵ�
			String img_nm = parser.getParameter("img_nm", "");	// �����̹���
			String zipcode1 = parser.getParameter("zipcode1", "");	// �����ȣ1
			String zipcode2 = parser.getParameter("zipcode2", "");	// �����ȣ2
			String zipaddr = parser.getParameter("zipaddr", "");	// �ּ�
			String detailaddr = parser.getParameter("detailaddr", "");	// �����ּ�
			String addr_clss = parser.getParameter("addr_clss"); //�ּұ���(��:1, ��:2)
			String chg_ddd_no = parser.getParameter("chg_ddd_no", "");	// ��ȭDDD��ȣ
			String chg_tel_hno = parser.getParameter("chg_tel_hno", "");	// ��ȭ����ȣ
			String chg_tel_sno = parser.getParameter("chg_tel_sno", "");	// ��ȭ�Ϸù�ȣ
			String road_srch = parser.getParameter("road_srch", "");	// ã�ƿ��ô±�
			String map_nm = parser.getParameter("map_nm", "");	// �൵�̹���
			String parking_yn = parser.getParameter("parking_yn", "");	// �������ɿ���
			String start_hh = parser.getParameter("start_hh", "");	// �����ð� ��1
			String start_mi = parser.getParameter("start_mi", "");	// �����ð� ��1
			String end_hh = parser.getParameter("end_hh", "");	// �����ð� ��2
			String end_mi = parser.getParameter("end_mi", "");	// �����ð� ��2
			String sls_end_day = parser.getParameter("sls_end_day", "");	// �޹���
			String url = parser.getParameter("url", "");	// Ȩ������
			String fd_menu = parser.getParameter("fd_menu", "");	// �ֿ�޴�
			String ctnt = parser.getParameter("ctnt", "");	// ����
			String main_banner_img = parser.getParameter("main_banner_img", "");	// ���ι���̹���
			String main_banner_url = parser.getParameter("main_banner_url", "");	// ���ι��URL
			String main_eps_yn = parser.getParameter("main_eps_yn", "N");	// ���γ��⿩��
			String main_rprs_img = parser.getParameter("main_rprs_img", "");	// ���δ�ǥ�̹���
			String main_rprs_img_url = parser.getParameter("main_rprs_img_url", "");	// ���δ�ǥ�̹���URL
			String main_rprs_img_eps_yn = parser.getParameter("main_rprs_img_eps_yn", "N");	// ���δ�ǥ�̹������⿩��
			
			String orgImg_nm = parser.getParameter("orgImg_nm", "");	// ���� �����̹���
			String orgMap_nm = parser.getParameter("orgMap_nm", "");	// ���� �൵�̹���
			String orgMain_banner_img = parser.getParameter("orgMain_banner_img", "");	// ���� ���ι���̹���
			String orgMain_rprs_img = parser.getParameter("orgMain_rprs_img", "");	// ���� ���δ�ǥ�̹���			
			
			String zipcode = zipcode1 + zipcode2;
			String sls_strt_time = start_hh + start_mi;
			String sls_end_time = end_hh + end_mi;
			
			//========================== ���� ���ε� Start =============================================================//
			String realPath	= AppConfig.getAppProperty("UPLOAD_REAL_PATH"); 
			realPath = realPath.replaceAll("\\.\\.","");
			String tmpPath	= AppConfig.getAppProperty("UPLOAD_TMP_PATH"); 
			tmpPath = tmpPath.replaceAll("\\.\\.","");
			String subDir = "/lounge";

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
			
			if ( !GolfUtil.isNull(main_rprs_img)) {
                File tmp = new File(tmpPath,main_rprs_img);
                if ( tmp.exists() ) {

        			File createPath  =	new	File(realPath + subDir);
        			if (!createPath.exists()){
        				createPath.mkdirs();
        			}

                    String name = main_rprs_img.substring(0, main_rprs_img.lastIndexOf('.'));
                    String ext = main_rprs_img.substring(main_rprs_img.lastIndexOf('.'));

                    File listAttch = new File(createPath, main_rprs_img);
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
			dataSet.setLong("FD_SEQ_NO", fd_seq_no);

			dataSet.setString("GF_SEQ_NO", gf_seq_no);
			dataSet.setString("FD_NM", fd_nm);
			dataSet.setString("NEW_YN", new_yn);
			dataSet.setString("BEST_YN", best_yn);
			dataSet.setString("FD_AREA_CD", fd_area_cd);
			dataSet.setString("REGION_GREEN_NM", region_green_nm);
			dataSet.setString("FD1_LEV_CD", fd1_lev_cd);
			dataSet.setString("FD2_LEV_CD", fd2_lev_cd);
			dataSet.setString("FD3_LEV_CD", fd3_lev_cd);
			dataSet.setString("IMG_NM", img_nm);
			dataSet.setString("ZIPCODE", zipcode);
			dataSet.setString("ZIPADDR", zipaddr);
			dataSet.setString("DETAILADDR", detailaddr);
			dataSet.setString("ADDR_CLSS", addr_clss);			
			dataSet.setString("CHG_DDD_NO", chg_ddd_no);
			dataSet.setString("CHG_TEL_HNO", chg_tel_hno);
			dataSet.setString("CHG_TEL_SNO", chg_tel_sno);
			dataSet.setString("ROAD_SRCH", road_srch);
			dataSet.setString("MAP_NM", map_nm);
			dataSet.setString("PARKING_YN", parking_yn);
			dataSet.setString("SLS_STRT_TIME", sls_strt_time);
			dataSet.setString("SLS_END_TIME", sls_end_time);
			dataSet.setString("SLS_END_DAY", sls_end_day);
			dataSet.setString("URL", url);
			dataSet.setString("FD_MENU", fd_menu);
			dataSet.setString("CTNT", ctnt);
			dataSet.setString("MAIN_BANNER_IMG", main_banner_img);
			dataSet.setString("MAIN_BANNER_URL", main_banner_url);
			dataSet.setString("MAIN_EPS_YN", main_eps_yn);
			dataSet.setString("MAIN_RPRS_IMG", main_rprs_img);
			dataSet.setString("MAIN_RPRS_IMG_URL", main_rprs_img_url);
			dataSet.setString("MAIN_RPRS_IMG_EPS_YN", main_rprs_img_eps_yn);
			
			// 04.���� ���̺�(Proc) ��ȸ
			GolfAdmGoodFoodUpdDaoProc proc = (GolfAdmGoodFoodUpdDaoProc)context.getProc("GolfAdmGoodFoodUpdDaoProc");
			
			// ���α׷� ��� ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
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
				if ( !GolfUtil.isNull(main_banner_img) && !GolfUtil.isNull(orgMain_banner_img)) {
                    File realAttch = new File(realPath + subDir, orgMain_banner_img);
	                if ( realAttch.exists() ) {
	                	realAttch.delete();
	                }				
				}
				if ( !GolfUtil.isNull(main_rprs_img) && !GolfUtil.isNull(orgMain_rprs_img)) {
                    File realAttch = new File(realPath + subDir, orgMain_rprs_img);
	                if ( realAttch.exists() ) {
	                	realAttch.delete();
	                }				
				}
				//========================== ���� ���ε� End =============================================================//
				request.setAttribute("returnUrl", "admGoodFoodList.do");
				request.setAttribute("resultMsg", "���� ������ ���������� ó�� �Ǿ����ϴ�.");      	
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
				
				if ( !GolfUtil.isNull(main_rprs_img)) {
                    File tmpAttch = new File(tmpPath, main_rprs_img);
	                if ( tmpAttch.exists() ) {
	                	tmpAttch.delete();
	                }				
	                
                    File realAttch = new File(realPath + subDir, main_rprs_img);
	                if ( realAttch.exists() ) {
	                	realAttch.delete();
	                }				
				}
				//========================== ���� ���ε� End =============================================================//
				request.setAttribute("returnUrl", "admGoodFoodChgForm.do");
				request.setAttribute("resultMsg", "���� ������ ���������� ó�� ���� �ʾҽ��ϴ�.\\n\\n�ݺ������� �������� ���� ��� �����ڿ� �����Ͻʽÿ�.");		        		
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
