/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : BookingCalActn
*   �ۼ���     : SHIN CHEONG GWI
*   ����        : ��ŷ����� �޷���ȸ
*   �������  : Golf Loung
*   �ۼ�����  : 2010-11-03
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*   2010.11.19   shin cheong gwi  �������� : ���õ� ��¥�� ������ ���ε� �� �ʱⰪ���� ���� �Ǵ� ���� ����
***************************************************************************************************/
package com.bccard.golf.action.admin.booking.premium;

import java.io.IOException;
import java.util.Calendar;
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
import com.bccard.golf.dbtao.proc.admin.booking.GolfadmBkTimeRegFormDaoProc;
import com.bccard.golf.dbtao.proc.admin.booking.premium.GolfAdmTopGolfBkCalDaoProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoException;
import com.bccard.waf.tao.TaoResult;

public class GolfadmTopGolfBookingCalActn extends GolfActn { 

	public static final String TITLE = "������ TOP��ŷ����� �޷���ȸ";
	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws BaseException, IOException, ServletException 
	{
		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);
		String viewType = "default";
		int calendarElements[] = {0, 0, 31}; // ���� ���� �� �ε���, �޷� - ���� �� ����, �޷� - ���� ��������
		int v_yyyy = 0;
		int v_mm = 0;
		String v_yyyymm = "";
		String ca = null;	
		
		try
		{
			// 01.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(viewType, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			
			String p_yyyymm = parser.getParameter("yyyymm", "");		// ���ó��
			String p_sort	= parser.getParameter("sort", "1000"); 		//0001:�����̾�  0002:��3��ŷ , 1000:TOP����
			String p_grs_yn = parser.getParameter("PGRS_YN", "");		// �������
			String p_green_nm = parser.getParameter("GREEN_NM", "");	// �������
			
			//String today = DateUtil.currdate("yyyy/MM/dd '('E')'");
			String todayformat = DateUtil.currdate("yyyyMMdd");
			String v_year= todayformat.substring(0,4);
			String v_month=todayformat.substring(4,6);
			debug("��¥=====================>"+p_yyyymm);
			if(!p_yyyymm.equals("")){
				v_yyyy = Integer.parseInt(p_yyyymm.substring(0, 4));
				v_mm = Integer.parseInt(p_yyyymm.substring(4, 6));
				v_yyyymm = p_yyyymm;
			}else{
				v_yyyy = Integer.parseInt(v_year);
				v_mm = Integer.parseInt(v_month);
				v_yyyymm = v_year + v_month;
			}			
						
			// 02.Proc �� ���� �� ���� 
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
				dataSet.setString("yyyymm", v_yyyymm);	
				dataSet.setString("SORT", p_sort);
				dataSet.setString("PGRS_YN", p_grs_yn);
				dataSet.setString("GREEN_NM", p_green_nm);
				
			// 03.Proc ����
						
			// ������  ��ȸ	
			GolfadmBkTimeRegFormDaoProc proc2 = (GolfadmBkTimeRegFormDaoProc)context.getProc("admBkTimeRegFormDaoProc");
			DbTaoResult titimeGreenList = (DbTaoResult) proc2.execute(context, request, dataSet);
			request.setAttribute("TitimeGreenList", titimeGreenList);	
			
			// ��ŷ���� ��ȸ
			GolfAdmTopGolfBkCalDaoProc instance = GolfAdmTopGolfBkCalDaoProc.getInstance();
			DbTaoResult bookingList = instance.execute(context, request, dataSet);	
						
			calendarElements = this.getCalendarElements(v_yyyy, v_mm);							
			ca = this.setCalendar(bookingList, calendarElements, v_yyyymm, bookingList.size());			
			
			// 04. paramMap ��
			paramMap.put("ca", ca); 
			paramMap.put("sort", p_sort);
			paramMap.put("PGRS_YN", p_grs_yn);
			paramMap.put("GREEN_NM", p_green_nm);
			paramMap.put("yyyymm", v_yyyymm);
			request.setAttribute("paramMap", paramMap);
			
		}catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, viewType);
	}
	
	
	//��ŷ����  �޷� ��� 
	public String setCalendar(TaoResult taoResult, int[] calendarElements, String searchDate, int list_size) throws TaoException
	{		 
		StringBuffer sql01 = new StringBuffer();
		
		List teof_date_list = (List)taoResult.getField("TEOF_DATE");		// ��ŷ��¥
		List teof_time_list = (List)taoResult.getField("TEOF_TIME");		// ��ŷ�ð�
		List green_nm_list = (List)taoResult.getField("GREEN_NM");			// ������
		List pgrs_yn_list = (List)taoResult.getField("PGRS_YN");			// �������
		List co_nm_list = (List)taoResult.getField("CO_NM");				// �̸�
		List seq_no_list = (List)taoResult.getField("APLC_SEQ_NO");			// ��û��ȣ
		List cdhd_id_list = (List)taoResult.getField("CDHD_ID");			// ȸ�� ���̵�	
		List cdhd_clss_list = (List)taoResult.getField("CDHD_NON_CDHD_CLSS");					// ȸ������
		List hp_no_list = (List)taoResult.getField("HP_NO");				// ��ȭ��ȣ
		List pe_nm_list = (List)taoResult.getField("BKG_PE_NM");			// ������
		List golf_lesn_rsvt_no = (List)taoResult.getField("GOLF_LESN_RSVT_NO");
								
		int intDateSerial = calendarElements[0];
		int intWeekSerial = calendarElements[1];
		int intLastDay = calendarElements[2];

		String searchDatefmt = searchDate.substring(0,4)+"/"+ searchDate.substring(4)+"/01";
		String preDate = DateUtil.dateAdd('M',-1,searchDatefmt,"yyyy/MM/dd"); // �Ѵ���
		String nextDate = DateUtil.dateAdd('M',1,searchDatefmt,"yyyy/MM/dd"); // �Ѵ���
		preDate = preDate.substring(0,4) + preDate.substring(5,7);
		nextDate = nextDate.substring(0,4) + nextDate.substring(5,7);
	
		sql01.append("\n<table width='100%;' border='0' cellspacing='0' cellpadding='0'>")
		     .append("\n<tr align='center' bgcolor='#C2AD88'>")
			 .append("\n<td height='24' bgcolor='#BBBC56' colspan='7'><a href=\"javascript:go_pre(form1,'"+preDate+"');\"><img src='http://golf.bccard.com/golf/images/aocstp/ar_01.gif' hspace='8' border='0' align='absmiddle'></a>")
			 .append("\n<strong><font color='#FFFFFF'>")
			 .append(searchDate.substring(0,4)).append("��").append(searchDate.substring(4)).append("��")
			 .append("\n</font></strong><a href=\"javascript:go_next(form1,'"+nextDate+"');\"><img src='http://golf.bccard.com/golf/images/aocstp/ar_02.gif' hspace='8' border='0' align='absmiddle'></a></td>")
			 .append("\n</tr>")
			 .append("\n<tr>")
			 .append("\n<td height='2' colspan='7'></td>")
			 .append("\n</tr>")
			 .append("\n</table>")
			 .append("\n<table width='100%;' border=1 cellpadding=0 cellspacing=1 bgcolor=#CECFCE>")
			 .append("\n<tr height='40'>")
			 .append("\n<td width='88' bgcolor='#FAF0DE'><div align='center'>��</div></td>")
			 .append("\n<td width='88' bgcolor='#FAF0DE'><div align='center'>��</div></td>")
			 .append("\n<td width='88' bgcolor='#FAF0DE'><div align='center'>ȭ</div></td>")
			 .append("\n<td width='88' bgcolor='#FAF0DE'><div align='center'>��</div></td>")
			 .append("\n<td width='88' bgcolor='#FAF0DE'><div align='center'>��</div><!--<img src='/golf/images/m_book_th.gif' width='28' height='20'>--></td>")
			 .append("\n<td width='88' bgcolor='#FAF0DE'><div align='center'>��</div><!--<img src='/golf/images/m_book_f.gif' width='28' height='20'>--></td>")
			 .append("\n<td width='88' bgcolor='#FAF0DE'><div align='center'>��</div><!--<img src='/golf/images/m_book_sa.gif' width='28' height='20'>--></td>")
			 .append("\n</tr>");
			// .append("\n</table>")
			// .append("\n<table border=0 cellpadding=0 cellspacing=1 bgcolor=#CECFCE>");
	
		boolean start = false;	
		// ��ŷ���� ��ȸ
		GolfAdmTopGolfBkCalDaoProc instance = GolfAdmTopGolfBkCalDaoProc.getInstance();
		
		for(int i = 1, index=1; i <= intWeekSerial; i++) {
			sql01.append("\n<tr bgcolor=#FFFFFF height=90 align=center style='padding-top:2'>");
			for(int j=1; j <=7; j++) {
				if (j==1) {	//�Ͽ��� font color
					sql01.append("<td width='88' height='90' align='left' valign='top'><font color='#C22727'><strong>");					
				}else {				
					sql01.append("<td width='88' height='90' align='left' valign='top'>");
				}
				if (intDateSerial > 1) {
					intDateSerial--;
				}else {
					start = true;
				}
				if ( start == true && index <= intLastDay) {
					String key = (index < 10 )? "0" + index : String.valueOf(index);					
					sql01.append("<b>"+index+"</b><br>\n");
										
					int num = 0;
					if(list_size > 0)			// �����Ͱ� ������츸 �Ѹ�..
					{						
						for(int ii=0;ii<teof_date_list.size();ii++){
							String bk_dt = (String)teof_date_list.get(ii);			// ��ŷ��¥
							bk_dt = bk_dt.substring(6, 8);
							String pgrs_yn = (String)pgrs_yn_list.get(ii);			// ��ŷ����
							String green_nm = (String)green_nm_list.get(ii);			// ������		
							String cdhd_clss = (String)cdhd_clss_list.get(ii);		// ȸ������
							String seq_no = (String)seq_no_list.get(ii);			// ��û��ȣ
							String cdhd_id = (String)cdhd_id_list.get(ii);			// ���̵�
							String bk_tm = (String)teof_time_list.get(ii);			// ��ŷ�ð�/��
							String bk_tm_hour = bk_tm.substring(0,2);				// �ð�
							String bk_tm_minute = bk_tm.substring(2,4);				// ��
							String hp_no = (String)hp_no_list.get(ii);				// ��ȭ��ȣ
							String pe_nm = (String)pe_nm_list.get(ii);				// ������
							String co_nm = (String)co_nm_list.get(ii);				// ȸ����
							String font_color = "";
							String rsvt_no = golf_lesn_rsvt_no.get(ii) == null ? "" : (String)golf_lesn_rsvt_no.get(ii);
							
							//  ���û��º� �� ����							
							font_color = this.getBookingStatusColor(pgrs_yn); 
							  
							if(bk_dt.equals(key)){									
								sql01.append("<table width='100%' bgcolor='#FAF0DE'><tr><td>\n");
								if(!rsvt_no.equals("")){
									sql01.append("<a href=\"javascript:goJoinList('"+rsvt_no+"');\" onMouseOver=\" MM_showHideLayers('golf"+ bk_tm_hour+"��"+bk_tm_minute+"��"+cdhd_id+num+index +"','','show','surbey','','hide')\" onMouseOut=\"MM_showHideLayers('golf"+ bk_tm_hour+"��"+bk_tm_minute+"��"+cdhd_id+num+index +"','','hide','surbey','','hide');\"><font color='"+font_color+"'>"+co_nm_list.get(ii)+"</font></a>");
								}else{
									sql01.append("<a href=\"#\" onMouseOver=\" MM_showHideLayers('golf"+ bk_tm_hour+"��"+bk_tm_minute+"��"+cdhd_id+num+index +"','','show','surbey','','hide')\" onMouseOut=\"MM_showHideLayers('golf"+ bk_tm_hour+"��"+bk_tm_minute+"��"+cdhd_id+num+index +"','','hide','surbey','','hide');\"><font color='"+font_color+"'>"+co_nm_list.get(ii)+"</font></a>");
								}
								sql01.append("</td></tr></table>\n");
								
								// list_info(data, Į������¥, ȸ�����̵�, ��ȭ��ȣ, ��ŷ����, �ð�/��, ��ȣ, ������ )
								sql01.append(this.list_info(co_nm, index, cdhd_id, hp_no, pgrs_yn, bk_tm_hour+"��"+bk_tm_minute+"��", num, pe_nm));
							}
							num++;
						}						
					}					
					index++;	
					
					if(j == 1){
						sql01.append("</b></font></td>\n");
					}else{						
						sql01.append("</td>\n");
					}
				} else {
					if(j == 1){
						sql01.append("</b></font></td>\n");
					}else{
						sql01.append("</td>\n");
					}
				}				
			}
			sql01.append("</tr>");
		}
		sql01.append("</table>");
		
		return sql01.toString();	
	}
	/*
	 * ���̾�(������, Į������¥, ����ھ��̵�, ��ȭ��ȣ, ��ŷ����, ��ŷ�ð�, ��ȣ, ������ )
	 */
	public String list_info(String co_nm, int index, String id, String tel, String stat, String time, int seq, String fellowNm) throws TaoException{
		String date = Integer.toString(index);
		StringBuffer sql01 = new StringBuffer();

				sql01.append("\n<div id='golf"+ time+id+seq+index +"' style=\"position:absolute; ");
					sql01.append("left:"+100+"%px; top:"+100+"%px; width:100; height:"+120+"; ");
					sql01.append("z-index:1; visibility: hidden; overflow:auto\" ");
					sql01.append("onMouseOver=\"MM_showHideLayers('golf"+ time+id+seq+index +"','','show','surbey','','hide')\" ");
					sql01.append("onMouseOut=\"MM_showHideLayers('golf"+ time+id+seq+index +"','','hide','surbey','','hide')\"> ");
					sql01.append("\n<table width=200 height=100%  border=0 cellspacing=0 cellpadding=1 bgcolor=#CCCCFF>");
					sql01.append("\n<tr>");
					sql01.append("\n	<td style='font-family: ����; font-size: 8pt; padding-left:5'>");
					sql01.append("<br>�̸�[id] : "+ co_nm +"<br>["+id + "]<br>��ȭ��ȣ : " + tel + "&nbsp;<br>��ŷȮ���ð� : " + time + "&nbsp;<br>������ : " + fellowNm + "<br>&nbsp;");
					sql01.append("\n	</td>");
					sql01.append("\n</tr>");
					sql01.append("\n</table>");
					sql01.append("\n</div>");
		return sql01.toString();
	}


	/**
	 * �޷� ��������� ���� ����(����)�ε���, ���� �� �ε�������, ���� ������ ��
	 * @version 2004 06 07
	 * @author  PARK SUNG WOO
	 * @param   year   int
	 * @param   month  int
	 * @return  int[]
	 */
	public int[] getCalendarElements(int year, int month) {
		int[] ret = new int[]{0, 0, 31};
		Calendar sDate = Calendar.getInstance(); 
		Calendar eDate = Calendar.getInstance(); 
		sDate.set(year, month-1, 1); 
		eDate.set(year, month, 1);
		eDate.add(Calendar.DAY_OF_MONTH, -1);
		ret[0] = sDate.get(Calendar.DAY_OF_WEEK); // ���� ���� �ε��� [ ��(1) ~��(7) ] 
		ret[1] = eDate.get(Calendar.WEEK_OF_MONTH); // ���� �� �ε��� ���� 
		ret[2] = eDate.get(Calendar.DAY_OF_MONTH); // ���� ������ ��

		return ret;
	}
	/*
	 *  ��ŷ���� ���� ��
	 */
	public String getBookingStatusColor(String pgrs_yn)
	{
		String font_color = "";
		if(pgrs_yn.equals("R")){				// �����û
			font_color = "3366FF";
		}else if(pgrs_yn.equals("A")){			// �������
			font_color = "000099";
		}else if(pgrs_yn.equals("W")){			// ��ŷ���
			font_color = "FFCC33";
		}else if(pgrs_yn.equals("B")){			// ��ŷȮ��
			font_color = "FF0033";
		}else if(pgrs_yn.equals("C")){			// ��ŷ���
			font_color = "CC6600";
		}else if(pgrs_yn.equals("F")){			// ����
			font_color = "00CC00";
		}else{
			font_color = "FFFFFF";
		}
		
		return font_color;
	}
}
