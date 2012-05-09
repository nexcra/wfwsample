/*****************************************************************************
 * Ŭ������ : NextPreLinkTag
 * �ۼ���	: ����ȣ
 * ����		: ����, ���� ��ư �±� ó��
 * ������� : bccard 
 * �ۼ����� : 2005.08.03 
********************************�����̷�***************************************
 * ����			������		������� 
 * 2005.08.03	����ȣ		����, ���� ��ư �±� ó��
 ******************************************************************************/
package com.bccard.golf.tag;

import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.waf.common.StrUtil;

/*******************************************************************************
 * ����, ���� ��ư �±� ó��
 * 
 * @author ����ȣ
 * @version 2005.08.03
 *  
 ******************************************************************************/ 
public class NextPreLinkTag extends BodyTagSupport {
	private String preOnclick;
	private String nextOnclick;
	private String preContent;
	private String nextContent;
	private DbTaoResult dbTao;

	
	/*************************************************************************
    * ���������� �Է�
    * @param nextContent ����������
    *************************************************************************/  
	public void setNextContent(String string) { this.nextContent = string; }

	/*************************************************************************
    * ����Ŭ�� �Է�
    * @param nextOnclick ����Ŭ��
    *************************************************************************/  
	public void setNextOnclick(String string) { this.nextOnclick = string; }

	/*************************************************************************
    * ���������� �Է�
    * @param preContent ����������
    *************************************************************************/  
	public void setPreContent(String string) { this.preContent = string; }

	/*************************************************************************
    * ����Ŭ�� �Է�
    * @param preOnclick ����Ŭ��
    *************************************************************************/  
	public void setPreOnclick(String string) { this.preOnclick = string; }

	/*************************************************************************
    * ���TAO �Է�
    * @param dbTao ���TAO
    *************************************************************************/  
	public void setDbTao(DbTaoResult result) { this.dbTao = result; }
	
	/*************************************************************************
    * ������ ����
    *************************************************************************/  
	public void release() {
		super.release();
		this.preOnclick = null;
		this.nextOnclick = null;
		this.preContent = null;
		this.nextContent = null;
		this.dbTao = null;
	}
	
	/*************************************************************************
    * ���ۼ���
    *************************************************************************/  
	public int doStartTag() throws JspException {
		return EVAL_BODY_BUFFERED;
	}
	
	/*************************************************************************
    * ������ ����
    *************************************************************************/  
	public int doEndTag() throws JspException {
		if ( dbTao == null ) { return EVAL_PAGE; }
		try {
			List pointer =  (List) this.dbTao.getField("pointer");
			List comm_seqno =  (List) this.dbTao.getField("comm_seqno");
			List comm_reply_ref =  (List) this.dbTao.getField("comm_reply_ref");
			List comm_title =  (List) this.dbTao.getField("comm_title");

			if ( pointer==null || pointer.size()==1 ) { return EVAL_PAGE; }

			JspWriter out = getPreviousOut();
            String src = bodyContent.getString();
			String nextStr = StrUtil.replace(StrUtil.isNull(this.nextContent, ""), "${nextOnclick}", this.nextOnclick);
			String preStr = StrUtil.replace(StrUtil.isNull(this.preContent, ""), "${preOnclick}", this.preOnclick);

			if ( "NEXT".equals((String) pointer.get(1)) ) {
				nextStr = StrUtil.replace(nextStr, "${SEQ_NO}", (String) comm_seqno.get(1));
				nextStr = StrUtil.replace(nextStr, "${REPLY_REF}", (String) comm_reply_ref.get(1));
				nextStr = StrUtil.replace(nextStr, "${TITLE}", (String) comm_title.get(1));
			} else {
                nextStr = "";
            }

			if ( "PREV".equals((String) pointer.get(1)) ) {
				preStr = StrUtil.replace(preStr, "${SEQ_NO}", (String) comm_seqno.get(1));
				preStr = StrUtil.replace(preStr, "${REPLY_REF}", (String) comm_reply_ref.get(1));
				preStr = StrUtil.replace(preStr, "${TITLE}", (String) comm_title.get(1));
			} else if ( pointer.size()==3 ) {
				preStr = StrUtil.replace(preStr, "${SEQ_NO}", (String) comm_seqno.get(2));
				preStr = StrUtil.replace(preStr, "${REPLY_REF}", (String) comm_reply_ref.get(2));
				preStr = StrUtil.replace(preStr, "${TITLE}", (String) comm_title.get(2));
			} else {
                preStr = "";
			}
            out.print( StrUtil.replace(src, "${PRE_NEXT_BODY}", nextStr+preStr) );

		} catch(Throwable t) {
			
			throw new JspException(t);
		}
		
		return EVAL_PAGE;
	}	

}
