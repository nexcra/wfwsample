/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������	: GolfEvntAlpensiaRegActn
*   �ۼ���	: (��)�̵������ ������
*   ����		: �̺�Ʈ > ����þ� > ��ŷ ��û ������
*   �������	: Golf
*   �ۼ�����	: 2010-06-24
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.event.alpensia;

import java.io.IOException;
import java.sql.ResultSet;
import java.util.Map;

import javax.servlet.ServletException;
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
import com.bccard.golf.dbtao.proc.event.alpensia.GolfEvntAlpensiaRegDaoProc;

import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfConfig;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.msg.MsgEtt;

/******************************************************************************
* Golf
* @author	(��)�̵������ 
* @version	1.0 
******************************************************************************/
public class GolfEvntAlpensiaRegActn extends GolfActn {
	
	public static final String TITLE = "�̺�Ʈ > ����þ� > ��ŷ ��û";
	
	/***************************************************************************************
	* �񾾰��� ���μ���
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
		
		try {
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			
			String script = "";
			int cntJumin = 0;
			int cntHp = 0;
			int evtBnstReg = 0;		// ����Է� ���

			String golf_svc_aplc_clss			= parser.getParameter("golf_svc_aplc_clss", "");	// ��ŷ ����
			String bkg_pe_nm					= parser.getParameter("bkg_pe_nm", "");				// ������ �̸�
			String hp_ddd_no					= parser.getParameter("hp_ddd_no","");				// �ڵ���
			String hp_tel_hno					= parser.getParameter("hp_tel_hno","");				// �ڵ���
			String hp_tel_sno					= parser.getParameter("hp_tel_sno","");				// �ڵ���
			String hadc_num						= parser.getParameter("hadc_num","");				// �ڵ�ĸ��
			String trm_unt						= parser.getParameter("trm_unt","");				// 1:1��2��, 2:2��3��
			String cdhd_id						= parser.getParameter("cdhd_id","");				// ������ ���̵�
			String cdhd_grd_seq_no				= parser.getParameter("cdhd_grd_seq_no","");		// �ο���	(1:1�� ��û, 2:��ü ��û)
			String rsvt_date					= parser.getParameter("rsvt_date","");				// ������
			String rsv_time						= parser.getParameter("rsv_time","");				// ����ð�
			rsvt_date = GolfUtil.replace(rsvt_date, "-", "");
			String cus_rmrk						= parser.getParameter("cus_rmrk","");				// ����û����
			cus_rmrk = GolfUtil.replace(cus_rmrk, "* ��ŷ ��û�� �߰� ��û ���� ���� �ٶ��ϴ�.", "");

			String jumin_no1					= parser.getParameter("jumin_no1", "");				// �ֹε�Ϲ�ȣ
			String jumin_no2					= parser.getParameter("jumin_no2","");				// �ֹε�Ϲ�ȣ
			String ddd_no						= parser.getParameter("ddd_no","");					// ��ȭ��ȣ
			String tel_hno						= parser.getParameter("tel_hno","");				// ��ȭ��ȣ
			String tel_sno						= parser.getParameter("tel_sno","");				// ��ȭ��ȣ
			String email						= parser.getParameter("email","");					// �̸���
			int sel_pnum						= parser.getIntParameter("sel_pnum",0);				// �ο���
			int pnum							= parser.getIntParameter("pnum",0);					// �ο���
			int tnum							= parser.getIntParameter("tnum",0);					// ����
			if(cdhd_grd_seq_no.equals("1"))	tnum = 1;												// �ο��� 1�̸� 1������ ����.
			String opt_yn						= parser.getParameter("opt_yn","");					// ���ұ���-�ɼǻ�뱸���ڵ� Y:2��1�� N:4��1��
			String compn_opt_yn					= "";					// ������ - ���ұ���-�ɼǻ�뱸���ڵ� Y:2��1�� N:4��1��
			String compn_bkg_pe_nm				= "";					// ������ - �̸�
			String compn_hp_ddd_no				= "";					// ������ - ����ó
			String compn_hp_tel_hno				= "";					// ������ - ����ó
			String compn_hp_tel_sno				= "";					// ������ - ����ó
			for(int i=0; i<tnum; i++){
				compn_opt_yn += "||" + parser.getParameter("compn_opt_yn"+i,"");
				for(int j=0; j<pnum; j++){
					compn_bkg_pe_nm += "||" + parser.getParameter("compn"+i+"_bkg_pe_nm"+j,"");
					compn_hp_ddd_no += "||" + parser.getParameter("compn"+i+"_hp_ddd_no"+j,"");
					compn_hp_tel_hno += "||" + parser.getParameter("compn"+i+"_hp_tel_hno"+j,"");
					compn_hp_tel_sno += "||" + parser.getParameter("compn"+i+"_hp_tel_sno"+j,"");
				}
			}
			
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("golf_svc_aplc_clss", golf_svc_aplc_clss);
			dataSet.setString("bkg_pe_nm", bkg_pe_nm);
			dataSet.setString("trm_unt", trm_unt);
			dataSet.setString("cdhd_id", cdhd_id);
			dataSet.setString("cdhd_grd_seq_no", cdhd_grd_seq_no);
			dataSet.setString("hp_ddd_no", hp_ddd_no);
			dataSet.setString("hp_tel_hno", hp_tel_hno);
			dataSet.setString("hp_tel_sno", hp_tel_sno);
			dataSet.setString("hadc_num", hadc_num);
			dataSet.setString("rsvt_date", rsvt_date);
			dataSet.setString("rsv_time", rsv_time);
			dataSet.setString("cus_rmrk", cus_rmrk);

			dataSet.setString("jumin_no", jumin_no1+jumin_no2);
			dataSet.setString("ddd_no", ddd_no);
			dataSet.setString("tel_hno", tel_hno);
			dataSet.setString("tel_sno", tel_sno);
			dataSet.setString("email", email);
			dataSet.setInt("pnum", pnum);
			dataSet.setInt("tnum", tnum);
			dataSet.setString("opt_yn", opt_yn);
			dataSet.setString("compn_opt_yn", compn_opt_yn);
			dataSet.setString("compn_bkg_pe_nm", compn_bkg_pe_nm);
			dataSet.setString("compn_hp_ddd_no", compn_hp_ddd_no);
			dataSet.setString("compn_hp_tel_hno", compn_hp_tel_hno);
			dataSet.setString("compn_hp_tel_sno", compn_hp_tel_sno);
			
			
			// 04.���� ���̺�(Proc) ��ȸ
			GolfEvntAlpensiaRegDaoProc proc = (GolfEvntAlpensiaRegDaoProc)context.getProc("GolfEvntAlpensiaRegDaoProc");
//			cntJumin = (int) proc.execute_jumin(context, request, dataSet);
//			cntHp = (int) proc.execute_hp(context, request, dataSet);
			
			if(cntJumin>0){ 
				script = "alert('������ �ֹε�Ϲ�ȣ�� ��û������ �ֽ��ϴ�.'); history.back();";
			}else if(cntHp>0){
				script = "alert('������ �ڵ��� ��ȣ�� ��û������ �ֽ��ϴ�.'); history.back();";
			}else{
				evtBnstReg = (int) proc.execute(context, request, dataSet);
				

				if(evtBnstReg>0){
					script = "alert('��û����� ó���Ǿ����ϴ�.'); parent.location.reload();";
				}else{
					script = "alert('��û����� ó������ �ʾҽ��ϴ�. �ٽ� �õ��� �ֽñ� �ٶ��ϴ�.');";
				}
			}
			
			
			request.setAttribute("script", script);
			
			paramMap.remove("cdhd_grd_seq_no");
			paramMap.remove("sel_pnum");
			paramMap.remove("agree_yn");
	        request.setAttribute("paramMap", paramMap);

		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 

		return super.getActionResponse(context, subpage_key);
		
	}
}