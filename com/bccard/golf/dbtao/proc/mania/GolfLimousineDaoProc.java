/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmCodeSelDaoProc
*   �ۼ���    : (��)����Ŀ�´����̼� ���¿�
*   ����      : ������ �ڵ� ����Ʈ �ڽ� ����
*   �������  : golf
*   �ۼ�����  : 2009-05-19
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.mania;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

/******************************************************************************
 * Topn
 * @author	����Ŀ�´����̼�
 * @version	1.0
 ******************************************************************************/
public class GolfLimousineDaoProc extends AbstractProc {

	public static final String TITLE = "������ �ڵ� ����Ʈ �ڽ� ����";
	
	/** *****************************************************************
	 * GolfAdmCodeSelDaoProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */
	public GolfLimousineDaoProc() {}	

	/**
	 * Proc ����.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */ 
	public DbTaoResult execute(WaContext context, TaoDataSet data) throws BaseException { 
		
		ResultSet rs = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(TITLE);

		try {
			con = context.getDbConnection("default", null);
			 
			//��ȸ ----------------------------------------------------------
			StringBuffer sql = new StringBuffer();
			
			sql.append("\n SELECT	*    					");
			sql.append("\n FROM 	BCDBA.TBGAMTMGMT		");
			//sql.append("\n WHERE GOLF_URNK_CMMN_CODE =?	");
			//if (use_yn != null && !use_yn.equals("")) { sql.append("\n AND USE_YN = ? ");	}							
			
			
			// �Է°� (INPUT)         
			int idx = 0;
			pstmt = con.prepareStatement(sql.toString());
			//pstmt.setString(++idx, Code);
			//if (use_yn != null && !use_yn.equals("")) {  pstmt.setString(++idx, use_yn);	}
			
			rs = pstmt.executeQuery();
			
			if(rs != null) {			 

				while(rs.next())  {	
					result.addString("NM" ,rs.getString("CAR_KND_NM") );
					result.addString("CD" ,rs.getString("CAR_KND_CLSS") );
					result.addString("PRICE" ,rs.getString("NORM_PRIC") );
					result.addString("PRICE2" ,rs.getString("PCT20_DC_PRIC") );
					result.addString("PRICE3" ,rs.getString("PCT30_DC_PRIC") );
					
					result.addString("RESULT", "00"); //������
				}
			}

			if(result.size() < 1) {
				result.addString("RESULT", "01");			
			}
			 
		} catch (Throwable t) {
			throw new BaseException(t);
		} finally {
			try { if(rs != null) {rs.close();} else{} } catch (Exception ignored) {}
			try { if(pstmt != null) {pstmt.close();} else{} } catch (Exception ignored) {}
			try { if(con != null) {con.close();} else{} } catch (Exception ignored) {}

		}

		return result;
	}
	
	
	public DbTaoResult execute(WaContext context, TaoDataSet data , String Column) throws BaseException { 
		ResultSet rs = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(TITLE);
		
		try{
			con = context.getDbConnection("default", null);
			String sql = this.getSelectQuery(Column);
			pstmt = con.prepareStatement(sql);
			
			rs = pstmt.executeQuery();
			boolean eos = false;
			
			while(rs.next()){
				if(!eos) {
					result.addString("RESULT", "00");
				}
				result.addString("NM" ,rs.getString("CAR_KND_NM") );
				result.addString("CD" ,rs.getString("CAR_KND_CLSS") );
				result.addString("PRICE" ,rs.getString("PRICE") );
				
				eos = true;
			}
			 
			if(!eos){
				result.addString("RESULT","01");
			}
			
			
		}catch (Throwable t) {
			throw new BaseException(t);
		} finally {
			try { if(rs != null) {rs.close();} else{} } catch (Exception ignored) {}
			try { if(pstmt != null) {pstmt.close();} else{} } catch (Exception ignored) {}
			try { if(con != null) {con.close();} else{} } catch (Exception ignored) {}

		}
		
		
		
		return result;
		
	}
	 
	/** ***********************************************************************
	    * Query�� �����Ͽ� �����Ѵ�.     
	************************************************************************ */
	    private String getSelectQuery(String Column){
	        StringBuffer sql = new StringBuffer();
	        
	        sql.append("\n   SELECT   CAR_KND_NM  						");
	        sql.append("\n 			  ,CAR_KND_CLSS						");
	        sql.append("\n 			  ,"+Column+" AS PRICE				");
	        sql.append("\n	 FROM 	BCDBA.TBGAMTMGMT					");
	        
	    
	        return sql.toString();
	    }
}
