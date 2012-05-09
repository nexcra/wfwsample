/******************************************************************************
* �� �ҽ��� �ߺ�ī�� �����Դϴ�.
* �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
* ���� : ����Ʈ ��� ��ȸ ���μ���
* ���� : 
* ���� : �ش������� ����Ʈ ����� ��ȸ�Ѵ�..
******************************************************************************/

package com.bccard.golf.dbtao.proc.admin.booking.premium;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.SmsSendProc;

import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.DbTaoProc;

import com.bccard.golf.msg.MsgEtt;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.common.StrUtil;
import com.bccard.waf.core.WaContext;

import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoResult;
import com.bccard.waf.tao.TaoException;




/** ****************************************************************************
 * ����Ʈ ��� ��ȸ ���� ���μ���.
 * @author ������
 * @version 2004.10.31
 **************************************************************************** */
public class BookingPointLstInqProc extends DbTaoProc {
    public static final String TITLE = "����Ʈ���� �����ȸ";

    /** ***********************************************************************
    * Proc ����.
    * @param con Connection
    * @param dataSet ��ȸ��������
    ************************************************************************ */
    public TaoResult execute(Connection con, TaoDataSet dataSet) throws TaoException {
        String MESSAGE_KEY = "CommonProc_0000";
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        DbTaoResult result = null;

        try{
            //��ȸ ���� Validation
			String roundDateFrom = dataSet.getString("roundDateFrom");
			String roundDateTo = dataSet.getString("roundDateTo");
			String pointDetlCd = dataSet.getString("pointDetlCd");
			long pageNo = dataSet.getLong("pageNo");
			long recordsInPage = dataSet.getLong("recordsInPage");

			if ("".equals(roundDateFrom)){
                MESSAGE_KEY = "BookingProc_0001";
                throw new Exception(MESSAGE_KEY);
            }
			if ("".equals(roundDateTo)){
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
			if(pointDetlCd!=null && !"".equals(pointDetlCd.trim())) pstmt.setString(++pidx, pointDetlCd);
			pstmt.setString(++pidx, roundDateFrom);
			pstmt.setString(++pidx, roundDateTo);
			if(pointDetlCd!=null && !"".equals(pointDetlCd.trim())) pstmt.setString(++pidx, pointDetlCd);
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
                result.addString("roundDate", rset.getString("round_date"));
	//			result.addString("roundDateFmt", super.getDateTimeFmt(rset.getString("round_date"), "yyyyMMdd", "yyyy.MM.dd"));
				result.addString("name", rset.getString("name"));
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
    * Query�� �����Ͽ� �����Ѵ�.    
    ************************************************************************ */
    private String getSelectQuery(TaoDataSet dataSet) throws Exception{
		String pointDetlCd		= dataSet.getString("pointDetlCd");
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("\n");
		sql.append("SELECT *").append("\n");
		sql.append("FROM (").append("\n");
		sql.append("	SELECT x.*, y.record_cnt, ROWNUM row_num").append("\n");
		sql.append("	FROM (").append("\n");
		sql.append("		SELECT a.memid, a.seq_no, a.point_clss, a.point_detl_cd, ").append("\n");
		sql.append("		  a.point_memo, a.round_date, b.name ").append("\n");
		sql.append("		FROM bcdba.tbgfpoint a, bcdba.ucusrinfo b ").append("\n");
		sql.append("		WHERE a.round_date >= ? ").append("\n");
        sql.append("		AND a.round_date <= ? ").append("\n");
        if(pointDetlCd!=null && !"".equals(pointDetlCd.trim())){
			sql.append("		AND a.point_detl_cd = ? ").append("\n");
		}
		sql.append("		AND a.point_clss = '50' ").append("\n");
		sql.append("		AND a.memid = b.memid(+) ").append("\n");
		sql.append("		ORDER BY a.round_date DESC ").append("\n");
		sql.append("	) x, (").append("\n");
		sql.append("		SELECT COUNT(*) record_cnt").append("\n");
		sql.append("		FROM bcdba.tbgfpoint a ").append("\n");
		sql.append("		WHERE a.round_date >= ? ").append("\n");
        sql.append("		AND a.round_date <= ? ").append("\n");
		if(pointDetlCd!=null && !"".equals(pointDetlCd.trim())){
			sql.append("		AND a.point_detl_cd = ? ").append("\n");
		}
		sql.append("		AND a.point_clss = '50' ").append("\n");
		sql.append("	) y").append("\n");
		sql.append("	WHERE ROWNUM <= ?").append("\n");
		sql.append(")").append("\n");
		sql.append("WHERE row_num >= ?").append("\n");
		sql.append("AND row_num <= ?").append("\n");
        return sql.toString();
    }
}