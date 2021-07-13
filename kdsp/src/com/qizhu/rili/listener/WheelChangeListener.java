package com.qizhu.rili.listener;

/**
 * Created by lindow on 11/24/15.
 * Interface to to observe user selection changes.
 */
public interface WheelChangeListener {
    /**
     * Called when user selects a new position in the wheel menu.
     *
     * @param selectedPosition the new position selected.
     */
    void onSelectionChange(int selectedPosition);
}