package lt.neworld.permissionsondemandtest

import android.Manifest
import android.app.LoaderManager
import android.content.CursorLoader
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.widget.SimpleCursorAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        load_list.setOnClickListener { startLoadContacts() }
    }

    private fun startLoadContacts() {
        val checkedPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
        if (checkedPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_CONTACTS), PERMISSION_REQUEST_ID)
        } else {
            loadContacts()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_ID -> if (grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED) {
                loadContacts()
            }
        }
    }

    private fun loadContacts() {
        val adapter = SimpleCursorAdapter(
                this,
                android.R.layout.simple_list_item_1,
                null,
                arrayOf(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY),
                intArrayOf(android.R.id.text1),
                0
        )
        list.adapter = adapter

        loaderManager.initLoader<Cursor>(0, null, object : LoaderManager.LoaderCallbacks<Cursor> {
            override fun onCreateLoader(id: Int, args: Bundle?): android.content.Loader<Cursor> {
                return CursorLoader(this@MainActivity, ContactsContract.Contacts.CONTENT_URI, PROJECTION, null, null, null)
            }

            override fun onLoaderReset(loader: android.content.Loader<Cursor>?) {
                adapter.swapCursor(null)
            }

            override fun onLoadFinished(loader: android.content.Loader<Cursor>?, data: Cursor?) {
                adapter.swapCursor(data)
            }
        })
    }

    companion object {
        private const val PERMISSION_REQUEST_ID = 0

        private val PROJECTION = arrayOf(ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)
    }
}
