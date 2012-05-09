/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfTopGolfCardRegActn
*   �ۼ���    : ������
*   ����      :  Top����ī�� ���� ��ŷ  > top���� ��ŷ ��ûó��
*   �������  : Golf
*   �ۼ�����  : 2010-10-21
************************* �����̷� ***************************************************************** 
*    ����       �ۼ���      �������
* 2011.02.11    �̰���	   ISP������� �߰�
***************************************************************************************************/
package com.bccard.golf.action.booking.premium;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.ispCert.ISPCommon;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.proc.booking.premium.GolfTopGolfCardListDaoProc;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentDaoProc;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

/******************************************************************************
* Golf
* @author	(��)�̵������ 
* @version	1.0
******************************************************************************/
public class GolfTopGolfCardRegActn extends GolfActn{
	
	public static final String TITLE = " Top����ī�� ���� ��ŷ  > top���� ��ŷ ��ûó��";

	/***************************************************************************************
	* ���� �����ȭ��
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/
	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";		
		String ispAccessYn  = "N";;
		String veriResCode = "1"; // ������� �ڵ� (1: �����ֹ��Ϸ�   3:�ֹ�������)
		
		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout"); 
		request.setAttribute("layout", layout);
		
		String memb_id ="";
		String memSocid = "";
		String checkYn = "N";
		int memNo= 0;
		String strMemChkNum = "";
		String coMemType ="" ;				//ī��
		String memName = "";
		
		String cstIP = request.getRemoteAddr(); //����IP
		String host_ip 		= java.net.InetAddress.getLocalHost().getHostAddress();
		String ispCardNo   	= "";				// ispī���ȣ
		
		// 01.��������üũ 
		UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request);
		
		if(userEtt != null){
			
			memb_id = userEtt.getAccount();				// ȸ�� ���̵�
			strMemChkNum = userEtt.getStrMemChkNum();	// 1:��ȸ�� / 4: ��ȸ�� / 5:����ȸ��
			memNo = userEtt.getMemid();
			memName = userEtt.getName();				//����				
			coMemType = userEtt.getStrCoMemType();		// 2:ȸ������(����) 6:����ī��(����)
			
			if("5".equals(strMemChkNum)){
				//if("6".equals(coMemType))
				//	memSocid = userEtt.getSocid();		//ī�����ī��(����)
					
				//else{
					memSocid = userEtt.getStrCoNum();		//����ī��(����) - ����� ��Ϲ�ȣ
				//}					
			}else{
				memSocid = userEtt.getSocid();			//-  �ֹε�Ϲ�ȣ
			}
			
		}
		
		try {
			
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			
			String script = "";

			String green_nm						= parser.getParameter("GREEN_NM", "");
			String teof_date					= parser.getParameter("TEOF_DATE", "");
			String teof_time					= parser.getParameter("TEOF_TIME","");
			String co_nm						= parser.getParameter("CO_NM","");
			String cdhd_id						= parser.getParameter("CDHD_ID","");
			String email_id						= parser.getParameter("EMAIL_ID","");
			
			
			String hp_ddd_no					= parser.getParameter("HP_DDD_NO","");
			String hp_tel_hno					= parser.getParameter("HP_TEL_HNO","");
			String hp_tel_sno					= parser.getParameter("HP_TEL_SNO","");
			String memp_expl					= parser.getParameter("MEMO_EXPL","");
			String bkg_pe_nm					= parser.getParameter("BKG_PE_NM","");
			String breach_amt					= parser.getParameter("BREACH_AMT","");
			//AFFI_GREEN_SEQ_NO
			String affi_green_seq_no			= parser.getParameter("AFFI_GREEN_SEQ_NO","");
			String golf_rsvt_curs_nm            = parser.getParameter("GOLF_RSVT_CURS_NM","");
			
			String paramater ="GREEN_NM="+green_nm+"&TEOF_DATE="+teof_date+"&TEOF_TIME="+teof_time+"&CO_NM="+co_nm+"&CDHD_ID="+cdhd_id +
							  "&EMAIL_ID="+email_id+"&HP_DDD_NO="+hp_ddd_no+"&HP_TEL_HNO="+hp_tel_hno+"&HP_TEL_SNO="+hp_tel_sno+
							  "&MEMO_EXPL="+memp_expl+"&bkg_pe_nm="+cdhd_id+"&breach_amt="+breach_amt+"&AFFI_GREEN_SEQ_NO="+affi_green_seq_no;
			
			// ISP���� üũ
			String iniplug 		= parser.getParameter("KVPpluginData", "");					// ISP ������
			HashMap kvpMap 		= null;
			String pcg         	= "";														// ����/���� ����			
			String valdlim	   	= "";														// ���� ����
			String pid 			= null;														// ���ξ��̵�
			String gubun        = "";
			
			GolfPaymentDaoProc payProc = (GolfPaymentDaoProc)context.getProc("GolfPaymentDaoProc");
			if(iniplug !=null && !"".equals(iniplug)) {
				kvpMap = payProc.getKvpParameter( iniplug );
			}	
			
			if(kvpMap != null) {
				ispAccessYn = "Y";
				pcg         = (String)kvpMap.get("PersonCorpGubun");		// ����/���� ����
				ispCardNo   = (String)kvpMap.get("CardNo");					// ispī���ȣ
				valdlim		= (String)kvpMap.get("CardExpire");				// ���� ����
				gubun 		= (String)kvpMap.get("ChCode");					// 01:����, 02:����, 03:�������, 04: �������,05:����Ʈī��
				if ("04".equals(gubun) || "03".equals(gubun)) {
					pid = (String)kvpMap.get("BizId");		 						// ����ڹ�ȣ
				} else {
					pid = (String)kvpMap.get("Pid");									// ���� �ֹι�ȣ
				}
			} else {
				ispCardNo = parser.getParameter("isp_card_no","");	// �ϳ�����ī�� ���
			}
			
			if(memSocid.equals(pid)){
				checkYn = "Y";
			}
			
//			if(memb_id.equals("bcgolf2") || memb_id.equals("altec16")){
//				checkYn = "Y";	
//			}
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("GREEN_NM", green_nm);
			dataSet.setString("TEOF_DATE", teof_date);
			dataSet.setString("TEOF_TIME", teof_time);
			dataSet.setString("CO_NM", co_nm);
			dataSet.setString("CDHD_ID", cdhd_id);
			dataSet.setString("EMAIL_ID", email_id);
			dataSet.setString("HP_DDD_NO", hp_ddd_no);  
			dataSet.setString("HP_TEL_HNO", hp_tel_hno);
			dataSet.setString("HP_TEL_SNO", hp_tel_sno);
			dataSet.setString("MEMO_EXPL", memp_expl);
			dataSet.setString("BKG_PE_NM", bkg_pe_nm);
			dataSet.setString("MEMB_ID", memb_id);
			dataSet.setString("BREACH_AMT", breach_amt);
			dataSet.setInt("MEMNO", memNo);
			dataSet.setString("GOLF_RSVT_CURS_NM", golf_rsvt_curs_nm); //�ڽ�
			
			//������ ���̺� ������SET
			dataSet.setString("GREEN_NO", affi_green_seq_no);	//������no
			dataSet.setString("CARD_NO", ispCardNo);		//ī���ȣ
			dataSet.setString("TEMP_PAY_DATE", valdlim);		//��ȿ�Ⱓ
			dataSet.setString("PAYPROC_ADMIN_NM", co_nm);		//��ȿ�Ⱓ
				
			// 04.���� ���̺�(Proc) ��ȸ
			if(checkYn.equals("Y")){
				
				GolfTopGolfCardListDaoProc proc = (GolfTopGolfCardListDaoProc)context.getProc("GolfTopGolfCardListDaoProc");

				int cntAppCnt = (int)proc.execute_appCnt(context, request, dataSet);		//ƼŸ�� ��û�ڼ�
				int cntJumin = (int) proc.execute_idsubmit(context, request, dataSet);		//ƼŸ�� �ֹε�� �ߺ� ����
				
				//cntAppCnt = 20;
					if(cntJumin > 0){ 
						script = "alert('������ ���̵��� ��û������ �ֽ��ϴ�.'); parent.location.href='GolfTopGolfCardList.do';";
						
					}else{
						if(cntAppCnt  > 20){
							
							script = "alert('��û������ ��û�ڼ��� �Ѿ����ϴ�.'); parent.location.href='GolfTopGolfCardList.do';";
						}
						else{
							int appInt = (int) proc.app_insert(context, request, dataSet);			//��û���̺� ���
							
							if(cntAppCnt == 20){
								int appEnd = (int) proc.execute_epsYn(context, request, dataSet);		//ƼŸ�� ��û����
							}
							
							if(appInt>0){
								int insertTemp = (int)proc.inputTemp(context, request, dataSet);		//������ ���̺� ������ ���
								
								if(insertTemp > 0){
									script = "alert('��ŷ����� ó���Ǿ����ϴ�.'); parent.location.href='GolfTopGolfCardList.do';";
								}else{
									script = "alert('ISP������ �Է��� �߸� �Ǿ����ϴ�'); location.href='GolfTopGolfCardSubmit.do?"+paramater + "';";
								}
							}else{
								script = "alert('��ŷ����� ó������ �ʾҽ��ϴ�. �ٽ� �õ��� �ֽñ� �ٶ��ϴ�.'); location.href='GolfTopGolfCardSubmit.do?"+paramater + "';";
							}
						}
					}
					
			}else{
				
				veriResCode = "3";
				script = "alert('���̵��  ����ī�尡 ��ġ���� �ʽ��ϴ�.'); location.href='GolfTopGolfCardSubmit.do?"+paramater + "';";
				debug("script =  "+script);
				
			}
				
			request.setAttribute("script", script);
	        request.setAttribute("paramMap", paramMap);
	        
		} catch(Throwable t) {
			
			veriResCode = "3";
			debug(TITLE, t);
			throw new GolfException(TITLE, t);
			
		} finally {
			
			if(ispAccessYn.equals("Y")){
			
				//ISP���� �α� ���
				HashMap hmap = new HashMap();
				hmap.put("ispAccessYn", ispAccessYn);
				hmap.put("veriResCode", veriResCode);
				hmap.put("title", TITLE);
				hmap.put("memName", memName);
				hmap.put("memSocid", memSocid);
				hmap.put("ispCardNo", ispCardNo);
				hmap.put("cstIP", cstIP);
				hmap.put("className", "GolfTopGolfCardRegActn");
				
				ISPCommon ispCoomon = new ISPCommon();
				ispCoomon.ispRecord(context, hmap);
			
			}

		}

		return super.getActionResponse(context, subpage_key);
		
	}
	
}
