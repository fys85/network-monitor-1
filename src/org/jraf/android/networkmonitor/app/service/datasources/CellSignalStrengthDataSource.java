/*
 * This source is part of the
 *      _____  ___   ____
 *  __ / / _ \/ _ | / __/___  _______ _
 * / // / , _/ __ |/ _/_/ _ \/ __/ _ `/
 * \___/_/|_/_/ |_/_/ (_)___/_/  \_, /
 *                              /___/
 * repository.
 *
 * Copyright (C) 2014 Carmen Alvarez (c@rmen.ca)
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
package org.jraf.android.networkmonitor.app.service.datasources;

import android.content.ContentValues;
import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;

import org.jraf.android.networkmonitor.Constants;
import org.jraf.android.networkmonitor.provider.NetMonColumns;

/**
 * Retrieves the cell signal strength.
 */
class CellSignalStrengthDataSource implements NetMonDataSource {
    private static final String TAG = Constants.TAG + CellSignalStrengthDataSource.class.getSimpleName();

    private NetMonSignalStrength mNetMonSignalStrength;
    private int mLastSignalStrength;
    private int mLastSignalStrengthDbm;
    private int mLastAsuLevel;
    private TelephonyManager mTelephonyManager;

    public CellSignalStrengthDataSource() {}

    @Override
    public void onCreate(Context context) {
        Log.v(TAG, "onCreate");
        mNetMonSignalStrength = new NetMonSignalStrength(context);
        mTelephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS | PhoneStateListener.LISTEN_SERVICE_STATE);
    }

    @Override
    public void onDestroy() {
        Log.v(TAG, "onDestroy");
        if (mTelephonyManager != null) mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
    }

    /**
     * @return a ContentValues having the following elements: {@link NetMonColumns#CELL_SIGNAL_STRENGTH}, {@link NetMonColumns#CELL_SIGNAL_STRENGTH_DBM}, and
     *         {@link NetMonColumns#CELL_ASU_LEVEL}. Any of these values may be absent if they could not be retrieved.
     */
    @Override
    public ContentValues getContentValues() {
        Log.v(TAG, "getContentValues");
        ContentValues values = new ContentValues(3);
        values.put(NetMonColumns.CELL_SIGNAL_STRENGTH, mLastSignalStrength);
        if (mLastSignalStrengthDbm != NetMonSignalStrength.SIGNAL_STRENGTH_NONE_OR_UNKNOWN)
            values.put(NetMonColumns.CELL_SIGNAL_STRENGTH_DBM, mLastSignalStrengthDbm);
        values.put(NetMonColumns.CELL_ASU_LEVEL, mLastAsuLevel);
        return values;
    }

    private PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            mLastSignalStrength = mNetMonSignalStrength.getLevel(signalStrength);
            mLastSignalStrengthDbm = mNetMonSignalStrength.getDbm(signalStrength);
            mLastAsuLevel = mNetMonSignalStrength.getAsuLevel(signalStrength);
        }

        @Override
        public void onServiceStateChanged(ServiceState serviceState) {
            Log.v(TAG, "onServiceStateChanged " + serviceState);
            if (serviceState.getState() != ServiceState.STATE_IN_SERVICE) {
                mLastSignalStrength = NetMonSignalStrength.SIGNAL_STRENGTH_NONE_OR_UNKNOWN;
                mLastSignalStrengthDbm = NetMonSignalStrength.SIGNAL_STRENGTH_NONE_OR_UNKNOWN;
                mLastAsuLevel = NetMonSignalStrength.SIGNAL_STRENGTH_NONE_OR_UNKNOWN;
            }
        }
    };
}