/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmEvntMngInsReg
*   �ۼ���    : ������
*   ����      : ������ > �̺�Ʈ > ����ȸ> ����ȸ ��� ó��
*   �������  : golf
*   �ۼ�����  : 2010-10-01
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.admin.event.benest;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest; 
import javax.servlet.http.HttpServletResponse;

import com.bccard.waf.common.BaseException;
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
import com.bccard.golf.dbtao.proc.admin.event.benest.GolfAdmEvntMngListDaoProc;
import com.bccard.golf.common.AppConfig;

/******************************************************************************
* Golf
* @author	�̵������
* @version	1.0
******************************************************************************/
public class GolfAdmEvntAppDateList extends GolfActn{
	
	public static final String TITLE = "������ > �̺�Ʈ > ����ȸ> ����ȸ ��� ó��";

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
			
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);	
			
			
			/*String green_nm = parser.getParameter("GREEN_NM","");		//�������
			String bltn_strt_date = parser.getParameter("BLTN_STRT_DATE", "").replaceAll("-", "");	//�ԽñⰣ
			String bltn_end_date = parser.getParameter("BLTN_END_DATE", "").replaceAll("-", "");	// �ԽñⰣ
			String evnt_strt_date = parser.getParameter("EVNT_STRT_DATE", "").replaceAll("-", "");	// �̺�Ʈ�Ⱓ
			String evnt_end_date = parser.getParameter("EVNT_END_DATE", "").replaceAll("-", "");	// �̺�Ʈ�Ⱓ
			
			String cpo_amt = parser.getParameter("CPO_AMT", "");//è�Ǿ�
			String acrg_cdhd_amt = parser.getParameter("ACRG_CDHD_AMT", "");	// ����
			String free_cdhd_amt = parser.getParameter("FREE_CDHD_AMT", "");	// ����
			String titl_img = parser.getParameter("TITL_IMG", ""); //Ÿ��Ʋ�̹���
			String evnt_bnft_expl = parser.getParameter("EVNT_BNFT_EXPL", "");	//�̺�Ʈ ���� ����
			
			String orgTitl_img = parser.getParameter("orgTitl_img", "");	// ���� Ÿ��Ʋ�̹���
			
			debug("@@@@@green_nm : " + green_nm);
			
////////////////////////////////////////////////////////////////////////////////////////////////
			// ���� ������ �̹����� ����					
			// imgPath : ���� ������ ���� �̹����� ����Ǵ� �����̸�
			// �Խ��� ���� ���ε�� �̹��� ���� imgPath ������ : /WEB-INF/config/config.xml ���� �߰��ϼ���.
			String imgPath = "/benest";
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
			if(!evnt_bnft_expl.equals("")){
	            MimeData mime = new MimeData();
				mime.setSaveURL ( mapDir + imgPath );			// �̹�����ȸURL ����
				mime.setSavePath( contImgPath + imgPath );		// ���� ���� ��� ����			
	            mime.decode(evnt_bnft_expl);                     		// MIME ���ڵ�
	            mime.saveFile();                           		// ������ ���� �����ϱ�
	            evnt_bnft_expl = mime.getBodyContent();        	// ���밡������
			}
            ////////////////////////////////////////////////////////////////////////////////////
			
			
			//========================== ���� ���ε� Start =============================================================//
			String realPath	= AppConfig.getAppProperty("UPLOAD_REAL_PATH"); 	
			String tmpPath	= AppConfig.getAppProperty("UPLOAD_TMP_PATH"); 
			String subDir = "/benest"; 

			if ( !GolfUtil.isNull(titl_img)) {
                File tmp = new File(tmpPath,titl_img);
                if ( tmp.exists() ) {

        			File createPath  =	new	File(realPath + subDir);
        			if (!createPath.exists()){
        				createPath.mkdirs();
        			}

                    String name = titl_img.substring(0, titl_img.lastIndexOf('.'));
                    String ext = titl_img.substring(titl_img.lastIndexOf('.'));

                    File listAttch = new File(createPath, titl_img);
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
			//dataSet.setString("REG_DATE", seq_no);
			dataSet.setString("GREEN_NM", green_nm);
			dataSet.setString("BLTN_STRT_DATE", bltn_strt_date);
			dataSet.setString("BLTN_END_DATE", bltn_end_date);
			dataSet.setString("EVNT_STRT_DATE", evnt_strt_date);
			dataSet.setString("EVNT_END_DATE", evnt_end_date);
			dataSet.setString("CPO_AMT", cpo_amt.replaceAll(",", ""));
			dataSet.setString("ACRG_CDHD_AMT", acrg_cdhd_amt.replaceAll(",",""));
			dataSet.setString("FREE_CDHD_AMT", free_cdhd_amt.replaceAll(",",""));
			dataSet.setString("TITL_IMG", titl_img);
			dataSet.setString("EVNT_BNFT_EXPL", evnt_bnft_expl);
			
			
			// 04.���� ���̺�(Proc) ��ȸ
			GolfAdmEvntMngListDaoProc proc = (GolfAdmEvntMngListDaoProc)context.getProc("GolfAdmEvntMngListDaoProc");
			int addResult = proc.excute_insert(context, dataSet);			
			
	        String returnUrlTrue = "";
	        String returnUrlFalse = "";
	       	returnUrlTrue = "admEvntMngList.do";
			
			if (addResult == 1) {
				request.setAttribute("returnUrl", returnUrlTrue);
				request.setAttribute("resultMsg", "����� ���������� ó�� �Ǿ����ϴ�.");      	
	        } else {
				request.setAttribute("returnUrl", returnUrlFalse);
				request.setAttribute("resultMsg", "����� ���������� ó�� ���� �ʾҽ��ϴ�.\\n\\n�ݺ������� ��ϵ��� ���� ��� �����ڿ� �����Ͻʽÿ�.");		        		
	        }*/
			
			// 05. Return �� ����			
			//paramMap.put("addResult", String.valueOf(addResult));			
	        request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.
	        
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
	
}
