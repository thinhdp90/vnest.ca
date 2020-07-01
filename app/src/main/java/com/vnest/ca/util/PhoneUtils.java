package com.vnest.ca.util;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import com.vnest.ca.entity.Contact;

public class PhoneUtils {
    static final String LOG_TAG = "PhoneContact";
    static final String NO_CONTACT = "No contact found!";

    public static Contact findContact(Context context, String name, OnFindListener onFindListener) {
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = ContactsContract.Data.CONTENT_URI;
        String[] projection = new String[]{
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.Contacts.HAS_PHONE_NUMBER};

        String selection = ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME + " = ?";
        String[] selectionArguments = {name};
        try (Cursor cursor = contentResolver.query(uri, projection, selection, selectionArguments, null)) {
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    return getContactNumber(context, cursor.getString(0));
                }
            }
        } catch (Exception e) {
            onFindListener.onError(e);
        }
        return null;
    }

    public static void callToContact(Context context, String name, OnCallListener onCallListener) {
        Contact contact = findContact(context, name, ex -> {
            onCallListener.onError(ex);
        });
        if (contact != null && contact.phoneNumber != null) {
            callToContact(context, contact);
            onCallListener.onSuccess();
        } else {
            onCallListener.onError(new NullPointerException(NO_CONTACT));
        }
    }

    public static void dialToContact(Context context, Contact contact) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + contact.phoneNumber));
        context.startActivity(intent);
    }

    public static void callToContact(Context context, Contact contact) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + contact.phoneNumber));
        context.startActivity(intent);
    }

    private static Contact getContactNumber(Context context, String contactID) {
        String contactNumber = null;
        Log.d(LOG_TAG, "Contact ID: " + contactID);
        Cursor cursorPhone = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
                        ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,

                new String[]{contactID},
                null);

        if (cursorPhone.moveToFirst()) {
            contactNumber = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        }
        cursorPhone.close();
        Log.d(LOG_TAG, "Contact Phone Number: " + contactNumber);
        return new Contact(contactNumber, null, null);
    }


    interface OnFindListener {
        void onError(Exception ex);
    }

    public interface OnCallListener {
        void onSuccess();

        void onError(Exception ex);
    }
}
