/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmGiftRegActn
*   �ۼ���     : (��)�̵������ ������	
*   ����        : ������ ����ǰ���� ��� ó��
*   �������  : Golf
*   �ۼ�����  : 2009-08-24
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.admin.member;

import java.io.IOException;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.bccard.waf.core.*;
import com.bccard.waf.action.*;
import com.bccard.waf.common.*;
import com.bccard.waf.tao.*; 

import com.bccard.golf.common.ResultException;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoConnection;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.admin.member.GolfAdmGiftRegDaoProc;

import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.msg.MsgEtt;

/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0
******************************************************************************/
public class GolfAdmGiftRegActn extends GolfActn  {
	
	public static final String TITLE = "������ ���̹��Ӵ� ���� ��� ó��";
	
	/***************************************************************************************
	* �񾾰��� ���μ���
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws BaseException {
		
		DbTaoConnection con = null;

		ResultException rx;

	//	debug("==== GolfAdmGiftRegActn start ===");
		
		try {
			RequestParser	parser	= context.getRequestParser("default", request, response);
			//1. �Ķ��Ÿ �� 
			String search_yn	= parser.getParameter("search_yn", "N");					// �˻�����
			String mode			= parser.getParameter("mode", "ins");						// ó������
			
			String search_clss	= "";
			String search_word	= "";
			
			if("Y".equals(search_yn)){
				search_clss		= parser.getParameter("search_clss");						// �˻�����
				search_word		= parser.getParameter("search_word");						// �˻���
			}
			long page_no		= parser.getLongParameter("page_no", 1L);				// ��������ȣ 
			long page_size		= parser.getLongParameter("page_size", 10L);			// ����������¼�
			
			String cdhd_id					= parser.getParameter("cdhd_id"); 
			String rcvr_nm					= parser.getParameter("rcvr_nm"); 
			String golf_tmnl_gds_code		= parser.getParameter("golf_tmnl_gds_code"); 
			String hp_ddd_no				= parser.getParameter("hp_ddd_no");
			String hp_tel_hno				= parser.getParameter("hp_tel_hno"); 
			String hp_tel_sno				= parser.getParameter("hp_tel_sno");
			String zp1						= parser.getParameter("zp1"); 	
			String zp2						= parser.getParameter("zp2"); 
			String zp = zp1+zp2;
			String addr						= parser.getParameter("addr"); 	
			String dtl_addr					= parser.getParameter("dtl_addr");
			String addr_clss				= parser.getParameter("addr_clss"); //�ּұ���(��:1, ��:2)
			String memo_ctnt				= parser.getParameter("memo_ctnt"); 	
			String snd_yn					= parser.getParameter("snd_yn"); 
			String p_idx					= parser.getParameter("p_idx", "");
			String acrg_cdhd_jonn_date		= parser.getParameter("acrg_cdhd_jonn_date", "");
			
			//2.��ȸ
			DbTaoDataSet input = new DbTaoDataSet(TITLE);
			input.setString("search_yn",	search_yn);
			if("Y".equals(search_yn)){
				input.setString("search_clss",	search_clss);
				input.setString("search_word",	search_word);
			}
			input.setString("mode",						mode);
			input.setString("cdhd_id",					cdhd_id);
			input.setString("rcvr_nm",					rcvr_nm);
			input.setString("golf_tmnl_gds_code",		golf_tmnl_gds_code);
			input.setString("hp_ddd_no",				hp_ddd_no);
			input.setString("hp_tel_hno",				hp_tel_hno);
			input.setString("hp_tel_sno",				hp_tel_sno);
			input.setString("zp",						zp);
			input.setString("addr",						addr);
			input.setString("dtl_addr",					dtl_addr);
			input.setString("addr_clss",				addr_clss);
			input.setString("memo_ctnt",				memo_ctnt);
			input.setString("snd_yn",					snd_yn);
			input.setLong("page_no",					page_no);
			input.setLong("page_size",					page_size);
			input.setString("p_idx",					p_idx);
			input.setString("acrg_cdhd_jonn_date",		acrg_cdhd_jonn_date);
			
			//debug("snd_yn:::::::::::::::::::::::::::"+snd_yn);
			
			Map paramMap = parser.getParameterMap();	
			
			// 3. DB ó�� 
			GolfAdmGiftRegDaoProc proc = (GolfAdmGiftRegDaoProc)context.getProc("GolfAdmGiftRegDaoProc");
			DbTaoResult giftInq = (DbTaoResult)proc.execute(context, request, input);
				
			request.setAttribute("giftInq", giftInq);	
			request.setAttribute("paramMap", paramMap);
			
		//	debug("==== GolfAdmGiftRegActn end ===");
			
		}catch(Throwable t) {
			return errorHandler(context,request,response,t);
		}finally{
			try{ if(con  != null) con.close();  }catch( Exception ignored){}
		}
		return super.getActionResponse(context);
		
	}
}
