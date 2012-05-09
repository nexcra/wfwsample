/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfMemInsActn
*   �ۼ���    : �̵������ ������
*   ����      : ���� > ���
*   �������  : golf 
*   �ۼ�����  : 2009-05-19
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.member;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.member.GolfMemDelFormDaoProc;
import com.bccard.golf.dbtao.proc.member.GolfMemEvntDaoProc;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

/******************************************************************************
* Golf
* @author	�̵������
* @version	1.0 
******************************************************************************/
public class GolfMemDelFormActn extends GolfActn{
	
	public static final String TITLE = "���� > ������";

	/***************************************************************************************
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";
		String userId = "";
		String jumin_no ="";
		int intMemGrade =0;
		int intCardGrade = 0;
						
		try {
			// ȸ���������̺� ���� �������� ����
			// 01.��������üũ
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
		 	
			 if(usrEntity != null) {
				userId		= (String)usrEntity.getAccount(); 
				jumin_no		= (String)usrEntity.getSocid(); 
				intMemGrade		= (int)usrEntity.getIntMemGrade(); 
				intCardGrade	= (int)usrEntity.getIntCardGrade(); 
			}
			
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);

			dataSet.setString("evntNo", "109" );	
			dataSet.setString("juminNo", jumin_no);	

			String join_chnl = "";
			String affi_firm_nm = "";
			String cancle_able_yn = "";
			String champ_seq_no = "";

			// 04.�̺�Ʈ ���̺�(Proc) ��ȸ			
			GolfMemEvntDaoProc event = (GolfMemEvntDaoProc)context.getProc("GolfMemEvntDaoProc");
			DbTaoResult addEvent = event.execute(context, dataSet, request);
			if (addEvent != null && addEvent.isNext()) {
				addEvent.first();
				addEvent.next();
				String pwin_date = (String) addEvent.getString("PWIN_DATE");
				String end_date = (String) addEvent.getString("END_DATE");
				String to_date = (String) addEvent.getString("TO_DATE");
				
				if( Integer.parseInt(to_date) <= Integer.parseInt(end_date)) {
					cancle_able_yn = "E";
				}
			}	

			// 05.���� ���̺�(Proc) ��ȸ			
			GolfMemDelFormDaoProc proc = (GolfMemDelFormDaoProc)context.getProc("GolfMemDelFormDaoProc");
			int cnt = proc.execute(context, dataSet, request);
			
			if (cnt > 0){
				//��������
				cancle_able_yn = "Y";
			}else{
				//�����Ұ�
				cancle_able_yn = "N";
			}

			// 05.���� ���̺�(Proc) ��ȸ	- ����ǰ ������ è�Ǿ��� Ż��Ұ�
			if(intMemGrade==1){
				DbTaoResult addChampion = proc.getChamp(context, dataSet, request);
				
				if (addChampion != null && addChampion.isNext()) {
					addChampion.first();
					addChampion.next();
					champ_seq_no = (String) addChampion.getString("SEQ_NO");
					
					if(!GolfUtil.empty(champ_seq_no) && !champ_seq_no.equals("")){
						cancle_able_yn = "C";
						debug("���� ���� �Ұ� ���� : ����ǰ ����");
					}
				}
			}
			
			// ����ī��ȸ���� ��� - Ż��Ұ�
			String strMemGr = "";
			DbTaoResult cardUser = proc.getCardMem(context, dataSet, request);
			if (cardUser != null && cardUser.isNext()) {
				cardUser.first();
				cardUser.next();
				strMemGr = (String) cardUser.getString("CDHD_SQ2_CTGO");
				
				System.out.print("strMemGr:"+strMemGr); 
				
				if("0005".equals(strMemGr) || "0006".equals(strMemGr))
				{
					cancle_able_yn = "V";
					debug("���� ���� �Ұ� ���� : ����ī�� ȸ��");
				}
				
			}
			
			// TM ȸ�� ��ȭ���ű� ������ ��� - Ż��Ұ�
			int tmMovieCnt = proc.getTmMovieCnt(context, dataSet, request);
			if(tmMovieCnt>0){
				cancle_able_yn = "T";
				debug("���� ���� �Ұ� ���� : �̺�Ʈ ����ǰ ����"); 
			}
						
			debug("GolfMemDelFormActn ::: intMemGrade : " + intMemGrade + " / cancle_able_yn : " + cancle_able_yn
					+ " / join_chnl(���԰��) : " + join_chnl);			
			
			// 05. Return �� ����			
			paramMap.put("join_chnl", join_chnl);	
			paramMap.put("CANCLE_ABLE_YN", cancle_able_yn);			
	        request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
		
	}
}
