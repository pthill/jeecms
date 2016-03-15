package com.jeecms.common.web.session.id;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

/** 通过UUID生成SESSION ID */
public class JugUUIDGenerator implements SessionIdGenerator {
	//	public String get() {
	//		UUID uuid = UUIDGenerator.getInstance().generateRandomBasedUUID();
	//		return StringUtils.remove(uuid.toString(), '-');
	//	}
	//
	//	public static void main(String[] args) {
	//		UUIDGenerator.getInstance().generateRandomBasedUUID();
	//		long time = System.currentTimeMillis();
	//		for (int i = 0; i < 100000; i++) {
	//			UUIDGenerator.getInstance().generateRandomBasedUUID();
	//		}
	//		time = System.currentTimeMillis() - time;
	//		System.out.println(time);
	//	}

	public String get() {
		return StringUtils.remove(UUID.randomUUID().toString(), '-');
	}

	public static void main(String[] args) {
		UUID.randomUUID();
		long time = System.currentTimeMillis();
		for (int i = 0; i < 100000; i++) {
			UUID.randomUUID();
		}
		time = System.currentTimeMillis() - time;
		System.out.println(time);
	}

}
