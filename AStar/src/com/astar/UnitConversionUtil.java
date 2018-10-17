package com.astar;

import java.text.DecimalFormat;

/**
 * 用于单位转换的类
 * 
 * @author zchar
 */
public final class UnitConversionUtil{
	private static final char[] CHINESE_NUMBER = { '零', '壹', '贰', '叁', '肆', '伍', '陆', '柒', '捌', '玖' };
	private static final char[] CHINESE_CODE = { '角', '分', '整' };
	private static final char[] CHINESE_CARRY = { '元', '拾', '佰', '仟', '万', '拾', '佰', '仟', '亿', '拾', '佰', '仟', '兆', '拾', '佰', '仟', '万', '拾', '佰', '仟' };

	/**
	 * 由传入的bit转成合适的单位表示 名字不太合适
	 * 
	 * @param bits
	 * @return
	 */
	@Deprecated
	public final static String BitToAnySuitableUnit(long bits) {
		long sizeTemp = bits;
		String returnStr = bits + "Bit";
		DecimalFormat format = new DecimalFormat("0.00");
		if (sizeTemp >= 1024) {
			sizeTemp = bits / 1024l;
			String xs = bits % 1024l + "";
			xs = format.format((Double.parseDouble(xs) / 1024.0d)).substring(2);
			returnStr = sizeTemp + "." + xs + "KB";
		}
		if (sizeTemp >= 1024) {
			sizeTemp = bits / (1024l * 1024l);
			String xs = bits % (1024l * 1024l) + "";
			xs = format.format((Double.parseDouble(xs) / 1024.0d / 1024.0d))
				.substring(2);
			returnStr = sizeTemp + "." + xs + "MB";
		}
		if (sizeTemp >= 1024) {
			sizeTemp = bits / (1024l * 1024l * 1024l);
			String xs = bits % (1024l * 1024l * 1024l) + "";
			xs = format.format((Double.parseDouble(xs) / 1024.0d / 1024.0d / 1024.0d))
				.substring(2);

			returnStr = sizeTemp + "." + xs + "GB";
		}
		if (sizeTemp >= 1024) {
			sizeTemp = bits / (1024l * 1024l * 1024l * 1024l);
			String xs = bits % (1024l * 1024l * 1024l * 1024l) + "";
			xs = format.format((Double.parseDouble(xs) / 1024.0d / 1024.0d / 1024.0d / 1024.0d))
				.substring(2);

			returnStr = sizeTemp + "." + xs + "TB";
		}
		if (sizeTemp >= 1024) {
			sizeTemp = bits / (1024l * 1024l * 1024l * 1024l * 1024l);
			String xs = bits % (1024l * 1024l * 1024l * 1024l * 1024l) + "";
			xs = format.format((Double.parseDouble(xs) / 1024.0d / 1024.0d
					/ 1024.0d / 1024.0d / 1024.0d)).substring(2);

			returnStr = sizeTemp + "." + xs + "PB";
		}
		return returnStr;
	}

	/**
	 * 由传入的bit转成合适的单位表示
	 * 
	 * @param bits
	 * @return
	 */
	public final static String formatBitToASUnit(long bits) {
		long sizeTemp = bits;
		String returnStr = bits + "Bit";
		DecimalFormat format = new DecimalFormat("0.00");
		if (sizeTemp >= 1024) {
			sizeTemp = bits / 1024l;
			String xs = bits % 1024l + "";
			xs = format.format((Double.parseDouble(xs) / 1024.0d)).substring(2);
			returnStr = sizeTemp + "." + xs + "KB";
		}
		if (sizeTemp >= 1024) {
			sizeTemp = bits / (1024l * 1024l);
			String xs = bits % (1024l * 1024l) + "";
			xs = format.format((Double.parseDouble(xs) / 1024.0d / 1024.0d))
				.substring(2);
			returnStr = sizeTemp + "." + xs + "MB";
		}
		if (sizeTemp >= 1024) {
			sizeTemp = bits / (1024l * 1024l * 1024l);
			String xs = bits % (1024l * 1024l * 1024l) + "";
			xs = format.format((Double.parseDouble(xs) / 1024.0d / 1024.0d / 1024.0d))
				.substring(2);

			returnStr = sizeTemp + "." + xs + "GB";
		}
		if (sizeTemp >= 1024) {
			sizeTemp = bits / (1024l * 1024l * 1024l * 1024l);
			String xs = bits % (1024l * 1024l * 1024l * 1024l) + "";
			xs = format.format((Double.parseDouble(xs) / 1024.0d / 1024.0d / 1024.0d / 1024.0d))
				.substring(2);

			returnStr = sizeTemp + "." + xs + "TB";
		}
		if (sizeTemp >= 1024) {
			sizeTemp = bits / (1024l * 1024l * 1024l * 1024l * 1024l);
			String xs = bits % (1024l * 1024l * 1024l * 1024l * 1024l) + "";
			xs = format.format((Double.parseDouble(xs) / 1024.0d / 1024.0d
					/ 1024.0d / 1024.0d / 1024.0d)).substring(2);

			returnStr = sizeTemp + "." + xs + "PB";
		}
		return returnStr;
	}

	/**
	 * 格式化毫秒到合适的格式 名字太长，但还有地方要用
	 * 
	 * @author 张超
	 * @date 创建时间 2017-5-30
	 * @since V1.0
	 */
	@Deprecated
	public final static String MillisecondToAnySuitableUnit(long times) {
		Integer ss = 1000;
		Integer mi = ss * 60;
		Integer hh = mi * 60;
		Integer dd = hh * 24;

		Long day = times / dd;
		Long hour = (times - day * dd) / hh;
		Long minute = (times - day * dd - hour * hh) / mi;
		Long second = (times - day * dd - hour * hh - minute * mi) / ss;
		Long milliSecond = times - day * dd - hour * hh - minute * mi - second
				* ss;

		StringBuffer sb = new StringBuffer();
		if (day > 0) {
			sb.append(day + "天");
		}
		if (hour > 0) {
			sb.append(hour + "小时");
		}
		if (minute > 0) {
			sb.append(minute + "分");
		}
		if (second > 0) {
			sb.append(second + "秒");
		}
		if (milliSecond > 0) {
			sb.append(milliSecond + "毫秒");
		}
		return sb.toString();
	}

	/**
	 * 格式化毫秒到合适的格式
	 * 
	 * @author 张超
	 * @date 创建时间 2017-5-30
	 * @since V1.0
	 */
	public final static String formatMSToASUnit(long times) {
		Integer ss = 1000;
		Integer mi = ss * 60;
		Integer hh = mi * 60;
		Integer dd = hh * 24;

		Long day = times / dd;
		Long hour = (times - day * dd) / hh;
		Long minute = (times - day * dd - hour * hh) / mi;
		Long second = (times - day * dd - hour * hh - minute * mi) / ss;
		Long milliSecond = times - day * dd - hour * hh - minute * mi - second
				* ss;

		StringBuffer sb = new StringBuffer();
		if (day > 0) {
			sb.append(day + "天");
		}
		if (hour > 0) {
			sb.append(hour + "小时");
		}
		if (minute > 0) {
			sb.append(minute + "分");
		}
		if (second > 0) {
			sb.append(second + "秒");
		}
		if (milliSecond > 0) {
			sb.append(milliSecond + "毫秒");
		}
		return sb.toString();
	}

	/**
	 * 钱数转成中文的钱数
	 * 
	 * @author 张超
	 * @date 创建时间 2017-5-30
	 * @since V1.0
	 */
	public final static String formatMoneyToChinese(double vMoney) throws Exception {
		return formatMoneyToChinese(Double.toString(vMoney));
	}

	/**
	 * 钱数转成中文的钱数
	 * 
	 * @author 张超
	 * @date 创建时间 2017-5-30
	 * @since V1.0
	 */
	public final static String formatMoneyToChinese(String vMoney) throws Exception {
		// 是不是数字格式,不是则直接报错
		Double.parseDouble(vMoney);
		String zheng = vMoney;
		String xiao = "";
		// 如果钱数含有小数点,则
		if (vMoney.indexOf('.') > 0) {
			String[] strTemp = vMoney.split("\\.");
			zheng = strTemp[0];
			xiao = strTemp[1];
		}
		if (zheng.length() > CHINESE_CARRY.length || xiao.length() > 2) {
			throw new Exception("数额太大或小数太多:" + vMoney);
		}
		String chineseStr = "";
		int carry = zheng.length() - 1;
		for (int i = 0; i < zheng.length(); i++) {
			chineseStr += (CHINESE_NUMBER[Integer.parseInt(zheng.charAt(i) + "")])
					+ (CHINESE_CARRY[carry - i] + "");
		}
		// if(xiao.length()>0){
		// chineseStr = chineseStr.substring(0,chineseStr.length()-1);
		// }
		for (int i = 0; i < xiao.length(); i++) {
			chineseStr += (CHINESE_NUMBER[Integer.parseInt(xiao.charAt(i) + "")])
					+ (CHINESE_CODE[i] + "");
		}

		/**
		 * 1.去掉除万,亿,兆,元之外所有数值为0 的位 2.整合多个零的为*(或者直接转成零,去除4,5步骤) 3,去掉独立的零
		 * 4.恢复由多个零整合而成的零 5.去掉亿,兆,万,元前边的零 6.去除前后的0 7.如果以元结尾,则加上整,8.删除关键位数相连
		 */
		// System.out.println(chineseStr);
		// 1.去掉除万,亿,兆之外所有数值为0 的位
		chineseStr = chineseStr.replaceAll("(?<=((\\w|[\\u4e00-\\u9fa5])零))([^(亿|万|兆|元)])(?=(\\w|[\\u4e00-\\u9fa5]){0,})", "");
		// System.out.println(chineseStr);
		// 2.整合多个零的为*
		chineseStr = chineseStr.replaceAll("零{2,}", "零");
		// 3,去掉独立的零
		// chineseStr = chineseStr.replaceAll("零", "");
		// 4.恢复由多个零整合而成的零
		// chineseStr = chineseStr.replaceAll("\\*", "零");
		// 5.去掉亿,兆,万,元前边的零
		chineseStr = chineseStr.replaceAll("(零)(?=(兆|亿|万|元)(\\w|[\\u4e00-\\u9fa5]){0,})", "");
		// 6.去除前后的0
		while (chineseStr.startsWith("零")) {
			chineseStr = chineseStr.substring(2, chineseStr.length());
		}
		while (chineseStr.endsWith("零")) {
			chineseStr = chineseStr.substring(0, chineseStr.length() - 1);
		}
		// 7.如果以元结尾,则加上整
		if (chineseStr.endsWith("元")) {
			chineseStr += "整";
		} else {
			// 把十万元，百万元，千万元的元去掉
			chineseStr = chineseStr.replaceAll("(?<=(拾万|佰万|仟万))(元)(?=(\\w|[\\u4e00-\\u9fa5]){0,})", "");
			// chineseStr = chineseStr.replaceAll("拾万元", "拾万");
			// chineseStr = chineseStr.replaceAll("佰万元", "佰万");
			// chineseStr = chineseStr.replaceAll("仟万元", "仟万");
		}
		while (chineseStr.startsWith("元")) {
			chineseStr = chineseStr.substring(1);
		}

		// 8.删除关键位数相连
		chineseStr = chineseStr.replaceAll("(?<=(兆|亿))(亿|万)(?=(\\w|[\\u4e00-\\u9fa5]){0,})", "");
		// 删除前边是兆亿万,后边是零的元,这个是为了配合框架,结果发现框架的规律琢磨不清,加不加元的数没有明显的不同
		// chineseStr =
		// chineseStr.replaceAll("(?<=(兆|亿|万))(元)(?=((\\w|[\\u4e00-\\u9fa5])){1,})","");
		while (chineseStr.startsWith("零")) {
			chineseStr = chineseStr.substring(1);
		}
		while (chineseStr.endsWith("零")) {
			chineseStr = chineseStr.substring(0, chineseStr.length() - 1);
		}
		return chineseStr;

	}

	public final static void main(String[] args) {
		System.out.println(UnitConversionUtil.BitToAnySuitableUnit(1234247832647l));

	}
}
