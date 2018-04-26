package com.spencerstudios.admincenter.Dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.spencerstudios.admincenter.R;

public class DialogBuilders {

    public static void infoDialog(Context context, String title, String msg){

        final AlertDialog info = new AlertDialog.Builder(context).create();
        info.setIcon(R.mipmap.ic_launcher);
        info.setTitle(title);
        info.setMessage(msg);
        info.setButton(DialogInterface.BUTTON_POSITIVE, "Okay, great", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                info.dismiss();
            }
        });

        info.show();
    }
}
