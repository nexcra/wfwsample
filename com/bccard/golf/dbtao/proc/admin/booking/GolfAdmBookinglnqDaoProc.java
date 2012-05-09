/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : TpAdmBookinglnqDaoProc
*   �ۼ���     : (��)�̵������ ������
*   ����        : ������ �Խ��ǰ��� ��� ��ȸ 
*   �������  : Golf
*   �ۼ�����  : 2009-05-11
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.booking;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.http.HttpServletRequest;

import com.bccard.waf.core.WaContext;
import com.bccard.waf.common.StrUtil;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoException;
import com.bccard.waf.tao.TaoResult; 
import com.bccard.waf.action.AbstractProc;

import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoResult;

import com.bccard.golf.common.GolfConfig;
import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.DbTaoProc;

/** ****************************************************************************
 * Media4th / Golf
 * @author
 * @version 2009-05-11 
 **************************************************************************** */
public class GolfAdmBookinglnqDaoProc extends AbstractProc {
	
	public static final String TITLE = "�Խ��� ���� ��� ��ȸ";
	
	
	/** ***********************************************************************
	* Proc ����.
	* @param con Connection
	* @param dataSet ��ȸ��������
	************************************************************************ */
	public TaoResult execute(WaContext context, HttpServletRequest request, TaoDataSet dataSet) throws DbTaoException {
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		DbTaoResult result = null;
		Connection con = null;
		
		//debug("==== GolfAdmBookinglnqDaoProc start ===");
		
		GolfConfig config = GolfConfig.getInstance();
		//String faq_Bookingseq = config.getBookingid("FAQ");
		
		try{
			//��ȸ ����
			String search_yn	= dataSet.getString("search_yn"); 		//�˻�����
			String search_clss	= "";									//�˻����
			String search_word	= "";									//�˻���
			String sdate		= "";
			String edate		= "";
			if("Y".equals(search_yn)){
				search_clss	= dataSet.getString("search_clss"); 		// �˻���
				search_word	= dataSet.getString("search_word"); 		// ����˻�����
				sdate	= dataSet.getString("sdate");
				edate	= dataSet.getString("edate");
			}
			long page_no = dataSet.getLong("page_no")==0L?1L:dataSet.getLong("page_no");
			long page_size = dataSet.getLong("page_size")==0L?10L:dataSet.getLong("page_size");
								

			String sql = this.getSelectQuery(search_yn,search_clss,sdate,edate);
		
			
			con = context.getDbConnection("default", null);
			
			pstmt = con.prepareStatement(sql);
			int pidx = 0;
			//pstmt.setString(++pidx, comm_clss);
			if("Y".equals(search_yn)){
				//pstmt.setString(++pidx, "%"+search_word+"%");
			}
			if(!"".equals(sdate))
			{
				pstmt.setString(++pidx, "%"+sdate+"%");
			}
			if(!"".equals(edate))
			{
				pstmt.setString(++pidx, "%"+edate+"%");
			}
			pstmt.setLong(++pidx, page_no);

			rset = pstmt.executeQuery();
			
			
			result = new DbTaoResult(TITLE);
			boolean existsData = false;
			
			while(rset.next()){

				if(!existsData){
					result.addString("RESULT", "00");
				}
				
				//curDateFormated = DateUtil.format(rset.getString("REG_DATE"),"yyyyMMdd","yyyy/MM/dd");
				
				result.addLong("row_num",				rset.getLong("RNUM"));
				result.addLong("BookingID",				rset.getLong("BookingID"));
				result.addString("Booking_CODE",			rset.getString("Booking_CODE"));
				result.addString("Booking_NM",			rset.getString("Booking_NM"));
				result.addString("USE_YN",				rset.getString("USE_YN"));
				result.addString("RG_SEQ_NO",			rset.getString("RG_SEQ_NO"));
				result.addString("UP_SEQ_NO",			rset.getString("UP_SEQ_NO"));
				result.addString("REG_DATE",			rset.getString("REG_DATE"));
				result.addString("MOD_DATE",			rset.getString("MOD_DATE"));
				result.addString("total_cnt",			rset.getString("TOT_CNT") );
				result.addString("curr_page",			rset.getString("PAGE") );
				
				/*
				if(faq_Bookingseq.equals(rset.getString("BookingID"))){
					result.addString("FAQ","true");
				}else{
					result.addString("FAQ", "false");
				}					
				*/
				existsData = true;
				
			}

			if(!existsData){
				result.addString("RESULT","01");
			}
		
			//debug("==== GolfAdmBookinglnqDaoProc end ===");
						
			
		}catch ( Exception e ) {
			//debug("==== GolfAdmBookinglnqDaoProc ERROR ===");
			
			//debug("==== GolfAdmBookinglnqDaoProc ERROR ===");
		}finally{
			try{ if(rset  != null) rset.close();  }catch( Exception ignored){}
			try{ if(pstmt != null) pstmt.close(); }catch( Exception ignored){}
			try{ if(con != null) con.close(); }catch( Exception ignored){}
		}
		return result;	
	}

		
	/** ***********************************************************************
	* Query�� �����Ͽ� �����Ѵ�.
	************************************************************************ */
	private String getSelectQuery(String search_yn,String search_clss,String sdate, String edate) throws Exception{

		StringBuffer sql = new StringBuffer();
	
		sql.append("\n SELECT	*																						");
		sql.append("\n FROM	(SELECT	ROWNUM RNUM,					");
		sql.append("\n 					BookingID,						");
		sql.append("\n 					Booking_CODE,					");
		sql.append("\n 					Booking_NM,						");
		sql.append("\n 					USE_YN,						");
		sql.append("\n 					RG_SEQ_NO,						");
		sql.append("\n 					UP_SEQ_NO,						");
		sql.append("\n 					REG_DATE,						");
		sql.append("\n 					MOD_DATE,						");
		sql.append("\n 					CEIL(ROWNUM/10) AS PAGE,	");
		sql.append("\n 					MAX(RNUM) OVER() TOT_CNT	");
		
		sql.append("\n 		FROM	(SELECT	ROWNUM AS RNUM,			");
		sql.append("\n 							BookingID,				");
		sql.append("\n 							Booking_CODE,			");
		sql.append("\n 							Booking_NM,				");
		sql.append("\n 							USE_YN,				");
		sql.append("\n 							(select ID from TBMGRINFO where MGR_SEQ_NO=TB.RG_SEQ_NO ) as RG_SEQ_NO,				");
		sql.append("\n 							(select ID from TBMGRINFO where MGR_SEQ_NO=TB.UP_SEQ_NO ) as UP_SEQ_NO,				");
		sql.append("\n 							REG_DATE,				");
		sql.append("\n 							MOD_DATE				");		
				
		
		sql.append("\n 				FROM BCGOLF.TBBBRDMGMT	 TB		");
		sql.append("\n 				WHERE 1=1 							");
		if("Y".equals(search_yn)){
			if("A".equals(search_clss)){
				sql.append("\n 				AND Booking_CODE  = 'A'		");
			}else if("B".equals(search_clss)){
				sql.append("\n 				AND Booking_CODE  = 'B'		");
			}else if("W".equals(search_clss)){
				sql.append("\n 				AND DEPT  like ?		");
			}else{
				
			}
		}
		if(!"".equals(sdate) && !"".equals(edate)){
			sql.append("\n 				AND ( REG_DATE >=  ?	and REG_DATE <=  ?	)	");
			
		}
		sql.append("\n 				ORDER BY REG_DATE DESC			");
		sql.append("\n 				)								");
		sql.append("\n 		ORDER BY RNUM 							");
		sql.append("\n 		)										");
		sql.append("\n WHERE PAGE = ?								");

		return sql.toString();
	}
}