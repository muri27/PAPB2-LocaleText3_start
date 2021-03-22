/*
 * Copyright (C) 2017 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.localetext;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * This app demonstrates how to localize an app with text, an image,
 * a floating action button, an options menu, and the app bar.
 */
public class MainActivity extends AppCompatActivity {

    // Default quantity is 1.
    private int mInputQuantity = 1;

    // Get the number format for this locale.
    private NumberFormat mNumberFormat=NumberFormat.getInstance();

    // Fixed price in U.S. dollars and cents: ten cents.
    private double mPrice = 0.10;

    // Exchange rates for France (FR), Israel (IW), Indonesia(ID).
    double mFrExchangeRate = 0.93; // 0.93 euros = $1.
    double mIwExchangeRate = 3.61; // 3.61 new shekels = $1.
    double mIdExchangeRate = 14000; // Rp14000 = $1.

    // Get locale's currency.
    private NumberFormat mCurrencyFormat=NumberFormat.getCurrencyInstance();

    /**
     * Creates the view with a toolbar for the options menu
     * and a floating action button, and initialize the
     * app data.
     *
     * @param savedInstanceState Bundle with activity's previously saved state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showHelp();
            }
        });

        // Get the current date.
        final Date myDate = new Date();
        // Add 5 days in milliseconds to create the expiration date.
        final long expirationDate = myDate.getTime() + TimeUnit.DAYS.toMillis(5);
        // Set the expiration date as the date to display.
        myDate.setTime(expirationDate);

        // Format the date for the locale.
        final String myFormattedDate = DateFormat.getDateInstance().format(myDate);
        TextView expDateView=findViewById(R.id.date);
        expDateView.setText(myFormattedDate);

        // Apply the exchange rate and calculate the price.
        final String myFormattedPrice;
        // Dapatkan apa locale yang dipilih user
        String deviceLocale = Locale.getDefault().getCountry();
        if(deviceLocale.equals("ID")){
            mPrice*=mIdExchangeRate;
        // Show the price string.
            myFormattedPrice=mCurrencyFormat.format(mPrice);
        }else{
        // Other than ID, use locale US
            mCurrencyFormat=NumberFormat.getCurrencyInstance(Locale.US);
            myFormattedPrice=mCurrencyFormat.format(mPrice);
        }
        TextView localePrice = findViewById(R.id.price);
        localePrice.setText(myFormattedPrice);

        final TextView totalPrice = findViewById(R.id.total);



        // Get the EditText view for the entered quantity.
        final EditText enteredQuantity = (EditText) findViewById(R.id.quantity);
        // Add an OnEditorActionListener to the EditText view.
        enteredQuantity.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // String myFormattedTotal;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // Close the keyboard.
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService
                            (Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    // Check if view v is empty.
                    if (v.toString().equals("")) {
                        // Don't format, leave alone.
                    } else {

                        // Parse string in view v to a number.
                        // Ngubah text dari yang diinputkan di enter a number sesuai dengan locale
                        // Misal 1000 di US make ',' tapi di ID make '.'
                        try {
                            mInputQuantity = mNumberFormat.parse(v.getText().toString()).intValue();
                            v.setError(null);
                        // Convert to string using locale's number format.
                            String myFormatedQuantity = mNumberFormat.format(mInputQuantity);
                            v.setText(myFormatedQuantity);
                        //  Calculate the total amount from price and quantity.
                            double total=mInputQuantity*mPrice;
                            String myFormattedTotalPrice=mCurrencyFormat.format(total);
                        // Show the total amount string.
                            totalPrice.setText(myFormattedTotalPrice);
                        } catch (ParseException e) {
                        //Menginputkan error ke edit text
                            v.setError(getText(R.string.enter_a_number));
                            e.printStackTrace();
                        }



                        return true;
                    }
                }
                return false;
            }
        });
    }

    /**
     * Shows the Help screen.
     */
    private void showHelp() {
        // Create the intent.
        Intent helpIntent = new Intent(this, HelpActivity.class);
        // Start the HelpActivity.
        startActivity(helpIntent);
    }

    /**
     * Clears the quantity when resuming the app after language is changed.
     */
    @Override
    protected void onResume() {
        super.onResume();
        ((EditText) findViewById(R.id.quantity)).getText().clear();
    }

    /**
     * Creates the options menu and returns true.
     *
     * @param menu       Options menu
     * @return boolean   True after creating options menu.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Handles options menu item clicks.
     *
     * @param item      Menu item
     * @return boolean  True if menu item is selected.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle options menu item clicks here.
        switch (item.getItemId()) {
            case R.id.action_help:
                Intent helpIntent = new Intent(this, HelpActivity.class);
                startActivity(helpIntent);
                return true;
            case R.id.action_language:
                Intent languageIntent = new Intent(Settings.ACTION_LOCALE_SETTINGS);
                startActivity(languageIntent);
                return true;
            default:
                // Do nothing
        }
        return super.onOptionsItemSelected(item);
    }
}
