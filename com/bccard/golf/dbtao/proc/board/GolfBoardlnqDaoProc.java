/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfBoardlnqDaoProc
*   �ۼ���     : (��)�̵������ �ǿ���
*   ����        : �Խ��� ��� ��ȸ 
*   �������  : Golf
*   �ۼ�����  : 2009-04-01
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.board;

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

import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.DbTaoProc;

/** ****************************************************************************
 * Media4th / Golf
 * @author
 * @version 2009-04-01
 **************************************************************************** */
public class GolfBoardlnqDaoProc extends AbstractProc {

	
	public static final String TITLE = "�Խ��� ��� ��ȸ";
	
	/** ***********************************************************************
	* Proc ����.
	* @param con Connection
	* @param dataSet ��ȸ��������
	************************************************************************ */
	public TaoResult execute(WaContext context, HttpServletRequest request, TaoDataSet dataSet) throws DbTaoException {
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		ResultSet rset_count = null;
		DbTaoResult result = null;
		Connection con = null;
		
		//debug("==== GolfBoardlnqDaoProc Start :"+TITLE+" ===");
		
		try{
			//��ȸ ����
			String search_yn				= dataSet.getString("search_yn"); 			//�˻�����
			String search_clss			= "";													//�˻����
			String search_word			= "";													//�˻���
			String sdate					= "";
			String edate					= "";
			if("Y".equals(search_yn)){
				search_clss	= dataSet.getString("search_clss"); 					// �˻���
				search_word	= dataSet.getString("search_word"); 					// ����˻�����
				sdate	= dataSet.getString("sdate");
				edate	= dataSet.getString("edate");
			}
			long page_no 				= dataSet.getLong("page_no")==0L?1L:dataSet.getLong("page_no");
			long page_size 				= dataSet.getLong("page_size")==0L?10L:dataSet.getLong("page_size");
			String boardid					= dataSet.getString("boardid"); 				//�Խ��ǹ�ȣ						

			//debug("search_yn:"+search_yn);
			
			String sql = this.getSelectQuery(search_yn,search_clss,sdate,edate);
		
			
			con = context.getDbConnection("default", null);
			
			pstmt = con.prepareStatement(sql);
			int pidx = 0;
			pstmt.setString(++pidx, boardid);
			if("Y".equals(search_yn)){
				pstmt.setString(++pidx, "%"+search_word+"%");
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
			
			
			//��ü ���� ���ؿ���
			int cntRec = 0;
			String sWhere = " AND  EPS_YN = 'Y' AND  DEL_YN = 'N' ";
			pstmt = con.prepareStatement(this.getCountQuery(sWhere));
			pidx = 0;
			pstmt.setString(++pidx, boardid);
			rset_count = pstmt.executeQuery();
			
			if(rset_count.next()){
				cntRec = rset_count.getInt("cnt");
			}
			
			
			int serial = (int) (cntRec - (page_size * (page_no-1)));
			
			
			while(rset.next()){

				if(!existsData){
					result.addString("RESULT", "00");
				}
							
				result.addLong("row_num",				rset.getLong("RNUM"));				
				result.addInt("serial", serial--);
				result.addLong("seq_no",					rset.getLong("SEQ_NO"));
				result.addString("boardid",				rset.getString("BOARDID"));
				result.addString("subject",				rset.getString("SUBJECT"));
				result.addString("point",					rset.getString("POINT"));
				result.addString("account",				rset.getString("ACCOUNT"));
				result.addString("name",					rset.getString("NAME"));
				result.addString("reg_dtime",			rset.getString("REG_DTIME"));
				result.addString("reg_ip",					rset.getString("REG_IP"));
				result.addString("read_cnt",				rset.getString("READ_CNT"));
				result.addString("total_cnt",				rset.getString("TOT_CNT") );
				result.addString("curr_page",			rset.getString("PAGE") );
				existsData = true;
				
			}

			if(!existsData){
				result.addString("RESULT","01");
			}
			//debug("==== GolfBoardlnqDaoProc end ===");
						
			
		}catch ( Exception e ) {
			//debug("==== GolfBoardlnqDaoProc ERROR ===");
			e.printStackTrace();
			//debug("==== GolfBoardlnqDaoProc ERROR ===");
		}finally{
			try { if(rset != null) {rset.close();} else{} } catch (Exception ignored) {}
			try { if(rset_count != null) {rset_count.close();} else{} } catch (Exception ignored) {}
			try { if(pstmt != null) {pstmt.close();} else{} } catch (Exception ignored) {}
			try { if(con != null) {con.close();} else{} } catch (Exception ignored) {}
		}
		return result;	
	}	
	
	
	/** ***********************************************************************
	* Query�� �����Ͽ� �����Ѵ�.
	************************************************************************ */
	private String getSelectQuery( String search_yn,String search_clss,String sdate, String edate) throws Exception{

		StringBuffer sql = new StringBuffer();
	
		sql.append("\n SELECT	*													");
		sql.append("\n FROM	(SELECT	ROWNUM RNUM,			");
		sql.append("\n 					SEQ_NO,									");
		sql.append("\n 					BOARDID,									");
		sql.append("\n 					SUBJECT,									");
		sql.append("\n 					POINT,										");
		sql.append("\n 					ACCOUNT,								");
		sql.append("\n 					NAME,										");
		sql.append("\n 					REG_DTIME,								");
		sql.append("\n 					REG_IP,										");
		sql.append("\n 					READ_CNT,								");		
		sql.append("\n 					CEIL(ROWNUM/10) AS PAGE,	    ");
		sql.append("\n 					MAX(RNUM) OVER() TOT_CNT	");		
		sql.append("\n 		FROM	(SELECT	ROWNUM AS RNUM,	");
		sql.append("\n 							SEQ_NO,							");
		sql.append("\n 							BOARDID,							");
		sql.append("\n 							SUBJECT	,						");
		sql.append("\n 							POINT,								");
		sql.append("\n 							ACCOUNT,						");
		sql.append("\n 							NAME,								");
		sql.append("\n 							REG_DTIME,						");
		sql.append("\n 							REG_IP,								");		
		sql.append("\n 							READ_CNT						");		
		sql.append("\n 					FROM TBBBRD	 TB				");
		sql.append("\n 					WHERE BOARDID = ?					");
		sql.append("\n 					AND  EPS_YN = 'Y' 					");
		sql.append("\n 					AND  DEL_YN = 'N' 					");
		if("Y".equals(search_yn)){
			if("T".equals(search_clss)){
				sql.append("\n 				AND SUBJECT  like ?				");
			}else if("C".equals(search_clss)){
				sql.append("\n 				AND ACCOUNT  like ?				");
			}else if("W".equals(search_clss)){
				sql.append("\n 				AND NAME  like ?					");
			}else{
				sql.append("\n 				AND SUBJECT  like ?				");
			}
		}
		if(!"".equals(sdate) && !"".equals(edate)){
			sql.append("\n 				AND ( REG_DTIME >=  ?	and REG_DTIME <=  ?	)	");
			
		}
		sql.append("\n 				ORDER BY REG_DTIME DESC			");
		sql.append("\n 				)								");
		sql.append("\n 		ORDER BY RNUM 							");
		sql.append("\n 		)										");
		sql.append("\n WHERE PAGE = ?								");

		return sql.toString();
	}
	
	
	/** ***********************************************************************
	* Query�� �����Ͽ� �����Ѵ�.
	************************************************************************ */
	private String getCountQuery( String sWhere ) throws Exception{		
		StringBuffer sQuery = new StringBuffer();// ski season query modify & ..!!	
		sQuery.append("\n select count(*) as cnt from TBBBRD where boardid = ?");
		sQuery.append("\n " + sWhere);
		return sQuery.toString();
	}
	
	
}
