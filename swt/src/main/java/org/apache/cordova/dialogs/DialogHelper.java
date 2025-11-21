//start - license
/*******************************************************************************
 * Copyright (c) 2025 Ashera Cordova
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *******************************************************************************/
//end - license
package org.apache.cordova.dialogs;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.nebula.widgets.opal.dialog.Dialog;
public class DialogHelper {
	public static Dialog buildAskDialog(final Shell shell, final String title, final String text,
			final String defaultValue, String... buttonlabel) {
		final Dialog dialog = new Dialog(shell);
		dialog.setCenterPolicy(org.eclipse.nebula.widgets.opal.dialog.Dialog.CenterOption.CENTER_ON_DIALOG);
		dialog.setTitle(title);
		dialog.getMessageArea().setText(text).addTextBox(defaultValue);
		dialog.setButtons(buttonlabel);
		return dialog;
	}
	
	public static Dialog buildAlertDialog(final Shell shell, final String title, final String text, String... buttonlabel) {
		final Dialog dialog = new Dialog(shell);
		dialog.setCenterPolicy(org.eclipse.nebula.widgets.opal.dialog.Dialog.CenterOption.CENTER_ON_DIALOG);
		dialog.setTitle(title);
		dialog.getMessageArea().setText(text);
		dialog.setButtons(buttonlabel);
		return dialog;
	}


	public static Dialog buildConfirmDialog(final Shell shell, final String title, final String text, String... buttons) {
		final Dialog dialog = new Dialog(shell);
		dialog.setCenterPolicy(org.eclipse.nebula.widgets.opal.dialog.Dialog.CenterOption.CENTER_ON_DIALOG);
		dialog.setTitle(title);
		dialog.getMessageArea().setText(text);
		dialog.setButtons(buttons);
		return dialog;
	}
}
