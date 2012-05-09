/******************************************************************************
* �� �ҽ��� �ߺ�ī�� �����Դϴ�.
* �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
* �ۼ� : 2007.01.04 [���뱹(ykcho@e4net.net)]
* ���� : dbtao ��Ű�� Ǯ ���� �׼�
* ���� :
* ���� :
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
 * DbTao ó���� ����� ��ȯ�ϱ� ���� Ŭ����.
 * @author ���뱹(ykcho@e4net.net)
 * @version 2007.01.04
 **************************************************************************** */
public class DbTaoResetActn extends AbstractAction {
    public static final String TITLE = "�׽�Ʈ�׼�";

    /** ***********************************************************************
     * �����ͺ��̽��� ������ �����Ѵ�.
     * @param con Connection
     * @return DbTaoConnection
     ************************************************************************ */
    public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, BaseException {
        DbTaoProcPool.reset();
        info("DbTaoProcPool �� ���µǾ����ϴ�.");
        return executeAction(context,"DbTaoTest",request,response);
    }
}

