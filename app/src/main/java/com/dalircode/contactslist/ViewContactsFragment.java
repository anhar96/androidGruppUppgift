package com.dalircode.contactslist;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.dalircode.contactslist.Utils.ContactListAdapter;
import com.dalircode.contactslist.Utils.DatabaseHelper;
import com.dalircode.contactslist.models.Contact;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

public class ViewContactsFragment extends Fragment {

    private static final String TAG = "ViewContactsFragment";
    private String testImageURL = "tr3.cbsistatic.com/hub/i/r/2018/05/14/92f5de44-8105-405d-ba16-0f42974d13d1/resize/1200x/0beb280181fe480a7f34e88677880415/android-security-1.jpg";

    public interface OnContactSelectedListner {
        public void OnContactSelected(Contact con);
    }

    OnContactSelectedListner mContactListner;

    public interface OnAddContactListener {
        public void onAddContact();
    }

    OnAddContactListener mOnAddContact;



    private static final int STANDARD_APPBAR = 0;
    private static final int SEARCH_APPBAR = 1;
    private int mAppBarState;

    private ContactListAdapter adapter;
    private AppBarLayout viewContactsBar, searchBar;
    private ListView contactsList;
    private EditText mSearchContacts;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_viewcontacts, container, false);

        viewContactsBar = view.findViewById(R.id.viewContactsToolbar);
        searchBar = view.findViewById(R.id.searchToolbar);
        contactsList = view.findViewById(R.id.contactsList);
        mSearchContacts = view.findViewById(R.id.etSearchContacts);


        Log.d(TAG, "onCreateView: Started.");

        setAppBarState(STANDARD_APPBAR);
        setupContactsList();

        FloatingActionButton fab = view.findViewById(R.id.fabAddContact);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Clicked fab.");
                mOnAddContact.onAddContact();

            }
        });

        ImageView ivSearchContact = view.findViewById(R.id.ivSearchIcon);
        ivSearchContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Clicked Search Icon.");

                ToggleToolBarState();

            }
        });

        ImageView ivBackArraow = view.findViewById(R.id.ivBackArrow);
        ivBackArraow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Clicked back arrow.");

                ToggleToolBarState();

            }
        });

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            mContactListner = (OnContactSelectedListner) getActivity();
            mOnAddContact = (OnAddContactListener) getActivity();
        } catch (ClassCastException e) {
            Log.e(TAG, "onAttach: ClassCastException: " + e.getMessage());
        }
    }

    private void setupContactsList() {
        final ArrayList<Contact> contacts = new ArrayList<>();

        DatabaseHelper databaseHelper = new DatabaseHelper(getActivity());
        Cursor cursor = databaseHelper.getAllContacts();
        if(!cursor.moveToNext()) {
            Toast.makeText(getActivity(), "There are no contacts to show", Toast.LENGTH_LONG).show();
        }
        while (cursor.moveToNext()) {
            contacts.add(new Contact(
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5)
            ));
        }

        Collections.sort(contacts, new Comparator<Contact>() {
            @Override
            public int compare(Contact contact1, Contact contact2) {
                return contact1.getName().compareToIgnoreCase(contact2.getName());
            }
        });

        adapter = new ContactListAdapter(getActivity(), R.layout.layout_contactslistitem, contacts, "");

        mSearchContacts.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                String text = mSearchContacts.getText().toString().toLowerCase(Locale.getDefault());
                adapter.filter(text);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        contactsList.setAdapter(adapter);

        contactsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: navigating to " + getString(R.string.contact_fragment));
                
                mContactListner.OnContactSelected(contacts.get(position));


            }
        });


    }


    //Initiates the appbar state toggle

    private void ToggleToolBarState() {
        Log.d(TAG, "ToggleToolBarState: Toggling app bar state.");

        if (mAppBarState == STANDARD_APPBAR) {
            setAppBarState(SEARCH_APPBAR);
        } else {
            setAppBarState(STANDARD_APPBAR);
        }
    }

    //Sets the appbar state for either the search 'mode' or 'standard' mode

    private void setAppBarState(int state) {

        Log.d(TAG, "setAppBarState: Changing app bar state to: " + state);
        mAppBarState = state;

        if (mAppBarState == STANDARD_APPBAR) {
            searchBar.setVisibility(View.GONE);
            viewContactsBar.setVisibility(View.VISIBLE);

            //hide the keyboard
            View view = getView();
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            try {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            } catch (NullPointerException e) {
                Log.d(TAG, "setAppBarState: NullPointerException: " + e.getMessage());
            }


        } else if (mAppBarState == SEARCH_APPBAR) {
            viewContactsBar.setVisibility(View.GONE);
            searchBar.setVisibility(View.VISIBLE);

            //open the keyboard
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setAppBarState(STANDARD_APPBAR);
    }

}

