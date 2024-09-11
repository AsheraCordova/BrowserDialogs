/*
       Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements.  See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership.  The ASF licenses this file
       to you under the Apache License, Version 2.0 (the
       "License"); you may not use this file except in compliance
       with the License.  You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.
*/
package org.apache.cordova.dialogs;

import java.util.ArrayList;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * This class provides access to notifications on the device.
 *
 * Be aware that this implementation gets called on
 * navigator.notification.{alert|confirm|prompt}, and that there is a separate
 * implementation in org.apache.cordova.CordovaChromeClient that gets
 * called on a simple window.{alert|confirm|prompt}.
 */
public class Notification extends CordovaPlugin {

    private static final String LOG_TAG = "Notification";

    private static final String ACTION_BEEP           = "beep";
    private static final String ACTION_ALERT          = "alert";
    private static final String ACTION_CONFIRM        = "confirm";
    private static final String ACTION_PROMPT         = "prompt";
    private static final String ACTION_ACTIVITY_START = "activityStart";
    private static final String ACTION_ACTIVITY_STOP  = "activityStop";
    private static final String ACTION_PROGRESS_START = "progressStart";
    private static final String ACTION_PROGRESS_VALUE = "progressValue";
    private static final String ACTION_PROGRESS_STOP  = "progressStop";
    private static final String ACTION_DISMISS_PREVIOUS  = "dismissPrevious";
    private static final String ACTION_DISMISS_ALL  = "dismissAll";

    private static final long BEEP_WAIT_TINE = 1000;

    private ArrayList<Dialog> dialogs = new ArrayList<Dialog>();

    public int confirmResult = -1;
//    public ProgressDialog spinnerDialog = null;
//    public ProgressDialog progressDialog = null;

    /**
     * Constructor.
     */
    public Notification() {
    }

    /**
     * Executes the request and returns PluginResult.
     *
     * @param action            The action to execute.
     * @param args              JSONArray of arguments for the plugin.
     * @param callbackContext   The callback context used when calling back into JavaScript.
     * @return                  True when the action was valid, false otherwise.
     */
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
    	/*
    	 * Don't run any of these if the current activity is finishing
    	 * in order to avoid android.view.WindowManager$BadTokenException
    	 * crashing the app. Just return true here since false should only
    	 * be returned in the event of an invalid action.
    	 */
//    	if (this.cordova.getActivity().isFinishing()) return true;

        if (action.equals(ACTION_BEEP)) {
            this.beep(args.getLong(0));
        }
        else if (action.equals(ACTION_ALERT)) {
        	String message = null;
        	if (!args.get(0).equals(JSONObject.NULL)) {
        		message = args.getString(0);
        	} else {
        		message = "null";
        	}
            this.alert(message, args.getString(1), args.getString(2), callbackContext);
            return true;
        }
        else if (action.equals(ACTION_CONFIRM)) {
            this.confirm(args.getString(0), args.getString(1), args.getJSONArray(2), callbackContext);
            return true;
        }
        else if (action.equals(ACTION_PROMPT)) {
            this.prompt(args.getString(0), args.getString(1), args.getJSONArray(2), args.getString(3), callbackContext);
            return true;
        }
        else if (action.equals(ACTION_ACTIVITY_START)) {
            this.activityStart(args.getString(0), args.getString(1));
        }
        else if (action.equals(ACTION_ACTIVITY_STOP)) {
            this.activityStop();
        }
        else if (action.equals(ACTION_PROGRESS_START)) {
            this.progressStart(args.getString(0), args.getString(1));
        }
        else if (action.equals(ACTION_PROGRESS_VALUE)) {
            this.progressValue(args.getInt(0));
        }
        else if (action.equals(ACTION_PROGRESS_STOP)) {
            this.progressStop();
        }
        else if (action.equals(ACTION_DISMISS_PREVIOUS)) {
            this.dismissPrevious(callbackContext);
        }
        else if (action.equals(ACTION_DISMISS_ALL)) {
            this.dismissAll(callbackContext);
        }
        else {
            return false;
        }

        // Only alert and confirm are async.
        callbackContext.success();
        return true;
    }

    //--------------------------------------------------------------------------
    // LOCAL METHODS
    //--------------------------------------------------------------------------

    /**
     * Beep plays the default notification ringtone.
     *
     * @param count     Number of times to play notification
     */
    public void beep(final long count) {
    	Runnable runnable = new Runnable() {
            public void run() {
            	Display display = Display.getDefault();
                if (display != null) {
                	for (long i = 0; i < count; ++i) {
                		display.beep();
                		try {
                            Thread.sleep(BEEP_WAIT_TINE);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                		           		
                	}
                }
            }
        };
        
        this.cordova.getActivity().runOnUiThread(runnable);
    }

    /**
     * Builds and shows a native Android alert with given Strings
     * @param message           The message the alert should display
     * @param title             The title of the alert
     * @param buttonLabel       The label of the button
     * @param callbackContext   The callback context
     */
    public synchronized void alert(final String message, final String title, final String buttonLabel, final CallbackContext callbackContext) {
        Runnable runnable = new Runnable() {
            public void run() {
            	final Shell shell = com.ashera.common.ShellManager.getInstance().getActiveShell();
            	Dialog.buildAlertDialog(shell, title, message, buttonLabel).show();
            	callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, 0));
            };
        };
        this.cordova.getActivity().runOnUiThread(runnable);
    }

    /**
     * Builds and shows a native Android confirm dialog with given title, message, buttons.
     * This dialog only shows up to 3 buttons.  Any labels after that will be ignored.
     * The index of the button pressed will be returned to the JavaScript callback identified by callbackId.
     *
     * @param message           The message the dialog should display
     * @param title             The title of the dialog
     * @param buttonLabels      A comma separated list of button labels (Up to 3 buttons)
     * @param callbackContext   The callback context.
     */
    public synchronized void confirm(final String message, final String title, final JSONArray buttonLabels, final CallbackContext callbackContext) {

        Runnable runnable = new Runnable() {
            public void run() {
        		final Shell shell = com.ashera.common.ShellManager.getInstance().getActiveShell();
            	java.util.List<String> labels = new ArrayList<>();
            	
                // First button
                if (buttonLabels.length() > 0) {
                	labels.add(buttonLabels.getString(0));
                }

                // Second button
                if (buttonLabels.length() > 1) {
                	labels.add(buttonLabels.getString(1));
                }

                // Third button
                if (buttonLabels.length() > 2) {
                	labels.add(buttonLabels.getString(2));
                }
                

                int index = Dialog.buildConfirmDialog(shell, title, message, labels.toArray(new String[0])).show();
                callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, buttonLabels.length() - index - 1));
            };
        };
        this.cordova.getActivity().runOnUiThread(runnable);
        
    }

    /**
     * Builds and shows a native Android prompt dialog with given title, message, buttons.
     * This dialog only shows up to 3 buttons.  Any labels after that will be ignored.
     * The following results are returned to the JavaScript callback identified by callbackId:
     *     buttonIndex			Index number of the button selected
     *     input1				The text entered in the prompt dialog box
     *
     * @param message           The message the dialog should display
     * @param title             The title of the dialog
     * @param buttonLabels      A comma separated list of button labels (Up to 3 buttons)
     * @param callbackContext   The callback context.
     */
    public synchronized void prompt(final String message, final String title, final JSONArray buttonLabels, final String defaultText, final CallbackContext callbackContext) {
        Runnable runnable = new Runnable() {
            public void run() {
            	final Shell shell = com.ashera.common.ShellManager.getInstance().getActiveShell();
            	java.util.List<String> labels = new ArrayList<>();
            	
                // First button
                if (buttonLabels.length() > 0) {
                	labels.add(buttonLabels.getString(0));
                }

                // Second button
                if (buttonLabels.length() > 1) {
                	labels.add(buttonLabels.getString(1));
                }

                // Third button
                if (buttonLabels.length() > 2) {
                	labels.add(buttonLabels.getString(2));
                }
                

                Dialog dialog = Dialog.buildAskDialog(shell, title, message, defaultText, labels.toArray(new String[0]));
				int index = dialog.show();
                final JSONObject result = new JSONObject();
                result.put("buttonIndex",buttonLabels.length() - index - 1);
                String promptText = dialog.getMessageArea().getTextBoxValue();
				result.put("input1", promptText.trim().length()==0 ? defaultText : promptText);
                callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, result));
            };
        };
        this.cordova.getActivity().runOnUiThread(runnable);
    }

    /**
     * Close previously opened dialog
     */
    public synchronized void dismissPrevious(final CallbackContext callbackContext){
        if(!dialogs.isEmpty()){
            dialogs.remove(dialogs.size()-1).close();
            callbackContext.success();
        }else{
            callbackContext.error("No previously opened dialog to dismiss");
        }
    }

    /**
     * Close any open dialog.
     */
    public synchronized void dismissAll(final CallbackContext callbackContext){
        if(!dialogs.isEmpty()){
            for(Dialog dialog: dialogs){
                dialog.close();
            }
            dialogs = new ArrayList<Dialog>();
            callbackContext.success();
        }else{
            callbackContext.error("No previously opened dialogs to dismiss");
        }
    }

    /**
     * Show the spinner.
     *
     * @param title     Title of the dialog
     * @param message   The message of the dialog
     */
    public synchronized void activityStart(final String title, final String message) {
    	
    }

    /**
     * Stop spinner.
     */
    public synchronized void activityStop() {
    	
    }

    /**
     * Show the progress dialog.
     *
     * @param title     Title of the dialog
     * @param message   The message of the dialog
     */
    public synchronized void progressStart(final String title, final String message) {
    	
    }

    /**
     * Set value of progress bar.
     *
     * @param value     0-100
     */
    public synchronized void progressValue(int value) {
    	
    }

    /**
     * Stop progress dialog.
     */
    public synchronized void progressStop() {
    	
    }
}
