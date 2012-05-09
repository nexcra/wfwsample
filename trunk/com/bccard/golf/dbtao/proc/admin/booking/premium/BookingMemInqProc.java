/******************************************************************************
* 이 소스는 ㈜비씨카드 소유입니다.
* 이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
* 작성 : 2004.10.31 [이훈주]
* 내용 : 회원정보 목록 조회 프로세스
* 수정 : 
* 내용 : 해당조건의 회원정보 목록을 조회한다..
******************************************************************************/
package com.bccard.golf.dbtao.proc.admin.booking.premium;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoResult;
import com.bccard.waf.tao.TaoException;
import com.bccard.waf.common.StrUtil;

import com.bccard.golf.msg.MsgEtt;
import com.bccard.golf.dbtao.DbTaoProc;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.DbTaoException;

/** ****************************************************************************
 * 회원정보 목록 조회 수행 프로세스.
 * @author 이훈주
 * @version 2004.10.31
 **************************************************************************** */
public class BookingMemInqProc extends DbTaoProc {
    public static final String TITLE = "부킹대상 회원조회";

    /** ***********************************************************************
    * Proc 실행.
    * @param con Connection
    * @param dataSet 조회조건정보
    ************************************************************************ */
    public TaoResult execute(Connection con, TaoDataSet dataSet) throws TaoException {
        String MESSAGE_KEY = "CommonProc_0000";
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        DbTaoResult result = null;

        try{
            //조회 조건 Validation
			String name		= dataSet.getString("name");
			String account	= dataSet.getString("account");
			long pageNo		= dataSet.getLong("pageNo");
			long recordsInPage = dataSet.getLong("recordsInPage");
			
			if (pageNo <= 0) pageNo = 1L;
			long startR = (pageNo-1L) * recordsInPage + 1L;
			long endR = pageNo * recordsInPage;

            result = new DbTaoResult(TITLE);
			String sql = this.getSelectQuery(dataSet);
            pstmt = con.prepareStatement(sql);
			int pidx = 0;            
			if(name!=null && !"".equals(name.trim())) pstmt.setString(++pidx, name);
			if(account!=null && !"".equals(account.trim())) pstmt.setString(++pidx, account);
			if(name!=null && !"".equals(name.trim())) pstmt.setString(++pidx, name);
			if(account!=null && !"".equals(account.trim())) pstmt.setString(++pidx, account);
			pstmt.setLong(++pidx, endR);
			pstmt.setLong(++pidx, startR);
			pstmt.setLong(++pidx, endR);
            rset = pstmt.executeQuery();
            
            boolean existsData = false;
            while(rset.next()){
                if(!existsData){
                    result.addString("result", "00");
                }
                result.addString("name", rset.getString("name"));
				result.addString("account", rset.getString("account"));
				result.addLong("memId", rset.getLong("memid"));
				result.addString("phone", rset.getString("phone"));
				result.addString("mobile", rset.getString("mobile"));
				result.addLong("pageNo", pageNo);
				result.addLong("recordCnt", rset.getLong("record_cnt"));
                existsData = true;
			}
            
            if(!existsData){
                result.addString("result","01");
            }
        }catch(Exception e){
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, MESSAGE_KEY, null );
            throw new DbTaoException(msgEtt,e);
        }finally{
            try{ if(rset  != null) rset.close();  }catch( Exception ignored){}
            try{ if(pstmt != null) pstmt.close(); }catch( Exception ignored){}
        }
        return result;
    }

    /** ***********************************************************************
    * Query를 생성하여 리턴한다.    
    ************************************************************************ */
    private String getSelectQuery(TaoDataSet dataSet) throws Exception{
		String name		= dataSet.getString("name");
		String account	= dataSet.getString("account");
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("\n");
		sql.append("SELECT *").append("\n");
		sql.append("FROM (").append("\n");
		sql.append("	SELECT x.*, y.record_cnt, ROWNUM row_num").append("\n");
		sql.append("	FROM (").append("\n");
		sql.append("		SELECT a.name, a.account, a.memid, a.phone, a.mobile ").append("\n");
		sql.append("		FROM bcdba.ucusrinfo a ").append("\n");
		sql.append("		WHERE a.memid is not null ").append("\n");
        if(name!=null && !"".equals(name.trim())){
			sql.append("		AND a.name = ? ").append("\n");
		}
		if(account!=null && !"".equals(account.trim())){
			sql.append("		AND a.account = ? ").append("\n");
		}
		sql.append("	) x, (").append("\n");
		sql.append("		SELECT COUNT(*) record_cnt").append("\n");
		sql.append("		FROM bcdba.ucusrinfo a ").append("\n");
		sql.append("		WHERE a.memid is not null ").append("\n");
        if(name!=null && !"".equals(name.trim())){
			sql.append("		AND a.name = ? ").append("\n");
		}
		if(account!=null && !"".equals(account.trim())){
			sql.append("		AND a.account = ? ").append("\n");
		}
		sql.append("	) y").append("\n");
		sql.append("	WHERE ROWNUM <= ?").append("\n");
		sql.append(")").append("\n");
		sql.append("WHERE row_num >= ?").append("\n");
		sql.append("AND row_num <= ?").append("\n");
        return sql.toString();
    }
}