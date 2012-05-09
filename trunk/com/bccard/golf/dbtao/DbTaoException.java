/******************************************************************************
* �� �ҽ��� �ߺ�ī�� �����Դϴ�.
* �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
* �ۼ� : 2007.01.04 [���뱹(ykcho@e4net.net)]
* ���� : DbTao ó���� �߻��ϴ� ����
* ���� :
* ���� :
******************************************************************************/
package com.bccard.golf.dbtao;

import java.io.Serializable;

import com.bccard.golf.msg.MsgEtt;
import com.bccard.golf.msg.MsgHandler;
import com.bccard.waf.tao.TaoException;

/** ***************************************************************************
* DbTao ó���� �߻��ϴ� ����
* @author ���뱹
* @version 2007.01.04
**************************************************************************** */
public class DbTaoException extends TaoException implements MsgHandler,Serializable {
    /** �޽������� ��ƼƼ */ private MsgEtt msgEtt;

    /** ***********************************************************************
    * �޽����� ���� ����.
    * @param s �޽���
    ************************************************************************ */
    public DbTaoException(String s, Throwable t) {
        super(s);
        super.setRootCause(t);
    }

    /** ***********************************************************************
    * �޽��������� ���� ����.
    * @param ett �޽��������� ���� MsgEtt
    ************************************************************************ */
    public DbTaoException(MsgEtt ett) {
        super(ett.getMessage());
        this.msgEtt = ett;

		super.setKey(ett.getKey());
    }

    /** ***********************************************************************
    * �޽������� �� ���� ����.
    * @param ett �޽��������� ���� MsgEtt
    ************************************************************************ */
    public DbTaoException(MsgEtt ett,Throwable t) {
        this(ett.getMessage(),t);
        this.msgEtt = ett;

		super.setKey(ett.getKey());
    }

    /** ***********************************************************************
    * �޽������� ��ƼƼ Ŭ���� ��ȯ.
    * @return �޽��������� ���� MsgEtt ��ȯ
    ************************************************************************ */
    public MsgEtt getMsgEtt() {
        if ( this.msgEtt == null ) {
            this.msgEtt = new MsgEtt();
            this.msgEtt.setType( MsgEtt.TYPE_ERROR );
            this.msgEtt.setTitle("");
            this.msgEtt.setMessage( getMessage() );
        }
        return this.msgEtt;
    }
}
