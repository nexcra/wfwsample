/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfFieldListActn
*   �ۼ���    : (��)����Ŀ�´����̼� ����ȯ
*   ����      : ���������� �ȳ�  ����Ʈ
*   �������  : Golf
*   �ۼ�����  : 2009-06-03
************************** �����̷� ****************************************************************
*    ����     �ۼ���   �������
*  20110304  �̰���   [http://www.bccard.com/-"Home > VIP���� > ���� > ���� ������ �ȳ� �� �����ߴ��� �α� ���
***************************************************************************************************/
package com.bccard.golf.action.lounge;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.admin.code.GolfAdmCodeSelDaoProc;
import com.bccard.golf.dbtao.proc.lounge.GolfFieldListDaoProc;

/******************************************************************************
* Golf
* @author	(��)����Ŀ�´����̼�
* @version	1.0
******************************************************************************/
public class GolfFieldListActn extends GolfActn{
	
	public static final String TITLE = " ���������� �ȳ� ����Ʈ";

	/***************************************************************************************
	* ���� ������ȭ��
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/
	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		String gf_nm = "";
		String hole_cd = "";
		String area_cd = "";
		boolean flag1 = false;
		boolean flag2 = false;
		
		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);
		
		try {
			// 01.��������üũ
			
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			paramMap.put("imgUrlPath", AppConfig.getAppProperty("IMG_URL_REAL")+"/lounge");
			
			paramMap.remove("s_gf_hole_cd");
			paramMap.remove("s_gf_area_cd");

			// Request �� ����
			long page_no		= parser.getLongParameter("page_no", 1L);			// ��������ȣ
			long record_size	= parser.getLongParameter("record_size", 10);		// ����������¼�
			String search_sel		= parser.getParameter("search_sel", "");
			String search_word		= parser.getParameter("search_word", "");
			
			String search_cd		= parser.getParameter("search_cd", "1"); // �˻� ����
			String gf_clss_cd		= parser.getParameter("s_gf_clss_cd", ""); // ����
			String[] gf_hole_cd		= parser.getParameterValues("s_gf_hole_cd", "");  // Ȧ��
			String[] gf_area_cd		= parser.getParameterValues("s_gf_area_cd", "");  // ������
			String grnfee_mb		= parser.getParameter("s_grnfee_mb", ""); // �׸��� ȸ���з�
			String grnfee_wk		= parser.getParameter("s_grnfee_wk", ""); // �׸��� ����
			String grnfee_wkend		= parser.getParameter("s_grnfee_wkend", ""); // �׸��� �ָ�
			long grnfee_amt1		= parser.getLongParameter("s_grnfee_amt1", 0L); // �׸��� ���1
			long grnfee_amt2		= parser.getLongParameter("s_grnfee_amt2", 0L); // �׸��� ���2
			String gf_nm1		= parser.getParameter("s_gf_nm1", ""); //�������(�󼼰˻�)
			
			String sido		= parser.getParameter("s_sido", ""); //����
			String gugun		= parser.getParameter("s_gugun", ""); //��������
			String dong		= parser.getParameter("s_dong", ""); //������
			String gf_nm2		= parser.getParameter("s_gf_nm2", ""); //�������(�����˻�)			
			
			String inBc = request.getAttribute("actnKey").toString();
			
			//[ http://www.bccard.com/->VIP����/����/���� ������ȳ� ]���� ���ӽ�
			if (inBc.substring(inBc.length()- 4, inBc.length()).equals("InBC")){	
				debug("## "+this.getClass().getName()+" | 'http://www.bccard.com/->VIP����/����/���� ������ȳ� '���� ���� " );
			}			
			
			if (search_cd.equals("1")) { // �󼼰˻�
				gf_nm = gf_nm1;
			} else if (search_cd.equals("2")) { // �����˻�
				gf_nm = gf_nm2;
			}
			
			//debug("gf_nm ===>"+ gf_nm);
			
			for (int i = 0; i < gf_hole_cd.length; i++) { 		
				if (gf_hole_cd[i] != null && gf_hole_cd[i].length() > 0) {
					hole_cd += ","+ gf_hole_cd[i];
					flag1 = true;
				}
			}
			
			for (int i = 0; i < gf_area_cd.length; i++) { 		
				if (gf_area_cd[i] != null && gf_area_cd[i].length() > 0) {
					area_cd += ","+ gf_area_cd[i];
					flag2 = true;
				}
			}
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setLong("PAGE_NO", page_no);
			dataSet.setLong("RECORD_SIZE", record_size);
			dataSet.setString("SEARCH_SEL", search_sel);
			dataSet.setString("SEARCH_WORD", search_word);
			
			dataSet.setString("SEARCH_CD", (GolfUtil.isNull(search_cd) ? "1" : search_cd));
			dataSet.setString("GF_CLSS_CD", gf_clss_cd);
			dataSet.setString("GRNFEE_MB", grnfee_mb);
			dataSet.setString("GRNFEE_WK", grnfee_wk);
			dataSet.setString("GRNFEE_WKEND", grnfee_wkend);
			dataSet.setLong("GRNFEE_AMT1", grnfee_amt1);
			dataSet.setLong("GRNFEE_AMT2", grnfee_amt2);
			dataSet.setString("SIDO", sido);
			dataSet.setString("GUGUN", gugun);
			dataSet.setString("DONG", dong);
			dataSet.setString("GF_NM", gf_nm);
			
			
			// 04.���� ���̺�(Proc) ��ȸ
			
			GolfFieldListDaoProc proc = (GolfFieldListDaoProc)context.getProc("GolfFieldListDaoProc");
			GolfAdmCodeSelDaoProc coopCpSelProc = (GolfAdmCodeSelDaoProc)context.getProc("GolfAdmCodeSelDaoProc");
			
			DbTaoResult golffieldListResult = (DbTaoResult) proc.execute(context, request, dataSet, gf_hole_cd, gf_area_cd);
			
			// �ڵ� ��ȸ ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
			DbTaoResult coopCpSel1 = coopCpSelProc.execute(context, dataSet, "0019", "Y"); //�����屸���ڵ�
			DbTaoResult coopCpSel2 = coopCpSelProc.execute(context, dataSet, "0020", "Y"); //������Ȧ���ڵ�
			DbTaoResult coopCpSel3 = coopCpSelProc.execute(context, dataSet, "0021", "Y"); //�����������ڵ�
			
			
			// ��ü 0��  [ 0/0 page] ���� ��������
			long totalRecord = 0L;
			long currPage = 0L;
			long totalPage = 0L;
			
			if (golffieldListResult != null && golffieldListResult.isNext()) {
				golffieldListResult.first();
				golffieldListResult.next();
				if (golffieldListResult.getObject("RESULT").equals("00")) {
					totalRecord = Long.parseLong((String)golffieldListResult.getString("TOTAL_CNT"));
					currPage = Long.parseLong((String)golffieldListResult.getString("CURR_PAGE"));
					totalPage = (totalRecord % record_size == 0) ? (totalRecord / record_size) : (totalRecord / record_size)+1;
				}
			}
			
			if (gf_clss_cd.equals("")) paramMap.put("s_gf_clss_cd", "0001");
			
			paramMap.put("totalRecord", String.valueOf(totalRecord));
			paramMap.put("currPage", String.valueOf(currPage));
			paramMap.put("totalPage", String.valueOf(totalPage));
			paramMap.put("resultSize", String.valueOf(golffieldListResult.size()));
			paramMap.put("gf_area_cd", (gf_area_cd.length == 1 ? gf_area_cd[0] : ""));
			
			request.setAttribute("golffieldListResult", golffieldListResult);
			request.setAttribute("record_size", String.valueOf(record_size));
			request.setAttribute("coopCpSel1", coopCpSel1);
			request.setAttribute("coopCpSel2", coopCpSel2);
			request.setAttribute("coopCpSel3", coopCpSel3);
	        request.setAttribute("paramMap", paramMap);
	        request.setAttribute("HoleCd", (flag1 ? hole_cd.substring(1,hole_cd.length()) : ""));
	        request.setAttribute("AreaCd", (flag2 ? area_cd.substring(1,area_cd.length()) : ""));
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
