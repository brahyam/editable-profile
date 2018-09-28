package com.dvipersquad.editableprofile.utils;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

/**
 * Common activity tools
 */
public class ActivityUtils {

    /**
     * Adds a fragment to a container using fragment manager
     *
     * @param fragmentManager fragment manager
     * @param fragment        fragment object
     * @param frameId         container id
     */
    public static void addFragmentToActivity(@NonNull FragmentManager fragmentManager,
                                             @NonNull Fragment fragment, int frameId) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(frameId, fragment);
        transaction.commit();
    }

}
