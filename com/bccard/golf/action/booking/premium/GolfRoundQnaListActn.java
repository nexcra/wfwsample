/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfRoundQnaListActn
*   �ۼ���    : shin cheong gwi
*   ����      : �������亯
*   �������  : golfloung
*   �ۼ�����  : 2010-11-09
************************** �����̷� ****************************************************************
*
***************************************************************************************************/
package com.bccard.golf.action.booking.premium;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.GolfUserEtt;
import com.bccard.golf.common.login.CardInfoEtt;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.booking.premium.GolfRoundAfterListProc;
import com.bccard.golf.dbtao.proc.booking.premium.GolfRoundAfterViewProc;
import com.bccard.golf.dbtao.proc.booking.premium.GolfTopGolfCardListDaoProc;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

public class GolfRoundQnaListActn extends GolfActn { 

	public static final String TITLE = "���� �������亯"; 
	
	// ��ŷ&���� �ı� ����
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException
	{
		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);
		String viewType = "default"; 
		
		try
		{ 
			// 01.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(viewType, request, response);
			Map paramMap = BaseAction.getParamToMap(request);			
			paramMap.put("title", TITLE);			
			
			String tab_idx = parser.getParameter("tab_idx", "2");
			String board_cd = parser.getParameter("board_cd", "52");				// �Խù���ȣ			
			String actn_key = parser.getParameter("actn_key", "");					// �Խù� �׼�
			String search_type = parser.getParameter("search_type2", "BOARD_SUBJ");			// �˻�����
			String search_word = parser.getParameter("search_word2", "");			// �˻���	
			String add_yn = parser.getParameter("add_yn", "");			
			long pageNo2 = parser.getLongParameter("pageNo2", 1L); 					// ��������ȣ			
			long recordsInPage2 = parser.getLongParameter("recordsInPage2", 10L); 	// ����������¼�			
			long totalPage2 = 0L;													// ��ü��������
			long recordCnt2 = 0L; 			
			actn_key = parser.getParameter("actn_key") == null ? "qna" : actn_key;		
			
			String topGolfCardNo 	= "";
			String topGolfCardYn 	= "N";		//ž����ī�� ���� ����
			String getPassId = "N";		//������Ͼ��̵� 
			String coMemType ="" ;				//ī��
			String memberClss = "";
			String memId = "";
			int memNo =  0;
			int intMemGrade = 0;
			String memSocId ="";
						
			// 02.Proc �� ���� �� ���� 
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("board_cd", board_cd);
			//dataSet.setString("board_dtl_cd", board_dtl_cd);
			dataSet.setLong("pageNo", pageNo2);
			dataSet.setLong("recordsInPage", recordsInPage2);					
			dataSet.setString("search_type", search_type);
			dataSet.setString("search_word", search_word);
			dataSet.setString("add_yn", "");			
		
			// 03.��������üũ 
			UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request);
			if(userEtt != null){
				
				memId = userEtt.getAccount();				// ȸ�� ���̵�
				memNo = userEtt.getMemid();					//��� ������ȣ
				intMemGrade = userEtt.getIntMemGrade();	
				memberClss= userEtt.getStrMemChkNum();		// 1:��ȸ�� / 4: ��ȸ�� / 5:����ȸ��
				coMemType = userEtt.getStrCoMemType();		// 2:ȸ������(����) 6:����ī��(����)
				if("5".equals(memberClss)){
					if("6".equals(coMemType))
						memSocId = userEtt.getSocid();
						
					else{
						memSocId = userEtt.getStrCoNum();		//����ī��(����) - ����� ��Ϲ�ȣ
					}					
				}else{
					memSocId = userEtt.getSocid();			//ī�����ī��(����) -  �ֹε�Ϲ�ȣ
				}				
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
						
						topGolfCardYn = "Y";		// ž����ī�� ������										
					}
					if("Y".equals(topGolfCardYn)){
						/**golfloung���� ���̵� �ִ��� Ȯ�� ������ ��� ���� �������� �̵�*/
						
						GolfTopGolfCardListDaoProc proc = (GolfTopGolfCardListDaoProc)context.getProc("GolfTopGolfCardListDaoProc");
						dataSet.setString("memId", memId);					//ȸ�����̵�
						int isYn = (int)proc.is_topMember(context, request, dataSet);		//ƼŸ�� ��û�ڼ�						
						if(isYn < 1 ){
							return super.getActionResponse(context, "join");		//������� �������� �̵�
						}
					}					
				}
				else 
				{
					topGolfCardYn = "N";			// ž����ī�� �̼�����						
				}
			} catch(Throwable t) 
			{
				topGolfCardYn = "N";
				debug("## ž����ī�� üũ ����");	
			}
			
			// 03.Proc ����
			GolfRoundAfterListProc instance1 = GolfRoundAfterListProc.getInstance();				// ����			
			DbTaoResult qnaList = instance1.execute(context, request, dataSet);		
			
			GolfRoundAfterViewProc instance2 = GolfRoundAfterViewProc.getInstance();			// Q&A �亯��..			
			
			if(qnaList.isNext()){
				qnaList.next();
				if(qnaList.getString("RESULT").equals("00")){
					paramMap.put("recordCnt2", String.valueOf(qnaList.getLong("RECORD_CNT")));
					recordCnt2 = qnaList.getLong("RECORD_CNT");					
				}else{
					paramMap.put("recordCnt2", "0");					
					recordCnt2 = 0L;
				}
			}
			
			totalPage2 = (recordCnt2 % recordsInPage2 == 0) ? (recordCnt2 / recordsInPage2) : (recordCnt2 / recordsInPage2) + 1;
			
			// 06. ����������̵�
			if( memId.equals("amazon6") || memId.equals("graceyang") ||  memId.equals("mongina") || memId.equals("msj9529") ||memId.equals("altec16") || memId.equals("bcgolf2")|| memId.equals("leekj76")){
				topGolfCardYn 	= "Y";	
			}
			
			request.setAttribute("qnaList", qnaList);
			//paramMap.put("qnaList", qnaList);	 					
			paramMap.put("tab_idx", tab_idx);
			paramMap.put("search_type2", search_type);
			paramMap.put("search_word2", search_word);
			paramMap.put("board_cd", board_cd);
			paramMap.put("actn_key", actn_key);			
			paramMap.put("pageNo2", String.valueOf(pageNo2));			
			paramMap.put("recordsInPage2", String.valueOf(recordsInPage2));	
			paramMap.put("totalPage2", String.valueOf(totalPage2));
			paramMap.put("topGolfCardYn", topGolfCardYn);
			request.setAttribute("paramMap", paramMap);					
			viewType = actn_key;
			
		}catch(Throwable t) {
			debug(TITLE, t);			
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, viewType);
	}
}
