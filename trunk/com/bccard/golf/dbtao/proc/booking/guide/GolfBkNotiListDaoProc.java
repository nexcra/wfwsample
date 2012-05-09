/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfBkNotiListDaoProc
*   �ۼ���     : (��)�̵������ ������
*   ����        : ��ŷ > ��ŷ ���̵� > �������� ����Ʈ ó�� 
*   �������  : Golf
*   �ۼ�����  : 2009-05-16
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.booking.guide;

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
 * @version 2009-05-16
 **************************************************************************** */
public class GolfBkNotiListDaoProc extends AbstractProc {
	
	public static final String TITLE = "�������� ����Ʈ ó��";
	
	
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
		
		//debug("==== GolfAdmBoardComListDaoProc start ===");
				
		try{
			//��ȸ ����
			String search_yn	= dataSet.getString("search_yn"); 		//�˻�����
			String search_clss	= "";									//�˻����
			String search_word	= "";									//�˻���
			if("Y".equals(search_yn)){
				search_clss	= dataSet.getString("search_clss"); 		// �˻���
				search_word	= dataSet.getString("search_word"); 		// ����˻�����
			}
			long page_no = dataSet.getLong("page_no")==0L?1L:dataSet.getLong("page_no");
			
			String boardid = "0001"; 

			String sql = this.getSelectQuery(search_yn,search_clss,search_word);			
			con = context.getDbConnection("default", null);
			
			pstmt = con.prepareStatement(sql);
			int pidx = 0;
			pstmt.setLong(++pidx, page_no);
			pstmt.setString(++pidx, boardid);
			pstmt.setLong(++pidx, page_no);

			rset = pstmt.executeQuery();
			
			
			result = new DbTaoResult(TITLE);
			boolean existsData = false;

			int art_num_no= 0;
			while(rset.next()){

				if(!existsData){
					result.addString("RESULT", "00");
				}
								
				result.addLong("row_num",				rset.getLong("RNUM"));
				result.addString("SEQ_NO",				rset.getString("SEQ_NO"));
				result.addString("EPS_YN",				rset.getString("EPS_YN"));
				result.addString("TITL",				rset.getString("TITL"));
				result.addString("REG_ATON",			rset.getString("REG_ATON"));
				result.addLong("INOR_NUM",				rset.getLong("INOR_NUM"));
				result.addString("GOLF_BOKG_FAQ_CLSS",	rset.getString("GOLF_BOKG_FAQ_CLSS"));

				result.addInt("ART_NUM" 				,rset.getInt("ART_NUM")-art_num_no);
				result.addString("total_cnt",			rset.getString("TOT_CNT") );
				result.addString("curr_page",			rset.getString("PAGE") );
				
				art_num_no++;
				existsData = true;
				
			}

			if(!existsData){
				result.addString("RESULT","01");
			}
		
						
			
		}catch ( Exception e ) {
			//debug("==== GolfAdmBoardComListDaoProc ERROR ===");
			 
			//debug("==== GolfAdmBoardComListDaoProc ERROR ===");
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
	private String getSelectQuery(String search_yn,String search_clss,String search_word) throws Exception{

		StringBuffer sql = new StringBuffer();
	
		sql.append("\n SELECT	*																						");
		sql.append("\n FROM	(SELECT	ROWNUM RNUM,					");
		sql.append("\n 				SEQ_NO,							");
		sql.append("\n 				EPS_YN,							");
		sql.append("\n 				TITL,							");
		sql.append("\n 				REG_ATON,						");
		sql.append("\n 				INOR_NUM, GOLF_BOKG_FAQ_CLSS,						");
		sql.append("\n 				CEIL(ROWNUM/10) AS PAGE,		");
		sql.append("\n 				MAX(RNUM) OVER() TOT_CNT		");	
		sql.append("\n 				, ((MAX(RNUM) OVER())-(?-1)*10) AS ART_NUM  	");
		sql.append("\n 		FROM	(SELECT	ROWNUM AS RNUM,			");
		sql.append("\n 						T1.BBRD_SEQ_NO AS SEQ_NO,					");
		sql.append("\n 						(CASE T1.EPS_YN WHEN 'Y' THEN '����' ELSE '�����' END) AS EPS_YN,					");
		sql.append("\n 						T1.TITL,					");
		sql.append("\n 						TO_CHAR(TO_DATE(T1.REG_ATON),'YYYY-MM-DD') AS REG_ATON,				");
		sql.append("\n 						T1.INQR_NUM AS INOR_NUM				");

		sql.append("\n 						, (CASE T1.GOLF_BOKG_FAQ_CLSS WHEN '0001' THEN '�����̾���ŷ'			");
		sql.append("\n 							WHEN '0002' THEN '��3��ŷ' 		");
		sql.append("\n 							WHEN '0003' THEN '���ֱ׸�������' 		");
		sql.append("\n 							WHEN '0004' THEN 'Sky72�帲�ὺ'		");
		sql.append("\n 							WHEN '0005' THEN '���߱׸�������'		");
		sql.append("\n 							ELSE ''		");
		sql.append("\n 							END) AS GOLF_BOKG_FAQ_CLSS	");		
		
		sql.append("\n 				FROM BCDBA.TBGBBRD T1			");
		sql.append("\n 				JOIN BCDBA.TBGMGRINFO T2 ON T1.REG_MGR_ID=T2.MGR_ID			");
		sql.append("\n 				WHERE T1.EPS_YN='Y' AND T1.BBRD_CLSS=?		");
		if("Y".equals(search_yn)){
			if("".equals(search_clss)){
				sql.append("\n 	AND ( TITL like '%"+ search_word + "%' or CTNT like '%"+ search_word +"%')" );
			}else{
				sql.append("\n 	AND "+ search_clss +"  like '%"+ search_word +"%'");
			}
		}
		sql.append("\n 				ORDER BY BBRD_SEQ_NO DESC			");
		sql.append("\n 				)								");
		sql.append("\n 		ORDER BY RNUM 							");
		sql.append("\n 		)										");
		sql.append("\n WHERE PAGE = ?								");

		return sql.toString();
	}
}