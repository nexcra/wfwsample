/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfadmGrUpdFormActn
*   �ۼ���    : (��)�̵������ ������
*   ����      : ������ ��ŷ ������ ���� ��
*   �������  : golf
*   �ۼ�����  : 2009-05-19
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.booking.normal;

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
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.booking.*;
import com.bccard.golf.dbtao.proc.booking.normal.*;
import com.bccard.golf.user.entity.UcusrinfoEntity;

/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0
******************************************************************************/
public class GolfBkNmActn extends GolfActn{
	
	public static final String TITLE = "��ŷ > �Ϲݺ�ŷ";
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";
		String isLogin = "";
		int intMemGrade = 0;		// ������
		int intMemberGrade = 0;		// ����� ���
		int intCardGrade = 0;		// ī�� ���
		String memb_id = "";
		int memb_point = 0;
		int gen_WKD_BOKG = 0;	// �Ϲ����ߺ�ŷȽ��
		int gen_WKE_BOKG = 0;	// �Ϲ��ָ���ŷȽ��
		int cy_MONEY = 0; 		// ���̹��Ӵ�

		String penalty = "";
		String penalty_start = "";
		String penalty_end = "";
		
		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);

		try {
			// 01.��������üũ
			UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request);
			 if(userEtt != null) {
				intMemGrade = userEtt.getIntMemGrade();
				memb_id = userEtt.getAccount();
				memb_point = userEtt.getCyberMoney();
				intMemberGrade = userEtt.getIntMemberGrade();
				intCardGrade = userEtt.getIntCardGrade();
			}

			if(memb_id != null && !"".equals(memb_id)){
				isLogin = "1";
			} else {
				isLogin = "0";
			}
			
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);

			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.) 
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			

			// 04-01. ��ŷ ���� ��ȸ
			GolfBkPenaltyDaoProc proc_penalty = (GolfBkPenaltyDaoProc)context.getProc("GolfBkPenaltyDaoProc");
			DbTaoResult penaltyView = proc_penalty.execute(context, dataSet, request);
			
			penaltyView.next();
			if(penaltyView.getString("RESULT").equals("00")){
				penalty = "Y";
				penalty_start = penaltyView.getString("BK_LIMIT_ST");
				penalty_end = penaltyView.getString("BK_LIMIT_ED");
			}else{
				penalty = "N";
			}
			paramMap.put("penalty", penalty);
			paramMap.put("penalty_start", penalty_start);
			paramMap.put("penalty_end", penalty_end);
//			debug("=`=`=`=`=`=`=`=`=GolfBkPreGrListActn ===  penalty => " + penalty + " / penalty_start : " + penalty_start + " / penalty_end : " + penalty_end);

			
			if(isLogin.equals("1")){
				// 04. ���ٱ��� ��ȸ	: �Ϲݺ�ŷ�� �������� ������ ���� => ���ٱ��� ���� ����� �������� ����
				
				// 05. ��ŷ Ƚ�� ��ȸ - ���̹� �Ӵ� ��볻���� ȸ������ �����ȴ�.
				// ����
				GolfBkBenefitTimesDaoProc proc_times = (GolfBkBenefitTimesDaoProc)context.getProc("GolfBkBenefitTimesDaoProc");
				DbTaoResult nmWkdView = proc_times.getNmWkdBenefit(context, dataSet, request);
				nmWkdView.next();
				gen_WKD_BOKG = nmWkdView.getInt("GEN_WKD_BOKG");
				cy_MONEY = nmWkdView.getInt("CY_MONEY");
				// �ָ�
				DbTaoResult nmWkeView = proc_times.getNmWkeBenefit(context, dataSet, request);
				nmWkeView.next();
				gen_WKE_BOKG = nmWkeView.getInt("GEN_WKE_BOKG");
			}
			
			paramMap.put("memb_id", memb_id);
			paramMap.put("day_cnt", gen_WKD_BOKG+"");
			paramMap.put("week_cnt", gen_WKE_BOKG+"");
			paramMap.put("memb_point", cy_MONEY+"");


			debug("ACTN : day_cnt : ���� ��ŷ ���� Ƚ�� : " + gen_WKD_BOKG+"");
			debug("ACTN : week_cnt : �ָ� ��ŷ ���� Ƚ�� : " + gen_WKE_BOKG+"");
			debug("ACTN : memb_point : ���̹��Ӵ� : " + memb_point);

			
	        request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.			
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
