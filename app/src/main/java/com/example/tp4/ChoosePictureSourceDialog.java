package com.example.tp4;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import java.util.Arrays;
import java.util.List;

public class ChoosePictureSourceDialog extends DialogFragment {

    public interface ChoosePictureSourceDialogListener {
        public void onDialogPhoneGalleryClick(DialogFragment dialog);
        public void onDialogTakePictureClick(DialogFragment dialog);
    }

    ChoosePictureSourceDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final List<String> options = Arrays.asList(getResources().getStringArray(R.array.picture_source));

        builder.setTitle(R.string.choose_picture_source)
            .setItems(R.array.picture_source, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String pickedOption = options.get(which);
                    if(pickedOption.equals(getString(R.string.phone_gallery))){
                        listener.onDialogPhoneGalleryClick(ChoosePictureSourceDialog.this);
                    } else if (pickedOption.equals(getString(R.string.take_picture))){
                       listener.onDialogTakePictureClick(ChoosePictureSourceDialog.this);
                    }
                }
            });

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (ChoosePictureSourceDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement ChoosePictureSourceDialogListener");
        }
    }
}
