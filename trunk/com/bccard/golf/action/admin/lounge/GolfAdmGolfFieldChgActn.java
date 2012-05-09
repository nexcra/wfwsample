/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmGolfFieldChgActn
*   �ۼ���    : (��)����Ŀ�´����̼� ����ȯ
*   ����      : ������ ������ ���� ó��
*   �������  : golf
*   �ۼ�����  : 2009-05-28
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
import com.bccard.golf.dbtao.proc.admin.lounge.GolfAdmGolfFieldUpdDaoProc;

/******************************************************************************
* Topn
* @author	(��)����Ŀ�´����̼�
* @version	1.0
******************************************************************************/
public class GolfAdmGolfFieldChgActn extends GolfActn{
	
	public static final String TITLE = "������ ������ ���� ó��";

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

			long gf_seq_no	= parser.getLongParameter("p_idx", 0L);// �Ϸù�ȣ
			
			/**�⺻���� �Է�**/
			String gf_nm = parser.getParameter("gf_nm", "");	// �������
			String cp_nm = parser.getParameter("cp_nm", "");	// ȸ���
			String gf_clss_cd = parser.getParameter("gf_clss_cd", "");	// ����
			String gf_hole_cd = parser.getParameter("gf_hole_cd", "");	// Ȧ��
			String gf_area_cd = parser.getParameter("gf_area_cd", "");	// ����
			String open_date = parser.getParameter("open_date", "");	// ������
			String zipcode1 = parser.getParameter("zipcode1", "");	// �����ȣ1
			String zipcode2 = parser.getParameter("zipcode2", "");	// �����ȣ2
			String zipaddr = parser.getParameter("zipaddr", "");	// �ּ�
			String detailaddr = parser.getParameter("detailaddr", "");	// �����ּ�
			String addr_clss  = parser.getParameter("addr_clss"); //�ּұ���(��:1, ��:2)
			String weath_cd = parser.getParameter("weath_cd", "");	// ���������ڵ�
			String subf = parser.getParameter("subf", "");	// �δ�ü�
			String url = parser.getParameter("url", "");	// Ȩ������
			String chg_ddd_no = parser.getParameter("chg_ddd_no", "");	// ��ȭDDD��ȣ
			String chg_tel_hno = parser.getParameter("chg_tel_hno", "");	// ��ȭ����ȣ
			String chg_tel_sno = parser.getParameter("chg_tel_sno", "");	// ��ȭ�Ϸù�ȣ
			String fx_ddd_no = parser.getParameter("fx_ddd_no", "");	// �ѽ�DDD��ȣ
			String fx_tel_hno = parser.getParameter("fx_tel_hno", "");	// �ѽ���ȭ����ȣ
			String fx_tel_sno = parser.getParameter("fx_tel_sno", "");	// �ѽ���ȭ�Ϸù�ȣ
			String gf_search = parser.getParameter("gf_search", "");	// ã�ƿ��ô� ��
			String img_nm = parser.getParameter("img_nm", "");	// �������̹���
			String map_nm = parser.getParameter("map_nm", "");	// �൵�̹���
			String titl = parser.getParameter("titl", "");	// ����
			String ctnt = parser.getParameter("ctnt", "");	// ����
			
			String zipcode = zipcode1 + zipcode2;
			
			/**���� ���� �Է�**/
			String rsv_ddd_no = parser.getParameter("rsv_ddd_no", "");	// ������ȭDDD��ȣ
			String rsv_tel_hno = parser.getParameter("rsv_tel_hno", "");	// ������ȭ����ȣ
			String rsv_tel_sno = parser.getParameter("rsv_tel_sno", "");	// ������ȭ�Ϸù�ȣ
			String mb_day = parser.getParameter("mb_day", "");	// ȸ���ǳ�
			String sls_end_day = parser.getParameter("sls_end_day", "");	// ������
			String caddie_sys = parser.getParameter("caddie_sys", "");	// ĳ��ý���
			String cart_sys = parser.getParameter("cart_sys", "");	// īƮ�ý���
			
			/**���� ����**/
			String mb_day_rsvt = parser.getParameter("mb_day_rsvt", "");	// ȸ���ǳ� ȸ��
			String nmb_day_rsvt = parser.getParameter("nmb_day_rsvt", "");	// ȸ���ǳ� ��ȸ��
			String wkend_mb_rsvt = parser.getParameter("wkend_mb_rsvt", "");	// �ָ� ȸ��
			String wkend_nmb_rsvt = parser.getParameter("wkend_nmb_rsvt", "");	// �ָ� ��ȸ��
			String wk_mb_rsvt = parser.getParameter("wk_mb_rsvt", "");	// ���� ȸ��
			String wk_nmb_rsvt = parser.getParameter("wk_nmb_rsvt", "");	// ���� ��ȸ��
			
			/**��� ����**/
			long grnfee_wk_mb_amt = parser.getLongParameter("grnfee_wk_mb_amt", 0L);	// �׸��� ���� ȸ�� ���
			long grnfee_wk_nmb_amt = parser.getLongParameter("grnfee_wk_nmb_amt", 0L);	// �׸��� ���� ��ȸ�� ���
			long grnfee_wk_wmb_amt = parser.getLongParameter("grnfee_wk_wmb_amt", 0L);	// �׸��� ���� ����ȸ�� ���
			long grnfee_wk_fmb_amt = parser.getLongParameter("grnfee_wk_fmb_amt", 0L);	// �׸��� ���� ����ȸ�� ���
			long grnfee_wkend_mb_amt = parser.getLongParameter("grnfee_wkend_mb_amt", 0L);	// �׸��� �ָ� ȸ�� ���
			long grnfee_wkend_nmb_amt = parser.getLongParameter("grnfee_wkend_nmb_amt", 0L);	// �׸��� �ָ� ��ȸ�� ���
			long grnfee_wkend_wmb_amt = parser.getLongParameter("grnfee_wkend_wmb_amt", 0L);	// �׸��� �ָ� ����ȸ�� ���
			long grnfee_wkend_fmb_amt = parser.getLongParameter("grnfee_wkend_fmb_amt", 0L);	// �׸��� �ָ� ����ȸ�� ���
			String caddie_mb_amt = parser.getParameter("caddie_mb_amt", "");	// ĳ���� ȸ�� ���
			String caddie_nmb_amt = parser.getParameter("caddie_nmb_amt", "");	// ĳ���� ��ȸ�� ���
			String caddie_wmb_amt = parser.getParameter("caddie_wmb_amt", "");	// ĳ���� ����ȸ�� ���
			String caddie_fmb_amt = parser.getParameter("caddie_fmb_amt", "");	// ĳ���� ����ȸ�� ���
			String cart_mb_amt = parser.getParameter("cart_mb_amt", "");	// īƮ�� ȸ�� ���
			String cart_nmb_amt = parser.getParameter("cart_nmb_amt", "");	// īƮ�� ��ȸ�� ���
			String cart_wmb_amt = parser.getParameter("cart_wmb_amt", "");	// īƮ�� ����ȸ�� ���
			String cart_fmb_amt = parser.getParameter("cart_fmb_amt", "");	// īƮ�� ����ȸ�� ���
			
			String orgImg_nm = parser.getParameter("orgImg_nm", "");	// ���� �������̹���
			String orgMap_nm = parser.getParameter("orgMap_nm", "");	// ���� �൵�̹���
			
			
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
			//========================== ���� ���ε� End =============================================================//
			
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("ADMIN_NO", admin_id);
			
			dataSet.setLong("GF_SEQ_NO", gf_seq_no);
			dataSet.setString("GF_NM", gf_nm);
			dataSet.setString("CP_NM", cp_nm);
			dataSet.setString("GF_CLSS_CD", gf_clss_cd);
			dataSet.setString("GF_HOLE_CD", gf_hole_cd);
			dataSet.setString("GF_AREA_CD", gf_area_cd);
			dataSet.setString("OPEN_DATE", GolfUtil.toDateFormat(open_date));
			dataSet.setString("ZIPCODE", zipcode);
			dataSet.setString("ZIPADDR", zipaddr);
			dataSet.setString("DETAILADDR", detailaddr);
			dataSet.setString("ADDR_CLSS", addr_clss);			
			dataSet.setString("WEATH_CD", weath_cd);
			dataSet.setString("SUBF", subf);
			dataSet.setString("URL", url);
			dataSet.setString("CHG_DDD_NO", chg_ddd_no);
			dataSet.setString("CHG_TEL_HNO", chg_tel_hno);
			dataSet.setString("CHG_TEL_SNO", chg_tel_sno);
			dataSet.setString("FX_DDD_NO", fx_ddd_no);
			dataSet.setString("FX_TEL_HNO", fx_tel_hno);
			dataSet.setString("FX_TEL_SNO", fx_tel_sno);
			dataSet.setString("GF_SEARCH", gf_search);
			dataSet.setString("IMG_NM", img_nm);
			dataSet.setString("MAP_NM", map_nm);
			dataSet.setString("TITL", titl);
			dataSet.setString("CTNT", ctnt);
			
			dataSet.setString("RSV_DDD_NO", rsv_ddd_no);
			dataSet.setString("RSV_TEL_HNO", rsv_tel_hno);
			dataSet.setString("RSV_TEL_SNO", rsv_tel_sno);
			dataSet.setString("MB_DAY", mb_day);
			dataSet.setString("SLS_END_DAY", sls_end_day);
			dataSet.setString("CADDIE_SYS", caddie_sys);
			dataSet.setString("CART_SYS", cart_sys);
			
			dataSet.setString("MB_DAY_RSVT", mb_day_rsvt);
			dataSet.setString("NMB_DAY_RSVT", nmb_day_rsvt);
			dataSet.setString("WKEND_MB_RSVT", wkend_mb_rsvt);
			dataSet.setString("WKEND_NMB_RSVT", wkend_nmb_rsvt);
			dataSet.setString("WK_MB_RSVT", wk_mb_rsvt);
			dataSet.setString("WK_NMB_RSVT", wk_nmb_rsvt);
			
			dataSet.setLong("GRNFEE_WK_MB_AMT", grnfee_wk_mb_amt);
			dataSet.setLong("GRNFEE_WK_NMB_AMT", grnfee_wk_nmb_amt);
			dataSet.setLong("GRNFEE_WK_WMB_AMT", grnfee_wk_wmb_amt);
			dataSet.setLong("GRNFEE_WK_FMB_AMT", grnfee_wk_fmb_amt);
			dataSet.setLong("GRNFEE_WKEND_MB_AMT", grnfee_wkend_mb_amt);
			dataSet.setLong("GRNFEE_WKEND_NMB_AMT", grnfee_wkend_nmb_amt);
			dataSet.setLong("GRNFEE_WKEND_WMB_AMT", grnfee_wkend_wmb_amt);
			dataSet.setLong("GRNFEE_WKEND_FMB_AMT", grnfee_wkend_fmb_amt);
			dataSet.setString("CADDIE_MB_AMT", caddie_mb_amt);
			dataSet.setString("CADDIE_NMB_AMT", caddie_nmb_amt);
			dataSet.setString("CADDIE_WMB_AMT", caddie_wmb_amt);
			dataSet.setString("CADDIE_FMB_AMT", caddie_fmb_amt);
			dataSet.setString("CART_MB_AMT", cart_mb_amt);
			dataSet.setString("CART_NMB_AMT", cart_nmb_amt);
			dataSet.setString("CART_WMB_AMT", cart_wmb_amt);
			dataSet.setString("CART_FMB_AMT", cart_fmb_amt);
			
			// 04.���� ���̺�(Proc) ��ȸ
			GolfAdmGolfFieldUpdDaoProc proc = (GolfAdmGolfFieldUpdDaoProc)context.getProc("GolfAdmGolfFieldUpdDaoProc");
			
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
				//========================== ���� ���ε� End =============================================================//
				request.setAttribute("returnUrl", "admGolfFieldList.do");
				request.setAttribute("resultMsg", "������ ������ ���������� ó�� �Ǿ����ϴ�.");      	
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
				//========================== ���� ���ε� End =============================================================//
				request.setAttribute("returnUrl", "admGolfFieldChgForm.do");
				request.setAttribute("resultMsg", "������ ������ ���������� ó�� ���� �ʾҽ��ϴ�.\\n\\n�ݺ������� �������� ���� ��� �����ڿ� �����Ͻʽÿ�.");		        		
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
