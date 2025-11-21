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

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Resource;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;

public class SWTGraphicUtil {
	public static Rectangle getBoundsOfMonitorOnWhichShellIsDisplayed(final Shell shell) {
		if (shell == null) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}
		if (shell.isDisposed()) {
			SWT.error(SWT.ERROR_WIDGET_DISPOSED);
		}
		Monitor monitor = shell.getMonitor();
		if (monitor == null) {
			monitor = shell.getDisplay().getPrimaryMonitor();
		}
		return monitor.getBounds();
	}

	public static boolean isMacOS() {
		return com.ashera.common.OperatingSystem.isOSX();
	}
	
	public static void safeDispose(final Resource resource) {
		if (resource != null && !resource.isDisposed()) {
			resource.dispose();
		}
	}
	public static void addDisposer(final Widget widget, final Resource... resources) {
		widget.addDisposeListener(e -> {
			if (resources == null) {
				return;
			}
			for (Resource resource:resources) {
				safeDispose(resource);
			}
		});
	}

	public static Image createImageFromFile(final String fileName) {
		if (new File(fileName).exists()) {
			return new Image(Display.getCurrent(), fileName);
		} else {
			return new Image(Display.getCurrent(), //
					SWTGraphicUtil.class.getResourceAsStream(fileName));
		}
	}

	public static void applyHTMLFormating(StyledText label) {
		
	}
}
