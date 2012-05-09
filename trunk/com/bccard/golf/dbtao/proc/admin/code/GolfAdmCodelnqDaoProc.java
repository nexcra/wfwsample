/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmCodelnqDaoProc
*   �ۼ���     : (��)�̵������ ������
*   ����        : ������ �����ڵ���� ��� ��ȸ 
*   �������  : Golf
*   �ۼ�����  : 2009-05-14
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.code;

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
 * @version 2009-05-14
 **************************************************************************** */
public class GolfAdmCodelnqDaoProc extends AbstractProc {
	
	public static final String TITLE = "�����ڵ���� ��� ��ȸ";
	
	
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
		
		debug("==== GolfAdmCodelnqDaoProc start ===");
		
		GolfConfig config = GolfConfig.getInstance();
		
		try{
			//��ȸ ����
			String search_yn	= dataSet.getString("search_yn"); 		//�˻�����
			String search_clss	= "";									//�˻����
			String search_word	= "";									//�˻���
			String search_cate_seq	= "";									//�˻���

			if("Y".equals(search_yn)){
				search_clss	= dataSet.getString("search_clss"); 		// �˻���
				search_word	= dataSet.getString("search_word"); 		// ����˻�����
				search_cate_seq	= dataSet.getString("search_cate_seq"); 		// ����˻�����
			}
	//		long page_no = dataSet.getLong("page_no")==0L?1L:dataSet.getLong("page_no");
			long page_size = dataSet.getLong("page_size")==0L?10L:dataSet.getLong("page_size");
								

			String sql = this.getSelectQuery(search_yn,search_cate_seq);	
			
			con = context.getDbConnection("default", null);
			
			pstmt = con.prepareStatement(sql);
			int pidx = 0;
			
			if(!"".equals(search_cate_seq) && search_cate_seq != null)
			{
					pstmt.setString(++pidx, ""+search_cate_seq+"");
			}
//			pstmt.setLong(++pidx, page_no);
			
			rset = pstmt.executeQuery();
			
			result = new DbTaoResult(TITLE);
			boolean existsData = false;
			
			while(rset.next()){

				if(!existsData){
					result.addString("RESULT", "00");
				}
				
				//curDateFormated = DateUtil.format(rset.getString("REG_DATE"),"yyyyMMdd","yyyy/MM/dd");
				
				result.addLong("row_num",					rset.getLong("RNUM"));
				result.addString("CD_CLSS",					rset.getString("GOLF_CMMN_CLSS"));
				result.addString("CD",						rset.getString("GOLF_CMMN_CODE"));
				result.addString("CD_NM",					rset.getString("GOLF_CMMN_CODE_NM"));
				result.addString("USE_YN",					rset.getString("USE_YN"));
				result.addString("REG_MGR_SEQ_NO",			rset.getString("REG_MGR_ID"));
				result.addString("CORR_MGR_SEQ_NO",			rset.getString("CHNG_MGR_ID"));
				result.addString("REG_DATE",				rset.getString("REG_ATON"));
				result.addString("CORR_DATE",				rset.getString("CHNG_ATON"));
				result.addString("total_cnt",				rset.getString("TOT_CNT") );
		//		result.addString("curr_page",				rset.getString("PAGE") );
				
				existsData = true;
				
			}

			if(!existsData){
				result.addString("RESULT","01");
			}
		
			//debug("==== GolfAdmCodelnqDaoProc end ===");
						
		}catch ( Exception e ) {
			//debug("==== GolfAdmCodelnqDaoProc ERROR ===");
			
			//debug("==== GolfAdmCodelnqDaoProc ERROR ===");
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
	private String getSelectQuery(String search_yn,String search_cate_seq) throws Exception{

		StringBuffer sql = new StringBuffer();
	
		sql.append("\n SELECT	*								");
		sql.append("\n FROM	(SELECT	ROWNUM RNUM,				");
		sql.append("\n 				GOLF_CMMN_CLSS,					");
		sql.append("\n 				GOLF_CMMN_CODE,							");
		sql.append("\n 				GOLF_CMMN_CODE_NM,						");
		sql.append("\n 				USE_YN,						");
		sql.append("\n 				REG_MGR_ID,				");
		sql.append("\n 				CHNG_MGR_ID,			");
		sql.append("\n 				REG_ATON,					");
		sql.append("\n 				CHNG_ATON,					");
	//	sql.append("\n 				CEIL(ROWNUM/10) AS PAGE,	");
		sql.append("\n 				MAX(RNUM) OVER() TOT_CNT	");	
		sql.append("\n 		FROM	(SELECT	ROWNUM AS RNUM,		");
		sql.append("\n 						GOLF_CMMN_CLSS,			");
		sql.append("\n 						GOLF_CMMN_CODE,					");
		sql.append("\n 						GOLF_CMMN_CODE_NM,				");
		sql.append("\n 						USE_YN,				");
		sql.append("\n 						(select MGR_ID from BCDBA.TBGMGRINFO where MGR_ID=TB.REG_MGR_ID ) as REG_MGR_ID,		");
		sql.append("\n 						(select MGR_ID from BCDBA.TBGMGRINFO where MGR_ID=TB.CHNG_MGR_ID ) as CHNG_MGR_ID,		");
		sql.append("\n 						to_char(to_date(REG_ATON,'yyyymmddhh24miss'), 'yyyy/mm/dd hh24:mi:ss') AS REG_ATON,				");
		sql.append("\n 						to_char(to_date(CHNG_ATON,'yyyymmddhh24miss'), 'yyyy/mm/dd hh24:mi:ss') AS CHNG_ATON				");		
		sql.append("\n 				FROM BCDBA.TBGCMMNCODE	 TB			");
		sql.append("\n 				WHERE  1=1 						");
		if(!"".equals(search_cate_seq)){
			sql.append("\n 				AND  GOLF_URNK_CMMN_CLSS = '0000' AND GOLF_URNK_CMMN_CODE = ? 						");
		}
		sql.append("\n 				ORDER BY GOLF_CMMN_CLSS, GOLF_CMMN_CODE, SORT_SEQ ASC			");
		sql.append("\n 				)								");
		sql.append("\n 		ORDER BY RNUM 							");
		sql.append("\n 		)										");
	//	sql.append("\n WHERE PAGE = ?								");

		return sql.toString();
	}
}
