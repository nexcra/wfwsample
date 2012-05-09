/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfBoardListActn
*   �ۼ���    : (��)����Ŀ�´����̼� ���¿�
*   ����      : ����Խ��� ����Ʈ
*   �������  : Golf
*   �ۼ�����  : 2009-05-28
************************** �����̷� ****************************************************************
*    ����     �ۼ���   �������
*  20110304  �̰���   [http://www.bccard.com/-"Home > VIP���� > ���� > ���� ��Ģ&�ų� �� �����ߴ��� �α� ��� �� �ʱⰪ ����
*  					  [http://www.bccard.com/-"Home > VIP���� > ���� > ���� ��� ����� �� �����ߴ��� �α� ��� �� �ʱⰪ ����
***************************************************************************************************/
package com.bccard.golf.action.bbs;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.bbs.GolfBoardListDaoProc;
import com.bccard.golf.dbtao.proc.code.GolfCodeSelDaoProc;

import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.golf.common.loginAction.SessionUtil;

/******************************************************************************
* Golf
* @author	(��)����Ŀ�´����̼�
* @version	1.0
******************************************************************************/
public class GolfBoardListActn extends GolfActn{
	
	public static final String TITLE = "����Խ��� ����Ʈ";

	/***************************************************************************************
	* ���� ������ȭ��
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
		int intCyberMoney = 0; 
		boolean flag = true;
		
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
				intCyberMoney	= (int)usrEntity.getCyberMoney(); //���̹��Ӵ�
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
			paramMap.put("RurlPath", AppConfig.getAppProperty("URL_REAL"));
			paramMap.put("imgUrlPath", AppConfig.getAppProperty("IMG_URL_REAL")+"/bbs");

			// Request �� ����
			long page_no		= parser.getLongParameter("page_no", 1L);			// ��������ȣ
			long record_size	= parser.getLongParameter("record_size", 10);		// ����������¼�
			String search_sel		= parser.getParameter("search_sel", "");
			String search_word		= parser.getParameter("search_word", "");

			String bbs		= parser.getParameter("bbs", "0035");
			String sch_field_cd		= parser.getParameter("sch_field_cd", ""); 
			String sch_clss_cd		= parser.getParameter("sch_clss_cd", ""); 
			String sch_sec_cd		= parser.getParameter("sch_sec_cd", ""); 
			String sch_hd_yn		= parser.getParameter("sch_hd_yn", ""); 
			String sreg_sdate		= parser.getParameter("sreg_sdate", "");
			String sreg_edate		= parser.getParameter("sreg_edate", "");
			String sort		= parser.getParameter("sort", "");
			
			sreg_sdate = sreg_sdate.length() == 10 ? DateUtil.format(sreg_sdate, "yyyy-MM-dd", "yyyyMMdd"): "";
			sreg_edate = sreg_edate.length() == 10 ? DateUtil.format(sreg_edate, "yyyy-MM-dd", "yyyyMMdd"): "";
			
			String inBc = request.getAttribute("actnKey").toString();
			
			//[ http://www.bccard.com/->VIP����/����/~ ]���� ���ӽ�
			if (inBc.equals("golfRulListInBC")){
				debug("## "+this.getClass().getName()+" | 'http://www.bccard.com/->VIP����/����/���� ��Ģ&�ų�'���� ���� " );				
				bbs = "0038";
			}else if (inBc.equals("golfDicListInBC")){
				debug("## "+this.getClass().getName()+" | 'http://www.bccard.com/->VIP����/����/���� ��� �����'���� ���� " );
				bbs = "0037";
			}
			
			// Ư���Խ��� �̿����� üũ
			/*
			if (bbs.equals("0036")){ // ����Į��
				if (isLogin.equals("0")) { // ���ȸ�� ����
					flag = false;
				}
			}
			*/
			
			//debug("bbs1 ====> "+ bbs);
			
			paramMap.put("bbs", bbs);
			paramMap.put("sch_field_cd", sch_field_cd);
			paramMap.put("sch_clss_cd", sch_clss_cd);
			paramMap.put("sch_sec_cd", sch_sec_cd);
			paramMap.put("search_word", search_word);
						
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setLong("PAGE_NO", page_no);
			dataSet.setLong("RECORD_SIZE", record_size);
			dataSet.setString("SEARCH_SEL", search_sel);
			dataSet.setString("SEARCH_WORD", search_word);
			dataSet.setString("BBS", bbs);
			dataSet.setString("SCH_FIELD_CD", sch_field_cd);
			dataSet.setString("SCH_CLSS_CD", sch_clss_cd);
			dataSet.setString("SCH_SEC_CD", sch_sec_cd.toUpperCase());
			dataSet.setString("SCH_HD_YN", sch_hd_yn);
			dataSet.setString("SREG_SDATE", sreg_sdate);
			dataSet.setString("SREG_EDATE", sreg_edate);
			dataSet.setString("SORT", sort);
			
			// �̿����� üũ
			//if (flag) {
				
				// 04.���� ���̺�(Proc) ��ȸ
				GolfBoardListDaoProc proc = (GolfBoardListDaoProc)context.getProc("GolfBoardListDaoProc");
				GolfCodeSelDaoProc coodSelProc = (GolfCodeSelDaoProc)context.getProc("GolfCodeSelDaoProc");
				DbTaoResult bbsListResult = (DbTaoResult) proc.execute(context, request, dataSet);
				DbTaoResult codeSel = (DbTaoResult) coodSelProc.execute(context, dataSet, bbs, "Y"); //�Խ��� ����
				
				// ��ü 0��  [ 0/0 page] ���� ��������
				long totalRecord = 0L;
				long currPage = 0L;
				long totalPage = 0L;
				
				if (bbsListResult != null && bbsListResult.isNext()) {
					bbsListResult.first();
					bbsListResult.next();
					if (bbsListResult.getObject("RESULT").equals("00")) {
						totalRecord = Long.parseLong((String)bbsListResult.getString("TOTAL_CNT"));
						currPage = Long.parseLong((String)bbsListResult.getString("CURR_PAGE"));
						totalPage = (totalRecord % record_size == 0) ? (totalRecord / record_size) : (totalRecord / record_size)+1;
					}
				}
				
				paramMap.put("totalRecord", String.valueOf(totalRecord));
				paramMap.put("currPage", String.valueOf(currPage));
				paramMap.put("totalPage", String.valueOf(totalPage));
				paramMap.put("resultSize", String.valueOf(bbsListResult.size()));
				
				request.setAttribute("bbsListResult", bbsListResult);
				request.setAttribute("codeSelResult", codeSel);
				request.setAttribute("record_size", String.valueOf(record_size));
		        request.setAttribute("paramMap", paramMap);
		        
			//} else {
			//	subpage_key = "limitReUrl";
			//}
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
