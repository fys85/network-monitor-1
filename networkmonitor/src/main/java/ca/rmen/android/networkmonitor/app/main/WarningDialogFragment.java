/*
 * This source is part of the
 *      _____  ___   ____
 *  __ / / _ \/ _ | / __/___  _______ _
 * / // / , _/ __ |/ _/_/ _ \/ __/ _ `/
 * \___/_/|_/_/ |_/_/ (_)___/_/  \_, /
 *                              /___/
 * repository.
 *
 * Copyright (C) 2014-2015 Carmen Alvarez (c@rmen.ca)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ca.rmen.android.networkmonitor.app.main;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;

import ca.rmen.android.networkmonitor.Constants;
import ca.rmen.android.networkmonitor.R;
import ca.rmen.android.networkmonitor.app.prefs.NetMonPreferences;
import ca.rmen.android.networkmonitor.util.Log;

/**
 * Warn the user about battery and data consumption.
 */
public class WarningDialogFragment extends DialogFragment { // NO_UCD (use default)

    private static final String TAG = Constants.TAG + WarningDialogFragment.class.getSimpleName();

    /**
     * An activity which contains a confirmation dialog fragment should implement this interface to be notified if the user clicks ok on the dialog.
     */
    public interface DialogButtonListener {
        void onAppWarningOkClicked();

        void onAppWarningCancelClicked();
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.v(TAG, "onCreateDialog: savedInstanceState = " + savedInstanceState);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.app_warning_title);
        final View view = LayoutInflater.from(getActivity()).inflate(R.layout.warning_dialog, null);
        builder.setView(view);
        OnClickListener positiveListener = null;
        OnClickListener negativeListener = null;
        if (getActivity() instanceof DialogButtonListener) {
            positiveListener = new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Log.v(TAG, "onClick (positive button");
                    FragmentActivity activity = getActivity();
                    if (activity == null) {
                        Log.w(TAG, "User clicked on dialog after it was detached from activity. Monkey?");
                    } else {
                        CheckBox showWarningDialog = (CheckBox) view.findViewById(R.id.app_warning_cb_stfu);
                        NetMonPreferences.getInstance(activity).setShowApppWarning(!showWarningDialog.isChecked());
                        ((DialogButtonListener) activity).onAppWarningOkClicked();
                    }
                }
            };
            negativeListener = new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Log.v(TAG, "onClick (negative button");
                    FragmentActivity activity = getActivity();
                    if (activity == null)
                        Log.w(TAG, "User clicked on dialog after it was detached from activity. Monkey?");
                    else
                        ((DialogButtonListener) activity).onAppWarningCancelClicked();
                }
            };
        }
        builder.setNegativeButton(R.string.app_warning_cancel, negativeListener);
        builder.setPositiveButton(R.string.app_warning_ok, positiveListener);
        builder.setCancelable(false);
        final Dialog dialog = builder.create();
        dialog.setCancelable(false);
        setCancelable(false);
        return dialog;
    }

    public static void show(FragmentActivity activity) {
        WarningDialogFragment fragment = new WarningDialogFragment();
        fragment.show(activity.getSupportFragmentManager(), TAG);
    }
}