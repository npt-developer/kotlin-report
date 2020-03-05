package com.example.kotlinreport.view.create

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.view.View
import android.widget.RadioGroup
import android.widget.Toast
import com.example.kotlinreport.R
import com.example.kotlinreport.db.DatabaseManager
import com.example.kotlinreport.model.SexType
import com.example.kotlinreport.model.User
import kotlinx.android.synthetic.main.activity_create.*
import java.io.File
import java.io.FileOutputStream


class CreateActivity : AppCompatActivity() {

    companion object {
        @JvmStatic
        private val REQUEST_CODE_CAMERA_PUTURE: Int = 1002
    }

    lateinit var mSexData: SexType
    var mAvataBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create)

        initActionBar()

        initForm()

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        var itemId = item!!.itemId
        if (itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    fun onSubmit() {
        if (!validatorUserName()) {
            return
        }

        if (!validatorUserSex()) {
            return
        }
        var user: User = User(
            null,
            name = tietUserCreateName.text.toString(),
            sex = mSexData,
            avatar = null
        )
        val userId = DatabaseManager.insertUser(this, user)
        user.id = userId

        if (mAvataBitmap !== null && userId > 0) {
            var avatar: String = "${user.id}.png"
            saveAvata(avatar)
            user.avatar = avatar
            DatabaseManager.updateUser(this, user)
        }

        Toast.makeText(this, "Thêm user thành công", Toast.LENGTH_SHORT).show()
        finish()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_CAMERA_PUTURE && resultCode == Activity.RESULT_OK) {
            mAvataBitmap = data!!.extras.get("data") as Bitmap
            imageViewUserCreateAvata.setImageBitmap(mAvataBitmap)
        }
    }

    fun initForm() {
        rgUserCreateSex.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { radioGroup, radioButtonId ->
            if (radioButtonId.equals(R.id.rbUserCreateMale)) {
                mSexData = SexType.MALE
            } else {
                mSexData = SexType.FEMALE
            }
        })

        buttonUserCreateChooseAvata.setOnClickListener {
            var intent: Intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, REQUEST_CODE_CAMERA_PUTURE)
        }

        buttonUserCreate.setOnClickListener {
            onSubmit()
        }
    }

    fun validatorUserName(): Boolean {
        val name: String = tietUserCreateName.text.toString()
        if (name.isEmpty()) {
            tilUserCreateName.error = getString(R.string.message_input_required)
            requestFocus(tietUserCreateName)
            return false
        }
        // not error
        tilUserCreateName.isErrorEnabled = false
        return true
    }

    fun validatorUserSex(): Boolean {
        if (mSexData == null) {
            textViewErrorUserCreateSex.text = getString(R.string.message_radio_required)
            textViewErrorUserCreateSex.visibility = View.VISIBLE
            requestFocus(rgUserCreateSex)
            return false
        }
        textViewErrorUserCreateSex.visibility = View.GONE
        return true
    }


    fun initActionBar() {
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setDisplayShowHomeEnabled(true)
            it.title = getString(R.string.activity_user_create_title)
            it.setDisplayShowTitleEnabled(true)
        }
    }

    private fun requestFocus(view: View) {
        view.isFocusableInTouchMode = true
        view.requestFocus()
    }

    private fun saveAvata(avatar: String) {
        var cw = ContextWrapper(this)
        var directory = cw.getDir("avata", Context.MODE_PRIVATE)
        if (!directory.isDirectory) {
            directory.mkdir()
        }

        var avataPath = File(directory, avatar)

        var fos = FileOutputStream(avataPath)
        mAvataBitmap!!.compress(Bitmap.CompressFormat.PNG, 100, fos)
        fos.close()

        Toast.makeText(this, "Save avata", Toast.LENGTH_SHORT).show()
    }
}
