package com.huiminpay.common.cache.util;

import org.apache.commons.lang.StringUtils;

import java.util.regex.Pattern;

public class PaymentUtil {

	private static final Pattern pattern = Pattern.compile("HMPAY(,\\S+){4}");
	private static final String HUIMINPAY_PREFIX = "HM";

	public static boolean checkPayOrderAttach(String attach) {
		if (StringUtils.isBlank(attach)) {
			return false;
		}
		return pattern.matcher(attach).matches();
	}

	public static String genUniquePayOrderNo() {
		return HUIMINPAY_PREFIX + WorkId();
	}

	public static String WorkId() {
		long id = IdWorkerUtils.getInstance().nextId();
		return String.valueOf(id);
	}

}
