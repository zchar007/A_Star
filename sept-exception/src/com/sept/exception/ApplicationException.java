package com.sept.exception;

public class ApplicationException extends SeptException {
	private static final long serialVersionUID = 8605483565352355562L;
	public static final String exceptionType = "系统";

	public ApplicationException() {
		this(ExceptionNames.defaultCode, "程序出现系统异常!");
	}

	public ApplicationException(Throwable cause) {
		this(ExceptionNames.defaultCode, cause == null ? "程序出现系统异常!" : cause.toString(), cause);
	}

	public ApplicationException(String errtext) {
		this(ExceptionNames.defaultCode, errtext);
	}

	public ApplicationException(String errtext, Throwable cause) {
		this(ExceptionNames.defaultCode, errtext, cause);
	}

	public ApplicationException(int errcode, String errtext) {
		super(errcode + "_" + errtext);
	}

	public ApplicationException(int errcode, String errtext, Throwable cause) {
		super(errcode + "_" + errtext, cause);
	}
}