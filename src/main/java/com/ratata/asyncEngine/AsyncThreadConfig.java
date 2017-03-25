package com.ratata.asyncEngine;

public interface AsyncThreadConfig {
	public static enum AsyncThreadStatus {
		INIT, RUNNING, STOP, ERROR
	}

	public static int poolsize = 5;
}
