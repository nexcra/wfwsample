/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfTopGolfCardNoticeListActn
*   �ۼ���    : �̵������ �ǿ���
*   ����      : ž����ī�� ��������
*   �������  : Golf
*   �ۼ�����  : 2010-10-25
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.booking.premium;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.GolfUserEtt;
import com.bccard.golf.common.login.CardInfoEtt;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.bbs.GolfBoardListDaoProc;
import com.bccard.golf.dbtao.proc.code.GolfCodeSelDaoProc;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0
******************************************************************************/
public class GolfTopGolfCardNoticeListActn extends GolfActn{
	
	public static final String TITLE = "ž����ī�� �������� ����Ʈ";

	/***************************************************************************************
	* ���� ������ȭ��
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/
	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";
				
		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);
		String memberClss = "";
		String memId = "";
		
		int memNo =  0;
		
		String strMemChkNum = "";		//ȸ������ 1:��ȸ�� / 4: ��ȸ�� / 5:����ȸ��
		// 00.���̾ƿ� URL ����
		String topGolfCardNo 	= "";
		String topGolfCardYn 	= "N";		//ž����ī�� ���� ����
		
		try {
			
			// 01.��������üũ 
			UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request);
						
			
			if(userEtt != null){
				memId = userEtt.getAccount();				// ȸ�� ���̵�
				memNo = userEtt.getMemid();
			}
			
			/*
			 * top���� ī�� ȸ������ üũ
			 * */
			
			GolfUserEtt mbr = SessionUtil.getTopnUserInfo(request);
			try {
				List topGolfCardList = mbr.getTopGolfCardInfoList();
				CardInfoEtt cardInfoTopGolfEtt = new CardInfoEtt();
				
				if( topGolfCardList!=null && topGolfCardList.size() > 0 )
				{
					for (int i = 0; i < topGolfCardList.size(); i++) 
					{
						cardInfoTopGolfEtt = (CardInfoEtt)topGolfCardList.get(0);
						topGolfCardNo = cardInfoTopGolfEtt.getCardNo();
						topGolfCardYn = "Y";
						debug("## ž����ī�� ���� ȸ�� | topGolfCardNo : "+topGolfCardNo);
					}
					
					//golfCardCoYn = mbr.getGolfCardCoYn();
				}
				else
				{
					topGolfCardYn = "N";
					debug("## ž����ī�� �̼���");					
				}
			} catch(Throwable t) 
			{
				topGolfCardYn = "N";
				debug("## ž����ī�� üũ ����");	
			}
			if(memId.equals("altec16") || memId.equals("amazon6") || memId.equals("graceyang") ||  memId.equals("mongina") || memId.equals("msj9529") ){
				topGolfCardYn 	= "Y";	
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

			String bbs		= parser.getParameter("bbs", "0062");
			String sch_field_cd		= parser.getParameter("sch_field_cd", ""); 
			String sch_clss_cd		= parser.getParameter("sch_clss_cd", ""); 
			String sch_sec_cd		= parser.getParameter("sch_sec_cd", ""); 
			String sch_hd_yn		= parser.getParameter("sch_hd_yn", ""); 
			String sreg_sdate		= parser.getParameter("sreg_sdate", "");
			String sreg_edate		= parser.getParameter("sreg_edate", "");
			String sort		= parser.getParameter("sort", "");
			if("".equals(bbs) || bbs == null) bbs = "0062"; 
			
			
			sreg_sdate = sreg_sdate.length() == 10 ? DateUtil.format(sreg_sdate, "yyyy-MM-dd", "yyyyMMdd"): "";
			sreg_edate = sreg_edate.length() == 10 ? DateUtil.format(sreg_edate, "yyyy-MM-dd", "yyyyMMdd"): "";
			
			
			
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
			paramMap.put("topGolfCardYn", topGolfCardYn);
			
			request.setAttribute("bbsListResult", bbsListResult);
			request.setAttribute("codeSelResult", codeSel);
			request.setAttribute("record_size", String.valueOf(record_size));
	        request.setAttribute("paramMap", paramMap);
		        
		
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
