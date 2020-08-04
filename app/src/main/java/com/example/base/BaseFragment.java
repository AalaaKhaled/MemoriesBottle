package com.example.base;

import android.app.ProgressDialog;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

public class BaseFragment extends Fragment {
    public void showMessage(String message,String posActionname){
        AlertDialog.Builder builder =new AlertDialog.Builder(getContext());
        builder.setMessage(message);
        builder.setPositiveButton(posActionname, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }
    public AlertDialog showMessage(String message, String posActionName,
                                   DialogInterface.OnClickListener onClickListener,
                                   boolean isCancelable){
        AlertDialog.Builder builder =new AlertDialog.Builder(getContext());
        builder.setMessage(message);
        builder.setPositiveButton(posActionName,onClickListener );
        builder.setCancelable(isCancelable);
        return builder.show();
    }

    public void showMessage(String message,String posActionname,
                            DialogInterface.OnClickListener posAction){
        AlertDialog.Builder builder =new AlertDialog.Builder(getContext());
        builder.setMessage(message);
        builder.setPositiveButton(posActionname,posAction);
        builder.show();
    }
    public void showMessage(int messageId,int posActionStringId){
        AlertDialog.Builder builder =new AlertDialog.Builder(getContext());
        builder.setMessage(messageId);
        builder.setPositiveButton(posActionStringId, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }
    public void showMessage(int message,int posActionname,
                            DialogInterface.OnClickListener posAction){
        AlertDialog.Builder builder =new AlertDialog.Builder(getContext());
        builder.setMessage(message);
        builder.setPositiveButton(posActionname,posAction);
        builder.show();
    }
    public AlertDialog showMessage(String message, String posActionName,
                                   DialogInterface.OnClickListener onClickListenerPos,
                                   boolean isCancelable , String neutral , DialogInterface.OnClickListener onClickListenerNeu){
        AlertDialog.Builder builder =new AlertDialog.Builder(getContext());
        builder.setMessage(message);
        builder.setPositiveButton(posActionName,onClickListenerPos );
        builder.setNeutralButton(neutral,onClickListenerNeu);
        builder.setCancelable(isCancelable);
        return builder.show();
    }

    ProgressDialog progressDialog;
    public void showProgressDialog(int messageId){
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage(getString(messageId));
        progressDialog.show();
    }
    public void hideProgressDialog(){
        if(progressDialog!=null&&progressDialog.isShowing())
            progressDialog.dismiss();
    }
}
