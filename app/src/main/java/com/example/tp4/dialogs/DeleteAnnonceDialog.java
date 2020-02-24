package com.example.tp4.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import com.example.tp4.R;

import org.jetbrains.annotations.NotNull;

public class DeleteAnnonceDialog extends DialogFragment {

    public interface DeleteAnnonceDialogListener {
        void onDialogDeleteClick(DialogFragment dialog);
        void onDialogCancelClick(DialogFragment dialog);
    }

    private DeleteAnnonceDialogListener listener;

    @NotNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.delete_ad_dialog_msg)
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onDialogDeleteClick(DeleteAnnonceDialog.this);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onDialogCancelClick(DeleteAnnonceDialog.this);
                    }
                });

        return builder.create();
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);

        try {
            listener = (DeleteAnnonceDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement ChoosePictureSourceDialogListener");
        }
    }
}
