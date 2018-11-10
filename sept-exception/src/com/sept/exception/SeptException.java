package com.sept.exception;

public class SeptException extends Exception {
	private static final long serialVersionUID = 8605483565352355562L;
	private int errcode;
	private String errtext;
	public static final String exceptionType = "";

	public SeptException() {
		this(ExceptionNames.defaultCode, "程序出现异常!");
	}

	public SeptException(Throwable cause) {
		this(ExceptionNames.defaultCode, cause == null ? "程序出现异常!" : cause.toString(), cause);
	}

	public SeptException(String errtext) {
		this(ExceptionNames.defaultCode, errtext);
	}

	public SeptException(String errtext, Throwable cause) {
		this(ExceptionNames.defaultCode, errtext, cause);
	}

	public SeptException(int errcode, String errtext) {
		super(errcode + "_" + errtext);
		this.errcode = errcode;
		this.errtext = errtext;
	}

	public SeptException(int errcode, String errtext, Throwable cause) {
		super(errcode + "_" + errtext, cause);
		this.errcode = errcode;
		this.errtext = errtext;
	}

	public String getClientDesc() {
		return this.errtext;
	}

	public int getErrorCode() {
		return this.errcode;
	}
}
