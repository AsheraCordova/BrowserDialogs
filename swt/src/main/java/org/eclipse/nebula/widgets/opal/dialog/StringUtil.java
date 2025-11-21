//start - license
/*******************************************************************************
 * Copyright (c) 2025 Ashera Cordova
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *******************************************************************************/
//end - license
package org.eclipse.nebula.widgets.opal.dialog;

import java.io.PrintWriter;
import java.io.StringWriter;

public class StringUtil {
	public static boolean isEmpty(String title) {
		return title == null || title.isEmpty();
	}

	public static String stackStraceAsString(Throwable exception) {
		final StringWriter stringWriter = new StringWriter();
		exception.printStackTrace(new PrintWriter(stringWriter));
		return stringWriter.toString();
	}
}
