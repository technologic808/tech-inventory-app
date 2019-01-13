package com.daftech.music.techinventory;


import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.daftech.music.techinventory.data.ProductContract.ProductEntry;

import static android.view.View.GONE;

//Activity to view the details of a particular product
public class ViewerActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Identifier for the product data loader
     */
    private static final int EXISTING_PRODUCT_LOADER = 0;
    /**
     * Content URI for the existing product (null if it's a new product)
     */
    private Uri mCurrentProductUri;
    /**
     * TextView field to enter the product name
     */
    private TextView mNameTextView;

    /**
     * TextView field to enter the product price
     */
    private TextView mPriceTextView;

    /**
     * TextView field to enter the product quantity
     */
    private TextView mQuantityTextView;

    /**
     * TextView field to enter the supplier name
     */
    private TextView mSupplierNameTextView;

    /**
     * TextView field to enter the supplier phone
     */
    private TextView mSupplierPhoneTextView;


    //empty constructor
    public ViewerActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewer);
        setTitle(R.string.product_details);

        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new product or editing an existing one.
        Intent intent = getIntent();
        mCurrentProductUri = intent.getData();

        //find button to edit the product
        Button editProduct = findViewById(R.id.edit_product);

        //Set on click listener to open the editor
        editProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create new intent to go to {@link EditorActivity}
                Intent intent = new Intent(ViewerActivity.this, EditorActivity.class);

                // Set the URI on the data field of the intent
                intent.setData(mCurrentProductUri);

                // Launch the {@link EditorActivity} to display the data for the current product.
                startActivity(intent);
            }
        });

        //find button to delete the product
        Button deleteProduct = findViewById(R.id.delete_product);

        //Set onclick listener for the button
        deleteProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Delete the product on confirmation
                showDeleteConfirmationDialog();
            }
        });

        //modify Quantity buttons
        Button increaseQuantity = findViewById(R.id.increase_quantity);
        Button decreaseQuantity = findViewById(R.id.decrease_quantity);

        //Set Onclick listeners for the quantities
        increaseQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentQuantityString = mQuantityTextView.getText().toString();
                int currentQuantityInt;
                if (currentQuantityString.length() == 0) {
                    currentQuantityInt = 0;
                    mQuantityTextView.setText(String.valueOf(currentQuantityInt));
                } else {
                    currentQuantityInt = Integer.parseInt(currentQuantityString) + 1;
                    if (currentQuantityInt >= 0) {
                        mQuantityTextView.setText(String.valueOf(currentQuantityInt));
                    }
                }
                updateQuantity();
            }
        });

        decreaseQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentQuantityString = mQuantityTextView.getText().toString();
                int currentQuantityInt;
                if (currentQuantityString.length() == 0) {
                    currentQuantityInt = 0;
                    mQuantityTextView.setText(String.valueOf(currentQuantityInt));
                } else {
                    currentQuantityInt = Integer.parseInt(currentQuantityString) - 1;
                    if (currentQuantityInt >= 0) {
                        mQuantityTextView.setText(String.valueOf(currentQuantityInt));
                    }
                }
                updateQuantity();
            }
        });

        //Find the call supplier button
        Button callSupplier = findViewById(R.id.call_supplier);

        //Set onclick listener on the button

        callSupplier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Call supplier intent
                String currentSupplierPhone = mSupplierPhoneTextView.getText().toString();
                ;
                Intent supplierCallingIntent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", currentSupplierPhone, null));
                startActivity(supplierCallingIntent);
            }
        });


        // Initialize a loader to read the product data from the database
        // and display the current values in the editor
        getLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);


        // Find all relevant views that we will need to read user input from
        mNameTextView = (TextView) findViewById(R.id.product_name);
        mPriceTextView = (TextView) findViewById(R.id.product_price);
        mQuantityTextView = (TextView) findViewById(R.id.product_quantity);
        mSupplierNameTextView = (TextView) findViewById(R.id.supplier_name);
        mSupplierPhoneTextView = (TextView) findViewById(R.id.supplier_phone);

    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Since the viewer shows all product attributes, define a projection that contains
        // all columns from the product table
        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRICE,
                ProductEntry.COLUMN_QUANTITY,
                ProductEntry.COLUMN_SUPPLIER_NAME,
                ProductEntry.COLUMN_PHONE_NUMBER
        };

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentProductUri,         // Query the content URI for the current product
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of product attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_SUPPLIER_NAME);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PHONE_NUMBER);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            String price = cursor.getString(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String supplierName = cursor.getString(supplierNameColumnIndex);
            String supplierPhone = cursor.getString(supplierPhoneColumnIndex);


            // Update the views on the screen with the values from the database
            mNameTextView.setText(name);
            mPriceTextView.setText(price);
            mQuantityTextView.setText(Integer.toString(quantity));
            mSupplierNameTextView.setText(supplierName);
            mSupplierPhoneTextView.setText(supplierPhone);

        }


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mNameTextView.setText("");
        mPriceTextView.setText("");
        mQuantityTextView.setText("");
        mSupplierNameTextView.setText("");
        mSupplierPhoneTextView.setText("");
    }

    /**
     * Prompt the user to confirm that they want to delete this product.
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the product.
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the product.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the product in the database.
     */
    private void deleteProduct() {
        // Only perform the delete if this is an existing product.
        if (mCurrentProductUri != null) {
            // Call the ContentResolver to delete the product at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentProductUri
            // content URI already identifies the product that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentProductUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();
    }

    //method to update the quantity in the database
    private void updateQuantity() {
        //Get the view we want to update
        String quantityString = mQuantityTextView.getText().toString().trim();

        // Create a ContentValues object where column names are the keys,
        // and product attributes from the editor are the values.

        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_QUANTITY, quantityString);

        //Update the quantity in the database
        int rowsAffected = getContentResolver().update(mCurrentProductUri, values, null, null);

        // Show a toast message depending on whether or not the update was successful.
        if (rowsAffected == 0) {
            // If no rows were affected, then there was an error with the update.
            Toast.makeText(this, getString(R.string.editor_update_product_failed),
                    Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the update was successful and we can display a toast.
            Toast.makeText(this, getString(R.string.editor_update_product_successful),
                    Toast.LENGTH_SHORT).show();
        }

    }
}
