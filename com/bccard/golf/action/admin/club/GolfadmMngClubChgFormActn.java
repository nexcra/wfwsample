/***************************************************************************************************
*   戚 社什澗 ∂搾松朝球 社政脊艦陥.
*   戚 社什研 巷舘生稽 亀遂馬檎 狛拭 魚虞 坦忽聖 閤聖 呪 赤柔艦陥.
*   適掘什誤  : GolfAdmMngClubChgFormActn
*   拙失切    : (爽)幻室朕溝艦追戚芝 沿昔謂
*   鎧遂      : 淫軒切 > 穿端 疑硲噺 淫軒 呪舛廿
*   旋遂骨是  : golf
*   拙失析切  : 2009-07-06
************************** 呪舛戚径 ****************************************************************
*    析切      獄穿   拙失切   痕井紫牌
*
***************************************************************************************************/
package com.bccard.golf.action.admin.club;

import java.io.IOException; 
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.admin.club.GolfAdmMngClubUpdFormDaoProc;
//import com.bccard.golf.dbtao.proc.mania.GolfManiaCodeSelDaoProc;

/******************************************************************************
* Topn
* @author	(爽)幻室朕溝艦追戚芝
* @version	1.0
******************************************************************************/
public class GolfadmMngClubChgFormActn extends GolfActn{
	
	public static final String TITLE = "淫軒切 > 疑硲噺 鯵竺切 呪舛廿";

	/***************************************************************************************
	* 搾松転匂昔闘 淫軒切鉢檎bb
	* @param context		WaContext 梓端. 
	* @param request		HttpServletRequest 梓端. 
	* @param response		HttpServletResponse 梓端. 
	* @return ActionResponse	Action 坦軒板 鉢檎拭 巨什巴傾戚拝 舛左. 
	***************************************************************************************/
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		
		// 00.傾戚焼数 URL 煽舌
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);
 
		try {
			// 01.室芝舛左端滴
			
			// 02.脊径葵 繕噺	 	
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);

			// Request 葵 煽舌
			long seq_no			= parser.getLongParameter("p_idx", 0);
			
			//debug("lessonずししししししししししししししししInq.size() ::> " + seq_no);
			
			// 03.Proc 拭 揮霜 葵 室特 (Proc拭 dataSet 莫殿税 壕伸(?)稽 request葵 暁澗 繕噺葵聖 揮遭陥.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setLong("RECV_NO", seq_no);
			
			// 04.叔薦 砺戚鷺(Proc) 繕噺
			GolfAdmMngClubUpdFormDaoProc proc = (GolfAdmMngClubUpdFormDaoProc)context.getProc("GolfAdmMngClubUpdFormDaoProc");
			//軒巷遭紺 榎衝淫軒 蓄窒
			//GolfManiaCodeSelDaoProc coopCpSelProc = (GolfManiaCodeSelDaoProc)context.getProc("GolfManiaCodeSelDaoProc");
			
			// 軒巷遭拝昔重短覗稽益轡 覗稽益轡 雌室繕噺 ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
			DbTaoResult maniaInq = proc.execute(context, dataSet);
			//DbTaoResult coopCpSel = coopCpSelProc.execute(context, dataSet, "0012", "Y"); //託曽坪球
			
			// 05. Return 葵 室特			
			//debug("maniaInq.size() ::> " + maniaInq.size());
			
			request.setAttribute("maniaInqResult", maniaInq);	
			//request.setAttribute("coopCpSel", coopCpSel);	
	        request.setAttribute("paramMap", paramMap); //乞窮 督虞耕斗葵聖 己拭 眼焼 鋼発廃陥.			
			
		} catch(Throwable t) {
			//debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
