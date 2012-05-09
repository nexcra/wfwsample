/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfEvntTmMovieCpnPopActn
*   �ۼ���    : (��)�̵������ ������
*   ����      : �̺�Ʈ > TM ��ȭ ���ű� �̺�Ʈ > ������ȣ ��� �˾�
*   �������  : Golf
*   �ۼ�����  : 2010-03-19
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.event.tmMovie;

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
import com.bccard.waf.tao.TaoDataSet;

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.booking.premium.GolfBkPreTimeResultDaoProc;
import com.bccard.golf.dbtao.proc.event.GolfEvntBkWinListDaoProc;
import com.bccard.golf.dbtao.proc.event.tmMovie.GolfEvntTmMovieProc;

import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.golf.common.loginAction.SessionUtil;
/******************************************************************************
* Golf
* @author	JSEUN
* @version	1.0
******************************************************************************/
public class GolfEvntTmMovieCpnPopActn extends GolfActn{
	
	public static final String TITLE = "�̺�Ʈ > TM ��ȭ ���ű� �̺�Ʈ > ������ȣ ��� �˾�";

	/***************************************************************************************
	* ���� �����ȭ��
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
		
		String userSocid = "";
		String userNm = "";
		String userEmail = "";

		try {
			// 01.��������üũ
			HttpSession session = request.getSession(true);
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);

			 if(usrEntity != null) {
				 userSocid 	= (String)usrEntity.getSocid();
				 userNm 	= (String)usrEntity.getName();
				 userEmail 	= (String)usrEntity.getEmail1();
			 }
			
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);

			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("userSocid", userSocid);
			dataSet.setString("tm_evt_no", "119");
			dataSet.setString("email"	, userEmail);
			dataSet.setString("socid"	, userSocid);
			dataSet.setString("userNm"	, userNm);
			
			// 04.���� ���̺�(Proc) ��ȸ
			GolfEvntTmMovieProc proc_tmMovie = (GolfEvntTmMovieProc)context.getProc("GolfEvntTmMovieProc");

			// 1) ��ȭ���ű� ���޿��� Ȯ�� - 1�� �̻��ϰ��
			int useEvtCpnCnt = (int) proc_tmMovie.useEvtCpnCnt(context, request, dataSet);	
			debug("GolfmemInsActn:::useEvtCpnCnt : " + useEvtCpnCnt );		
			
			if(useEvtCpnCnt==0){
				
				// 2) �������� �ٿ�ޱ� ���� ó��
				synchronized(this) {	// ���� ���� �߻��� ���� max �� �����°� ����
					int cupnTmMovie = (int) proc_tmMovie.cupnNumber(context, request, dataSet);
				}
			}

			// ����Ʈ
			DbTaoResult cpnList = proc_tmMovie.cpnList(context, request, dataSet);
			request.setAttribute("cpnList", cpnList);				
			
			// 05. Return �� ����
	        request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.			
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
