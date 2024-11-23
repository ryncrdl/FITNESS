package com.easyfitness.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.easyfitness.R;

public class LoadingDialog {

    private final Dialog dialog;
    private final ProgressBar progressBar;
    private final TextView loadingText;

    public LoadingDialog(Context context) {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.loading_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        progressBar = dialog.findViewById(R.id.progressBar);
        loadingText = dialog.findViewById(R.id.loadingText);
    }

    public void show() {
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    public void hide() {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public void setLoadingText(String text) {
        loadingText.setText(text);
    }
}
