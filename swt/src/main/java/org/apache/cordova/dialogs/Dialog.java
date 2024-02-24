package org.apache.cordova.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * Instances of this class are dialog box This component was inspired by the
 * Oxbow Project (http://code.google.com/p/oxbow/) by Eugene Ryzhikov
 */
public class Dialog {

	/**
	 * Types of opal dialog
	 */
	public enum OpalDialogType {
		CLOSE, YES_NO, OK, OK_CANCEL, SELECT_CANCEL, NO_BUTTON, OTHER, NONE
	}

	public enum CenterOption {
		CENTER_ON_SCREEN, CENTER_ON_DIALOG
	}

	private CenterOption centerPolicy = CenterOption.CENTER_ON_SCREEN;

	private String title;
	OpalDialogType buttonType;
	private final MessageArea messageArea;
	private final FooterArea footerArea;
	final Shell shell;

	private int minimumWidth = 400;
	private int minimumHeight = 150;
	
	private Point lastSize;

	/**
	 * Constructor
	 */
	public Dialog() {
		this(null);
	}

	/**
	 * Constructor
	 *
	 * @param resizable if <code>true</code>, the window is resizable
	 */
	public Dialog(final boolean resizable) {
		this(null, resizable);
	}

	/**
	 * Constructor
	 *
	 * @param parent parent shell
	 */
	public Dialog(final Shell parent) {
		this(parent, true);
	}

	/**
	 * Constructor
	 *
	 * @param parent parent shell
	 * @param resizable if <code>true</code>, the window is resizable
	 */
	public Dialog(final Shell parent, final boolean resizable) {
		if (parent == null) {
			shell = new Shell(Display.getCurrent(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | (resizable ? SWT.RESIZE : SWT.NONE));
		} else {
			shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | (resizable ? SWT.RESIZE : SWT.NONE));
			if (parent.getImage() != null) {
				shell.setImage(parent.getImage());
			}
		}
		messageArea = new MessageArea(this);
		footerArea = new FooterArea(this);
	}

	/**
	 * Show the dialog box
	 *
	 * @return the index of the selected button
	 */
	public int show() {
		final GridLayout gd = new GridLayout(1, true);
		gd.horizontalSpacing = 0;
		gd.verticalSpacing = 0;
		gd.marginHeight = gd.marginWidth = 0;
		shell.setLayout(gd);

		messageArea.render();
		footerArea.render();
		if (title != null) {
			shell.setText(title);
		}
		pack();
		center();
		
		shell.setMinimumSize(shell.computeSize(minimumWidth, SWT.DEFAULT));
		shell.open();

		final Display display = shell.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}

		return footerArea.getSelectedButton();
	}

	private void center() {
		final Point preferredSize = shell.computeSize(SWT.DEFAULT, SWT.DEFAULT);

		if (preferredSize.x < minimumWidth) {
			preferredSize.x = minimumWidth;
		}

		if (preferredSize.y < minimumHeight) {
			preferredSize.y = minimumHeight;
		}
		
		final int centerX;
		final int centerY;

		if (centerPolicy == CenterOption.CENTER_ON_SCREEN || shell.getParent() == null) {
			Shell activeShell = shell.getDisplay().getActiveShell();
			if (activeShell == null) {
				activeShell = shell;
			}
			final Rectangle monitorBounds = SWTGraphicUtil.getBoundsOfMonitorOnWhichShellIsDisplayed(activeShell);
			centerX = monitorBounds.x + (monitorBounds.width - preferredSize.x) / 2;
			centerY = monitorBounds.y + (monitorBounds.height - preferredSize.y) / 2;
		} else {
			final Shell parent = (Shell) shell.getParent();
			centerX = parent.getLocation().x + (parent.getSize().x - preferredSize.x) / 2;
			centerY = parent.getLocation().y + (parent.getSize().y - preferredSize.y) / 2;
		}

		shell.setBounds(centerX, centerY, preferredSize.x, preferredSize.y);		
	}

	/**
	 * Close the dialog box
	 */
	public void close() {
		shell.dispose();
	}

	/**
	 * Compute the size of the shell
	 */
	void pack() {

		final Point preferredSize = shell.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		Rectangle bounds = shell.getBounds();
		
		preferredSize.x = Math.max(preferredSize.x, minimumWidth);
		preferredSize.y = Math.max(preferredSize.y, minimumHeight);
		

		if(lastSize != null) {
			preferredSize.x = Math.max(preferredSize.x, lastSize.x);
			preferredSize.y = Math.max(preferredSize.y, lastSize.y);
		}

		shell.setBounds(bounds.x, bounds.y, preferredSize.x, preferredSize.y);
		lastSize = null;
	}
	
	/**
	 * Build a dialog box that asks a question
	 *
	 * @shell parent shell
	 * @param title title of the dialog box
	 * @param text text of the question
	 * @param defaultValue default value of the input
	 * @return dialog
	 */
	public static Dialog buildAskDialog(final Shell shell, final String title, final String text,
			final String defaultValue, String... buttonlabel) {
		final Dialog dialog = new Dialog(shell);
		dialog.setTitle(title);
		dialog.getMessageArea().setText(text).addTextBox(defaultValue);
		dialog.setButtons(buttonlabel);
		return dialog;
	}
	
	public static Dialog buildAlertDialog(final Shell shell, final String title, final String text, String... buttonlabel) {
		final Dialog dialog = new Dialog(shell);
		dialog.setTitle(title);
		dialog.getMessageArea().setText(text);
		dialog.setButtons(buttonlabel);
		return dialog;
	}

	/**
	 * Build a dialog box that asks the user a confirmation. The button "yes" is
	 * not enabled before timer seconds
	 *
	 * @param shell parent shell
	 * @param title title of the dialog box
	 * @param text text to display
	 * @param timer number of seconds before enabling the yes button
	 * @return dialog
	 */
	public static Dialog buildConfirmDialog(final Shell shell, final String title, final String text, String... buttons) {
		final Dialog dialog = new Dialog(shell);
		dialog.setTitle(title);
		dialog.getMessageArea().setText(text);

		dialog.setButtons(buttons);
		return dialog;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(final String title) {
		this.title = title;
	}

	/**
	 * @return the buttonType
	 */
	public OpalDialogType getButtonType() {
		return buttonType;
	}

	public void setButtons(String... buttons) {
		footerArea.setButtonLabels(buttons).setDefaultButtonIndex(-1);
	}

	/**
	 * @return the messageArea
	 */
	public MessageArea getMessageArea() {
		return messageArea;
	}

	/**
	 * @return the footerArea
	 */
	public FooterArea getFooterArea() {
		return footerArea;
	}

	/**
	 * @return the shell
	 */
	public Shell getShell() {
		return shell;
	}

	/**
	 * @return the index of the selected button
	 */
	public int getSelectedButton() {
		return getFooterArea().getSelectedButton();
	}

	/**
	 * @return the selection state of the checkbox
	 */
	public boolean getCheckboxValue() {
		return footerArea.getCheckBoxValue();
	}

	/**
	 * @return the minimum width of the dialog box
	 */
	public int getMinimumWidth() {
		return minimumWidth;
	}

	/**
	 * @param minimumWidth the minimum width of the dialog box to set
	 */
	public void setMinimumWidth(final int minimumWidth) {
		this.minimumWidth = minimumWidth;
	}

	/**
	 * @return the minimum height of the dialog box
	 */
	public int getMinimumHeight() {
		return minimumHeight;
	}

	/**
	 * @param minimumHeight the minimum height of the dialog box to set
	 */
	public void setMinimumHeight(final int minimumHeight) {
		this.minimumHeight = minimumHeight;
	}

	/**
	 * @return the center policy (Dialog centered on screen or centered in the
	 *         center of the parent window)
	 */
	public CenterOption getCenterPolicy() {
		return centerPolicy;
	}

	/**
	 * @param centerPolicy center policy
	 */
	public void setCenterPolicy(final CenterOption centerPolicy) {
		this.centerPolicy = centerPolicy;
	}
	
	void setLastSize(Point lastSize) {
		this.lastSize = lastSize;
	}

}