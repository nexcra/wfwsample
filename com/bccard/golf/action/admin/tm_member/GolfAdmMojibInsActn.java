/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmMojibInsActn.java
*   작성자    : E4NET 은장선
*   내용      : 모집인 가입처리
*   적용범위  : Golf
*   작성일자  : 2009-09-03
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.admin.tm_member;

import java.io.IOException;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.initech.dbprotector.CipherClient;
import com.bccard.waf.action.AbstractAction;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.AbstractEntity;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext; 
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;

import com.bccard.waf.common.DateUtil;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.proc.admin.tm_member.GolfAdmMojibProc;
/******************************************************************************
* Golf
* @author	
* @version	1.0
******************************************************************************/
public class GolfAdmMojibInsActn extends GolfActn {
	
	public static final String TITLE = "모집인 가입처리"; 
	/***************************************************************************************
	* 비씨골프 관리자로그인 프로세스
	* @param context		WaContext 객체. 
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
	***************************************************************************************/
	 
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {
		
		DbTaoResult taoResult = null;
		DbTaoResult taoPartner = null;
		
		String subpage_key = "default";
		
		try {
			RequestParser	parser	= context.getRequestParser("default", request, response);			
			Map paramMap = parser.getParameterMap();	
		

			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);			
			
			
			String acpt_chnl_clss		= parser.getParameter("acpt_chnl_clss","2");
			String rcru_pl_clss			= parser.getParameter("rcru_pl_clss","");
			String tb_rslt_clss			= parser.getParameter("tb_rslt_clss","99");
			
			
			String hg_nm				= parser.getParameter("hg_nm","");
			String jumin_no1			= parser.getParameter("jumin_no1","");
			String jumin_no2			= parser.getParameter("jumin_no2","");
			String hp_ddd_no			= parser.getParameter("hp_ddd_no","");
			String hp_tel_hno			= parser.getParameter("hp_tel_hno","");
			String hp_tel_sno			= parser.getParameter("hp_tel_sno","");
			String hom_zp1				= parser.getParameter("hom_zp1","");
			String hom_zp2				= parser.getParameter("hom_zp2","");
			String zipaddr				= parser.getParameter("zipaddr","");
			String detailaddr			= parser.getParameter("detailaddr","");
			String addr_clss 			= parser.getParameter("addr_clss"); //주소구분(구:1, 신:2)
			String co_nm				= parser.getParameter("co_nm","");
			String wkpl_ddd_no			= parser.getParameter("wkpl_ddd_no","");
			String wkpl_tel_hno			= parser.getParameter("wkpl_tel_hno","");
			String wkpl_tel_sno			= parser.getParameter("wkpl_tel_sno","");
			String eng_nm				= parser.getParameter("eng_nm","");
			String bthd1				= parser.getParameter("bthd1","");
			String bthd2				= parser.getParameter("bthd2","");
			String bthd3				= parser.getParameter("bthd3","");
			String scal_lcal_clss		= parser.getParameter("scal_lcal_clss","");
			String hom_ddd_no			= parser.getParameter("hom_ddd_no","");
			String hom_tel_hno			= parser.getParameter("hom_tel_hno","");
			String hom_tel_sno			= parser.getParameter("hom_tel_sno","");
			String jobtl_nm				= parser.getParameter("jobtl_nm","");
			String email_id				= parser.getParameter("email_id","");
			String card_no1				= parser.getParameter("card_no1","");
			String card_no2				= parser.getParameter("card_no2","");
			String card_no3				= parser.getParameter("card_no3","");
			String card_no4				= parser.getParameter("card_no4","");
			String golf_cdhd_grd_clss	= parser.getParameter("golf_cdhd_grd_clss","");
			String join_chnl			= parser.getParameter("join_chnl","");
			String conc_date			= parser.getParameter("conc_date","");
			String emp_no				= parser.getParameter("emp_no","");
			String acpt_pl_chg_nm		= parser.getParameter("acpt_pl_chg_nm","");
			String auth_clss            = parser.getParameter("auth_clss","");

			String vald_lim1			= parser.getParameter("vald_lim1","");
			String vald_lim2            = parser.getParameter("vald_lim2","");
			String dc_amt				= parser.getParameter("dc_amt","0");
			String disc_clss            = parser.getParameter("disc_clss","0");


			String jumin_no				= jumin_no1 + jumin_no2;
			String hom_zp				= hom_zp1 + hom_zp2;
			String hom_dong_blw_addr	= zipaddr + "|" + detailaddr;
			String btdt					= bthd1 + bthd2 + bthd3;
			String card_no				= card_no1 + card_no2 + card_no3 + card_no4;

			String vald_lim				= "";
			if ("".equals(vald_lim1) || "".equals(vald_lim2) )
			{
				vald_lim = "";
			}else {
				vald_lim= "20"+vald_lim2+vald_lim1;
			}


//파라미터값 제대로 전송확인
debug("hg_nm >>>>>>>>>>>>>" + hg_nm);
debug("jumin_no >>>>>>>>>>>>>" + jumin_no);
debug("hp_ddd_no >>>>>>>>>>>>>" + hp_ddd_no + hp_tel_hno + hp_tel_sno);
debug("hom_zp >>>>>>>>>>>>>" + hom_zp);
debug("hom_dong_blw_addr >>>>>>>>>>>>>" + hom_dong_blw_addr);
debug("co_nm >>>>>>>>>>>>>" + co_nm);
debug("wkpl_ddd_no >>>>>>>>>>>>>" + wkpl_ddd_no + wkpl_tel_hno + wkpl_tel_sno);
debug("eng_nm >>>>>>>>>>>>>" + eng_nm);
debug("btdt >>>>>>>>>>>>>" + btdt);
debug("scal_lcal_clss >>>>>>>>>>>>>" + scal_lcal_clss);
debug("hom_ddd_no >>>>>>>>>>>>>" + hom_ddd_no + hom_tel_hno + hom_tel_sno);
debug("jobtl_nm >>>>>>>>>>>>>" + jobtl_nm);
debug("email_id >>>>>>>>>>>>>" + email_id);
debug("card_no >>>>>>>>>>>>>" + card_no);
debug("join_chnl >>>>>>>>>>>>>" + join_chnl);
debug("conc_date >>>>>>>>>>>>>" + golf_cdhd_grd_clss);
debug("emp_no >>>>>>>>>>>>>" + emp_no);
debug("acpt_pl_chg_nm >>>>>>>>>>>>>" + acpt_pl_chg_nm);
debug("auth_clss >>>>>>>>>>>>>" + auth_clss);
debug("vald_lim >>>>>>>>>>>>>" + vald_lim);
debug("dc_amt >>>>>>>>>>>>>" + dc_amt);
debug("disc_clss >>>>>>>>>>>>>" + disc_clss);

//여기까지

			dataSet.setString("acpt_chnl_clss",acpt_chnl_clss);
			dataSet.setString("rcru_pl_clss",rcru_pl_clss);
			dataSet.setString("tb_rslt_clss",tb_rslt_clss);
			
			dataSet.setString("hg_nm",hg_nm);
			dataSet.setString("jumin_no",jumin_no);
			dataSet.setString("hp_ddd_no",hp_ddd_no);
			dataSet.setString("hp_tel_hno",hp_tel_hno);
			dataSet.setString("hp_tel_sno",hp_tel_sno);
			dataSet.setString("hom_zp",hom_zp);
			dataSet.setString("hom_dong_blw_addr",hom_dong_blw_addr);			
			dataSet.setString("addr_clss", addr_clss);
			dataSet.setString("co_nm",co_nm);
			dataSet.setString("wkpl_ddd_no",wkpl_ddd_no);
			dataSet.setString("wkpl_tel_hno",wkpl_tel_hno);
			dataSet.setString("wkpl_tel_sno",wkpl_tel_sno);
			dataSet.setString("eng_nm",eng_nm);
			dataSet.setString("btdt",btdt);
			dataSet.setString("scal_lcal_clss",scal_lcal_clss);
			dataSet.setString("hom_ddd_no",hom_ddd_no);
			dataSet.setString("hom_tel_hno",hom_tel_hno);
			dataSet.setString("hom_tel_sno",hom_tel_sno);
			dataSet.setString("jobtl_nm",jobtl_nm);
			dataSet.setString("email_id",email_id);
			dataSet.setString("card_no",card_no);
			dataSet.setString("join_chnl",join_chnl);
			dataSet.setString("conc_date",conc_date);
			dataSet.setString("emp_no",emp_no);
			dataSet.setString("acpt_pl_chg_nm",acpt_pl_chg_nm);
			dataSet.setString("auth_clss",auth_clss);
			dataSet.setString("golf_cdhd_grd_clss",golf_cdhd_grd_clss);
			
			dataSet.setString("vald_lim",vald_lim);
			dataSet.setString("dc_amt", dc_amt);
			dataSet.setString("disc_clss",disc_clss);



			//제휴사등록일경우엔 제휴사등록 화면으로 이동
			String action_key = super.getActionKey(context);
			debug(action_key);

			GolfAdmMojibProc proc = new GolfAdmMojibProc();
			
			taoPartner = (DbTaoResult)proc.execute_partner(context, dataSet);

			request.setAttribute("taoPartner", taoPartner);

			if (taoPartner != null ) {
				taoPartner.next();
				debug(taoPartner.getString("RESULT"));

				if ("01".equals(taoPartner.getString("RESULT").trim()	))
				{
					subpage_key="error"; //요걸 다른 페이지로 한번 "기존에 자료가 있습니다~" 라는 메세지 처리
				} else
				{
					taoResult = (DbTaoResult)proc.execute(context, dataSet);		

				}

			}

			
		}catch(Throwable t) {
			return errorHandler(context,request,response,t);
		}
		
		return getActionResponse(context, subpage_key);
		
	}
}
