package com.sunilsahoo.bhagavadgita;

import java.io.Serializable;

import android.os.Bundle;

public interface OnFragmentResult extends Serializable {
    public void onFragmentCallback(int action, Bundle bundle);
}
