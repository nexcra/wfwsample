/******************************************************************************
* 이 소스는 ㈜비씨카드 소유입니다.
* 이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
* 작성 : 2007.01.04 [조용국(ykcho@e4net.net)]
* 내용 : dbtao 패키지 풀 리셋 액션
* 수정 :
* 내용 :
******************************************************************************/
package com.bccard.golf.dbtao;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.waf.action.AbstractAction;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.WaContext;

/** ****************************************************************************
 * DbTao 처리시 결과를 반환하기 위한 클래스.
 * @author 조용국(ykcho@e4net.net)
 * @version 2007.01.04
 **************************************************************************** */
public class DbTaoResetActn extends AbstractAction {
    public static final String TITLE = "테스트액션";

    /** ***********************************************************************
     * 데이터베이스에 연동을 제어한다.
     * @param con Connection
     * @return DbTaoConnection
     ************************************************************************ */
    public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, BaseException {
        DbTaoProcPool.reset();
        info("DbTaoProcPool 이 리셋되었습니다.");
        return executeAction(context,"DbTaoTest",request,response);
    }
}

