/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfGoodFoodListActn
*   �ۼ���    : (��)����Ŀ�´����̼� ����ȯ
*   ����      : ������ �ֺ����� ����Ʈ
*   �������  : Golf
*   �ۼ�����  : 2009-06-09
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.lounge;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.waf.common.BaseException;
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
import com.bccard.golf.dbtao.proc.booking.GolfBkPermissionDaoProc;
import com.bccard.golf.dbtao.proc.category.GolfCateSelInqDaoProc;
import com.bccard.golf.dbtao.proc.lounge.GolfGoodFoodListDaoProc;

import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.golf.common.loginAction.SessionUtil;

/******************************************************************************
* Golf
* @author	(��)����Ŀ�´����̼�
* @version	1.0
******************************************************************************/
public class GolfGoodFoodListActn extends GolfActn{
	
	public static final String TITLE = " ������ �ֺ����� ����Ʈ";

	/***************************************************************************************
	* ���� �����ȭ��
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/
	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		String userNm = ""; 
		String memClss ="";
		String userId = "";
		String isLogin = ""; 
		String juminno = ""; 
		String memGrade = ""; 
		int intMemGrade = 0; 
		String permission = "";
		
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
			
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			paramMap.put("imgUrlPath", AppConfig.getAppProperty("IMG_URL_REAL")+"/lounge");
			
			
			// Request �� ����
			long page_no		= parser.getLongParameter("page_no", 1L);			// ��������ȣ
			long record_size	= parser.getLongParameter("record_size", 10);		// ����������¼�
			String search_sel		= parser.getParameter("search_sel", "");
			String search_word		= parser.getParameter("search_word", "");
			
			String sido	= parser.getParameter("s_sido", "");		// ����
			String gugun	= parser.getParameter("s_gugun", "");		// ��������
			String dong	= parser.getParameter("s_dong", "");		// ������
			String fd1_lev_cd	= parser.getParameter("s_fd1_lev_cd", "");		// ����1�� �з�
			String fd2_lev_cd	= parser.getParameter("s_fd2_lev_cd", "");		// ����2�� �з�
			String fd3_lev_cd	= parser.getParameter("s_fd3_lev_cd", "");		// ����3�� �з�
			String gf_area_cd		= parser.getParameter("s_gf_area_cd", "");  // ������
			
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setLong("PAGE_NO", page_no);
			dataSet.setLong("RECORD_SIZE", record_size);
			dataSet.setString("SEARCH_SEL", search_sel);
			dataSet.setString("SEARCH_WORD", search_word);
			
			dataSet.setString("SIDO", sido);
			dataSet.setString("GUGUN", gugun);
			dataSet.setString("DONG", dong);
			dataSet.setString("FD1_LEV_CD", fd1_lev_cd);
			dataSet.setString("FD2_LEV_CD", fd2_lev_cd);
			dataSet.setString("FD3_LEV_CD", fd3_lev_cd);
			dataSet.setString("GF_AREA_CD", gf_area_cd);
			
			dataSet.setString("PT_CATEGORY_ID", "0000");
			dataSet.setString("CTG_CLSS", "10");
			
			// ���ٱ��� ��ȸ	
			String permissionColum = "ETHS_APO_YN";
			GolfBkPermissionDaoProc proc_permission = (GolfBkPermissionDaoProc)context.getProc("GolfBkPermissionDaoProc");
			DbTaoResult permissionView = proc_permission.execute(context, dataSet, userId, permissionColum);

			permissionView.next();
			if(permissionView.getString("RESULT").equals("00")){
				permission = permissionView.getString("LIMT_YN");
				//debug("=`=`=`=`=`=`=`=`=GolfBkPreGrListActn === PMI_BOKG_APO_YN => " + permissionView.getString("PMI_BOKG_APO_YN"));
			}else{
				permission = "N";
			}
			
			
			
			// �̿����� üũ
			//if (isLogin.equals("1") && intMemGrade < 4) { // �췮ȸ���̻� ����
				
				// 04.���� ���̺�(Proc) ��ȸ
				GolfGoodFoodListDaoProc proc = (GolfGoodFoodListDaoProc)context.getProc("GolfGoodFoodListDaoProc");
				GolfCateSelInqDaoProc coopCtSelProc = (GolfCateSelInqDaoProc)context.getProc("GolfCateSelInqDaoProc");
				
				DbTaoResult goodfoodListResult = (DbTaoResult) proc.execute(context, request, dataSet);
				
				// �ڵ� ��ȸ ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
				DbTaoResult coopCtSel = coopCtSelProc.execute(context, request, dataSet); //���ĺз��ڵ�
				
				
				// ��ü 0��  [ 0/0 page] ���� ��������
				long totalRecord = 0L;
				long currPage = 0L;
				long totalPage = 0L;
				
				if (goodfoodListResult != null && goodfoodListResult.isNext()) {
					goodfoodListResult.first();
					goodfoodListResult.next();
					if (goodfoodListResult.getObject("RESULT").equals("00")) {
						totalRecord = Long.parseLong((String)goodfoodListResult.getString("TOTAL_CNT"));
						currPage = Long.parseLong((String)goodfoodListResult.getString("CURR_PAGE"));
						totalPage = (totalRecord % record_size == 0) ? (totalRecord / record_size) : (totalRecord / record_size)+1;
					}
				}
				
				
				paramMap.put("totalRecord", String.valueOf(totalRecord));
				paramMap.put("currPage", String.valueOf(currPage));
				paramMap.put("totalPage", String.valueOf(totalPage));
				paramMap.put("resultSize", String.valueOf(goodfoodListResult.size()));
				
				request.setAttribute("goodfoodListResult", goodfoodListResult);
				request.setAttribute("record_size", String.valueOf(record_size));
				request.setAttribute("coopCtSel", coopCtSel);
				request.setAttribute("paramMap", paramMap);
				
			//} else {
				//subpage_key = "limitReUrl";
			//}
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
