/***************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*  Ŭ������		:   TourMemberServlet
*  �� �� ��		:   TourMemberServlet
*  ��    ��		:   TourMemberServlet 
*  �������		:   golfLoung
*  �ۼ�����		:   2006.12.27
*  http://develop.golfloung.com:13300/app/golfloung/TourMemberServlet?jumin_no=
************************** �����̷� ***************************************************
* Ŀ��屸����	������		������	���泻��
* golfloung		20100517	������	���� ��� -> ��, è�ǿ� ���
****************************************************************************************/
package com.bccard.golf.action.member;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

//import com.bccard.fortify.FilterUtil;
import com.bccard.waf.common.StrUtil;
/***************************************************************************************
 * TourMemberServlet
 * @version 2006.12.27
 * @author  e4net
****************************************************************************************/
public class TourMemberServlet extends HttpServlet {

	/** */
	public void doPost(HttpServletRequest request, HttpServletResponse response) 
	throws IOException, ServletException {
		doGet(request,response);
	}

	/** */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try{
			
			boolean existsData = false;
			String userName = "";			
			String blackLevel = "0000";
			String jumin_no      = StrUtil.isNull(request.getParameter("jumin_no"),"");
			String grdNum = "";
			String join_chnl = "";

			Context envContext = (Context) new InitialContext();
			DataSource ds = (DataSource)envContext.lookup("golflngdata_web");
			con = ds.getConnection();

			response.setContentType("text/html;charset=euc-kr");
			
			response.setHeader("Cache-Control","no-store");   
			response.setHeader("Pragma","no-cache");   

			//response.setContentType("text/html;charset=KSC5601");
			//request.setCharacterEncoding("KSC5601");
			PrintWriter out = response.getWriter();

	        StringBuffer sql = new StringBuffer();			
			sql.append("\n	SELECT ROWNUM, HG_NM, PLEVEL, END_DATE, SORT_SEQ, JOIN_CHNL ");
			sql.append("\n	FROM (	");
			sql.append("\n	    SELECT a.HG_NM as HG_NM, c.CDHD_SQ2_CTGO as PLEVEL, a.JOIN_CHNL	");
			sql.append("\n	    , CASE CDHD_SQ1_CTGO WHEN '0001' THEN TO_CHAR(SYSDATE,'YYYYMMDD') ELSE ACRG_CDHD_END_DATE END END_DATE	");
			sql.append("\n	    , C.SORT_SEQ	");
			sql.append("\n	    FROM BCDBA.TBGGOLFCDHD a	");
			sql.append("\n      INNER JOIN BCDBA.TBGGOLFCDHDGRDMGMT b on a.CDHD_ID=b.CDHD_ID	");
			sql.append("\n	    INNER JOIN BCDBA.TBGGOLFCDHDCTGOMGMT c on b.CDHD_CTGO_SEQ_NO=c.CDHD_CTGO_SEQ_NO	");
			sql.append("\n	    WHERE a.JUMIN_NO=? AND NVL(a.SECE_YN, 'N')<>'Y'	");
			sql.append("\n	    AND C.CDHD_SQ2_CTGO in ('0007', '0001', '0015', '0002','0018','0019','0020','0021','0022')		");
			sql.append("\n	    ORDER BY C.SORT_SEQ		");
			sql.append("\n	) WHERE END_DATE >= TO_CHAR(SYSDATE,'yyyyMMdd')	");
			sql.append("\n	AND ROWNUM=1	");			
			
			
			if(!"".equals(jumin_no)) {
				pstmt = con.prepareStatement(sql.toString());
				pstmt.setString(1, jumin_no);
							
				rs = pstmt.executeQuery();
				
				if(rs != null) {			 
				
					if(rs.next())  {
						
						userName = rs.getString("HG_NM");
						blackLevel = rs.getString("PLEVEL");
						join_chnl = rs.getString("JOIN_CHNL");	
						existsData = true;
				
					}
				}
			} 

			out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
			out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
			out.println("<html>");
			out.println("<head>");
			out.println("	<meta http-equiv=\"Content-Type\" content=\"text/html; charset=euc-kr\" />");
			out.println("	<title></title>");
			out.println("</head>");
			out.println("<body>"); 
			
			if(existsData && ("0007".equals(blackLevel) || "0001".equals(blackLevel) || "0015".equals(blackLevel) 
					|| "0002".equals(blackLevel)|| "0018".equals(blackLevel)|| "0019".equals(blackLevel)
					|| "0020".equals(blackLevel)|| "0021".equals(blackLevel)|| "0022".equals(blackLevel))){
//
//				if("0007".equals(blackLevel)){
//					out.println( FilterUtil.getXSSFilter(userName) + "��,������Դϴ�.");
//					grdNum = "1";
//				}else if("0001".equals(blackLevel)){
//					out.println( FilterUtil.getXSSFilter(userName) + "��,è�ǿµ���Դϴ�");
//					grdNum = "2";
//				}else if("0015".equals(blackLevel)){
//					out.println( FilterUtil.getXSSFilter(userName) + "��,APT �����̾�����Դϴ�.");
//					grdNum = "4";
//				}else if("0020".equals(blackLevel)){
//					out.println( FilterUtil.getXSSFilter(userName) + "��, Smart300 ����Դϴ�.");
//					grdNum = "6";
//				}else if("0021".equals(blackLevel)){
//					out.println( FilterUtil.getXSSFilter(userName) + "��, Smart500 ����Դϴ�.");
//					grdNum = "7";
//				}else if("0022".equals(blackLevel)){
//					out.println( FilterUtil.getXSSFilter(userName) + "��, Smart1000 ����Դϴ�.");
//					grdNum = "8";
//				}
//				
//				if("1102".equals(join_chnl)){
//					out.println( FilterUtil.getXSSFilter(userName) + "��, TM���-��� ����Դϴ�.");
//					grdNum = "5";					
//				}
				
			} else {
				
				out.println("�ش��ڷᰡ �����ϴ�.");
				grdNum = "3";
				
			}
			
			out.println("<input type='hidden' id='black' value='"+grdNum+"' name='black'>");
			out.println("</body>");
			out.println("</html>");
			// 20100628  ���� : black  1: ��  2: è�ǿ�  3: ������
			
			out.close();

		} catch (Throwable t) {
			
			System.out.println("t.getStackTrace() : " + t.getStackTrace());
			
	        try{
	        	
	            if(rs != null)
	                rs.close();
	            if(pstmt != null)
	                pstmt.close();
	            if(con != null)
	                con.close();
	        }
	        catch(Throwable t1) {
	        	System.out.println("t1.getStackTrace() : " + t1.getStackTrace());
	        }			
					
		}
		
		if (rs != null) {
		    try {
		    	rs.close();
		    } catch (SQLException e) {
		    	System.out.println("rs e.getMessage() : " + e.getMessage());
		    	System.out.println("rs e.getSQLState() : " + e.getSQLState());
		    	System.out.println("rs e.getStackTrace() : " + e.getStackTrace());
		    }catch (Exception e1){
		    	System.out.println("rs e1.getStackTrace() : " + e1.getStackTrace());
		    }
		}
		
		if (pstmt != null) {
		    try {
		    	pstmt.close();
		    } catch (SQLException e) {
		    	System.out.println("pstmt e.getMessage() : " + e.getMessage());
		    	System.out.println("pstmt e.getSQLState() : " + e.getSQLState());
		    	System.out.println("pstmt e.getStackTrace() : " + e.getStackTrace());
		    }catch (Exception e1){
		    	System.out.println("pstmt e1.getStackTrace() : " + e1.getStackTrace());
		    }
		}		
		
		if (con != null) {
		    try {
		    	con.close();
		    } catch (SQLException e) {
		    	System.out.println("con e.getMessage() : " + e.getMessage());
		    	System.out.println("con e.getSQLState() : " + e.getSQLState());
		    	System.out.println("con e.getStackTrace() : " + e.getStackTrace());		      
		    }catch (Exception e1){
		    	System.out.println("con e1.getStackTrace() : " + e1.getStackTrace());
		    }
		}			

	}

}
