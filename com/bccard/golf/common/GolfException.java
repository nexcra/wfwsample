/** ****************************************************************************
 * �� �ҽ��� �ߺ�ī�� �����Դϴ�.
 * �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
 * �ۼ� : csj007
 * ���� : ���漼 ���� �ý��� ����
 ************************** �����̷� *******************************************
 *    ����      ����   �ۼ���   �������
 *
 **************************************************************************** */
package com.bccard.golf.common;

import java.io.Serializable;

import com.bccard.golf.msg.MsgEtt;
import com.bccard.golf.msg.MsgHandler;
import com.bccard.waf.common.BaseException;


/******************************************************************************
* ���漼 ���հ��� �ý��� ����.
* @author csj007
* @version 2008.08.06
******************************************************************************/
public class GolfException extends BaseException implements MsgHandler,Serializable {
    /** �޽������� ��ƼƼ */ private MsgEtt msgEtt;

    /** ***********************************************************************
    * �ý��� ����.
    * @param ett �޽��������� ���� MsgEtt
    ************************************************************************ */
    public GolfException(MsgEtt ett) {
        super(ett.getMessage());
        this.msgEtt = ett;
    }

    /** ***********************************************************************
    * �޽������� �� ���� ����.
    * @param ett �޽��������� ���� MsgEtt
    ************************************************************************ */
    public GolfException(MsgEtt ett,Throwable t) {
        this(ett);
        super.setRootCause(t);
    }

    /** ***********************************************************************
     * �ý��� ����, ����� ���� ���� �б� ����
     * @param t Throwable
     ************************************************************************ */
     public GolfException(Throwable t) {
    	 this("", t);
     }
     
    /** ***********************************************************************
     * �ý��� ����, ����� ���� ���� �б� ����
     * @param title �׼� Ÿ��Ʋ
     * @param t Throwable
     ************************************************************************ */
     public GolfException(String title, Throwable t) {
 		super.setRootCause(t);
 		if ( t instanceof MsgHandler ) {
 			this.msgEtt = ((MsgHandler)t).getMsgEtt();
 			if (title == null || title.compareTo("") == 0) { this.msgEtt.setTitle(title); }
 		} else {
 			this.msgEtt = new MsgEtt( MsgEtt.TYPE_ERROR, title, "SYSTEM_ERROR", null );
 		}
     }
     
     /** ***********************************************************************
      * �ý��� ����, ����� ���� ���� �б� ����
      * @param title �׼� Ÿ��Ʋ
      * @param msgkey �޽���Ű
      * @param t Throwable
      ************************************************************************ */
      public GolfException(String title, String msgkey, Throwable t) {
  		if ( t != null ) { super.setRootCause(t); }
		this.msgEtt = new MsgEtt( MsgEtt.TYPE_ERROR, title, msgkey, null );
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