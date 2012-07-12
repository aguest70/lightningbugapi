package de.lightningbug.api.util;

/**
 * Utility methods for string objects.
 * 
 * @author Sebastian Kirchner
 * 
 */
public class StringUtils {

	private StringUtils() {
		super();
	}

	/**
	 * Capitalizes the first letter of the given string
	 * 
	 * @param lowerCaseString
	 *            a string object
	 * @return <code>null</code>, if the given string is <code>null</code>.
	 *         Returns an empty string, if the given string contains no
	 *         characters. If the given string begins with a letter, a modified
	 *         string is returned. The first letter of the returned string will
	 *         be in upper case.
	 */
	public static String capitalize(final String lowerCaseString) {
		if (lowerCaseString == null) {
			return null;
		}
		if (lowerCaseString.length() < 1) {
			return lowerCaseString;
		}
		final String firstLetter = lowerCaseString.substring(0, 1).toUpperCase();
		final String tail = lowerCaseString.substring(1, lowerCaseString.length());
		return firstLetter.concat(tail);
	}

}
