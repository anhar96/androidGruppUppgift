package com.dalircode.contactslist;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.dalircode.contactslist.Utils.ChangePhotoDialog;
import com.dalircode.contactslist.Utils.DatabaseHelper;
import com.dalircode.contactslist.Utils.Init;
import com.dalircode.contactslist.Utils.UniversalImageLoader;
import com.dalircode.contactslist.models.Contact;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditContactFragment extends Fragment implements ChangePhotoDialog.OnPhotoReceivedListener {
    private static final String TAG = "EditContactFragment";


    public EditContactFragment() {
        super();
        setArguments(new Bundle());
    }

    private Contact mContact;
    private EditText mPhoneNumber, mName, mEmail;
    private CircleImageView mContactImage;
    private Spinner mSelectDevice;
    private Toolbar toolbar;
    private String mSelectedImagePath;
    private int mPreviousKeyStroke;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editcontact, container, false);
        mPhoneNumber = view.findViewById(R.id.etContactPhone);
        mName = view.findViewById(R.id.etContactName);
        mEmail = view.findViewById(R.id.etContactEmail);
        mContactImage = view.findViewById(R.id.contactImage);
        mSelectDevice = view.findViewById(R.id.selectDevice);
        toolbar = view.findViewById(R.id.editContactToolbar);
        Log.d(TAG, "onCreateView: started.");

        mSelectedImagePath = null;


        TextView heading = view.findViewById(R.id.textContactToolbar);
        heading.setText(getString(R.string.edit_contact));

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        setHasOptionsMenu(true);

        mContact = getContactFromBundle();

        if (mContact != null) {
            init();
        }

        ImageView ivBackArrow = view.findViewById(R.id.ivBackArrow);
        ivBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked back arrow.");

                getActivity().getSupportFragmentManager().popBackStack();
            }
        });


        ImageView ivCheckMark = view.findViewById(R.id.ivCheckMark);
        ivCheckMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: saving the edited contact.");

                if (checkStringIfNull(mName.getText().toString())) {
                    Log.d(TAG, "onClick: saving changes to the contact: " + mName.getText().toString());

                    DatabaseHelper databaseHelper = new DatabaseHelper(getActivity());
                    Cursor cursor = databaseHelper.getContactID(mContact);

                    int contactID = -1;
                    while (cursor.moveToNext()) {
                        contactID = cursor.getInt(0);
                    }
                    if (contactID > -1) {
                        if (mSelectedImagePath != null) {
                            mContact.setProfileImage(mSelectedImagePath);
                        }
                        mContact.setName(mName.getText().toString());
                        mContact.setPhonenumber(mPhoneNumber.getText().toString());
                        mContact.setDevice(mSelectDevice.getSelectedItem().toString());
                        mContact.setEmail(mEmail.getText().toString());

                        databaseHelper.updateContact(mContact, contactID);
                        Toast.makeText(getActivity(), "Contact Updated", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "Database Error", Toast.LENGTH_SHORT).show();
                    }
                }
            }


        });

        ImageView ivCamera = view.findViewById(R.id.ivCamera);
        ivCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (int i = 0; i < Init.PERMISSIONS.length; i++) {
                    String[] permission = {Init.PERMISSIONS[i]};
                    if (((MainActivity) getActivity()).checkPermission(permission)) {
                        if (i == Init.PERMISSIONS.length - 1) {
                            Log.d(TAG, "onClick: opening the 'image selection dialog box'.");
                            ChangePhotoDialog dialog = new ChangePhotoDialog();
                            dialog.show(getFragmentManager(), getString(R.string.change_photo_dialog));
                            dialog.setTargetFragment(EditContactFragment.this, 0);
                        }
                    } else {
                        ((MainActivity) getActivity()).verifyPermissions(permission);
                    }
                }


            }
        });

        return view;
    }

    private boolean checkStringIfNull(String string){
        if(string.equals("")){
            return false;
        }else{
            return true;
        }
    }

    private void init() {
        mPhoneNumber.setText(mContact.getPhonenumber());
        mName.setText(mContact.getName());
        mEmail.setText(mContact.getEmail());
        UniversalImageLoader.setImage(mContact.getProfileImage(), mContactImage, null, "");
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.device_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSelectDevice.setAdapter(adapter);
        int position = adapter.getPosition(mContact.getDevice());
        mSelectDevice.setSelection(position);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.contact_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menuitem_delete:
                Log.d(TAG, "onOptionsItemSelected: deleting contact.");
                DatabaseHelper databaseHelper = new DatabaseHelper(getActivity());
                Cursor cursor = databaseHelper.getContactID(mContact);

                int contactID = -1;
                while (cursor.moveToNext()) {
                    contactID = cursor.getInt(0);
                }

                if (contactID > -1) {
                    if (databaseHelper.deleteContact(contactID) > 0) {
                        Toast.makeText(getActivity(), "Contact Deleted", Toast.LENGTH_SHORT).show();

                        this.getArguments().clear();
                        getActivity().getSupportFragmentManager().popBackStack();
                    } else {
                        Toast.makeText(getActivity(), "Database Error", Toast.LENGTH_SHORT).show();
                    }
                }
        }
        return super.onOptionsItemSelected(item);
    }

    private Contact getContactFromBundle() {
        Log.d(TAG, "getContactFromBundle: arguments: " + getArguments());

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            return bundle.getParcelable(getString(R.string.contact));
        } else {
            return null;
        }
    }


    @Override
    public void getBitmapImage(Bitmap bitmap) {
        Log.d(TAG, "getBitmapImage: got the bitmap: " + bitmap);

        if (bitmap != null) {

            ((MainActivity) getActivity()).compressBitmap(bitmap, 70);
            mContactImage.setImageBitmap(bitmap);
        }
    }

    @Override
    public void getImagePath(String imagePath) {
        Log.d(TAG, "getImagePath: got the image path: " + imagePath);

        if (!imagePath.equals("")) {
            imagePath = imagePath.replace(":/", "://");
            mSelectedImagePath = imagePath;
            UniversalImageLoader.setImage(imagePath, mContactImage, null, "");
        }
    }


    private void initOnTextChangeListener() {

        mPhoneNumber.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                mPreviousKeyStroke = keyCode;

                return false;
            }
        });

        mPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                String number = s.toString();
                Log.d(TAG, "afterTextChanged:  " + number);
            }
        });
    }
}