/******************************************************************************
* 이 소스는 ㈜비씨카드 소유입니다.
* 이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
* 작성 : 2004.10.31 [이훈주]
* 내용 : 패널티 목록 조회 프로세스
* 수정 : 
* 내용 : 해당조건의 포인트 목록을 조회한다..
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
 * 패널티 목록 조회 수행 프로세스.
 * @author 이훈주
 * @version 2004.10.31
 **************************************************************************** */
public class BookingPenaltyLstInqProc extends DbTaoProc {
    public static final String TITLE = "패널티관리 목록조회";

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
			String roundDateFrom	= dataSet.getString("roundDateFrom");
			String roundDateTo		= dataSet.getString("roundDateTo");
			String setFrom			= dataSet.getString("setFrom");
			String setTo			= dataSet.getString("setTo");
			long greenNo			= dataSet.getLong("greenNo");
			String pointDetlCd		= dataSet.getString("pointDetlCd");
			long pageNo				= dataSet.getLong("pageNo");
			long recordsInPage		= dataSet.getLong("recordsInPage");

			if ("".equals(roundDateFrom) || "".equals(roundDateTo)){
                MESSAGE_KEY = "BookingProc_0001";
                throw new Exception(MESSAGE_KEY);
            }
			if ("".equals(setFrom) || "".equals(setTo)){
                MESSAGE_KEY = "BookingProc_0002";
                throw new Exception(MESSAGE_KEY);
            }

			if (pageNo <= 0) pageNo = 1L;
			long startR = (pageNo-1L) * recordsInPage + 1L;
			long endR = pageNo * recordsInPage;

            result = new DbTaoResult(TITLE);
			String sql = this.getSelectQuery(dataSet);
            pstmt = con.prepareStatement(sql);
			int pidx = 0;            
			pstmt.setString(++pidx, roundDateFrom);
			pstmt.setString(++pidx, roundDateTo);
			pstmt.setString(++pidx, setFrom);
			pstmt.setString(++pidx, setTo);
			if(pointDetlCd!=null && !"".equals(pointDetlCd.trim())) pstmt.setString(++pidx, pointDetlCd);
			if(greenNo!=0L) pstmt.setLong(++pidx, greenNo);
			pstmt.setString(++pidx, roundDateFrom);
			pstmt.setString(++pidx, roundDateTo);
			pstmt.setString(++pidx, setFrom);
			pstmt.setString(++pidx, setTo);
			if(pointDetlCd!=null && !"".equals(pointDetlCd.trim())) pstmt.setString(++pidx, pointDetlCd);
			if(greenNo!=0L) pstmt.setLong(++pidx, greenNo);
			pstmt.setLong(++pidx, endR);
			pstmt.setLong(++pidx, startR);
			pstmt.setLong(++pidx, endR);
            rset = pstmt.executeQuery();
            
            boolean existsData = false;
            while(rset.next()){
                if(!existsData){
                    result.addString("result", "00");
                }
				result.addString("memId", rset.getString("memid"));
				result.addString("seqNo", rset.getString("seq_no"));
				result.addLong("pointClss", rset.getLong("point_clss"));
				result.addString("pointDetlCd", rset.getString("point_detl_cd"));
				result.addString("pointMemo", rset.getString("point_memo"));
				result.addLong("greenNo", rset.getLong("green_no"));
                result.addString("roundDate", rset.getString("round_date"));
//				result.addString("roundDateFmt", super.getDateTimeFmt(rset.getString("round_date"), "yyyyMMdd", "yyyy.MM.dd"));
				result.addString("breachCont", rset.getString("breach_cont"));
				result.addString("penaltyApplyClss", rset.getString("penalty_apply_clss"));
				result.addString("setDateFrom", rset.getString("set_from"));
//				result.addString("setDateFromFmt", super.getDateTimeFmt(rset.getString("set_from"), "yyyyMMdd", "yyyy.MM.dd"));
				result.addString("setDateTo", rset.getString("set_to"));
//				result.addString("setDateToFmt", super.getDateTimeFmt(rset.getString("set_to"), "yyyyMMdd", "yyyy.MM.dd"));
				result.addString("penaltyResnCd", rset.getString("penalty_resn_cd"));
				result.addString("name", rset.getString("name"));
				result.addString("greenNm", rset.getString("green_nm"));
				result.addString("regionClss", rset.getString("region_clss"));
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
		long greenNo		= dataSet.getLong("greenNo");
		String pointDetlCd	= dataSet.getString("pointDetlCd");
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("\n");
		sql.append("SELECT *").append("\n");
		sql.append("FROM (").append("\n");
		sql.append("	SELECT x.*, y.record_cnt, ROWNUM row_num").append("\n");
		sql.append("	FROM (").append("\n");
		sql.append("		SELECT a.memid, a.seq_no, a.point_clss, a.point_detl_cd, a.point_memo, a.green_no, a.round_date, ").append("\n");
		sql.append("		  a.breach_cont, a.penalty_apply_clss, a.set_from, a.set_to, a.penalty_resn_cd, b.name, c.green_nm , c.region_clss").append("\n");
		sql.append("		FROM bcdba.tbgfpoint a, bcdba.ucusrinfo b, bcdba.tbgfgreen c ").append("\n");
		sql.append("		WHERE a.round_date >= ? ").append("\n");
        sql.append("		AND a.round_date <= ? ").append("\n");
		sql.append("		AND (a.set_from >= ? OR a.set_to <= ?) ").append("\n");
        if(pointDetlCd!=null && !"".equals(pointDetlCd.trim())){
			sql.append("		AND a.point_detl_cd = ? ").append("\n");
		}
		if(greenNo!=0L){
			sql.append("		AND a.green_no = ? ").append("\n");
		}
		sql.append("		AND a.point_clss = '60' ").append("\n");
		sql.append("		AND a.memid = b.memid(+) ").append("\n");
		sql.append("		AND a.green_no = c.green_no(+) ").append("\n");
		sql.append("		ORDER BY a.round_date DESC ").append("\n");
		sql.append("	) x, (").append("\n");
		sql.append("		SELECT COUNT(*) record_cnt").append("\n");
		sql.append("		FROM bcdba.tbgfpoint a ").append("\n");
		sql.append("		WHERE a.round_date >= ? ").append("\n");
        sql.append("		AND a.round_date <= ? ").append("\n");
		sql.append("		AND (a.set_from >= ? OR a.set_to <= ?) ").append("\n"); 
		if(pointDetlCd!=null && !"".equals(pointDetlCd.trim())){
			sql.append("		AND a.point_detl_cd = ? ").append("\n");
		}
		if(greenNo!=0L){
			sql.append("		AND a.green_no = ? ").append("\n");
		}
		sql.append("		AND a.point_clss = '60' ").append("\n");
		sql.append("	) y").append("\n");
		sql.append("	WHERE ROWNUM <= ?").append("\n");
		sql.append(")").append("\n");
		sql.append("WHERE row_num >= ?").append("\n");
		sql.append("AND row_num <= ?").append("\n");
        return sql.toString();
    }
}