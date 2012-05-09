/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfadmTopGolfRoundAfterInqActn
*   �ۼ���    : shin cheong gwi
*   ����      : ������ > �����ı�
*   �������  : golfloung
*   �ۼ�����  : 2010-11-24
************************** �����̷� ****************************************************************
*
***************************************************************************************************/
package com.bccard.golf.action.admin.booking.premium;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.admin.booking.premium.GolfAdmTopGolfRoundAfterListProc;
import com.bccard.golf.dbtao.proc.admin.booking.premium.GolfAdmTopGolfRoundAfterViewProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

public class GolfadmTopGolfRoundAfterInqActn extends GolfActn{

	public static final String TITLE = "������ > �����ı� / Q&A"; 
	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException
	{
		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);
		String viewType = "default";
		int viewSize = 0;
		int replySize = 0;
		
		try
		{
			// 01.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(viewType, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			
			String actn_key = parser.getParameter("actn_key", "");
			String board_cd = parser.getParameter("board_cd", "");
			String tab_idx = parser.getParameter("tab_idx", "1");
			String access = parser.getParameter("access", "");
			String search_type = parser.getParameter("search_type", "");						// �˻�����
			String search_word = parser.getParameter("search_word", "");						// �˻���	
			String search_type2 = parser.getParameter("search_type2", "");						// �˻�����
			String search_word2 = parser.getParameter("search_word2", "");						// �˻���	
			//long ref_no = parser.getLongParameter("ref_no", 0L);
			long ans_lev = parser.getLongParameter("ans_lev", 0L);
			long ans_stg = parser.getLongParameter("ans_stg", 0L);	
			long board_no = parser.getLongParameter("board_no", 0L);
			long pageNo = parser.getLongParameter("pageNo", 1L); 									// ��������ȣ
			long pageNo2 = parser.getLongParameter("pageNo2", 1L); 									// ��������ȣ
			
			DbTaoDataSet data = new DbTaoDataSet(TITLE);
				data.setLong("board_no", board_no);
				data.setString("board_cd", board_cd);				
						
			if(actn_key.equals("after") || actn_key.equals("qna")){														
			
				GolfAdmTopGolfRoundAfterViewProc instance = GolfAdmTopGolfRoundAfterViewProc.getInstance();
				//instance.readCnt_execute(context, request, data);									// Read Count Increase
				DbTaoResult roundView = instance.execute(context, request, data);					// �󼼺���
				
				if(access.equals("edit") || access.equals("reply")){			// ���� / ��۽�
					//GolfRoundAfterViewProc instance = GolfRoundAfterViewProc.getInstance();
					//DbTaoResult roundView = instance.execute(context, request, data);			// �󼼺���
					request.setAttribute("roundView", roundView);			// �󼼺���
					//request.setAttribute("roundViewSize", String.valueOf(roundView.size()));
					viewSize = roundView.size();
					
					if(access.equals("reply")){
						DbTaoResult replyView = instance.qnaAnswer_execute(context, request, data);
						request.setAttribute("replyView", replyView);
						request.setAttribute("replySize", String.valueOf(replyView.size()));
						paramMap.put("replySize", String.valueOf(replyView.size()));							
					}					
					viewType = "default"; 
				}else{															// �����
					DbTaoResult nextPrevList = instance.nextPrev_execute(context, request, data);		// ������ / ������
					
					//if(actn_key.equals("after")){				// ���� �ı�					
					//	paramMap.put("answerSize", String.valueOf(roundView.size()));
					//	debug("answrSize=================>"+roundView.size());
					//}else if(actn_key.equals("qna")){			// Q&A
						DbTaoResult answerView = instance.qnaAnswer_execute(context, request, data);	// Q&A �亯��..
						
						request.setAttribute("answerView", answerView);
						request.setAttribute("answerSize", String.valueOf(answerView.size()));
						paramMap.put("answerSize", String.valueOf(answerView.size()));		
						
						debug("answrSize=================>"+answerView.size());
					//}
					
					List reg_no = (List)roundView.getField("reg_no");
					String regno = "0";
					if(roundView.size() > 0){
						regno = (String)reg_no.get(0);
					}
					request.setAttribute("reg_no", regno);
					
					GolfAdmTopGolfRoundAfterListProc instance2 = GolfAdmTopGolfRoundAfterListProc.getInstance();		// �ڸ�Ʈ ����
					DbTaoResult commentList = instance2.comment_execute(context, request, data);	
					
					request.setAttribute("commentList", commentList);								// �ڸ�Ʈ ����
					request.setAttribute("commentSize", String.valueOf(commentList.size()));
					
					request.setAttribute("roundView", roundView);			// �󼼺���				
					request.setAttribute("nextPrevList", nextPrevList);		// ������/������
					viewSize = roundView.size();
					viewType = actn_key;
				}				
			}			
			
			paramMap.put("pageNo", String.valueOf(pageNo));
			paramMap.put("pageNo2", String.valueOf(pageNo2));
			paramMap.put("actn_key", actn_key);
			paramMap.put("board_cd", board_cd);
			paramMap.put("tab_idx", tab_idx);
			paramMap.put("ans_lev", String.valueOf(ans_lev));
			paramMap.put("ans_stg", String.valueOf(ans_stg));
			paramMap.put("board_no", String.valueOf(board_no));
			paramMap.put("roundViewSize", String.valueOf(viewSize));
			paramMap.put("search_type", search_type);
			paramMap.put("search_word", search_word);
			paramMap.put("search_type2", search_type2);
			paramMap.put("search_word2", search_word2);
			paramMap.put("access", access);
			request.setAttribute("paramMap", paramMap);
						
		}catch(Throwable t) {
			debug(TITLE, t);			
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, viewType);		
	}
}
