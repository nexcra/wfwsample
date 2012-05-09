/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfRoundAfterListActn
*   �ۼ���    : shin cheong gwi
*   ����      : �����ı�
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
import com.bccard.golf.dbtao.proc.booking.premium.GolfTopGolfCardListDaoProc;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

public class GolfRoundAfterListActn extends GolfActn { 

	public static final String TITLE = "���� �ı�"; 
	
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
			
			String tab_idx = parser.getParameter("tab_idx", "1");
			String board_cd = parser.getParameter("board_cd", "12");			// �Խù���ȣ			
			String actn_key = parser.getParameter("actn_key", "after");			// �Խù� �׼�
			String search_type = parser.getParameter("search_type", "BOARD_SUBJ");	// �˻�����
			String search_word = parser.getParameter("search_word", "");	// �˻���	
			//String add_yn = parser.getParameter("add_yn", "");
			long pageNo = parser.getLongParameter("pageNo", 1L); 			// ��������ȣ			
			long recordsInPage = parser.getLongParameter("recordsInPage", 10L); // ����������¼�			
			long totalPage = 0L;			// ��ü��������
			long recordCnt = 0L; 						
			//actn_key = parser.getParameter("actn_key") == null ? "after" : actn_key;	
			int totalSize = 0;
			
			String topGolfCardNo 	= "";
			String topGolfCardYn 	= "N";		//ž����ī�� ���� ����
			//String getPassId = "N";		//������Ͼ��̵� 
			String coMemType ="" ;				//ī��
			String memberClss = "";
			String memId = "";
			int memNo =  0;
			int intMemGrade = 0;
			String memSocId ="";
			
			// 02.Proc �� ���� �� ���� 
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
				//dataSet.setString("board_cd", board_cd);
				//dataSet.setString("board_dtl_cd", board_dtl_cd);
				dataSet.setString("search_type", search_type);
				dataSet.setString("search_word", search_word);
				dataSet.setString("add_yn", "");	
				dataSet.setString("board_cd", board_cd);
				dataSet.setLong("pageNo", pageNo);
				dataSet.setLong("recordsInPage", recordsInPage);
					
			// 03.Proc ����
			GolfRoundAfterListProc instance = null;
			DbTaoResult roundList = null;
			instance = GolfRoundAfterListProc.getInstance();			
			roundList = instance.execute(context, request, dataSet);
			totalSize = roundList.size();
			debug("��ü������:"+totalSize);   
						
			if(roundList.isNext()){
				roundList.next();
				if(roundList.getString("RESULT").equals("00")){					
					paramMap.put("recordCnt", String.valueOf(roundList.getLong("RECORD_CNT")));
					recordCnt = roundList.getLong("RECORD_CNT");
				}else{
					paramMap.put("recordCnt", "0");
					recordCnt = 0L;
				}
			} 
			
			totalPage = (recordCnt % recordsInPage == 0) ? (recordCnt / recordsInPage) : (recordCnt / recordsInPage) + 1;
			
			// 04.��������üũ 
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
			
			// 05. top���� ī�� ȸ������ üũ			 			
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
			
			// 06. ����������̵�
			if( memId.equals("amazon6") || memId.equals("graceyang") ||  memId.equals("mongina") || memId.equals("msj9529") ||memId.equals("altec16") || memId.equals("bcgolf2")|| memId.equals("leekj76")){
				topGolfCardYn 	= "Y";	
			}
			
			debug("totalPage====>"+totalPage+"====>recordCnt===>"+recordCnt+"===>listSize===>"+roundList.size());
			
			request.setAttribute("roundList", roundList);	
			paramMap.put("listSize", String.valueOf(roundList.size()));
			//paramMap.put("roundList", roundList);			
			paramMap.put("tab_idx", tab_idx);
			paramMap.put("search_type", search_type);
			paramMap.put("search_word", search_word);
			paramMap.put("board_cd", board_cd);
			paramMap.put("actn_key", actn_key);
			paramMap.put("pageNo", String.valueOf(pageNo));			
			paramMap.put("recordsInPage", String.valueOf(recordsInPage));			
			paramMap.put("totalPage", String.valueOf(totalPage));
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
