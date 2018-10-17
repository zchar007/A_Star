package com.astar;

public class AStarException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AStarException(String msg) {
		super("ASTAR:" + msg);
		Alert.alertError("ASTAR:" + msg);
	}

	public AStarException(String msg, Throwable e) {	
		super("ASTAR:" + msg, e);
		Alert.alertError("ASTAR:" + msg);
	}

	public AStarException(Throwable e) {
		this(e == null ? "" : e.toString(), e);
	}

	public AStarException(String msg, int exception_type) {
		super("ASTAR:" + msg);
		if (exception_type == AStar.EXCEPTION_ERROR) {
			Alert.alertError("ASTAR:" + msg);
		} else if (exception_type == AStar.EXCEPTION_INFO) {
			Alert.alertInformation("ASTAR:" + msg);
		} else if (exception_type == AStar.EXCEPTION_WARN) {
			Alert.alertWarning("ASTAR:" + msg);
		} else if (exception_type == AStar.EXCEPTION_QUESTION) {
			Alert.alertQuestion("ASTAR:" + msg);
		}
	}

	public AStarException(String msg, Throwable e, int exception_type) {
		super("ASTAR:" + msg, e);
		if (exception_type == AStar.EXCEPTION_ERROR) {
			Alert.alertError("ASTAR:" + msg);
		} else if (exception_type == AStar.EXCEPTION_INFO) {
			Alert.alertInformation("ASTAR:" + msg);
		} else if (exception_type == AStar.EXCEPTION_WARN) {
			Alert.alertWarning("ASTAR:" + msg);
		} else if (exception_type == AStar.EXCEPTION_QUESTION) {
			Alert.alertQuestion("ASTAR:" + msg);
		}
	}

	public AStarException(Throwable e, int exception_type) {
		this(e == null ? "" : e.toString(), e, exception_type);

	}
}
