package com.example.kotlinreport.view.update

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.view.MenuItem
import android.view.View
import android.widget.RadioGroup
import android.widget.Toast
import com.example.kotlinreport.R
import com.example.kotlinreport.config.AppConfig
import com.example.kotlinreport.db.DatabaseManager
import com.example.kotlinreport.model.SexType
import com.example.kotlinreport.model.User
import kotlinx.android.synthetic.main.activity_create.*
import kotlinx.android.synthetic.main.activity_update.*
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class UpdateActivity : AppCompatActivity() {

    companion object {
        @JvmStatic
        private val REQUEST_CODE_CAMERA_PICTURE: Int = 1002
    }

    var mAvatarBitmap: Bitmap? = null
    lateinit var mUser: User
    var mPosition: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update)

        val userId = intent.extras?.getLong("id")
        mPosition = intent.extras?.getInt("position")
        if (userId == null || mPosition == null) {
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
            setResult(Activity.RESULT_CANCELED)
            finish()
        } else {
            var userTmp: User? = getUser(userId)
            if (userTmp != null) {
                mUser = userTmp
            } else {
                Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
                setResult(Activity.RESULT_CANCELED)
                finish()
            }
        }

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
        // save avate
        if (mAvatarBitmap !== null) {
            val avatarName: String = "${mUser.id}.png"
            saveAvata(avatarName)
            mUser.avatar = avatarName

        }
        // update user data
        mUser.name = tietUserUpdateName.text.toString()

        DatabaseManager.updateUser(this, mUser)

        Toast.makeText(this, "Cập nhật user thành công", Toast.LENGTH_SHORT).show()

        // set result after updated user
        var intentResult = Intent()
        intentResult.putExtra("id", mUser.id)
        mPosition.let {
            intentResult.putExtra("position", it)
        }

        setResult(Activity.RESULT_OK, intentResult)
        finish()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == UpdateActivity.REQUEST_CODE_CAMERA_PICTURE && resultCode == Activity.RESULT_OK) {
            mAvatarBitmap = data!!.extras.get("data") as Bitmap

            updateAvatarView()
        }
    }

    fun initForm() {
        textViewUserUpdateId.text = "Id: ${mUser.id}"
        tietUserUpdateName.setText(mUser.name)
        // init checked sex
        if (mUser.sex == SexType.MALE) {
            rgUserUpdateSex.check(R.id.rbUserUpdateMale)
        } else {
            rgUserUpdateSex.check(R.id.rbUserUpdateFemale)
        }

        updateAvatarView()

        rgUserUpdateSex.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { radioGroup, radioButtonId ->
            if (radioButtonId.equals(R.id.rbUserUpdateMale)) {
                mUser.sex = SexType.MALE
            } else {
                mUser.sex = SexType.FEMALE
            }

            updateAvatarView()
        })

        buttonUserUpdateChooseAvatar.setOnClickListener {
            var intentCamera: Intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intentCamera, UpdateActivity.REQUEST_CODE_CAMERA_PICTURE)
        }

        buttonUserUpdate.setOnClickListener {
            onSubmit()
        }
    }

    fun validatorUserName(): Boolean {
        val name: String = tietUserUpdateName.text.toString()
        if (name.isEmpty()) {
            tilUserUpdateName.error = getString(R.string.message_input_required)
            requestFocus(tietUserUpdateName)
            return false
        }
        // not error
        tilUserUpdateName.isErrorEnabled = false
        return true
    }

    fun validatorUserSex(): Boolean {
        if (mUser.sex == null) {
            textViewErrorUserUpdateSex.text = getString(R.string.message_radio_required)
            textViewErrorUserUpdateSex.visibility = View.VISIBLE
            requestFocus(rgUserUpdateSex)
            return false
        }
        textViewErrorUserUpdateSex.visibility = View.GONE
        return true
    }


    fun initActionBar() {
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setDisplayShowHomeEnabled(true)
            it.title = getString(R.string.activity_user_update_title)
            it.setDisplayShowTitleEnabled(true)
        }
    }

    private fun requestFocus(view: View) {
        view.isFocusableInTouchMode = true
        view.requestFocus()
    }

    private fun saveAvata(avatar: String) {
        var cw = ContextWrapper(this)
        var directory = cw.getDir(AppConfig.User.AVATAR_FOLDER_NAME, Context.MODE_PRIVATE)
        if (!directory.isDirectory) {
            directory.mkdir()
        }

        var avataPath = File(directory, avatar)

        var fos = FileOutputStream(avataPath)
        mAvatarBitmap!!.compress(Bitmap.CompressFormat.PNG, 100, fos)
        fos.close()

        Toast.makeText(this, "Save avata", Toast.LENGTH_SHORT).show()
    }

    private fun updateAvatarView() {
        if (mAvatarBitmap == null) {
            if (mUser.avatar != null) {
                var cw = ContextWrapper(imageViewUserUpdateAvatar.context.applicationContext)
                var directory = cw.getDir(AppConfig.User.AVATAR_FOLDER_NAME, Context.MODE_PRIVATE)

                var avataFile = File(directory, mUser.avatar)
                if (avataFile.exists()) {
                    val bitmap = BitmapFactory.decodeStream(FileInputStream(avataFile))
                    imageViewUserUpdateAvatar.setImageBitmap(bitmap)
                }
            } else {
                if (mUser.sex == SexType.MALE) {
                    imageViewUserUpdateAvatar.setImageResource(R.drawable.ic_man)
                } else {
                    imageViewUserUpdateAvatar.setImageResource(R.drawable.ic_woman)
                }
            }
        } else {
            imageViewUserUpdateAvatar.setImageBitmap(mAvatarBitmap)
        }
    }

    private fun getUser(id: Long): User? {
        return DatabaseManager.getUser(this, id)
    }
}