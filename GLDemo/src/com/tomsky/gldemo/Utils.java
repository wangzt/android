package com.tomsky.gldemo;

public class Utils {

	public static int pow2(int size) {
		int small = (int) (Math.log((double)size) / Math.log(2.0f));
		if ((1 << small) >= size) {
			return 1 << small;
		} else {
			return 1 << (small+1);
		}
	}
}
